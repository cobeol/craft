rootProject.name = "craft"

val prefix = rootProject.name

include("$prefix-core")
include("$prefix-api")

val dongle = "$prefix-dongle"
include(dongle)
file(dongle).listFiles()?.filter {
    it.isDirectory && it.name.startsWith("v")
}?.forEach { file ->
    include(":$dongle:${file.name}")
}

include("$prefix-plugin")
include("$prefix-publish")