package org.example.ons.Repository;

import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Kiosk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssistanceReportRepository extends JpaRepository<AssistanceReport,Long> {
    boolean existsByCaseNumber(String caseNumber);

    AssistanceReport findAssistanceReportByReportId(Long id);
    List<AssistanceReport>findAssistanceReportByKiosk(Kiosk kiosk);
    long countByKiosk(Kiosk kiosk);

}
