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
^^accent-colored text^^
==bottom-stripe highlight==
#hashtag-with-marker-wave
```

The renderer also includes `MarkardTheme.Midnight` as a structurally different
theme: it changes the backdrop, decorations, document position, typography,
inline colors, spacing, and highlight geometry without changing Markdown logic.

## Local fonts

The library uses Noto Sans CJK SC as the default local fallback, with LXGW
WenKai, Noto Serif CJK SC, and Sarasa Gothic SC as additional Chinese-capable
families. Font files are intentionally not
committed to the repository. Run the initialization step below before the
first build:

```bash
mkdir -p markard/src/commonMain/composeResources/font \
         markard/src/commonMain/composeResources/files

curl -L --fail --show-error \
  'https://github.com/notofonts/noto-cjk/raw/main/Sans/Variable/TTF/Subset/NotoSansSC-VF.ttf' \
  -o markard/src/commonMain/composeResources/font/noto_sans_cjk_sc.ttf
curl -L --fail --show-error \
  'https://raw.githubusercontent.com/lxgw/LxgwWenKai/main/fonts/TTF/LXGWWenKai-Regular.ttf' \
  -o markard/src/commonMain/composeResources/font/lxgw_wenkai_regular.ttf
curl -L --fail --show-error \
  'https://github.com/notofonts/noto-cjk/raw/main/Serif/Variable/TTF/Subset/NotoSerifSC-VF.ttf' \
  -o markard/src/commonMain/composeResources/font/noto_serif_cjk_sc.ttf

# Requires bsdtar (available by default on macOS).
sarasa_archive="$(mktemp "${TMPDIR:-/tmp}/sarasa-gothic.XXXXXX.7z")"
curl -L --fail --show-error \
  'https://github.com/be5invis/Sarasa-Gothic/releases/download/v1.0.40/SarasaGothicSC-TTF-1.0.40.7z' \
  -o "$sarasa_archive"
bsdtar -xOf "$sarasa_archive" SarasaGothicSC-Regular.ttf \
  > markard/src/commonMain/composeResources/font/sarasa_gothic_sc_regular.ttf
rm -f "$sarasa_archive"

curl -L --fail --show-error \
  'https://raw.githubusercontent.com/notofonts/noto-fonts/main/LICENSE' \
  -o markard/src/commonMain/composeResources/files/NOTO_CJK_LICENSE.txt
curl -L --fail --show-error \
  'https://raw.githubusercontent.com/lxgw/LxgwWenKai/main/OFL.txt' \
  -o markard/src/commonMain/composeResources/files/LXGW_WENKAI_OFL.txt
curl -L --fail --show-error \
  'https://raw.githubusercontent.com/be5invis/Sarasa-Gothic/main/LICENSE' \
  -o markard/src/commonMain/composeResources/files/SARASA_GOTHIC_LICENSE.txt
```

`MarkardFontTheme` lets themes choose different families for headings, body
text, `^^accent^^`, and `==highlight==`. Each non-Noto family falls back to a
lighter Normal-weight Noto Sans CJK SC glyph when a glyph is unavailable.
