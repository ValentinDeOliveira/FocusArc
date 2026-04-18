---
title: Domain Overview
sidebar_position: 1
---

# Domain Overview

FocusArc organizes work sessions in a three-level hierarchy:

**User → Arc → Chapter → Task**

```mermaid
flowchart LR
    User --> Arc
    Arc --> Chapter
    Chapter --> Task
    Task -.->|optional| Tag
    User --> Tag
```