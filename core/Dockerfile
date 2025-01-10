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

EXPOSE 8080
ENTRYPOINT ["java", "-Dloader.path=/app/handler,/app/libs", "-cp", "/app/core.jar", "org.springframework.boot.loader.launch.PropertiesLauncher"]
