# Security Review

## Input Validation Strategy
The Talentboozt server architecture primarily uses `Jakarta Validation API` across Request DTOs. Invalid input is rejected before controller logic executes, via `@Valid` annotations and `MethodArgumentNotValidException` global handlers, preventing injection at the source.

- File uploads are validated directly for file size constraints natively through `spring.servlet.multipart.max-file-size=20MB`.
- Google ReCAPTCHA secret validation integrates at the UI boundary, specifically on unauthenticated endpoints like user registration, mapping values against the backend.

## Auth Protection Coverage
Security rules are built contextually mapped onto the Spring Security `FilterChain`.
1. **Public APIs:** Configured loosely to accept any traffic (e.g. static assets, public profiles). Wait headers are dropped conditionally.
2. **Private Domain Rules:** Validates `Bearer` JWT claims globally via intercepting customized standard Spring Request headers.
3. **Admin Actions:** Handled independently through specialized endpoints validating implicit roles associated via tokens extracted out of db queries (e.g., verifying `roles.contains('ADMIN')`).

## Common Vulnerability Exposure Analysis
- **XSS (Cross-Site Scripting):** Sanitized primarily at the frontend Framework level, but strictly encoded JSON responses limit execution environments natively within typical Single Page Apps. JSON properties use default serialization encoding. User-generated HTML (`jsoup` dependency is included) is used precisely for sanitizing or safely handling scraped rich-text editor data.
- **CSRF (Cross-Site Request Forgery):** Because the app relies entirely on Stateless REST endpoints with `JWTs` injected inside the Authorization Header—not browser-managed domain cookies—CSRF is inherently mitigated, and its protection is commonly disabled in standard configurations (`csrf.disable()`).

## Secrets Handling
All operational secrets (database credentials, API keys) are strictly segregated from the source code via environment variable parameterization (`${SECRET:Default}`) using `.env` injections dynamically built into CI pipelines or runtime environments (Docker/AWS Secrets Manager).
No plaintext passwords exist. Spring Boot manages BCrypt hashing exclusively.

## Endpoint Resilience Security
To mitigate Automated Brute-Force and DDoS application-layer attacks:
- `resilience4j` rate limiting is applied to heavy POST/Search endpoints preventing systemic database exhaustion.
- The use of `spring.mvc.async.request-timeout=30000` combined with Tomcat threads limits blocks slow-loris attacks.

## Data Protection Considerations
MongoDB is inherently secured using role-based authentication credentials at the deployment level.
Additionally, in transit, communication between the load balancer, ingress controllers, and clients is secured via standard AWS ACM Certificates (TLS 1.2+).

## Revision Summary
- Created base Security Documentation outlining current Spring configurations.
- Integrated resilience metrics documentation into threat models.
- Described usage of `jsoup` for HTML sanitization and `.env` parameterizations.
