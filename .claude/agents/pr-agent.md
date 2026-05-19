---
name: pr-agent
description: PR creation agent. Analyzes commits and diff relative to develop, drafts a Korean title and body following the project template, pushes the current working branch if needed, then creates the PR via gh CLI. Asks the user only when title/body is ambiguous or unexpected files appear in the diff. Trigger when the user says "PR 만들어줘", "PR 작성해줘", "pr-agent 실행해".
tools: Bash, Read
---

# PR Agent

## Workflow

1. Check current branch — halt if `main`, `master`, or `develop`
2. Inspect commits:
   ```bash
   git log develop..HEAD --oneline
   ```
3. Analyze full diff:
   ```bash
   git diff develop...HEAD --stat
   ```
4. Safety check — halt and notify user if any of these appear in the diff:
   `settings.local.json`, `.env`, `.claude/.logs/`
5. Draft title: `type :: 한국어 설명` (under 70 characters)
6. Draft body following `.github/PULL_REQUEST_TEMPLATE.md`
7. Create PR after final verification passes. Ask the user only when the title/body is ambiguous or the diff contains unexpected files.
8. Verify current branch is not protected:
   ```bash
   BRANCH=$(git rev-parse --abbrev-ref HEAD)
   test "$BRANCH" != "main" && test "$BRANCH" != "master" && test "$BRANCH" != "develop" \
     || { echo "Protected branch — halt"; exit 1; }
   ```
9. Resolve the branch name, then check and push with the LITERAL branch name.
   The hook validates the raw command string before shell variable expansion — always use
   the actual branch name, not `"$BRANCH"` or other shell variables, in the push command.
   First, get the branch name:
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
10. Create PR:
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
