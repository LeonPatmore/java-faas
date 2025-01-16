import os

from docker_utils import NETWORK_NAME
from faas_runner import FaasRunner
import docker

CLIENT = docker.from_env()
FILE_PATH = os.path.abspath(__file__)


class FaasInstance:

    def stop(self):
        raise NotImplementedError()


class DockerFaasRunner(FaasRunner):

    def run(self, envs: dict):
        volumes = [f"{FILE_PATH}/../../example/build/libs/example-0.0.1-SNAPSHOT-plain.jar:/app/handler/handler.jar"]
        container = CLIENT.containers.run("spring-boot-faas",
                                          environment=envs,
                                          volumes=volumes,
                                          ports={'8080/tcp': 8080},
                                          detach=True,
                                          remove=True,
                                          network=NETWORK_NAME)
        return container
