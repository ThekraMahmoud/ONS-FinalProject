        package org.example.ons.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.CampaignAnalyticsDTO;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.CampaignPayment;
import org.example.ons.Service.CampaignPaymentService;
import org.example.ons.Service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/campaigns")
@AllArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignPaymentService campaignPaymentService;
    // Create Campaign

    @PostMapping("/register")
    public ResponseEntity<?> createCampaign(
            @Valid @RequestBody Campaign campaign, HttpServletRequest request) {

        Campaign savedCampaign =
                campaignService.createCampaign(campaign);
        HttpSession session = request.getSession();

        request.changeSessionId();

        session.setAttribute(
                "registrationCampaignId",
                savedCampaign.getId()
        );

        session.setMaxInactiveInterval(30 * 60);

        try {
            CampaignPayment payment =
                    campaignPaymentService.createPayment(
                            savedCampaign.getId()
                    );

            return ResponseEntity.status(201).body(
                    Map.of(
                            "message", "Campaign registered; payment required",
                            "campaignId", savedCampaign.getId(),
                            "paymentId", payment.getId(),
                            "paymentStatus", payment.getStatus(),
                            "paymentUrl", payment.getPaymentUrl()
                    )
            );

        } catch (ApiException e) {
            return ResponseEntity.status(502).body(
                    Map.of(
                            "message", "Campaign registered, but payment setup failed",
                            "campaignId", savedCampaign.getId(),
                            "retryPaymentUrl",
                            "/api/v1/payments/campaign/"
                                    + savedCampaign.getId()
                    )
            );
        }
    }

    // Get All Campaigns
    @GetMapping("/get")
    public ResponseEntity<?> getAllCampaigns() {
        return ResponseEntity.status(200).body(campaignService.getAllCampaigns());
    }

    // Get Campaign By ID
    @GetMapping("/me")
    public ResponseEntity<?> getCampaignById(@AuthenticationPrincipal Campaign campaign) {
        return ResponseEntity.status(200).body(campaignService.getCampaignById(campaign.getId()));
    }

    // Update Campaign
    @PutMapping("/update")
    public ResponseEntity<?> updateCampaign(@AuthenticationPrincipal Campaign campaign, @Valid @RequestBody Campaign campaignUpdate) {

        campaignService.updateCampaign(campaign.getId(), campaignUpdate);

        return ResponseEntity.status(200).body(new ApiResponse( "Campaign updated successfully"));
    }

    // Delete Campaign
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCampaign(@AuthenticationPrincipal Campaign campaign) {
        campaignService.deleteCampaign(campaign.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Campaign deleted successfully"));
    }


    @GetMapping("/analytics")
    public ResponseEntity<?> getCampaignAnalytics(@AuthenticationPrincipal Campaign campaign) {

        CampaignAnalyticsDTO analytics = campaignService.getCampaignAnalytics(campaign.getId());
        return ResponseEntity.ok(analytics);
    }


    @PostMapping("/analytics/email")
    public ResponseEntity<?> sendCampaignAnalyticsEmail(@AuthenticationPrincipal Campaign campaign) throws Exception {

        campaignService.sendCampaignAnalyticsEmail(campaign.getId());

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Campaign analytics report sent successfully"));
    }
}
