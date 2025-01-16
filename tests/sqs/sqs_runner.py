import docker

CLIENT = docker.from_env()


class SqsRunner:

    HOST_NAME = "localstack"

    def __init__(self, network_name: str):
        self.container = None
        self.network_name = network_name

    def start(self):
        if self.container:
            return
        self.container = CLIENT.containers.run("localstack/localstack:latest",
                                               ports={'4566/tcp': 4566},
                                               environment={"SERVICES": "sqs",
                                                            "AWS_DEFAULT_REGION": "us-east-1",
                                                            "DOCKER_HOST": "unix:///var/run/docker.sock"},
                                               volumes=["/var/run/docker.sock:/var/run/docker.sock"],
                                               detach=True,
                                               remove=True,
                                               network=self.network_name,
                                               name=self.HOST_NAME)

    @staticmethod
    def hostname() -> str:
        return f"http://{SqsRunner.HOST_NAME}:4566"

    def stop(self):
        if not self.container:
            return
        self.container.stop()
        self.container = None
