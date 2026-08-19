#!/usr/bin/env bash
set -euo pipefail

font_dir="markard/src/commonMain/composeResources/font"
license_dir="markard/src/commonMain/composeResources/files"
mkdir -p "$font_dir" "$license_dir"

curl -L --fail --show-error \
  'https://github.com/notofonts/noto-cjk/raw/main/Sans/Variable/TTF/Subset/NotoSansSC-VF.ttf' \
  -o "$font_dir/noto_sans_cjk_sc.ttf"
curl -L --fail --show-error \
  'https://raw.githubusercontent.com/lxgw/LxgwWenKai/main/fonts/TTF/LXGWWenKai-Regular.ttf' \
  -o "$font_dir/lxgw_wenkai_regular.ttf"
curl -L --fail --show-error \
  'https://github.com/notofonts/noto-cjk/raw/main/Serif/Variable/TTF/Subset/NotoSerifSC-VF.ttf' \
  -o "$font_dir/noto_serif_cjk_sc.ttf"

sarasa_archive="$(mktemp "${TMPDIR:-/tmp}/sarasa-gothic.XXXXXX.7z")"
curl -L --fail --show-error \
  'https://github.com/be5invis/Sarasa-Gothic/releases/download/v1.0.40/SarasaGothicSC-TTF-1.0.40.7z' \
  -o "$sarasa_archive"
bsdtar -xOf "$sarasa_archive" SarasaGothicSC-Regular.ttf \
  > "$font_dir/sarasa_gothic_sc_regular.ttf"
rm -f "$sarasa_archive"

curl -L --fail --show-error \
  'https://raw.githubusercontent.com/notofonts/noto-fonts/main/LICENSE' \
  -o "$license_dir/NOTO_CJK_LICENSE.txt"
curl -L --fail --show-error \
  'https://raw.githubusercontent.com/lxgw/LxgwWenKai/main/OFL.txt' \
  -o "$license_dir/LXGW_WENKAI_OFL.txt"
curl -L --fail --show-error \
  'https://raw.githubusercontent.com/be5invis/Sarasa-Gothic/main/LICENSE' \
  -o "$license_dir/SARASA_GOTHIC_LICENSE.txt"

echo "Fonts downloaded to $font_dir"
