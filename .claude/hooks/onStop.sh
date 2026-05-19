#!/bin/bash
# Stop hook: compile check (Java/Gradle only) + change summary

INPUT=$(cat)
# jq optional — fall back to pwd if unavailable
if command -v jq >/dev/null 2>&1; then
    CWD=$(echo "$INPUT" | jq -r '.cwd // empty' 2>/dev/null)
else
    CWD=""
fi
if [[ -z "$CWD" ]]; then
    CWD="$(pwd)"
fi

cd "$CWD" || exit 0

# Detect any pending changes (tracked AND untracked)
CHANGED=$(git status --porcelain 2>/dev/null)
if [[ -z "$CHANGED" ]]; then
    exit 0
fi

CHANGED_FILES=$(git status --porcelain | sed -E 's/^...//' | sed 's/ -> /\n/g')

# Compile only when Java/Kotlin/Gradle files changed
if echo "$CHANGED_FILES" | grep -qE \
    '\.(java|kt|kts)$|build\.gradle(\.kts)?$|settings\.gradle(\.kts)?$|gradle\.properties$|gradle/libs\.versions\.toml$|gradlew(\.bat)?$'; then
    echo "[Hook] Java/Gradle changes detected — running compileJava..." >&2
    OUTPUT=$(./gradlew compileJava --quiet 2>&1)
    EXIT_CODE=$?
    if [[ $EXIT_CODE -ne 0 ]]; then
        # Warning only — hook exits 0 so Claude turn is not blocked
        echo "[Hook] Compile FAILED — fix before finishing:"
        echo "$OUTPUT" | tail -20
    else
        echo "[Hook] Compile OK" >&2
    fi
else
    echo "[Hook] No Java/Gradle changes — compile skipped" >&2
fi

echo "[Hook] Changed files:" >&2
git status --short >&2
echo "[Hook] Diff stat:" >&2
git diff --stat 2>/dev/null | tail -20 >&2

exit 0
