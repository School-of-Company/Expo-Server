# Bug Fix Skill

## When to Use

When fixing any unexpected behavior, error, or regression.

## Steps

1. Confirm reproduction condition — expected vs actual behavior
2. Locate related code:
   ```bash
   grep -rn "<keyword>" src/main/java --include="*.java"
   ```
3. Write a failing test that reproduces the bug (when possible)
4. Confirm test fails (proves reproduction)
5. Fix root cause with minimal change
6. Compile check: `./gradlew compileJava`
7. Run related test: `./gradlew test --tests "<TestClass>"`
8. Inspect scope: `git diff` — change limited to the bug

## Done Criteria

- Compile succeeds
- Reproducing test passes (or absence of test is stated in PR)
- `git diff` shows only bug-related changes
