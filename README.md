[README1.md](https://github.com/user-attachments/files/34053312/README1.md)
<div align="center">
  <img src="src/main/resources/images/img.png" alt="ONS logo" width="220" />
  <h1>ONS — Lost Pilgrims Assistance System</h1>
  <p>A digital assistance platform to help identify lost pilgrims and coordinate support with their campaigns.</p>
</div>

## Deployment

[Open the deployed ONS application](https://d3ujyxwecja6r4.cloudfront.net/)

The application is deployed on AWS, where the React web interface and Spring Boot backend are served together. It provides screens for pilgrim assistance and campaign operations, including reports, kiosks, accommodations, and analytics. Some workflows are still being validated against the backend.

## Overview

ONS brings pilgrim identification and assistance workflows together for campaign teams. A pilgrim can request help through an interactive kiosk; the system records the request and kiosk location so the relevant team can coordinate a response.

This repository contains the Spring Boot backend. The frontend is maintained separately.

## Backend capabilities

- Manage pilgrims, pilgrim cards, campaigns, accommodations, kiosks, and staff.
- Identify pilgrims using card information or OCR. OCR processing is delegated to a separate AI service.
- Create and process assistance reports, including assignment, reassignment, status changes, and report history.
- Send notifications through configured email and automation integrations.
- View campaign dashboards and analytics, and access kiosk and map-related services.
- Support campaign registration and payment workflows through Moyasar.

## System diagrams

### Use case diagram

```mermaid
flowchart LR
    Pilgrim["Pilgrim"]
    Staff["Campaign Staff"]
    Admin["System Administrator"]

    subgraph ONS["Smart Lost Pilgrim Assistance System"]
        Language([Select Language])
        Translate([Translate Instructions])
        Request([Request Assistance])
        CreateReport([Create Assistance Report])
        Scan([Scan Pilgrim Card])
        Identify([Identify Pilgrim])
        Find([Find Campaign and Accommodation])
        Receive([Receive Notification])
        Send([Send Notification])
        Details([View Report Details])
        Status([Update Report Status])
        Pickup([Confirm Pilgrim Pickup])
        Users([Manage Authorized Users])
        Reports([Monitor All Reports])
        Kiosks([Manage Kiosks])
    end

    Pilgrim --- Language
    Pilgrim --- Request
    Pilgrim --- Scan
    Pilgrim --- Receive
    Staff --- Receive
    Staff --- Details
    Staff --- Status
    Staff --- Pickup
    Admin --- Users
    Admin --- Reports
    Admin --- Kiosks

    Language -. "«include»" .-> Translate
    Request -. "«include»" .-> CreateReport
    Request -. "«include»" .-> Identify
    Request -. "«include»" .-> Send
    Identify -. "«include»" .-> Find
    Receive -. "«include»" .-> Send
```

### Class diagram

```mermaid
classDiagram
    direction LR

    class Campaign {
        +Long campaignId
        +String campaignName
        +String contactNumber
        +String status
    }
    class Pilgrim {
        +Long pilgrimId
        +String fullName
        +String nationality
        +String preferredLanguage
        +String phoneNumber
    }
    class Accommodation {
        +Long accommodationId
        +String name
        +String address
        +String city
    }
    class PilgrimCard {
        +Long cardId
        +String cardNumber
        +String cardType
        +String status
        +String imageReference
    }
    class AuthorizedStaff {
        +Long staffId
        +String fullName
        +String role
        +String phoneNumber
        +String status
    }
    class Kiosk {
        +Long kioskId
        +String kioskCode
        +String locationName
        +double latitude
        +double longitude
        +String status
    }
    class AssistanceReport {
        +Long reportId
        +LocalDateTime createdAt
        +String caseNumber
        +ReportStatus status
        +LocalDateTime pickupTime
        +createReport()
        +updateStatus()
        +confirmPickup()
    }
    class Notification {
        +Long notificationId
        +String message
        +LocalDateTime sentAt
        +NotificationStatus status
        +send()
        +markAsRead()
    }
    class TranslationService {
        +translateMessage()
        +getLocalizedInstructions()
    }
    class CardRecognitionService {
        +scanCard()
        +extractCardData()
        +validateCardData()
    }
    class AssistanceService {
        +findPilgrim()
        +createReport()
        +notifyCampaign()
        +confirmPickup()
    }
    class ReportStatus {
        <<enumeration>>
        OPEN
        ASSIGNED
        IN_PROGRESS
        RESOLVED
        CANCELLED
    }
    class NotificationStatus {
        <<enumeration>>
        PENDING
        SENT
        FAILED
        READ
    }

    Campaign "1" o-- "0..*" Pilgrim : manages
    Campaign "1" o-- "1..*" Accommodation : operates
    Campaign "1" --> "0..*" AuthorizedStaff : employs
    Pilgrim "0..*" --> "1" Accommodation : stays at
    Pilgrim "1" --> "1" PilgrimCard : has
    Pilgrim "1" --> "0..*" AssistanceReport : has cases
    AuthorizedStaff "0..1" --> "0..*" AssistanceReport : handles
    Kiosk "1" --> "0..*" AssistanceReport : creates
    AssistanceReport "1" --> "0..*" Notification : generates
    AssistanceReport ..> ReportStatus : uses
    Notification ..> NotificationStatus : uses
    TranslationService ..> Kiosk : supports multilingual UI
    CardRecognitionService ..> PilgrimCard : reads
    AssistanceService ..> Pilgrim : finds
    AssistanceService ..> AssistanceReport : creates and updates
    AssistanceService ..> Notification : sends
```

## Technologies and integrations

- Java 17 and Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA, Spring Security, and Bean Validation
- MySQL and Maven
- AWS for deployment
- External integrations configured for OCR/QR processing, Google Maps, n8n notifications, email, and Moyasar payments

## Running locally

1. Install Java 17 or newer and MySQL.
2. Create a MySQL database named `ons`, or update the datasource settings in `src/main/resources/application.properties`.
3. Configure the integration environment variables referenced in `application.properties`: `N8N_EMAIL_WEBHOOK_URL`, `N8N_WHATSAPP_WEBHOOK_URL`, `GOOGLE_MAPS_API_KEY`, `MOYASAR_SECRET_KEY`, `MAIL_USERNAME`, and `MAIL_PASSWORD`.
4. If using OCR, start the separate AI service and set `ons.ai.base-url` to its address. The default is `http://localhost:8000`.
5. Start the backend:

   ```bash
   ./mvnw spring-boot:run
   ```

The API uses the `/api/v1` prefix for most resources. For the Postman collection and its setup notes, see [`postman/README.md`](postman/README.md).

## Team

- **Danah Alsahli** — [@DanahAlsahli](https://github.com/DanahAlsahli)
- **Thekra Mahmoud** — [@ThekraMahmoud](https://github.com/ThekraMahmoud)
- **Faisal Bageis** — [@faisalbageis](https://github.com/faisalbageis)

## Faisal's contribution

My contribution included API documentation for account management, notifications, maps and navigation, roles and permissions, background jobs, and advanced assistance-report workflows. 

### Account management

- `GET /api/v1/auth/get/all`
- `GET /api/v1/auth/get/{id}`
- `PUT /api/v1/auth/activate/{id}`
- `PUT /api/v1/auth/deactivate/{id}`
- `POST /api/v1/auth/change-password/{id}`

### Notifications

- `POST /notification/add`
- `GET /notification/get`
- `GET /notification/get/{id}`
- `PUT /notification/update/{id}`
- `DELETE /notification/delete/{id}`

### Maps and routes

- `GET /api/v1/map/markers`
- `GET /api/v1/map/nearest-kiosk`
- `POST /api/v1/map/route`
- `GET /api/v1/map/kiosks/{kioskId}/nearest-place`
- `GET /api/v1/routes`

### Advanced assistance-report workflows

- `POST /api/v1/reports/{reportId}/merge`
- `GET /api/v1/reports/overdue`
- `GET /api/v1/reports/campaign/{campaignId}/overdue`
- `GET /api/v1/field-staff/availability`

See my [GitHub profile](https://github.com/faisalbageis).
