---
title: "ADR-002: Auth Token Storage"
sidebar_position: 2
---

# ADR-002: Auth Token Storage

## ステータス
承認済み

## 文脈

APIはログインと登録で、短期アクセストークンと長期レフレッシュトークンを発行します。
そのトーケンはクライアントサイドで保存するが必要があります。フロントエンドがリクエストを認証できるようにするためです。

2つの一般的な方法は：

- **localStorage** —　トークンはJavaScriptがアクセス可能なブラウザのストレージに保存されます。
実装しやすい
 simple to implement with a request interceptor that reads and injects the `Authorization` header.
- **HttpOnly cookies** — tokens stored in cookies that are inaccessible to JavaScript; the browser sends them automatically on every request.

## 決定

Tokens are stored in **HttpOnly, SameSite=Strict cookies** set by the server on login, registration, and token refresh.
The frontend sends `withCredentials: true` on all requests so the browser includes the cookies automatically.
A server-side `CookieService` handles building, setting, and clearing the cookies.

## 結果

- Scripts running on the page cannot read or steal the tokens, eliminating the XSS token-theft vector.
- `SameSite=Strict` prevents the cookies from being sent on cross-site requests, making CSRF attacks ineffective without requiring an explicit CSRF token.
- The frontend no longer needs to manage token storage or manually inject `Authorization` headers.
- The auth guard cannot read the token from JS to decide whether to redirect — instead, a 401 response from the API triggers the redirect. This means a brief render of the protected component may occur before the API call completes.