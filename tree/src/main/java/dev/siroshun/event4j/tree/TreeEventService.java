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

package dev.siroshun.event4j.tree;

import dev.siroshun.event4j.api.caller.EventCaller;
import dev.siroshun.event4j.api.listener.ListenerExceptionHandler;
import dev.siroshun.event4j.api.listener.ListenerSubscriber;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

/**
 * An interface for retrieving an instance of {@link EventCaller} and {@link ListenerSubscriber}.
 * <p>
 * When an event is fired, the event's class and parent class listeners (up to the root event class) are invoked.
 * For example, for the root event class A, if there is a B that inherits from A and a C that inherits from it, the listeners are called in the order C -&gt; B -&gt; A.
 * <p>
 * The implementation of this interface can be created from {@link TreeEventService.Factory}, that is instanced by {@link TreeEventService#factory()} method.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
@NullMarked
public interface TreeEventService<K, E, O> {

    /**
     * Creates a new {@link Factory}.
     *
     * @param <K> the key type
     * @param <E> the event type
     * @param <O> the order type
     * @return a new {@link Factory}
     */
    @Contract(value = " -> new", pure = true)
    static <K, E, O> TreeEventService.Factory<K, E, O> factory() {
        return new TreeEventServiceImpl.FactoryImpl<>(null, null, null);
    }

    /**
     * Gets the {@link EventCaller} of this {@link TreeEventService}.
     *
     * @return the {@link EventCaller}
     */
    EventCaller<E> caller();

    /**
     * Gets the {@link ListenerSubscriber} of this {@link TreeEventService}.
     *
     * @return the {@link ListenerSubscriber}
     */
    ListenerSubscriber<K, E, O> subscriber();

    /**
     * A factory interface to create {@link TreeEventService}.
     * <p>
     * The implementation of this interface should be immutable.
     *
     * @param <K> the key type
     * @param <E> the event type
     * @param <O> the order type
     */
    @NullUnmarked
    interface Factory<K, E, O> {

        /**
         * Sets the key class.
         *
         * @param keyClass the key class
         * @param <K1>     the new key type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <K1> @NonNull Factory<K1, E, O> keyClass(Class<? extends K1> keyClass);

        /**
         * Sets the root event class.
         *
         * @param eventClass the root event class
         * @param <E1>       the new event type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <E1> @NonNull Factory<K, E1, O> eventClass(Class<? extends E1> eventClass);

        /**
         * Sets the {@link Comparator} to sort listeners by the specified orders.
         *
         * @param orderComparator the {@link Comparator} to sort listeners by the specified orders
         * @param <O1>            the new order type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <O1> @NonNull Factory<K, E, O1> orderComparator(Comparator<? super O1> orderComparator);

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
        <O1> @NonNull Factory<K, E, O1> orderComparator(Comparator<? super O1> orderComparator, @Nullable O1 defaultOrder);

        /**
         * Sets the {@link Comparator} and the default order.
         * <p>
         * The default order is used when the order is not specified.
         * If the default value is not null, no nulls passed to {@link Comparator}; Otherwise, it is possible to pass nulls to {@link Comparator}.
         *
         * @param defaultOrder the default order that is used when the order is not specified
         * @param <O1>         the new order type
         * @return the new {@link Factory}
         */
        @Contract("_ -> new")
        <O1 extends Comparable<O1>> @NonNull Factory<K, E, O1> defaultOrder(@NonNull O1 defaultOrder);

        /**
         * Creates a new {@link TreeEventService} with {@link ListenerExceptionHandler#continueHandler()}.
         *
         * @return a new {@link TreeEventService}
         */
        @Contract("-> new")
        @NonNull TreeEventService<K, E, O> create();

        /**
         * Creates a new {@link TreeEventService} with the custom {@link ListenerExceptionHandler}.
         *
         * @param exceptionHandler the {@link ListenerExceptionHandler}
         * @return a new {@link TreeEventService}
         */
        @Contract("_ -> new")
        @NonNull TreeEventService<K, E, O> create(@NonNull ListenerExceptionHandler<K, E, O> exceptionHandler);
    }

}
