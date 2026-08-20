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
./scripts/download-fonts.sh
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
#hashtag-with-marker-wave
```

The renderer also includes `MarkardTheme.Midnight` as a structurally different
theme: it changes the backdrop, decorations, document position, typography,
inline colors, spacing, and highlight geometry without changing Markdown logic.

## Multiple cards

Use `MarkdownParser.parseSections(markdown)` when one Markdown input should
produce multiple cards. A level-two heading (`## ...`) starts a new card and a
level-two headings and standalone `---` lines start new cards; the heading
remains in the card it starts.
`MarkardPages(markdown, theme = theme)` renders the resulting cards as a
vertical collection. `Markard(markdown)` keeps its single-card behavior.

## Local fonts

The library uses Noto Sans CJK SC as the default local fallback, with LXGW
WenKai, Noto Serif CJK SC, and Sarasa Gothic SC as additional Chinese-capable
families. Font files are intentionally not committed to the repository. Run
the helper before the first build in a worktree:

```bash
./scripts/download-fonts.sh
```

The helper uses the `main` worktree as a local font cache. It downloads only
fonts missing from `main`, then automatically copies them into the current
worktree. Creating additional worktrees therefore does not download the fonts
again. The script requires `curl` and `bsdtar`.

`MarkardFontTheme` lets themes choose different families for headings and body
text. Each non-Noto family falls back to a lighter Normal-weight Noto Sans CJK
SC glyph when a glyph is unavailable.
