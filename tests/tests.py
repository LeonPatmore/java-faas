from time import sleep

import docker
import pytest

from docker_utils import create_network
from faas_runner_docker import DockerFaasRunner
from sqs.sqs_message_producer import DockerSqsMessageProducer, SqsMessageProducer
from sqs.sqs_runner import SqsRunner


@pytest.fixture(scope="session")
def network_name():
    return "spring-boot-faas-tests"


@pytest.fixture(scope="session")
def docker_client():
    return docker.from_env()


@pytest.fixture(scope="session", autouse=True)
def setup_network(docker_client, network_name):
    create_network(docker_client, network_name)


@pytest.fixture(scope="session")
def sqs(network_name):
    sqs = SqsRunner(network_name)
    sqs.start()
    yield sqs
    sqs.stop()


@pytest.fixture(scope="session")
def sqs_message_producer(docker_client, network_name) -> SqsMessageProducer:
    return DockerSqsMessageProducer(docker_client, network_name)


@pytest.fixture(scope="session")
def faas_runner(docker_client, network_name):
    return DockerFaasRunner(docker_client, network_name)


@pytest.fixture
def run_instance(sqs, faas_runner):
    container = faas_runner.run({
        "EVENT_SOURCE_SQS_ENABLED": "true",
        "SPRING_CLOUD_AWS_REGION_STATIC": "us-east-1",
        "SPRING_CLOUD_AWS_SQS_ENDPOINT": sqs.hostname(),
        "SPRING_CLOUD_AWS_CREDENTIALS_ACCESS_KEY": "dummy",
        "SPRING_CLOUD_AWS_CREDENTIALS_SECRET_KEY": "dummy",
        "ROOT_SOURCE_PROPS_QUEUENAME": "testQueue"
    })
    yield
    container.stop()


def test_something(run_instance, sqs_message_producer):
    sleep(5)  # TODO: Improve this to wait for application to be healthy.
    sqs_message_producer.produce("asd", "testQueue")
    pass
