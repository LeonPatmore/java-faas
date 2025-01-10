plugins {
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("io.mockk:mockk:1.13.14")
}
