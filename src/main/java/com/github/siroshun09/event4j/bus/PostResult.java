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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * A record that represents the result of {@link EventSubscriber#post(Object)}.
 *
 * @param <E>        the event type
 * @param event      the event instance
 * @param exceptions the exceptions that occurred while posting an event
 */
public record PostResult<E>(@NotNull E event, @NotNull @Unmodifiable Map<SubscribedListener<E>, Throwable> exceptions) {

    /**
     * Creates a new {@link PostResult}.
     *
     * @param event the event instance
     * @param <E>   the event type
     * @return a new {@link PostResult} that indicates success
     */
    @Contract(value = "_ -> new", pure = true)
    public static <E> @NotNull PostResult<E> success(@NotNull E event) {
        return new PostResult<>(event, Collections.emptyMap());
    }

    /**
     * Creates a new {@link PostResult}.
     *
     * @param event      the event instance
     * @param exceptions the exceptions that occurred while posting an event
     * @param <E>        the event type
     * @return a new {@link PostResult} that indicates failure
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static <E> @NotNull PostResult<E> failure(@NotNull E event,
                                                     @NotNull Map<SubscribedListener<E>, Throwable> exceptions) {
        if (!exceptions.isEmpty()) {
            return new PostResult<>(event, exceptions);
        } else {
            throw new IllegalArgumentException("The exception map is empty.");
        }
    }

    /**
     * The constructor of {@link PostResult}. Use {@link #success(Object)} or {@link #failure(Object, Map)}
     *
     * @param event      the event
     * @param exceptions the exceptions
     */
    @ApiStatus.Internal
    public PostResult(@NotNull E event, @NotNull Map<SubscribedListener<E>, Throwable> exceptions) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(exceptions);

        this.event = event;
        this.exceptions = Collections.unmodifiableMap(exceptions);
    }

    /**
     * Check if this result indicates success or not.
     *
     * @return whether these results indicate success or not.
     */
    public boolean isSuccess() {
        return exceptions.isEmpty();
    }

    /**
     * Check if this result indicates failure or not.
     *
     * @return whether these results indicate failure or not.
     */
    public boolean isFailure() {
        return !isSuccess();
    }
}
