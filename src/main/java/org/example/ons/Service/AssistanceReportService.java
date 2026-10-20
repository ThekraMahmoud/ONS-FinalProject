package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Kiosk;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.CampaignRepository;
import org.example.ons.Repository.KioskRepository;
import org.example.ons.Repository.PilgrimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistanceReportService {
    private final AssistanceReportRepository assistanceReportRepository;
    private final PilgrimRepository pilgrimRepository;
    private final KioskRepository kioskRepository;
    private final CampaignRepository campaignRepository;

    public void createReport(AssistanceReport report) {
        Pilgrim pilgrim = pilgrimRepository.findById(
                report.getPilgrim().getId()).orElse(null);

        if (pilgrim == null) {
            throw new ApiException("pilgrim not found");
        }

        Kiosk kiosk = kioskRepository.findById(
                report.getKiosk().getId()).orElse(null);

        if (kiosk == null) {
            throw new ApiException("kiosk not found");
        }

        Campaign campaign = campaignRepository.findCampaignById(
                report.getCampaign().getId());

        if (campaign == null) {
            throw new ApiException("campaign not found");
        }

        report.setPilgrim(pilgrim);
        report.setKiosk(kiosk);
        report.setCampaign(campaign);
        report.setStatus(ReportStatus.OPEN);
        assistanceReportRepository.save(report);
    }

    public List<AssistanceReport> getAllReports() {
        List<AssistanceReport> assistanceReports= assistanceReportRepository.findAll();
        if(assistanceReports.isEmpty()){
            throw new ApiException("there is no reports in the system");
        }
        return assistanceReports;
    }

    public AssistanceReport getReportById(Long id) {
        AssistanceReport assistanceReport= assistanceReportRepository.findAssistanceReportByReportId(id);
        if(assistanceReport==null){
            throw new ApiException("report not found");
        }
               return assistanceReport;
    }

    public void updateReport(Long id, AssistanceReport updatedReport) {
        AssistanceReport report = getReportById(id);

        Pilgrim pilgrim = pilgrimRepository.findById(
                updatedReport.getPilgrim().getId()).orElse(null);

        if (pilgrim == null) {
            throw new ApiException("pilgrim not found");
        }

        Kiosk kiosk = kioskRepository.findById(
                updatedReport.getKiosk().getId()).orElse(null);

        if (kiosk == null) {
            throw new ApiException("kiosk not found");
        }

        Campaign campaign = campaignRepository.findCampaignById(
                updatedReport.getCampaign().getId());

        if (campaign == null) {
            throw new ApiException("campaign not found");
        }

        report.setPilgrim(pilgrim);
        report.setKiosk(kiosk);
        report.setCampaign(campaign);

        report.setPickupTime(updatedReport.getPickupTime());
         assistanceReportRepository.save(report);
    }

    public void deleteReport(Long id) {
        AssistanceReport report = getReportById(id);
        if(report==null){
            throw new ApiException("report not found");
        }
        assistanceReportRepository.delete(report);
    }

}
