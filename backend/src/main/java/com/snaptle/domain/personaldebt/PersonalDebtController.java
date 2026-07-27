package com.snaptle.domain.personaldebt;

import com.snaptle.domain.personaldebt.dto.CreatePersonalDebtRequest;
import com.snaptle.domain.personaldebt.dto.PersonalDebtResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips/{tripId}/personal-debts")
public class PersonalDebtController {

    private final PersonalDebtService personalDebtService;

    public PersonalDebtController(PersonalDebtService personalDebtService) {
        this.personalDebtService = personalDebtService;
    }

    @PostMapping
    public ResponseEntity<PersonalDebtResponse> createPersonalDebt(@AuthenticationPrincipal Long userId,
                                                                     @PathVariable Long tripId,
                                                                     @Valid @RequestBody CreatePersonalDebtRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personalDebtService.createPersonalDebt(userId, tripId, request));
    }

    @GetMapping
    public ResponseEntity<List<PersonalDebtResponse>> getPersonalDebts(@AuthenticationPrincipal Long userId,
                                                                         @PathVariable Long tripId) {
        return ResponseEntity.ok(personalDebtService.getPersonalDebts(userId, tripId));
    }
}
