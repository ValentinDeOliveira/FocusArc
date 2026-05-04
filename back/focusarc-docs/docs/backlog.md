---                                                                                                                                                                                                                               
sidebar_position: 2
title: Backlog
---

# Backlog

## Security

- [ ] Move JWT secret to env var (`application.properties` — hardcoded)
- [ ] Move MongoDB credentials to env var (`application.yaml` — hardcoded admin/admin123)

## Domain / Business Logic
- [ ] Add cross-field validation on `TaskUpdateDto` (`completedMinutes < estimatedMinutes`)
- [ ] Change `User.lastLogin` from `LocalDateTime` to `Instant`
- [ ] Add `timezone` field in `User`
- [ ] Fix `timezone` usage in Task mass creation
- [ ] Assert `name` is not null on `Task.update`
- [ ] Find a way to differenciate `no tag` and `remove tag` on `Task.update`
- [ ] Modify summary endpoint to get total number of chapters from the back
- [ ] set default `estimatedMinutes` in `ArcCreationDto` to 0 (will be populated by chapters & tasks)

## Tests
- [ ] Check inheritence in `RecalculationIntegrationTest`

## Frontend
- [ ] Fix `dashboard-resume` page on summary call, decide to fix whether the back call to summary
      OR when no chapter assigned on that day, if a task is created, create a chapter AND the task to it
- [ ] Fix tag load on `arc-resolver.ts`