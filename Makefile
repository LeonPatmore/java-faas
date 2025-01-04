build:
	docker build -t java-faas .
	docker build -t java-faas-example -f example.Dockerfile .

run:
	docker run -p 8080:8080 java-faas-example
