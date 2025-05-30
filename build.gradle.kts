/*
 *     Copyright (c) 2020-2025 Siroshun09
 *
 *     This file is part of event4j.
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

plugins {
    alias(libs.plugins.jcommon)
    alias(libs.plugins.aggregated.javadoc)
    alias(libs.plugins.mavenPublication)
    alias(libs.plugins.mavenCentralPortal)
}

jcommon {
    javaVersion = JavaVersion.VERSION_17

    commonDependencies {
        compileOnlyApi(libs.annotations)

        testImplementation(platform(libs.junit.bom))
        testImplementation(libs.junit.jupiter)
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

aggregatedJavadoc {
    modules = listOf("org.jetbrains.annotations")
}

mavenPublication {
    localRepository(mavenCentralPortal.stagingDirectory)
    description("An event library for Java.")
    mitLicense()
    developer("Siroshun09")
    github("Siroshun09/Event4J")
}
