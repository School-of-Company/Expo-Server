# Expo Server

Spring Boot 3.2.10 / Java 17 / Gradle Kotlin DSL backend for exhibition management.

## Harness Feedback Loop

Every task that modifies repository files should follow this loop:

1. `git status && git diff && git log --oneline -5`
2. Plan minimal changes
3. Apply changes
4. `./gradlew compileJava`
5. Analyze failures and fix
6. `git diff --stat` — confirm scope

## Core Commands

```bash
./gradlew compileJava                                      # fastest check
./gradlew test --tests "team.startup.expo.domain.<domain>.*"
./gradlew build -x test                                    # build, skip tests
```

## Project Structure

```
src/main/java/team/startup/expo/
├── global/          # config, exception, filter, thirdparty (discord, aws)
└── domain/          # expo, training, participant, trainee, application,
                     # attendance, form, survey, sms, alarm, image, admin,
                     # health, excel
```

Each domain: `domain/ dto/ event/ exception/ presentation/ repository/ service/impl/`

## Task Routing

Match your task to an agent + skill before starting:

| Task | Agent | Skill |
|------|-------|-------|
| New feature / API | `feature-agent` | `implement-feature` |
| Bug fix | `fix-agent` | `fix-bug` |
| Test writing | `test-agent` | `write-test` |
| Refactoring | `refactor-agent` | `refactor-safely` |
| Commit | — | `commit` |
| PR creation | `pr-agent` | `write-pr` |
| Local diff review before PR | `review-agent` | `review-local-diff` |
| PR review comments | `feedback-agent` | `resolve-pr-comments` |
| General feedback | `feedback-agent` | `handle-feedback` |

Agents live in `.claude/agents/`. Skills are repository-local procedure documents in `.claude/skills/` — not automatically invoked by Claude Code. Read them when referenced.

## Rule Documents

Read the matching rule before working:

- Architecture & domain structure: `.claude/rules/architecture.md`
- Coding standards & exceptions: `.claude/rules/coding-standards.md`
- Git workflow & commit format: `.claude/rules/git-workflow.md`
- Testing guidelines: `.claude/rules/testing-standards.md`

## Safety Rules

Hooks block most of these automatically — some require discipline:

- Do NOT push to `main`, `master`, or `develop` directly
- Do NOT run `git push --force` or `git reset --hard`
- Do NOT run `rm -rf`; destructive removal must be performed manually by the user.
- Do NOT declare `@Transactional` at class level
- Do NOT use `System.out.println()` — use `@Slf4j`
- Do NOT hardcode secrets or env vars
- Do NOT commit `settings.local.json`, `.env`, `.claude/.logs/`
- Do NOT commit unless the user explicitly asks — use the `commit` skill when asked
- Do not add obvious comments. Add comments only for non-obvious domain constraints.

Do not manually enforce formatting. Use Gradle verification commands instead.

## Response Language

- Explanations to the user: Korean
- Files under `.claude/` (rules, agents, skills): English
- Commit messages and PR titles: Korean — `type :: 한국어 설명`
