package com.snaptle.domain.settlement;

import com.snaptle.domain.settlement.dto.SettlementResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips/{tripId}/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping
    public ResponseEntity<SettlementResponse> endTripAndSettle(@AuthenticationPrincipal Long userId,
                                                                 @PathVariable Long tripId) {
        return ResponseEntity.ok(settlementService.endTripAndSettle(userId, tripId));
    }

    @GetMapping
    public ResponseEntity<SettlementResponse> getSettlement(@AuthenticationPrincipal Long userId,
                                                              @PathVariable Long tripId) {
        return ResponseEntity.ok(settlementService.getSettlement(userId, tripId));
    }
}
