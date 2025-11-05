# Memospace - Spaced Repetition Flashcard Backend

Spaced-repetition flashcards app following hexagonal architecture, CQRS, and Domain-Driven Design (DDD) principles. Built with Java 21, Spring Boot 3.5.6, and H2 database.

## Recent Updates

### Domain-Driven Design Integration (November 2025)
Memospace now incorporates **Domain-Driven Design (DDD)** tactical and strategic patterns:

- **Bounded Contexts**: Domain organized into 10 contexts (Authoring, Learning, Collection, Review, Media, Search, Import/Export, Analytics, IAM, Sync)
- **Aggregates**: Business invariants enforced at aggregate boundaries (NoteAggregate, CardAggregate, etc.)
- **Value Objects**: Type-safe domain primitives (NoteId, Ease, Quality, Tag, etc.)
- **Domain Events**: Event-driven coordination between bounded contexts (NoteCreated, CardReviewed, etc.)
- **Event Bus**: In-memory pub/sub for cross-context communication
- **Anti-Corruption Layer**: Protect domain from external format pollution (APKG, JSON import)

See `docs/context-map.md` and `docs/decision-records/adr-001-ddd-introduction.md` for details.

## Features

- **Hexagonal Architecture**: Clean separation between domain, application, and infrastructure layers
- **CQRS Pattern**: Command Query Responsibility Segregation for clear separation of read/write operations
- **Domain-Driven Design**: Bounded contexts, aggregates, value objects, and domain events
- **Note-Based Cards**: Generate flashcards from structured notes using customizable templates
- **Spaced Repetition**: Implements SM-2 algorithm for optimal review scheduling
- **Media Support**: Content-addressed storage for images and audio with automatic deduplication
- **RESTful API**: Complete CRUD operations for decks, cards, notes, reviews, and media
- **Template System**: Flexible card generation with support for cloze deletions
- **Import/Export**: JSON-based backup and migration with duplicate detection
- **H2 Database**: In-memory by default, file-based option available
- **API Documentation**: Auto-generated Swagger UI
- **Lombok Integration**: Reduced boilerplate code
- **Comprehensive Testing**: Unit and integration tests

## Quick Start

### Prerequisites
- Java 21
- Maven 3.6+

### Running the Application

```bash
# Clone and navigate to the project
cd memospace

# Run with in-memory H2 database (default)
./mvnw spring-boot:run

# Run with file-based H2 database
./mvnw spring-boot:run -Dspring.profiles.active=local
```

The application will start on `http://localhost:8080`

### API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### H2 Console (Development)

Access the H2 database console at: http://localhost:8080/h2-console

- **JDBC URL**: `jdbc:h2:mem:memospace` (default) or `jdbc:h2:file:./.data/memospace` (local profile)
- **Username**: `sa`
- **Password**: (empty)

## API Overview

### Base URL
```
http://localhost:8080/api/v1
```

### Core Endpoints

#### Decks
```http
GET    /decks           # List all decks
POST   /decks           # Create a new deck
GET    /decks/{id}      # Get deck by ID
PATCH  /decks/{id}      # Update deck
DELETE /decks/{id}      # Delete deck (cascades to notes and cards)
```

#### Note Types
```http
GET    /note-types           # List all note types
POST   /note-types           # Create a new note type with card templates
GET    /note-types/{id}      # Get note type by ID
PATCH  /note-types/{id}      # Update note type
DELETE /note-types/{id}      # Delete note type
```

#### Notes
```http
GET    /notes                      # Search notes with filters
POST   /notes                      # Create a note (generates cards)
GET    /notes/{id}                 # Get note by ID
PATCH  /notes/{id}                 # Update note (regenerates cards)
DELETE /notes/{id}                 # Delete note and its cards
GET    /notes/{id}/cards           # Get all cards for a note
POST   /notes/{id}/regenerate-cards # Regenerate cards (REPLACE/KEEP modes)
```

#### Cards
```http
GET    /cards                    # Search cards with filters
POST   /cards                    # Create a new card
GET    /cards/{id}               # Get card by ID
PATCH  /cards/{id}               # Update card
DELETE /cards/{id}               # Delete card
```

#### Reviews
```http
GET    /reviews/next             # Get next due cards
POST   /reviews/{cardId}         # Submit a review
GET    /reviews/history          # Get review history
```

#### Media
```http
POST   /media                    # Upload media file (image/audio)
GET    /media/{id}               # Download/stream media file
GET    /media/diagnostics        # Run media diagnostics
```

#### Import/Export
```http
GET    /export/json              # Export collection or deck as JSON
POST   /import/json              # Import collection from JSON
```

## Example Usage

### Workflow 1: Using Notes (Template-Based)

#### 1. Create a Deck
```bash
curl -X POST http://localhost:8080/api/v1/decks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spanish Vocabulary",
    "description": "Basic Spanish words"
  }'
```

#### 2. Create a Note Type with Templates
```bash
curl -X POST http://localhost:8080/api/v1/note-types \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Basic",
    "fields": ["Front", "Back"],
    "templates": [
      {
        "name": "Card 1",
        "front": "{{Front}}",
        "back": "{{Back}}"
      }
    ]
  }'
```

#### 3. Create a Note (Auto-generates Cards)
```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Content-Type: application/json" \
  -d '{
    "deckId": 1,
    "noteTypeId": 1,
    "fieldValues": {
      "Front": "What is the Spanish word for hello?",
      "Back": "Hola"
    },
    "tags": ["greetings", "basic"]
  }'
```

### Workflow 2: Direct Card Creation

#### Create a Card Directly
```bash
curl -X POST http://localhost:8080/api/v1/cards \
  -H "Content-Type: application/json" \
  -d '{
    "deckId": 1,
    "front": "What is the Spanish word for goodbye?",
    "back": "Adiós",
    "tags": ["greetings", "basic"]
  }'
```

### Review Workflow

#### 1. Get Next Due Cards
```bash
curl "http://localhost:8080/api/v1/reviews/next?limit=5"
```

#### 2. Review a Card
```bash
curl -X POST http://localhost:8080/api/v1/reviews/1 \
  -H "Content-Type: application/json" \
  -d '{
    "quality": 4,
    "msSpent": 3000
  }'
```

### Media Workflow

#### 1. Upload Media File
```bash
curl -X POST http://localhost:8080/api/v1/media \
  -F "file=@image.png"
```

Response:
```json
{
  "id": "f3a1b2c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2",
  "url": "/api/v1/media/f3a1b2c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2",
  "mimeType": "image/png",
  "sizeBytes": 123456,
  "originalFilename": "image.png",
  "deduplicated": false
}
```

#### 2. Use Media in Notes
Create a note with media references:
```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Content-Type: application/json" \
  -d '{
    "deckId": 1,
    "noteTypeId": 1,
    "fieldValues": {
      "Front": "What does this image show? ![image](/media/f3a1b2c...)",
      "Back": "Answer with audio: [sound:pronunciation.mp3]"
    }
  }'
```

#### 3. Run Media Diagnostics
```bash
curl http://localhost:8080/api/v1/media/diagnostics
```

Response:
```json
{
  "summary": {
    "missingOnDisk": 0,
    "orphansOnDisk": 2,
    "danglingReferences": 1,
    "unusedAssets": 12
  },
  "missingOnDisk": [],
  "orphansOnDisk": [{"path":"ab12...", "sizeBytes":1234}],
  "danglingReferences": [{"noteId":"...", "reference":"[sound:foo.mp3]"}],
  "unusedAssets": ["9c0d...", "aaef..."]
}
```

## Media Features

### Content-Addressed Storage
- Files stored by SHA-256 hash for automatic deduplication
- Path layout: `media/{hash[0..2]}/{hash}.{ext}`
- Identical content uploaded multiple times only stored once

### Supported Formats
- **Images**: PNG, JPEG, GIF, WebP, SVG
- **Audio**: MP3, M4A, OGG, WAV, WebM

### Media References in Notes
- Markdown: `![alt](/media/{id})`
- HTML: `<img src="/media/{id}">`, `<audio src="/media/{id}">`
- Anki-style: `[sound:filename.mp3]`

### Security
- Maximum file size: 25 MB (configurable)
- MIME type validation
- Filename sanitization
- Path traversal protection
- Content-addressed storage prevents tampering

### Media Diagnostics
Identifies:
- **Missing on disk**: Metadata exists but file is missing
- **Orphans on disk**: Files present but no metadata
- **Dangling references**: Notes reference non-existent media
- **Unused assets**: Media not referenced by any note

## Import/Export Features

### JSON Export/Import
Export your entire collection or specific decks as JSON for backup, migration, or sharing. The JSON format includes all decks, note types, notes, cards, and optionally a media manifest.

#### Export Collection
```bash
# Export entire collection
curl "http://localhost:8080/api/v1/export/json?includeMediaManifest=true" \
  -o my-collection.json

# Export specific deck
curl "http://localhost:8080/api/v1/export/json?deckId=1&includeMediaManifest=false" \
  -o my-deck.json
```

#### Import Collection
```bash
# Import JSON file
curl -X POST http://localhost:8080/api/v1/import/json \
  -H "Content-Type: application/json" \
  -d @my-collection.json

# Import with note type mapping (map "Basic" to existing note type)
curl -X POST http://localhost:8080/api/v1/import/json \
  -H "Content-Type: application/json" \
  -H 'X-NoteType-Mapping: {"Basic":"existing-uuid"}' \
  -d @my-collection.json
```

### Export Format
```json
{
  "version": 1,
  "exportedAt": "2025-10-11T12:00:00Z",
  "decks": [
    {"id": "uuid", "name": "Languages::English", "description": "..."}
  ],
  "noteTypes": [
    {
      "id": "uuid",
      "name": "Basic",
      "fields": ["Front", "Back"],
      "templates": [
        {
          "id": "uuid",
          "name": "Card 1",
          "frontTemplate": "{{Front}}",
          "backTemplate": "{{Back}}",
          "isCloze": false
        }
      ],
      "css": ".card{color:blue;}"
    }
  ],
  "notes": [
    {
      "id": "uuid",
      "deckId": "uuid",
      "noteTypeId": "uuid",
      "fieldValues": {"Front": "Question", "Back": "Answer"},
      "tags": ["tag1", "tag2"],
      "createdAt": "2025-10-10T10:20:30Z",
      "updatedAt": "2025-10-10T10:20:30Z"
    }
  ],
  "cards": [...],
  "mediaManifest": [
    {
      "id": "sha256-hex",
      "originalFilename": "image.png",
      "mimeType": "image/png",
      "sizeBytes": 12345
    }
  ]
}
```

### Import Behavior

#### Duplicate Detection
- Notes are detected as duplicates based on content hash (note type + field values)
- Duplicate notes are skipped during import
- Cards from duplicate notes are remapped to existing notes

#### ID Remapping
- All UUIDs are regenerated during import
- ID mapping is returned in the response for reference
- Foreign key relationships are automatically updated

#### Deck & Note Type Reuse
- Decks with matching names are reused (not duplicated)
- Note types with matching names and field structures are reused
- Use `X-NoteType-Mapping` header for explicit note type mapping

#### Import Response
```json
{
  "summary": {
    "created": {
      "decks": 1,
      "noteTypes": 0,
      "notes": 42,
      "cards": 55,
      "mediaLinked": 38
    },
    "updated": {
      "notes": 0,
      "cards": 0
    },
    "skipped": {
      "duplicateNotes": 4
    }
  },
  "idMapping": {
    "old-note-id": "new-note-id",
    ...
  },
  "warnings": [
    {
      "code": "INVALID_MEDIA_REF",
      "detail": "filename foo.png not found"
    }
  ]
}
```

### APKG Import (MVP - Coming Soon)
Basic APKG import support is planned for future releases. This will allow importing Anki decks (.apkg files) with Basic and Cloze note types.

## Review Quality Scale

- **0**: Complete blackout - no recollection
- **1**: Incorrect response, but correct answer seemed familiar
- **2**: Incorrect response, correct answer recalled with difficulty
- **3**: Correct response, but with some difficulty
- **4**: Perfect response with ease

## Development

### Running Tests
```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=SchedulerServiceTest

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

### Build
```bash
# Package the application
./mvnw clean package

# Run the packaged JAR
java -jar target/memospace-0.0.1-SNAPSHOT.jar
```

### Profiles

- **Default**: In-memory H2 database
- **local**: File-based H2 database (persists data in `./.data/memospace`)

## Architecture

Memospace implements a **three-layer hexagonal architecture** enhanced with **CQRS** and **Domain-Driven Design (DDD)** patterns.

### 1. Domain Layer (Core Business Logic)

The domain layer is **framework-agnostic** with no dependencies on Spring, JPA, or other infrastructure concerns.

#### Bounded Contexts
Domain is organized into bounded contexts with clear responsibilities:

- **Authoring**: Note types, notes, card templates, and card generation
- **Learning**: Card scheduling state and SM-2 algorithm
- **Collection**: Deck hierarchy and organization
- **Review**: Immutable review history (ReviewLog)
- **Media**: Content-addressed media asset storage
- **Search**: Query language and filtering
- **Import/Export**: Format translation with Anti-Corruption Layer

See `docs/context-map.md` for complete context map and relationships.

#### DDD Tactical Patterns

**Aggregates** (enforce business invariants):
- `NoteAggregate`: Validates fields, normalizes tags, publishes domain events
- `CardAggregate`: Enforces state transitions (NEW → LEARNING → REVIEW), manages scheduling
- `DeckAggregate`: Validates hierarchy (`::`-separated names)
- `ReviewLogAggregate`: Immutable audit log
- `MediaAssetAggregate`: Validates MIME types and file sizes

**Value Objects** (type-safe domain primitives):
- **IDs**: `NoteId`, `NoteTypeId`, `CardId`, `DeckId` (prevent ID mixing)
- **Learning**: `Ease` (≥1.3), `Interval` (≥1 day), `Quality` (0-4), `DueDate`
- **Authoring**: `FieldName`, `FieldValue`, `TemplateName`, `Tag`
- **Common**: `AuditTrail`, domain event base classes

**Domain Events** (cross-context communication):
- `NoteCreated`, `NoteUpdated`, `CardsRegenerated`
- `CardReviewed`, `CardLapsed`, `CardGraduated`
- `DeckCreated`, `DeckDeleted`
- `MediaUploaded`, `MediaReferenced`

**Domain Services**:
- `SchedulerService`: SM-2 spaced repetition algorithm
- `FilteredDeckService`: Query language parsing and evaluation
- `ImportService`, `ExportService`: JSON import/export with deduplication
- `QueryLanguageService`: Parse search queries

**Repository Ports**: Define contracts for persistence (implemented by adapters)

### 2. Application Layer (CQRS)

Orchestrates domain logic using Command Query Responsibility Segregation:

- **Commands**: Write operations (Create, Update, Delete)
- **Queries**: Read operations (Get, Search)
- **Handlers**: Execute commands/queries with transaction management
  - Load aggregates from repositories
  - Call aggregate behavior methods
  - Pull and publish domain events via `DomainEventBus`
  - Save aggregates
- **Command Bus & Query Bus**: Route requests to appropriate handlers

**Event-Driven Coordination**: Application handlers subscribe to domain events to coordinate behavior across bounded contexts.

### 3. Infrastructure/Adapter Layer

Framework-specific implementations:

**Web Adapters** (`adapter/web/`):
- REST controllers route HTTP requests to CommandBus/QueryBus
- DTOs handle API contracts with validation annotations
- Mappers convert between domain aggregates and DTOs
- Global exception handler for consistent error responses

**Persistence Adapters** (`adapter/persistence/`):
- JPA entities (separate from domain models)
- Spring Data JPA repositories
- Repository adapters implement domain ports
- Mappers convert between domain aggregates and JPA entities

**Event Adapters** (`adapter/event/`):
- `InMemoryDomainEventBus`: Synchronous event pub/sub
- Future: Outbox pattern or message queue adapters

**Storage Adapters** (`adapter/storage/`):
- `FilesystemMediaStorageAdapter`: Content-addressed storage (SHA-256)

**Template Adapters** (`adapter/template/`):
- `TemplateEngineAdapter`: Mustache-style card rendering

### Key Architectural Principles

- **Dependency Inversion**: Domain layer has no outward dependencies
- **Separation of Concerns**: Bounded contexts isolate domain areas
- **Type Safety**: Value objects prevent primitive obsession
- **Invariant Protection**: Aggregates guard business rules
- **Event-Driven Design**: Contexts communicate via domain events
- **CQRS**: Separate models for reads and writes
- **Hexagonal Ports**: Domain defines contracts, adapters implement them

## Project Structure

```
src/main/java/org/project/memospace/
├── domain/
│   ├── common/              # Shared DDD building blocks
│   │   ├── value/           # Common value objects (IDs, Tag, AuditTrail)
│   │   ├── event/           # Domain event infrastructure
│   │   ├── port/            # DomainEventBus port
│   │   └── BoundedContext.java
│   ├── authoring/           # Authoring bounded context
│   │   ├── aggregate/       # NoteAggregate, NoteTypeAggregate
│   │   ├── value/           # FieldName, FieldValue, TemplateName
│   │   ├── event/           # NoteCreated, NoteUpdated, CardsRegenerated
│   │   ├── port/            # Repository ports
│   │   └── service/         # Domain services
│   ├── learning/            # Learning/Scheduling bounded context
│   │   ├── aggregate/       # CardAggregate (in progress)
│   │   ├── value/           # Ease, Interval, Quality, DueDate
│   │   ├── event/           # CardReviewed, CardLapsed, CardGraduated
│   │   ├── port/            # Repository ports
│   │   └── service/         # SchedulerService
│   ├── collection/          # Collection bounded context
│   │   └── aggregate/       # DeckAggregate (planned)
│   ├── review/              # Review bounded context
│   │   └── aggregate/       # ReviewLogAggregate (planned)
│   ├── media/               # Media bounded context
│   │   └── aggregate/       # MediaAssetAggregate (planned)
│   ├── search/              # Search/Browser bounded context
│   │   ├── spec/            # QuerySpec, Specifications
│   │   └── service/         # QueryLanguageService
│   ├── importexport/        # Import/Export bounded context
│   │   ├── service/         # ImportService, ExportService
│   │   └── acl/             # Anti-Corruption Layer (planned)
│   ├── model/               # Legacy domain models (gradual migration)
│   ├── service/             # Legacy domain services
│   ├── port/                # Legacy repository ports
│   └── exception/           # Domain exceptions
├── application/
│   └── service/
│       ├── command/         # Command definitions (write operations)
│       ├── query/           # Query definitions (read operations)
│       ├── handler/         # Command and query handlers
│       ├── impl/            # CommandBus and QueryBus implementations
│       └── config/          # Application configuration
├── adapter/
│   ├── web/
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── mapper/          # Domain ↔ DTO mappers
│   │   └── exception/       # Global exception handler
│   ├── persistence/
│   │   ├── jpa/
│   │   │   ├── entity/      # JPA entities
│   │   │   ├── repository/  # Spring Data repositories
│   │   │   └── mapper/      # JPA entity ↔ Domain mappers
│   │   └── stats/           # Stats read repositories
│   ├── event/               # Event bus adapters
│   │   └── InMemoryDomainEventBus.java
│   ├── storage/             # File storage adapters
│   └── template/            # Template engine adapters
└── config/                  # Spring Boot configuration

src/test/java/
├── domain/                  # Unit tests for domain logic
│   ├── authoring/           # Authoring context tests
│   └── learning/            # Learning context tests
└── integration/             # End-to-end integration tests

docs/                        # Project documentation
├── context-map.md           # DDD bounded contexts and relationships
├── decision-records/
│   └── adr-001-ddd-introduction.md
└── ddd-implementation-progress.md
```

### Migration Notes

Memospace is **gradually migrating** to DDD patterns:
- **New aggregates** coexist with legacy models during transition
- **Mappers** bridge old and new domain models
- **New features** use DDD patterns immediately
- **Existing features** migrate incrementally (use case by use case)

See `CLAUDE.md` for detailed DDD contribution guidelines and `docs/ddd-implementation-progress.md` for migration status.

## Built With

- **Java 21** - Programming language
- **Spring Boot 3.5.6** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Validation** - Input validation
- **H2 Database** - In-memory/file database
- **Springdoc OpenAPI 2.7.0** - API documentation (Swagger UI)
- **Lombok** - Boilerplate reduction
- **JUnit 5** - Testing framework
- **Maven** - Build tool

## Documentation

- **`README.md`** - This file (quick start and API overview)
- **`CLAUDE.md`** - Developer guide for AI-assisted development (architecture, patterns, DDD guidelines)
- **`docs/context-map.md`** - DDD bounded contexts, relationships, and ubiquitous language
- **`docs/decision-records/adr-001-ddd-introduction.md`** - Rationale for DDD adoption
- **`docs/ddd-implementation-progress.md`** - Current DDD implementation status and roadmap

## Contributing

When contributing to Memospace, please follow the DDD patterns:

1. **Identify bounded context** for your feature (Authoring, Learning, Collection, etc.)
2. **Create value objects** for domain concepts (IDs, constraints, business logic)
3. **Update/create aggregates** with behavior methods that enforce invariants
4. **Define domain events** for significant state changes
5. **Update repository ports** to accept value object IDs and specifications
6. **Implement handlers** that orchestrate aggregates and publish events
7. **Write tests**: Unit tests for aggregates/value objects, integration tests for use cases

See `CLAUDE.md` for detailed contribution guidelines and architectural patterns.

## License

This project is licensed under the MIT License.

## Acknowledgments

- **SM-2 Algorithm**: Based on SuperMemo's spaced repetition research
- **Anki**: Inspiration for note-based flashcard system and media handling
- **Domain-Driven Design**: Eric Evans and Vaughn Vernon's foundational work