FROM azul/zulu-openjdk:25.0.0-jdk AS builder

COPY . /app
WORKDIR /app
RUN chmod +x ./gradlew && ./gradlew --no-daemon assemble

FROM azul/zulu-openjdk-alpine:25-jre-headless-latest

WORKDIR /app
COPY --from=builder /app/build/libs/to-do-app-be-*-SNAPSHOT.jar ./app.jar

EXPOSE 8080
CMD ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]