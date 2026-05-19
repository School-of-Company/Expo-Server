# Feature Implementation Skill

## When to Use

When implementing any new API endpoint, domain method, or business logic.

## Pre-Work Checklist

- [ ] On a feature branch (not `main`, `master`, `develop`)
- [ ] `git status` — no uncommitted changes
- [ ] Requirements are fully understood (ask if unclear)

## Steps

1. Read similar existing domain code for patterns
2. Write Exception class
3. Modify or create Entity
4. Write DTOs (Request, Response)
5. Write Repository interface + QueryDSL impl (if needed)
6. Declare Service interface
7. Implement ServiceImpl
8. Write Controller
9. Write Event + EventHandler (if needed)
10. `./gradlew compileJava` — must pass before finishing

## Done Criteria

- `./gradlew compileJava` succeeds
- `git diff --stat` reviewed — no unintended file changes
