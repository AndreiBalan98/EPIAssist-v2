# EPIAssist-v2

EPIAssist is an AI-powered document assistant built around a RAG (Retrieval-Augmented Generation) architecture. The idea is simple: you upload markdown documents, the system breaks them into sections, generates vector embeddings for each section, and uses that knowledge base to answer questions intelligently — giving the AI actual context to work with rather than just relying on what it already knows.

This is v2 of the project. Still a work in progress.

---

## What it does

- Accepts markdown documents via a REST API
- Automatically extracts a table of contents from headings
- Splits each document into semantic chunks (based on heading hierarchy)
- Generates a vector embedding for every chunk using an external Python service
- Stores everything in a PostgreSQL database
- Exposes a chat endpoint that proxies questions to an AI service
- Collects user feedback (rating + message) on responses

---

## How it works

There are two services running side by side:

**Java backend (Spring Boot)** handles all the REST API logic — document storage, chunking, TOC extraction, and serving data to clients. It talks to a PostgreSQL database via JPA.

**Python service** handles the AI-specific work — generating embeddings from text and producing chat responses. The Java backend calls it over HTTP whenever it needs to embed a chunk or answer a question.

When a document is uploaded, the pipeline goes like this:

1. The markdown is saved to the database
2. Headings are parsed to build a table of contents
3. The document is split into chunks, each identified by a hierarchical URL (e.g. `doc-name/Chapter 1/Section 2`)
4. Each chunk is sent to the Python service to get a vector embedding
5. Chunks with their embeddings are stored in the database

When a user sends a chat message, it's forwarded to the Python AI service which can use the stored knowledge to respond.

---

## Project structure

```
EPIAssist-v2/
├── src/
│   └── main/
│       ├── java/org/epi_assist/EPIAssist_v2/
│       │   ├── config/         Spring beans (RestClient setup)
│       │   ├── controller/     REST endpoints (chat, documents, chunks, feedback)
│       │   ├── service/        Business logic
│       │   ├── entity/         JPA entities (Document, Chunk, Feedback)
│       │   ├── repository/     Spring Data repositories
│       │   └── dto/            Request and response records
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
└── DOCUMENTATION.md
```

---

## Tech stack

| Layer | Technology |
|---|---|
| Backend API | Java 17, Spring Boot 4, Spring Data JPA |
| Database | PostgreSQL |
| Build | Maven |
| AI / Embeddings | Python service (external, port 8000) |
| Containerization | Docker |

---

## API overview

| Method | Endpoint | Description |
|---|---|---|
| POST | `/chat` | Send a message, get an AI response |
| GET | `/documents` | List all document names |
| POST | `/documents` | Upload a markdown document |
| GET | `/documents/{name}` | Get the full content of a document |
| GET | `/documents/toc/{name}` | Get the table of contents |
| DELETE | `/documents/{name}` | Delete a document and its chunks |
| GET | `/chunks/{name}` | Get all chunks for a document |
| POST | `/feedback` | Submit feedback on a response |

Full API and service documentation is in [DOCUMENTATION.md](./DOCUMENTATION.md).

---

## Running locally

You need PostgreSQL running and the Python AI service available at port 8000. The Java backend runs on port 8080.

```bash
# build and run with Maven
./mvnw spring-boot:run
```

Or with Docker:

```bash
docker build -t epiassist-v2 .
docker run -p 8080:8080 epiassist-v2
```

---

## Status

This project is actively being developed. The core ingestion pipeline and REST API are working. The Python AI service and the frontend are separate components, still being built out.
