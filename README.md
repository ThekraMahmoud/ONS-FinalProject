<div align="center">
  <img src="https://raw.githubusercontent.com/faisalbageis/ONS-FinalProject/main/src/main/resources/images/img.png" alt="ONS logo" width="220" />
  <h1>ONS — Lost Pilgrims Assistance System</h1>
  <p>A digital assistance platform to help identify lost pilgrims and coordinate support with their campaigns.</p>
</div>

## Deployment

[Open the deployed ONS application](https://d3ujyxwecja6r4.cloudfront.net/)

ONS is deployed on AWS. The frontend is maintained in a separate repository from the Spring Boot backend.

- **Frontend repository:** [ONS-Frontend](https://github.com/ThekraMahmoud/ONS-Frontend)
- **Backend / main project repository:** [ONS-FinalProject](https://github.com/faisalbageis/ONS-FinalProject)

## Overview

ONS brings pilgrim identification and assistance workflows together for campaign teams. A pilgrim can request help through an interactive kiosk; the system records the request and kiosk location so the relevant team can coordinate a response.

This repository contains the ONS frontend developed with React and Vite. The backend provides APIs for pilgrim assistance, kiosk operations, campaign workflows, and reporting.

## Frontend

- **Framework:** React
- **Build tool:** Vite
- **Language:** JavaScript
- **Mapping:** Leaflet and React Leaflet
- **API integration:** Axios
- **Backend API prefix:** `/api/v1`

### UI/UX and visual identity

The ONS interface uses a modern visual direction inspired by Islamic art and architecture:

- Islamic geometric patterns and decorative motifs.
- Architectural arches inspired by Islamic design.
- A coordinated emerald-green, ivory, and gold palette.
- Consistent layouts, clear information hierarchy, and readable spacing.
- User-focused screens for pilgrims, campaign teams, supervisors, field staff, and administrators.

## Frontend features and contributions

- Developed ONS frontend interfaces using React and Vite.
- Worked on the Haramain interactive map using Leaflet and React Leaflet.
- Integrated kiosk data into the frontend, including device locations and statuses.
- Worked on frontend integration with backend APIs for kiosk information, activity, reports, and campaign analytics.

## Selected API endpoints

The following endpoints were identified in the supplied `KioskController.java` and `CampaignController.java` files. They are listed as ONS API routes; confirm individual ownership against the code history before attributing each endpoint personally.

### Kiosk management and monitoring

| Method | Endpoint | Description |
|---|---|---|
| `PATCH` | `/api/v1/kiosks/update-status/{id}` | Update kiosk status |
| `PATCH` | `/api/v1/kiosks/update-location/{kioskId}` | Update kiosk location |
| `PATCH` | `/api/v1/kiosks/heartbeat/all` | Update heartbeat signals for all kiosks |
| `GET` | `/api/v1/kiosks/{kioskId}/reports` | Retrieve reports associated with a kiosk |
| `GET` | `/api/v1/kiosks/activity` | Retrieve kiosk activity data |
| `GET` | `/api/v1/kiosks/report/{adminId}` | Kiosk report operation associated with an admin ID |

### Campaign analytics

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/campaigns/analytics` | Retrieve analytics for the authenticated user's campaign |
| `POST` | `/api/v1/campaigns/analytics/email` | Send a campaign analytics report by email |

### Additional workflows

Other work described for the project includes case receipt confirmation, final handover confirmation, handover history, assistance requests initiated through kiosks, and administrative service functionality. The exact route paths for these workflows are not listed here because their controller files have not yet been provided for verification.

## Project screenshots

Screenshots can be added to `docs/images/` and embedded here when the actual image files are available.

<!-- Example:
<div align="center">
  <img src="docs/images/home-page.png" alt="ONS home page" width="800" />
</div>
-->

## Running the frontend locally

1. Clone the frontend repository:

   ```bash
   git clone https://github.com/ThekraMahmoud/ONS-Frontend.git
   cd ONS-Frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the development server:

   ```bash
   npm run dev
   ```

Configure the backend API connection and any environment variables according to the current project configuration.

> Never commit passwords, API keys, access tokens, or other secrets to GitHub.

## Team

- **Thekra Mahmoud** — [@ThekraMahmoud](https://github.com/ThekraMahmoud)
- **Danah Alsahli** — [@DanahAlsahli](https://github.com/DanahAlsahli)
- **Faisal Bageis** — [@faisalbageis](https://github.com/faisalbageis)

## Thekra Mahmoud's contributions

- Frontend development with React and Vite.
- UI/UX work for ONS, including a visual direction inspired by Islamic art and architecture.
- Haramain interactive map integration using Leaflet / React Leaflet.
- Kiosk-related functionality and integration, including location updates, status updates, heartbeat monitoring, and kiosk activity/report retrieval.
- Campaign analytics functionality and analytics email reporting.
- Contributions to assistance handover workflows and administrative services.

> The endpoint list above is based on the controller files shared for review. Add the exact paths for handover and Admin Service endpoints after checking their respective controller files.
