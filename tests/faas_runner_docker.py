from docker import DockerClient

from docker_utils import wait_for_container_to_be_healthy
from faas_runner import FaasRunner


class FaasInstance:

    def stop(self):
        raise NotImplementedError()


class DockerFaasRunner(FaasRunner):

    def __init__(self, docker_client: DockerClient, network_name: str, image_name: str, handler_jar_path: str or None):
        self.docker_client = docker_client
        self.network_name = network_name
        self.image_name = image_name
        if handler_jar_path:
            volumes = [f"{handler_jar_path}:/app/handler/handler.jar"]
        else:
            volumes = []
        self.volumes = volumes

    def run(self, envs: dict):
        container = self.docker_client.containers.run(self.image_name,
                                                      environment=envs,
                                                      volumes=self.volumes,
                                                      ports={'8080/tcp': 8080},
                                                      detach=True,
                                                      remove=False,
                                                      network=self.network_name)

        container.reload()
        wait_for_container_to_be_healthy(self.docker_client, container)
        return container
