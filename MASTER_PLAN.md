# WealthOS Master Plan (Expanded)

WealthOS is a personal wealth management tool designed to help users visualize and manage their finances based on a **Period-based Ledger** model. It uses a **Full Kotlin Stack** with **Kotlin Multiplatform (KMP)** to share logic across all platforms.

## Core Philosophy
- **Period-based Tracking:** Data is organized into `SpendingPeriod` entries (e.g., Dec 15 to Jan 14), matching the user's existing 13-year historical record in Notion.
- **50/30/20 Budgeting:** Automatic categorization and analysis into Needs, Wants, and Savings buckets.
- **Backend-Driven Logic:** All financial calculations (sums, percentages, status) are performed on the backend and exposed via the API to ensure a single source of truth.

## Technical Stack
- **Shared Logic (KMP):** Business logic, data models, and validation shared between all platforms.
- **Android App:** Jetpack Compose (Kotlin) - **Primary use: Monitoring and visualization.**
- **Web Frontend:** Compose Multiplatform for Web (Kotlin/Wasm or Kotlin/JS) - **Primary use: Data entry and deep analysis.**
- **Backend:** Kotlin with **Ktor** for the REST API.
- **Database:** **Exposed** (Kotlin SQL library) with **PostgreSQL**.
- **Deployment:** **Docker** on a local server.
- **Networking:** **Tailscale** for secure remote access.
- **Migration:** **Notion API** for importing historical data (2013-present).

## Implementation Phases

### Phase 0: Research & Schema Mapping (COMPLETED)
- [x] **Notion Schema Analysis:** Queried the Notion API to inspect the current spending database structure.
- [x] **Data Model Design:** Defined the `SpendingPeriod` model to match the Notion ledger style.
- [x] **Database Schema Design:** Mapped 30+ financial categories to a relational structure.

### Phase 1: Project Setup & Foundation
- [x] **KMP Project Initialization:** Set up the multiplatform project structure (`common`, `composeApp`, `server`). Configured Gradle version catalogs and applied required Kotlin/Compose plugins.
- [x] **Shared Data Models:** Implement the `SpendingPeriod` model in the `common` module. Annotated with `@Serializable` and included backend-calculated fields as properties.
- [x] **Backend Setup (Ktor):** Implement a basic Ktor server using Netty, configured Exposed ORM with PostgreSQL, setup a `/health` endpoint, and enforce environmental variable-based configuration.
- [x] **Dockerization:** Create a multi-stage `Dockerfile` and `docker-compose.yaml` to orchestrate the Ktor application and PostgreSQL database with persistent volumes.
- [x] **Client Foundations:** 
    - Implement a shared HTTP client using `Ktor Client` in the `common` module for API consumption.
    - Set up a Dependency Injection framework (e.g., Koin) for the KMP modules.
    - Establish a cross-platform architectural pattern using standard **ViewModels** with **StateFlow** for state management.
    - Configure Multiplatform Navigation (e.g., Jetbrains Navigation Compose or Voyager).

### Phase 2: Feature - Visualize Outgoings (Web Entry & Notion Migration)
- [ ] **Backend (API & Logic):**
    - [x] **REST API endpoints:** Implement `GET /api/periods`, `POST /api/periods`, `PUT /api/periods/{id}`, and `DELETE /api/periods/{id}` using Ktor Routing.
    - [ ] **DTO Mapping:** Create dedicated API response DTOs that serialize both database entities and calculated fields (e.g., balance, percentages).
    - [ ] **Calculation Engine:** Build the logic mapping specific spending categories to 50/30/20 buckets.
    - [ ] **Notion Data Migration:** Develop an offline or admin-triggered service to interact with the Notion API, map historical data into `SpendingPeriod` entities, and seed the PostgreSQL database.
- [ ] **Frontend (Shared/Web):**
    - [ ] **State Management:** Implement `PeriodRepository` and `PeriodViewModel` using Flow to fetch and cache data.
    - [ ] **Tabular View:** Build a Compose Material 3 spreadsheet-like component for viewing historical records.
    - [ ] **Fast Data Entry UI:** Design an optimized, keyboard-navigable form for manually adding/editing a `SpendingPeriod`.
    - [ ] **Analytics Dashboard:** Integrate or build basic charting components (using Compose Canvas) for annual summaries and category trends.
- [ ] **Frontend (Android):**
    - [ ] **App Entry Point:** Configure Android `MainActivity` and tie it into the shared compose UI.
    - [ ] **Mobile-Optimized Dashboard:** Create a responsive, read-only variant of the dashboard tailored for mobile screens, highlighting balance and bucket statuses.

### Phase 3: Refinement & Local Deployment
- [ ] **Database Migrations:** Replace `SchemaUtils.create` with a robust migration tool like **Flyway** or **Liquibase** for production readiness.
- [ ] **Testing:** 
    - Implement `Ktor Server` Application tests for the API endpoints.
    - Write unit tests for the 50/30/20 Calculation Engine and DTO mappings in `common`.
- [ ] **Local Server Deployment:** Refine the Docker setup, potentially introducing an Nginx/Caddy reverse proxy, and deploy to the home server.
- [ ] **Tailscale Integration:** Expose the internal Docker network securely using a Tailscale subnet router or direct container integration.
- [ ] **CI/CD:** Setup basic GitHub Actions workflows to build the KMP project and run tests on push.

### Phase 4: Feature - Emergency Budget
- [ ] **Shared Logic:** Introduce a calculation module in `common` that analyzes 3-6 months of historical `totalNeeds` to determine target emergency fund requirements.
- [ ] **Backend:** Expose `GET /api/emergency-fund` to deliver progress metrics.
- [ ] **UI:** Build a gauge/progress tracker component on Web and Android to visualize current savings against the dynamically calculated target.

### Phase 5: Feature - Bills & Subscriptions
- [ ] **Data Model:** Add `Subscription` and `RecurringBill` entities.
- [ ] **Backend:** Implement scheduling logic or date-checks to flag upcoming payments within the current `SpendingPeriod`.
- [ ] **UI:** Create a calendar or timeline view showing due dates and estimated remaining discretionary income.

### Phase 6: Feature - Investments Overview
- [ ] **Data Model:** Create schemas for portfolios, asset allocations, and historical performance snapshots.
- [ ] **Backend:** Endpoints to manually record end-of-month portfolio values.
- [ ] **UI:** Line charts mapping total net worth growth over time across both Web and Android.

### Phase 7: Feature - Pension Management
- [ ] **Data Model:** Add `PensionContribution` and `RetirementGoal` entities.
- [ ] **Calculations:** Shared projections based on current contributions, expected growth rates, and retirement age.
- [ ] **UI:** Retirement scenario sliders and calculators.

### Phase 8: Feature - Salary Sorter
- [ ] **Rule Engine:** Define configurable rules on the backend (e.g., "Send X% to Savings, Y% to specific Monzo pot").
- [ ] **UI:** Interactive flow to review and execute (or just copy instructions for) payday allocations.

## Next Steps
1.  **Client Foundations:** Set up Ktor Client, Dependency Injection (Koin), and a basic UI architectural pattern (ViewModel + StateFlow) in the shared module.
2.  **API Construction:** Implement the CRUD endpoints for `SpendingPeriod` in the Ktor server.
3.  **UI Scaffold:** Create the first screen on Android/Web showing a list of periods retrieved from the local backend.
