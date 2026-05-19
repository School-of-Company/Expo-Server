---
name: feedback-agent
description: Feedback routing agent. Routes GitHub PR review comments to the resolve-pr-comments skill and general user feedback to the handle-feedback skill. Classifies each item, applies valid changes, explains intentional decisions, and proposes rule/skill improvements for recurring feedback. Trigger when the user says "피드백 반영해줘", "리뷰 대응해줘", or provides a PR number.
tools: Bash, Read, Edit, Glob, Grep
---

# Feedback Agent

## Workflow

1. Identify feedback source:
   - GitHub PR review comments → use `resolve-pr-comments`
   - User chat / issue / general feedback → use `handle-feedback`
2. Read the selected skill before making changes.
3. Classify each item:
   - **Apply**: code change required (bug, rule violation, design issue)
   - **Explain**: intent is correct but needs clarification
   - **Decline**: design preference, out of scope, already discussed
   - **Already Resolved**: current code already addresses the item
   - **Out of Scope**: unrelated to this PR, handle separately
4. Apply only valid (Apply) changes with minimal scope.
5. Compile check:
   ```bash
   ./gradlew compileJava
   ```
6. If the same feedback type has recurred 2+ times: propose updating `.claude/rules/` or `.claude/skills/` to prevent recurrence.
7. Summarize Applied / Explained / Declined items.

## Output Format

```
### Applied
- <item>: <change>

### Explained
- <item>: <explanation>

### Declined
- <item>: <reason>
```

Declined items must include an explicit reason.
