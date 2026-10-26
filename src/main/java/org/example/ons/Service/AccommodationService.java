package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final PilgrimRepository pilgrimRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    // Create Accommodation
    public void createAccommodation(Integer campaignId, Accommodation accommodation) {

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        accommodation.setCampaign(campaign);

        if (accommodation.getStatus() == null) {
            accommodation.setStatus("ACTIVE");
        }

        accommodationRepository.save(accommodation);
    }


    // Get All Accommodations
    public List<Accommodation> getAllAccommodations(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new ApiException("Only admin can view all accommodations");
        }
        return accommodationRepository.findAll();
    }

    // Get Accommodation By ID
    public Accommodation getAccommodationById(Integer id) {
        return getRequiredAccommodation(id);
    }

    // Update Accommodation
    public void updateAccommodation(
            Integer campaignId,
            Integer id,
            Accommodation updatedAccommodation) {

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        Accommodation existingAccommodation = getRequiredAccommodation(id);

        // تأكد أن المبنى تابع للحملة المسجّلة دخولها
        if (existingAccommodation.getCampaign() == null
                || !existingAccommodation.getCampaign().getId().equals(campaignId)) {
            throw new ApiException("You are not authorized to update this accommodation");
        }

        existingAccommodation.setName(updatedAccommodation.getName());
        existingAccommodation.setAddress(updatedAccommodation.getAddress());
        existingAccommodation.setCity(updatedAccommodation.getCity());
        existingAccommodation.setLatitude(updatedAccommodation.getLatitude());
        existingAccommodation.setLongitude(updatedAccommodation.getLongitude());

        // لا نغيّر الحملة المرتبطة بالمبنى من بيانات الطلب
        accommodationRepository.save(existingAccommodation);
    }

    // Delete Accommodation - preserve existing functionality
    public void deleteAccommodation(Integer campaignId, Integer id) {

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        Accommodation accommodation = getRequiredAccommodation(id);

        if (accommodation.getCampaign() == null
                || !accommodation.getCampaign().getId().equals(campaignId)) {
            throw new ApiException("You are not authorized to delete this accommodation");
        }

        accommodationRepository.delete(accommodation);
    }

    // Update Accommodation Status
    public void updateAccommodationStatus(
            Integer campaignId,
            Integer id,
            String status) {

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        Accommodation accommodation = getRequiredAccommodation(id);

        if (accommodation.getCampaign() == null
                || !accommodation.getCampaign().getId().equals(campaignId)) {
            throw new ApiException(
                    "You are not authorized to update this accommodation"
            );
        }

        if (status == null || !status.matches("ACTIVE|INACTIVE")) {
            throw new ApiException(
                    "Invalid status. Allowed values: ACTIVE, INACTIVE"
            );
        }

        accommodation.setStatus(status);
        accommodationRepository.save(accommodation);
    }







    // Get Pilgrims in an Accommodation
    @Transactional(readOnly = true)
    public List<Pilgrim> getAccommodationPilgrims(Integer id) {
        getRequiredAccommodation(id);

        return pilgrimRepository.findByAccommodation_Id(id);
    }


    //حلوه للادمن
    // Get Reports for Pilgrims staying at an Accommodation
    @Transactional(readOnly = true)
    public List<AssistanceReport> getAccommodationReports(Integer id) {
        getRequiredAccommodation(id);

        List<Pilgrim> pilgrims =
                pilgrimRepository.findByAccommodation_Id(id);

        List<AssistanceReport> reports = new ArrayList<>();

        for (Pilgrim pilgrim : pilgrims) {
            reports.addAll(
                    assistanceReportRepository.findByPilgrim_Id(
                            pilgrim.getId()
                    )
            );
        }

        return reports;
    }

    //ترجع بيانات الحجاج للحمله
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCampaignAccommodationsWithPilgrimsAndReports(
            Integer campaignId) {

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        List<Accommodation> accommodations =
                accommodationRepository.findByCampaign_Id(campaignId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Accommodation accommodation : accommodations) {

            List<Pilgrim> pilgrims =
                    pilgrimRepository.findByAccommodation_Id(accommodation.getId());

            List<Map<String, Object>> pilgrimsData = new ArrayList<>();

            for (Pilgrim pilgrim : pilgrims) {

                List<AssistanceReport> reports =
                        assistanceReportRepository.findByPilgrim_Id(pilgrim.getId());

                Map<String, Object> pilgrimData = new HashMap<>();
                pilgrimData.put("pilgrimId", pilgrim.getId());
                pilgrimData.put("reports", reports);

                pilgrimsData.add(pilgrimData);
            }

            Map<String, Object> accommodationData = new HashMap<>();
            accommodationData.put("accommodationId", accommodation.getId());
            accommodationData.put("accommodationName", accommodation.getName());
            accommodationData.put("address", accommodation.getAddress());
            accommodationData.put("city", accommodation.getCity());
            accommodationData.put("pilgrims", pilgrimsData);

            result.add(accommodationData);
        }

        return result;
    }

    // Get Accommodation Location
    @Transactional(readOnly = true)
    public Map<String, Object> getAccommodationLocation(Integer id) {
        Accommodation accommodation = getRequiredAccommodation(id);

        Map<String, Object> location = new HashMap<>();

        location.put("accommodationId", accommodation.getId());
        location.put("name", accommodation.getName());
        location.put("address", accommodation.getAddress());
        location.put("city", accommodation.getCity());
        location.put("latitude", accommodation.getLatitude());
        location.put("longitude", accommodation.getLongitude());

        return location;
    }

    // Validate Accommodation Existence
    private Accommodation getRequiredAccommodation(Integer id) {
        Accommodation accommodation =
                accommodationRepository.findAccommodationById(id);

        if (accommodation == null) {
            throw new ApiException(
                    "Accommodation not found with ID: " + id
            );
        }

        return accommodation;
    }



}