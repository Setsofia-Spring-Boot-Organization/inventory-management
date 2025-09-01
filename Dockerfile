# ---- Build stage ----
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Ensure gradlew is executable
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon || return 0

# Copy project files
COPY . .

# Build the application (skip tests for faster build)
RUN ./gradlew clean build -x test --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the fat jar from build stage
COPY --from=build /app/build/libs/*.jar inventory-management.jar

# Expose port (Render will override with $PORT)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "inventory-management.jar"]
