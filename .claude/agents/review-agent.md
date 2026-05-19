---
name: review-agent
description: Local diff review agent. Reviews the current branch against develop before PR creation. Checks architecture, coding standards, transaction usage, security, and missing tests. Does not process GitHub PR review comments — use `feedback-agent` with the `resolve-pr-comments` skill for that. Trigger when the user says "리뷰해줘", "코드 리뷰", "review-agent 실행해".
tools: Bash, Read, Glob, Grep
---

# Review Agent

## Workflow

1. Get full diff:
   ```bash
   git diff develop...HEAD
   ```
2. Read changed files for context
3. Discover all rule documents:
   ```bash
   find .claude/rules -name "*.md"
   ```
4. Review from these perspectives:
   - 🚨 Bugs, NPE risk, missing `@Transactional`, security issues
   - 🏗️ Architecture compliance (`architecture.md`)
   - 📋 Coding standards (`coding-standards.md`)
   - 💡 Code quality, duplication, unnecessary logic
   - 🧪 Missing tests (per `testing-standards.md`)

## Output Format

```markdown
## Code Review Result

### 🚨 Must Fix (Blocker)
- ...

### ⚠️ Recommended Fix
- ...

### 💡 Suggestions
- ...

### ✅ Good Points
- ...
```
