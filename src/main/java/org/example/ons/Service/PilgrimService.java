package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.PilgrimResponseDTO;
import org.example.ons.Model.Accommodation;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.CampaignRepository;
import org.example.ons.Repository.PilgrimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PilgrimService {

    private final PilgrimRepository pilgrimRepository;
    private final CampaignRepository campaignRepository;
    private final AssistanceReportRepository assistanceReportRepository;

    // Create Pilgrim
    public void createPilgrim(Integer campaignId, Pilgrim pilgrim) {
        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found with ID: " + campaignId);
        }

        pilgrim.setCampaign(campaign);
        pilgrimRepository.save(pilgrim);
    }

    // Get All Pilgrims
    public List<Pilgrim> getAllPilgrims() {
        return pilgrimRepository.findAll();
    }

    // Get Pilgrim By ID
    public Pilgrim getPilgrimById(Integer id) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(id);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }

        return pilgrim;
    }

    // Get Campaign By Pilgrim ID
    public Campaign getPilgrimCampaign(Integer pilgrimId) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(pilgrimId);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + pilgrimId);
        }

        if (pilgrim.getCampaign() == null) {
            throw new ApiException("No campaign associated with this pilgrim");
        }

        return pilgrim.getCampaign();
    }

    // Get Accommodation By Pilgrim ID
    public Accommodation getPilgrimAccommodation(Integer pilgrimId) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(pilgrimId);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + pilgrimId);
        }

        if (pilgrim.getAccommodation() == null) {
            throw new ApiException("No accommodation associated with this pilgrim");
        }

        return pilgrim.getAccommodation();
    }

    // Get Reports By Pilgrim ID
    public List<AssistanceReport> getPilgrimReports(Integer pilgrimId) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(pilgrimId);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + pilgrimId);
        }

        return assistanceReportRepository.findByPilgrim_Id(pilgrimId);
    }

    // Update Pilgrim
    public void updatePilgrim(Integer id, Pilgrim updatedPilgrim) {
        Pilgrim existingPilgrim = pilgrimRepository.findPilgrimById(id);

        if (existingPilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }

        existingPilgrim.setFullName(updatedPilgrim.getFullName());
        existingPilgrim.setNationality(updatedPilgrim.getNationality());
        existingPilgrim.setPhoneNumber(updatedPilgrim.getPhoneNumber());
        existingPilgrim.setVisaNumber(updatedPilgrim.getVisaNumber());
        existingPilgrim.setCampaign(updatedPilgrim.getCampaign());
        existingPilgrim.setAccommodation(updatedPilgrim.getAccommodation());

        pilgrimRepository.save(existingPilgrim);
    }

    // Delete Pilgrim
    public void deletePilgrim(Integer id) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(id);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }

        pilgrimRepository.delete(pilgrim);
    }

    // Search Pilgrim by Visa Number
    public Pilgrim searchPilgrimByVisaNumber(String visaNumber) {
        if (visaNumber == null || visaNumber.isBlank()) {
            throw new ApiException("Visa number is required");
        }

        Pilgrim pilgrim = pilgrimRepository.findPilgrimByVisaNumber(
                visaNumber.trim()
        );

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found");
        }

        return pilgrim;
    }

    // Search Pilgrims by Full Name
    public List<Pilgrim> searchPilgrimsByName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new ApiException("Pilgrim name is required");
        }

        return pilgrimRepository.findByFullNameContainingIgnoreCase(
                fullName.trim()
        );
    }

    // Get Pilgrim Response DTO
    public PilgrimResponseDTO getPilgrimResponseById(Integer id) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(id);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }

        Campaign campaign = pilgrim.getCampaign();

        if (campaign == null) {
            throw new ApiException("No campaign linked to this pilgrim");
        }

        return new PilgrimResponseDTO(
                pilgrim.getFullName(),
                pilgrim.getNationality(),
                pilgrim.getPhoneNumber(),
                pilgrim.getVisaNumber(),
                campaign.getId(),
                campaign.getCampaignName()
        );
    }
}
