# WealthOS

WealthOS is a personal wealth management tool designed for the **Full Kotlin Stack**. It utilizes a **Period-based Ledger** model, matching a 13-year historical financial record previously maintained in Notion.

## Core Philosophy
- **Period-based Tracking:** Data is organized into `SpendingPeriod` entries (matching custom cycles like the 15th of one month to the 14th of the next).
- **50/30/20 Budgeting:** Automatic categorization of all spending into Needs, Wants, and Savings buckets.
- **Backend-Driven Analysis:** A central Ktor server performs all calculations and data persistence, ensuring a single source of truth for all platforms (Web and Android).

## Technical Stack
- **Shared Logic (KMP):** Business logic, shared data models, and API clients.
- **Backend:** Kotlin with **Ktor** (REST API) and **Exposed** (ORM).
- **Database:** **PostgreSQL**.
- **Frontend:** **Compose Multiplatform** for Web (Wasm) and Android.
- **Infrastructure:** **Docker** (Production) / Native Homebrew (Local Development).

## Local Development Setup

### 1. Prerequisites
- **Java 21 or 25** (Project targets JVM 21, but works on newer JDKs).
- **Homebrew** (for native PostgreSQL on macOS).

### 2. Database Setup
Install and start PostgreSQL natively:
```bash
brew install postgresql@17
brew services start postgresql@17
createdb wealthos
```

### 3. Running the Project
Local development requires two terminal windows.

#### Terminal 1: Backend
The backend needs a few environment variables for Notion synchronization. Create a local `.env` file (git-ignored) or pass them directly:
```bash
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/wealthos \
JDBC_DATABASE_USER=$(whoami) \
JDBC_DATABASE_PASSWORD="" \
NOTION_API_KEY=your_key_here \
./gradlew :server:run
```

#### Terminal 2: Web Frontend (Browser)
Run the dev server with hot-reload enabled:
```bash
./gradlew -t :composeApp:wasmJsBrowserDevelopmentRun
```
The app will open automatically at **http://localhost:8081/**.

## Testing
The project includes unit tests for shared logic and integration tests for the backend.

### 1. Shared Logic Tests (Calculation Engine)
To run unit tests for financial calculations in the `common` module:
```bash
./gradlew :common:jvmTest
```

### 2. Backend Integration Tests (API)
To run integration tests for the Ktor REST API. These tests use an **in-memory H2 database** and do not require your local PostgreSQL to be running:
```bash
./gradlew :server:test
```

### 3. Run All Tests
```bash
./gradlew test
```

## Notion Sync
Once the app is running, click the **"Sync Notion"** button in the Overview tab to import your historical data into the local PostgreSQL instance.


## Project Structure
- `/common`: Shared KMP module (Models, Repository, API Client).
- `/server`: Ktor backend and Notion migration service.
- `/composeApp`: Shared UI and platform-specific entry points (Wasm, Android, JVM).
- `/gradle`: Version catalog and build configuration.
