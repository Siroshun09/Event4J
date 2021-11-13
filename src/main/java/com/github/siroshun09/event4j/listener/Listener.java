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

package com.github.siroshun09.event4j.listener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An interface to receive and process events.
 *
 * @param <E> the event type
 */
@FunctionalInterface
public interface Listener<E> {

    /**
     * Creates a new {@link Listener}.
     *
     * @param consumer the consumer to consume an event
     * @param <E>      the event type
     * @return a new {@link Listener}
     */
    @Contract(pure = true)
    static <E> @NotNull Listener<E> create(@NotNull Consumer<E> consumer) {
        return consumer::accept;
    }

    /**
     * The method to receive events.
     * <p>
     * If an exception is thrown while calling this method, {@link #handleException(Object, Throwable)} will be called.
     *
     * @param event the event
     */
    void handle(@NotNull E event);

    /**
     * The method to be called when an exception is thrown in {@link #handle(Object)}.
     * <p>
     * This method does nothing by default.
     *
     * @param event     the event
     * @param throwable the exception that caused
     */
    default void handleException(@NotNull E event, @NotNull Throwable throwable) {
        // do nothing by default
    }
}
