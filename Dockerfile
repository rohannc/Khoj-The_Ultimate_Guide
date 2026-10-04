# Multi-stage Dockerfile for Spring Boot (JDK 21)
# Stage 1: Build the JAR
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Image (Lightweight JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/Khoj-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=supabase
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
