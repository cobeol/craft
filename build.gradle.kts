plugins {
    idea
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.dokka) apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.get().pluginId)
    apply(plugin = rootProject.libs.plugins.serialization.get().pluginId)

    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly(rootProject.libs.paper)

        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        implementation(rootProject.libs.json)
        implementation(rootProject.libs.protobuf)
    }
}

listOf(projectApi, projectCore).forEach { module ->
    with(module) {
        apply(plugin = rootProject.libs.plugins.dokka.get().pluginId)

        tasks {
            create<Jar>("sourcesJar") {
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }

            create<Jar>("dokkaJar") {
                archiveClassifier.set("javadoc")
                dependsOn("dokkaHtml")

                from(layout.buildDirectory.dir("dokka/html")) {
                    include("**")
                }
            }
        }
    }
}

tasks {
    register<DefaultTask>("setupModules") {
        doLast {
            val defaultPrefix = "sample"
            val projectPrefix = rootProject.name

            if (defaultPrefix != projectPrefix) {
                fun rename(suffix: String) {
                    val from = "$defaultPrefix-$suffix"
                    val to = "$projectPrefix-$suffix"
                    file(from).takeIf { it.exists() }?.renameTo(file(to))
                }

                rename("api")
                rename("core")
                rename("plugin")
                rename("publish")
            }
        }
    }
}

idea {
    module {
        excludeDirs.add(file(".server"))
        excludeDirs.addAll(allprojects.map { it.layout.buildDirectory.get().asFile })
        excludeDirs.addAll(allprojects.map { it.file(".gradle") })
    }
}
