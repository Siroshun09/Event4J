plugins {
    id("event4j.common-conventions")
    id("event4j.publication")
}

dependencies {
    testImplementation(projects.event4jTestHelper)
}

afterEvaluate {
    aggregator.JavadocAggregator.addProject(this)
}
