import java.net.URI

plugins {
    `java-test-fixtures`
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI.create("https://maven.pkg.github.com/LeonPatmore/spring-boot-faas")
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

dependencies {
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesApi("io.mockk:mockk:1.13.14")
}
