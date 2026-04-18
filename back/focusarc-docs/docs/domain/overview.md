---
title: Domain Overview
sidebar_position: 1
---

# Domain Overview

FocusArc organizes work sessions in a three-level hierarchy:

**User → Arc → Chapter → Task**

```mermaid
classDiagram
    class User {
        UserId id
        String name
        String email
        LocalDateTime lastLogin
    }

    class Arc {
        ArcId id
        UserId owner
        String name
        int totalEstimatedMinutes
        int totalCompletedMinutes
        ArcStatus status
    }

    class Chapter {
        ChapterId id
        ArcId arc
        int estimatedMinutes
        int completedMinutes
        LocalDate scheduledDate
        boolean allTasksDone
    }

    class Task {
        TaskId id
        ChapterId chapter
        int estimatedMinutes
        int completedMinutes
        Instant startAt
        Instant startedAt
        Instant endAt
        Instant completedAt
        TaskStatus status
        String name
        String description
        TagId tagId
    }

    class Tag {
        TagId id
        UserId owner
        String label
        TagColor color
    }

    User "1" --> "0..*" Arc : has
    Arc "1" --> "0..*" Chapter : has
    Chapter "1" --> "0..*" Task : contains
    User "1" --> "0..*" Tag : owns
    Task "0..*" --> "0..1" Tag : tagged with
```

## Enums

| Enum            | Values                                                                       |
|-----------------|------------------------------------------------------------------------------|
| `ArcStatus`     | `ACTIVE`, `COMPLETED`, `ARCHIVED`                                            |
| `ChapterStatus` | `PLANNED`, `COMPLETED`, `SKIPPED`                                            |
| `TaskStatus`    | `PLANNED`, `IN_PROGRESS`, `DONE`, `SKIPPED`                                  |
| `TagColor`      | `RED`, `ORANGE`, `YELLOW`, `GREEN`, `TEAL`, `BLUE`, `PURPLE`, `PINK`, `GRAY` |

## Key Invariants

- A user may have **at most one ACTIVE arc** at a time.
- An arc may have **at most one chapter per date**.
- A task belongs to exactly one chapter and optionally references one tag.