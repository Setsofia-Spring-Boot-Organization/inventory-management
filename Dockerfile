# Step 1: Use a lightweight JDK base image
FROM eclipse-temurin:21-jdk-jammy as builder

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy Gradle wrapper and build files first (to leverage caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Step 4: Download dependencies (cached in Docker layers)
RUN ./gradlew dependencies --no-daemon || return 0

# Step 5: Copy the entire project
COPY . .

# Step 6: Build the application (skip tests for faster build)
RUN ./gradlew clean build -x test --no-daemon

# Step 7: Use a smaller runtime image
FROM eclipse-temurin:21-jre-jammy

# Step 8: Set working directory
WORKDIR /app

# Step 9: Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Step 10: Expose port (Render uses PORT env variable anyway)
EXPOSE 8080

# Step 11: Run the application
ENTRYPOINT ["java","-jar","app.jar"]
