# Coding Standards

## Forbidden Patterns

- `System.out.println()` — use `@Slf4j` instead
- `@Autowired` field injection — constructor injection only via `@RequiredArgsConstructor`
- Class-level `@Transactional`
- `ServiceImpl` depending directly on another `ServiceImpl`
- Business logic inside `Controller`
- Hardcoded secrets or environment variables

## Comments

Do not add obvious comments. Add comments only when explaining non-obvious domain constraints or external API behavior.

## Naming

| Target | Convention |
|--------|------------|
| Classes | `PascalCase` |
| Methods, variables | `camelCase` |
| Constants | `UPPER_SNAKE_CASE` |
| Request DTOs | `{Action}{Domain}Request` |
| Response DTOs | `{Domain}Response` |
| Events | `{Action}Event` |
| Event handlers | `{Action}EventHandler` |

## Exception Handling

- Define exceptions in `domain/{name}/exception/`
- Extend `RuntimeException`; do not use `@ResponseStatus` (handled globally)
- Message: Korean formal speech, ends with period (`"엑스포를 찾을 수 없습니다."`)
- Do not include sensitive values in exception messages. Non-sensitive domain IDs may be included when useful for debugging.

## Transaction Rules

- Read: `@Transactional(readOnly = true)`
- Write: `@Transactional`
- Event handlers: `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Transactional(propagation = REQUIRES_NEW)`

## Dependency Injection

```java
@Service
@RequiredArgsConstructor
public class ExpoServiceImpl implements ExpoService {
    private final ExpoRepository expoRepository;
    private final ApplicationEventPublisher eventPublisher;
}
```
