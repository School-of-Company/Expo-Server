# Safe Refactoring Skill

## When to Use

When improving code structure without changing behavior.

## Principle

Tests are the evidence of behavior preservation. One change at a time.

## Steps

1. Clarify the goal (remove duplication? separate concerns? simplify?)
2. Establish baseline:
   ```bash
   ./gradlew compileJava && ./gradlew test 2>&1 | tail -5
   ```
3. Apply one structural change
4. Compile: `./gradlew compileJava`
5. Run tests: `./gradlew test`
   - Pass → proceed to next change
   - Fail → analyze and revert before continuing
6. Inspect scope: `git diff` — confirm no behavior changes mixed in

## Commit Rule

```bash
git commit -m "refactor :: 한국어 설명"
```

Never mix behavior changes and refactoring in one commit.
