import os

from docker import DockerClient

from faas_runner import FaasRunner

FILE_PATH = os.path.abspath(__file__)


class FaasInstance:

    def stop(self):
        raise NotImplementedError()


class DockerFaasRunner(FaasRunner):

    def __init__(self, docker_client: DockerClient, network_name: str, image_tag: str):
        self.docker_client = docker_client
        self.network_name = network_name
        self.image_name = f"leonpatmore2/spring-boot-faas:{image_tag}"

    def run(self, envs: dict):
        volumes = [f"{FILE_PATH}/../../example/build/libs/example-0.0.1-SNAPSHOT-plain.jar:/app/handler/handler.jar"]
        container = self.docker_client.containers.run(self.image_name,
                                                      environment=envs,
                                                      volumes=volumes,
                                                      ports={'8080/tcp': 8080},
                                                      detach=True,
                                                      remove=True,
                                                      network=self.network_name)
        return container
