package com.snaptle.domain.expense;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {

    List<ExpenseParticipant> findAllByExpenseId(Long expenseId);

    List<ExpenseParticipant> findAllByExpenseIdIn(List<Long> expenseIds);
}
