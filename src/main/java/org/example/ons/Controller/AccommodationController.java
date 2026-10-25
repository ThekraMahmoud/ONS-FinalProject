package org.example.ons.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Accommodation;
import org.example.ons.Service.AccommodationService;
import org.springframework.http.ResponseEntity;
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
    @PostMapping({"", "/add"})
    public ResponseEntity<ApiResponse> addAccommodation(
            @Valid @RequestBody Accommodation accommodation
    ) {
        accommodationService.createAccommodation(accommodation);

        return ResponseEntity.ok(
                new ApiResponse("Accommodation added successfully")
        );
    }

    // 2. Get All Accommodations
    @GetMapping({"", "/get"})
    public ResponseEntity<List<Accommodation>> getAllAccommodations() {
        return ResponseEntity.ok(
                accommodationService.getAllAccommodations()
        );
    }

    // 3. Get Accommodation By ID
    @GetMapping({"/{id}", "/get/{id}"})
    public ResponseEntity<Accommodation> getAccommodationById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                accommodationService.getAccommodationById(id)
        );
    }

    // 4. Update Accommodation
    @PutMapping({"/{id}", "/update/{id}"})
    public ResponseEntity<ApiResponse> updateAccommodation(
            @PathVariable Integer id,
            @Valid @RequestBody Accommodation accommodation
    ) {
        accommodationService.updateAccommodation(id, accommodation);

        return ResponseEntity.ok(
                new ApiResponse("Accommodation updated successfully")
        );
    }

    // 5. Update Accommodation Status
    @PatchMapping("/{accommodationId}/status")
    public ResponseEntity<ApiResponse> updateAccommodationStatus(
            @PathVariable Integer accommodationId,
            @Valid @RequestBody AccommodationStatusRequest request
    ) {
        accommodationService.updateAccommodationStatus(
                accommodationId,
                request.getStatus()
        );

        return ResponseEntity.ok(
                new ApiResponse("Accommodation status updated successfully")
        );
    }

    // 6. Get Pilgrims in Accommodation
    @GetMapping("/{accommodationId}/pilgrims")
    public ResponseEntity<?> getAccommodationPilgrims(
            @PathVariable Integer accommodationId
    ) {
        return ResponseEntity.ok(
                accommodationService.getAccommodationPilgrims(
                        accommodationId
                )
        );
    }

    // 7. Get Reports for Accommodation
    @GetMapping("/{accommodationId}/reports")
    public ResponseEntity<?> getAccommodationReports(
            @PathVariable Integer accommodationId
    ) {
        return ResponseEntity.ok(
                accommodationService.getAccommodationReports(
                        accommodationId
                )
        );
    }

    // 8. Get Accommodation Location
    @GetMapping("/{accommodationId}/location")
    public ResponseEntity<Map<String, Object>> getAccommodationLocation(
            @PathVariable Integer accommodationId
    ) {
        return ResponseEntity.ok(
                accommodationService.getAccommodationLocation(
                        accommodationId
                )
        );
    }

    // Preserve existing Delete endpoint
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteAccommodation(
            @PathVariable Integer id
    ) {
        accommodationService.deleteAccommodation(id);

        return ResponseEntity.ok(
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