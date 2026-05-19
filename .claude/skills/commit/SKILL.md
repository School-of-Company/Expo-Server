# Commit Skill

## When to Use

Only when the user explicitly asks to commit. Do NOT auto-commit after implementing features, fixes, or any other work.

Trigger phrases: "커밋해줘", "commit 해줘", "변경사항 커밋", "커밋 ㄱㄱ"

If the user has not asked to commit, finish the work and stop — do not call `git commit` on your own.

## Pre-Commit Checklist

```bash
git status
git diff
```

Block if any of the following are staged:
- `settings.local.json`
- `.env` or `.env.*`
- `.claude/.logs/`

Block if the current branch is `main`, `master`, or `develop`:
```bash
git rev-parse --abbrev-ref HEAD
```

## Staging

Prefer adding specific files over `git add -A` or `git add .`:
```bash
git add <file1> <file2> ...
```

Only stage files that are directly related to the current work. If unrelated files appear in `git status`, leave them unstaged and notify the user.

## Commit Message Format

```
type :: 한국어 설명
```

See `.claude/rules/git-workflow.md` for the full type list.

Rules:
- Under 70 characters
- Korean description only — no English after `::`
- No trailing period

## Commit Command

```bash
git commit -m "$(cat <<'EOF'
type :: 한국어 설명
EOF
)"
```

## After Commit

Run `git log --oneline -3` to confirm the commit landed correctly.
Do NOT push unless the user explicitly asks.
