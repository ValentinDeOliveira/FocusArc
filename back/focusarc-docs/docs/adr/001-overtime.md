---
title: "ADR-001: Overtime"
sidebar_position: 1
---

# ADR-001: Overtime

## Status
Accepted

## Context

When a user works on a task, they may not stop exactly when the estimated time ends. 
Interrupting the session or requiring manual input at that moment would break focus which is, 
the core purpose of the app.

## Decision

When a task's timer reaches `endAt`, it automatically transitions into **overtime mode** without any user interaction. 
The timer continues running. 

When the user eventually stops the task, `completedMinutes` is recorded as:

```
completedMinutes = estimatedMinutes + overtime duration
```

## Consequences

- Users are never interrupted mid-focus when the estimated time ends.
- `completedMinutes` can exceed `estimatedMinutes`, which is intentional.
- Recalculation cascades (Chapter → Arc) must handle `completedMinutes > estimatedMinutes` 
without treating it as an error.