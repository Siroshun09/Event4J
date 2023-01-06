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
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.function.Predicate;

/**
 * A subscriber to manage {@link Listener}s.
 *
 * @param <E> the type of event
 */
@SuppressWarnings("UnusedReturnValue")
public interface EventSubscriber<E> {

    /**
     * Gets the event {@link Class} of this subscriber.
     *
     * @return the event {@link Class} of this subscriber
     */
    @NotNull Class<E> getEventClass();

    /**
     * Posts the event instance to subscribed listeners.
     *
     * @param event the event instance
     * @return the {@link PostResult}
     * @throws IllegalStateException if this subscriber is already closed
     */
    @NotNull PostResult<E> post(@NotNull E event);

    /**
     * Subscribes the new {@link Listener}.
     *
     * @param key      the key
     * @param listener the {@link Listener} to subscribe
     * @return the {@link SubscribedListener}
     * @throws IllegalStateException if this subscriber is already closed
     */
    @NotNull SubscribedListener<E> subscribe(@NotNull Key key, @NotNull Listener<E> listener);

    /**
     * Subscribes the new {@link Listener}.
     *
     * @param key      the key
     * @param listener the {@link Listener} to subscribe
     * @param priority the priority of the {@link Listener}
     * @return the {@link SubscribedListener}
     * @throws IllegalStateException if this subscriber is already closed
     */
    @NotNull SubscribedListener<E> subscribe(@NotNull Key key, @NotNull Listener<E> listener, @NotNull Priority priority);

    /**
     * Subscribes the new {@link Listener}.
     *
     * @param key           the key
     * @param listener      the {@link Listener} to subscribe
     * @param priorityValue the priority value of the {@link Listener}
     * @return the {@link SubscribedListener}
     * @throws IllegalStateException if this subscriber is already closed
     */
    @NotNull SubscribedListener<E> subscribe(@NotNull Key key, @NotNull Listener<E> listener, int priorityValue);

    /**
     * Unsubscribes the specified {@link SubscribedListener}.
     *
     * @param subscribedListener the {@link SubscribedListener} to unsubscribe
     * @return if the listener has been unsubscribed, returns {@code true}, otherwise {@code false}
     * @throws IllegalStateException if this subscriber is already closed
     */
    boolean unsubscribe(@NotNull SubscribedListener<E> subscribedListener);

    /**
     * Unsubscribes the {@link Listener}s that subscribed with the given {@link Key}.
     *
     * @param key the key to unsubscribe
     * @return if the listeners has been unsubscribed, returns {@code true}. otherwise {@code false}
     * @throws IllegalStateException if this subscriber is already closed
     */
    boolean unsubscribeAll(@NotNull Key key);

    /**
     * Unsubscribes all listeners that satisfy the given {@link Predicate}.
     *
     * @param predicate a predicate which returns true for listeners to be unsubscribed
     * @return if the listeners has been unsubscribed, returns {@code true}. otherwise {@code false}
     * @throws IllegalStateException if this subscriber is already closed
     */
    boolean unsubscribeIf(@NotNull Predicate<SubscribedListener<?>> predicate);

    /**
     * Gets the list of subscribed listeners.
     *
     * @return the list of subscribed listeners.
     */
    @NotNull @UnmodifiableView List<SubscribedListener<E>> getSubscribedListeners();

    /**
     * Checks if this subscriber is closed.
     *
     * @return if this subscriber is closed, returns {@code true}, otherwise {@code false}
     */
    boolean isClosed();
}
