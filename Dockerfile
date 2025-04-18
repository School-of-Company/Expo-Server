# Build stage
FROM gradle:jdk17-alpine AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew clean build

# Run Stage
FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/build/libs/expo-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]