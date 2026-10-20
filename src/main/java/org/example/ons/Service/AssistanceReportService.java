package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Repository.AssistanceReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistanceReportService {
    private final AssistanceReportRepository assistanceReportRepository;

    public AssistanceReport createReport(AssistanceReport report) {
        report.setStatus(ReportStatus.OPEN);
        return assistanceReportRepository.save(report);
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

    public AssistanceReport updateReport(Long id, AssistanceReport updatedReport) {
        AssistanceReport report = getReportById(id);

        report.setPickupTime(updatedReport.getPickupTime());
        report.setPilgrim(updatedReport.getPilgrim());
        report.setKiosk(updatedReport.getKiosk());
        report.setCampaign(updatedReport.getCampaign());

        return assistanceReportRepository.save(report);
    }

    public void deleteReport(Long id) {
        AssistanceReport report = getReportById(id);
        if(report==null){
            throw new ApiException("report ot found");
        }
        assistanceReportRepository.delete(report);
    }

}
