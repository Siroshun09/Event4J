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

import com.github.siroshun09.event4j.listener.ListenerBase;
import com.github.siroshun09.event4j.listener.ListenerFactory;
import com.github.siroshun09.event4j.listener.SubscribedListener;
import com.github.siroshun09.event4j.subscriber.EventSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

class SimpleEventSubscriber<K, E, O> implements EventSubscriber<K, E, O> {

    private final Class<E> eventClass;
    private final Comparator<SubscribedListener<K, E, O>> sorter;
    private final @UnknownNullability O defaultOrder;

    private final StampedLock lock = new StampedLock();
    private final List<SubscribedListener<K, E, O>> listeners = new ArrayList<>();

    volatile SubscribedListener<K, E, O>[] sortedListenersArray;

    SimpleEventSubscriber(@NotNull Class<E> eventClass, @NotNull Comparator<O> orderComparator, @UnknownNullability O defaultOrder) {
        this.eventClass = eventClass;
        this.sorter = Comparator.comparing(SubscribedListener::order, orderComparator);
        this.defaultOrder = defaultOrder;
    }

    @Override
    public @NotNull Class<E> eventClass() {
        return this.eventClass;
    }

    @Override
    public @NotNull ListenerFactory<K, E, O> listenerFactory() {
        return new ListenerFactoryImpl();
    }

    @Override
    public @NotNull SubscribedListener<K, E, O> subscribe(@NotNull K key, @NotNull Consumer<? super E> consumer, @Nullable O order) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(consumer);

        var subscribedListener = new SubscribedListenerImpl<>(key, this.eventClass, consumer, order != null ? order : this.defaultOrder);

        long writeLock = this.lock.writeLock();

        try {
            this.listeners.add(subscribedListener);
            this.sortListeners();
        } finally {
            this.lock.unlockWrite(writeLock);
        }

        return subscribedListener;
    }

    @Override
    public @NotNull @Unmodifiable Collection<SubscribedListener<K, E, O>> subscribeAll(@NotNull Iterable<ListenerBase<K, E, O>> listeners) {
        List<SubscribedListener<K, E, O>> subscribedListeners = new ArrayList<>();

        for (var listener : listeners) {
            if (listener.eventClass() != this.eventClass) {
                throw new IllegalArgumentException("The event class of the listener does not match this event subscriber. (Expected " + this.eventClass + ", but got " + listener.eventClass());
            }

            var key = Objects.requireNonNull(listener.key());
            var consumer = Objects.requireNonNull(listener.consumer());
            var order = listener.order();

            subscribedListeners.add(new SubscribedListenerImpl<>(key, this.eventClass, consumer, order != null ? order : this.defaultOrder));
        }

        long writeLock = this.lock.writeLock();

        try {
            this.listeners.addAll(subscribedListeners);
            this.sortListeners();
        } finally {
            this.lock.unlockWrite(writeLock);
        }

        return Collections.unmodifiableCollection(subscribedListeners);
    }

    @Override
    public boolean unsubscribeByKey(@NotNull K key) {
        Objects.requireNonNull(key);
        return this.unsubscribeIf(subscribed -> subscribed.key().equals(key));
    }

    @Override
    public boolean unsubscribe(@NotNull SubscribedListener<K, E, O> subscribedListener) {
        Objects.requireNonNull(subscribedListener);

        long writeLock = this.lock.writeLock();
        boolean result;

        try {
            result = this.listeners.remove(subscribedListener);
            this.sortListeners();
        } finally {
            this.lock.unlockWrite(writeLock);
        }

        return result;
    }

    @Override
    public boolean unsubscribeAll(@NotNull Collection<? extends SubscribedListener<K, E, O>> subscribedListeners) {
        boolean result;
        long writeLock = this.lock.writeLock();

        try {
            result = this.listeners.removeAll(subscribedListeners);
            this.sortListeners();
        } finally {
            this.lock.unlockWrite(writeLock);
        }

        return result;
    }

    @Override
    public boolean unsubscribeIf(@NotNull Predicate<? super SubscribedListener<K, E, O>> predicate) {
        Objects.requireNonNull(predicate);

        long writeLock = this.lock.writeLock();
        boolean result;

        try {
            result = this.listeners.removeIf(predicate);
            this.sortListeners();
        } finally {
            this.lock.unlockWrite(writeLock);
        }

        return result;
    }

    @Override
    public @NotNull @Unmodifiable List<? extends SubscribedListener<K, E, O>> getSubscribedListeners() {
        long readLock = this.lock.readLock();
        List<SubscribedListener<K, E, O>> copiedListeners;

        try {
            copiedListeners = new ArrayList<>(this.listeners);
        } finally {
            this.lock.unlockRead(readLock);
        }

        return Collections.unmodifiableList(copiedListeners);
    }

    @SuppressWarnings("unchecked")
    private void sortListeners() {
        if (this.listeners.isEmpty()) {
            this.sortedListenersArray = null;
            return;
        }

        this.listeners.sort(this.sorter);
        this.sortedListenersArray = this.listeners.toArray(SubscribedListener[]::new);
    }

    private class ListenerFactoryImpl implements ListenerFactory<K, E, O> {

        private K key;
        private Consumer<? super E> consumer;
        private O order;

        @Override
        public @NotNull ListenerFactory<K, E, O> key(K key) {
            this.key = key;
            return this;
        }

        @Override
        public @NotNull ListenerFactory<K, E, O> consumer(Consumer<? super E> consumer) {
            this.consumer = consumer;
            return this;
        }

        @Override
        public @NotNull ListenerFactory<K, E, O> order(O order) {
            this.order = order;
            return this;
        }

        @Override
        public @NotNull SubscribedListener<K, E, O> subscribe() {
            return SimpleEventSubscriber.this.subscribe(this.key, this.consumer, this.order);
        }
    }

    private record SubscribedListenerImpl<K, E, O>(@NotNull K key,
                                                   @NotNull Class<E> eventClass,
                                                   @NotNull Consumer<? super E> consumer,
                                                   @UnknownNullability O order) implements SubscribedListener<K, E, O> {
    }
}
