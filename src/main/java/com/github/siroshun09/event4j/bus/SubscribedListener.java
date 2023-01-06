/*
 *     Copyright (c) 2020-2023 Siroshun09
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

import com.github.siroshun09.event4j.key.Key;
import com.github.siroshun09.event4j.listener.Listener;
import com.github.siroshun09.event4j.priority.Priority;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A record that represents subscribed {@link Listener}.
 *
 * @param <E>        the event type
 * @param eventClass tye event {@link Class}
 * @param key        the key of that {@link Listener}
 * @param listener   the {@link Listener} instance
 * @param priority   the priority of that {@link Listener}
 */
public record SubscribedListener<E>(@NotNull Class<E> eventClass, @NotNull Key key,
                                    @NotNull Listener<E> listener, @NotNull Priority priority) {

    /**
     * Creates a new {@link SubscribedListener}.
     *
     * @param eventClass tye event {@link Class}
     * @param key        the key of that {@link Listener}
     * @param listener   the {@link Listener} instance
     * @param priority   the priority of that {@link Listener}
     */
    public SubscribedListener(@NotNull Class<E> eventClass, @NotNull Key key,
                              @NotNull Listener<E> listener, @NotNull Priority priority) {
        this.eventClass = Objects.requireNonNull(eventClass);
        this.key = Objects.requireNonNull(key);
        this.listener = Objects.requireNonNull(listener);
        this.priority = Objects.requireNonNull(priority);
    }
}
