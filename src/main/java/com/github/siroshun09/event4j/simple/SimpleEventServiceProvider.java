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

package com.github.siroshun09.event4j.simple;

import com.github.siroshun09.event4j.caller.AsyncEventCaller;
import com.github.siroshun09.event4j.caller.EventCaller;
import com.github.siroshun09.event4j.listener.ListenerBase;
import com.github.siroshun09.event4j.listener.ListenerExceptionHandler;
import com.github.siroshun09.event4j.listener.SubscribedListener;
import com.github.siroshun09.event4j.subscriber.EventSubscriber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

class SimpleEventServiceProvider<K, E, O> implements EventServiceProvider<K, E, O> {

    private final EventSubscriberHolder<K, E, O> subscriberHolder;
    private final EventCaller<E> eventCaller;
    private final AsyncEventCaller<E> asyncEventCaller;

    SimpleEventServiceProvider(@NotNull Class<E> eventClass,
                               @NotNull Comparator<O> orderComparator,
                               @UnknownNullability O defaultOrder,
                               @Nullable ListenerExceptionHandler<K, E, O> listenerExceptionHandler,
                               @Nullable Executor executor) {
        this.subscriberHolder = new EventSubscriberHolder<>(eventClass, orderComparator, defaultOrder);
        this.eventCaller = new SimpleEventCaller<>(this.subscriberHolder, listenerExceptionHandler);
        this.asyncEventCaller = executor != null ? AsyncEventCaller.create(this.eventCaller, executor) : null;
    }

    @Override
    public @NotNull Class<E> eventClass() {
        return this.subscriberHolder.getEventClass();
    }

    @Override
    public @NotNull EventCaller<E> caller() {
        return this.eventCaller;
    }

    @Override
    public boolean isAsyncCallerAvailable() {
        return this.asyncEventCaller != null;
    }

    @Override
    public @NotNull AsyncEventCaller<E> asyncCaller() {
        if (this.asyncEventCaller == null) {
            throw new IllegalStateException("AsyncEventCaller is not available.");
        }
        return this.asyncEventCaller;
    }

    @Override
    public @NotNull <T extends E> EventSubscriber<K, T, O> subscriber(@NotNull Class<T> eventClass) {
        return this.subscriberHolder.subscriber(eventClass);
    }

    @Override
    public @NotNull @Unmodifiable Collection<EventSubscriber<K, ? extends E, O>> subscribers() {
        return this.subscriberHolder.subscribers();
    }

    @Override
    public @NotNull Collection<SubscribedListener<K, ? extends E, O>> subscribeAll(@NotNull Iterable<ListenerBase<K, ? extends E, O>> listeners) {
        Map<Class<? extends E>, TypedListenerCollector<K, ? extends E, O>> byClass = new HashMap<>();
        int count = 0;

        for (var listener : listeners) {
            byClass.computeIfAbsent(listener.eventClass(), TypedListenerCollector::new).add(listener);
            count++;
        }

        var subscribedListeners = new ArrayList<SubscribedListener<K, ? extends E, O>>(count);

        byClass.values().forEach(collector -> this.subscribeAll(collector, subscribedListeners));

        return Collections.unmodifiableCollection(subscribedListeners);
    }

    private <T extends E> void subscribeAll(@NotNull TypedListenerCollector<K, T, O> collector, @NotNull List<SubscribedListener<K, ? extends E, O>> subscribedListeners) {
        subscribedListeners.addAll(this.subscriber(collector.clazz).subscribeAll(collector.list));
    }

    @Override
    public void unsubscribeAll(@NotNull Collection<? extends SubscribedListener<K, ? extends E, O>> subscribedListeners) {
        Map<Class<? extends E>, TypedSubscribedListenerCollector<K, ? extends E, O>> byClass = new HashMap<>();

        for (var listener : subscribedListeners) {
            byClass.computeIfAbsent(listener.eventClass(), TypedSubscribedListenerCollector::new).add(listener);
        }

        byClass.values().forEach(this::unsubscribeAll);
    }

    private <T extends E> void unsubscribeAll(@NotNull TypedSubscribedListenerCollector<K, T, O> collector) {
        this.subscriber(collector.clazz).unsubscribeAll(collector.list);
    }

    @Override
    public void unsubscribeByKey(@NotNull K key) {
        this.subscriberHolder.subscriberNodes().forEach(node -> node.value().unsubscribeByKey(key));
    }

    static final class FactoryImpl<K, E, O> implements Factory<K, E, O> {

        private final Class<E> eventClass;
        private final Comparator<O> orderComparator;
        private final O defaultOrder;
        private final Executor executor;

        FactoryImpl(Class<E> eventClass, Comparator<O> orderComparator, O defaultOrder, Executor executor) {
            this.eventClass = eventClass;
            this.orderComparator = orderComparator;
            this.defaultOrder = defaultOrder;
            this.executor = executor;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull <K1> Factory<K1, E, O> keyClass(Class<? extends K1> keyClass) {
            return (Factory<K1, E, O>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull <E1> Factory<K, E1, O> eventClass(Class<? extends E1> eventClass) {
            return new SimpleEventServiceProvider.FactoryImpl<>((Class<E1>) eventClass, this.orderComparator, this.defaultOrder, this.executor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull <O1> Factory<K, E, O1> orderComparator(Comparator<? extends O1> orderComparator) {
            return new SimpleEventServiceProvider.FactoryImpl<>(this.eventClass, (Comparator<O1>) orderComparator, null, this.executor);
        }

        @SuppressWarnings("unchecked")
        @Override
        public @NotNull <O1> Factory<K, E, O1> orderComparator(Comparator<? extends O1> orderComparator, O1 defaultOrder) {
            return new SimpleEventServiceProvider.FactoryImpl<>(this.eventClass, (Comparator<O1>) orderComparator, defaultOrder, this.executor);
        }

        @Override
        @Contract("_ -> this")
        public @NotNull SimpleEventServiceProvider.FactoryImpl<K, E, O> executor(Executor executor) {
            return new SimpleEventServiceProvider.FactoryImpl<>(this.eventClass, this.orderComparator, this.defaultOrder, this.executor);
        }

        @Override
        public @NotNull EventServiceProvider<K, E, O> create() {
            return this.create(null);
        }

        @Override
        public @NotNull EventServiceProvider<K, E, O> create(@Nullable ListenerExceptionHandler<K, E, O> exceptionHandler) {
            return new SimpleEventServiceProvider<>(this.eventClass, this.orderComparator, this.defaultOrder, exceptionHandler, this.executor);
        }
    }

    private static class TypedCollector<T, C> {
        final Class<T> clazz;
        final List<C> list = new ArrayList<>();

        private TypedCollector(Class<T> clazz) {
            this.clazz = clazz;
        }

        @SuppressWarnings("unchecked")
        void add(Object element) {
            this.list.add((C) element);
        }
    }

    private static class TypedListenerCollector<K, E, O> extends TypedCollector<E, ListenerBase<K, E, O>> {
        private TypedListenerCollector(Class<E> clazz) {
            super(clazz);
        }
    }

    private static class TypedSubscribedListenerCollector<K, E, O> extends TypedCollector<E, SubscribedListener<K, E, O>> {
        private TypedSubscribedListenerCollector(Class<E> clazz) {
            super(clazz);
        }
    }
}
