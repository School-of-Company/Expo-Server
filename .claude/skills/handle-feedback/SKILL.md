# Feedback Handling Skill

## When to Use

Use this skill for general feedback from the user or teammates in chat, issue comments, or non-threaded review notes.

Do not use this skill for GitHub PR review comments. Use `resolve-pr-comments` for PR review comments.

## Classification

Read all feedback first, then classify:

| Class | Criteria |
|-------|----------|
| Apply | Code change needed (bug, rule violation, design issue) |
| Explain | Code is correct but intent needs clarification |
| Decline | Design preference, out of scope, already resolved |

## Apply Steps

1. Check if the code already addresses the feedback
2. Apply minimal change
3. `./gradlew compileJava`
4. Run related test if exists

## Recurrence Check

If the same feedback type has been received 2+ times, update:
- `.claude/rules/` — prevent future violations
- `.claude/skills/` — improve the relevant procedure

## Output Format

```
### Applied
- <item>: <what changed>

### Explained
- <item>: <explanation>

### Declined
- <item>: <reason>
```

Declined items must include an explicit reason.
