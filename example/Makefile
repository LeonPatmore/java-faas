build:
	MSYS_NO_PATHCONV=1 chmod +x gradlew && ./gradlew build -x test

build-docker:
	docker build -t faas-example:latest . --build-arg GITHUB_USER=$(githubUser) \
	--build-arg GITHUB_TOKEN=$(githubToken)
