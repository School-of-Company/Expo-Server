plugins {
	java
	id("org.springframework.boot") version "3.2.10"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm")
}

group = "java.team.startup"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation ("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation ("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation ("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("net.nurigo:sdk:4.3.0")
	implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
	implementation("com.google.zxing:core:3.5.0")
	implementation("com.google.zxing:javase:3.5.0")
	annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")
	implementation("com.google.code.gson:gson:2.8.9")
	implementation("org.apache.poi:poi:5.3.0")
	implementation("org.apache.poi:poi-ooxml:5.3.0")
	implementation(kotlin("stdlib-jdk8"))
	implementation ("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly ("io.micrometer:micrometer-registry-prometheus")
	implementation ("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor ("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor ("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor ("jakarta.persistence:jakarta.persistence-api")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
