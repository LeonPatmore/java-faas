test-core:
	 MSYS_NO_PATHCONV=1 cd core && ./gradlew --no-daemon test

test-functional:
	cd tests && pipenv run pytest

build:
	cd core && docker build -t leonpatmore2/spring-boot-faas .
