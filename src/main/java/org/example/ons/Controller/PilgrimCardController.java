        package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Model.PilgrimCard;
import org.example.ons.Service.PilgrimCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pilgrim-cards")
@AllArgsConstructor
public class PilgrimCardController {

    private final PilgrimCardService pilgrimCardService;

    // Create Pilgrim Card
    @PostMapping("/add")
    public ResponseEntity<?> createPilgrimCard(@AuthenticationPrincipal Pilgrim pilgrim, @Valid @RequestBody PilgrimCard pilgrimCard) {
        pilgrimCardService.createPilgrimCard(pilgrim.getId(),pilgrimCard);
        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim card created successfully"));
    }

    // Get All Pilgrim Cards
    @GetMapping("/get")
    public ResponseEntity<?> getAllPilgrimCards() {
        return ResponseEntity.status(200).body(pilgrimCardService.getAllPilgrimCards());
    }

    // Get Pilgrim Card By ID
    @GetMapping("/me")
    public ResponseEntity<?> getPilgrimCardById(@AuthenticationPrincipal Pilgrim pilgrim) {
        return ResponseEntity.status(200).body(pilgrimCardService.getPilgrimCardById(pilgrim.getId()));
    }

    // Update Pilgrim Card
    @PutMapping("/update")
    public ResponseEntity<?> updatePilgrimCard(@AuthenticationPrincipal Pilgrim pilgrim, @Valid @RequestBody PilgrimCard pilgrimCard) {
        pilgrimCardService.updatePilgrimCard(pilgrim.getId(), pilgrimCard);
        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim card updated successfully"));
    }

    // Delete Pilgrim Card
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePilgrimCard(@AuthenticationPrincipal Pilgrim pilgrim) {
        pilgrimCardService.deletePilgrimCard(pilgrim.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim card deleted successfully"));
    }
}
