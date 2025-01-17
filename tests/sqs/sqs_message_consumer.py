import json

from docker import DockerClient


class SqsMessageConsumer:

    def __init__(self, queue_name: str):
        self.queue_name = queue_name

    def get_messages(self) -> list:
        raise NotImplementedError


class DockerSqsMessageConsumer(SqsMessageConsumer):

    def __init__(self, docker_client: DockerClient, network_name: str, queue_name: str):
        super().__init__(queue_name)
        self.docker_client = docker_client
        self.network_name = network_name

    def get_messages(self) -> list:
        queue_url = f"http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/{self.queue_name}"
        messages = self.docker_client.containers.run("amazon/aws-cli",
                                                     command="sqs receive-message --endpoint-url=http://localstack:4566"
                                                             f" --queue-url {queue_url}"
                                                             f" --max-number-of-messages 1"
                                                             f" --wait-time-seconds 10",
                                                     environment={
                                                         "AWS_ACCESS_KEY_ID": "dummy",
                                                         "AWS_SECRET_ACCESS_KEY": "dummy",
                                                         "AWS_DEFAULT_REGION": "us-east-1"},
                                                     network=self.network_name,
                                                     detach=False,
                                                     remove=True)
        return json.loads(messages)["Messages"]
