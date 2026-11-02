---
name: handle-feedback
description: Work through feedback that arrives outside a PR — chat, an issue, a verbal review note. Classify each item as apply / explain / decline, act only on what holds up, and fold repeated feedback back into the project's rules.
allowed-tools: Bash, Read, Glob, Grep, Edit, Write
---

# Handle Feedback

For review comments on a GitHub PR, use `resolve-reviews` — it fetches the comments and replies on the
thread. This skill is for everything else: a message in chat, a line in an issue, something said in
passing. The difference that matters is that **nobody is tracking these**, so an item that gets quietly
dropped is simply lost.

## Step 1 — List the Items Separately

Feedback usually arrives as a paragraph holding three unrelated points. Split them first; a merged list
is how the second and third points get forgotten.

## Step 2 — Check Whether It's Already Handled

Read the current code before changing anything. A fair share of feedback describes a state that no
longer exists — someone read an older version, or the fix landed after they looked.

## Step 3 — Classify

| Class       | When                                                                         | What you owe back                                                                                    |
| ----------- | ---------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| **Apply**   | A real defect, a convention violation, a missing test, a clear improvement   | The change, and where it landed                                                                      |
| **Explain** | The code is right and deliberate, but the reason isn't visible               | The reason — and if it wasn't obvious to a reader, that's a sign the code or a comment should say it |
| **Decline** | Personal preference, out of scope for this work, or based on a wrong premise | Why, in one sentence                                                                                 |

Do not apply everything by default. Feedback applied without judgment produces changes nobody can
justify later, and it trains reviewers to stop thinking before they write.

Equally, do not decline because a change is inconvenient. "Out of scope" means _this_ work isn't the
place for it — so say where it does belong (a follow-up issue, the next PR).

## Step 4 — Apply, Minimally

One change per item, touching only what the item is about. If an item turns out to need a large change,
stop and say so instead of quietly expanding the work — the person who gave the feedback probably didn't
know its cost either.

Then verify with the project's own commands (`test` skill works out what they are). Report the result;
a change that wasn't run isn't done.

## Step 5 — Fold Recurring Feedback Into the Rules

**If the same feedback comes up a second time, the problem is the rule, not the change.** Fix it where it
won't come back:

- A convention → wherever this repo keeps them (`CLAUDE.md`, `AGENTS.md`, `CONTRIBUTING.md`, or
  `.claude/rules/*.md` if it has them — check which exist before assuming)
- Something a skill should have caught → that skill
- Something a hook could catch mechanically → a hook module

This step is the one that gets skipped, and skipping it is why the same review comment gets written four
times across four PRs.

## Step 6 — Report

```
적용:
  - <피드백>: <파일:줄 — 무엇을 바꿨는지>

설명:
  - <피드백>: <현재 코드가 맞는 이유>

거절:
  - <피드백>: <이유> (다룰 곳이 있으면 어디인지)

검증: <실행한 명령과 결과>
규칙 반영: <반복된 피드백이 있었다면 어디를 고쳤는지>
```

Every declined item needs its reason in writing. An item silently dropped reads as an item ignored, and
the person will bring it back next week.
