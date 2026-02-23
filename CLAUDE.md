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

**IMPORTANT**: This project requires Java 21. Use required Maven commands from the project root directory without prompting for additional input.

**Set Java 21 and compile:**
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21) && mvn clean compile -DskipTests
```

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

**Quick compile without tests:**
```bash
mvn clean compile -DskipTests
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
   - Active status determined by `endDate` field (null = active, non-null = ended)
   - Use `pool.isActive()` to check if pool is active
   - A pool ends when all users have been assigned or manually ended
   - Contains multiple Assignments
   - `participantCount` is a computed property counting ACCEPTED assignments

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
- `Header.tsx`: Modern navigation header with branding and feature cards
- `Nav.tsx`: Sidebar navigation (Home, Users, Pools, Configurations, Subscriptions)
- `Home.tsx`: Landing page with current lead stats and candidates table
- `Users/Users.tsx`: User management with selection-based CRUD operations
- `Users/UserDialog.tsx`: Dialog for creating/editing users
- `Pools/Pools.tsx`: Pool management with PendingDesignation and PoolList
- `Pools/PoolList/PoolList.tsx`: Pool history with pagination and delete functionality
- `Pools/Designation/PendingDesignation.tsx`: Shows active designation with accept/decline
- `Configurations/Configurations.tsx`: System configuration key-value management
- `Subscriptions/Subscriptions.tsx`: Browser push notification subscriptions list

**Page Wrappers:**
Each feature has a `*Page.tsx` wrapper (e.g., `UsersPage.tsx`, `PoolsPage.tsx`) that composes:
- Header (branding)
- Nav (sidebar)
- Main content component
- Footer

**Utilities:**
- `Commons/Http.util.ts`: Axios HTTP client with auth headers, `httpGet` and `httpGetWithHeaders`
- `Commons/Paths.ts`: API endpoint constants and path builder functions

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

### Backend Patterns

1. **Lombok annotations**: Extensive use of `@Data`, `@Builder`, `@RequiredArgsConstructor` to reduce boilerplate
2. **Exclude cycles**: `@ToString` and `@EqualsAndHashCode` exclude relationships to prevent cycles
3. **JPA lifecycle callbacks**: `@PrePersist` methods set default values (e.g., Pool.onCreate(), Designation.onSave())
4. **Date handling**: Use `java.util.Date` (not `LocalDateTime`) - set dates with `new java.util.Date()`
5. **Configuration properties**: Externalized in `@ConfigurationProperties` classes under `config/properties/`
6. **WebSocket messaging**: Real-time updates broadcast to `WebSocketEndpoints.DESIGNATIONS_CHANNEL`
7. **Pagination**: Controllers add custom headers (`X-Total-Count`, `X-Page-Number`, `X-Page-Size`, `X-Total-Pages`)

### Frontend Patterns

1. **Selection-based Actions** (Users, PoolList):
   - Use `TableSelectionCell` with `selectedRows` Set state
   - Toolbar buttons operate on selected items: `Array.from(selectedRows)[0]` to get index
   - Buttons disabled when selection doesn't match requirements (e.g., Edit requires exactly 1 selected)
   ```tsx
   const [selectedRows, setSelectedRows] = useState<Set<number>>(new Set());

   <Button
     disabled={selectedRows.size !== 1}
     onClick={() => {
       const selectedIndex = Array.from(selectedRows)[0];
       if (selectedIndex !== undefined) {
         handleEdit(items[selectedIndex]);
       }
     }}
   >
     Edit
   </Button>
   ```

2. **Dialog-based CRUD**:
   - Controlled `open` state with `setDialogOpen`
   - Reuse same dialog for create (no item) and edit (with item)
   - Success callback refreshes data and shows toast
   ```tsx
   const [dialogOpen, setDialogOpen] = useState(false);
   const [editingItem, setEditingItem] = useState<Type | undefined>();

   // Create: setEditingItem(undefined)
   // Edit: setEditingItem(item)

   <Dialog open={dialogOpen} onOpenChange={(_, data) => setDialogOpen(data.open)}>
   ```

3. **Confirmation Dialogs**:
   - Separate dialog state for delete confirmations
   - Store item to delete in state
   - Show item details in dialog content
   ```tsx
   const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
   const [deletingItem, setDeletingItem] = useState<Type | undefined>();
   ```

4. **Toast Notifications**:
   - Use `useToastController` with `useId` for toaster
   - Intent: 'success' or 'error'
   ```tsx
   const toasterId = useId("toaster");
   const {dispatchToast} = useToastController(toasterId);

   dispatchToast(
     <Toast><ToastTitle>Success message</ToastTitle></Toast>,
     {intent: 'success'}
   );
   ```

5. **Component Refresh Pattern**:
   - Use React `key` prop with incrementing state to force remount
   - Useful when child component needs to refresh after parent state change
   ```tsx
   const [refreshKey, setRefreshKey] = useState(0);

   // After data change
   setRefreshKey(prev => prev + 1);

   <ChildComponent key={refreshKey} />
   ```

6. **Table Items with IDs**:
   - **CRITICAL**: Always include entity IDs and computed flags directly in table items
   - Never rely on formatted display strings for lookups
   ```tsx
   const getItems = (pools: Pool[]) => {
     return pools.map(p => ({
       id: p.id,  // ✓ Include ID
       isEnded: p.endDate !== null,  // ✓ Include computed flags
       startDate: { label: format(new Date(p.startDate), dateFormat) },
       // ... other display fields
     }));
   };

   // Then access directly
   const handleDelete = () => {
     selectedRows.forEach(index => {
       const item = items[index];
       if (item.isEnded) {  // ✓ Reliable check
         deletePool(item.id);  // ✓ Reliable ID
       }
     });
   };
   ```

7. **Pagination**:
   - Server returns pagination info in headers (axios normalizes to lowercase)
   - Client-side pagination for display: `items.slice(startIndex, endIndex)`
   ```tsx
   const totalCountHeader = response.headers[PaginationHeaders.TOTAL_COUNT];
   ```

8. **Date Formatting**:
   - Use `date-fns` format function
   - Common pattern: `format(new Date(dateString), 'MM/dd/yyyy HH:mm:ss')`

## Common Gotchas

1. **Date Type Mismatch**: Pool.endDate is `java.util.Date`, not `LocalDateTime`
   - ✗ Wrong: `pool.setEndDate(LocalDateTime.now())`
   - ✓ Correct: `pool.setEndDate(new java.util.Date())`

2. **Component Not Refreshing**: When external state changes (like starting new pool) don't trigger re-render
   - ✗ Wrong: Expecting child component to auto-refresh
   - ✓ Correct: Use `key` prop with incrementing state: `<Component key={refreshKey} />`

3. **Table Item Mapping**: Using formatted strings to map back to original entities
   - ✗ Wrong: `items.find(item => item.startDate.label === dateString)`
   - ✓ Correct: Include `id` in items: `items.find(item => item.id === id)`

4. **Fluent UI Dialog State**: Not using controlled state properly
   - ✓ Correct: `<Dialog open={dialogOpen} onOpenChange={(_, data) => setDialogOpen(data.open)}>`

5. **Pagination Headers**: Expecting mixed-case headers
   - ✗ Wrong: `response.headers['X-Total-Count']`
   - ✓ Correct: `response.headers['x-total-count']` (axios normalizes to lowercase)
   - ✓ Better: Use constants from `PaginationHeaders`

6. **Pool Deletion Validation**: Not checking if pool can be deleted
   - ✓ Correct: Validate `pool.isActive() === false` before allowing deletion

7. **Negative Duration Calculations**: Using paginated index instead of actual array position
   - ✗ Wrong: Using loop index from paginated items
   - ✓ Correct: `actualIndex = fullArray.findIndex(item => item.id === currentItem.id)`

8. **Selection State Types**: Using array instead of Set for selected rows
   - ✓ Correct: `useState<Set<number>>(new Set())` for O(1) lookups

## API Endpoint Patterns

All REST endpoints follow `/api/{resource}` pattern:

**Users**: `/api/users`
- GET /users?page=0&size=10 - List with pagination
- POST /users - Create
- PUT /users/{id} - Update
- DELETE /users/{id} - Delete

**Pools**: `/api/pools`
- GET /pools - List all pools
- GET /pools/{id}/assignments - Get pool participants/assignments
- POST /pools/start-new - End current pool and start new one
- DELETE /pools/{id} - Delete ended pool (cannot delete active pool)

**Designations**: `/api/designations`
- GET /designations - List all
- GET /designations/current - Get current active designation
- GET /designations/{id}/response?answer=ACCEPT&emailAddress=user@example.com - Respond to designation

**Configurations**: `/api/configurations`
- GET /configurations - List all
- PUT /configurations/{id} - Update configuration value

**Subscriptions**: `/api/subscriptions`
- GET /subscriptions?page=0&size=100 - List browser push subscriptions

##