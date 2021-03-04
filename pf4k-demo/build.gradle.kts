plugins {
    id("common-build-plugin")
}
dependencies {
    implementation(project(":pf4k-api"))
    implementation(project(":pf4k-impl"))
}
tasks.jar {
    manifest {
        attributes["Application-Name"] = "pf4k-demo"
        //attributes["Main-Class"] = ModularityApplication::class.java.name
    }
}
