FROM gradle:9.4.1-jdk21 AS builder

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]