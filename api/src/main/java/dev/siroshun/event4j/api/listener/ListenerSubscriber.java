/*
 *     Copyright (c) 2020-2024 Siroshun09
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An interface to subscribe/unsubscribe listeners.
 * <p>
 * This interface can also be used to get existing listeners.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
public interface ListenerSubscriber<K, E, O> {

    /**
     * Gets all existing {@link SubscribedListener}s.
     *
     * @return all existing {@link SubscribedListener}s
     */
    @NotNull Collection<SubscribedListener<K, ? extends E, O>> allListeners();

    /**
     * Gets the existing {@link SubscribedListener}s for the specified event.
     *
     * @param eventClass the class of the event
     * @param <T> the event type
     * @return the existing {@link SubscribedListener}s for the specified event
     */
    <T extends E> @NotNull List<SubscribedListener<K, T, O>> listenersFor(@NotNull Class<T> eventClass);

    /**
     * Subscribes a new listener for the specified event.
     *
     * @param eventClass the class of the event
     * @param builder    the {@link Consumer} to modify {@link ListenerFactory}
     * @param <T>        the event type
     * @return a {@link SubscribedListener}
     */
    <T extends E> @NotNull SubscribedListener<K, T, O> subscribe(@NotNull Class<T> eventClass, @NotNull Consumer<? super ListenerFactory<K, T, O>> builder);

    /**
     * Creates a new {@link BulkSubscriber} for subscribing multiple listeners at once.
     *
     * @return a new {@link BulkSubscriber}
     */
    @Contract("-> new")
    @NotNull BulkSubscriber<K, E, O> bulkSubscriber();

    /**
     * Unsubscribes the specified {@link SubscribedListener}.
     *
     * @param subscribedListener the {@link SubscribedListener} to unsubscribe
     */
    void unsubscribe(@NotNull SubscribedListener<K, ? extends E, O> subscribedListener);

    /**
     * Unsubscribes the specified {@link SubscribedListener}s.
     *
     * @param subscribedListeners the {@link SubscribedListener}s to unsubscribe
     */
    void unsubscribeAll(@NotNull Collection<SubscribedListener<K, ? extends E, O>> subscribedListeners);

    /**
     * Unsubscribes listeners by the specified key.
     *
     * @param key the key
     */
    void unsubscribeByKey(@NotNull K key);

    /**
     * Unsubscribes all listeners that satisfy the given {@link Predicate}.
     *
     * @param predicate a predicate which returns true for listeners to be unsubscribed
     */
    void unsubscribeIf(@NotNull Predicate<SubscribedListener<K, ? extends E, O>> predicate);

    /**
     * An interface for subscribing multiple listeners at once.
     *
     * @param <K> the key type
     * @param <E> the event type
     * @param <O> the order type
     */
    interface BulkSubscriber<K, E, O> {

        /**
         * Adds a new listener for the specified event.
         *
         * @param eventClass the class of the event
         * @param builder    the {@link Consumer} to modify {@link ListenerFactory}
         * @param <T>        the event type
         * @return this {@link BulkSubscriber}
         */
        @Contract("_, _ -> this")
        <T extends E> @NotNull BulkSubscriber<K, E, O> add(@NotNull Class<T> eventClass, @NotNull Consumer<? super ListenerFactory<K, T, O>> builder);

        /**
         * Subscribes added listeners.
         *
         * @return {@link SubscribedListener}s
         */
        @NotNull List<SubscribedListener<K, ? extends E, O>> subscribe();

    }
}
