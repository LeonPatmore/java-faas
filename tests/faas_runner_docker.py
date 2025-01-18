from docker import DockerClient

from docker_utils import wait_for_container_to_be_healthy
from faas_runner import FaasRunner


class FaasInstance:

    def stop(self):
        raise NotImplementedError()


class DockerFaasRunner(FaasRunner):

    def __init__(self, docker_client: DockerClient, network_name: str, image_tag: str, handler_jar_path: str):
        self.docker_client = docker_client
        self.network_name = network_name
        self.image_name = f"leonpatmore2/spring-boot-faas:{image_tag}"
        self.handler_jar_path = handler_jar_path

    def run(self, envs: dict):
        volumes = [f"{self.handler_jar_path}:/app/handler/handler.jar"]
        container = self.docker_client.containers.run(self.image_name,
                                                      environment=envs,
                                                      volumes=volumes,
                                                      ports={'8080/tcp': 8080},
                                                      detach=True,
                                                      remove=True,
                                                      network=self.network_name)

        container.reload()
        wait_for_container_to_be_healthy(container)
        return container
