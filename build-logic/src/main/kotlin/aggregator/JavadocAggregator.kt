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

package aggregator

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

object JavadocAggregator {

    private const val COLLECT_JAVADOC_TASK = "collectJavadocTask"
    const val AGGREGATE_JAVADOC_TASK_NAME = "aggregateJavadoc"

    val javadocLinks: MutableSet<String> = HashSet()
    val includes: MutableSet<String> = HashSet()
    val excludes: MutableSet<String> = HashSet()

    fun addProject(project: Project) {
        val javadocTask = project.tasks.withType<Javadoc>()[JavaPlugin.JAVADOC_TASK_NAME]
        val collectTask = project.tasks.register(COLLECT_JAVADOC_TASK).get()
        val javadocClasspath = project.files().builtBy(collectTask)

        collectTask.doFirst {
            javadocClasspath.from(javadocTask.classpath.files).builtBy(javadocTask.classpath)
            includes.addAll(javadocTask.includes)
            excludes.addAll(javadocTask.excludes)
            (javadocTask.options as StandardJavadocDocletOptions).links?.let { javadocLinks.addAll(it) }
        }

        project.rootProject.tasks.named<Javadoc>(AGGREGATE_JAVADOC_TASK_NAME) {
            dependsOn(project.tasks.getByName(JavaPlugin.CLASSES_TASK_NAME))
            source(javadocTask.source)
            (classpath as ConfigurableFileCollection).from(javadocClasspath)
        }
    }
}
