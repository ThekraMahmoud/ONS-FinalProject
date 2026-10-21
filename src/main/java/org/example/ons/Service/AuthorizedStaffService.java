package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.AuthorizedStaffDTO;
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


        authRepository.save(user);
        authorizedStaffRepository.save(staff);
    }

    public void deleteStaff(Integer id) {

        AuthorizedStaff staff = authorizedStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("staff not found"));

        authorizedStaffRepository.delete(staff);
        authRepository.delete(staff.getUser());
    }

}
