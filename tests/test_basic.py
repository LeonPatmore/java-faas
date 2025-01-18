import logging
import os
from time import sleep

import docker
import pytest

from docker_utils import create_network
from faas_runner_docker import DockerFaasRunner
from sqs.sqs_message_consumer import DockerSqsMessageConsumer, SqsMessageConsumer
from sqs.sqs_message_producer import DockerSqsMessageProducer, SqsMessageProducer
from sqs.sqs_runner import SqsRunner


@pytest.fixture(scope="session")
def network_name():
    return "spring-boot-faas-tests"


@pytest.fixture(scope="session")
def target_sqs_queue():
    return "targetQueue"


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
def sqs_target_message_consumer(docker_client, network_name, target_sqs_queue) -> SqsMessageConsumer:
    return DockerSqsMessageConsumer(docker_client, network_name, target_sqs_queue)


@pytest.fixture(scope="session")
def faas_image_tag():
    tag = os.getenv("CIRCLE_SHA1", "latest")
    logging.info(f"Using faas tag [ {tag} ]")
    return tag


@pytest.fixture(scope="session")
def faas_runner(docker_client, network_name, faas_image_tag):
    return DockerFaasRunner(docker_client, network_name, faas_image_tag)


@pytest.fixture
def run_instance(sqs, faas_runner, target_sqs_queue):
    container = faas_runner.run({
        "EVENT_SOURCE_SQS_ENABLED": "true",
        "EVENT_TARGET_SQS_ENABLED": "true",
        "SPRING_CLOUD_AWS_REGION_STATIC": "us-east-1",
        "SPRING_CLOUD_AWS_SQS_ENDPOINT": sqs.hostname(),
        "SPRING_CLOUD_AWS_CREDENTIALS_ACCESS_KEY": "dummy",
        "SPRING_CLOUD_AWS_CREDENTIALS_SECRET_KEY": "dummy",
        "ROOT_SOURCE_PROPS_QUEUENAME": "testQueue",
        "ROOT_TARGET_PROPS_QUEUENAME": target_sqs_queue,
        "ROOT_TARGET_FACTORY": "sqsEventTargetFactory"
    })
    yield
    container.stop()


def test_something(run_instance, sqs_message_producer, sqs_target_message_consumer):
    sleep(5)  # TODO: Improve this to wait for application to be healthy.
    sqs_message_producer.produce("{\"firstName\":\"Leon\",\"lastName\":\"Patmore\"}", "testQueue")
    messages = sqs_target_message_consumer.get_messages()
    assert len(messages) == 1
    assert messages[0]["Body"] == "Hello Leon Patmore!"
