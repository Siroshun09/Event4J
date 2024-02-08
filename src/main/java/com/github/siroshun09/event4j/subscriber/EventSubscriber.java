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

package com.github.siroshun09.event4j.subscriber;

import com.github.siroshun09.event4j.listener.ListenerFactory;
import com.github.siroshun09.event4j.listener.ListenerBase;
import com.github.siroshun09.event4j.listener.SubscribedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An interface to manage listeners.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
public interface EventSubscriber<K, E, O> {

    /**
     * Gets the event class of this {@link EventSubscriber}.
     *
     * @return the event class of this {@link EventSubscriber}
     */
    @NotNull Class<E> eventClass();

    /**
     * Creates a new {@link ListenerFactory}.
     *
     * @return a new {@link ListenerFactory}
     */
    @NotNull ListenerFactory<K, E, O> listenerFactory();

    /**
     * Subscribes a new listener.
     *
     * @param key      the key
     * @param consumer the {@link Consumer}
     * @return a {@link SubscribedListener}
     * @see #subscribe(Object, Consumer, Object)
     */
    default @NotNull SubscribedListener<K, E, O> subscribe(@NotNull K key, @NotNull Consumer<? super E> consumer) {
        return this.subscribe(key, consumer, null);
    }

    /**
     * Subscribes a new listener from {@link ListenerBase}.
     *
     * @param listener the {@link ListenerBase}
     * @return a {@link SubscribedListener}
     * @see #subscribe(Object, Consumer, Object)
     */
    default @NotNull SubscribedListener<K, E, O> subscribe(@NotNull ListenerBase<K, E, O> listener) {
        return this.subscribe(listener.key(), listener.consumer(), listener.order());
    }

    /**
     * Subscribes a new listener.
     * <p>
     * The key is only used for {@link #unsubscribeByKey(Object)}. Same keys can be passed to this method for subscribing multiple listeners.
     *
     * @param key      the key
     * @param consumer the {@link Consumer}
     * @param order    the order
     * @return a {@link SubscribedListener}
     */
    @NotNull SubscribedListener<K, E, O> subscribe(@NotNull K key, @NotNull Consumer<? super E> consumer, @Nullable O order);

    /**
     * Subscribes multiple listeners.
     * <p>
     * If you want to subscribe multiple listeners,
     * this method is better for performance than calling {@link #subscribe(ListenerBase)} one by one.
     *
     * @param listeners the listeners to subscribe
     * @return {@link SubscribedListener}s
     */
    @NotNull @Unmodifiable Collection<SubscribedListener<K, E, O>> subscribeAll(@NotNull Iterable<ListenerBase<K, E, O>> listeners);

    /**
     * Unsubscribes the specified {@link SubscribedListener}.
     *
     * @param subscribedListener the {@link SubscribedListener} to unsubscribe
     * @return if the listener has been unsubscribed, returns {@code true}, otherwise {@code false}
     * @throws IllegalStateException if this subscriber is already closed
     */
    boolean unsubscribe(@NotNull SubscribedListener<K, E, O> subscribedListener);

    /**
     * Unsubscribes the specified {@link SubscribedListener}s.
     *
     * @param subscribedListeners the {@link SubscribedListener}s to unsubscribe
     * @return {@code true} if at least one listener is unsubscribed, otherwise {@code false}
     */
    boolean unsubscribeAll(@NotNull Collection<? extends SubscribedListener<K, E, O>> subscribedListeners);

    /**
     * Unsubscribes listeners by the specified key.
     *
     * @param key the key
     * @return {@code true} if at least one listener is unsubscribed, otherwise {@code false}
     */
    boolean unsubscribeByKey(@NotNull K key);

    /**
     * Unsubscribes all listeners that satisfy the given {@link Predicate}.
     *
     * @param predicate a predicate which returns true for listeners to be unsubscribed
     * @return {@code true} if at least one listener is unsubscribed, otherwise {@code false}
     */
    boolean unsubscribeIf(@NotNull Predicate<? super SubscribedListener<K, E, O>> predicate);

    /**
     * Gets the list of subscribed listeners.
     *
     * @return the list of subscribed listeners.
     */
    @NotNull @Unmodifiable List<? extends SubscribedListener<K, E, O>> getSubscribedListeners();

}
