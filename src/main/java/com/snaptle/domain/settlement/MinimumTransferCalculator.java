package com.snaptle.domain.settlement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 순잔액(받을 돈 - 낼 돈) 맵을 입력받아, 채무자가 채권자에게 보내야 할 송금 목록을
 * 그리디(최대 채무자 - 최대 채권자 우선 매칭) 방식으로 계산한다.
 * 최적해(이론적 최소 송금 횟수)를 항상 보장하지는 않지만 실용적으로 널리 쓰이는 근사 알고리즘이다.
 */
final class MinimumTransferCalculator {

    private MinimumTransferCalculator() {
    }

    record Transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
    }

    static List<Transfer> calculate(Map<Long, BigDecimal> netBalances) {
        List<Balance> creditors = new ArrayList<>();
        List<Balance> debtors = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            int comparison = entry.getValue().compareTo(BigDecimal.ZERO);
            if (comparison > 0) {
                creditors.add(new Balance(entry.getKey(), entry.getValue()));
            } else if (comparison < 0) {
                debtors.add(new Balance(entry.getKey(), entry.getValue().negate()));
            }
        }

        creditors.sort(Comparator.comparing((Balance b) -> b.amount).reversed());
        debtors.sort(Comparator.comparing((Balance b) -> b.amount).reversed());

        List<Transfer> transfers = new ArrayList<>();
        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size() && debtorIndex < debtors.size()) {
            Balance creditor = creditors.get(creditorIndex);
            Balance debtor = debtors.get(debtorIndex);

            BigDecimal transferAmount = creditor.amount.min(debtor.amount);
            transfers.add(new Transfer(debtor.userId, creditor.userId, transferAmount));

            creditor.amount = creditor.amount.subtract(transferAmount);
            debtor.amount = debtor.amount.subtract(transferAmount);

            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }
            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }
        }

        return transfers;
    }

    private static final class Balance {
        private final Long userId;
        private BigDecimal amount;

        private Balance(Long userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }
}
