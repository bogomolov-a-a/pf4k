plugins {
    id("common-build-plugin")
}
dependencies {
    implementation(project(":pf4k-api"))
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}