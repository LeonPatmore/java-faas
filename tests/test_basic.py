import logging
import os

import docker
import pytest

from docker_utils import create_network
from faas_runner_docker import DockerFaasRunner
from sqs.sqs_message_consumer import DockerSqsMessageConsumer, SqsMessageConsumer
from sqs.sqs_message_producer import DockerSqsMessageProducer, SqsMessageProducer
from sqs.sqs_runner import SqsRunner

FILE_PATH = os.path.abspath(__file__)


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
def faas_image_name():
    name = os.getenv("FAAS_IMAGE_NAME", "leonpatmore2/spring-boot-faas:latest")
    logging.info(f"Using faas image name [ {name} ]")
    return name


@pytest.fixture(scope="session")
def faas_handler_jar_path():
    path = os.getenv("HANDLER_PATH", f"{FILE_PATH}/../../example/build/libs/example-0.0.1-SNAPSHOT-plain.jar")
    logging.info(f"Using faas handler jar path [ {path} ]")
    return path


@pytest.fixture(scope="session")
def faas_runner(docker_client, network_name, faas_image_name, faas_handler_jar_path):
    return DockerFaasRunner(docker_client, network_name, faas_image_name, faas_handler_jar_path)


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


def test_sqs_to_sqs(run_instance, sqs_message_producer, sqs_target_message_consumer):
    sqs_message_producer.produce("{\"firstName\":\"Leon\",\"lastName\":\"Patmore\"}", "testQueue")
    messages = sqs_target_message_consumer.get_messages()
    assert len(messages) == 1
    assert messages[0]["Body"] == "Hello Leon Patmore!"
