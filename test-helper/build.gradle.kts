plugins {
    id("event4j.common-conventions")
}

dependencies {
    implementation(projects.event4jApi)
    api(platform(libs.junit.bom))
    api(libs.junit.jupiter)
}
