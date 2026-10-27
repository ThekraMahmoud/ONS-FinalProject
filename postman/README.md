# ONS — Run once in Postman

## Import and run

1. Import `ONS_Run_Once.postman_collection.json` and `ONS_Local.postman_environment.json`.
2. Select **ONS Local — Run once** in the environment selector.
3. Fill three values: `adminPassword` (your existing seeded admin password), `testEmail` (a mailbox you control that supports plus aliases, such as Gmail), and `testPhone` (your Saudi test WhatsApp number, exactly 05xxxxxxxx). Adjust `baseUrl` if needed. Never share an exported environment containing filled credentials/session cookies.
4. Start the backend with your test database, n8n workflows, Google APIs, test Moyasar key/fee, and SMTP configured.
5. Click **Run collection**, keep **1 iteration**, preserve folder/request order, and run. Keep “stop run if an error occurs” off so authorization failures/diagnostics do not hide later results. Request failures should be investigated, not changed to expected-success codes.

Do not run against production. The normal flow creates real test records, submits emails and assignment WhatsApp messages, makes map requests, and creates test invoices through the gateway configured on your backend. Scripts do not issue hidden HTTP requests or pay invoices automatically.

## What is created automatically

- Two primary campaigns and separate captured registration sessions, plus one additional registration-ID probe campaign/invoice.
- Two campaign supervisors, a spare supervisor, two campaign employees, a spare employee, two field workers and a spare field worker. No pilgrim login accounts.
- Three kiosks, three accommodations, three pilgrims, and three manually numbered QR cards.
- Separate reports for staff acceptance, field assignment/reassignment, batch assignment, other-campaign denial, legacy paths and the reminder test.
- A notification record and completed/failed identification request fixtures.

Generated emails are plus aliases of `testEmail`, e.g. `you+ons-supervisora-<run>@gmail.com`. All staff fixture passwords start as `testPassword` (a disposable test password). Phone numbers are reused intentionally; the backend does not require them to be unique. Every ID is captured from responses; none is assumed to be 1. The first request resets generated IDs and starts a NEW test run each time. Do not rerun the whole collection to resume an old payment.

## Sessions and authentication

All saved requests disable the cookie jar. Each staff request has explicit Basic Auth, anonymous requests have explicit No Auth, and payment requests send `registrationCookieA` or `registrationCookieB` in an explicit Cookie header. This prevents a cached admin login from invalidating negative tests. No cookie-domain allowlist is needed because scripts read Set-Cookie response headers rather than the cookie jar. If your Postman version does not retain request settings on import, enable **Settings → Disable cookie jar** for these requests.

Registration remains valid for 30 minutes of inactivity. Main payment tests create/reuse an invoice and verify an UNPAID invoice stays PENDING with its campaign PLANNED. They check that missing or different-campaign sessions return 403. The profile-update test verifies a supervisor cannot activate an unpaid campaign by submitting ACTIVE. Source currently permits ADMIN to create supervisor accounts for PLANNED test campaigns; these fixtures follow that actual behavior.

## Optional phases (false by default)

| Environment flag | Effect |
|---|---|
| `runPasswordChange` | Changes only the spare employee password, then updates its environment password automatically. |
| `runTimedChecks` | Waits/polls the dedicated OPEN report for about 16 minutes, checks the reminder flag and overdue retrieval. Allow at least 70 seconds for each script if your Postman version has a script timeout setting. Requires Collection Runner, not isolated Send, for setNextRequest loops. |
| `runDisclosureDiagnostics` | Fails assertions if raw password values or field-staff report collections are serialized. User.password hiding was deferred, so this may expose a known unresolved issue. No password values are logged by scripts. |
| `runAdminEmailReport` | Sends the kiosk usage report to the administrator's actual configured email. Leave false if the seeded admin@ons.com address is not yours. |
| `runCleanup` | Deletes only disposable/unlinked fixtures. Main campaigns, payments and main reports remain for inspection. |
| `runCampaignCleanup` | Deletes a separately supplied `cleanupCampaignId`. Do not point it at an important campaign. Related payments/reports can prevent deletion through foreign-key constraints. |
| `runPaidVerification` | Run folder 15 AFTER manually completing TEST checkout at the saved `paymentUrlA`. Confirms PAID, campaign ACTIVE and stable paidAt on repeated verification. |

A one-run API collection cannot complete hosted card entry/3DS by itself, and cannot prove an email/WhatsApp arrived in your inbox. To test the invoice email: complete test checkout in a browser, enable `runPaidVerification`, and run ONLY folder 15 while the original registration session is valid. First paid verification should send the invoice email; repetition should not resend. Check your inbox and n8n execution separately. The duplicate-email assertion cannot be made from the API response alone.

To test the seven-minute reminder, enable `runTimedChecks` before the run. The scheduler may take approximately 7–8 minutes to submit, and overdue uses strictly more than 15 minutes. No database timestamps are changed by the collection. The current public heartbeat refreshes ALL kiosk timestamps; a 200 response does not prove devices are online.

## Reading results

Open each request's **Test Results** or the Runner results. Expected 401/403 responses are PASS when denied as intended. Optional SKIPPED requests and requests skipped because prerequisites failed are not passes or proof the feature works. A setup failure can skip dependent steps; fix the initial failure and rerun with a new run ID rather than guessing IDs.

All controller mappings are represented either by a functional request, an optional destructive request, or an anonymous authorization check. The `/report` aliases are checked independently for anonymous denial; their full positive workflow is exercised on `/api/v1/reports`. The public `/report/self-service` alias is tested with invalid input to prove it reaches validation without creating an extra report. Filter-managed logout is separate from controller counts. Mapping coverage is NOT the same as successful runtime validation.

Known existing limitations can surface as failures: MariaDB `FOR UPDATE OF` syntax during batch assignment; missing SMTP/n8n/Google credentials; missing User.password exclusion; broad serialization; or missing 403 handler. Permission checks using ApiException elsewhere may still return 400. The collection does not repair backend issues or prove concurrency/CSRF safety.

## Files and verification

- Collection: `ONS_Run_Once.postman_collection.json`
- Environment: `ONS_Local.postman_environment.json`
- Static coverage: `endpoint-coverage.json` (includes every controller mapping and matching request names)
- Bundle: `ONS_Postman_Run_Once.zip`

The assistant validated JSON, JavaScript syntax, template bodies and static mapping coverage locally. The collection has NOT been run against ONS, and no emails, messages, payments or database changes were triggered by the assistant.

Postman references: [cookie manager / disabling the cookie jar](https://learning.postman.com/docs/use/send-requests/response-data/cookies), [execution control and optional requests](https://learning.postman.com/latest-v-12/docs/tests-and-scripts/write-scripts/postman-sandbox-reference/pm-execution).

Static inventory: 123 methods, 15 controllers, 152 path/method mappings; 356 collection requests (20 optional).
