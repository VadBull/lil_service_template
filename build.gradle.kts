

plugins {
    id("java")
    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("jacoco")
}
jacoco {
    version = "0.8.7"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}



tasks.jacocoTestReport {
    dependsOn(tasks.test)
}




group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security:3.0.4")
    implementation("org.flywaydb:flyway-core:8.5.13")
    implementation("org.flywaydb:flyway-mysql:8.5.13")
    implementation("org.mapstruct:mapstruct:1.4.2.Final")
    annotationProcessor ("org.mapstruct:mapstruct-processor:1.4.2.Final")
    implementation("javax.validation:validation-api:1.1.0.Final")
    compileOnly("org.projectlombok:lombok:1.18.28")
    implementation("com.mysql:mysql-connector-j:8.0.33")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.4")
    testImplementation ("org.projectlombok:lombok")
}



tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy (tasks.jacocoTestReport)
}
