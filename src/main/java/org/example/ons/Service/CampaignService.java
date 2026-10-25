package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.Accommodation;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Repository.AccommodationRepository;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.AuthorizedStaffRepository;
import org.example.ons.Repository.CampaignRepository;
import org.example.ons.Repository.PilgrimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final PilgrimRepository pilgrimRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final AccommodationRepository accommodationRepository;
    private final AuthorizedStaffRepository authorizedStaffRepository;
    private final AuthorizedStaffService authorizedStaffService;
    private final NotificationService notificationService;

    // Create campaign
    public Campaign createCampaign(Campaign campaign) {
        campaign.setStatus("PLANNED");

        Campaign savedCampaign = campaignRepository.save(campaign);

        notificationService.notifyCampaignAdded(
                savedCampaign,
                savedCampaign.getEmail()
        );

        return savedCampaign;
    }

    // Get all campaigns
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    // Get campaign by ID
    public Campaign getCampaignById(Integer id) {
        return campaignRepository.findCampaignById(id);
    }

    // Update campaign
    public void updateCampaign(Integer id, Campaign updatedCampaign) {
        Campaign existingCampaign = getRequiredCampaign(id);

        existingCampaign.setCampaignName(updatedCampaign.getCampaignName());
        existingCampaign.setContactNumber(updatedCampaign.getContactNumber());
        existingCampaign.setStartDate(updatedCampaign.getStartDate());
        existingCampaign.setEndDate(updatedCampaign.getEndDate());
        existingCampaign.setStatus(updatedCampaign.getStatus());
        existingCampaign.setEmail(updatedCampaign.getEmail());

        campaignRepository.save(existingCampaign);
    }

    // Delete campaign
    public void deleteCampaign(Integer id) {
        Campaign campaign = getRequiredCampaign(id);
        campaignRepository.delete(campaign);
    }

    // Update campaign status
    public void updateCampaignStatus(Integer campaignId, String status) {
        Campaign campaign = getRequiredCampaign(campaignId);

        if (status == null ||
                !status.matches("PLANNED|ACTIVE|COMPLETED|CANCELLED")) {
            throw new ApiException(
                    "Invalid campaign status. Allowed values: PLANNED, ACTIVE, COMPLETED, CANCELLED"
            );
        }

        campaign.setStatus(status);
        campaignRepository.save(campaign);
    }

    // Get pilgrims belonging to a campaign
    @Transactional(readOnly = true)
    public List<Pilgrim> getCampaignPilgrims(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return pilgrimRepository.findByCampaign_Id(campaignId);
    }

    // Get reports belonging to a campaign
    @Transactional(readOnly = true)
    public List<AssistanceReport> getCampaignReports(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return assistanceReportRepository.findByCampaign_Id(campaignId);
    }

    // Get authorized staff belonging to a campaign
    @Transactional(readOnly = true)
    public List<AuthorizedStaff> getCampaignStaff(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return authorizedStaffRepository.findByCampaign_Id(campaignId);
    }

    // Add a supervisor to a campaign
    public void addStaffToCampaign(
            Integer campaignId,
            AuthorizedStaffDTO staffDTO
    ) {
        getRequiredCampaign(campaignId);
        authorizedStaffService.createStaff(staffDTO, campaignId);
    }

    // Remove authorized staff from a campaign
    public void removeStaffFromCampaign(
            Integer campaignId,
            Integer userId
    ) {
        getRequiredCampaign(campaignId);

        AuthorizedStaff staff =
                authorizedStaffRepository.findAuthorizedStaffById(userId);

        if (staff == null) {
            throw new ApiException("Authorized staff not found with ID: " + userId);
        }

        if (staff.getCampaign() == null
                || !campaignId.equals(staff.getCampaign().getId())) {
            throw new ApiException(
                    "The authorized staff member does not belong to this campaign"
            );
        }

        authorizedStaffService.deleteStaff(userId);
    }

    // Get campaign statistics
    @Transactional(readOnly = true)
    public Map<String, Object> getCampaignStatistics(Integer campaignId) {
        getRequiredCampaign(campaignId);

        List<Pilgrim> pilgrims =
                pilgrimRepository.findByCampaign_Id(campaignId);

        List<AssistanceReport> reports =
                assistanceReportRepository.findByCampaign_Id(campaignId);

        List<AuthorizedStaff> staff =
                authorizedStaffRepository.findByCampaign_Id(campaignId);

        List<Accommodation> accommodations =
                accommodationRepository.findByCampaign_Id(campaignId);

        Map<String, Object> statistics = new HashMap<>();

        statistics.put("campaignId", campaignId);
        statistics.put("totalPilgrims", pilgrims.size());
        statistics.put("totalReports", reports.size());
        statistics.put("totalStaff", staff.size());
        statistics.put("totalAccommodations", accommodations.size());

        statistics.put(
                "openReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.OPEN, campaignId
                )
        );

        statistics.put(
                "assignedReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.ASSIGNED, campaignId
                )
        );

        statistics.put(
                "inProgressReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.IN_PROGRESS, campaignId
                )
        );

        statistics.put(
                "resolvedReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.RESOLVED, campaignId
                )
        );

        return statistics;
    }

    // Get accommodations belonging to a campaign
    @Transactional(readOnly = true)
    public List<Accommodation> getCampaignAccommodations(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return accommodationRepository.findByCampaign_Id(campaignId);
    }

    // Validate campaign existence
    private Campaign getRequiredCampaign(Integer campaignId) {
        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException(
                    "Campaign not found with ID: " + campaignId
            );
        }

        return campaign;
    }
}
