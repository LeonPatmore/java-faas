import pytest

from faas_runner_docker import DockerFaasRunner


@pytest.fixture
def faas_runner():
    return DockerFaasRunner()


def test_something(faas_runner):
    container = faas_runner.run({
        "EVENT_SOURCE_SQS_ENABLED": "true",
        "SPRING_CLOUD_AWS_REGION_STATIC": "us-east-1",
        "SPRING_CLOUD_AWS_SQS_ENDPOINT": "http://localstack:4566",
        "SPRING_CLOUD_AWS_CREDENTIALS_ACCESS_KEY": "dummy",
        "SPRING_CLOUD_AWS_CREDENTIALS_SECRET_KEY": "dummy"
    })
    pass
