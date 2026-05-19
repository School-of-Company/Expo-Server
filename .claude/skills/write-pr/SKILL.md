# PR Writing Skill

## When to Use

When creating a pull request for a completed feature, fix, or refactor.

## Pre-Check

```bash
git log develop..HEAD --oneline   # commit list
git diff develop...HEAD --stat    # changed files
git status                         # no uncommitted changes
```

Halt if any of these appear in the diff: `settings.local.json`, `.env`, `.claude/.logs/`

## PR Body Format

```markdown
## 개요
<한 문장 요약>

## 작업 내용
- <항목 1>
- <항목 2>

## 변경 파일
- **추가**: <새 파일>
- **수정**: <변경 파일>

## 테스트 체크리스트
- [ ] 컴파일 확인
- [ ] 로컬 실행 확인
- [ ] 관련 테스트 통과
```

## Remote Branch Check

Before running `gh pr create`, confirm the branch exists on remote.
Do NOT push if the current branch is `main`, `master`, or `develop` — halt instead.

The hook validates the raw command string before shell variable expansion.
Always use the LITERAL branch name, not `"$BRANCH"` or other variables, in the push command.

Get the branch name first:
```bash
git rev-parse --abbrev-ref HEAD
```

Then use the literal branch name returned above. Example:
```bash
git ls-remote --heads origin chore/371-claude-code-harness
git push -u origin chore/371-claude-code-harness
```

Do not run placeholder commands like `git push -u origin <current-branch>`. Always replace the placeholder with the literal branch name returned by `git rev-parse --abbrev-ref HEAD`.

Do not combine `git push` with `||`, `&&`, subshells, or grouped commands.

## Create Command

```bash
gh pr create --base develop \
  --title "type :: 한국어 설명" \
  --body "$(cat <<'EOF'
<body>
EOF
)"
```
