FROM maven:3.9.4-eclipse-temurin-17 as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM openjdk:17

COPY --from=builder /app/target/demo-*.jar app/demo.jar

# Run the application
ENTRYPOINT ["java","-jar","/app/demo.jar"]