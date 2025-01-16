import docker
from docker.errors import APIError

CLIENT = docker.from_env()

NETWORK_NAME = "faas-tests"
try:
    CLIENT.networks.create("faas-tests")
except APIError as e:
    if e.status_code != 409:
        raise e
