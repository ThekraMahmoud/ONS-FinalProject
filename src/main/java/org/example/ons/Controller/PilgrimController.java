package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Service.PilgrimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.ons.Service.PilgrimCardService;
import org.example.ons.DTO.VerifyPilgrimDTO;

@RestController
@RequestMapping("/api/v1/pilgrims")
@AllArgsConstructor
public class PilgrimController {

    private final PilgrimService pilgrimService;
    private final PilgrimCardService pilgrimCardService;

    // Create Pilgrim
    @PostMapping("/add/{campaignId}")
    public ResponseEntity<?> createPilgrim(
            @PathVariable Integer campaignId,
            @Valid @RequestBody Pilgrim pilgrim) {

        pilgrimService.createPilgrim(campaignId, pilgrim);

        return ResponseEntity.ok(
                new ApiResponse("Pilgrim created successfully")
        );
    }

    // Get All Pilgrims
    @GetMapping("/get")
    public ResponseEntity<?> getAllPilgrims() {
        return ResponseEntity.ok(pilgrimService.getAllPilgrims());
    }

    // Get Pilgrim By ID - existing endpoint
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPilgrimById(@PathVariable Integer id) {
        return ResponseEntity.ok(pilgrimService.getPilgrimById(id));
    }

    // Identify Pilgrim Using QR Card Number
    @PostMapping("/identify")
    public ResponseEntity<?> identifyPilgrim(
            @RequestBody java.util.Map<String, String> request) {

        String cardNumber = request.get("cardNumber");

        Pilgrim pilgrim =
                pilgrimCardService.identifyPilgrimByCardNumber(cardNumber);

        return ResponseEntity.ok(pilgrim);
    }

    // Get Pilgrim By ID - required endpoint
    @GetMapping("/{pilgrimId:\\d+}")
    public ResponseEntity<?> getPilgrimByRequiredId(
            @PathVariable Integer pilgrimId) {

        return ResponseEntity.ok(
                pilgrimService.getPilgrimById(pilgrimId)
        );
    }

    // Get Campaign By Pilgrim ID
    @GetMapping("/{pilgrimId:\\d+}/campaign")
    public ResponseEntity<?> getPilgrimCampaign(
            @PathVariable Integer pilgrimId) {

        return ResponseEntity.ok(
                pilgrimService.getPilgrimCampaign(pilgrimId)
        );
    }

    // Get Accommodation By Pilgrim ID
    @GetMapping("/{pilgrimId:\\d+}/accommodation")
    public ResponseEntity<?> getPilgrimAccommodation(
            @PathVariable Integer pilgrimId) {

        return ResponseEntity.ok(
                pilgrimService.getPilgrimAccommodation(pilgrimId)
        );
    }

    // Get Reports By Pilgrim ID
    @GetMapping("/{pilgrimId:\\d+}/reports")
    public ResponseEntity<?> getPilgrimReports(
            @PathVariable Integer pilgrimId) {

        return ResponseEntity.ok(
                pilgrimService.getPilgrimReports(pilgrimId)
        );
    }

    // Update Pilgrim
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePilgrim(
            @PathVariable Integer id,
            @Valid @RequestBody Pilgrim pilgrim) {

        pilgrimService.updatePilgrim(id, pilgrim);

        return ResponseEntity.ok(
                new ApiResponse("Pilgrim updated successfully")
        );
    }

    // Delete Pilgrim
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePilgrim(@PathVariable Integer id) {
        pilgrimService.deletePilgrim(id);

        return ResponseEntity.ok(
                new ApiResponse("Pilgrim deleted successfully")
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPilgrimIdentity(
            @Valid @RequestBody VerifyPilgrimDTO dto) {

        Pilgrim pilgrim =
                pilgrimCardService.verifyPilgrimIdentity(dto);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Pilgrim identity verified successfully",
                        "pilgrimId", pilgrim.getId(),
                        "fullName", pilgrim.getFullName(),
                        "visaNumber", pilgrim.getVisaNumber()
                )
        );
    }

    // Search Pilgrim by Visa Number
    @GetMapping("/search")
    public ResponseEntity<?> searchPilgrim(
            @RequestParam(required = false) String visaNumber,
            @RequestParam(required = false) String fullName) {

        if (visaNumber != null && !visaNumber.isBlank()) {
            return ResponseEntity.ok(
                    pilgrimService.searchPilgrimByVisaNumber(visaNumber)
            );
        }

        if (fullName != null && !fullName.isBlank()) {
            return ResponseEntity.ok(
                    pilgrimService.searchPilgrimsByName(fullName)
            );
        }

        throw new org.example.ons.Api.ApiException(
                "Provide visaNumber or fullName to search"
        );
    }

    // Manual Identification - Verify pilgrim using visa and card numbers
    @PostMapping("/manual-identification")
    public ResponseEntity<?> manualIdentification(
            @Valid @RequestBody VerifyPilgrimDTO dto) {

        Pilgrim pilgrim = pilgrimCardService.verifyPilgrimIdentity(dto);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message", "Pilgrim identity verified successfully",
                        "pilgrimId", pilgrim.getId(),
                        "fullName", pilgrim.getFullName(),
                        "visaNumber", pilgrim.getVisaNumber()
                )
        );
    }

    @GetMapping("/etPilgrimResponse/{id}")
    public ResponseEntity<?> getPilgrimResponseById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                pilgrimService.getPilgrimResponseById(id)
        );
    }
}