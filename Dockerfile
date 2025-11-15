# Build stage
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-alpine
WORKDIR /app

# Copy the jar
COPY --from=build /app/target/*.jar demo.jar

# Copy the truststore explicitly to /app/certs
COPY ./client.truststore.jks /app/certs/client.truststore.jks

EXPOSE 8800
ENTRYPOINT ["java","-jar","demo.jar"]
