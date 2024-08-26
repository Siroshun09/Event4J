/**
 * Event4J Tree provides "tree" implementation for dispatching/listening events.
 * <p>
 * The implementation details are at {@link dev.siroshun.event4j.tree.TreeEventService}.
 */
module dev.siroshun.event4j.tree {
    requires org.jetbrains.annotations;
    requires dev.siroshun.event4j.api;

    exports dev.siroshun.event4j.tree;
}
