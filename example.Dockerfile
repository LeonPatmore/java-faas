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
COPY example example
RUN ls
RUN ./gradlew clean build -x test

FROM java-faas
COPY --from=build /app/example/build/libs/*.jar /app/handler.jar
