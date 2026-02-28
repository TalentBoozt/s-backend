# Configuration Guide

## Loading Strategy
The application uses standard `application.properties` combined with `.env` variable overrides heavily relying on `{...:DefaultValue}` fallbacks to ease local development without explicit configurations.
Spring Boot loads properties from the root application context natively parsing the `.env` thanks to the `me.paulschwarz:spring-dotenv` dependency.

## Common Environment Variables

### Core Services
- `DATABASE_URI`: Your MongoDB database connect string. Default: `mongodb://localhost:27017`
- `DATABASE`: Target mongo database name. Default: `talentboozt`
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`: For caching initialization.
- `SERVER_PORT`: Tomact binding port. Default `3269`
- `ENV_CONNECTION`: Global application context string defining backend targeting (dev/prod).

### Internal Integrations
- `JWT_SECRET`: For signing user sessions tokens. A long cryptographically strong hash.
- `RECAPTCHA_SECRET`: Google reCaptcha challenge validations.

### Third Party Authentication (OAuth2)
- `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET`: OAuth 2.0 Client credentials.
- `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET`: App Client Credentials via GitHub.
- `FACEBOOK_CLIENT_ID` / `FACEBOOK_CLIENT_SECRET`: GraphAPI App bindings.
- `LINKEDIN_CLIENT_ID` / `LINKEDIN_CLIENT_SECRET`: LinkedIn OAuth 2 bindings.

### Transactional Apis
- `STRIPE_SECRET` & `STRIPE_TEST_SECRET`: Keys corresponding to live and sandbox billing processing environments.
- `STRIPE_WEBHOOK_SECRET` & `STRIPE_TEST_WEBHOOK_SECRET`: Secure cryptographic validation for webhook event signatures ensuring events originate from Stripe explicitly.

### SMTP Relays
- `EMAIL_USERNAME` / `EMAIL_PASSWORD`: Mail relay configuration credentials mapped currently through Zoho SMTP.

## Feature Flags and Operational Configs
- `management.endpoint.health.mongo.enabled=true`: Built-in readiness probes mapping NoSQL state for AWS health checks.
- `audit.batch-size=100` / `audit.flush-interval-s=5`: Feature sizing buffers for audit log processing. Limits db writes.
- `management.metrics.export.prometheus.enabled=true`: Scrape endpoints configuration for metrics ingestor sidecars.
- `resilience4j.ratelimiter.instances.*`: Granular application limits for critical controller functions. Defines maximum allowable concurrency bounds.

## Revision Summary
- Created unified configuration repository parameters directly derived from `.env` interpolations in `application.properties`.
- Documented metrics, actuator, and internal auditing variables mappings.
