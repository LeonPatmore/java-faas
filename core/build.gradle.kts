dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(project(":common"))

    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}
