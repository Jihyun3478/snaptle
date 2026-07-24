package com.snaptle.domain.settlement;

import com.snaptle.domain.expense.Expense;
import com.snaptle.domain.expense.ExpenseParticipant;
import com.snaptle.domain.expense.ExpenseParticipantRepository;
import com.snaptle.domain.expense.ExpenseRepository;
import com.snaptle.domain.settlement.dto.MemberBalanceResponse;
import com.snaptle.domain.settlement.dto.SettlementResponse;
import com.snaptle.domain.settlement.dto.SettlementTransferResponse;
import com.snaptle.domain.trip.Trip;
import com.snaptle.domain.trip.TripService;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementTransferRepository settlementTransferRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;
    private final TripService tripService;

    public SettlementService(SettlementRepository settlementRepository,
                              SettlementTransferRepository settlementTransferRepository,
                              ExpenseRepository expenseRepository,
                              ExpenseParticipantRepository expenseParticipantRepository, TripService tripService) {
        this.settlementRepository = settlementRepository;
        this.settlementTransferRepository = settlementTransferRepository;
        this.expenseRepository = expenseRepository;
        this.expenseParticipantRepository = expenseParticipantRepository;
        this.tripService = tripService;
    }

    @Transactional
    public SettlementResponse endTripAndSettle(Long userId, Long tripId) {
        Trip trip = tripService.getTripOrThrow(tripId);
        tripService.requireMember(tripId, userId);
        tripService.requireNotEnded(trip);

        TripBalances balances = calculateBalances(tripId);

        trip.end();

        Settlement settlement;
        try {
            settlement = settlementRepository.saveAndFlush(Settlement.create(tripId));
        } catch (DataIntegrityViolationException e) {
            throw new SnaptleException(ErrorCode.TRIP_ALREADY_ENDED);
        }
        List<MinimumTransferCalculator.Transfer> calculatedTransfers =
                MinimumTransferCalculator.calculate(balances.netBalances());
        List<SettlementTransfer> transfers = calculatedTransfers.stream()
                .map(t -> SettlementTransfer.of(settlement.getId(), t.fromUserId(), t.toUserId(), t.amount()))
                .toList();
        settlementTransferRepository.saveAll(transfers);

        return toResponse(settlement, balances, transfers);
    }

    public SettlementResponse getSettlement(Long userId, Long tripId) {
        tripService.getTripOrThrow(tripId);
        tripService.requireMember(tripId, userId);

        Settlement settlement = settlementRepository.findFirstByTripIdOrderByCreatedAtDesc(tripId)
                .orElseThrow(() -> new SnaptleException(ErrorCode.SETTLEMENT_NOT_FOUND));

        TripBalances balances = calculateBalances(tripId);
        List<SettlementTransfer> transfers = settlementTransferRepository.findAllBySettlementId(settlement.getId());

        return toResponse(settlement, balances, transfers);
    }

    private TripBalances calculateBalances(Long tripId) {
        Map<Long, BigDecimal> totalPaidByUser = new HashMap<>();
        Map<Long, BigDecimal> totalOwedByUser = new HashMap<>();
        for (Long memberId : tripService.getMemberUserIds(tripId)) {
            totalPaidByUser.put(memberId, BigDecimal.ZERO);
            totalOwedByUser.put(memberId, BigDecimal.ZERO);
        }

        List<Expense> expenses = expenseRepository.findAllByTripIdOrderByPaidAtDesc(tripId);
        List<Long> expenseIds = expenses.stream().map(Expense::getId).toList();
        List<ExpenseParticipant> participants = expenseParticipantRepository.findAllByExpenseIdIn(expenseIds);

        for (Expense expense : expenses) {
            totalPaidByUser.merge(expense.getPayerId(), expense.getConvertedAmount(), BigDecimal::add);
        }
        for (ExpenseParticipant participant : participants) {
            totalOwedByUser.merge(participant.getUserId(), participant.getShareAmount(), BigDecimal::add);
        }

        Set<Long> allUserIds = new HashSet<>(totalPaidByUser.keySet());
        allUserIds.addAll(totalOwedByUser.keySet());

        Map<Long, BigDecimal> netBalances = new HashMap<>();
        for (Long userId : allUserIds) {
            BigDecimal paid = totalPaidByUser.getOrDefault(userId, BigDecimal.ZERO);
            BigDecimal owed = totalOwedByUser.getOrDefault(userId, BigDecimal.ZERO);
            totalPaidByUser.putIfAbsent(userId, paid);
            totalOwedByUser.putIfAbsent(userId, owed);
            netBalances.put(userId, paid.subtract(owed));
        }

        return new TripBalances(totalPaidByUser, totalOwedByUser, netBalances);
    }

    private SettlementResponse toResponse(Settlement settlement, TripBalances balances,
                                           List<SettlementTransfer> transfers) {
        List<MemberBalanceResponse> balanceResponses = balances.netBalances().keySet().stream()
                .map(memberId -> new MemberBalanceResponse(
                        memberId, balances.totalPaidByUser().get(memberId), balances.totalOwedByUser().get(memberId),
                        balances.netBalances().get(memberId)))
                .toList();
        List<SettlementTransferResponse> transferResponses = transfers.stream()
                .map(SettlementTransferResponse::from)
                .toList();

        return SettlementResponse.of(settlement, balanceResponses, transferResponses);
    }

    private record TripBalances(Map<Long, BigDecimal> totalPaidByUser, Map<Long, BigDecimal> totalOwedByUser,
                                 Map<Long, BigDecimal> netBalances) {
    }
}
