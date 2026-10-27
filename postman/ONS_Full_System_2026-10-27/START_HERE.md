# ONS — full system, one ordered run

This collection was built from ONS.zip supplied on 27 October 2026. All 94 Java source files in that ZIP matched the workspace. It contains **493 saved requests**, covering **126 controller methods / 155 method-and-path mappings / 16 controllers**, plus filter-managed logout. Timed polling adds repeated requests during execution.

## Import and run

1. Start the ONS backend and its database with your usual configuration. Keep the published n8n email/WhatsApp workflows, UltraMsg instance, SMTP and Moyasar **test** credentials available. The Google key must have quota. Start the Python AI service for image tests.
2. Import **ONS_FULL_SYSTEM_Run_Once.postman_collection.json** into Postman. This is the NEW collection named **ONS — FULL SYSTEM — Run once — 27 Oct 2026**. No environment file is needed; all settings are collection variables.
3. Open that collection → **Variables**. Fill these three values once and save:

   | Variable | Value |
   |---|---|
   | `adminPassword` | Your existing admin password |
   | `testEmail` | Your receiving Gmail address; generated plus-addresses deliver to the same inbox |
   | `testPhone` | Your receiving Saudi phone in `05xxxxxxxx` format |

   `adminEmail` defaults to `admin@ons.com`; change it if necessary. `baseUrl` defaults to `http://localhost:8080` and applies to every request. Use **No environment** to avoid confusion with yesterday's settings; the scripts also give collection values precedence over stale environment values.
4. For the included image uploads, set Postman **Settings → General → Working directory** to this extracted folder. The upload requests initially point to the fixture files in this workspace. If you moved the folder or are using another computer, select the matching `fixtures/qr.png`, `fixtures/hid.png`, or `fixtures/empty.png` file in the six multipart requests in folder **05B**, once before the run. The zero-byte file intentionally tests empty-upload rejection.
5. Click **Run collection**. Select **all folders**, use **1 iteration**, keep the saved order, and use a **500 ms request delay**. Keep Postman running until the final folder finishes. The reminder/overdue section waits until its dedicated report is about **16 minutes old**. Each timer script waits at most 45 seconds, with a bounded poll count.

Do not run folders separately on the first run: later requests need the users, records, IDs and registration cookies captured by earlier requests. You can rerun the whole collection: it generates fresh email aliases, case numbers, visas and card IDs and clears previously captured IDs/cookies.

## What runs automatically

- Registers campaigns A and B using the current nested `user` registration body.
- Verifies that the server forces `CAMPAIGN_OWNER`, inactive owner and a PENDING payment, despite submitted `role=ADMIN` and `active=true`.
- Tests payment creation/reuse, unpaid verification, registration-cookie ownership, cross-session rejection and rejection without a session. Cookies are captured separately for A and B; the ordinary cookie jar is disabled per request.
- Proves inactive owners cannot log in, then explicitly uses ADMIN activation of **these new test owners only** to run the owner permission suite. Campaigns stay PLANNED and invoices stay PENDING. **Admin activation does not prove that paid checkout works.**
- Creates supervisors, ordinary campaign employees, owner-created employees, field workers, kiosks, accommodations, pilgrims, cards, reports and notification records. No manual ID setup is needed.
- Exercises owner/supervisor management, admin restrictions, own/foreign-campaign reads and writes, public identification, QR/OCR image integration, employee acceptance, report assignment and report state transitions.
- Exercises all report controller mappings on both `/api/v1/reports` and `/report`, using distinct fixtures for the alias lifecycle.
- Exercises dashboards, campaign analytics and analytics email, notification CRUD, map routes, nearest kiosk, nearest gate/bathroom, OSRM, heartbeat, password changes, account deactivation/reactivation and password serialization checks.
- Reads every protected mapping without credentials and expects 401. These authentication tests are separate from the functional requests.
- Waits for the existing seven-minute scheduler to set `reminderSubmitted`, then checks the 15-minute overdue result. It does not alter database timestamps or call a nonexistent reminder endpoint.
- Deletes disposable fixtures after their workflows, preserving the main records for investigation. Alias DELETE uses an unassigned disposable report rather than a report with status history.

The run creates real test invoices and sends messages through the configured services. Verify actual email/WhatsApp arrival in your apps: an HTTP success or `reminderSubmitted=true` confirms backend submission, not inbox delivery. Expected 400/401/403 rejection responses count as passing **only when the associated test expects that code**.

## Settings and honest limits

Most checks are enabled by default:

| Setting | Default | Effect |
|---|---|---|
| `runGoogleTests` | `true` | Five functional Google-related endpoint requests; server-side fan-out may make more provider calls. A 429 fails its request, then blocks later Google requests and records them as skipped. No automatic provider retries. |
| `runAiImageTests` | `true` | Real Python QR/OCR integrations using synthetic images. The fixed HID `447000000000001` is intentionally independent of the newly registered cards. OCR checks extraction and creation of an identification request; the other public identity tests check a registered card. |
| `runTimedChecks` | `true` | Real seven-minute reminder and 15-minute overdue checks; approximately 16 minutes total fixture age. |
| `runPasswordChange` | `true` | Changes only the newly created spare employee's password and tests authentication with the new password. |
| `runCleanup` | `true` | Deletes disposable notification/card/pilgrim/accommodation/kiosk/staff/report fixtures from this run. It is not a full database reset. |
| `runDisclosureDiagnostics` | `true` | Additional password/report-collection serialization checks. |
| `runAdminEmailReport` | `true` | Sends the kiosk activity report to the existing admin's configured email. |
| `runCampaignCleanup` | `false` | Opt-in DELETE of this run's extra registration-probe campaign. Its invoice may prevent deletion because of a foreign key. The main campaigns are never deleted. The campaign DELETE mapping is always checked for anonymous rejection, and its functional deletion request is included but disabled by default. |

Setting a flag to `false` skips those checks; it does **not** mean the functionality passed. Missing fixture IDs also produce a BLOCKED assertion and skip dependent requests instead of guessing IDs. The final summary logs `skippedDetails`; Postman's Runner failed-test total is authoritative.

### Existing deferred issues will be visible

Folder **13B** deliberately expects the intended owner permissions and may fail with the current code:

1. Owner reading `/api/v1/accommodations/my-campaign`: the controller still rejects the owner.
2. Owner reading another campaign's accommodation by ID: the handler lacks a campaign ownership check.

These failures are not hidden or changed to passing expectations. Other failures can reveal real system problems, integration configuration, missing local images, database issues, or quotas; inspect the response before changing an expectation.

## Successful payment — separate manual prerequisite

Hosted checkout requires completing a Moyasar test payment. The main run intentionally does not submit card data or label ADMIN activation as paid verification.

After the main run, before the registration session expires (30 minutes):

1. In the main collection Variables, open `paymentUrlA` in a browser and complete **test** checkout.
2. Import **ONS_PAID_Verification_Only.postman_collection.json**.
3. Copy these values from the main collection into the paid-verification collection:

   | Paid-verification variable | Main-collection variable |
   |---|---|
   | `baseUrl` | `baseUrl` |
   | `paymentCampaignId` | `campaignAId` |
   | `paymentId` | `paymentAId` |
   | `registrationCookie` | `registrationCookieA` |

4. Run that two-request collection once. It verifies PAID, paidAt, ACTIVE campaign, active owner, and repeated verification preserving paidAt. Confirm invoice email arrival separately. Do **not** restart the main collection to verify that invoice: a new run replaces its IDs and cookies.

## Share results

From the completed Runner result, choose **Export results** and share the resulting `postman_test_run.json`. Include failed responses or the Postman console summary if needed. Do not share an exported collection after a run without removing passwords, registration cookies and personal data from its Variables.

## Validation performed while preparing this package

JSON parsing, all script syntax, fixture file existence, unique request names/IDs, variable declarations, current endpoint coverage, and offline setup/session/gating checks are checked in `validation-results.json`. No backend requests, emails, WhatsApp messages, Google requests or payments were executed by the assistant. No application Java or properties files were edited.
