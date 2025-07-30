#2. Docker Setup
#Dockerfile (for Spring Boot backend)
#dockerfile
# Build stage
FROM maven:eclipse-temurin:21 as builder
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:21
WORKDIR /app

COPY --from=builder /workspace/app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]