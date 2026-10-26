package org.example.ons.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.CampaignPayment;
import org.example.ons.Model.User;
import org.example.ons.Service.AuthorizedStaffService;
import org.example.ons.Service.CampaignPaymentService;
import org.example.ons.Service.CampaignService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/campaigns")
@AllArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignPaymentService campaignPaymentService;
    private final AuthorizedStaffService authorizedStaffService;

    @PostMapping("/register")
    public ResponseEntity<?> createCampaign(
            @Valid @RequestBody Campaign campaign,
            HttpServletRequest request) {

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
                            "message",
                            "Campaign registered; payment required",
                            "campaignId", savedCampaign.getId(),
                            "paymentId", payment.getId(),
                            "paymentStatus", payment.getStatus(),
                            "paymentUrl", payment.getPaymentUrl()
                    )
            );

        } catch (ApiException exception) {

            return ResponseEntity.status(502).body(
                    Map.of(
                            "message",
                            "Campaign registered, but payment setup failed",
                            "campaignId", savedCampaign.getId(),
                            "retryPaymentUrl",
                            "/api/v1/payments/campaign/"
                                    + savedCampaign.getId()
                    )
            );
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllCampaigns() {

        return ResponseEntity.status(200)
                .body(campaignService.getAllCampaigns());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCampaignById(
            @AuthenticationPrincipal User user) {

        Integer campaignId = getSupervisorCampaignId(user);

        return ResponseEntity.ok(
                campaignService.getCampaignById(campaignId)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCampaign(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Campaign campaignUpdate) {

        Integer campaignId = getSupervisorCampaignId(user);

        Campaign currentCampaign =
                campaignService.getCampaignById(campaignId);

        // Profile updates cannot activate a campaign without payment.
        campaignUpdate.setStatus(currentCampaign.getStatus());

        campaignService.updateCampaign(
                campaignId,
                campaignUpdate
        );

        return ResponseEntity.ok(
                new ApiResponse("Campaign updated successfully")
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCampaign(
            @PathVariable Integer id) {

        campaignService.deleteCampaign(id);

        return ResponseEntity.ok(
                new ApiResponse("Campaign deleted successfully")
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getCampaignAnalytics(
            @AuthenticationPrincipal User user) {

        Integer campaignId = getSupervisorCampaignId(user);

        return ResponseEntity.ok(
                campaignService.getCampaignAnalytics(campaignId)
        );
    }

    @PostMapping("/analytics/email")
    public ResponseEntity<?> sendCampaignAnalyticsEmail(
            @AuthenticationPrincipal User user) throws Exception {

        Integer campaignId = getSupervisorCampaignId(user);

        campaignService.sendCampaignAnalyticsEmail(campaignId);

        return ResponseEntity.ok(
                new ApiResponse(
                        "Campaign analytics report sent successfully"
                )
        );
    }

    private Integer getSupervisorCampaignId(User user) {

        if (user == null
                || !user.isActive()
                || !"AuthorizedStaff".equals(user.getRole())) {

            throw new AccessDeniedException(
                    "Only campaign supervisors can access this endpoint"
            );
        }

        AuthorizedStaff staff =
                authorizedStaffService.getStaffById(user.getId());

        if (staff.getStaffRole() != AuthorizedStaffRole.SUPERVISOR
                || staff.getCampaign() == null) {

            throw new AccessDeniedException(
                    "You must be a supervisor linked to a campaign"
            );
        }

        return staff.getCampaign().getId();
    }
}