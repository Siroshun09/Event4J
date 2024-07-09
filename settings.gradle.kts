pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "event4j"

addProject("api")
addProject("test-helper")

fun addProject(name: String) {
    include("${rootProject.name}-$name")
    project(":${rootProject.name}-$name").projectDir = file(name)
}
