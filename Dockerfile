# First stage: Build the application
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /build

# Copy source code
COPY . .

# Build the application with Maven (without using wrapper)
RUN mvn clean package

# Second stage: Create runtime image
FROM eclipse-temurin:17-jre-jammy

# Add labels
LABEL maintainer="your.email@example.com"
LABEL application="product-service"
LABEL version="1.0"

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Scripts for waiting and health checks
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=45s --timeout=5s --start-period=75s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose application port
EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Europe/Zagreb"

ENTRYPOINT exec java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar