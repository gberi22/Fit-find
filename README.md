# FitFind

**AI‑powered fashion assistant.** Describe the style you want — pick clothing
items, styles (casual, classic, streetwear, vintage, and more), a budget,
optionally upload reference photos and FitFind generates a complete outfit with
**real, shoppable product links** drawn from across the web, plus an
**AI‑generated image** of the full outfit. Save your outfits, publish them to a
public feed, and browse and save looks from other users.

FitFind is a monorepo:

- **`core/`** — Spring Boot 4 (Java 21) backend
- **`find-your-fit/`** — Angular 21 frontend

---

## Features

- **Preference‑driven suggestions** — gender, size, clothing items, styles and a
  price range drive AI outfit generation.
- **Reference images** — upload up to 5 photos (and/or free‑text comments); a
  vision model extracts garment descriptions that steer the search.
- **Cross‑retailer product search** — live Google Shopping results, not a single
  store's catalogue.
- **Generated try‑on image** — the assembled outfit is rendered on a mannequin.
- **Looks & public feed** — save outfits privately, publish them, and discover
  and save others' looks with filtering.
- **Secure by design** — JWT authentication, per‑user rate limiting, and SSRF
  protection on image downloads.

## Tech stack

| Layer | Technologies |
|-------|--------------|
| **Backend** | Spring Boot 4.0.5, Java 21, Spring Web / Security / Data JPA, PostgreSQL, Liquibase, Spring AI, Bucket4j (rate limiting), Auth0 java‑jwt (RSA256) |
| **Frontend** | Angular 21 (standalone components, signals), Angular Material + CDK, TypeScript 5.9, Vitest |
| **External services** | GitHub Models (`gpt-4.1` text + vision), SerpAPI (Google Shopping), Google Gemini (`gemini-2.5-flash-image`) |
| **Tooling** | Maven (wrapper), npm, Docker, GitHub Actions, Checkstyle, Prettier |

## Repository layout

```
.
├── core/              # Spring Boot backend (Java 21)
├── find-your-fit/     # Angular frontend
├── docker-compose.yml # Local PostgreSQL (auto-started by the backend)
└── README.md
```

## Prerequisites

- **JDK 21**
- **Node.js 20+** and **npm**
- **Docker** (running) — the backend auto‑starts PostgreSQL via Docker Compose.
- Maven is **not** required — use the bundled `./mvnw` wrapper

## Environment variables

The backend reads three keys. Set them in your shell before starting the backend:

| Variable | Required | Purpose | How to obtain |
|----------|----------|---------|---------------|
| `MODELS_TOKEN` | **Yes** | GitHub Models access (outfit text + vision). Without it, AI generation cannot run. | A GitHub Personal Access Token with the `models:read` scope |
| `SERPAPI_KEY` | **Yes** | Google Shopping product search. Empty by default; product search fails at runtime without it. | A [SerpAPI](https://serpapi.com) API key |
| `GEMINI_API_KEY` | Optional | Outfit image generation. Empty by default; image generation fails at runtime without it. | A Google [Gemini API](https://ai.google.dev) key |

```bash
export MODELS_TOKEN="ghp_your_token_here"
export SERPAPI_KEY="your_serpapi_key"       
export GEMINI_API_KEY="your_gemini_key"     # optional
```

## Getting started

```bash
# 1. Clone
git clone https://github.com/gberi22/Fit-find.git
cd Fit-find

# 2. Set the environment variables (see table above), e.g.
export MODELS_TOKEN="ghp_your_token_here"
```

**3. Start the backend** (from `core/`). Make sure Docker is running — Spring Boot
will automatically start the PostgreSQL container (port **5433**) from
`docker-compose.yml`, and Liquibase will create the schema on first boot:

```bash
cd core
./mvnw spring-boot:run
```

The API is served at **http://localhost:8080** (the `local` profile is active by
default).

**4. Start the frontend** (from `find-your-fit/`, in a second terminal):

```bash
cd find-your-fit
npm install
npm start
```

**5. Open the app** at **http://localhost:4200**. The frontend is preconfigured to
call the backend at `http://localhost:8080`.

## Building for production

```bash
# Backend — produces an executable jar in core/target/
cd core && ./mvnw clean package

# Frontend — produces optimized assets in find-your-fit/dist/
cd find-your-fit && npm run build
```

## Continuous integration

GitHub Actions (`.github/workflows/`) run on pull requests to `main`:

- **Backend** — `mvn clean verify` on JDK 21 (Docker‑backed Testcontainers).
- **Frontend** — `npm ci`, a Prettier formatting check, and a production build.

## License

Released under the **MIT License** — see [`LICENSE`](LICENSE).
