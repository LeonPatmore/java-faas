dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation(project(":common"))

    // Default source implementations
    runtimeOnly(project(":web"))
    runtimeOnly(project(":sqs"))

    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("org.awaitility:awaitility:4.2.2")
    testImplementation(testFixtures(project(":common")))
}
