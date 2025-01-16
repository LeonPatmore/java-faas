from docker import DockerClient


class SqsMessageProducer:

    def produce(self, msg: str, queue_name: str):
        raise NotImplementedError


class DockerSqsMessageProducer(SqsMessageProducer):

    def __init__(self, docker_client: DockerClient, network_name: str):
        self.docker_client = docker_client
        self.network_name = network_name

    def produce(self, msg: str, queue_name: str):
        queue_url = f"http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/{queue_name}"
        self.docker_client.containers.run("amazon/aws-cli",
                                          command="sqs send-message --endpoint-url=http://localstack:4566"
                                                  f" --queue-url {queue_url}"
                                                  f" --message-body '{msg}'",
                                          environment={
                                              "AWS_ACCESS_KEY_ID": "dummy",
                                              "AWS_SECRET_ACCESS_KEY": "dummy",
                                              "AWS_DEFAULT_REGION": "us-east-1"},
                                          network=self.network_name,
                                          detach=False,
                                          remove=True)
