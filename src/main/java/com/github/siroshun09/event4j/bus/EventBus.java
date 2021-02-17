/*
 *     Copyright (c) 2020-2021 Siroshun09
 *
 *     This file is part of Event4J.
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

package com.github.siroshun09.event4j.bus;

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.handlerlist.HandlerList;
import com.github.siroshun09.event4j.handlerlist.Key;
import com.github.siroshun09.event4j.listener.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * An interface that manages listeners of events and dispatches events.
 */
public interface EventBus {

    /**
     * Creates a new {@link EventBus}.
     *
     * @return a new {@link EventBus}
     * @see SimpleEventBus
     */
    @NotNull
    static EventBus newEventBus() {
        return new SimpleEventBus();
    }

    /**
     * Gets the handler list of the event class.
     * <p>
     * Only one handler list is created per event class in one event bus instance.
     *
     * @param eventClass the event class to get handler list
     * @param <T>        the type of event class
     * @return the handler list of the event class.
     */
    @NotNull <T extends Event> HandlerList<T> getHandlerList(@NotNull Class<T> eventClass);

    /**
     * Unsubscribes all listeners subscribed with the specified {@link Key} for this event bus instance.
     *
     * @param key the key that subscribed listeners
     */
    void unsubscribeAll(@NotNull Key key);

    /**
     * Dispatches the event to subscribed listeners.
     * <p>
     * If an exception is thrown while calling a listener, {@link Listener#handleException(Event, Throwable)} is called and then continues to call the other listeners.
     * That is, if a listener throws an exception, the method is processed to complete successfully.
     * <p>
     * This method also dispatches to the listener of a higher class from which the event class inherits.
     * <p>
     * Example:
     * <p>
     * There is SampleEvent1, which extends Event, and then there is SampleEvent2, which extends SampleEvent1.
     * <p>
     * Calling this method on SampleEvent2 will invoke the subscribed listeners in the order SampleEvent2 → SampleEvent1 → Event.
     *
     * @param event the event instance
     * @param <T>   the event type
     * @return the given event instance
     */
    @NotNull <T extends Event> T callEvent(@NotNull T event);
}
