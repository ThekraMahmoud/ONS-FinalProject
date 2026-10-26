
        package org.example.ons.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.*;
import org.example.ons.Service.AccommodationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accommodations")
@AllArgsConstructor
@Validated
public class AccommodationController {

    private final AccommodationService accommodationService;

    // 1. Create Accommodation
    @PreAuthorize("hasAuthority('CAMPAIGN')")
    @PostMapping("/add")
    public ResponseEntity<?> addAccommodation(
            @AuthenticationPrincipal Campaign campaign,
            @Valid @RequestBody Accommodation accommodation) {

        accommodationService.createAccommodation(
                campaign.getId(),
                accommodation
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation added successfully")
        );
    }


    // 2. Get All Accommodations - ADMIN Only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<?> getAllAccommodations(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(200).body(
                accommodationService.getAllAccommodations(user.getId())
        );
    }


    // 3. Get Accommodation By ID - ADMIN Only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccommodationById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationById(id)
        );
    }


    // 4. Update Accommodation
    @PreAuthorize("hasAuthority('CAMPAIGN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAccommodation(
            @AuthenticationPrincipal Campaign campaign,
            @PathVariable Integer id,
            @Valid @RequestBody Accommodation accommodation) {

        accommodationService.updateAccommodation(
                campaign.getId(),
                id,
                accommodation
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation updated successfully")
        );
    }


    // 5. Update Accommodation Status
    @PreAuthorize("hasAuthority('CAMPAIGN')")
    @PatchMapping("/{accommodationId}/status")
    public ResponseEntity<?> updateAccommodationStatus(
            @AuthenticationPrincipal Campaign campaign,
            @PathVariable Integer accommodationId,
            @Valid @RequestBody AccommodationStatusRequest request) {

        accommodationService.updateAccommodationStatus(
                campaign.getId(),
                accommodationId,
                request.getStatus()
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation status updated successfully")
        );
    }


    // 6. Get Pilgrims in Accommodation - ADMIN Only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{accommodationId}/pilgrims")
    public ResponseEntity<?> getAccommodationPilgrims(
            @PathVariable Integer accommodationId) {

        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationPilgrims(
                        accommodationId
                )
        );
    }


    // 7. Get Reports for Accommodation - ADMIN Only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{accommodationId}/reports")
    public ResponseEntity<?> getAccommodationReports(
            @PathVariable Integer accommodationId) {

        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationReports(
                        accommodationId
                )
        );
    }


    // 8. Get All Accommodations with Pilgrims and Reports for Campaign
    @PreAuthorize("hasAuthority('CAMPAIGN')")
    @GetMapping("/my-campaign")
    public ResponseEntity<?> getCampaignAccommodationsWithPilgrimsAndReports(
            @AuthenticationPrincipal Campaign campaign) {

        return ResponseEntity.status(200).body(
                accommodationService
                        .getCampaignAccommodationsWithPilgrimsAndReports(
                                campaign.getId()
                        )
        );
    }


    // 9. Get Accommodation Location - ADMIN Only
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{accommodationId}/location")
    public ResponseEntity<?> getAccommodationLocation(
            @PathVariable Integer accommodationId) {

        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationLocation(
                        accommodationId
                )
        );
    }


    // 10. Delete Accommodation
    @PreAuthorize("hasAuthority('CAMPAIGN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccommodation(
            @AuthenticationPrincipal Campaign campaign,
            @PathVariable Integer id) {

        accommodationService.deleteAccommodation(
                campaign.getId(),
                id
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation deleted successfully")
        );
    }


    // Request body for status update
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AccommodationStatusRequest {

        @NotBlank
        @Pattern(
                regexp = "ACTIVE|INACTIVE",
                message = "Status must be ACTIVE or INACTIVE"
        )
        private String status;
    }
}