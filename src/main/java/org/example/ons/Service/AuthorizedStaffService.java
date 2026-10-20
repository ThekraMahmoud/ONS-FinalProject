package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.Campaign;
import org.example.ons.Repository.AuthorizedStaffRepository;
import org.example.ons.Repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizedStaffService {
    private final AuthorizedStaffRepository authorizedStaffRepository;
    private final CampaignRepository campaignRepository;

    public AuthorizedStaff createStaff(AuthorizedStaff staff) {

        if (authorizedStaffRepository.findAuthorizedStaffByEmail(staff.getEmail()) != null) {
            throw new ApiException("email already exists");
        }

        if (staff.getCampaign() != null) {
            Campaign campaign = campaignRepository.findCampaignById(
                            staff.getCampaign().getCampaignId());

            if(campaign==null){
                throw new ApiException("campaign not fount");
            }


            staff.setCampaign(campaign);
        }

        return authorizedStaffRepository.save(staff);
    }

    public List<AuthorizedStaff> getAllStaff() {
        List<AuthorizedStaff> staffList = authorizedStaffRepository.findAll();

        if (staffList.isEmpty()) {
            throw new ApiException("there is no staff in the system");
        }

        return staffList;
    }

    public AuthorizedStaff getStaffById(Long id) {
        return authorizedStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException("staff not found"));
    }

    public AuthorizedStaff updateStaff(Long id, AuthorizedStaff updatedStaff) {

        AuthorizedStaff staff = getStaffById(id);

        AuthorizedStaff existingStaff =
                authorizedStaffRepository.findAuthorizedStaffByEmail(updatedStaff.getEmail());

        if (existingStaff != null && !existingStaff.getStaffId().equals(id)) {
            throw new ApiException("email already exists");
        }

        if (updatedStaff.getCampaign() != null) {
            Campaign campaign = campaignRepository.findCampaignById(
                    updatedStaff.getCampaign().getCampaignId());

            if (campaign == null) {
                throw new ApiException("campaign not found");
            }
        } else {
            staff.setCampaign(null);
        }

        staff.setName(updatedStaff.getName());
        staff.setEmail(updatedStaff.getEmail());
        staff.setPhoneNumber(updatedStaff.getPhoneNumber());
        staff.setRole(updatedStaff.getRole());
        staff.setActive(updatedStaff.isActive());

        return authorizedStaffRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        AuthorizedStaff staff = getStaffById(id);
        authorizedStaffRepository.delete(staff);
    }
}
