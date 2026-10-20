package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Service.AuthorizedStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class AuthorizedStaffController {
    private final AuthorizedStaffService authorizedStaffService;
    @PostMapping("/add")
    public ResponseEntity<?> createStaff(@Valid @RequestBody AuthorizedStaff staff) {
        authorizedStaffService.createStaff(staff);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Staff created successfully"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllStaff() {
        List<AuthorizedStaff> staffList = authorizedStaffService.getAllStaff();
        return ResponseEntity.status(200).body(staffList);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Long id) {
        AuthorizedStaff staff = authorizedStaffService.getStaffById(id);
        return ResponseEntity.status(200).body(staff);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody AuthorizedStaff updatedStaff) {

        authorizedStaffService.updateStaff(id, updatedStaff);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Staff updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        authorizedStaffService.deleteStaff(id);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Staff deleted successfully"));
    }
}
