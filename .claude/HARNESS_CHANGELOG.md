# 하네스 변경 이력

## 2026-05-19 — 초기 하네스 구성 (#371)

### 추가

**CLAUDE.md** (~100줄): 하네스 진입점(라우터). 피드백 루프, 작업 유형별 라우팅 테이블, 규칙 문서 인덱스, 안전 규칙 정의.

### 설계 참고

- https://news.hada.io/topic?id=24744 — CLAUDE.md는 규칙 덤프가 아니라 라우팅 진입점이어야 한다. Progressive Disclosure: CLAUDE.md는 ~100줄로 유지하고, 세부 내용은 `.claude/` 하위 디렉터리로 분리.
- 참고 레포: `themoment-team/datagsm-server` (Spring/Kotlin 하네스), `School-of-Company/Gwangju-talent-festival-Client` (프론트엔드 하네스)

**settings.json**: 권한 허용/차단 목록 (docker compose V1+V2, jq, gh CLI, gradlew, `git ls-remote`) + PreToolUse, PostToolUse (Edit/Write 분리), Stop 훅. 상대 경로만 사용.

**훅:**
- `preToolUse.sh`: regex 기반 위험 명령 차단 (rm -rf, git push --force, git reset --hard, docker compose down -v, curl|sh). 보호 브랜치 감지. compound `git push` 차단 (`&&`/`||`/서브쉘에 포함된 push 금지, push는 단독 명령으로만 실행). placeholder 브랜치명 차단 (`<current-branch>`, `<branch>`). jq 의존성 검사. `.claude/.logs/command.log`에 명령 로깅.
- `postToolUse.sh`: 경량 검사 — yq YAML 문법 (yq 없으면 건너뜀), .env/.local 쓰기 경고. CWD로 이동 + 파일 존재 여부 확인 후 처리.
- `onStop.sh`: `git status --porcelain`으로 변경 감지. Java/Gradle 파일 변경 시에만 compileJava 실행. `sed`로 이름 변경 파일 처리. 항상 `git diff --stat` 출력.

**규칙 (4개):**
- `architecture.md`: 도메인 패키지 구조, 의존성 방향, 엔티티 규칙 (ID 타입은 기존 도메인 패턴 따름 — 항상 `Long`이 아님)
- `coding-standards.md`: 금지 패턴, 주석 정책, 네이밍, 예외 처리, 트랜잭션
- `git-workflow.md`: 브랜치 전략, 커밋 컨벤션 (`type :: 한국어 설명`), PR 규칙
- `testing-standards.md`: 테스트 필수/선택 기준, JUnit 5 + Mockito 템플릿 (ID 타입은 기존 레포지토리 시그니처 따름)

**에이전트 (7개):** feature, fix, test, refactor, review, pr, feedback

**스킬 (8개, `<name>/SKILL.md` 형식):** implement-feature, fix-bug, write-test, refactor-safely, write-pr, review-local-diff, resolve-pr-comments, handle-feedback

> 스킬은 레포지토리 로컬 절차 문서입니다. Claude Code slash command로 자동 등록되지 않습니다. CLAUDE.md 라우팅 테이블에서 참조만 합니다.

### 설계 결정

- Progressive Disclosure: CLAUDE.md는 라우터, 세부 내용은 `.claude/` 하위 디렉터리
- CLAUDE.md 목표: ~100줄 (매 세션마다 컨텍스트 비대화 방지)
- PostToolUse: 경량만 (compileJava 없음) — 파일 편집마다 느려지는 것 방지
- onStop.sh: Java/Gradle 파일 변경 시에만 compileJava — 마크다운 전용 세션은 건너뜀
- preToolUse.sh에서 regex 기반 차단 (glob 아님) — 신뢰도 높은 패턴 매칭
- compound `git push` 차단: `&&`/`||`/서브쉘 내 push는 브랜치 안전 검증 우회 가능
- placeholder 브랜치명 차단: 꺽쇠 괄호가 셸 리다이렉션 유발
- `feedback-agent`는 라우팅 전용 에이전트; PR 코멘트 직접 처리는 `resolve-pr-comments` 스킬
- `handle-feedback`은 일반 채팅/이슈 피드백 전용 — GitHub PR review comment는 `resolve-pr-comments`로 분리
- 엔티티/레포지토리 ID 타입: 기존 도메인 패턴 따름 (항상 `Long`이 아님)
- 테스트 템플릿은 `sampleId` 변수 사용 — `Long` ID 고정 가정 방지
- jq 필수, yq 선택 (YAML 검사용)
- 초기 범위: 규칙 4개, 에이전트 7개, 스킬 8개 — harness-self-improve 패턴으로 확장

---

## 2026-05-19 — 피드백 반영 및 스킬 추가

### 변경

**preToolUse.sh 보안 수정 (36d4b4f):**
- 뉴라인(`\n`) compound push 차단 추가 — `&&`/`||`/`;` 외 개행으로 인한 push 체크 우회 방지
- 보호 브랜치 regex false positive 수정 — `feat/fix-main-issue` 같은 브랜치명에서 오탐 방지, 단어 경계 명시

**gh api POST 허용:**
- `gh api --method POST` 허용 — PR/이슈 코멘트 및 답글 작성에 필요
- PATCH, PUT, DELETE만 차단 유지

**스킬 추가/수정:**
- `commit` 스킬 추가 — 사용자 명시적 요청 시에만 커밋, 컨벤션 강제
- `resolve-pr-comments` 스킬에 답글 달기 단계 추가 (반영/거절 각각 답변 형식 정의)
- `write-pr` 스킬 PR 템플릿 적용 (`.github/PULL_REQUEST_TEMPLATE.md` 반영)

### 설계 결정

- `gh api POST` 허용: 코멘트 작성은 되돌릴 수 있는 작업이므로 차단 불필요. 수정/삭제(PATCH/DELETE)만 위험으로 간주.
- `commit` 스킬 분리: 자동 커밋 방지를 위해 명시적 트리거가 필요한 별도 스킬로 관리.
- PR 답글 엔드포인트: `/pulls/<pr>/comments/<id>/replies` (POST) — `in_reply_to` 파라미터 방식 아님.
