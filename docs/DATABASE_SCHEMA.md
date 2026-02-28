# Database Schema

## Database Type
**MongoDB** (NoSQL Document Store) running independently via clustered deployments (e.g., MongoDB Atlas or managed AWS instance).
**Redis** is also used for key-value caching and potentially transient sessions.

## Schema Overview
Because the architecture relies on Domain-Driven Design and spring-data-mongodb, collections map closely to physical entity classes. There are fewer relational joins and more embedded documents.
Collections are loosely coupled using standard ObjectId references across domain contexts.

## Collections Description

### `users`
- Stores identity and lifecycle information logic from `user` and `auth` packages.
- **Fields:** `_id`, `email`, `passwordHash`, `roles`, `oAuthProviders`, `profile_picture`, `reputationScore`.
- **Relationships:** Referenced frequently across other domains by string UID.

### `posts` / `announcements` (Community Domain)
- Stores community job posts or general posts.
- **Fields:** `_id`, `authorId`, `content`, `linkPreview`, `images`, `likesCount`, `createdAt`.
- **Indexes:** Frequently indexed on `authorId` and text search on `content`.

### `jobs` (Com/Plat Job Portal Domain)
- Standardized job listings.
- **Fields:** `_id`, `companyId`, `title`, `description`, `skills_required`, `status`.
- **References:** Reference to `companyId` (user/employer id).

### `courses` (Com/Plat Courses Domain)
- Represents learning modules and playlists.
- **Fields:** `_id`, `title`, `modules` (embedded array of videos/materials), `instructorId`, `price`.

### `messages` (Messaging Domain)
- Stores inter-user conversations or general announcements.
- **Fields:** `_id`, `senderId`, `receiverId`/`groupId`, `content`, `readStatus`, `timestamp`.
- **Note:** Real-time data mapped from Websockets before persisting.

### `audit_logs`
- Used for tracking user activities and compliance.
- **Fields:** `_id`, `actorId`, `actionType`, `resourceType`, `resourceId`, `timestamp`, `ipAddress`.
- **Lifecycle:** Often subject to TTL (Time-To-Live) index automatically defined by `audit.expire-after-days` configuration property.

### `transactions` (Payment Domain)
- Stores payment history mapping to Stripe events.
- **Fields:** `_id`, `userId`, `stripeCustomerId`, `amount`, `status`, `webhookEventId`.

## Entity Relationship Example (Document References)

```mermaid
erDiagram
    US [[USER]] {
        ObjectId _id
        String email
        String[] roles
    }
    JB [[JOB_POST]] {
        ObjectId _id
        ObjectId companyId
        String title
    }
    PT [[POST]] {
        ObjectId _id
        ObjectId authorId
        String content
    }
    AD [[AUDIT_LOG]] {
        ObjectId _id
        ObjectId actorId
        String action
    }

    US ||--o{ JB : "creates"
    US ||--o{ PT : "authors"
    US ||--o{ AD : "triggers"
```

## Migration Strategy
Unlike RDBMS, MongoDB does not strictly require schema migrations in the form of Flyway/Liquibase unless data mapping code changes.
Data model updates are handled inherently via Spring Data POJOs mapped as `@Document` and `@Field` with tolerance for missing schema structure in older documents. Some logic acts as custom upgrade scripts when fields fail validation.

## Revision Summary
- Updated to reflect NoSQL structure of the application.
- Enumerated active core collections based on system's domain bounded contexts.
