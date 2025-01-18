FROM amazoncorretto:21-alpine AS build

ARG GITHUB_USER
RUN test -n "$GITHUB_USER" || (echo "GITHUB_USER  not set" && false)
ARG GITHUB_TOKEN
RUN test -n "$GITHUB_TOKEN" || (echo "GITHUB_TOKEN  not set" && false)

ENV GITHUB_USER=${GITHUB_USER}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle.kts .
RUN chmod +x gradlew

COPY src src
RUN ./gradlew clean build -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/handler/handler.jar
