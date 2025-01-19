build:
	MSYS_NO_PATHCONV=1 chmod +x gradlew && ./gradlew build -x test

test:
	MSYS_NO_PATHCONV=1 chmod +x gradlew && ./gradlew check

docker-build:
	docker build -t leonpatmore2/spring-boot-faas .

save-image:
	docker image save leonpatmore2/spring-boot-faas:$(imageTag) -o spring-boot-faas.tar
