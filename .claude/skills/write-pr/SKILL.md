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

Follow `.github/PULL_REQUEST_TEMPLATE.md` exactly:

```markdown
## 💡 배경 및 개요

> PR을 하게 된 문제상황, 배경 및 개요에 대해서 작성해주세요!

Resolves: #{이슈번호}

## 📃 작업내용

> PR에서 한 작업을 작성해주세요!

## 🙋‍♂️ 리뷰노트

> 구현 시에 고민이었던 점들 혹은 특정 부분에 대한 의도가 있었다면 PR 리뷰의 이해를 돕기 위해 서술해주세요!

## ✅ PR 체크리스트

- [ ] 이 작업으로 인해 변경이 필요한 문서가 변경되었나요? (e.g. `.env`, `노션`, `README`)
- [ ] 이 작업을 하고나서 공유해야할 팀원들에게 공유되었나요?
- [ ] 작업한 코드가 정상적으로 동작하나요?
- [ ] Merge 대상 브랜치가 올바른가요?
- [ ] PR과 관련 없는 작업이 있지는 않나요?

## 🎸 기타
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
## 💡 배경 및 개요

<배경 설명>

Resolves: #{이슈번호}

## 📃 작업내용

<작업 내용>

## 🙋‍♂️ 리뷰노트

<리뷰 노트>

## ✅ PR 체크리스트

- [ ] 이 작업으로 인해 변경이 필요한 문서가 변경되었나요? (e.g. `.env`, `노션`, `README`)
- [ ] 이 작업을 하고나서 공유해야할 팀원들에게 공유되었나요?
- [ ] 작업한 코드가 정상적으로 동작하나요?
- [ ] Merge 대상 브랜치가 올바른가요?
- [ ] PR과 관련 없는 작업이 있지는 않나요?

## 🎸 기타
EOF
)"
```
