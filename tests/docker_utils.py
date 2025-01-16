import docker
from docker.errors import APIError

CLIENT = docker.from_env()


def create_network(name: str):
    try:
        CLIENT.networks.create(name)
    except APIError as e:
        if e.status_code != 409:
            raise e
