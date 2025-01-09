FROM amazoncorretto:21-alpine AS build

WORKDIR /app

COPY core-tmp/gradle gradle
COPY core-tmp/gradlew .
COPY core-tmp/gradlew.bat .
COPY core-tmp/build.gradle.kts .
COPY core-tmp/settings.gradle.kts .
RUN chmod +x gradlew
COPY common common
COPY core core
RUN ls
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/core/build/libs/*.jar core.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dloader.path=/app/handler,/app/libs", "-cp", "/app/core.jar", "org.springframework.boot.loader.launch.PropertiesLauncher"]
