FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Start with proper port binding
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.datasource.hikari.connection-timeout=60000 -jar app.jar"]
