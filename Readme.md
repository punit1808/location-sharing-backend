# 📍 Location Service

A **Spring Boot** microservice that streams **real-time user location updates** using **Kafka (Aiven)** and persists them into **Postgres (DB)**.  

- **Producer** → Publishes user location updates to Kafka (`location-updates` topic).  
- **Consumer** → Subscribes to Kafka, caches updates, and periodically flushes them to Postgres.  
- **Deployment** → Runs inside a Docker container on **Render**, while Kafka & DB are hosted on **Aiven/Neon**.  

---

## ⚙️ Tech Stack

- Java 17  
- Spring Boot 3.x  
- Spring Kafka  
- PostgreSQL (Aiven)  
- Kafka (Aiven)  
- Docker + Render  
- Git Actions

---

## 🚀 Setup

### 1. Clone Repository
```bash
git clone https://github.com/punit1808/location-sharing-backend.git
cd location-sharing-backend
```

### 2. Configure Application Properties
Create `src/main/resources/application.properties`:

```properties
spring.application.name=location
server.port=8080

# -------------------
# PostgreSQL
# -------------------
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# -------------------
# Kafka Service (Aiven)
# -------------------
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_URL}

spring.kafka.properties.security.protocol=SASL_SSL
spring.kafka.properties.sasl.mechanism=SCRAM-SHA-256
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="avnadmin" password="${KAFKA_PASSWORD}";
spring.kafka.properties.ssl.truststore.location=/app/config/client.truststore.jks
spring.kafka.properties.ssl.truststore.password=${TRUSTSTORE_PASSWORD}
spring.kafka.properties.ssl.truststore.type=JKS

spring.kafka.consumer.group-id=location-service
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=*

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# -------------------
# App Specific
# -------------------
kafka.topics.location=location-updates
```

---

## 🔑 Environment Variables

Set these in **Render dashboard → Environment** or in a local `.env` file:

```bash
DB_URL=jdbc:postgresql://<your-neon-host>:5432/<dbname>?sslmode=require
DB_USER=neondb_owner
DB_PASSWORD=********

KAFKA_BOOTSTRAP_URL=kafka-xxxxx-XXXXXXXX.aivencloud.com:14786
KAFKA_PASSWORD=********
TRUSTSTORE_PASSWORD=********
```

---

## 🔐 Kafka Certificates

Download the truststore from **Aiven Console → Service → Connection Information → Certificates**.  

- Save as `client.truststore.jks` in the project root.  
- Dockerfile copies it into the container: `/app/config/client.truststore.jks`.  

---

## 🐳 Docker Setup

### Dockerfile
```dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Kafka truststore
COPY client.truststore.jks /app/config/client.truststore.jks

# Copy Spring Boot jar
COPY target/location-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
```

### Build & Run Locally
```bash
./mvnw clean package -DskipTests
docker build -t location-service .
docker run --env-file .env -p 8080:8080 location-service
```

---

## 🚀 Deploy on Render

1. Push Docker image to **DockerHub**:
   ```bash
   docker tag location-service your-dockerhub-username/location-service:latest
   docker push your-dockerhub-username/location-service:latest
   ```

2. In **Render Dashboard**:
   - Create a **Web Service** using the DockerHub image.  
   - Add environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP`, `KAFKA_PASSWORD`, `TRUSTSTORE_PASSWORD`).  
   - Deploy.  

---

## 📡 API Endpoints (Example)

If you expose a REST API for location updates:

```http
POST /locations/update
Content-Type: application/json

{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "lat": 28.7041,
  "lng": 77.1025
}
```

This request → Producer publishes to Kafka → Consumer stores in DB.  

---

## 📖 Notes

- Do **not** commit `.jks` or secrets to GitHub.  
- Store sensitive values in **Render Environment Variables**.  
- Kafka topic `location-updates` is auto-created by `KafkaConfig`.  

---

✅ Ready to run your **Location Streaming Service** on Render with Aiven Kafka + Neon Postgres!
