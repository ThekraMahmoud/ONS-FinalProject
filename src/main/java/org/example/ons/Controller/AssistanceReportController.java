package org.example.ons.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Service.AssistanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class AssistanceReportController {
    private final AssistanceReportService assistanceReportService;


    @PostMapping("/add")
    public ResponseEntity<?> createReport(@RequestBody AssistanceReport report) {
        assistanceReportService.createReport(report);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Report created successfully"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllReports() {
        List<AssistanceReport> reports = assistanceReportService.getAllReports();
        return ResponseEntity.status(200).body(reports);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        AssistanceReport report = assistanceReportService.getReportById(id);
        return ResponseEntity.status(200).body(report);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody AssistanceReport updatedReport) {

        assistanceReportService.updateReport(id, updatedReport);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Report updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        assistanceReportService.deleteReport(id);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Report deleted successfully"));
    }



}
