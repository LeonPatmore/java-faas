from docker import DockerClient
from docker.errors import APIError


def create_network(client: DockerClient, name: str):
    try:
        client.networks.create(name)
    except APIError as e:
        if e.status_code != 409:
            raise e
