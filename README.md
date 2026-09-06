# TeleStore

TeleStore is a Spring Boot REST API that lets users upload, organize, and download files, using **Telegram** as the underlying storage backend (via `TelegramMedia`, `TelegramDocument`, `TelegramFile`, `TelegramMessage`, and `TelegramResponse`). Files are grouped into user-owned folders, and every resource is scoped to the authenticated user via JWT-based security (`CustomUserDetails`).

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Domain Model](#domain-model)
- [Authentication](#authentication)
- [API Reference](#api-reference)
  - [Auth](#auth-api)
  - [Users](#users-api)
  - [Folders](#folders-api)
  - [Media](#media-api)
- [Error Handling](#error-handling)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)

---

## Features

| Capability | Description |
|---|---|
| User accounts | Register/login with email + password, JWT-secured sessions |
| Folder hierarchy | Create nested folders (`parentFolderId`), list, rename, delete |
| Media upload | Upload files, stored via Telegram, tracked with metadata (type, mime, size) |
| Media search | Filter media by query text, type, and folder, with pagination + sorting |
| Media download | Stream original file content back with correct filename/MIME type |
| Media update | Rename files or move them between folders |

---

## Tech Stack

- **Java + Spring Boot** (Spring Web, Spring Security, Spring Data MongoDB)
- **MongoDB** — entities (`Folder`, `Media`) are annotated with `@Document`, `@Id`, `@Indexed`, `@CreatedDate`/`@LastModifiedDate`
- **Lombok** — `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` across entities/DTOs
- **Telegram Bot API** — used as the actual file storage layer, abstracted behind `TelegramMedia`/`TelegramDocument`/`TelegramFile`/`TelegramMessage`/`TelegramResponse<T>`

---

## Domain Model

### `User`
| Field | Type | Notes |
|---|---|---|
| `id` | String | Mongo `@Id` |
| `name` | String | |
| `email` | String | Used as login identifier |
| `password` | String | Hashed (not returned in responses) |
| `createdAt` | Instant | |
| `updatedAt` | Instant | |

### `Folder`
| Field | Type | Notes |
|---|---|---|
| `id` | String | Mongo `@Id` |
| `userId` | String | Owner |
| `name` | String | |
| `parentFolderId` | String | `@Indexed`; null/absent for root folders |
| `createdAt` / `updatedAt` | Instant | |

### `Media`
| Field | Type | Notes |
|---|---|---|
| `id` | String | Mongo `@Id` |
| `userId` | String | Owner |
| `folderId` | String | Nullable — file may live outside any folder |
| `filename` | String | |
| `mediaType` | `MediaType` | `IMAGE`, `VIDEO`, `AUDIO`, `DOCUMENT`, `OTHER` |
| `mimeType` | String | |
| `size` | Long | Bytes |
| `extension` | String | |
| `media` | `TelegramMedia` | Pointer to the Telegram-stored blob |
| `createdAt` / `updatedAt` | Instant | |

### `TelegramMedia` (embedded)
| Field | Type | Notes |
|---|---|---|
| `chatId` | Long | Telegram chat used as storage channel |
| `messageId` | Long | Message holding the file |
| `fileId` | String | Telegram file identifier |
| `fileUniqueId` | String | Telegram unique file identifier |

### Telegram API DTOs (internal, used to talk to the Telegram Bot API)
| DTO | Purpose |
|---|---|
| `TelegramResponse<T>` | Generic wrapper: `ok`, `result`, `description` |
| `TelegramMessage` | `message_id`, embedded `document` |
| `TelegramDocument` | `file_id`, `file_unique_id`, `file_size`, `file_name`, `mime_type` |
| `TelegramFile` | `file_id`, `file_unique_id`, `file_size`, `file_path` (used to resolve a download URL) |

---

## Authentication

All endpoints except `/api/auth/**` require a valid authenticated session. Controllers pull the current user off the Spring Security context:

```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
String userId = userDetails.getUserId();
```

Include the issued token on every subsequent request:

```
Authorization: Bearer <token>
```

> Note: `UserController.updateUser` currently resolves the user by `authentication.getName()` (email) rather than `userId` — inconsistent with the rest of the codebase, worth aligning if refactoring.

---

## API Reference

Base URL: `http://localhost:8080` (adjust to your deployment)

### Auth API

#### Register
```
POST /api/auth/register
```
**Body**
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```
**Response:** `204 No Content`

#### Login
```
POST /api/auth/login
```
**Body**
```json
{
  "email": "string",
  "password": "string"
}
```
**Response:** `200 OK`
```json
{
  "token": "string"
}
```

---

### Users API

#### Get current user
```
GET /api/users/me
```
🔒 Auth required
**Response:** `200 OK`
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "createdAt": "2026-01-01T00:00:00Z",
  "updatedAt": "2026-01-01T00:00:00Z"
}
```

#### Update current user
```
PUT /api/users
```
🔒 Auth required
**Body**
```json
{
  "name": "string"
}
```
**Response:** `200 OK` — updated `UserResponse`

---

### Folders API

| Method | Path | Description |
|---|---|---|
| POST | `/api/folder` | Create a folder |
| GET | `/api/folder` | List all folders for the user |
| GET | `/api/folder/{id}` | Get a single folder |
| GET | `/api/folder/children/{parentFolderId}` | List direct children of a folder |
| PUT | `/api/folder/{id}` | Rename/update a folder |
| DELETE | `/api/folder/{id}` | Delete a folder |

All routes require auth and are scoped to `userId`.

#### Create folder
```
POST /api/folder
```
**Body**
```json
{
  "name": "string",
  "parentFolderId": "string | null"
}
```
**Response:** `200 OK` — created `Folder`

#### List folders
```
GET /api/folder
```
**Response:** `200 OK` — `Folder[]`

#### Get folder
```
GET /api/folder/{id}
```
**Response:** `200 OK` — `Folder`

#### Get child folders
```
GET /api/folder/children/{parentFolderId}
```
**Response:** `200 OK` — `Folder[]`

#### Update folder
```
PUT /api/folder/{id}
```
**Body**
```json
{
  "name": "string"
}
```
**Response:** `200 OK` — updated `Folder`

#### Delete folder
```
DELETE /api/folder/{id}
```
**Response:** `204 No Content`

---

### Media API

| Method | Path | Description |
|---|---|---|
| POST | `/api/media/upload` | Upload a file |
| GET | `/api/media` | Search/list media (paginated) |
| GET | `/api/media/{id}` | Get media metadata |
| GET | `/api/media/{id}/download` | Download file bytes |
| PATCH | `/api/media/{id}` | Rename/move media |
| DELETE | `/api/media/{id}` | Delete media |

#### Upload media
```
POST /api/media/upload
Content-Type: multipart/form-data
```
**Form fields**
| Field | Type | Required |
|---|---|---|
| `file` | binary | yes |
| `folderId` | string | yes |

**Response:** `200 OK` — created `Media`

#### Search/list media
```
GET /api/media
```
**Query parameters**
| Param | Type | Default | Description |
|---|---|---|---|
| `q` | string | — | Free-text search |
| `type` | string | — | Filter by `MediaType` |
| `folderId` | string | — | Filter by folder |
| `page` | int | `0` | Page index |
| `size` | int | `20` | Page size |
| `sortBy` | string | `createdAt` | Sort field |
| `sortDir` | string | `desc` | `asc` or `desc` |

**Response:** `200 OK` — Spring `Page<Media>`
```json
{
  "content": [ /* Media objects */ ],
  "totalElements": 0,
  "totalPages": 0,
  "number": 0,
  "size": 20
}
```

#### Get media metadata
```
GET /api/media/{id}
```
**Response:** `200 OK` — `Media`

#### Download media
```
GET /api/media/{id}/download
```
**Response:** `200 OK`
- `Content-Disposition: attachment; filename="<filename>"`
- `Content-Type: <mimeType>`
- Body: raw file bytes

#### Update media
```
PATCH /api/media/{id}
```
**Body**
```json
{
  "folderId": "string | null",
  "filename": "string | null"
}
```
Only non-null fields are applied.
**Response:** `200 OK` — updated `Media`

#### Delete media
```
DELETE /api/media/{id}
```
**Response:** `204 No Content`

---

## Error Handling

No global exception handler is present in the provided controllers, so unhandled exceptions (e.g., resource not found, unauthorized access, Telegram upstream failures) will surface as default Spring Boot error responses (`500` unless a `@ControllerAdvice` is added at the service layer). Recommended: add a centralized `@RestControllerAdvice` to map service-layer exceptions to `404`, `403`, and `400` responses with a consistent error body.

---

## Getting Started

1. Clone the repository and open it in your IDE.
2. Configure MongoDB connection properties (`application.properties`/`.yml`):
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/telestore
   ```
3. Configure your Telegram Bot token and storage chat ID (used internally by the service layer when populating `TelegramMedia`).
4. Configure JWT secret/expiry used by the security layer producing `CustomUserDetails`.
5. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```
6. Register a user, log in to obtain a token, then call folder/media endpoints with `Authorization: Bearer <token>`.

---

## Project Structure

```
com.argha.telestore
├── controller
│   ├── AuthController
│   ├── UserController
│   ├── FolderController
│   └── MediaController
├── dto
│   ├── auth        (LoginRequest, LoginResponse, ResgisterRequest)
│   ├── user        (UserResponse, UpdateUserRequest)
│   ├── folder      (CreateFolderRequest, UpdateFolderRequest)
│   ├── media       (UpdateMediaRequest)
│   └── telegram    (TelegramDocument, TelegramFile, TelegramMessage, TelegramResponse)
├── entity
│   ├── User
│   ├── Folder
│   ├── Media
│   ├── MediaType
│   └── TelegramMedia
├── security
│   └── CustomUserDetails
└── service
    ├── AuthService
    ├── UserService
    ├── FolderService
    └── MediaService
```
