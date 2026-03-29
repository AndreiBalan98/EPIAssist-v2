# EPIAssist-v2 — Server Documentation

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Configuration](#configuration)
4. [Database Schema](#database-schema)
5. [Data Models](#data-models)
   - [Entities](#entities)
   - [DTOs](#dtos)
6. [API Reference](#api-reference)
   - [Chat](#chat)
   - [Documents](#documents)
   - [Chunks](#chunks)
   - [Feedback](#feedback)
7. [Services & Business Logic](#services--business-logic)
8. [Repositories](#repositories)
9. [External Integrations](#external-integrations)

---

## Overview

EPIAssist-v2 is a Spring Boot REST API that powers a **RAG (Retrieval-Augmented Generation)** system for document-based AI assistance. It handles markdown document ingestion, automatic chunking and embedding generation, table-of-contents extraction, AI-powered chat, and user feedback collection.

**Stack:**
- Java 17
- Spring Boot 4.0.4 (Web, Data JPA)
- PostgreSQL
- Lombok
- External AI/embedding service (Python, port 8000)

**Server port:** `8081`

---

## Architecture

```
Client
  │
  ▼
Controllers  (HTTP layer)
  │
  ▼
Services     (business logic)
  │        │
  ▼        ▼
Repositories  External AI Service (port 8000)
  │
  ▼
PostgreSQL (epiassist_db)
```

**Package root:** `org.epi_assist.EPIAssist_v2`

| Package      | Responsibility                         |
|--------------|----------------------------------------|
| `config`     | RestClient bean configuration          |
| `controller` | HTTP request handling                  |
| `service`    | Business logic                         |
| `repository` | Database access (Spring Data JPA)      |
| `entity`     | JPA-mapped database tables             |
| `dto`        | Request/response transfer objects      |

---

## Configuration

**File:** `src/main/resources/application.properties`

```properties
spring.application.name=EPIAssist-v2
spring.datasource.url=jdbc:postgresql://localhost:5432/epiassist_db
spring.datasource.username=postgres
spring.datasource.password=cosmonet
spring.jpa.hibernate.ddl-auto=update
server.port=8081
```

**RestClient** (`config/RestClientConfig.java`) — a Spring-managed `RestClient` bean with base URL `http://localhost:8000`, used to communicate with the external AI/embedding service.

---

## Database Schema

Schema is auto-managed by Hibernate (`ddl-auto=update`). Three tables are created:

### `documents`

| Column            | Type    | Constraints          |
|-------------------|---------|----------------------|
| `id`              | BIGINT  | PK, auto-increment   |
| `name`            | VARCHAR | NOT NULL, UNIQUE     |
| `markdown_content`| TEXT    | NOT NULL             |
| `toc`             | JSONB   | nullable             |

### `chunks`

| Column      | Type   | Constraints        |
|-------------|--------|--------------------|
| `id`        | BIGINT | PK, auto-increment |
| `url`       | TEXT   | NOT NULL           |
| `content`   | TEXT   | NOT NULL           |
| `embedding` | JSONB  | nullable           |

### `feedbacks`

| Column    | Type    | Constraints        |
|-----------|---------|--------------------|
| `id`      | BIGINT  | PK, auto-increment |
| `convo`   | TEXT    | nullable           |
| `stars`   | INTEGER | nullable           |
| `message` | TEXT    | nullable           |

---

## Data Models

### Entities

#### `Document`

JPA entity mapped to the `documents` table.

```java
@Entity
@Table(name = "documents")
class Document {
    Long id;
    String name;               // unique document identifier
    String markdownContent;    // raw markdown text of the document
    List<TocSectionDto> toc;   // stored as JSONB
}
```

#### `Chunk`

JPA entity mapped to the `chunks` table. Each chunk represents a section of a document, identified by a hierarchical URL.

```java
@Entity
@Table(name = "chunks")
class Chunk {
    Long id;
    String url;              // e.g. "documentName/heading1/subheading"
    String content;          // text content of the section
    List<Float> embedding;   // vector embedding, stored as JSONB
}
```

#### `Feedback`

JPA entity mapped to the `feedbacks` table.

```java
@Entity
@Table(name = "feedbacks")
class Feedback {
    Long id;
    String convo;     // conversation transcript
    Integer stars;    // star rating
    String message;   // user's written feedback
}
```

---

### DTOs

All DTOs are Java records (immutable).

#### Request DTOs

| DTO                  | Fields                                        | Used by              |
|----------------------|-----------------------------------------------|----------------------|
| `ChatRequestDto`     | `String message`                              | `POST /chat`         |
| `FeedbackRequestDto` | `String convo`, `Integer stars`, `String message` | `POST /feedback` |
| `EmbeddingRequestDto`| `String text`                                 | Internal (embedding service call) |

#### Response DTOs

| DTO                   | Fields                               | Used by                     |
|-----------------------|--------------------------------------|-----------------------------|
| `ChatResponseDto`     | `String message`                     | `POST /chat`                |
| `DocumentNameDto`     | `String name`                        | `GET /documents`, `POST /documents` |
| `DocumentContentDto`  | `String content`                     | `GET /documents/{name}`     |
| `DocumentDto`         | `DocumentNameDto nameDto`, `DocumentContentDto contentDto` | Internal composite |
| `ChunkDto`            | `Long id`, `String url`, `String content`, `List<Float> embedding` | `GET /chunks/{name}` |
| `TocSectionDto`       | `int position`, `String name`, `int level` | `GET /documents/toc/{name}` |
| `EmbeddingResponseDto`| `List<Float> embedding`              | Internal (embedding service response) |

---

## API Reference

### Chat

#### `POST /chat`

Sends a user message to the external AI service and returns the response.

**Request body** (`application/json`):
```json
{
  "message": "What is EPI?"
}
```

**Response** (`200 OK`, `application/json`):
```json
{
  "message": "EPI stands for..."
}
```

---

### Documents

#### `GET /documents`

Returns the names of all stored documents.

**Response** (`200 OK`, `application/json`):
```json
[
  { "name": "user-manual" },
  { "name": "safety-guide" }
]
```

---

#### `GET /documents/{name}`

Returns the full markdown content of a document.

**Path parameter:** `name` — document name

**Response** (`200 OK`, `application/json`):
```json
{
  "content": "# Introduction\n\nThis document..."
}
```

---

#### `GET /documents/toc/{name}`

Returns the table of contents for a document, as extracted from its markdown headings.

**Path parameter:** `name` — document name

**Response** (`200 OK`, `application/json`):
```json
[
  { "position": 0, "name": "Introduction", "level": 1 },
  { "position": 1, "name": "Installation", "level": 2 }
]
```

| Field      | Description                                        |
|------------|----------------------------------------------------|
| `position` | Zero-based index of the heading in the document    |
| `name`     | Heading text                                       |
| `level`    | Heading depth (1 = `#`, 2 = `##`, etc.)            |

---

#### `POST /documents`

Uploads a markdown file, processes it, generates embeddings, and persists everything.

**Request** (`multipart/form-data`):

| Parameter | Type          | Description           |
|-----------|---------------|-----------------------|
| `name`    | String        | Unique document name  |
| `file`    | MultipartFile | The `.md` file        |

**Response** (`201 Created`, `application/json`):
```json
{
  "name": "user-manual"
}
```

**Processing pipeline (see DocumentService):**
1. Read file bytes → UTF-8 string
2. Save `Document` entity (name + markdown)
3. Extract TOC → update `Document.toc`
4. Extract chunks from markdown headings
5. For each chunk → call `/embedding` on the AI service → save `Chunk` with embedding

---

### Chunks

#### `GET /chunks/{name}`

Returns all chunks belonging to a document, including their vector embeddings.

**Path parameter:** `name` — document name

**Response** (`200 OK`, `application/json`):
```json
[
  {
    "id": 1,
    "url": "user-manual/Introduction",
    "content": "This document covers...",
    "embedding": [0.021, -0.134, ...]
  }
]
```

Chunks are matched by URL prefix (`documentName/`).

---

### Feedback

#### `POST /feedback`

Stores a user feedback entry.

**Request body** (`application/json`):
```json
{
  "convo": "User: What is EPI?\nAssistant: ...",
  "stars": 4,
  "message": "Very helpful!"
}
```

**Response:** `201 Created` (no body)

---

## Services & Business Logic

### `DocumentService`

The most complex service. Orchestrates document persistence, TOC extraction, chunking, and embedding generation.

#### `postDocument(String name, MultipartFile file)`

Full document ingestion pipeline:

```
1. file.getBytes() → UTF-8 markdown string
2. new Document(name, markdown) → documentRepository.save()
3. extractToc(markdown) → document.setToc() → documentRepository.save()
4. extractChunks(name, markdown) → List<Chunk>
5. for each chunk:
     POST http://localhost:8000/embedding  { text: chunk.content }
     → chunk.setEmbedding(response.embedding)
     → chunkRepository.save(chunk)
6. return DocumentNameDto(name)
```

#### `extractToc(String content)`

Parses markdown line by line. A line is a heading if it starts with one or more `#` characters followed by a space.

- **Level:** number of leading `#` characters
- **Name:** text after the `# ` prefix
- **Position:** sequential index (0-based) among all headings found

Returns `List<TocSectionDto>`.

#### `extractChunks(String documentName, String content)`

Splits the markdown into sections based on heading hierarchy:

- Content is split by lines (`\r\n`)
- A heading array of size 10 tracks the current hierarchy
- When a heading is encountered:
  - The current accumulated text becomes the content of the previous chunk
  - The heading array is updated at the appropriate depth
  - Deeper headings reset all headings below them
- The chunk URL is built by joining non-empty heading array slots with `/`, prefixed with `documentName`
  - Example: `user-manual/Chapter 1/Installation`
- Non-heading lines accumulate into the current chunk's content

Returns `List<Chunk>` (without embeddings — these are added in `postDocument`).

#### `getDocumentsNames()`

Returns `List<DocumentNameDto>` — all document names from the database.

#### `getDocumentByName(String name)`

Finds document by name, returns `DocumentContentDto(markdownContent)`.

#### `getDocumentToc(String name)`

Finds document by name, returns its stored `List<TocSectionDto>`.

---

### `ChunkService`

#### `getChunksByDocumentName(String documentName)`

Calls `chunkRepository.findByUrlStartingWith(documentName + "/")` and maps results to `List<ChunkDto>`.

---

### `ChatService`

#### `chat(ChatRequestDto request)`

Proxies the request to the external AI service:

```
POST http://localhost:8000/ai
Body: { "message": "..." }
→ returns ChatResponseDto
```

Uses the injected `RestClient` bean.

---

### `FeedbackService`

#### `postFeedback(FeedbackRequestDto dto)`

Creates and saves a `Feedback` entity from the DTO fields (`convo`, `stars`, `message`).

---

## Repositories

All repositories extend `JpaRepository<Entity, Long>` and inherit standard CRUD methods.

### `DocumentRepository`

```java
Document findByName(String name);
```

### `ChunkRepository`

```java
List<Chunk> findByUrlStartingWith(String prefix);
```

Used to retrieve all chunks belonging to a document by matching the URL prefix `documentName/`.

### `FeedbackRepository`

No custom queries — uses inherited JPA methods only.

---

## External Integrations

Both integrations use the `RestClient` bean configured with base URL `http://localhost:8000`.

### Embedding Service — `POST /embedding`

Called during document upload for each extracted chunk.

**Request:**
```json
{ "text": "chunk content here" }
```

**Response:**
```json
{ "embedding": [0.021, -0.134, 0.309, ...] }
```

The returned float list is stored in `Chunk.embedding` (JSONB column).

### AI Chat Service — `POST /ai`

Called on every chat request.

**Request:**
```json
{ "message": "user question" }
```

**Response:**
```json
{ "message": "AI response" }
```
