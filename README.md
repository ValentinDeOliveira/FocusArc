![CI](https://github.com/ValentinDeOliveira/FocusArc/actions/workflows/ci.yml/badge.svg)

# FocusArc

Productivity application. 
Users organize focused work sessions through a three-level hierarchy: **Arc** (a multi-week goal) → **Chapter** (one calendar day) → **Task** (a timed work block). When a task's estimated time runs out, the timer enters **overtime mode** automatically — no interruption, no required input.

📖 **[Full documentation →](https://google.com)**

---

## Structure

| Folder              | Description                                         |
|---------------------|-----------------------------------------------------|
| [`/back`](./back)   | REST API — Java 17 · Spring Boot 4 · MongoDB        |
| [`/front`](./front) | Web client — Angular 21 · TypeScript · Tailwind CSS |

See each folder's README for setup and run instructions.

---

## Tech stack

| Layer     | Technology                                                |
|-----------|-----------------------------------------------------------|
| Backend   | Java 17 · Spring Boot 4.0.2 · MongoDB 6.0                 |
| Frontend  | Angular 21 · TypeScript · Tailwind CSS · Angular Material |
| Testing   | JUnit 5 · Testcontainers · Vitest                         |
| DevOps    | Docker · GitHub Actions                                   |