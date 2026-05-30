# Design Duel Championship

A "King of the Hill" tournament-style voting platform where judges compare designs head-to-head to crown a champion.

## Tech Stack

| Layer    | Technology                           |
|----------|--------------------------------------|
| Backend  | Java 17, Spring Boot 4.0.6           |
| Database | MongoDB                              |
| Auth     | JWT (HMAC-SHA), stateless            |
| Frontend | Vanilla HTML / CSS / JS (static SPA) |
| Build    | Maven                                |

## Architecture

```mermaid
flowchart TB
    subgraph Client["Browser (Static SPA)"]
        L[Login Page]
        A[Arena Page]
        LB[Leaderboard Page]
    end

    L & A & LB --> RL

    subgraph Backend["Spring Boot - 8080"]
        direction TB
        RL[RateLimitFilter] --> JF[JwtAuthFilter]

        JF --> AC[AuthController]
        JF --> VC[VoteController]
        JF --> DC[DesignController]
        JF --> SC[SessionController]
        JF --> RC[ResultsController]

        AC --> AS[AuthService]
        VC --> VS[VoteService]
        DC --> DS[DesignService]
        SC --> SS[SessionService]
        RC --> RS[ResultsService]
    end

    AS & VS & DS & SS & RS --> DB

    subgraph DB["MongoDB - 27017"]
        COL1[(designs)]
        COL2[(judges)]
        COL3[(judge_sessions)]
        COL4[(votes)]
        COL5[(tournament_results)]
    end
```

## Tournament Flow

```mermaid
sequenceDiagram
    participant J as Judge (Browser)
    participant API as Spring Boot API
    participant DB as MongoDB

    Note over J: Login / Auto-register
    J->>API: POST /api/auth/login { username }
    API->>DB: Find or create judge
    API-->>J: { token, judgeId, username }

    Note over J: Voting Loop
    J->>API: GET /api/session/current
    API->>DB: Load session
    API-->>J: { champion, challenger, progress }

    J->>API: POST /api/votes { winnerId, loserId }
    API->>DB: Record vote, update stats
    API->>DB: Advance session (new challenger)
    API-->>J: { accepted, newChampion? }

    Note over J: Repeat until all designs seen

    Note over J: Final Pick & Results
    J->>API: GET /api/results
    API->>DB: Aggregate rankings
    API-->>J: { rankings, winner, myFinalPick }
```

## Getting Started

### Prerequisites

- Java 17+
- MongoDB
- Maven

### Run

```bash
mvn spring-boot:run
```

Opens at **http://localhost:8080**. On first startup, 20 fantasy-themed designs are seeded automatically.

### Configuration

All settings are configurable via environment variables (see `application.yml`):

| Variable                      | Default                                 | Description                      |
|-------------------------------|-----------------------------------------|----------------------------------|
| `MONGODB_URI`                 | `mongodb://localhost:27017/design-duel` | MongoDB connection string        |
| `JWT_SECRET`                  | (built-in fallback)                     | HMAC-SHA key for JWT signing     |
| `CORS_ORIGINS`                | `*`                                     | Allowed CORS origins             |
| `RATE_LIMIT_ENABLED`          | `true`                                  | Enable/disable rate limiting     |
| `RATE_LIMIT_CAPACITY`         | `30`                                    | Token bucket capacity            |
| `RATE_LIMIT_REFILL`           | `20`                                    | Tokens refilled per minute       |
| `USERNAME_VALIDATION_ENABLED` | `false`                                 | Enable username regex validation |
| `USERNAME_PATTERN`            | `^[a-z0-9._-]+$`                        | Username validation regex        |

## How It Works

- **Auto-registration**: Judges are created on first login — no admin pre-creation needed.
- **King of the Hill**: A champion design faces challengers. Win keeps the crown; loss promotes the challenger.
- **Progress**: Each judge sees every design once. When all are seen, their current champion becomes their **final pick**.
- **Leaderboard**: Rankings are computed by final pick count (descending), then win rate.
- **Anti-abuse**: Token-bucket rate limiter (30 req burst, 20/min refill) protects the API.
