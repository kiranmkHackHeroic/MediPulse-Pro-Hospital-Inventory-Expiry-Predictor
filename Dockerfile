# =======================================================
# GitHub: https://github.com/kiranmkHackHeroic/MediPulse-Pro-Hospital-Inventory-Expiry-Predictor
# Stage 1: Build JAR inside a Maven container
# =======================================================
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy Maven POM and source files
COPY pom.xml .
COPY src ./src

# Compile and package Spring Boot executable JAR
RUN mvn clean package -DskipTests

# =======================================================
# Stage 2: Ultra-lightweight Runtime container
# =======================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built JAR from Stage 1
COPY --from=builder /build/target/*.jar app.jar

# Set Port and entrypoint
EXPOSE 2330
ENTRYPOINT ["java", "-jar", "app.jar"]
