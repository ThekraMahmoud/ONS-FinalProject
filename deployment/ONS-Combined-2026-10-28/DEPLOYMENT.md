# ONS: React and Spring Boot on the existing Beanstalk server

## What is included

The Java backend is copied from `/Users/faisal/IdeaProjects/ONS`. React is from
`https://github.com/ThekraMahmoud/ONS-Frontend`, revision `54c856e`.
The linked GitHub repository contains the frontend, so this package combines it
with the existing local backend. The original backend checkout was not edited.

React production files are inside `src/main/resources/static`. Both pages and API
requests are served by the same Spring Boot process. No Node server is required
on Beanstalk. Relative API paths and cookies continue to use the same origin.

## Deployment changes

- Added GET-only public access to the actual React pages and bundled assets.
- Added `FrontendController` so refreshing a React page, including `/payment`,
  loads `index.html`. API paths never use this fallback.
- All existing API matchers and authority rules remain in their original order.
- Added `X-Requested-With: XMLHttpRequest` to the frontend's API client so failed
  authentication stays in the app rather than opening the browser's Basic login.
- Declared the frontend's existing imported `@zxing/library` package directly,
  using version 0.23.0 already present in its dependency lockfile.
- Replaced local database credentials in the build copy with environment
  placeholders. Beanstalk's existing environment properties supply their values.
- The bundled Moyasar publishable key is the TEST publishable key from the
  frontend's `.env.example`. No secret Moyasar key is included in frontend assets.
- Existing admin initialization and payment business methods are unchanged.

## Upload this version

1. Open Elastic Beanstalk, environment `ons-backend-env`.
2. Choose **Upload and deploy**.
3. Select `ONS-combined-2026-10-28.jar` from this delivery folder.
4. Use the version label `ons-full-2026-10-28-01`.
5. Deploy and wait until environment events confirm completion.
6. Open `https://d3ujyxwecja6r4.cloudfront.net/`.
7. Test the `/login` page, admin sign-in, and refreshing `/dashboard`.
8. In Postman with no Authorization or cookies, confirm
   `GET /api/v1/auth/get/all` still returns 401. With current admin Basic
   credentials it should return 200.

Keep all existing Beanstalk environment properties, including `SERVER_PORT=5000`,
the corrected RDS connection URL and password, mail, Maps, Moyasar, and n8n values.
For browser access exclusively through CloudFront HTTPS, set the Beanstalk
environment property `SERVER_SERVLET_SESSION_COOKIE_SECURE=true` to mark the
registration/payment session cookie Secure. Local HTTP development would need
this setting omitted or false.

Keep the CloudFront origin, HTTP port 80, all seven allowed HTTP methods,
`CachingDisabled`, and `AllViewer`. The public CloudFront address stays the same.
CloudFront-to-Beanstalk traffic currently uses HTTP; this package does not change
that connection to TLS. The existing paid/free-account billing settings remain
unchanged.

The existing backend-only version remains in Beanstalk's Application versions
and can be redeployed if a rollback is needed.

## Validation completed locally

- React production build succeeded.
- 22 existing frontend unit/contract tests passed, using mocked API responses.
- Seven Java MVC/security test methods passed, covering all 11 React page
  routes, query-bearing payment callbacks, the index and its referenced assets,
  Basic authentication, anonymous API rejection, role rejection, and missing
  resources/API routes.
- Java compiled and the executable JAR was packaged successfully.
- Checked the JAR contains React's index, scripts, styles, and page controller.

These checks do not exercise the live RDS database, payments, n8n, mail, Google
Maps, or Python. The full database application-context test was not run. Those
services retain their existing deployment requirements and need live testing.

## Existing frontend/backend compatibility issues

The hosting changes do not add backend business endpoints or change permissions.
The following existing mismatches were identified while checking the linked
frontend against the local backend:

1. `getPilgrimCardForPilgrim()` requests
   `GET /api/v1/pilgrim-cards/pilgrim/{pilgrimId}`. The current Java controller has
   `/get` and `/me?cardId=...`, but no matching lookup by pilgrim. The corresponding
   frontend card lookup and combined pilgrim/card registration cannot complete
   against this backend until that API contract is reconciled.
2. `getPilgrims()` requests `/api/v1/pilgrims/get`, which is ADMIN-only in the
   current security configuration. Campaign staff/owner flows that use that
   function need to use their existing scoped `/api/v1/pilgrims/me` endpoint or
   otherwise reconcile the API contract, without opening the admin list.
3. Guest kiosk setup requests `/api/v1/kiosks/get`, which currently requires a
   staff role. Serving the kiosk page publicly does not grant anonymous access
   to that list or to protected pilgrim data.
4. Python OCR still needs a running Python service; bundling React with Java
   does not deploy Python.

The frontend repository's own integration notes may describe a different backend
revision. They are preserved as project reference material, not as verified
descriptions of this backend.

## Rebuild the delivered source

Extract `ONS-combined-source.zip`. Install Java 21, Node.js 24 or newer, and pnpm
11 (the frontend dependencies include a package requiring Node.js 24).
From the extracted root run:

```bash
bash build-combined.sh
```

This installs locked frontend dependencies, runs the frontend checks, builds its
production assets, copies them into Spring Boot, runs the routing/security tests,
and packages `target/ONS-0.0.1-SNAPSHOT.jar`. The database context test is excluded
from this deployment check because it needs actual runtime services.
