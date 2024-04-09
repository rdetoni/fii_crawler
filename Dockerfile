# Use the official OpenJDK 17 image based on Alpine Linux as the base image
FROM openjdk:17-jdk-alpine

# Copy the compiled Spring Boot JAR file into the container
COPY target/crawler-0.1.1-SNAPSHOT.jar /app.jar

# Define the entry point for running the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
