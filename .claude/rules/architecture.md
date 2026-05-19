# Architecture Rules

## Domain Package Structure

Every domain must follow this layout. Apply identically for new domains.

```
domain/{name}/
├── domain/
│   └── {Name}.java              # @Entity; may include domain methods
├── dto/
│   ├── request/
│   │   └── {Action}{Name}Request.java
│   └── response/
│       └── {Name}Response.java
├── event/
│   ├── {Name}Event.java         # POJO — no need to extend ApplicationEvent
│   └── handler/
│       └── {Name}EventHandler.java
├── exception/
│   └── {Name}NotFoundException.java  # extends RuntimeException
├── presentation/
│   └── {Name}Controller.java
├── repository/
│   ├── {Name}Repository.java           # JpaRepository<{Name}, ExistingIdType>
│   └── {Name}RepositoryCustom.java     # QueryDSL interface
└── service/
    ├── {Name}Service.java              # Interface
    └── impl/
        └── {Name}ServiceImpl.java      # @Service, @RequiredArgsConstructor
```

## Dependency Direction

```
Controller → Service (interface) → Repository
                                 → ApplicationEventPublisher
```

- `ServiceImpl` must implement its `Service` interface
- `Controller` injects the `Service` interface only, never `ServiceImpl` directly
- `ServiceImpl` injects `Service` interfaces only, never another `ServiceImpl`
- `global/` is accessible from `domain/`; the reverse is forbidden

## Entity Rules

- **PK: follow the existing domain pattern** — do NOT assume every entity uses `Long id`
  - Most JPA entities: `@GeneratedValue(strategy = GenerationType.IDENTITY)` + `Long id`
  - Domains with external IDs (e.g., Expo) may use `String` or other types — preserve the existing type and repository method signature
- **Repository ID type must match the existing entity ID type.** Do not create new repositories with `Long` ID by default. For Expo-like domains that use external string IDs, preserve `String` ID signatures.
- Associations: `@ManyToOne(fetch = FetchType.LAZY)` by default (EAGER is forbidden)
- Use `@Builder` + `@NoArgsConstructor(access = PROTECTED)` together
- Mutate state through domain methods, not setters
