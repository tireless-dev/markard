# Architecture Decisions

This journal records durable choices that shape Markard’s boundaries. The
current-state summary lives in [architecture.md](architecture.md); rationale
and supersession history belong here.

## ADR-0001: Keep Markard as a single KMP Compose library for v0.1

**Status:** Accepted  
**Date:** 2026-08-19

### Context

Markard needs shared Markdown parsing, a document model, themed Compose
rendering, and eventual image capture across Android, iOS, JVM, and Wasm. The
project is deliberately small and the desktop sample is the primary development
host.

### Decision

Keep the reusable implementation in `:markard`, with package-level boundaries
for parser, model, theme, renderer, and future capture code. Keep
`:sample-desktop` as a consumer and development host. Do not split the library
into multiple Gradle modules until reuse or implementation pressure demonstrates
that the split is valuable.

### Consequences

Shared behavior stays easy to test and remains portable. The library must avoid
application concerns and target-incompatible dependencies. A later module split
is allowed, but it must be recorded as a new decision.

### Verification

`./gradlew build` must continue to compile the library and its configured
platform targets, and `:sample-desktop` must depend on—not be depended on by—
`:markard`.

## ADR-0002: Use one Compose rendering pipeline for preview and capture

**Status:** Accepted  
**Date:** 2026-08-19

### Context

Markard’s value depends on predictable, shareable card images. Separate preview
and export renderers would allow visual drift and duplicate layout logic.

### Decision

Capture the rendered Compose representation through a Compose-native mechanism,
with `GraphicsLayer` to `ImageBitmap` as the preferred direction. The library
may expose an `ImageBitmap`, while each consumer owns platform-specific saving,
sharing, or downloading.

### Consequences

Preview and exported output remain visually aligned. Capture API details stay
open until supported-target experiments establish a stable contract. Any
platform-specific implementation must be the smallest necessary adapter rather
than a second renderer.

### Verification

The desktop sample must be able to capture the same rendered card it displays;
platform persistence and sharing must remain outside `:markard`.
