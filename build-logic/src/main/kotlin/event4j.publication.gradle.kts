/*
 *     Copyright (c) 2020-2024 Siroshun09
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
    id("event4j.common-conventions")
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            java {
                withJavadocJar()
                withSourcesJar()
            }

            groupId = project.group.toString()
            artifactId = project.name

            from(components["java"])

            pom {
                name.set(project.name)
                url.set("https://github.com/Siroshun09/Event4J")
                description.set("An event library for Java.")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/license/mit")
                    }
                }

                developers {
                    developer {
                        name.set("Siroshun09")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/Siroshun09/Event4J.git")
                    developerConnection.set("scm:git@github.com:Siroshun09/Event4J.git")
                    url.set("https://github.com/Siroshun09/Event4J")
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/Siroshun09/Event4J/issues")
                }

                ciManagement {
                    system.set("GitHub Actions")
                    url.set("https://github.com/Siroshun09/Event4J/runs")
                }
            }
        }

        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.dir("staging"))
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}
