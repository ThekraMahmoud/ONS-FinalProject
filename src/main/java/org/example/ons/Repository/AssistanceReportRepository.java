package org.example.ons.Repository;

import org.example.ons.Model.AssistanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssistanceReportRepository extends JpaRepository<AssistanceReport,Long> {
    boolean existsByCaseNumber(String caseNumber);

    AssistanceReport findAssistanceReportByReportId(Long id);

}
