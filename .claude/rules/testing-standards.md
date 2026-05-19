# Testing Standards

## Guidelines

- **Complex business logic (new feature)**: service-layer unit test required
- **Bug fix**: write a failing test that reproduces the bug first (strongly recommended)
- **Simple CRUD with no logic**: test is optional; state the reason in the PR description if skipped
- Never claim "no tests needed" without an explicit reason

## Test File Location

```
src/test/java/team/startup/expo/domain/{name}/service/{Name}ServiceTest.java
```

## Test Template

> **This template is illustrative only. Always adapt class names, method names, and ID types to the existing code.**
> - Always check the existing repository method signature before choosing the ID type. Do not assume every repository uses `Long`. Use `String`, `Long`, `UUID`, or another type according to the existing code.
> - Do NOT call `.id(...)` on a builder — JPA entities use `@GeneratedValue` and the id field is not exposed via builder.
> - If an ID is required, use `ReflectionTestUtils.setField(entity, "id", value)` only in tests, where `value` matches the existing entity ID type.
> - Always check existing fixture/factory patterns in `src/test` before writing new builders.

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

## Running Tests

```bash
./gradlew test
./gradlew test --tests "team.startup.expo.domain.<name>.*"
```
