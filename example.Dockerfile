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
COPY example example
RUN ls
RUN ./gradlew clean build -x test

FROM java-faas
COPY --from=build /app/example/build/libs/*.jar /app/handler.jar
