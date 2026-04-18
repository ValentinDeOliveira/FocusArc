---                                                                                                                                                                                                                              
title: Task                                                                                                                                                                                                                       
sidebar_position: 1
---

# Task

A discrete work block scheduled within a chapter, with an estimated duration and a time slot.

## Fields
| Field              | Type         | Description                                      |
|--------------------|--------------|--------------------------------------------------|
| `id`               | `TaskId`     | Unique identifier                                |
| `chapter`          | `ChapterId`  | The arc that contains the chapter                |
| `estimatedMinutes` | `int`        | The estimated minutes on the task                |
| `completedMinutes` | `int`        | The time spent on the task                       |
| `startAt`          | `Instant`    | The time the task is scheduled                   |
| `startedAt`        | `Instant`    | The time the task has been started by the user   |
| `completedAt`      | `Instant`    | The time the task has been completed by the user |
| `status`           | `TaskStatus` | The status of the task                           |
| `name`             | `String`     | The name of the task                             |
| `description`      | `String`     | The description of the task (TBD)                |
| `tagId`            | `TagId`      | The tag of the task                              |

## Derived Fields

| Field         | Derived From | Rule                                             |                                                                                                                                                                                                  
|---------------|--------------|--------------------------------------------------|
| `endAt`       | Task         | The time the task is supposed to end             |

## Enums

### TaskStatus
| Value         | Description                 |
|---------------|-----------------------------|
| `PLANNED`     | Task has been planned       |
| `IN_PROGRESS` | Task is in progress         |
| `DONE`        | Task has been finished      |
| `SKIPPED`     | Task has been skipped (TBD) |

## Constraints
- A finished task (`DONE` or `SKIPPED`) cannot be started or completed again
- `estimatedMinutes` cannot exceed **1440** (number of minutes in a day)
- `completedMinutes` cannot exceed **1440** (number of minutes in a day)
- A task's `startAt` time slot cannot overlap with another task in the same chapter
- Only one task can be `IN_PROGRESS` per chapter at a time

## Lifecycle
- Created as `PLANNED`
- `PLANNED` → `IN_PROGRESS` when the task is started
- `PLANNED` can skip directly to `SKIPPED` (TBD, no endpoint for it yet)
- `IN_PROGRESS` → `DONE` when the task is completed