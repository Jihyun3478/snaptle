package com.snaptle.domain.personaldebt;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalDebtRepository extends JpaRepository<PersonalDebt, Long> {

    List<PersonalDebt> findAllByTripIdOrderByCreatedAtDesc(Long tripId);

    List<PersonalDebt> findAllByTripIdAndCreditorIdOrTripIdAndDebtorIdOrderByCreatedAtDesc(
            Long tripIdForCreditor, Long creditorId, Long tripIdForDebtor, Long debtorId);
}
