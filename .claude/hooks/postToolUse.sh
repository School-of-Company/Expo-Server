#!/bin/bash
# PostToolUse: lightweight file-type checks after Edit/Write
# Requires: jq; yq optional (YAML check skipped if absent)

if ! command -v jq >/dev/null 2>&1; then
    exit 0
fi

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')
CWD=$(echo "$INPUT" | jq -r '.cwd // empty')

if [[ -z "$FILE_PATH" || -z "$CWD" ]]; then
    exit 0
fi

cd "$CWD" || exit 0

if [[ ! -f "$FILE_PATH" ]]; then
    exit 0
fi

# YAML syntax check (yq only; skip gracefully if not installed)
if [[ "$FILE_PATH" == *.yml ]] || [[ "$FILE_PATH" == *.yaml ]]; then
    if command -v yq >/dev/null 2>&1; then
        if ! yq eval '.' "$FILE_PATH" > /dev/null 2>&1; then
            echo "[Hook] YAML syntax error in $FILE_PATH" >&2
            yq eval '.' "$FILE_PATH" 2>&1 | head -10
            exit 1
        else
            echo "[Hook] YAML OK: $(basename "$FILE_PATH")" >&2
        fi
    else
        echo "[Hook] yq not found — YAML syntax check skipped (install yq to enable)" >&2
    fi
fi

# Warn on sensitive file writes
BASENAME=$(basename "$FILE_PATH")
if [[ "$BASENAME" == .env* || "$FILE_PATH" == *.env || "$FILE_PATH" == *.env.* ]]; then
    echo "[Hook] WARNING: .env file written. Do not commit this file." >&2
fi
if [[ "$BASENAME" == "settings.local.json" ]]; then
    echo "[Hook] WARNING: settings.local.json written. Do not commit this file." >&2
fi

exit 0
