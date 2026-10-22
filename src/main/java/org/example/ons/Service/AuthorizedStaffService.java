package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.DTO.EmployeeUpdateDTO;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.User;
import org.example.ons.Model.Campaign;
import org.example.ons.Repository.AuthRepository;
import org.example.ons.Repository.AuthorizedStaffRepository;
import org.example.ons.Repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizedStaffService {
    private final AuthorizedStaffRepository authorizedStaffRepository;
    private final CampaignRepository campaignRepository;
    private final AuthRepository authRepository;

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

            staff.setStaffRole(dto.getStaffRole());
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

        dto.setStaffRole(AuthorizedStaffRole.REGULAR_EMPLOYEE);

        createStaff(dto, campaign.getId());
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

}
