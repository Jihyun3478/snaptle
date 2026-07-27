package com.snaptle.domain.expense;

import com.snaptle.domain.expense.dto.CreateExpenseRequest;
import com.snaptle.domain.expense.dto.ExpenseResponse;
import com.snaptle.domain.expense.dto.OcrRecognizeResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/trips/{tripId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/ocr")
    public ResponseEntity<OcrRecognizeResponse> recognizeReceipt(@AuthenticationPrincipal Long userId,
                                                                   @PathVariable Long tripId,
                                                                   @RequestPart MultipartFile image) {
        return ResponseEntity.ok(expenseService.recognizeReceipt(userId, tripId, image));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@AuthenticationPrincipal Long userId,
                                                           @PathVariable Long tripId,
                                                           @Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.createExpense(userId, tripId, request));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(@AuthenticationPrincipal Long userId,
                                                               @PathVariable Long tripId,
                                                               @RequestParam(required = false) ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.getExpenses(userId, tripId, category));
    }
}
