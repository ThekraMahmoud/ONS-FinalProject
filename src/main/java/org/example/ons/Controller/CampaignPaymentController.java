package org.example.ons.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ons.Service.CampaignPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class CampaignPaymentController {

    private final CampaignPaymentService campaignPaymentService;

    @PostMapping("/campaign/{campaignId}")
    public ResponseEntity<?> createPayment(
            @PathVariable Integer campaignId) {

        return ResponseEntity.status(200)
                .body(campaignPaymentService.createPayment(
                        campaignId
                ));
    }

    @PostMapping("/campaign/{campaignId}/{paymentId}/verify")
    public ResponseEntity<?> verifyPayment(
            @PathVariable Integer campaignId,
            @PathVariable Long paymentId) {

        return ResponseEntity.status(200)
                .body(campaignPaymentService.verifyPayment(
                        campaignId,
                        paymentId
                ));
    }
}