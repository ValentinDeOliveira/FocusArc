---
sidebar_position: 5
title: Backlog
---

# Backlog

## Security

- [X] Move JWT secret to env var (`application.properties` — hardcoded)
- [X] Move MongoDB credentials to env var (`application.yaml` — hardcoded admin/admin123)
- [X] Externalize CORS allowed origins (`SecurityConfig.java` — hardcoded `http://localhost:4200`)

## Domain / Business Logic
- [ ] Add cross-field validation on `TaskUpdateDto` (`completedMinutes < estimatedMinutes`)
- [ ] Change `User.lastLogin` from `LocalDateTime` to `Instant`
- [ ] Add `timezone` field in `User`
- [ ] Fix `timezone` usage in Task mass creation
- [ ] Assert `name` is not null on `Task.update`
- [ ] Find a way to differenciate `no tag` and `remove tag` on `Task.update`
- [X] Modify summary endpoint to get total number of chapters from the back
- [X] `ArcCreationDto` — `totalEstimatedMinutes` defaults to 0 (populated by recalculation)

## Tests
- [ ] Check inheritence in `RecalculationIntegrationTest`

## Frontend
- [ ] Fix `dashboard-resume` page on summary call, decide to fix whether the back call to summary
      OR when no chapter assigned on that day, if a task is created, create a chapter AND the task to it
- [X] Fix tag load on `arc-resolver.ts`