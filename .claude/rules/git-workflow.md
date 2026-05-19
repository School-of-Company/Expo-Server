# Git Workflow Rules

## Protected Branches

Direct commit or push to `main`, `master`, or `develop` is strictly forbidden.
When detected, halt immediately and guide the user to create a feature branch.

## Branch Strategy

```
master ← develop ← feat/<name>
                  ← fix/<name>
                  ← refactor/<name>
                  ← chore/<name>
                  ← docs/<name>
```

All branches are created from `develop`. `<name>`: lowercase kebab-case.

## Commit Convention

Format: `type :: 한국어 설명`

| type | usage |
|------|-------|
| `add` | new feature |
| `update` | modify existing feature |
| `fix` | bug fix |
| `refactor` | structural improvement, no behavior change |
| `test` | add or update tests |
| `docs` | documentation |
| `chore` | build or config changes |
| `merge` | merge branch |

Examples:
- `add :: 박람회 목록 조회 API 추가`
- `fix :: 참가자 중복 등록 오류 수정`

## PR Rules

- Base branch: always `develop`
- Title: same format as commit, under 70 characters
- Never include `settings.local.json`, `.env`, `.claude/.logs/` in a PR

## gh CLI Usage

Allowed:
- `gh api` GET — read-only queries (PR info, comments, review comments)
- `gh api --method POST` — creating comments or replies on PRs/issues
- `gh pr create`, `gh pr view`, `gh pr list`, `gh pr checks`

Forbidden:
- `gh api --method PATCH` / `PUT` — modifying existing resources
- `gh api --method DELETE` — deleting resources

## Push Policy

Claude Code may push only the current working branch. An explicit branch target is required.
Protected branches, force push, compound push, and push without an explicit branch are forbidden.

Allowed (use the literal branch name, not a placeholder):
```bash
git push -u origin YOUR_ACTUAL_BRANCH_NAME
git push origin YOUR_ACTUAL_BRANCH_NAME
```

Forbidden:
```bash
git push --force
git push -f
git push origin                              # missing explicit branch target
git push origin main
git push origin master
git push origin develop
git push origin HEAD:main
git push origin HEAD:master
git push origin HEAD:develop
git status && git push -u origin my-branch  # compound push
```

Do not run placeholder commands like `git push -u origin <current-branch>`. The hook blocks any command matching `<current-branch>` or `<branch>` patterns.

Before pushing, always verify:
```bash
git rev-parse --abbrev-ref HEAD   # must not be main, master, or develop
git status                          # confirm intended changes
```

The PreToolUse hook enforces all push safety checks automatically.

Settings permissions provide coarse restrictions. The authoritative push safety policy is enforced by `.claude/hooks/preToolUse.sh`.
