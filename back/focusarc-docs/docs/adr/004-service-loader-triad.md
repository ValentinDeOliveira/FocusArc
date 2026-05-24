---
title: "ADR-004: Service Layer Decomposition"
sidebar_position: 4
---
Service / Loader / RecalculationService triad. 
The split is deliberate (fetch-or-throw lives in Loader, business logic in Service, aggregation in RecalculationService)

**TODO**