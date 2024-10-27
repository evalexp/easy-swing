plugins {
    id("java")
    id("eu.kakde.gradle.sonatype-maven-central-publisher") version "1.0.6"
}

group = "io.github.evalexp"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.yaml:snakeyaml:2.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}

// ------------------------------------
// PUBLISHING TO SONATYPE CONFIGURATION
// ------------------------------------

val COMPONENT_TYPE = "java" // "java" or "versionCatalog"
val GROUP: String = group.toString()
val ARTIFACT_ID = rootProject.name
val VERSION = version.toString()
val PUBLISHING_TYPE = "AUTOMATIC" // USER_MANAGED or AUTOMATIC
val SHA_ALGORITHMS = listOf("SHA-256", "SHA-512") // sha256 and sha512 are supported but not mandatory. Only sha1 is mandatory but it is supported by default.
val DESC = "A swing framework to support i18n and auto-wired features"
val LICENSE = "Apache-2.0"
val LICENSE_URL = "https://opensource.org/licenses/Apache-2.0"
val GITHUB_REPO = "evalexp/easy-swing.git"
val DEVELOPER_ID = "evalexp"
val DEVELOPER_NAME = "Zhou Hao"


val sonatypeUsername: String? by project // this is defined in ~/.gradle/gradle.properties
val sonatypePassword: String? by project // this is defined in ~/.gradle/gradle.properties

sonatypeCentralPublishExtension {
    // Set group ID, artifact ID, version, and other publication details
    groupId.set(GROUP)
    artifactId.set(ARTIFACT_ID)
    version.set(VERSION)
    componentType.set(COMPONENT_TYPE) // "java" or "versionCatalog"
    publishingType.set(PUBLISHING_TYPE) // USER_MANAGED or AUTOMATIC

    // Set username and password for Sonatype repository
    username.set(System.getenv("SONATYPE_USERNAME") ?: sonatypeUsername)
    password.set(System.getenv("SONATYPE_PASSWORD") ?: sonatypePassword)

    // Configure POM metadata
    pom {
        name.set(ARTIFACT_ID)
        description.set(DESC)
        url.set("https://github.com/${GITHUB_REPO}")
        licenses {
            license {
                name.set(LICENSE)
                url.set(LICENSE_URL)
            }
        }
        developers {
            developer {
                id.set(DEVELOPER_ID)
                name.set(DEVELOPER_NAME)
            }
        }
        scm {
            url.set("https://github.com/${GITHUB_REPO}")
            connection.set("scm:git:https://github.com/${GITHUB_REPO}")
            developerConnection.set("scm:git:https://github.com/${GITHUB_REPO}")
        }
        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/${GITHUB_REPO}/issues")
        }
    }
}
