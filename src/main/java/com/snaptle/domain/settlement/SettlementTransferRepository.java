package com.snaptle.domain.settlement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementTransferRepository extends JpaRepository<SettlementTransfer, Long> {

    List<SettlementTransfer> findAllBySettlementId(Long settlementId);
}
