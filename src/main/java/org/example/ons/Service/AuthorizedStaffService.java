package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.DTO.EmployeeUpdateDTO;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizedStaffService {
    private final AuthorizedStaffRepository authorizedStaffRepository;
    private final CampaignRepository campaignRepository;
    private final AuthRepository authRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final ReportStatusHistoryRepository reportStatusHistoryRepository;

    public void createStaff(AuthorizedStaffDTO dto,Integer campaignId) {

        AuthorizedStaff staff = new AuthorizedStaff();

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole("AuthorizedStaff");
        user.setActive(dto.isActive());


            Campaign campaign = campaignRepository.findCampaignById(campaignId);

            if (campaign == null) {
                throw new ApiException("campaign not found");
            }

            staff.setStaffRole(AuthorizedStaffRole.SUPERVISOR);
            staff.setCampaign(campaign);


        authRepository.save(user);
        staff.setUser(user);
        authorizedStaffRepository.save(staff);
    }

    public List<AuthorizedStaff> getAllStaff() {

        List<AuthorizedStaff> staffList = authorizedStaffRepository.findAll();

        if (staffList.isEmpty()) {
            throw new ApiException("there is no staff in the system");
        }

        return staffList;
    }

    public AuthorizedStaff getStaffById(Integer id) {

        AuthorizedStaff staff = authorizedStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("staff not found"));

        return staff;
    }

    public void updateStaff(Integer id, AuthorizedStaffDTO dto) {

        AuthorizedStaff staff = authorizedStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("staff not found"));

        User user = staff.getUser();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setActive(dto.isActive());

        staff.setStaffRole(dto.getStaffRole());
        authRepository.save(user);
        authorizedStaffRepository.save(staff);
    }

    public void deleteStaff(Integer id) {

        AuthorizedStaff staff = authorizedStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("staff not found"));

        authorizedStaffRepository.delete(staff);
        authRepository.delete(staff.getUser());
    }
    public List<AuthorizedStaff> getEmployees(Integer id) {
        AuthorizedStaff authorizedStaff =
                authorizedStaffRepository.findAuthorizedStaffById(id);

        if (authorizedStaff == null) {
            throw new ApiException("user not found");
        }

        if (authorizedStaff.getStaffRole() != AuthorizedStaffRole.SUPERVISOR) {
            throw new ApiException("you are not authorized");
        }

        Campaign campaign = authorizedStaff.getCampaign();

        if (campaign == null) {
            throw new ApiException("supervisor has no campaign");
        }

        List<AuthorizedStaff> employees =
                authorizedStaffRepository.findAuthorizedStaffByCampaignAndStaffRole(
                        campaign,
                        AuthorizedStaffRole.REGULAR_EMPLOYEE
                );

        if (employees.isEmpty()) {
            throw new ApiException("there are no employees in the campaign");
        }

        return employees;
    }


    public void addEmployee(Integer supervisorId, AuthorizedStaffDTO dto) {

        AuthorizedStaff supervisor =
                authorizedStaffRepository.findAuthorizedStaffById(supervisorId);

        if (supervisor == null) {
            throw new ApiException("supervisor not found");
        }

        if (supervisor.getStaffRole() != AuthorizedStaffRole.SUPERVISOR) {
            throw new ApiException("you are not authorized");
        }

        Campaign campaign = supervisor.getCampaign();

        if (campaign == null) {
            throw new ApiException("supervisor has no campaign");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole("AuthorizedStaff");
        user.setActive(dto.isActive());

        AuthorizedStaff employee = new AuthorizedStaff();
        employee.setStaffRole(AuthorizedStaffRole.REGULAR_EMPLOYEE);
        employee.setCampaign(campaign);

        authRepository.save(user);
        employee.setUser(user);
        authorizedStaffRepository.save(employee);
    }


    public void updateEmployee(
            Integer supervisorId,
            Integer employeeId,
            EmployeeUpdateDTO dto) {

        AuthorizedStaff supervisor =
                authorizedStaffRepository.findAuthorizedStaffById(supervisorId);

        if (supervisor == null) {
            throw new ApiException("supervisor not found");
        }

        if (supervisor.getStaffRole() != AuthorizedStaffRole.SUPERVISOR) {
            throw new ApiException("you are not authorized");
        }

        AuthorizedStaff employee =
                authorizedStaffRepository.findAuthorizedStaffById(employeeId);

        if (employee == null) {
            throw new ApiException("employee not found");
        }

        if (employee.getStaffRole() != AuthorizedStaffRole.REGULAR_EMPLOYEE) {
            throw new ApiException("this user is not a regular employee");
        }

        if (supervisor.getCampaign() == null ||
                employee.getCampaign() == null ||
                !supervisor.getCampaign().getId()
                        .equals(employee.getCampaign().getId())) {

            throw new ApiException("employee does not belong to your campaign");
        }

        User user = employee.getUser();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setActive(dto.isActive());

        authRepository.save(user);
    }

    public void deleteEmployee(Integer supervisorId, Integer employeeId) {

        AuthorizedStaff supervisor =
                authorizedStaffRepository.findAuthorizedStaffById(supervisorId);

        if (supervisor == null) {
            throw new ApiException("supervisor not found");
        }

        if (supervisor.getStaffRole() != AuthorizedStaffRole.SUPERVISOR) {
            throw new ApiException("you are not authorized");
        }

        AuthorizedStaff employee =
                authorizedStaffRepository.findAuthorizedStaffById(employeeId);

        if (employee == null) {
            throw new ApiException("employee not found");
        }

        if (employee.getStaffRole() != AuthorizedStaffRole.REGULAR_EMPLOYEE) {
            throw new ApiException("this user is not a regular employee");
        }

        if (supervisor.getCampaign() == null ||
                employee.getCampaign() == null ||
                !supervisor.getCampaign().getId()
                        .equals(employee.getCampaign().getId())) {

            throw new ApiException("employee does not belong to your campaign");
        }

        deleteStaff(employeeId);
    }




    // قبول حاج ضائع
    public void acceptPilgrim(Integer staffId, Integer pilgrimId, long reportId) {

    AuthorizedStaff staff = authorizedStaffRepository.findAuthorizedStaffById(staffId);

        if (staff == null) {
        throw new ApiException("Authorized staff not found");
    }

    AssistanceReport report = assistanceReportRepository.findAssistanceReportByReportId(reportId);
        if (report == null) {
        throw new ApiException("Assistance report not found");
    }
    // التأكد أن التقرير تابع للحاج المطلوب
        if (report.getPilgrim() == null ||
                !report.getPilgrim().getId().equals(pilgrimId)) {
        throw new ApiException("This report does not belong to this pilgrim");
    }

    // التأكد أن الطلب لم تتم معالجته سابقًا
        if (report.getStatus() != ReportStatus.OPEN) {
        throw new ApiException(
                "This report is no longer available for acceptance"
        );
    }
        report.setStatus(ReportStatus.ASSIGNED);
        report.setAuthorizedStaff(staff);

        assistanceReportRepository.save(report);
}




    private void updateReportStatus(AssistanceReport report, ReportStatus newStatus) {

        LocalDateTime now = LocalDateTime.now();

        ReportStatusHistory previous = reportStatusHistoryRepository.findTopByReport_ReportIdOrderByChangedAtDesc(report.getReportId());

        long durationMinutes = 0;

        if (previous != null) {
            durationMinutes = java.time.Duration.between(
                    previous.getChangedAt(),
                    now
            ).toMinutes();
        }

        ReportStatusHistory history = new ReportStatusHistory();
        history.setReport(report);
        history.setOldStatus(report.getStatus());
        history.setNewStatus(newStatus);
        history.setChangedAt(now);
        history.setDurationMinutes(durationMinutes);

        report.setStatus(newStatus);

        assistanceReportRepository.save(report);
        reportStatusHistoryRepository.save(history);
    }



    public void advanceReportStatus(Integer staffId, long reportId) {

        AuthorizedStaff staff = authorizedStaffRepository.findAuthorizedStaffById(staffId);

        if (staff == null) {
            throw new ApiException("Authorized staff not found");
        }

        AssistanceReport report = assistanceReportRepository.findAssistanceReportByReportId(reportId);

        if (report == null) {
            throw new ApiException("Assistance report not found");
        }

        if (report.getAuthorizedStaff() == null ||
                !report.getAuthorizedStaff().getId().equals(staffId)) {
            throw new ApiException(
                    "This report is not assigned to this staff member");
        }

        ReportStatus nextStatus;

        switch (report.getStatus()) {
            case ASSIGNED:
                nextStatus = ReportStatus.IN_PROGRESS;
                break;

            case IN_PROGRESS:
                nextStatus = ReportStatus.RESOLVED;
                break;

            default:
                throw new ApiException(
                        "This report cannot move to the next status");
        }
        updateReportStatus(report, nextStatus);
    }


    public List<AssistanceReport> getStaffReports(Integer staff_id){
        List<AssistanceReport> assistanceReports =
                assistanceReportRepository.findAssistanceReportByAuthorizedStaff_Id(staff_id);

        if(assistanceReports.isEmpty()){
            throw  new ApiException("you dont have reports");
        }
        return assistanceReports;
    }


    public List<AssistanceReport> getStaffReportsByStatus(Integer staff_id,ReportStatus reportStatus){
        List<AssistanceReport> assistanceReports =
                assistanceReportRepository.findAssistanceReportByAuthorizedStaff_IdAndStatus(staff_id,reportStatus);

        if(assistanceReports.isEmpty()){
            throw  new ApiException("you dont have reports");
        }
        return assistanceReports;
    }

}
