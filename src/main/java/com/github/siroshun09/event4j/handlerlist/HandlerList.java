/*
 *     Copyright (c) 2020 Siroshun09
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

package com.github.siroshun09.event4j.handlerlist;

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.listener.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An interface that manages listeners of the event.
 *
 * @param <T> the type of event
 */
public interface HandlerList<T extends Event> {

    /**
     * Creates a new {@link HandlerList}.
     *
     * @param <T> the type of event
     * @return a new {@link HandlerList}
     */
    @Contract(value = " -> new", pure = true)
    @NotNull
    static <T extends Event> HandlerList<T> create() {
        return new HandlerListImpl<>();
    }

    /**
     * Dispatches the event to subscribed listeners.
     *
     * @param event the event instance
     */
    void post(@NotNull T event);

    /**
     * Subscribes the listener.
     * <p>
     * This method subscribes the listener with {@link Priority#NORMAL}.
     *
     * @param key      the key to be used to subscribe the listener
     * @param listener the listener to subscribe
     * @return {@code true} if it is subscribed, {@code false} otherwise
     */
    boolean subscribe(@NotNull Key key, @NotNull Listener<T> listener);

    /**
     * Subscribes the listener.
     *
     * @param key      the key to be used to subscribe the listener
     * @param listener the listener to subscribe
     * @param priority the priority of the listener
     * @return {@code true} if it is subscribed, {@code false} otherwise
     */
    boolean subscribe(@NotNull Key key, @NotNull Listener<T> listener, @NotNull Priority priority);

    /**
     * Unsubscribes the listener.
     * <p>
     * The listener instance must be the same as when you subscribed it to be unregistered.
     *
     * @param listener the listener instance to unsubscribe
     * @return {@code true} if it is unsubscribed, {@code false} otherwise
     */
    boolean unsubscribe(@NotNull Listener<T> listener);

    /**
     * Unsubscribes the listener of the specified priority.
     * <p>
     * The listener instance must be the same as when you subscribed it to be unsubscribed.
     *
     * @param listener the listener instance to unsubscribe
     * @param priority the priority of the listener.
     * @return {@code true} if it is unsubscribed, {@code false} otherwise
     */
    boolean unsubscribe(@NotNull Listener<T> listener, @NotNull Priority priority);

    /**
     * Unsubscribes all listeners subscribed with the specified {@link Key}.
     *
     * @param key the key to unsubscribe
     */
    void unsubscribeAll(@NotNull Key key);

    /**
     * Gets the number of subscribed listeners.
     *
     * @return the number of subscribed listeners.
     */
    int getSize();
}
