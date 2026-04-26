---                                                                                                                                                                                                                              
title: User                                                                                                                                                                                                                       
sidebar_position: 1
---

# User

A registered account. The root of the entire hierarchy.

## Fields
| Field          | Type            | Description                  |
|----------------|-----------------|------------------------------|
| `id`           | `UserId`        | Unique identifier            |
| `name`         | `String`        | The username                 |
| `email`        | `String`        | The email                    |
| `password`     | `String`        | The password (stored hashed) |
| `lastLogin`    | `LocalDateTime` | The last time user logged in |
| `authProvider` | `AuthProvider`  | How the user registered      |

## Enums

### AuthProvider

| Value    | Description                          |
|----------|--------------------------------------|
| `LOCAL`  | User registered with mail + password |
| `GOOGLE` | User registered with Google          |


## Constraints
- `email` must be unique