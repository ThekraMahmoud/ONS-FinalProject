
package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Kiosk;
import org.example.ons.Repository.KioskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KioskService {

    private final KioskRepository kioskRepository;

    // Create Kiosk
    public void createKiosk(Kiosk kiosk) {
        kioskRepository.save(kiosk);
    }

    // Get All Kiosks
    public List<Kiosk> getAllKiosks() {
        return kioskRepository.findAll();
    }

    // Get Kiosk By ID
    public Kiosk getKioskById(Integer id) {
        Kiosk kiosk = kioskRepository.findKioskById(id);

        if (kiosk == null) {
            throw new ApiException("Kiosk not found with ID: " + id);
        }

        return kiosk;
    }

    // Update Kiosk
    public void updateKiosk(Integer id, Kiosk updatedKiosk) {

        Kiosk existingKiosk = getKioskById(id);

        existingKiosk.setKioskCode(updatedKiosk.getKioskCode());
        existingKiosk.setLocationName(updatedKiosk.getLocationName());
        existingKiosk.setLatitude(updatedKiosk.getLatitude());
        existingKiosk.setLongitude(updatedKiosk.getLongitude());
        existingKiosk.setStatus(updatedKiosk.getStatus());

        kioskRepository.save(existingKiosk);
    }

    // Delete Kiosk
     public void deleteKiosk(Integer id) {
        Kiosk kiosk = getKioskById(id);
        kioskRepository.delete(kiosk);
    }
}
