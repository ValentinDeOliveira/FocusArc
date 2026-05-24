# FocusArc Frontend

Angular client for FocusArc, a productivity application. 
Users organize focused work sessions through a three-level hierarchy: **Arc** (a multi-week goal) → **Chapter** (one calendar day) → **Task** (a timed work block with overtime support).

📖 **[Full documentation →](https://google.com)**

---

## Tech stack

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Framework | Angular 21                        |
| Language  | TypeScript 5.9                    |
| Styling   | Tailwind CSS 4 · Angular Material |
| Testing   | Vitest                            |
| Build     | Angular CLI 21                    |

---

## Prerequisites

- Node.js 20+
- npm 11.6.2+
- Angular CLI 21 — `npm install -g @angular/cli`
- The [backend](../back/README.md) running on `http://localhost:8080`

---

## Run locally

**1. Install dependencies**
```bash
npm install
```

**2. Set environment variables** — copy the example file and fill in the values:
```bash
cp .env.example .env
# edit .env
```

**3. Start the dev server**
```bash
npm start
```

The app is available at `http://localhost:4200` and reloads automatically on file changes.



## Build for production

```bash
npm run build
```

Output is in `dist/`. Serve it with any static file host.

---

## Environment variables

| Variable                  | Description                                                                                      |
|---------------------------|--------------------------------------------------------------------------------------------------|
| `NG_APP_API_BASE_URL`     | Backend base URL (e.g. `http://localhost:8080/api` locally, `https://api.focusarc.com` in prod). |
| `NG_APP_GOOGLE_CLIENT_ID` | Google OAuth 2.0 client ID — must match the backend's `GOOGLE_CLIENT_ID`.                        |

> Variables are injected at build time via [`@ngx-env/builder`](https://github.com/chihab/ngx-env). All frontend env vars must be prefixed with `NG_APP_`.
