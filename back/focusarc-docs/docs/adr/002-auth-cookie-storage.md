---
title: "ADR-002: Auth Token Storage"
sidebar_position: 2
---

# ADR-002: Auth Token Storage

## Status
Accepted

## Context

The API issues a short-lived access token and a long-lived refresh token on login and registration.
These tokens need to be stored client-side so the frontend can authenticate subsequent requests.

The two common approaches are:

- **localStorage** — tokens stored in JS-accessible browser storage; simple to implement with a request interceptor that reads and injects the `Authorization` header.
- **HttpOnly cookies** — tokens stored in cookies that are inaccessible to JavaScript; the browser sends them automatically on every request.

## Decision

Tokens are stored in **HttpOnly, SameSite=Strict cookies** set by the server on login, registration, and token refresh.
The frontend sends `withCredentials: true` on all requests so the browser includes the cookies automatically.
A server-side `CookieService` handles building, setting, and clearing the cookies.

## Consequences

- Scripts running on the page cannot read or steal the tokens, eliminating the XSS token-theft vector.
- `SameSite=Strict` prevents the cookies from being sent on cross-site requests, making CSRF attacks ineffective without requiring an explicit CSRF token.
- The frontend no longer needs to manage token storage or manually inject `Authorization` headers.
- The auth guard cannot read the token from JS to decide whether to redirect — instead, a 401 response from the API triggers the redirect. This means a brief render of the protected component may occur before the API call completes.