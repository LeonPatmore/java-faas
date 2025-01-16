import pytest

from docker_utils import create_network
from faas_runner_docker import DockerFaasRunner
from sqs.sqs_runner import SqsRunner


@pytest.fixture(scope="session")
def network_name():
    return "spring-boot-faas-tests"


@pytest.fixture(scope="session", autouse=True)
def setup_network(network_name):
    create_network(network_name)


@pytest.fixture(scope="session")
def sqs(network_name):
    sqs = SqsRunner(network_name)
    sqs.start()
    yield sqs
    sqs.stop()


@pytest.fixture(scope="session")
def faas_runner(network_name):
    return DockerFaasRunner(network_name)


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


def test_something(run_instance):
    pass
