plugins {
    `java-library`
    jacoco
    alias(libs.plugins.lombok)
    alias(libs.plugins.gradle.maven.publish)
    alias(libs.plugins.sonarqube)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("tools.jackson.core:jackson-databind")
    implementation(libs.logback.classic)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(platform(libs.mockito.bom))
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

    testImplementation(platform(libs.assertj.bom))
    testImplementation("org.assertj:assertj-core")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

mavenPublishing {
    pom {
        name.set("DSSE Java")
        description.set("A Java implementation of the Dead Simple Signing Envelope (DSSE) specification.")
        inceptionYear.set("2026")
        url.set("https://github.com/aigled/dsse-java")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/license/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("aigled")
                name.set("Dorian Aigle")
                url.set("https://github.com/aigled")
            }
        }
        scm {
            url.set("https://github.com/aigled/dsse-java")
            connection.set("scm:git:git://github.com/aigled/dsse-java.git")
            developerConnection.set("scm:git:ssh://git@github.com/aigled/dsse-java.git")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "aigled_dsse-java")
        property("sonar.organization", "aigled")
        property("sonar.junit.reportPaths", "build/test-results")
    }
}

tasks.withType<JavaCompile> {
    options.release = 17
}

tasks.compileJava {
    // Stores formal parameter names of constructors and methods in the generated class file so that the
    // method java.lang.reflect.Executable.getParameters from the Reflection API can retrieve them.
    options.compilerArgs.add("-parameters")
}

tasks.test {
    jvmArgs("-Xshare:off")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
    }
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "${project.group}.${project.name}",
            "Specification-Title" to "DSSE",
            "Specification-Version" to "1.0.2",
            "Specification-Vendor" to "Secure Systems Lab",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Dorian Aigle"
        )
    }
    from(rootProject.file("LICENSE")) {
        into("META-INF")
    }
}
