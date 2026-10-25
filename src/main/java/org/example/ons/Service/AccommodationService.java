package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Accommodation;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Repository.AccommodationRepository;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.CampaignRepository;
import org.example.ons.Repository.PilgrimRepository;
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

    // Create Accommodation
    public void createAccommodation(Accommodation accommodation) {

        if (accommodation.getCampaign() != null
                && accommodation.getCampaign().getId() != null) {

            Campaign campaign = campaignRepository
                    .findCampaignById(accommodation.getCampaign().getId());

            if (campaign == null) {
                throw new ApiException("Campaign not found");
            }

            accommodation.setCampaign(campaign);
        }

        if (accommodation.getStatus() == null) {
            accommodation.setStatus("ACTIVE");
        }

        accommodationRepository.save(accommodation);
    }

    // Get All Accommodations
    public List<Accommodation> getAllAccommodations() {
        return accommodationRepository.findAll();
    }

    // Get Accommodation By ID
    public Accommodation getAccommodationById(Integer id) {
        return getRequiredAccommodation(id);
    }

    // Update Accommodation
    public void updateAccommodation(
            Integer id,
            Accommodation updatedAccommodation
    ) {
        Accommodation existingAccommodation = getRequiredAccommodation(id);

        existingAccommodation.setName(updatedAccommodation.getName());
        existingAccommodation.setAddress(updatedAccommodation.getAddress());
        existingAccommodation.setCity(updatedAccommodation.getCity());
        existingAccommodation.setLatitude(updatedAccommodation.getLatitude());
        existingAccommodation.setLongitude(updatedAccommodation.getLongitude());

        if (updatedAccommodation.getCampaign() != null
                && updatedAccommodation.getCampaign().getId() != null) {

            Campaign campaign = campaignRepository.findCampaignById(
                    updatedAccommodation.getCampaign().getId()
            );

            if (campaign == null) {
                throw new ApiException("Campaign not found");
            }

            existingAccommodation.setCampaign(campaign);
        }

        accommodationRepository.save(existingAccommodation);
    }

    // Delete Accommodation - preserve existing functionality
    public void deleteAccommodation(Integer id) {
        Accommodation accommodation = getRequiredAccommodation(id);
        accommodationRepository.delete(accommodation);
    }

    // Update Accommodation Status
    public void updateAccommodationStatus(Integer id, String status) {
        Accommodation accommodation = getRequiredAccommodation(id);

        if (status == null
                || !status.matches("ACTIVE|INACTIVE")) {
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