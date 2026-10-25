package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.MergeReportsDTO;
import org.example.ons.DTO.SelfServiceReportDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Service.AssistanceReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/reports", "/report"})
@RequiredArgsConstructor
public class AssistanceReportController {

    private final AssistanceReportService assistanceReportService;

    // 1. POST /api/v1/reports
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody AssistanceReport report) {

        assistanceReportService.createReport(report);

        return ResponseEntity.status(201)
                .body(new ApiResponse("Report created successfully"));
    }

    // 2. GET /api/v1/reports
    @GetMapping
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.ok(
                assistanceReportService.getAllReports()
        );
    }

    // 3. GET /api/v1/reports/{reportId}
    @GetMapping("/{reportId:\\d+}")
    public ResponseEntity<?> getReportById(
            @PathVariable Long reportId) {

        return ResponseEntity.ok(
                assistanceReportService.getReportById(reportId)
        );
    }

    // 4. PUT /api/v1/reports/{reportId}
    @PutMapping("/{reportId:\\d+}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long reportId,
            @RequestBody AssistanceReport updatedReport) {

        assistanceReportService.updateReport(reportId, updatedReport);

        return ResponseEntity.ok(
                new ApiResponse("Report updated successfully")
        );
    }

    // 5. PATCH /api/v1/reports/{reportId}/status
    @PatchMapping("/{reportId:\\d+}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> request) {

        String statusValue = request.get("status");

        if (statusValue == null || statusValue.isBlank()) {
            throw new org.example.ons.Api.ApiException(
                    "Status is required"
            );
        }

        ReportStatus status;

        try {
            status = ReportStatus.valueOf(
                    statusValue.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new org.example.ons.Api.ApiException(
                    "Invalid report status"
            );
        }

        assistanceReportService.updateReportStatus(reportId, status);

        return ResponseEntity.ok(
                new ApiResponse("Report status updated successfully")
        );
    }

    // 6. POST /api/v1/reports/{reportId}/assign
    @PostMapping("/{reportId:\\d+}/assign")
    public ResponseEntity<?> assignReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, Integer> request) {

        Integer fieldStaffId = request.get("fieldStaffId");

        if (fieldStaffId == null) {
            throw new org.example.ons.Api.ApiException(
                    "Field staff ID is required"
            );
        }

        assistanceReportService.assignReport(reportId, fieldStaffId);

        return ResponseEntity.ok(
                new ApiResponse("Report assigned successfully")
        );
    }

    // 7. POST /api/v1/reports/{reportId}/reassign
    @PostMapping("/{reportId:\\d+}/reassign")
    public ResponseEntity<?> reassignReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, Integer> request) {

        Integer fieldStaffId = request.get("fieldStaffId");

        if (fieldStaffId == null) {
            throw new org.example.ons.Api.ApiException(
                    "Field staff ID is required"
            );
        }

        assistanceReportService.reassignReport(reportId, fieldStaffId);

        return ResponseEntity.ok(
                new ApiResponse("Report reassigned successfully")
        );
    }

    // 8. POST /api/v1/reports/{reportId}/accept
    @PostMapping("/{reportId:\\d+}/accept")
    public ResponseEntity<?> acceptReport(
            @PathVariable Long reportId) {

        assistanceReportService.acceptReport(reportId);

        return ResponseEntity.ok(
                new ApiResponse("Report accepted successfully")
        );
    }

    // 9. POST /api/v1/reports/{reportId}/start
    @PostMapping("/{reportId:\\d+}/start")
    public ResponseEntity<?> startReport(
            @PathVariable Long reportId) {

        assistanceReportService.startReport(reportId);

        return ResponseEntity.ok(
                new ApiResponse("Report processing started")
        );
    }

    // 10. POST /api/v1/reports/{reportId}/resolve
    @PostMapping("/{reportId:\\d+}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long reportId) {

        assistanceReportService.resolveReport(reportId);

        return ResponseEntity.ok(
                new ApiResponse("Report resolved successfully")
        );
    }

    // 11. POST /api/v1/reports/{reportId}/cancel
    @PostMapping("/{reportId:\\d+}/cancel")
    public ResponseEntity<?> cancelReport(
            @PathVariable Long reportId) {

        assistanceReportService.cancelReport(reportId);

        return ResponseEntity.ok(
                new ApiResponse("Report cancelled successfully")
        );
    }

    // 12. POST /api/v1/reports/{reportId}/reopen
    @PostMapping("/{reportId:\\d+}/reopen")
    public ResponseEntity<?> reopenReport(
            @PathVariable Long reportId) {

        assistanceReportService.reopenReport(reportId);

        return ResponseEntity.ok(
                new ApiResponse("Report reopened successfully")
        );
    }

    // 13. GET /api/v1/reports/{reportId}/history
    @GetMapping("/{reportId:\\d+}/history")
    public ResponseEntity<?> getReportHistory(
            @PathVariable Long reportId) {

        return ResponseEntity.ok(
                assistanceReportService.getReportHistory(reportId)
        );
    }

    // 14. GET /api/v1/reports/{reportId}/assignees
    @GetMapping("/{reportId:\\d+}/assignees")
    public ResponseEntity<?> getReportAssignees(
            @PathVariable Long reportId) {

        return ResponseEntity.ok(
                assistanceReportService.getReportAssignees(reportId)
        );
    }

    // 15. GET /api/v1/reports/{reportId}/notifications
    @GetMapping("/{reportId:\\d+}/notifications")
    public ResponseEntity<?> getReportNotifications(
            @PathVariable Long reportId) {

        return ResponseEntity.ok(
                assistanceReportService.getReportNotifications(reportId)
        );
    }

    // 16. GET /api/v1/reports/summary
    @GetMapping("/summary")
    public ResponseEntity<?> getReportsSummary() {
        return ResponseEntity.ok(
                assistanceReportService.getReportsSummary()
        );
    }

    // 17. GET /api/v1/reports/statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getReportsStatistics() {
        return ResponseEntity.ok(
                assistanceReportService.getReportsStatistics()
        );
    }

    // 18. GET /api/v1/reports/export
    @GetMapping(
            value = "/export",
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> exportReports() {

        String csv = assistanceReportService.exportReports();

        byte[] content = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reports.csv"
                )
                .contentType(
                        MediaType.parseMediaType("text/csv;charset=UTF-8")
                )
                .body(content);
    }

    // إنشاء بلاغ الخدمة الذاتية - المسار الحالي
    @PostMapping("/self-service")
    public ResponseEntity<?> createSelfServiceReport(
            @Valid @RequestBody SelfServiceReportDTO dto) {

        AssistanceReport report =
                assistanceReportService.createSelfServiceReport(dto);

        return ResponseEntity.status(201).body(
                Map.of(
                        "message", "Assistance report created successfully",
                        "reportId", report.getReportId(),
                        "caseNumber", report.getCaseNumber(),
                        "status", report.getStatus().name()
                )
        );
    }

    // المسارات القديمة للمحافظة على التوافق

    @PostMapping("/add")
    public ResponseEntity<?> createReportLegacy(
            @RequestBody AssistanceReport report) {

        assistanceReportService.createReport(report);

        return ResponseEntity.ok(
                new ApiResponse("Report created successfully")
        );
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllReportsLegacy() {
        return ResponseEntity.ok(
                assistanceReportService.getAllReports()
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReportByIdLegacy(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assistanceReportService.getReportById(id)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReportLegacy(
            @PathVariable Long id,
            @RequestBody AssistanceReport updatedReport) {

        assistanceReportService.updateReport(id, updatedReport);

        return ResponseEntity.ok(
                new ApiResponse("Report updated successfully")
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReportLegacy(
            @PathVariable Long id) {

        assistanceReportService.deleteReport(id);

        return ResponseEntity.ok(
                new ApiResponse("Report deleted successfully")
        );
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<?> countReportByStatus(
            @PathVariable ReportStatus status) {

        return ResponseEntity.ok(
                assistanceReportService.countReportByStatus(status)
        );
    }

    @GetMapping("/count/campaign/{campaignId}/status/{status}")
    public ResponseEntity<?> countReportByStatusForCampaign(
            @PathVariable Integer campaignId,
            @PathVariable ReportStatus status) {

        return ResponseEntity.ok(
                assistanceReportService.countReportByStatusForCampaign(
                        status, campaignId
                )
        );
    }

    @PostMapping("/{reportId}/merge")
    public ResponseEntity<?> mergeReports(
            @PathVariable Long reportId,
            @Valid @RequestBody MergeReportsDTO dto) {

        assistanceReportService.mergeReports(reportId, dto);

        return ResponseEntity.ok(
                new ApiResponse("Reports merged and assigned successfully")
        );
    }

    @GetMapping("/campaign/{campaignId}/overdue")
    public ResponseEntity<?> getOverdueReportsForCampaign(
            @PathVariable Integer campaignId) {

        return ResponseEntity.ok(
                assistanceReportService.getCampaignOverdueReports(campaignId)
        );
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> getAllOverdueReports() {
        return ResponseEntity.ok(
                assistanceReportService.getAllOverdueReports()
        );
    }
}