import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version ("1.4.20")
    id("java")
    `kotlin-dsl`
}

val kotlinVersion = "1.4.20"
val springBootVersion = "2.4.2"
val springDependencyManagementVersion = "1.0.11.RELEASE"
val classGraphVersion = "4.8.98"
val detektPluginVersion = "1.15.0"
val spotbugsPluginVersion = "4.6.0"
val jacocoVersion = "0.8.6"
val sonarPluginVersion = "3.1"
val dokkaVersion = kotlinVersion
val javaVersion = "11"
repositories {
    mavenCentral()
    gradlePluginPortal()
    jcenter()
}
plugins.apply(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper::class.java)
dependencies {
    /*spring*/
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    implementation("io.spring.gradle:dependency-management-plugin:$springDependencyManagementVersion")
    implementation("io.github.classgraph:classgraph:$classGraphVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
    /*quality*/
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektPluginVersion")
    implementation("gradle.plugin.com.github.spotbugs.snom:spotbugs-gradle-plugin:$spotbugsPluginVersion")
    /*test reports*/
    implementation("org.jacoco:org.jacoco.core:$jacocoVersion")
    /*sonar*/
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:$sonarPluginVersion")
    /*documentation*/
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = javaVersion
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = javaVersion
}
/*plugin ids*/
val COMMON_BUILD_PLUGIN_ID = "common-build-plugin"
val COMMON_SPRING_BOOT_WEB_PLUGIN_ID = "common-spring-boot-web-plugin"
val CORE_SPRING_BOOT_PLUGIN_ID = "core-spring-boot-plugin"
val COMMON_SPRING_BOOT_TEST_PLUGIN_ID = "common-spring-boot-test-plugin"
gradlePlugin {
    plugins {
        create(COMMON_BUILD_PLUGIN_ID) {
            id = COMMON_BUILD_PLUGIN_ID
            implementationClass = "org.artembogomolova.build.plugins.CommonBuildPlugin"
        }
        create(CORE_SPRING_BOOT_PLUGIN_ID) {
            id = CORE_SPRING_BOOT_PLUGIN_ID
            implementationClass = "org.artembogomolova.build.plugins.CoreSpringBootPlugin"
        }
        create(COMMON_SPRING_BOOT_WEB_PLUGIN_ID) {
            id = COMMON_SPRING_BOOT_WEB_PLUGIN_ID
            implementationClass = "org.artembogomolova.build.plugins.SpringBootWebPlugin"
        }
        create(COMMON_SPRING_BOOT_TEST_PLUGIN_ID) {
            id = COMMON_SPRING_BOOT_TEST_PLUGIN_ID
            implementationClass = "org.artembogomolova.build.plugins.SpringBootTestPlugin"
        }
    }
}