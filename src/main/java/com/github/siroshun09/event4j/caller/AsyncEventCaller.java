/*
 *     Copyright (c) 2020-2024 Siroshun09
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

package com.github.siroshun09.event4j.caller;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * An interface that supports async event calling.
 *
 * @param <E> the event type
 */
public interface AsyncEventCaller<E> extends EventCaller<E> {

    /**
     * Creates a new {@link AsyncEventCaller} with specified {@link EventCaller} and {@link Executor}.
     *
     * @param eventCaller the {@link EventCaller} that calls events
     * @param executor    the {@link Executor} that executes {@link #call(Object)} asynchronously
     * @param <E>         the event type
     * @return a new {@link AsyncEventCaller}
     */
    @Contract(value = "_, _ -> new", pure = true)
    static <E> @NotNull AsyncEventCaller<E> create(@NotNull EventCaller<E> eventCaller, @NotNull Executor executor) {
        return new DefaultAsyncEventCaller<>(eventCaller, executor);
    }

    /**
     * Calls {@link #call(Object)} asynchronously.
     *
     * @param event the event instance
     * @param <T>   the event type that inherits from {@link E}
     */
    <T extends E> void callAsync(@NotNull T event);

    /**
     * Calls {@link #call(Object)} asynchronously.
     *
     * @param event    the event instance
     * @param callback the {@link Consumer} that is used as a callback after calling the event
     * @param <T>      the event type that inherits from {@link E}
     */
    <T extends E> void callAsync(@NotNull T event, @Nullable Consumer<? super T> callback);

}
