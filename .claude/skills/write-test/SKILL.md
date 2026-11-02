---
name: write-test
description: Write new tests for code that has none — figure out the project's test runner and existing patterns, then cover the happy path, failure paths, and edges. Use when adding tests, or when a fix needs a regression test.
allowed-tools: Bash, Read, Glob, Grep, Write, Edit
---

# Write Tests

For _running_ tests and diagnosing failures, use the `test` skill. This one is about writing tests that
didn't exist yet.

## Step 1 — Match the Project, Don't Impose a Style

Read a sibling test before writing one. The point is that a new test looks like it belongs.

```bash
git ls-files | grep -iE '(test|spec)' | head -20   # where tests live, and how they're named
ls gradlew pom.xml package.json pyproject.toml go.mod Cargo.toml 2>/dev/null
node -e "console.log(require('./package.json').scripts)" 2>/dev/null
```

From that: the runner, the file naming (`*.spec.ts` vs `*Test.kt` vs `test_*.py`), where the file goes
(beside the source or under a mirrored tree), and how this project builds fixtures and doubles. Follow
what's there even if you'd do it differently — a lone test in a different style is friction for
everyone who reads it next.

## Step 2 — Decide What Deserves a Test

Take the cases from the code's own branches, not from a template:

1. **The happy path** — one case that proves the thing works
2. **Every failure the code handles** — each `catch`, each error return, each validation rejection.
   Untested error paths are where bugs hide, because nobody runs them by hand
3. **Boundaries** — empty, one, many; first and last; `null`/absent; the largest value that's allowed
4. **The bug you just fixed** — a regression test that fails on the old code. If it passes without the
   fix, it isn't testing the fix

Skip tests that restate the framework (a getter returning what was set) — they cost maintenance and
prove nothing.

## Step 3 — Isolate What You Don't Own

Network, clock, filesystem, and randomness make tests flaky. Replace them at the project's usual seam:
a fake passed into the constructor, the runner's mocking API, a temp directory with a config override.
Prefer injecting a fake over patching a global — the test then reads as "this collaborator returns X"
rather than "the world is monkeypatched".

Replace only what is slow, external, or nondeterministic, and keep everything the test actually depends on
real — a mock of your own logic proves the mock works. If you're unsure whether something needs a double,
run the test with the real one first. Two signs you've gone too far: the double's setup is longer than the
test itself, or you can't say why this one has to be faked. A faked response carries the real shape in
full, not just the fields this test reads.

## Step 4 — Make Each Test Stand Alone

- Runnable by itself, in any order. No test may depend on another having run
- Clean up what it created (temp dirs, servers, spies), including when it fails — the runner's
  teardown hook, not a line at the end of the test body
- Restore environment variables by **deleting** keys that were absent before. In Node,
  `process.env.X = undefined` stores the string `"undefined"`, which is truthy and leaks into the next
  test; use `delete process.env.X`

## Step 5 — Verify the Test Actually Tests

Run it, then **break the code on purpose and run again**. A test that passes against broken code is
worse than no test: it reports safety that isn't there.

This is where the subtle ones get caught. If you assert "X takes precedence over Y", the two inputs
must collide on the same key — with disjoint inputs the assertion passes on a merge that ignores
precedence entirely, and it proves only that both values survived.

## Step 6 — Report

State the command you ran, the cases added, and anything you chose not to cover and why. An untested
path named in the report can be picked up later; an unmentioned one is just missing.
