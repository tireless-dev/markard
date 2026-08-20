# Repository Instructions

## Git workflow

- 默认使用独立的 Git worktree 开发，不直接修改当前 `main` 工作目录。
- 创建 worktree 后立即在新 worktree 中运行 `./scripts/download-fonts.sh`。脚本会复用 `main` worktree 的字体；若 `main` 尚未下载，则只下载一次后自动复制。
- 完成任务后汇报 worktree 路径、分支名和合并方式。
- 仅在用户明确要求时，才直接修改当前工作目录或 `main` 分支。
