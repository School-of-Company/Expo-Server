# Test Writing Skill

## When to Use

When adding tests for new or existing service logic.

## File Location

```
src/test/java/team/startup/expo/domain/{name}/service/{Name}ServiceTest.java
```

## Steps

1. Read `{Name}ServiceImpl.java` fully
2. Check existing test patterns:
   ```bash
   find src/test -name "*Test.java" | head -5
   ```
3. Write happy path test cases
4. Write exception path test cases
5. Minimize mock scope — only mock direct dependencies
6. Run:
   ```bash
   ./gradlew test --tests "team.startup.expo.domain.<name>.*"
   ```
7. Fix failures and re-run until all pass

## Template

> **Illustrative only — adapt class names, method names, and ID types to the existing code.**
> Always check the existing repository method signature before choosing the ID type. Do not assume every repository uses `Long`. Use `String`, `Long`, `UUID`, or another type according to the existing code.
> Do NOT call `.id(...)` on builders — entity IDs are `@GeneratedValue` and not exposed via builder.
> If an ID is required, use `ReflectionTestUtils.setField(entity, "id", value)` only in tests, where `value` matches the existing entity ID type.

```java
@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock SampleRepository sampleRepository;
    @InjectMocks SampleServiceImpl sampleService;

    @Test
    @DisplayName("존재하는 ID로 조회하면 응답 DTO를 반환한다")
    void success() {
        var sampleId = "sample-id";  // Replace with actual ID type (String, Long, UUID…)
        Sample sample = Sample.builder()
            .name("Test")
            .build();
        given(sampleRepository.findById(sampleId)).willReturn(Optional.of(sample));

        SampleResponse result = sampleService.findSample(sampleId);

        assertThat(result.name()).isEqualTo("Test");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 예외를 던진다")
    void notFound() {
        var sampleId = "sample-id";
        given(sampleRepository.findById(sampleId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sampleService.findSample(sampleId))
            .isInstanceOf(SampleNotFoundException.class);
    }
}
```

> This example uses `String` only to avoid implying that all IDs are `Long`.
> Always replace `sampleId` with the actual ID type used by the existing repository.
