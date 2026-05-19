# Local Diff Review Skill

## When to Use

Before creating any PR. Use this skill to self-review the current branch diff and catch issues before requesting a code review.

Do NOT use this skill for GitHub PR review comment resolution — use `resolve-pr-comments` for that.

## Steps

```bash
git diff develop...HEAD
```

Read all changed files in full.

## Checklist

- [ ] Architecture rules followed (`.claude/rules/architecture.md`)
- [ ] `@Transactional` declared at method level only (no class-level)
- [ ] No NPE risk
- [ ] No hardcoded secrets or environment variables
- [ ] No `System.out.println()` — `@Slf4j` used instead
- [ ] No obvious comments
- [ ] No `settings.local.json`, `.env`, `.claude/.logs/` included
- [ ] Dependency direction: Controller → Service interface → Repository
- [ ] Compile: `./gradlew compileJava`
- [ ] Tests added where required by `testing-standards.md`
- [ ] All TODOs resolved

Fix all issues before creating the PR. If a critical issue cannot be fixed in this PR, file a follow-up issue and reference it in the PR description.
