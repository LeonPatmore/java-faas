dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly(project(":common"))

    testImplementation(testFixtures(project(":common")))
}
