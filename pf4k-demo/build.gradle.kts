plugins {
    id("common-build-plugin")
}
/*dependencies {
    implementation(project(":pf4k-impl"))
}*/
/*configurations.implementation.get().isCanBeResolved = true
val dependenciesSet = configurations.runtimeClasspath.get().asFileTree
val classpathEntryNamePattern = " common/%s"
val classPathAttributeName = "Class-Path"
var classpath: String = ""
var i = 0

dependenciesSet.forEach {
    /*
 * https://stackoverflow.com/questions/33758244/add-classpath-in-manifest-file-of-jar-in-gradle-in-java-8
 */
/*    val classpathEntryName = classpathEntryNamePattern.format(it.name)
    classpath += if (i++ == 0) {
        String.format("%0\$-60s", classpathEntryName)
    } else {
        String.format("%0\$-71s", classpathEntryName)
    }
}

classpath = classpath.substring(0 until classpath.length - 1)
tasks.jar {
    manifest {
        attributes["Application-Name"] = "pf4k-demo"
        attributes["Main-Class"] = "org.artembogomova.pf4k.impl.ModularizedApplication"
        attributes[classPathAttributeName] = classpath
    }
    finalizedBy("commonJarCopy")
}
tasks.register("commonJarCopy", Copy::class.java) {
    dependenciesSet.forEach {
        from(it.absolutePath)
    }
    into("$buildDir/libs/common")
}*/
