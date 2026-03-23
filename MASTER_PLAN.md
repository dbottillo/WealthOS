# WealthOS Master Plan

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
- [x] **KMP Project Initialization:** Set up the multiplatform project structure (`common`, `composeApp`, `server`).
- [ ] **Shared Data Models:** Implement the `SpendingPeriod` model in the `common` module.
- [ ] **Backend Setup (Ktor):** Implement a basic Ktor server with database connectivity using Exposed.
- [ ] **Dockerization:** Create a `Dockerfile` and `docker-compose.yaml` for the backend and database.
- [ ] **Client Foundations:** Set up basic navigation and a shared API client.

### Phase 2: Feature - Visualize Outgoings (Web Entry & Notion Migration)
- [ ] **Backend:**
    - [ ] REST API for CRUD operations on `SpendingPeriod`.
    - [ ] **Notion Data Migration:** Develop a service to pull historical data (2013-present) from Notion.
    - [ ] **Calculation Engine:** Implement 50/30/20 formulas (Needs/Wants/Savings sums and percentages).
- [ ] **Frontend (Web):**
    - [ ] **Fast Data Entry:** Optimized keyboard-friendly interface for manual entry of period totals.
    - [ ] **Tabular View:** Spreadsheet-like view for reviewing and editing periods.
    - [ ] **Analytics Dashboard:** Annual summaries, median spending per category, and monthly trends.
- [ ] **Frontend (Android):**
    - [ ] **Read-Only Dashboard:** High-level overview of monthly spending trends and category breakdowns.

### Phase 3: Refinement & Local Deployment
- [ ] **Local Server Deployment:** Deploy Docker containers to the home server.
- [ ] **Tailscale Integration:** Configure for secure remote access.
- [ ] **UI/UX Polishing:** Refine design and animations for the initial features.
- [ ] **Testing:** Implement unit and integration tests.

### Phase 4: Feature - Emergency Budget
- [ ] Shared logic for calculating target funds based on historical `SpendingPeriod` data.
- [ ] Progress tracking UI on Web and Android.

### Phase 5: Feature - Bills & Subscriptions
- [ ] Management of recurring payments and upcoming due dates.

### Phase 6: Feature - Investments Overview
- [ ] Manual tracking of investment portfolios and performance charts.

### Phase 7: Feature - Pension Management
- [ ] Monitoring pension growth and retirement planning tools.

### Phase 8: Feature - Salary Sorter
- [ ] Rule engine for allocating income to different spending/savings buckets.

## Next Steps
1.  **Scaffold Project:** Create the KMP project structure.
2.  **Initialize Database:** Set up the PostgreSQL schema based on `SCHEMA_MAPPING.md`.
3.  **Run Health Check:** Verify the Ktor server and API client are communicating.
