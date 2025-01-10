
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs:3.2.1")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    compileOnly(project(":common"))

    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("org.awaitility:awaitility:4.2.2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:localstack")
    testImplementation(testFixtures(project(":common")))
}
