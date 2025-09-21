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

package dev.siroshun.event4j.api.listener;

import dev.siroshun.event4j.api.caller.EventCaller;
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
     * Creates a {@link ListenerExceptionHandler} that returns {@link Result#CONTINUE}.
     *
     * @param <K> the key type
     * @param <E> the event type
     * @param <O> the order type
     * @return a {@link ListenerExceptionHandler} that returns {@link Result#CONTINUE}
     */
    static <K, E, O> @NotNull ListenerExceptionHandler<K, E, O> continueHandler() {
        return (event, listener, e) -> Result.CONTINUE;
    }

    /**
     * Handles the exception.
     *
     * @param event     the event instance
     * @param listener  the {@link SubscribedListener} that throws an exception
     * @param exception the thrown exception
     * @return the {@link Result} that indicates what the {@link EventCaller} should do next
     */
    @NotNull Result handleException(@NotNull E event, @NotNull SubscribedListener<K, ? extends E, O> listener, @NotNull Throwable exception);

    /**
     * The operations that indicates what the {@link EventCaller} should do next
     */
    enum Result {

        /**
         * A {@link Result} that indicates that the {@link EventCaller} should not post the event to subsequent listeners.
         */
        BREAK,

        /**
         * A {@link Result} that indicates that the {@link EventCaller} should post the event to subsequent listeners.
         */
        CONTINUE,

        /**
         * A {@link Result} that indicates that {@link EventCaller} should re-throw an exception.
         */
        RETHROW

    }
}
