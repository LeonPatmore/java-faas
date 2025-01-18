FROM amazoncorretto:21-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew .
RUN chmod +x gradlew
COPY gradlew.bat .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY common common
COPY core core
COPY web web
COPY sqs sqs
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/core/build/libs/*.jar core.jar

HEALTHCHECK --interval=20s --timeout=3s --start-period=3s --retries=3 \
  CMD wget -T5 -qO- http://localhost:8080/actuator/health | grep UP || exit 1

EXPOSE 8080
ENTRYPOINT ["java",
    "-Dloader.path=/app/handler,/app/libs",
    "-cp",
    "/app/core.jar",
    "org.springframework.boot.loader.launch.PropertiesLauncher"]
