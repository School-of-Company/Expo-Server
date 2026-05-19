# Harness Changelog

## 2026-05-19 — Initial Harness Setup (#371)

### Added

**CLAUDE.md** (~100 lines): Harness entry point (router). Defines feedback loop, task routing table, rule document index, and safety rules.

### Design Reference

- https://news.hada.io/topic?id=24744 — CLAUDE.md should be a routing entry point, not a rule dump. Progressive Disclosure: keep CLAUDE.md ~100 lines; push detail to `.claude/` subdirectories.
- Reference repos: `themoment-team/datagsm-server` (Spring/Kotlin harness), `School-of-Company/Gwangju-talent-festival-Client` (frontend harness)

**settings.json**: Permission allow/deny list (docker compose V1+V2, jq, gh CLI, gradlew, `git ls-remote`) + PreToolUse, PostToolUse (Edit/Write separated), Stop hooks. Relative paths only.

**Hooks:**
- `preToolUse.sh`: Regex-based dangerous command blocking (rm -rf, git push --force, git reset --hard, docker compose down -v, curl|sh). Protected branch detection. Compound `git push` blocking (push embedded in `&&`/`||`/subshells is forbidden; push must be a standalone command). Placeholder branch name blocking (`<current-branch>`, `<branch>`). jq dependency check. Command logging to `.claude/.logs/command.log`.
- `postToolUse.sh`: Lightweight checks — yq YAML syntax (skips if yq absent), .env/.local write warnings. cd to CWD + file existence check before processing.
- `onStop.sh`: Detects changes via `git status --porcelain`. Runs compileJava only when Java/Gradle files changed. Handles renamed files via `sed`. Always outputs `git diff --stat`.

**Rules (4 files):**
- `architecture.md`: Domain package structure, dependency direction, entity rules (ID type follows existing domain pattern — not always `Long`)
- `coding-standards.md`: Forbidden patterns, comments policy, naming, exceptions, transactions
- `git-workflow.md`: Branch strategy, commit convention (`type :: 한국어 설명`), PR rules
- `testing-standards.md`: When tests are required vs optional, JUnit 5 + Mockito template (ID type follows existing repository signature)

**Agents (7 files):** feature, fix, test, refactor, review, pr, feedback

**Skills (8 files, `<name>/SKILL.md` format):** implement-feature, fix-bug, write-test, refactor-safely, write-pr, review-local-diff, resolve-pr-comments, handle-feedback

> Skills are repository-local procedure documents. They are NOT auto-invoked as Claude Code slash commands. Reference via Task Routing table in CLAUDE.md.

### Design Decisions

- Progressive Disclosure: CLAUDE.md is the router; detail lives in `.claude/` subdirectories
- CLAUDE.md target: ~100 lines (prevents context bloat every session)
- PostToolUse: lightweight only (no compileJava) — prevents slowdown on every file edit
- onStop.sh: compileJava only when Java/Gradle files change — skips for markdown-only sessions
- Regex-based blocking in preToolUse.sh (not glob) for reliable pattern matching
- Compound `git push` blocked: push inside `&&`/`||`/subshells bypasses branch safety validation
- Placeholder branch names (`<current-branch>`) blocked: angle brackets cause shell redirection
- `feedback-agent` is a routing agent; direct PR comment processing is in `resolve-pr-comments` skill
- `handle-feedback` is for general chat/issue feedback only; not for GitHub PR review comments
- Entity/Repository ID type: follows existing domain pattern (not always `Long`)
- Test templates use `sampleId` variable to avoid hardcoding `Long` ID assumption
- jq required for hooks; yq optional for YAML checks
- Initial scope: 4 rules, 7 agents, 8 skills — expand via harness-self-improve pattern
