package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Model.PilgrimCard;
import org.example.ons.Repository.PilgrimCardRepository;
import org.example.ons.Repository.PilgrimRepository;
import org.springframework.stereotype.Service;
import org.example.ons.DTO.VerifyPilgrimDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class PilgrimCardService {

    private final PilgrimCardRepository pilgrimCardRepository;
    private final PilgrimRepository pilgrimRepository;

    // Create Pilgrim Card
    public void createPilgrimCard(Integer pilgrimId, PilgrimCard pilgrimCard) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(pilgrimId);

        if (pilgrim == null) {
            throw new ApiException(
                    "Pilgrim not found with ID: " + pilgrimId
            );
        }

        if (pilgrim.getPilgrimCard() != null) {
            throw new ApiException("This pilgrim already has a card");
        }

        pilgrimCard.setPilgrim(pilgrim);
        pilgrimCardRepository.save(pilgrimCard);
    }

    // Get All Pilgrim Cards
    public List<PilgrimCard> getAllPilgrimCards() {
        return pilgrimCardRepository.findAll();
    }

    // Get Pilgrim Card By ID
    public PilgrimCard getPilgrimCardById(Integer id) {
        PilgrimCard pilgrimCard =
                pilgrimCardRepository.findPilgrimCardById(id);

        if (pilgrimCard == null) {
            throw new ApiException(
                    "Pilgrim card not found with ID: " + id
            );
        }

        return pilgrimCard;
    }

    // Identify Pilgrim using the card number extracted from QR
    public Pilgrim identifyPilgrimByCardNumber(String cardNumber) {

        if (cardNumber == null || cardNumber.isBlank()) {
            throw new ApiException("Card number is required");
        }

        PilgrimCard pilgrimCard =
                pilgrimCardRepository.findPilgrimCardByCardNumber(
                        cardNumber.trim()
                );

        if (pilgrimCard == null) {
            throw new ApiException("Pilgrim card not found");
        }

        if (!"ACTIVE".equals(pilgrimCard.getStatus())) {
            throw new ApiException("Pilgrim card is not active");
        }

        Pilgrim pilgrim = pilgrimCard.getPilgrim();

        if (pilgrim == null) {
            throw new ApiException(
                    "No pilgrim is associated with this card"
            );
        }

        return pilgrim;
    }

    // Update Pilgrim Card
    public void updatePilgrimCard(
            Integer id,
            PilgrimCard updatedPilgrimCard
    ) {
        PilgrimCard existingPilgrimCard = getPilgrimCardById(id);

        existingPilgrimCard.setCardNumber(
                updatedPilgrimCard.getCardNumber()
        );
        existingPilgrimCard.setCardType(
                updatedPilgrimCard.getCardType()
        );
        existingPilgrimCard.setStatus(
                updatedPilgrimCard.getStatus()
        );
        existingPilgrimCard.setImageReference(
                updatedPilgrimCard.getImageReference()
        );

        pilgrimCardRepository.save(existingPilgrimCard);
    }

    // Delete Pilgrim Card
    public void deletePilgrimCard(Integer id) {
        PilgrimCard pilgrimCard = getPilgrimCardById(id);
        pilgrimCardRepository.delete(pilgrimCard);
    }

    public Pilgrim verifyPilgrimIdentity(VerifyPilgrimDTO dto) {

        if (dto == null
                || dto.getVisaNumber() == null
                || dto.getVisaNumber().isBlank()
                || dto.getCardNumber() == null
                || dto.getCardNumber().isBlank()) {
            throw new ApiException(
                    "Visa number and card number are required"
            );
        }

        PilgrimCard card =
                pilgrimCardRepository.findPilgrimCardByCardNumber(
                        dto.getCardNumber().trim()
                );

        if (card == null) {
            throw new ApiException("Pilgrim card not found");
        }

        if (!"ACTIVE".equals(card.getStatus())) {
            throw new ApiException("Pilgrim card is not active");
        }

        Pilgrim pilgrim = card.getPilgrim();

        if (pilgrim == null) {
            throw new ApiException(
                    "No pilgrim is associated with this card"
            );
        }

        if (!pilgrim.getVisaNumber().equals(
                dto.getVisaNumber().trim())) {
            throw new ApiException(
                    "Visa number does not match the card holder"
            );
        }

        return pilgrim;
    }

}