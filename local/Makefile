run:
	docker-compose -f docker-compose.yaml up -d

test-sqs:
	docker run --network local_default --rm -e AWS_ACCESS_KEY_ID=dummy -e AWS_SECRET_ACCESS_KEY=dummy -e AWS_DEFAULT_REGION=us-east-1 amazon/aws-cli sqs send-message --endpoint-url=http://localstack:4566 --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/testQueue --message-body '{\"firstname\":\"leon\",\"lastname\":\"patmore\"}'
