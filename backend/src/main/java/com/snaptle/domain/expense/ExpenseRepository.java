package com.snaptle.domain.expense;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByTripIdOrderByPaidAtDesc(Long tripId);

    List<Expense> findAllByTripIdAndCategoryOrderByPaidAtDesc(Long tripId, ExpenseCategory category);
}
