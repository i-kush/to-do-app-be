FROM eclipse-temurin:21-jdk AS builder

COPY . /app
WORKDIR /app
RUN chmod +x ./gradlew && ./gradlew --no-daemon assemble

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /app/build/libs/to-do-app-be-*-SNAPSHOT.jar ./app.jar

EXPOSE 8080
CMD ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]