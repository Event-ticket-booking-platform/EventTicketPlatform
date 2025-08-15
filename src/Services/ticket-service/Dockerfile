# # Step 1: Build stage
# FROM maven:3.9.6-eclipse-temurin-17 AS build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn clean package -DskipTests

# # Step 2: Runtime stage
# FROM eclipse-temurin:17-jdk
# WORKDIR /app
# COPY --from=build /app/target/*.jar app.jar

# # Expose Spring Boot port
# EXPOSE 8080

# # Run the Spring Boot app
# ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY src/Services/ticket-service/pom.xml .
RUN mvn dependency:go-offline

COPY src/Services/ticket-service/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/ticket-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
