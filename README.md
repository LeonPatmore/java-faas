# Spring Boot Faas

A Spring Boot image for wrapping functions written with Spring Boot.

## Example

### Writing a Function

See [example](example) for a basic example of how to write a function.

### Running the Function

Running the function is simple! Simple run the `spring-boot-faas` image and pass in your function jar to the following path:
`/app/handler/handler.jar`.

```
function:
image: spring-boot-faas
ports:
    - "8080:8080"
environment:
    - EVENT_SOURCE_SQS_ENABLED=true
    - SPRING_CLOUD_AWS_REGION_STATIC=us-east-1
    - SPRING_CLOUD_AWS_SQS_ENDPOINT=http://localstack:4566
    - SPRING_CLOUD_AWS_CREDENTIALS_ACCESS_KEY=dummy
    - SPRING_CLOUD_AWS_CREDENTIALS_SECRET_KEY=dummy
    - ROOT_SOURCE_PROPS_QUEUENAME=testQueue
volumes:
    - ../example/build/libs/example-0.0.1-SNAPSHOT-plain.jar:/app/handler/handler.jar
```

### Properties

The image can run a single function (called a `root` function) or can run multilpe functions.

For running a single function:

- `root.source.factory`: Optional. If unset, there must be exactly on source factory enabled.
- `root.source.props`: Properties passed to the source factory. Check factory for more details.
- `root.target.factory`: Optional. If unset, no event target is used.
- `root.target.props`: Properties passed to the target factory. Check factory for more details.

For running multiple functions:

- `functions.<func_name>.handler`: Required. The handler bean name for this function.
- `functions.<func_name>.source.factory`: Optional. If unset, there must be exactly on source factory enabled.
- `functions.<func_name>.source.props`: Properties passed to the source factory. Check factory for more details.
- `functions.<func_name>.target.factory`: Optional. If unset, no event target is used.
- `functions.<func_name>.target.props`: Properties passed to the target factory. Check factory for more details.

## Default Factories

### SQS

#### Source

`event.source.sqs.enabled=true`

Properties:

- `queueName`: Required.

#### Target

`event.target.sqs.enabled=true`

Properties:

- `queueName`: Required.

## Custom Factories

TODO
