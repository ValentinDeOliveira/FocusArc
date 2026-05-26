---
title: ドメインの概要
sidebar_position: 1
---

# ドメインの概要

FocusArcは作業セッションを3レベルの階層で管理します：

**User → Arc → Chapter → Task**

```mermaid
flowchart LR
    User --> Arc
    Arc --> Chapter
    Chapter --> Task
    Task -.->|optional| Tag
    User --> Tag
```