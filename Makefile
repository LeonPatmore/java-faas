build:
	cd core && docker build -t java-faas .

run:
	export MSYS_NO_PATHCONV=1
	docker run -v $(pwd)/example/build/libs/example-0.0.1-SNAPSHOT-plain.jar:/app/handler/handler.jar -p 8080:8080 -e EVENT_SOURCE_WEB_ENABLED=true java-faas
