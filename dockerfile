# Base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file (build it locally first)
COPY target/*.jar app.jar

# Add build information
ARG BUILD_VERSION=unknown
ARG BUILD_TIMESTAMP=unknown
LABEL version="${BUILD_VERSION}" \
      build-timestamp="${BUILD_TIMESTAMP}"

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]