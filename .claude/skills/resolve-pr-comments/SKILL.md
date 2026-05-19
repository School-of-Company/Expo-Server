# PR Review Comment Resolution Skill

## When to Use

Use this skill when a GitHub PR already has review comments and the task is to inspect those comments, judge whether each comment is valid, and apply only valid changes.

Do not use this skill for local self-review before creating a PR.

## Inputs

- PR number
- Review comments from GitHub
- Current branch diff
- Referenced files and surrounding code

## Steps

1. Fetch PR comments:
   ```bash
   gh pr view <number> --comments
   gh api repos/:owner/:repo/pulls/<number>/comments
   gh api repos/:owner/:repo/issues/<number>/comments
   ```
2. For each comment, inspect the referenced file and surrounding code.
3. Classify each comment:
   | Class | Meaning |
   |-------|---------|
   | Valid | The comment points out a real bug, convention violation, missing test, or design issue |
   | Already Resolved | The current code already addresses the comment |
   | Needs Explanation | The code is intentional, but the reason should be explained |
   | Out of Scope | The request is unrelated to this PR and should be handled separately |
   | Invalid | The comment is based on a misunderstanding or incorrect assumption |
4. Apply only `Valid` comments.
5. Keep changes minimal and directly tied to the comment.
6. Run verification:
   ```bash
   ./gradlew compileJava
   ./gradlew test --tests "<RelatedTestClass>"
   ```
7. Reply to each comment on GitHub with the outcome:
   - **Applied**: `[<commit-hash>](https://github.com/<owner>/<repo>/commit/<full-hash>)에서 반영했습니다.`
     ```bash
     HASH=$(git rev-parse --short HEAD)
     FULL=$(git rev-parse HEAD)
     REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
     # body: "[${HASH}](https://github.com/${REPO}/commit/${FULL})에서 반영했습니다."
     ```
   - **Already Resolved**: `현재 코드에 이미 반영되어 있습니다. (<evidence>)`
   - **Explained**: `의도한 구현입니다. <reason>`
   - **Declined / Out of Scope**: `<reason> 이유로 반영하지 않겠습니다.`

   Post each reply using the comment's ID:
   ```bash
   gh api repos/:owner/:repo/pulls/<pr-number>/comments/<comment-id>/replies \
     --method POST \
     -f body="<reply>"
   ```
8. Summarize the response:
   ```md
   ## PR Review Comment Resolution

   ### Applied
   - <comment>: <change>

   ### Already Resolved
   - <comment>: <evidence>

   ### Explained
   - <comment>: <reason>

   ### Declined / Out of Scope
   - <comment>: <reason>
   ```

## Rules

- Do not blindly apply every review comment.
- Always inspect the current code before changing it.
- Do not make unrelated refactors while resolving comments.
- If a comment is valid but too large for this PR, propose a follow-up issue instead of expanding scope.
- If the same review pattern repeats, propose updating `.claude/rules/` or `.claude/skills/`.
