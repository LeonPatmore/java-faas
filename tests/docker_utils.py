import logging
import time
from datetime import datetime, timedelta

from docker import DockerClient
from docker.errors import APIError
from docker.models.containers import Container


def create_network(client: DockerClient, name: str):
    try:
        client.networks.create(name)
    except APIError as e:
        if e.status_code != 409:
            raise e


def wait_for_container_to_be_healthy(container: Container, max_wait: timedelta = timedelta(seconds=15)):
    container_name = container.name
    start_time = datetime.now()
    end_time = start_time + max_wait
    while datetime.now() < end_time:
        container.reload()
        health_status = container.attrs.get("State", {}).get("Health", {}).get("Status", "unknown")
        if health_status == "healthy":
            logging.info(f"Container '{container_name}' is healthy.")
            return
        elif health_status != "starting" and health_status != "unknown":
            logging.error(f"Container '{container_name}' health status: {health_status}")
            logging.info(container.logs())
            raise RuntimeError(f"Container '{container_name}' health status: {health_status}")
        logging.info(f"Waiting for container '{container_name}' to be healthy. Current status: {health_status}")
        time.sleep(1)
    raise TimeoutError(f"Timeout waiting for container '{container_name}' to become healthy.")
