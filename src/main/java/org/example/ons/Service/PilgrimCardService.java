        package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.PilgrimCard;
import org.example.ons.Repository.PilgrimCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PilgrimCardService {

    private final PilgrimCardRepository pilgrimCardRepository;

    // Create Pilgrim Card
    public void createPilgrimCard(PilgrimCard pilgrimCard) {
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
            throw new ApiException("Pilgrim card not found with ID: " + id
            );
        }

        return pilgrimCard;
    }

    public void updatePilgrimCard(Integer id, PilgrimCard updatedPilgrimCard) {

        PilgrimCard existingPilgrimCard = getPilgrimCardById(id);

        existingPilgrimCard.setCardNumber(updatedPilgrimCard.getCardNumber());
        existingPilgrimCard.setCardType(updatedPilgrimCard.getCardType());
        existingPilgrimCard.setStatus(updatedPilgrimCard.getStatus());
        existingPilgrimCard.setImageReference(updatedPilgrimCard.getImageReference());
        existingPilgrimCard.setPilgrim(updatedPilgrimCard.getPilgrim());
        pilgrimCardRepository.save(existingPilgrimCard);
    }

    // Delete Pilgrim Card
    public void deletePilgrimCard(Integer id) {
        PilgrimCard pilgrimCard = getPilgrimCardById(id);
        pilgrimCardRepository.delete(pilgrimCard);
    }
}
