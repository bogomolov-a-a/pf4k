plugins {
    id("common-build-plugin")
    `kotlin-dsl`
}

dependencies {
    implementation(project(":pf4k-app-launcher"))
    implementation(project(":pf4k-app-impl"))
    implementation(project(":pf4k-api"))
}
val COMMON_APP_LAUNCHER_PLUGIN_ID = "common-app-launcher-plugin"
gradlePlugin {
    plugins {
        create(COMMON_APP_LAUNCHER_PLUGIN_ID) {
            id = COMMON_APP_LAUNCHER_PLUGIN_ID
            implementationClass = "org.artembogomolova.pf4k.plugins.CommonAppLauncherPlugin"
        }
    }
}