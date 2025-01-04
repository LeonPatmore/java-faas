FROM amazoncorretto:21-alpine AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN chmod +x gradlew
COPY common common
COPY core core
RUN ls
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/core/build/libs/*.jar core.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dloader.path=/app/handler.jar", "-cp", "/app/core.jar", "org.springframework.boot.loader.launch.PropertiesLauncher"]
