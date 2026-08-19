# Markard

Markard is a Kotlin Multiplatform + Compose Multiplatform component for rendering Markdown as polished, shareable cards.

## Modules

- `:markard` — reusable library targeting Android, iOS, JVM, and Wasm.
- `:sample-desktop` — JVM Compose playground and showcase app.

## Documentation

- [Architecture](docs/architecture.md) — product boundary, module structure, platform strategy, and v0.1 direction.
- [Architecture decisions](docs/decisions.md) — durable decisions and their rationale.

## Build

```bash
./gradlew build
```

Run the desktop playground with:

```bash
./gradlew :sample-desktop:run
```
