plugins {
    id("java")
    id("org.springframework.boot") version "2.7.6"
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
    implementation("javax.validation:validation-api:1.1.0.Final")
    implementation("com.mysql:mysql-connector-j:8.0.33")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.4.2.Final")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.4")
    testImplementation("org.projectlombok:lombok")
    testImplementation("com.github.tomakehurst:wiremock:2.27.2")
    testImplementation("org.springframework.security:spring-security-test:5.7.5")
}



tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
