
        package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.CampaignAnalyticsDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.CampaignRepository;
import org.example.ons.Repository.PilgrimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;


        @Service
@AllArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final NotificationService notificationService;
    private final PilgrimRepository pilgrimRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final EmailService emailService;

    // Create Campaign
    public Campaign createCampaign(Campaign campaign) {

        campaign.setStatus("PLANNED");

        Campaign savedCampaign = campaignRepository.save(campaign);

        notificationService.notifyCampaignAdded(
                savedCampaign,
                savedCampaign.getEmail()
        );

        return savedCampaign;
    }

    // Get All Campaigns
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    // Get Campaign By ID
    public Campaign getCampaignById(Integer id) {
        Campaign campaign = campaignRepository.findCampaignById(id);

        if (campaign == null) {
            throw new ApiException("Campaign not found with ID: " + id);
        }

        return campaign;
    }

    // Update Campaign
    public void updateCampaign(Integer id, Campaign updatedCampaign) {

        Campaign existingCampaign = campaignRepository.findCampaignById(id);

 if(existingCampaign==null){
    throw new ApiException("Campaign not found with ID: " + id);
 }
        existingCampaign.setCampaignName(updatedCampaign.getCampaignName());
        existingCampaign.setContactNumber(updatedCampaign.getContactNumber());
        existingCampaign.setStartDate(updatedCampaign.getStartDate());
        existingCampaign.setEndDate(updatedCampaign.getEndDate());
        existingCampaign.setStatus(updatedCampaign.getStatus());
        existingCampaign.setEmail(updatedCampaign.getEmail());

        campaignRepository.save(existingCampaign);
    }

    // Delete Campaign
    public void deleteCampaign(Integer id) {
        Campaign campaign = getCampaignById(id);
        campaignRepository.delete(campaign);
    }



    //تعرض تقرير للحمله
    public CampaignAnalyticsDTO getCampaignAnalytics(Integer campaignId) {

                Campaign campaign = campaignRepository.findCampaignById(campaignId);

                if (campaign == null) {
                    throw new ApiException("Campaign not found");
                }

                List<Pilgrim> pilgrims = pilgrimRepository.findAll()
                        .stream()
                        .filter(p -> p.getCampaign() != null
                                && Objects.equals(p.getCampaign().getId(), campaignId))
                        .toList();

                List<AssistanceReport> reports = assistanceReportRepository.findAll()
                        .stream()
                        .filter(r -> r.getCampaign() != null
                                && Objects.equals(r.getCampaign().getId(), campaignId))
                        .toList();

                long totalPilgrims = pilgrims.size();
                long totalReports = reports.size();

                long pilgrimsWithReports = reports.stream()
                        .filter(r -> r.getPilgrim() != null)
                        .map(r -> r.getPilgrim().getId())
                        .filter(Objects::nonNull)
                        .distinct()
                        .count();

                double reportRate = totalPilgrims == 0
                        ? 0
                        : (double) pilgrimsWithReports / totalPilgrims * 100;

                long openReports = reports.stream()
                        .filter(r -> r.getStatus() == ReportStatus.OPEN)
                        .count();

                long assignedReports = reports.stream()
                        .filter(r -> r.getStatus() == ReportStatus.ASSIGNED)
                        .count();

                long inProgressReports = reports.stream()
                        .filter(r -> r.getStatus() == ReportStatus.IN_PROGRESS)
                        .count();

                long resolvedReports = reports.stream()
                        .filter(r -> r.getStatus() == ReportStatus.RESOLVED)
                        .count();

                long cancelledReports = reports.stream()
                        .filter(r -> r.getStatus() == ReportStatus.CANCELLED)
                        .count();

                double resolutionRate = totalReports == 0
                        ? 0
                        : (double) resolvedReports / totalReports * 100;

                List<String> recommendations = new ArrayList<>();

                if (openReports > 0) {
                    recommendations.add(
                            "There are " + openReports
                                    + " open reports that require follow-up."
                    );
                }

                if (assignedReports > 0) {
                    recommendations.add(
                            "There are " + assignedReports
                                    + " assigned reports awaiting follow-up."
                    );
                }

                if (inProgressReports > 0) {
                    recommendations.add(
                            "There are " + inProgressReports
                                    + " reports currently in progress."
                    );
                }

                if (cancelledReports > 0) {
                    recommendations.add(
                            "There are " + cancelledReports
                                    + " cancelled reports."
                    );
                }

                if (totalReports > 0 && resolutionRate < 70) {
                    recommendations.add(
                            "The report resolution rate is low. "
                                    + "Improve report follow-up and resource allocation."
                    );
                }

                if (totalPilgrims > 0 && reportRate >= 20) {
                    recommendations.add(
                            "The percentage of pilgrims with reports is high. "
                                    + "Review the campaign's needs and available resources."
                    );
                }

                if (recommendations.isEmpty()) {
                    recommendations.add(
                            "No additional recommendations based on the current statistics."
                    );
                }

                return new CampaignAnalyticsDTO(
                        campaign.getId(),
                        campaign.getCampaignName(),
                        totalPilgrims,
                        pilgrimsWithReports,
                        Math.round(reportRate * 100.0) / 100.0,
                        totalReports,
                        openReports,
                        assignedReports,
                        inProgressReports,
                        resolvedReports,
                        cancelledReports,
                        Math.round(resolutionRate * 100.0) / 100.0,
                        recommendations
                );
            }

//ترسل التقرير ايميل


            public void sendCampaignAnalyticsEmail(Integer campaignId) throws Exception {

                Campaign campaign = campaignRepository.findCampaignById(campaignId);

                if (campaign == null) {
                    throw new ApiException("Campaign not found");
                }

                String email = campaign.getEmail();

                if (email == null || email.isBlank()) {
                    throw new ApiException("Campaign email not found");
                }

                CampaignAnalyticsDTO dto = getCampaignAnalytics(campaignId);

                String subject = "تقرير حملة أُنس - " + dto.getCampaignName();

                String recommendations = dto.getRecommendations() == null
                        || dto.getRecommendations().isEmpty()
                        ? "لا توجد توصيات إضافية."
                        : String.join("\n", dto.getRecommendations());

                String html = """
            <!DOCTYPE html>
            <html lang="ar" dir="rtl">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>تقرير الحملة</title>
            </head>
            <body style="margin:0;padding:24px;background:#f5f7fa;
                         font-family:Arial,sans-serif;color:#263238;direction:rtl;">
                <div style="max-width:700px;margin:auto;background:#ffffff;
                            padding:30px;border-radius:12px;">

                    <h2 style="color:#087e75;text-align:center;">
                        تقرير إحصائيات الحملة
                    </h2>

                    <h3 style="color:#087e75;">معلومات الحملة</h3>
                    <p><strong>اسم الحملة:</strong> %s</p>
                    <p><strong>رقم الحملة:</strong> %d</p>

                    <hr style="border:0;border-top:1px solid #e0e0e0;">

                    <h3 style="color:#087e75;">إحصائيات الحجاج</h3>
                    <p>إجمالي الحجاج: %d</p>
                    <p>الحجاج الذين لديهم بلاغات: %d</p>
                    <p>نسبة الحجاج الذين لديهم بلاغات: %.2f%%</p>

                    <hr style="border:0;border-top:1px solid #e0e0e0;">

                    <h3 style="color:#087e75;">إحصائيات البلاغات</h3>
                    <p>إجمالي البلاغات: %d</p>
                    <p>البلاغات المفتوحة: %d</p>
                    <p>البلاغات المسندة: %d</p>
                    <p>البلاغات قيد المعالجة: %d</p>
                    <p>البلاغات المحلولة: %d</p>
                    <p>البلاغات الملغاة: %d</p>

                    <div style="background:#e8f5f3;padding:15px;border-radius:8px;">
                        <strong>نسبة حل البلاغات: %.2f%%</strong>
                    </div>

                    <h3 style="color:#087e75;">التوصيات</h3>
                    <div style="white-space:pre-line;line-height:1.8;">%s</div>

                    <hr style="border:0;border-top:1px solid #e0e0e0;">

                    <p style="text-align:center;color:#78909c;font-size:12px;">
                        أُنس | في ضيافة الرحمن
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                        escapeHtml(dto.getCampaignName()),
                        dto.getCampaignId(),
                        dto.getTotalPilgrims(),
                        dto.getPilgrimsWithReports(),
                        dto.getReportRate(),
                        dto.getTotalReports(),
                        dto.getOpenReports(),
                        dto.getAssignedReports(),
                        dto.getInProgressReports(),
                        dto.getResolvedReports(),
                        dto.getCancelledReports(),
                        dto.getResolutionRate(),
                        escapeHtml(recommendations)
                );

                emailService.sendHtmlEmail(email, subject, html);
            }

            private String escapeHtml(String value) {
                if (value == null) {
                    return "";
                }

                return value.replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#39;");
            }





}
