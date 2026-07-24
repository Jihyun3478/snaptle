package com.snaptle.domain.expense;

import com.snaptle.global.common.Currency;
import com.snaptle.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expenses")
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "original_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal originalAmount;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "converted_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal convertedAmount;

    @Column(name = "receipt_image_url")
    private String receiptImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Builder
    private Expense(Long tripId, Long payerId, String merchantName, LocalDateTime paidAt, Currency currency,
                     BigDecimal originalAmount, BigDecimal exchangeRate, BigDecimal convertedAmount,
                     String receiptImageUrl, ExpenseCategory category) {
        this.tripId = tripId;
        this.payerId = payerId;
        this.merchantName = merchantName;
        this.paidAt = paidAt;
        this.currency = currency;
        this.originalAmount = originalAmount;
        this.exchangeRate = exchangeRate;
        this.convertedAmount = convertedAmount;
        this.receiptImageUrl = receiptImageUrl;
        this.category = category;
    }

    public static Expense create(Long tripId, Long payerId, String merchantName, LocalDateTime paidAt,
                                  Currency currency, BigDecimal originalAmount, BigDecimal exchangeRate,
                                  BigDecimal convertedAmount, String receiptImageUrl, ExpenseCategory category) {
        return Expense.builder()
                .tripId(tripId)
                .payerId(payerId)
                .merchantName(merchantName)
                .paidAt(paidAt)
                .currency(currency)
                .originalAmount(originalAmount)
                .exchangeRate(exchangeRate)
                .convertedAmount(convertedAmount)
                .receiptImageUrl(receiptImageUrl)
                .category(category)
                .build();
    }
}
