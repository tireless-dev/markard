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

The playground focuses on one output: a fixed 3:4 Xiaohongshu-style text card
with a live raw-Markdown editor. `MarkardTheme.Xiaohongshu` uses poster-scale
typography, theme-owned decorations, and high-contrast inline styles.

Inline Markdown semantics are rendered by the active theme:

```markdown
**strong with a theme-defined color**
*theme-defined emphasis*
^^accent-colored text^^
==bottom-stripe highlight==
#hashtag-with-marker-wave
```

The renderer also includes `MarkardTheme.Midnight` as a structurally different
theme: it changes the backdrop, decorations, document position, typography,
inline colors, spacing, and highlight geometry without changing Markdown logic.
