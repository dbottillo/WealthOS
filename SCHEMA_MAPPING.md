# WealthOS Schema Mapping

This document details the mapping between the historical Notion database and the WealthOS `SpendingPeriod` data model.

## Primary Entity: `SpendingPeriod`

### Meta Information
| Notion Property | Kotlin Field | Type | Description |
|---|---|---|---|
| `Name` | `name` | `String` | e.g., "Jan 2026" |
| `Period` (Start) | `startDate` | `LocalDate` | The start date of the period |
| `Period` (End) | `endDate` | `LocalDate` | The end date of the period |
| `Created time` | `createdAt` | `Instant` | Audit log |

### Income (Incomes Bucket)
| Notion Property | Kotlin Field | Type |
|---|---|---|
| `Salary` | `salary` | `Double` |
| `Other income` | `otherIncome` | `Double` |
| `Fabio contributions` | `partnerContributions` | `Double` |
| **Total** | `totalIncome` | `Double` (Computed) |

### Needs (50% Bucket)
| Notion Property | Kotlin Field | Type |
|---|---|---|
| `Mortgage` | `mortgage` | `Double` |
| `Bills` | `bills` | `Double` |
| `Groceries` | `groceries` | `Double` |
| `Transport` | `transport` | `Double` |
| `Personal care` | `personalCare` | `Double` |
| `Dentist` | `dentist` | `Double` |
| `Expenses` | `expenses` | `Double` |
| **Total** | `totalNeeds` | `Double` (Computed) |

### Wants (30% Bucket)
| Notion Property | Kotlin Field | Type |
|---|---|---|
| `Eating out` | `eatingOut` | `Double` |
| `Shopping` | `shopping` | `Double` |
| `Entertainment` | `entertainment` | `Double` |
| `Books` | `books` | `Double` |
| `Clothing` | `clothing` | `Double` |
| `Gifts` | `gifts` | `Double` |
| `Tech` | `tech` | `Double` |
| `Drinks` | `drinks` | `Double` |
| `Holidays` | `holidays` | `Double` |
| `Lego` | `lego` | `Double` |
| `Gaming` | `gaming` | `Double` |
| `Comics` | `comics` | `Double` |
| `Psychotherapy` | `psychotherapy` | `Double` |
| `Gym` | `gym` | `Double` |
| `Cycling` | `cycling` | `Double` |
| `Culture` | `culture` | `Double` |
| `Parents` | `parents` | `Double` |
| **Total** | `totalWants` | `Double` (Computed) |

### Savings (20% Bucket)
| Notion Property | Kotlin Field | Type |
|---|---|---|
| `Savings` | `savings` | `Double` |
| `Investment` | `investment` | `Double` |
| `SIPP` | `sipp` | `Double` |
| **Total** | `totalSavings` | `Double` (Computed) |

## Backend-Calculated Fields
These fields are not stored in the database but are calculated by the Ktor server before being exposed via the API:
- `balance`: `totalIncome - (totalNeeds + totalWants + totalSavings)`
- `needsPercentage`: `totalNeeds / totalIncome`
- `wantsPercentage`: `totalWants / totalIncome`
- `savingsPercentage`: `totalSavings / totalIncome`
- `status`: Logic based on `balance` (e.g., `< 0` = 🔴, `< -1000` = 🔴).
