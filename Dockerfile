FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENV DB_URL=jdbc:mysql://host.docker.internal:3306/HMS_API?serverTimezone=UTC
ENV DB_USERNAME=root
ENV DB_PASSWORD=root

ENTRYPOINT ["java", \
            "-Dspring.datasource.url=${DB_URL}", \
            "-Dspring.datasource.username=${DB_USERNAME}", \
            "-Dspring.datasource.password=${DB_PASSWORD}", \
            "-jar", "/app/app.jar"]