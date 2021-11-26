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

import com.github.siroshun09.event4j.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class SimpleEventBus<E> implements EventBus<E> {

    private final Map<Class<?>, SimpleEventSubscriber<?>> subscriberMap = new ConcurrentHashMap<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final Class<E> eventClass;
    private final Executor asyncExecutor;

    private final List<Consumer<PostResult<?>>> resultConsumers = new CopyOnWriteArrayList<>();

    SimpleEventBus(@NotNull Class<E> eventClass, @NotNull Executor asyncExecutor) {
        this.eventClass = Objects.requireNonNull(eventClass);
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor);
    }

    @Override
    public @NotNull Class<E> getEventClass() {
        return eventClass;
    }

    @Override
    public @NotNull <T extends E> EventSubscriber<T> getSubscriber(@NotNull Class<T> eventClass) {
        Objects.requireNonNull(eventClass);
        checkClosed();

        var subscriber = getSubscriberOrNull(eventClass);

        if (subscriber == null) {
            subscriber = new SimpleEventSubscriber<>(eventClass);
            subscriberMap.put(eventClass, subscriber);
        }

        return subscriber;
    }

    @Override
    public @NotNull @Unmodifiable Collection<EventSubscriber<?>> getSubscribers() {
        return Set.copyOf(subscriberMap.values());
    }

    @Override
    public void unsubscribeAll(@NotNull Key key) {
        Objects.requireNonNull(key);
        checkClosed();

        subscriberMap.values().forEach(subscriber -> subscriber.unsubscribeAll(key));
    }

    @Override
    public void unsubscribeIf(@NotNull Predicate<SubscribedListener<?>> predicate) {
        Objects.requireNonNull(predicate);
        checkClosed();

        subscriberMap.values().forEach(subscriber -> subscriber.unsubscribeIf(predicate));
    }

    @Override
    public <T extends E> @NotNull T callEvent(@NotNull T event) {
        Objects.requireNonNull(event);
        checkClosed();

        return postEventToSubscribers(event);
    }

    @Override
    public @NotNull <T extends E> CompletableFuture<T> callEventAsync(@NotNull T event) {
        return callEventAsync(event, asyncExecutor);
    }

    @Override
    public @NotNull <T extends E> CompletableFuture<T> callEventAsync(@NotNull T event, @NotNull Executor executor) {
        Objects.requireNonNull(executor);

        Function<Supplier<T>, CompletableFuture<T>> function = supplier -> CompletableFuture.supplyAsync(supplier, executor);

        return callEventAsync(event, function);
    }

    @Override
    public @NotNull <T extends E> CompletableFuture<T> callEventAsync(@NotNull T event,
                                                                      @NotNull Function<Supplier<T>, CompletableFuture<T>> futureFunction) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(futureFunction);
        checkClosed();

        return futureFunction.apply(() -> callEvent(event));
    }

    @Override
    public void close() {
        checkClosed();

        closed.set(true);

        subscriberMap.values().forEach(SimpleEventSubscriber::close);
        subscriberMap.clear();
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public boolean addResultConsumer(@NotNull Consumer<PostResult<?>> consumer) {
        Objects.requireNonNull(consumer);
        checkClosed();

        return resultConsumers.add(consumer);
    }

    @Override
    public boolean removeResultConsumer(@NotNull Consumer<PostResult<?>> consumer) {
        Objects.requireNonNull(consumer);
        checkClosed();

        return resultConsumers.remove(consumer);
    }

    @SuppressWarnings("unchecked")
    private @Nullable <T extends E> SimpleEventSubscriber<T> getSubscriberOrNull(@NotNull Class<T> eventClass) {
        return (SimpleEventSubscriber<T>) subscriberMap.get(eventClass);
    }

    private void checkClosed() {
        if (closed.get()) {
            throw new IllegalStateException("The eventbus is closed.");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T extends E> @NotNull T postEventToSubscribers(@NotNull T event) {
        var eventSubscriber = getSubscriberOrNull((Class<T>) event.getClass());

        if (eventSubscriber != null) {
            consumeResult(eventSubscriber.post(event));
        } else {
            consumeResult(() -> PostResult.success(event));
        }

        Class<?> clazz = event.getClass().getSuperclass();

        while (clazz != null && eventClass.isAssignableFrom(clazz)) {
            var subscriber = (EventSubscriber) getSubscriberOrNull((Class) clazz);

            if (subscriber != null) {
                consumeResult(subscriber.post(event));
            } else {
                consumeResult(() -> PostResult.success(event));
            }

            clazz = clazz.getSuperclass();
        }

        return event;
    }

    private void consumeResult(@Nullable PostResult<?> result) {
        if (resultConsumers.isEmpty()) {
            return;
        }

        if (1 < resultConsumers.size()) {
            for (var consumer : resultConsumers) {
                consumer.accept(result);
            }
        } else {
            resultConsumers.get(0).accept(result);
        }
    }

    private void consumeResult(@NotNull Supplier<PostResult<?>> resultSupplier) {
        if (resultConsumers.isEmpty()) {
            return;
        }

        var result = resultSupplier.get();

        if (1 < resultConsumers.size()) {
            for (var consumer : resultConsumers) {
                consumer.accept(result);
            }
        } else {
            resultConsumers.get(0).accept(result);
        }
    }
}
