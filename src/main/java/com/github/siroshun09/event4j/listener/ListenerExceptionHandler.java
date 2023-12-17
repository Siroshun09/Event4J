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

package com.github.siroshun09.event4j.listener;

import org.jetbrains.annotations.NotNull;

/**
 * An interface to handle the exception that is thrown from {@link SubscribedListener#consumer()}.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
@FunctionalInterface
public interface ListenerExceptionHandler<K, E, O> {

    /**
     * Handles the exception
     *
     * @param event     the event instance
     * @param listener  the {@link SubscribedListener} that throws an exception
     * @param exception the thrown exception
     * @return the {@link Result} that indicates what the {@link com.github.siroshun09.event4j.caller.EventCaller} should do next
     */
    @NotNull Result handleException(@NotNull E event, @NotNull SubscribedListener<K, ? extends E, O> listener, @NotNull Throwable exception);

    /**
     * The operations that indicates what the {@link com.github.siroshun09.event4j.caller.EventCaller} should do next
     */
    enum Result {

        /**
         * A {@link Result} that indicates that the {@link com.github.siroshun09.event4j.caller.EventCaller} should not post the event to subsequent listeners.
         */
        BREAK,

        /**
         * A {@link Result} that indicates that the {@link com.github.siroshun09.event4j.caller.EventCaller} should post the event to subsequent listeners.
         */
        CONTINUE,

        /**
         * A {@link Result} that indicates that {@link com.github.siroshun09.event4j.caller.EventCaller} should re-throw an exception.
         */
        RETHROW
    }
}
