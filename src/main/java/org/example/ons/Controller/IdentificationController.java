package org.example.ons.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.example.ons.Service.IdentificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.ons.Model.IdentificationRequest;
import org.example.ons.Service.IdentificationRequestService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/identification")
@AllArgsConstructor
public class IdentificationController {

    private final IdentificationService identificationService;
    private final IdentificationRequestService identificationRequestService;

    // Scan QR - frontend sends the extracted card number
    @PostMapping("/qr/scan")
    public ResponseEntity<?> scanQr(
            @Valid @RequestBody QrScanRequest request) {

        IdentificationRequest identificationRequest =
                identificationService.createRequest(request.getCardNumber());

        return ResponseEntity.ok(Map.of(
                "message", "Identification request created",
                "requestId", identificationRequest.getRequestId(),
                "status", identificationRequest.getStatus().name()
        ));
    }

    public static class QrScanRequest {

        @NotBlank(message = "Card number is required")
        @Size(max = 50, message = "Card number must not exceed 50 characters")
        private String cardNumber;

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    // Validate extracted identification data
    @PostMapping("/validate")
    public ResponseEntity<?> validateIdentity(
            @Valid @RequestBody org.example.ons.DTO.VerifyPilgrimDTO dto) {

        return ResponseEntity.ok(
                identificationService.validateIdentity(dto)
        );
    }

    // Get identification request by ID
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<?> getRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                identificationRequestService.getRequestById(requestId)
        );
    }

    // Retry failed identification request
    @PostMapping("/requests/{requestId}/retry")
    public ResponseEntity<?> retryRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                identificationRequestService.retryRequest(requestId)
        );
    }

    // Submit request for manual review
    @PostMapping("/requests/{requestId}/manual-review")
    public ResponseEntity<?> manualReview(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                identificationRequestService.submitForManualReview(requestId)
        );
    }

}
