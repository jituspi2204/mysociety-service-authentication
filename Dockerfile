FROM maven:3.9-amazoncorretto-23 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DSkipTests

FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=build target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
