build:
	MSYS_NO_PATHCONV=1 chmod +x gradlew && ./gradlew --no-daemon build -x test

test:
	MSYS_NO_PATHCONV=1 chmod +x gradlew && ./gradlew --no-daemon check

docker-build:
	docker build -t leonpatmore2/spring-boot-faas .
