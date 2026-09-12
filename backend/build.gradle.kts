plugins {
    java
    id("org.springframework.boot") version "4.1.0-RC1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.mezon.classmanagement"
version = "0.0.1"
description = "backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["tomcat.version"] = "11.0.22"
extra["netty.version"] = "4.2.14.Final"
val springCloudVersion by extra("2025.1.2")

dependencies {


    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")


    implementation("org.projectlombok:lombok")
    implementation("com.nimbusds:nimbus-jose-jwt:10.0.2")
    implementation("org.mapstruct:mapstruct:1.6.3")

    implementation("org.apache.poi:poi:5.5.1")
    implementation("org.apache.tika:tika:3.3.1")
    implementation("dev.langchain4j:langchain4j:1.17.1")
    //implementation("dev.langchain4j:langchain4j-core:1.17.1")
    implementation("dev.langchain4j:langchain4j-open-ai:1.17.1")
    //implementation("dev.langchain4j:langchain4j-easy-rag:1.17.1-beta27")
    //implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.17.1")
    //implementation("dev.langchain4j:langchain4j-google-genai:1.17.1-beta27")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika:1.17.1-beta27")

    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")

    implementation("ai.djl.sentencepiece:sentencepiece:0.36.0")
    implementation("org.apache.lucene:lucene-core:10.5.0")
    //implementation("org.apache.lucene:lucene-queryparser:10.5.0")
    implementation("org.apache.lucene:lucene-analysis-common:10.5.0")
    implementation("commons-validator:commons-validator:1.10.1")
    implementation(files("libs/VnCoreNLP/VnCoreNLP-Gradle-1.2.jar"))

    //implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    //implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-mail")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}
