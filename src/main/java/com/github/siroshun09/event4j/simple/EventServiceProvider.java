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

package com.github.siroshun09.event4j.simple;

import com.github.siroshun09.event4j.caller.AsyncEventCaller;
import com.github.siroshun09.event4j.caller.EventCaller;
import com.github.siroshun09.event4j.listener.ListenerExceptionHandler;
import com.github.siroshun09.event4j.listener.ListenerBase;
import com.github.siroshun09.event4j.listener.SubscribedListener;
import com.github.siroshun09.event4j.subscriber.EventSubscriber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.Executor;

/**
 * An interface to provide simple implementations of interfaces such as {@link EventCaller} and {@link EventSubscriber}.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
public interface EventServiceProvider<K, E, O> {

    /**
     * Creates a new {@link Factory}.
     *
     * @param <K> the key type
     * @param <E> the event type
     * @param <O> the order type
     * @return a new {@link Factory}
     */
    @Contract(" -> new")
    static <K, E, O> EventServiceProvider.@NotNull Factory<K, E, O> factory() {
        return new SimpleEventServiceProvider.FactoryImpl<>(null, null, null, null);
    }

    /**
     * Gets the root class of events.
     *
     * @return the root class of events
     */
    @NotNull Class<E> eventClass();

    /**
     * Gets the {@link EventCaller}.
     * <p>
     * This {@link EventCaller} also dispatches to listeners of the higher class of the given event.
     *
     * @return the {@link EventCaller} implementation
     */
    @NotNull EventCaller<E> caller();

    /**
     * Checks if this provider supports getting {@link AsyncEventCaller} by calling {@link #asyncCaller()}.
     * <p>
     * If the {@link Executor} is set through {@link Factory#executor(Executor)}, this method returns {@code true}; otherwise {@code false}.
     *
     * @return {@code true} if {@link #asyncCaller()} can be used in this provider, otherwise {@code false}
     */
    boolean isAsyncCallerAvailable();

    /**
     * Gets the {@link AsyncEventCaller} that created with {@link #caller()} and the {@link Executor} which is set through {@link Factory#executor(Executor)}.
     * <p>
     * If the {@link Executor} is not set, this method throws {@link IllegalStateException}.
     *
     * @return the {@link AsyncEventCaller}
     * @throws IllegalStateException if the {@link Executor} is not set ({@link #isAsyncCallerAvailable()} returns {@code false})
     */
    @NotNull AsyncEventCaller<E> asyncCaller();

    /**
     * Gets the {@link EventSubscriber} of the given event class.
     *
     * @param eventClass the event class to get {@link EventSubscriber}
     * @param <T>        the event type
     * @return the {@link EventSubscriber} of the given event class
     */
    <T extends E> @NotNull EventSubscriber<K, T, O> subscriber(@NotNull Class<T> eventClass);

    /**
     * Gets all currently available {@link EventSubscriber}s.
     *
     * @return all currently available {@link EventSubscriber}s in this provider.
     */
    @NotNull @Unmodifiable Collection<EventSubscriber<K, ? extends E, O>> subscribers();

    /**
     * Subscribes the given {@link ListenerBase}.
     *
     * @param listeners the {@link ListenerBase} to subscribe
     * @return the {@link SubscribedListener}s
     */
    @NotNull Collection<SubscribedListener<K, ? extends E, O>> subscribeAll(@NotNull Iterable<ListenerBase<K, ? extends E, O>> listeners);

    /**
     * Unsubscribes the given {@link SubscribedListener}s.
     *
     * @param listeners the {@link SubscribedListener}s to unsubscribe
     */
    void unsubscribeAll(@NotNull Collection<? extends SubscribedListener<K, ? extends E, O>> listeners);

    /**
     * Unsubscribes listeners by the given key.
     *
     * @param key the key to unsubscribe listeners
     * @see EventSubscriber#unsubscribeByKey(Object)
     */
    void unsubscribeByKey(@NotNull K key);

    /**
     * A factory interface to create {@link EventServiceProvider}.
     * <p>
     * The implementation of this interface is immutable, so all factory methods creates a new instance of {@link Factory}.
     *
     * @param <K> the key type
     * @param <E> the event type
     * @param <O> the order type
     */
    interface Factory<K, E, O> {

        /**
         * Sets the key class.
         *
         * @param keyClass the key class
         * @param <K1>     the new key type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <K1> @NotNull Factory<K1, E, O> keyClass(Class<? extends K1> keyClass);

        /**
         * Sets the root event class.
         *
         * @param eventClass the root event class
         * @param <E1>       the new event type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <E1> @NotNull Factory<K, E1, O> eventClass(Class<? extends E1> eventClass);

        /**
         * Sets the {@link Comparator} to sort listeners by the specified orders.
         *
         * @param orderComparator the {@link Comparator} to sort listeners by the specified orders
         * @param <O1>            the new order type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <O1> @NotNull Factory<K, E, O1> orderComparator(Comparator<? extends O1> orderComparator);

        /**
         * Sets the {@link Comparator} and the default order.
         * <p>
         * The default order is used when the order is not specified.
         * If the default value is not null, no nulls passed to {@link Comparator}; Otherwise, it is possible to pass nulls to {@link Comparator}.
         *
         * @param orderComparator the {@link Comparator} to sort listeners by the specified orders
         * @param defaultOrder    the default order that is used when the order is not specified
         * @param <O1>            the new order type
         * @return the new {@link Factory}
         */
        @Contract("_, _ -> new")
        <O1> @NotNull Factory<K, E, O1> orderComparator(Comparator<? extends O1> orderComparator, O1 defaultOrder);

        /**
         * Sets the {@link Executor}.
         *
         * @param executor the {@link Executor} to create {@link AsyncEventCaller}
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        @NotNull Factory<K, E, O> executor(Executor executor);

        /**
         * Creates a new {@link EventServiceProvider} without the custom {@link ListenerExceptionHandler}.
         *
         * @return a new {@link EventServiceProvider}
         */
        @NotNull EventServiceProvider<K, E, O> create();

        /**
         * Creates a new {@link EventServiceProvider} with the custom {@link ListenerExceptionHandler}.
         *
         * @param exceptionHandler the {@link ListenerExceptionHandler}
         * @return a new {@link EventServiceProvider}
         */
        @NotNull EventServiceProvider<K, E, O> create(@Nullable ListenerExceptionHandler<K, E, O> exceptionHandler);

    }
}
