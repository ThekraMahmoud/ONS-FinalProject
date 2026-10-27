# ONS live test report — 28 October 2026

Application: https://d3ujyxwecja6r4.cloudfront.net

Status: public checks and all five available campaign-owner sections, plus the available admin screens, have been tested. Workflow coverage is incomplete because owner operations are blocked by integration failures, and supervisor/representative/field-staff accounts are not present. This report does not certify every endpoint or workflow.

## Restrictions respected

- No email or WhatsApp delivery tests.
- No paid checkout or payment submission.
- No changes to Java/React application code or AWS configuration during this test.
- Existing campaign and user records were not edited or deleted.
- Valid campaign registration was excluded because the service sends a notification.

## Automated live checks

28 checks passed; 0 failed. Full results: [live-public-results.json](live-public-results.json).

| Check | Result |
| --- | --- |
| Eleven direct page URLs return the React shell | Pass |
| JavaScript, CSS, and logo assets load | Pass |
| Ten protected API requests without authentication return 401 | Pass |
| Empty campaign registration rejected with 400 | Pass |
| Payment creation and verification without the registration session rejected with 403 | Pass |
| Map request with a nonexistent kiosk rejected before route-provider use | Pass |

Page shell responses only verify that routes and assets are served. They do not verify the authenticated features inside those pages.

## Live browser checks — admin

| Feature | Observed result |
| --- | --- |
| Admin login | Pass; dashboard opened with real data |
| Overview | Partially works; latest reports displays a load error |
| Campaign list | Existing active campaign loads; email column is blank |
| Reports list | Fails with a load error when there are no reports |
| Accounts | Two existing accounts load; owner role filter works; staff-details warning appears |
| Pilgrims | Empty list displays correctly |
| Accommodations | Empty list displays correctly |
| Cards | Empty list displays correctly |
| Kiosks | Empty list and create/edit/status/search/filter checks pass |
| Kiosk form validation | Latitude 91 is rejected by the browser |
| Logout | Pass; returns to login page |

## Live findings

1. **Guest kiosk setup is blocked.** The public setup screen reports that access to the device list was denied and disables continuing. The frontend requests `/api/v1/kiosks/get`, while backend security requires a staff role. Evidence: [live-kiosk-access-failure.png](live-kiosk-access-failure.png).
2. **Empty reports appear as a server failure.** Both the admin overview widget and reports screen show a load error with zero reports. The backend's `AssistanceReportService.getAllReports()` throws `ApiException("There are no reports in the system")` for an empty list; the frontend does not convert this case into an empty state. This cause is supported by source inspection; no authenticated HTTP response body was extracted from the browser. Evidence: [live-admin-reports-failure.png](live-admin-reports-failure.png).
3. **Accounts show a staff-details warning when there are no staff.** `AuthorizedStaffService.getAllStaff()` throws an exception for an empty list, and the frontend `getAllAuthorizedStaff()` does not normalize it. `AdminPeopleDirectory` then displays the warning. Existing users still load.
4. **Campaign email is blank.** The admin campaign table reads `campaign.email`; the owner email is stored under the campaign's user. This is a display mapping issue identified from the live blank field and source inspection.

## Live browser checks — campaign owner

| Feature | Observed result |
| --- | --- |
| Owner login | Pass; correct campaign and owner workspace displayed |
| Overview summary | Campaign report/pilgrim counts display; team-count warning and accommodation-chart access error |
| Pilgrim directory | Fails with “Your account does not have permission to perform this request” |
| Manual pilgrim form | Opens, but accommodation options fail and Save pilgrim remains disabled |
| Accommodation list | Fails with the same access error |
| Accommodation creation | UI reports success for an inactive synthetic record; record cannot be independently read back through this owner list |
| Team list | Correctly displays an empty list without an error |
| Representative prerequisite | Save is disabled with a clear explanation when no active supervisor exists |
| Analytics | Pass; zero counts, resolution percentage, and chart display correctly |
| Refresh while signed in | Returns to login; owner authentication state is lost |

5. **Owner pilgrim and accommodation workflows are blocked.** Live owner screens call `getPilgrims()` → `/api/v1/pilgrims/get`, which is restricted to ADMIN. Reproduced in the pilgrim directory, accommodation list, accommodation chart, and accommodation selection in the add-pilgrim form. The add-pilgrim form cannot save even with optional accommodation because its load failure disables saving. Evidence: [live-owner-pilgrims-denied.png](live-owner-pilgrims-denied.png), [live-owner-pilgrim-add-blocked.png](live-owner-pilgrim-add-blocked.png).
6. **Owner overview warns about team counts with no staff.** The dedicated team page handles the empty-staff response correctly; the overview does not. This is a separate empty-state handling inconsistency.
7. **Refreshing loses login state.** Reproduced by refreshing the signed-in owner analytics screen; the browser returned to `/login`. `auth.js` keeps `currentUser` in module memory without restoring it on page load. Evidence: [live-refresh-login-lost.png](live-refresh-login-lost.png). This check did not establish whether the backend session itself remained valid; it establishes that the frontend workspace session is not restored.

The accommodation UI also says unoccupied accommodations are unavailable to owners, but the current backend `/api/v1/accommodations/my-campaign` service accepts owners and returns all their campaign accommodations, including empty ones. Its response uses `accommodationId` and `accommodationName`. This existing endpoint should be assessed when selecting a frontend fix; no new business endpoint was added during testing.

## Additional source compatibility findings — not fully reproduced live

- The same ADMIN-only pilgrim-list request is used by supervisor screens and post-registration lookup. `/api/v1/pilgrims/me` requires a specific `pilgrimId` and is not a drop-in list replacement; a fix must first assess the existing campaign/search response contracts.
- `getPilgrimCardForPilgrim()` requests `/api/v1/pilgrim-cards/pilgrim/{pilgrimId}`, which is not defined in the current backend controller. Card lookup and registration recovery depend on it. Existing card endpoints need to be considered before selecting a fix.
- OCR depends on a separate Python service; that service was not included in the combined Java deployment. OCR completion has not been live-validated.

## Test data left in the database

One kiosk was created for this test:

- Code: `ONS-QA-20261028-01`
- Location name: `ONS deployment test only - edited`
- Coordinates: 0, 0 (synthetic test values)
- Final status: `INACTIVE`

Create, edit, transition to MAINTENANCE, restore to INACTIVE, search, and status filtering were verified. The record was left inactive, not permanently deleted. Evidence: [live-admin-kiosk-passed.png](live-admin-kiosk-passed.png).

One synthetic accommodation creation was submitted by the owner:

- Name: `ONS-QA-STAY-20261028-01`
- Address: `Synthetic deployment test - not a real address`
- City: Makkah
- Coordinates: 0, 0
- Submitted status: `INACTIVE`

The UI reported successful creation. Read-back, editing, status changes, and deletion are not verified because the owner list is blocked. Do not submit a duplicate merely because the list stays empty. Evidence: [live-owner-accommodation-created.png](live-owner-accommodation-created.png).

No staff accounts or pilgrims were created. No existing campaign/user records were changed. The browser is now at login after the refresh test.

## Remaining coverage

- Successful owner pilgrim/card operations and accommodation read/edit/status workflows after resolving the access failures.
- Supervisor, regular employee, and field-staff workflows; these accounts were not present in the available admin list.
- Real report lifecycle, assignment, and resolution where notifications can be excluded.
- Pilgrim/card/accommodation lifecycle with synthetic test data.
- QR/OCR and routing integrations where prerequisites and user restrictions permit.
- Email/WhatsApp delivery and paid checkout remain intentionally excluded.
