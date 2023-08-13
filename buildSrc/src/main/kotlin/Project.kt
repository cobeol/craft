import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

private fun Project.subproject(name: String) = project(":${rootProject.name}-$name")

val Project.projectApi
    get() = subproject("api")

val Project.projectCore
    get() = subproject("core")

val Project.projectPlugin
    get() = subproject("plugin")
