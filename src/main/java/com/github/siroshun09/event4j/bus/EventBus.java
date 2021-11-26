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

package com.github.siroshun09.event4j.bus;

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.key.Key;
import com.github.siroshun09.event4j.listener.Listener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An event bus to dispatch events.
 *
 * @param <E> the top-level event type
 */
public interface EventBus<E> {

    /**
     * Creates a new {@link EventBus}.
     * <p>
     * The top-level event type is {@link Event}.
     *
     * @return a new {@link EventBus}
     */
    @Contract(value = "-> new", pure = true)
    static @NotNull EventBus<Event> create() {
        return create(Event.class, ForkJoinPool.commonPool());
    }

    /**
     * Creates a new {@link EventBus} with the specified {@link Executor}.
     * <p>
     * The top-level event type is {@link Event}.
     *
     * @param asyncExecutor the {@link Executor} to call events asynchronously
     * @return a new {@link EventBus}
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull EventBus<Event> create(@NotNull Executor asyncExecutor) {
        return create(Event.class, asyncExecutor);
    }

    /**
     * Creates a new {@link EventBus} with the specified event type and the specified {@link Executor}.
     *
     * @param eventClass the top-level event class
     * @param <E>        the top-level event type
     * @return a new {@link EventBus}
     */
    @Contract(value = "_ -> new", pure = true)
    static <E> @NotNull EventBus<E> create(@NotNull Class<E> eventClass) {
        return create(eventClass, ForkJoinPool.commonPool());
    }

    /**
     * Creates a new {@link EventBus} with the specified event type and the specified {@link Executor}.
     *
     * @param eventClass    the top-level event class
     * @param asyncExecutor the {@link Executor} to call events asynchronously
     * @param <E>           the top-level event type
     * @return a new {@link EventBus}
     */
    @Contract(value = "_,_ -> new", pure = true)
    static <E> @NotNull EventBus<E> create(@NotNull Class<E> eventClass, @NotNull Executor asyncExecutor) {
        return new SimpleEventBus<>(eventClass, asyncExecutor);
    }

    /**
     * Gets the top-level event type of this event bus.
     *
     * @return the top-level event type of this event bus
     */
    @NotNull Class<E> getEventClass();

    /**
     * Gets the {@link EventSubscriber} of the specified event.
     *
     * @param eventClass the event class to get the {@link EventSubscriber}
     * @param <T>        the event type
     * @return the {@link EventSubscriber} of the specified event
     * @throws IllegalStateException if this event bus is already closed
     */
    @NotNull <T extends E> EventSubscriber<T> getSubscriber(@NotNull Class<T> eventClass);

    /**
     * Gets the collection of registered {@link EventSubscriber}s.
     * <p>
     * Included in this collection are subscribers to events
     * in which {@link #getSubscriber(Class)} has been called at least once.
     *
     * @return the collection of registered {@link EventSubscriber}s
     */
    @NotNull @Unmodifiable Collection<EventSubscriber<?>> getSubscribers();

    /**
     * Unsubscribes all listeners subscribed with the specified {@link Key} for this event bus instance.
     *
     * @param key the key that subscribed listeners
     * @throws IllegalStateException if this event bus is already closed
     */
    void unsubscribeAll(@NotNull Key key);

    /**
     * Unsubscribes all listeners that satisfy the given {@link Predicate}.
     *
     * @param predicate a predicate which returns true for listeners to be unsubscribed
     * @throws IllegalStateException if this event bus is already closed
     */
    void unsubscribeIf(@NotNull Predicate<SubscribedListener<?>> predicate);

    /**
     * Dispatches the event to subscribed listeners.
     * <p>
     * If an exception is thrown while calling a listener,
     * {@link Listener#handleException(Object, Throwable)} is called and then continues to call the other listeners.
     * That is, if a listener throws an exception, the method is processed to complete successfully.
     * <p>
     * This method also dispatches to listeners of the higher class of the given event.
     *
     * @param event the event instance
     * @param <T>   the event type
     * @return the given event instance
     * @throws IllegalStateException if this event bus is already closed
     */
    @NotNull <T extends E> T callEvent(@NotNull T event);

    /**
     * Calls {@link #callEvent(Object)} asynchronously.
     *
     * @param event the event instance
     * @param <T>   the event type
     * @return the {@link CompletableFuture} to get the given event instance
     * @throws IllegalStateException if this event bus is already closed
     */
    @NotNull <T extends E> CompletableFuture<T> callEventAsync(@NotNull T event);

    /**
     * Calls {@link #callEvent(Object)} asynchronously.
     *
     * @param event    the event instance
     * @param executor the executor to call {@link #callEvent(Object)}
     * @param <T>      the event type
     * @return the {@link CompletableFuture} to get the given event instance
     * @throws IllegalStateException if this event bus is already closed
     */
    @NotNull <T extends E> CompletableFuture<T> callEventAsync(@NotNull T event, @NotNull Executor executor);

    /**
     * Calls {@link #callEvent(Object)} asynchronously.
     * <p>
     * The {@link Supplier} of the input of the {@link Function} is {@code () -> callEvent(event)},
     * and the event will be called when {@link Supplier#get} is executed.
     * <p>
     * In {@link #callEventAsync(Object, Executor)},
     * the {@link Supplier} is used as {@code supplier -> CompletableFuture.supplyAsync(supplier, executor)}
     * to create the required {@link Function}.
     *
     * @param event          the event instance
     * @param futureFunction the function to create {@link CompletableFuture}
     * @param <T>            the event type
     * @return the {@link CompletableFuture} to get the given event instance
     * @throws IllegalStateException if this event bus is already closed
     */
    @NotNull <T extends E> CompletableFuture<T> callEventAsync(@NotNull T event,
                                                               @NotNull Function<Supplier<T>, CompletableFuture<T>> futureFunction);

    /**
     * Closes this event bus.
     *
     * @throws IllegalStateException if this event bus is already closed
     */
    void close();

    /**
     * Checks if this event bus is closed.
     *
     * @return if this event bus is closed, returns {@code true}, otherwise {@code false}
     */
    boolean isClosed();

    /**
     * Adds the {@link Consumer} to consume {@link PostResult}.
     *
     * @param consumer the {@link Consumer} to add
     * @throws IllegalStateException if this event bus is already closed
     * @return if the consumer has been added to this event bus, returns {@code true}, otherwise {@code false}
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean addResultConsumer(@NotNull Consumer<PostResult<?>> consumer);

    /**
     * Removes the {@link Consumer} to consume {@link PostResult}.
     *
     * @param consumer the {@link Consumer} to remove
     * @throws IllegalStateException if this event bus is already closed
     * @return if the consumer has been removed from this event bus, returns {@code true}, otherwise {@code false}
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean removeResultConsumer(@NotNull Consumer<PostResult<?>> consumer);
}
