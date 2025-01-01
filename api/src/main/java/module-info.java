/**
 * Event4J API provides common interfaces/classes.
 */
module dev.siroshun.event4j.api {
    requires org.jetbrains.annotations;
    requires org.jspecify;

    exports dev.siroshun.event4j.api.caller;
    exports dev.siroshun.event4j.api.listener;
    exports dev.siroshun.event4j.api.priority;
}
