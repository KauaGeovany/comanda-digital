FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/comanda-digital-api.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/comanda-digital-api.jar"]
