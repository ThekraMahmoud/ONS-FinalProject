package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Notification;
import org.example.ons.Repository.NotificationRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Value("${n8n.email-webhook-url}")
    private String emailWebhookUrl;

    @Value("${ons.email-logo-url}")
    private String logoUrl;

    private final RestClient restClient = RestClient.create();


    public void createNotification(Notification notification) {
         notificationRepository.save(notification);
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

    public void updateNotification(Long id, Notification updatedNotification) {
        Notification notification = getNotificationById(id);

        notification.setMessage(updatedNotification.getMessage());
        notification.setStatus(updatedNotification.getStatus());
        notification.setReport(updatedNotification.getReport());
        notification.setCampaign(updatedNotification.getCampaign());

         notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);
    }

    public void sendNotificationEmail(
            Long notificationId,
            String email,
            String subject) {

        Notification notification = getNotificationById(notificationId);

        ClassPathResource template =
                new ClassPathResource("templates/email-template.html");

        String html;

        try (InputStream inputStream = template.getInputStream()) {
            html = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new ApiException("Could not load email template");
        }

        html = html.replace(
                "{{subject}}",
                HtmlUtils.htmlEscape(subject)
        );

        html = html.replace(
                "{{message}}",
                HtmlUtils.htmlEscape(notification.getMessage())
        );

        html = html.replace(
                "{{logoUrl}}",
                HtmlUtils.htmlEscape(logoUrl)
        );

        Map<String, Object> body = Map.of(
                "notificationId", notification.getNotificationId(),
                "email", email,
                "subject", subject,
                "html", html
        );

        try {
            restClient.post()
                    .uri(emailWebhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new ApiException("Could not submit email to n8n");
        }
    }
}
