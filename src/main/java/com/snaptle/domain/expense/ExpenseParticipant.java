package com.snaptle.domain.expense;

import com.snaptle.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "expense_participants",
        uniqueConstraints = @UniqueConstraint(name = "uk_expense_participants_expense_user",
                columnNames = {"expense_id", "user_id"})
)
public class ExpenseParticipant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_id", nullable = false)
    private Long expenseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "share_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal shareAmount;

    @Builder
    private ExpenseParticipant(Long expenseId, Long userId, BigDecimal shareAmount) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
    }

    public static ExpenseParticipant of(Long expenseId, Long userId, BigDecimal shareAmount) {
        return ExpenseParticipant.builder()
                .expenseId(expenseId)
                .userId(userId)
                .shareAmount(shareAmount)
                .build();
    }
}
