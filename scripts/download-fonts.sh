#!/usr/bin/env bash
set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
main_root="$(git worktree list --porcelain | awk '
  /^worktree / { path = substr($0, 10) }
  $1 == "branch" && $2 == "refs/heads/main" { print path; exit }
')"

if [[ -z "$main_root" ]]; then
  echo "Unable to find the main-branch worktree." >&2
  exit 1
fi

relative_font_dir="markard/src/commonMain/composeResources/font"
main_font_dir="$main_root/$relative_font_dir"
target_font_dir="$repo_root/$relative_font_dir"
font_files=(
  noto_sans_cjk_sc.ttf
  lxgw_wenkai_regular.ttf
  noto_serif_cjk_sc.ttf
  sarasa_gothic_sc_regular.ttf
)

download_file() {
  local url="$1"
  local destination="$2"
  local temporary
  temporary="$(mktemp "${destination}.XXXXXX")"
  if curl -L --fail --show-error "$url" -o "$temporary"; then
    mv "$temporary" "$destination"
  else
    rm -f "$temporary"
    return 1
  fi
}

ensure_main_fonts() {
  mkdir -p "$main_font_dir"

  if [[ ! -s "$main_font_dir/noto_sans_cjk_sc.ttf" ]]; then
    download_file \
      'https://github.com/notofonts/noto-cjk/raw/main/Sans/Variable/TTF/Subset/NotoSansSC-VF.ttf' \
      "$main_font_dir/noto_sans_cjk_sc.ttf"
  fi
  if [[ ! -s "$main_font_dir/lxgw_wenkai_regular.ttf" ]]; then
    download_file \
      'https://raw.githubusercontent.com/lxgw/LxgwWenKai/main/fonts/TTF/LXGWWenKai-Regular.ttf' \
      "$main_font_dir/lxgw_wenkai_regular.ttf"
  fi
  if [[ ! -s "$main_font_dir/noto_serif_cjk_sc.ttf" ]]; then
    download_file \
      'https://github.com/notofonts/noto-cjk/raw/main/Serif/Variable/TTF/Subset/NotoSerifSC-VF.ttf' \
      "$main_font_dir/noto_serif_cjk_sc.ttf"
  fi
  if [[ ! -s "$main_font_dir/sarasa_gothic_sc_regular.ttf" ]]; then
    local archive temporary_font
    archive="$(mktemp "${TMPDIR:-/tmp}/sarasa-gothic.XXXXXX.7z")"
    temporary_font="$(mktemp "$main_font_dir/sarasa_gothic_sc_regular.ttf.XXXXXX")"
    if curl -L --fail --show-error \
      'https://github.com/be5invis/Sarasa-Gothic/releases/download/v1.0.40/SarasaGothicSC-TTF-1.0.40.7z' \
      -o "$archive" && bsdtar -xOf "$archive" SarasaGothicSC-Regular.ttf > "$temporary_font"; then
      mv "$temporary_font" "$main_font_dir/sarasa_gothic_sc_regular.ttf"
    else
      rm -f "$archive" "$temporary_font"
      return 1
    fi
    rm -f "$archive"
  fi
}

ensure_main_fonts

if [[ "$repo_root" != "$main_root" ]]; then
  mkdir -p "$target_font_dir"
  for font_file in "${font_files[@]}"; do
    cp -p "$main_font_dir/$font_file" "$target_font_dir/$font_file"
  done
  echo "Fonts copied from $main_font_dir to $target_font_dir"
else
  echo "Fonts ready in $main_font_dir"
fi
