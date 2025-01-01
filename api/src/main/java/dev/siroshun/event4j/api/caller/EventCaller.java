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

package dev.siroshun.event4j.api.caller;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * An interface to call events.
 *
 * @param <E> the event type
 */
@FunctionalInterface
@NullMarked
public interface EventCaller<E> {

    /**
     * Creates a new {@link EventCaller} that calls {@link #call(Object)} on the given {@link Executor}.
     *
     * @param caller the original {@link EventCaller}
     * @param executor the {@link Executor} to use calling {@link #call(Object)}
     * @param <E> the event type
     * @return a new {@link EventCaller}
     */
    @Contract(value = "_, _ -> new", pure = true)
    static <E> EventCaller<E> asyncCaller(EventCaller<E> caller, Executor executor) {
        Objects.requireNonNull(caller, "caller cannot be null.");
        Objects.requireNonNull(executor, "executor cannot be null.");
        return new AsyncEventCaller<>(caller, executor);
    }

    /**
     * Calls the event.
     *
     * @param event the event instance
     */
    void call(E event);

    /**
     * Calls the event.
     *
     * @param event the event instance
     * @param callback the {@link Consumer} that accepts the event after calling.
     * @param <T>   the event type that inherits from {@link E}
     */
    default <T extends E> void call(T event, Consumer<? super T> callback) {
        Objects.requireNonNull(event, "event cannot be null.");
        Objects.requireNonNull(callback, "callback cannot be null.");
        this.call(event);
        callback.accept(event);
    }
}
