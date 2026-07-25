FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /application
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /application
COPY --from=builder /application/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]