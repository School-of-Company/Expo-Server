---
name: refactor-safely
description: Restructure code without changing behavior — establish a test baseline, change one thing at a time, verify after each step, and keep refactors out of behavior commits. Use when improving structure, removing duplication, or separating concerns.
allowed-tools: Bash, Read, Glob, Grep, Edit, Write
---

# Refactor Safely

A refactor that changes behavior isn't a refactor — it's an undocumented change. **Tests are the only
evidence that behavior was preserved**, so everything below is built around keeping them green.

## Step 1 — Name the Goal

Say what improves, in one sentence: remove duplication, separate two concerns that got tangled, delete a
layer nothing uses, make a name say what the thing does. If you can't name it, the code doesn't need
refactoring yet.

Then check the scope. **If it needs edits across more than three unrelated files, stop and reconsider** —
either the goal is too broad, or it's really several refactors that should land separately.

## Step 2 — Establish a Baseline

Run the tests **before touching anything**, with the project's own command (the `test` skill works out
what that is).

Three outcomes decide what happens next:

- **Green** — that's the baseline. Note how long it takes; you'll run it repeatedly
- **Already failing** — fix or stash that first. You cannot tell what your refactor broke if something
  was broken before it
- **No tests covering this code** — write them first (the `write-test` skill). Refactoring untested code
  is editing blind, and "it still compiles" is not behavior preservation

## Step 3 — One Change at a Time

Apply a single structural change, then verify. Not three changes and one verification.

```bash
git diff        # read what you actually changed before trusting it
```

Verify with the project's type check and tests. On failure, the default is to **revert and reconsider**,
not to fix forward:

```bash
git checkout -- <file>   # or git stash, if you want to keep it to study
```

Fixing forward is how a clean refactor turns into a tangle of half-applied changes with no green state
to fall back to. Reverting costs one step; untangling costs an afternoon.

## Step 4 — Commit the Refactor Alone

```
refactor(scope): 구조 설명
```

**Never mix behavior changes into a refactor commit.** The reason is practical: when a bug appears next
week, a pure-refactor commit can be reverted or skipped while bisecting. A commit that moved three files
_and_ fixed an edge case can't be reasoned about at all.

If you notice a real bug mid-refactor, don't fix it in place — finish or stash the refactor, fix the bug
as its own commit, then continue.

## What Not to Do

- **No new features.** "While I'm in here" is how refactors become unreviewable
- **Don't add an abstraction for one use case.** An interface, a base class, or a generic helper earns
  its place when a _second_ real caller exists — not in anticipation of one. Speculative abstraction is
  the thing future refactors have to undo
- **Don't reformat files you're not restructuring.** Formatting noise hides the actual change in review
- **Don't rename and move in the same step.** Git tracks a rename or a content change well, both at once
  poorly — and so does a reviewer
