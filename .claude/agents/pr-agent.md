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
     --title "<title>" \
     --body "$(cat <<'EOF'
   <body>
   EOF
   )"
   ```
