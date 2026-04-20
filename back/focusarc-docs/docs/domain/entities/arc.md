---                                                                                                                                                                                                                              
title: Arc                                                                                                                                                                                                                       
sidebar_position: 1
---

# Arc

A multi-week focus span owned by a user. At most one can be active at a time.

## Fields
| Field                   | Type        | Description                                       |
|-------------------------|-------------|---------------------------------------------------|
| `id`                    | `ArcId`     | Unique identifier                                 |
| `owner`                 | `UserId`    | The user who owns the arc                         |
| `name`                  | `String`    | Arc name                                          |
| `status`                | `ArcStatus` | Current status of the arc                         |
| `startDate`             | `LocalDate` | Start date of the arc                             |
| `endDate`               | `LocalDate` | End date of the arc                               |

## Derived Fields

| Field                   | Derived From           | Rule                                                                             |                                                                                                                                                                                                  
|-------------------------|------------------------|----------------------------------------------------------------------------------|
| `totalEstimatedMinutes` | Tasks → Chapters → Arc | Sum of `estimatedMinutes` across all tasks in all chapters of this arc           |
| `totalCompletedMinutes` | Tasks → Chapters → Arc | Sum of `completedMinutes` across all completed tasks in all chapters of this arc |

## Enums

### ArcStatus
| Value       | Description                    |
|-------------|--------------------------------|
| `ACTIVE`    | Current active arc of the user |
| `COMPLETED` | Completed arc                  |
| `ARCHIVED`  | Archived arc                   |

## Constraints
- A user can have at most one `ACTIVE` arc at a time
- `startDate` must be before `endDate`

## Lifecycle
### ArcStatus transition
When a user create an arc, it will be immediately defined as `ACTIVE`.

Once that Arc end (we reach the `endDate`) the status pass as `COMPLETED` (TBD)

`ARCHIVED` TBD