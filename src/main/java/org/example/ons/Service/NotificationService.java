package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Notification;
import org.example.ons.Repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;


    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        if(notifications.isEmpty()){
            throw new ApiException("there is no notification in the system");
        }
        return notifications;
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Notification not found"));
    }

    public Notification updateNotification(Long id, Notification updatedNotification) {
        Notification notification = getNotificationById(id);

        notification.setMessage(updatedNotification.getMessage());
        notification.setStatus(updatedNotification.getStatus());
        notification.setReport(updatedNotification.getReport());
        notification.setCampaign(updatedNotification.getCampaign());

        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);
    }
}
