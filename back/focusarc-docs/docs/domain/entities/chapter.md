---                                                                                                                                                                                                                              
title: Chapter                                                                                                                                                                                                                       
sidebar_position: 1
---

# Chapter

Represents a single calendar day within an arc. Contains the tasks planned for that day.

## Fields
| Field           | Type        | Description                            |
|-----------------|-------------|----------------------------------------|
| `id`            | `ChapterId` | Unique identifier                      |
| `arc`           | `ArcId`     | The arc that contains the chapter      |
| `scheduledDate` | `LocalDate` | The date this chapter is scheduled for |

## Derived Fields

| Field              | Derived From                     | Rule                                                                          |                                                                                                                                                                                                  
|--------------------|----------------------------------|-------------------------------------------------------------------------------|
| `estimatedMinutes` | Tasks → Chapters                 | Sum of `estimatedMinutes` across all tasks in the chapter                     |
| `completedMinutes` | Tasks → Chapters                 | Sum of `completedMinutes` across all completed tasks the chapter              |
| `allTasksDone`     | Tasks → Chapters                 | become true when all tasks are done (or skipped)                              |
| `status`           | `allTasksDone` + `scheduledDate` | `COMPLETED` if all tasks done; `SKIPPED` if date is past; otherwise `PLANNED` |                                                                                                  

## Enums

### ChapterStatus
| Value       | Description                |
|-------------|----------------------------|
| `PLANNED`   | Chapter has been planned   |
| `COMPLETED` | Chapter has been completed |
| `SKIPPED`   | Chapter has been skipped   |

## Constraints
- At most one chapter per arc per date
- A chapter can only be created within an `ACTIVE` arc