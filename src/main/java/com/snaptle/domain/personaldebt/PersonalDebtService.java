package com.snaptle.domain.personaldebt;

import com.snaptle.domain.personaldebt.dto.CreatePersonalDebtRequest;
import com.snaptle.domain.personaldebt.dto.PersonalDebtResponse;
import com.snaptle.domain.trip.TripService;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersonalDebtService {

    private final PersonalDebtRepository personalDebtRepository;
    private final TripService tripService;

    public PersonalDebtService(PersonalDebtRepository personalDebtRepository, TripService tripService) {
        this.personalDebtRepository = personalDebtRepository;
        this.tripService = tripService;
    }

    @Transactional
    public PersonalDebtResponse createPersonalDebt(Long userId, Long tripId, CreatePersonalDebtRequest request) {
        tripService.requireMember(tripId, userId);

        if (request.creditorId().equals(request.debtorId())) {
            throw new SnaptleException(ErrorCode.SAME_CREDITOR_AND_DEBTOR);
        }
        if (!tripService.isMember(tripId, request.creditorId())) {
            throw new SnaptleException(ErrorCode.PARTICIPANT_NOT_TRIP_MEMBER);
        }
        if (!tripService.isMember(tripId, request.debtorId())) {
            throw new SnaptleException(ErrorCode.PARTICIPANT_NOT_TRIP_MEMBER);
        }

        PersonalDebt debt = PersonalDebt.create(
                tripId, request.creditorId(), request.debtorId(), request.amount(), request.reason()
        );
        return PersonalDebtResponse.from(personalDebtRepository.save(debt));
    }

    public List<PersonalDebtResponse> getPersonalDebts(Long userId, Long tripId) {
        tripService.requireMember(tripId, userId);

        return personalDebtRepository.findAllByTripIdOrderByCreatedAtDesc(tripId).stream()
                .filter(debt -> debt.getCreditorId().equals(userId) || debt.getDebtorId().equals(userId))
                .map(PersonalDebtResponse::from)
                .toList();
    }
}
