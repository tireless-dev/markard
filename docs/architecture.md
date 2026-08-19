# Markard Architecture

**Status:** Active architecture baseline  
**Scope:** v0.1 library and desktop development host  
**Last updated:** 2026-08-19

## Purpose and product boundary

Markard is a Kotlin Multiplatform and Compose Multiplatform component for
rendering Markdown as polished, shareable cards. It is intended to be used like
another Compose UI primitive:

```kotlin
Text()
Image()
Markdown()
Markard()
```

The library owns Markdown semantics, card layout, visual themes, deterministic
Compose rendering, and eventually capture of the rendered card as an
`ImageBitmap`. It is not a Markdown editor or a complete sharing application.

Application concerns remain outside the library:

- text input and editing;
- file opening, saving, drafts, and history;
- navigation and application settings;
- persistence and synchronization;
- clipboard and platform share/download flows;
- publishing workflows and social integrations.

The governing boundary is:

```text
Markdown string
    ↓
Parse
    ↓
MarkardDocument
    ↓
Theme
    ↓
Compose rendering
    ↓
ImageBitmap capture (planned)
```

## Repository structure and dependency direction

The repository contains one reusable library and one development host:

```text
markard/
├── markard/              # Reusable KMP / Compose Multiplatform component
├── sample-desktop/       # Desktop playground and showcase app
├── docs/                 # Architecture and decision records
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```

The dependency direction is one-way:

```text
sample-desktop → markard
```

`markard` must never depend on `sample-desktop`. The desktop application is a
development and demonstration host, not the product boundary.

Keep the library as one Gradle module for v0.1. Use package boundaries before
introducing modules such as `markard-core`, `markard-compose`, or
`markard-export`; split only when implementation pressure or reuse justifies it.

## Platform strategy

The library targets Android, iOS, JVM/Desktop, and Web/Wasm. The configured
targets are:

```text
Android
iOS ARM64
iOS Simulator ARM64
JVM
Wasm browser
```

Wasm is the initial web target. Do not add Kotlin/JS merely for completeness;
add it only if there is a concrete requirement to publish a JavaScript-consumable
package.

Dependencies in `commonMain` must be compatible with every supported target.
Prefer a KMP-compatible Markdown parser or the small internal parser when the
supported Markdown subset does not justify a dependency. Avoid JVM-only APIs in
shared code.

## Internal architecture

The current package-level separation is sufficient for v0.1:

```text
dev.tireless.markard
├── parser/   MarkdownParser.kt
├── model/    MarkardDocument.kt
├── theme/    MarkardTheme.kt
└── Markard.kt
```

Responsibilities are separated even though they live in one module:

1. `parser` converts Markdown text into the internal document model.
2. `model` represents block and inline semantics without Compose types.
3. `theme` supplies card geometry, colors, and typography.
4. `Markard.kt` maps the model and theme to Compose UI.
5. A future `capture` package may expose capture state without creating a second
   rendering system.

The parser and renderer must remain separable. Rendering directly from raw
parser tokens would make Markdown handling and Compose layout harder to evolve
independently.

## Markdown model and supported subset

The initial model is deliberately small:

```text
MarkardDocument
├── Heading(level, content)
├── Paragraph(content)
├── Quote(content)
└── UnorderedList(items)

Inline
├── Text
├── Strong
├── Emphasis
└── Code
```

The v0.1 parsing target is:

```markdown
# Heading 1
## Heading 2

Paragraph

**Bold**
*Italic*
`Inline code`

> Quote

- List item
- List item
```

Full CommonMark compatibility is not a v0.1 goal. Tables, footnotes, raw HTML,
embedded media, complex nested lists, task lists, images, and custom extensions
may be considered later if they support the card use case.

## Rendering and layout

Markard renders a card, not a generic scrollable Markdown document. The
preferred layout is a configured or theme-defined width with content-driven
height:

```text
fixed/configured width + content-driven height = predictable card bounds
```

Rendering should stay deterministic and Compose-native. Image capture, when
implemented, must reuse the same composable representation shown in the preview:

```text
Markard composable → GraphicsLayer → ImageBitmap
```

There must not be a separate image renderer. The invariant is:

> What the user sees in Compose is what gets captured.

If a target requires platform-specific capture code, isolate only that minimum
difference in its platform source set.

## Themes

Themes are a first-class capability, not only color presets. A theme may control
card width, padding, background, corner radius, typography, heading scale and
spacing, paragraph spacing, list markers, quote treatment, code treatment,
density, decorative elements, and footer treatment.

The first differentiated themes are `Default` and `Minimal`. Additional themes
such as Editorial, Paper, Dark, Terminal, Quote, Poster, WeChat, or Xiaohongshu
are future options, not v0.1 commitments. The API should allow custom themes
without requiring consumers to fork the renderer.

## Public API direction

Keep the public API small and idiomatic for Compose. The current entry point is:

```kotlin
@Composable
fun Markard(
    markdown: String,
    modifier: Modifier = Modifier,
    theme: MarkardTheme = MarkardTheme.Default,
)
```

Capture state is a direction, not a frozen contract:

```kotlin
@Composable
fun rememberMarkardState(): MarkardState

suspend fun MarkardState.capture(): ImageBitmap
```

The exact capture API should be fixed only after implementation evidence from
the supported targets. Platform consumers own saving and sharing:

```text
Android: ImageBitmap → share/save
iOS:     ImageBitmap → share/save
Desktop: ImageBitmap → file
Web:     ImageBitmap → Blob/download
```

## Desktop development host

`sample-desktop` is the primary v0.1 development host. It should provide a
small, utilitarian playground for live Markdown input, theme switching, card
preview, capture/export experiments, visual regression checks, and library
demonstration.

Editing belongs only to this application. It must not leak into the `markard`
API or create application-level persistence in the library.

## Current implementation and planned milestones

Implemented in the repository today:

- KMP/Compose library module with Android, iOS, JVM, and Wasm configuration;
- `MarkardDocument` block and inline model;
- internal Markdown parser for the initial subset;
- Compose renderer for headings, paragraphs, quotes, lists, and inline styles;
- `Default` and `Minimal` themes;
- desktop sample that renders a fixed example and consumes the library;
- common parser tests.

Next milestones:

1. Improve the desktop playground with editable Markdown, live preview, and a
   theme selector.
2. Validate the initial Markdown subset with common tests and edge cases.
3. Investigate shared capture through `GraphicsLayer` and `ImageBitmap`.
4. Demonstrate capture/export in the desktop host without moving persistence or
   sharing into the library.
5. Keep Android, iOS, JVM, and Wasm compilation green as dependencies evolve.

## Non-goals for v0.1

Do not add full CommonMark support, a rich editor, mobile or web applications,
cloud synchronization, a template marketplace, authentication, storage, social
publishing, AI generation, or server-side rendering.

## Definition of done

The v0.1 architecture is realized when:

- `markard` is reusable and application-independent;
- Android, iOS, JVM, and Wasm targets compile;
- the desktop sample runs and demonstrates live Markdown rendering;
- the initial Markdown subset and at least two themes work;
- the rendered result can be captured as `ImageBitmap` or an equivalent
  Compose-native image representation;
- the desktop sample can demonstrate capture/export;
- editor, storage, sharing, and application-specific behavior remain outside
  the library.

## Related records

- [Architecture decisions](decisions.md)
