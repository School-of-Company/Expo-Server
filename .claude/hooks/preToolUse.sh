#!/bin/bash
# PreToolUse: block dangerous commands + log all bash calls
# Requires: jq

if ! command -v jq >/dev/null 2>&1; then
    echo "[Hook] jq is required for Claude Code hooks. Install jq first." >&2
    exit 1
fi

INPUT=$(cat)
TOOL_NAME=$(echo "$INPUT" | jq -r '.tool_name')

if [[ "$TOOL_NAME" != "Bash" ]]; then
    exit 0
fi

COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command')
CWD=$(echo "$INPUT" | jq -r '.cwd // empty')
if [[ -z "$CWD" || "$CWD" == "null" ]]; then
    CWD="$(pwd)"
fi
TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
LOG_DIR="$CWD/.claude/.logs"
mkdir -p "$LOG_DIR"
echo "[$TIMESTAMP] $COMMAND" >> "$LOG_DIR/command.log"

# Regex-based dangerous command blocking
BLOCKED_REGEX=(
    '(^|[[:space:]])rm[[:space:]]+-rf([[:space:]]|$)'
    '(^|[[:space:]])rm[[:space:]]+-fr([[:space:]]|$)'
    '(^|[[:space:]])sudo[[:space:]]+rm'
    'git[[:space:]]+push[[:space:]]+--force'
    'git[[:space:]]+push.*--force-with-lease'
    'git[[:space:]]+push[[:space:]]+-f([[:space:]]|$)'
    'git[[:space:]]+reset[[:space:]]+--hard'
    'docker[[:space:]]+compose[[:space:]]+down.+(-v|--volumes)'
    'docker-compose[[:space:]]+down.+(-v|--volumes)'
    'curl[[:space:]].*\|[[:space:]]*(ba)?sh'
    'wget[[:space:]].*\|[[:space:]]*(ba)?sh'
    'gh[[:space:]]+api.*(--method|-X)[[:space:]]+(POST|PATCH|PUT|DELETE)'
)

for pattern in "${BLOCKED_REGEX[@]}"; do
    if echo "$COMMAND" | grep -qE "$pattern"; then
        echo "[Hook] Blocked dangerous command: $COMMAND" >&2
        exit 2
    fi
done

# Block placeholder branch names — angle brackets cause shell redirection on literal execution
if echo "$COMMAND" | grep -qE 'git[[:space:]]+push.*<(current-branch|branch)>'; then
    echo "[Hook] Placeholder branch name detected. Replace with the actual branch name before running." >&2
    exit 2
fi

# Block compound git push commands FIRST — before any further push validation.
# Push inside ;, &&, ||, or {} bypasses branch safety validation entirely.
if echo "$COMMAND" | grep -qE '(^|[;&|{}[:space:]])git[[:space:]]+push'; then
    if [[ "$COMMAND" == *$'\n'* ]] || echo "$COMMAND" | grep -qE '[;&|{}]'; then
        echo "[Hook] git push must be executed as a standalone command. First run: git rev-parse --abbrev-ref HEAD, then push with the literal branch name." >&2
        exit 2
    fi
fi

# Block push to protected remote branches (including HEAD:branch syntax)
if echo "$COMMAND" | grep -qE 'git[[:space:]]+push.*([[:space:]]|^)(main|master|develop|HEAD:(main|master|develop))([[:space:]]|:|$)'; then
    echo "[Hook] Blocked: pushing to protected branch is forbidden." >&2
    exit 2
fi

# Safe push validation: allow pushing only the current non-protected working branch
if echo "$COMMAND" | grep -qE '^git[[:space:]]+push([[:space:]]+-u)?[[:space:]]+origin'; then
    BRANCH=$(git -C "$CWD" rev-parse --abbrev-ref HEAD 2>/dev/null)
    if [[ "$BRANCH" == "main" || "$BRANCH" == "master" || "$BRANCH" == "develop" ]]; then
        echo "[Hook] Protected branch '$BRANCH' — push is forbidden." >&2
        exit 2
    fi
    if echo "$COMMAND" | grep -qE '(^|[[:space:]])(--force|-f)([[:space:]]|$)'; then
        echo "[Hook] Force push is forbidden." >&2
        exit 2
    fi
    # Block explicit protected branch target
    if echo "$COMMAND" | grep -qE '[[:space:]](main|master|develop|HEAD:(main|master|develop))$'; then
        echo "[Hook] Push to protected branch is forbidden." >&2
        exit 2
    fi
    # Extract the token immediately after "origin" — requires explicit branch target
    TARGET=$(echo "$COMMAND" | awk '{
      for (i = 1; i <= NF; i++) {
        if ($i == "origin" && (i + 1) <= NF) {
          print $(i + 1)
          exit
        }
      }
    }' | tr -d "\"'")
    if [[ -z "$TARGET" ]]; then
        echo "[Hook] Push target branch is required. Use: git push -u origin $BRANCH" >&2
        exit 2
    fi
    if [[ "$TARGET" != "$BRANCH" && "$TARGET" != "origin/$BRANCH" ]]; then
        echo "[Hook] Push target must match current branch '$BRANCH'. Use: git push -u origin $BRANCH" >&2
        exit 2
    fi
fi

# Block commit from protected local branches
if echo "$COMMAND" | grep -qE '^git[[:space:]]+commit'; then
    BRANCH=$(git -C "$CWD" rev-parse --abbrev-ref HEAD 2>/dev/null)
    if [[ "$BRANCH" == "main" || "$BRANCH" == "master" || "$BRANCH" == "develop" ]]; then
        echo "[Hook] Protected branch '$BRANCH' — direct commit is forbidden." >&2
        echo "[Hook] Create a feature branch: git checkout -b feat/<name> origin/develop" >&2
        exit 2
    fi
fi

exit 0
