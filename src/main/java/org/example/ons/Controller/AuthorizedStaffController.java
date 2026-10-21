package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.User;
import org.example.ons.Service.AuthorizedStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authorized-staff")
@RequiredArgsConstructor
public class AuthorizedStaffController {
    private final AuthorizedStaffService authorizedStaffService;
    @PostMapping("/create/{campaignId}")
    public ResponseEntity<?> createStaff(
            @PathVariable Integer campaignId,
            @Valid @RequestBody AuthorizedStaffDTO dto) {

        authorizedStaffService.createStaff(dto, campaignId);

        return ResponseEntity.status(200).body(new ApiResponse("Authorized staff Registered successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStaff() {

        return ResponseEntity.status(200).body(authorizedStaffService.getAllStaff());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Integer id) {

        return ResponseEntity.status(200).body(authorizedStaffService.getStaffById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable Integer id,
            @Valid @RequestBody AuthorizedStaffDTO dto) {

        authorizedStaffService.updateStaff(id, dto);

        return ResponseEntity.status(200).body(new ApiResponse("Authorized staff updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {

        authorizedStaffService.deleteStaff(id);

        return ResponseEntity.status(200).body(new ApiResponse("Authorized staff deleted successfully"));
    }
}
