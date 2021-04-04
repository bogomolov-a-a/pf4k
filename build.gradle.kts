plugins {
    id("maven-publish")
    id("publishing")
}
/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/6.8.2/samples
 */
allprojects {
    val projectGroup: String by project
    group = projectGroup
    version = getProjectVersion()
}

tasks.withType<Wrapper>().configureEach {
    val gradleWrapperVersion: String by project
    gradleVersion = gradleWrapperVersion
    distributionType = Wrapper.DistributionType.BIN

}
repositories {
    mavenCentral()
    gradlePluginPortal()

}

buildscript {
    dependencies {
        classpath("org.artembogomolova.common:common-build-plugins:1.0.10")
    }

    val GITHUB_BUILD_PLUGINS_REPOSITORY_ENV_NAME = "GITHUB_BUILD_PLUGINS_REPOSITORY"
    val githubBuildPluginsRepository: String? by project
    val buildPluginRepositoryUrl = System.getenv(GITHUB_BUILD_PLUGINS_REPOSITORY_ENV_NAME) ?: githubBuildPluginsRepository
    val USERNAME_ENV_NAME = "USERNAME"
    val TOKEN_ENV_NAME = "TOKEN"
    val login: String? by project
    val token: String? by project
    val gprLogin: String = (System.getenv(USERNAME_ENV_NAME) ?: login) as String
    val gprPassword: String = (System.getenv(TOKEN_ENV_NAME) ?: token) as String

    repositories {
        mavenCentral()
        gradlePluginPortal()

        maven {
            url = java.net.URI(buildPluginRepositoryUrl)
            credentials {
                username = gprLogin
                password = gprPassword
            }
        }

    }
}
plugins.apply("common-base-plugin")
val USERNAME_ENV_NAME = "USERNAME"
val TOKEN_ENV_NAME = "TOKEN"
val login: String? by project
val token: String? by project
val gprLogin: String = (System.getenv(USERNAME_ENV_NAME) ?: login) as String
val gprPassword: String = (System.getenv(TOKEN_ENV_NAME) ?: token) as String
val pf4kRegistryUrl: String? by project
val mavenPackageRegistryUri: String =
    (pf4kRegistryUrl ?: (System.getenv("MAVEN_PACKAGE_REGISTRY_URL") + System.getenv("GITHUB_REPOSITORY")))

subprojects {
    plugins.apply("common-no-ext-tool-build-plugin")
    plugins.apply("maven-publish")
    plugins.apply("publishing")

    publishing {
        repositories {
            maven {
                url = java.net.URI(mavenPackageRegistryUri)
                credentials {
                    username = gprLogin
                    password = gprPassword
                }
            }
        }
        publications {
            create<MavenPublication>(this@subprojects.name) {
                from(components["java"])
            }
        }
    }
    dependencies {
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.2")

    }
}
with(tasks.getByPath(":pf4k-plugins:publishPluginMavenPublicationToMavenRepository")) {
    enabled = false
}
fun getProjectVersion(): String {
    val projectVersion: String? = System.getenv("GITHUB_REF")
    return if (null == projectVersion) {
        "local-SNAPSHOT"
    } else {
        val TAG_PREFIX = "refs/tags/v"
        val tagIndex = projectVersion.indexOf(TAG_PREFIX)
        if (tagIndex > -1) {
            projectVersion.substring(tagIndex + TAG_PREFIX.length)
        } else {
            val HEADS_PREFIX = "refs/heads/"
            val headsIndex = projectVersion.indexOf(HEADS_PREFIX)
            /*branch-commit_sha*/
            projectVersion.substring(headsIndex + HEADS_PREFIX.length) + "-" + System.getenv("GITHUB_SHA")
        }
    }

}