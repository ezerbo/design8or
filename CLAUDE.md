# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Design8or (Designator) is a Round Robin task assignment system that automatically designates leads from a pool of candidates. The application uses a scheduled rotation to automatically select candidates who haven't participated in the current pool, sends designation emails, and allows candidates to accept or decline assignments.

## Tech Stack

**Backend:**
- Spring Boot 3.4.1 with Java 21
- H2 in-memory database with Spring Data JPA
- Quartz scheduler for rotation jobs
- WebSocket for real-time notifications
- JavaMail for email notifications
- Web Push API for browser notifications
- Spring Security with basic auth (user: design8tor, password: designator)
- SpringDoc OpenAPI for API documentation

**Frontend:**
- React 18 with TypeScript
- Fluent UI component library
- React Router for navigation
- Axios for HTTP requests

## Development Commands

### Backend

**Build and run the application:**
```bash
mvn clean install
mvn spring-boot:run
```

**Run tests:**
```bash
mvn test
```

**Run specific test class:**
```bash
mvn test -Dtest=DesignationServiceTest
```

**Access the API documentation:**
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

### Frontend

**Install dependencies:**
```bash
cd ui
npm install
```

**Run development server:**
```bash
cd ui
npm start
```
Frontend runs on http://localhost:3000 (proxies API calls to backend on port 8080)

**Run tests:**
```bash
cd ui
npm test
```

**Build for production:**
```bash
cd ui
npm run build
```

### Docker

**Build the backend jar first:**
```bash
mvn clean package
```

**Build and run with Docker:**
```bash
docker build -t design8or .
docker run -p 8080:8080 design8or
```

## Architecture

### Core Domain Model

The application revolves around four main entities:

1. **User** (`model/User.java`): Candidates eligible for designation
   - Has many Assignments and Designations
   - Validated email address required

2. **Pool** (`model/Pool.java`): Represents a rotation cycle
   - Status: STARTED or ENDED
   - A pool ends when all users have been assigned
   - Contains multiple Assignments

3. **Designation** (`model/Designation.java`): A request to a user to become the lead
   - Status: PENDING, ACCEPTED, DECLINED, EXPIRED, REASSIGNED
   - Tracks designation date, user response date, and reassignment date
   - Only one active designation can exist at a time

4. **Assignment** (`model/Assignment.java`): Confirmed assignment to a user
   - Links a User to a Pool
   - Created when a user accepts a designation

### Key Services

**RotationService** (`service/rotation/RotationService.java`):
- Schedules the rotation job using Quartz
- Cron expression configurable in `application.yml` (default: daily at 10 AM)
- Reads from `app.rotation.cron-expression` property

**DesignationService** (`service/DesignationService.java`):
- Core business logic for designating users
- Handles designation acceptance/decline
- Manages reassignment when users decline
- Broadcasts designation updates via WebSocket
- Sends email notifications to designated users

**PoolService** (`service/PoolService.java`):
- Manages pool lifecycle
- Determines eligible users (those without assignments in current pool)
- Closes pool when all users have been assigned

**StaleReqService** (`service/rotation/StaleReqService.java`):
- Monitors pending designations
- Expires designations that remain unanswered beyond configured time
- Configurable via `app.rotation.stale-req-check-time-in-mins`

**EmailService & PushNotificationService** (`service/communication/`):
- Send email and browser push notifications
- Email configuration in `app.designation-email` section
- Push notification keys in `app.browser-push-notification-keys`

### Communication Flow

1. **Scheduled Rotation**: RotationJob runs based on cron expression
2. **User Selection**: PoolService selects eligible user from current pool
3. **Designation Creation**: DesignationService creates and saves designation
4. **Notification**: Email sent + WebSocket broadcast + Browser push notification
5. **User Response**: User accepts/declines via REST API
6. **Assignment**: If accepted, Assignment created and linked to Pool
7. **Reassignment**: If declined, broadcast to other eligible users

### Frontend Structure

**Main Components:**
- `Design8or.tsx`: Root component with routing
- `Header.tsx`: Navigation header
- `Home.tsx`: Landing page
- `Users/Users.tsx`: User management (CRUD operations)
- `Pools/Pools.tsx`: Pool listing and management
- `Pools/PoolList/PoolList.tsx`: Displays pool history
- `Pools/Designation/PendingDesignation.tsx`: Shows active designation

**Utilities:**
- `Commons/Http.util.ts`: Axios HTTP client with auth headers
- `Commons/Paths.ts`: API endpoint constants

### Configuration

Main configuration in `src/main/resources/application.yml`:

- **Database**: H2 in-memory with MODE=MYSQL
- **JPA**: Using create-drop strategy (recreates schema on startup)
- **Data initialization**: `src/main/resources/data/data.sql`
- **Email server**: localhost:25 (use local SMTP server for testing)
- **CORS**: Allows http://localhost:4200
- **Rotation cron**: `app.rotation.cron-expression`
- **Stale request timeout**: `app.rotation.stale-req-check-time-in-mins`

### REST API Structure

Controllers follow standard REST conventions:
- `UserController`: /api/users
- `PoolController`: /api/pools
- `DesignationController`: /api/designations
- `PushNotificationController`: /api/push-notification

All responses use ResponseEntity with appropriate HTTP status codes.
Pagination supported via `PaginationParams` and `PaginationHeaders`.

## Testing

**Backend tests** (`src/test/java/com/ss/design8or/`):
- Service layer tests using JUnit and Mockito
- Test classes: AssignmentServiceTest, UserServiceTest, DesignationServiceTest, PushNotificationServiceTest

**Frontend tests** (`ui/src/**/*.test.tsx`):
- Component tests using React Testing Library
- Test files co-located with components

## Database

Uses H2 in-memory database that resets on each startup (ddl-auto: create-drop).
Initial data loaded from `src/main/resources/data/data.sql`.
Access H2 console at http://localhost:8080/h2-console with:
- JDBC URL: jdbc:h2:mem:design8or_db
- Username: sa
- Password: (blank)

## Important Patterns

1. **Lombok annotations**: Extensive use of `@Data`, `@Builder`, `@RequiredArgsConstructor` to reduce boilerplate
2. **Exclude cycles**: `@ToString` and `@EqualsAndHashCode` exclude relationships to prevent cycles
3. **JPA lifecycle callbacks**: `@PrePersist` methods set default values (e.g., Pool.onCreate(), Designation.onSave())
4. **Composite keys**: Assignment uses `AssignmentId` as an embedded composite key (user_id + pool_id)
5. **Configuration properties**: Externalized in `@ConfigurationProperties` classes under `config/properties/`
6. **WebSocket messaging**: Real-time updates broadcast to `WebSocketEndpoints.DESIGNATIONS_CHANNEL`