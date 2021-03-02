package org.artembogomolova.build.utils

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import java.io.File
import org.artembogomolova.build.plugins.BUILD_DIR_PATH_PROPERTY_NAME

private class ClassPathClassFounder(buildDir: String) {
    companion object {
        val MAIN_CLASSES_SET: String = "%s/classes/kotlin/main" + File.pathSeparator + "%s/classes/java/main" + File.pathSeparator
    }

    private val searchingPath = MAIN_CLASSES_SET.format(buildDir, buildDir)

    private val classFounder = ClassGraph().overrideClasspath(searchingPath).verbose()
        .enableAllInfo()

    fun getAllClassInfo(action: (classInfo: ClassInfo) -> Unit) {
        classFounder.scan().use { scanResult ->
            scanResult.allClasses.forEach { classInfo -> action(classInfo) }
        }
    }
}

fun excludeGeneratedModelClasses(clazzInfo: ClassInfo): Boolean = clazzInfo.name.endsWith("_")
fun findCoverageClasses(properties: Map<String, Any>, availableToExclude: (ClassInfo) -> Boolean): Pair<List<String>, List<String>> {
    val includes = ArrayList<String>()
    val excludes = ArrayList<String>()
    val result = Pair<List<String>, List<String>>(includes, excludes)
    val classPathClassFounder = ClassPathClassFounder(properties[BUILD_DIR_PATH_PROPERTY_NAME] as String)
    println("try to search includes and excludes in '$classPathClassFounder.searchingPath' for project ")
    classPathClassFounder.getAllClassInfo { clazzInfo ->
        if (availableToExclude(clazzInfo)) {
            excludes.add(clazzInfo.name)
        } else {
            includes.add(clazzInfo.name)
        }
    }
    println("found includes: ${result.first}")
    println("found excludes: ${result.second}")
    return result
}