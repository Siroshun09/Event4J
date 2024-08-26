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

package dev.siroshun.event4j.tree;

import dev.siroshun.event4j.api.listener.ListenerFactory;
import dev.siroshun.event4j.api.listener.ListenerSubscriber;
import dev.siroshun.event4j.api.listener.SubscribedListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class ListenerSubscriberImpl<K, E, O> implements ListenerSubscriber<K, E, O> {

    private final ListenerList<K, E, O> listenerList;
    private final O defaultOrder;

    ListenerSubscriberImpl(@NotNull ListenerList<K, E, O> listenerList, O defaultOrder) {
        this.listenerList = listenerList;
        this.defaultOrder = defaultOrder;
    }

    @Override
    public @NotNull Collection<SubscribedListener<K, ? extends E, O>> allListeners() {
        return this.listenerList.holders().stream().flatMap(this::listeners).toList();
    }

    private <T extends E> @NotNull Stream<SubscribedListener<K, ? extends E, O>> listeners(@NotNull ListenerList<K, E, O>.Holder<T> holder) {
        return holder.listeners().stream().map(Function.identity());
    }

    @Override
    public @NotNull <T extends E> List<SubscribedListener<K, T, O>> listenersFor(@NotNull Class<T> eventClass) {
        return this.listenerList.holder(eventClass).listeners();
    }

    @Override
    public @NotNull <T extends E> SubscribedListener<K, T, O> subscribe(@NotNull Class<T> eventClass, @NotNull Consumer<? super ListenerFactory<K, T, O>> builder) {
        Objects.requireNonNull(eventClass, "eventClass cannot be null.");
        Objects.requireNonNull(builder, "builder cannot be null.");

        var factory = new ListenerFactoryImpl<K, T, O>(eventClass, this.defaultOrder);
        builder.accept(factory);
        return this.addListener(factory.build());
    }

    @Override
    public @NotNull <T extends E> SubscribedListener<K, T, O> subscribe(@NotNull Class<T> eventClass, @NotNull K key, @NotNull Consumer<? super T> consumer) {
        Objects.requireNonNull(eventClass, "eventClass cannot be null.");
        Objects.requireNonNull(key, "key cannot be null.");
        Objects.requireNonNull(consumer, "consumer cannot be null.");
        return this.addListener(new SubscribedListenerImpl<>(eventClass, key, consumer, this.defaultOrder));
    }

    @Override
    public @NotNull <T extends E> SubscribedListener<K, T, O> subscribe(@NotNull Class<T> eventClass, @NotNull K key, @NotNull Consumer<? super T> consumer, @Nullable O order) {
        Objects.requireNonNull(eventClass, "eventClass cannot be null.");
        Objects.requireNonNull(key, "key cannot be null.");
        Objects.requireNonNull(consumer, "consumer cannot be null.");
        return this.addListener(new SubscribedListenerImpl<>(eventClass, key, consumer, order != null ? order : this.defaultOrder));
    }

    @Contract("_ -> param1")
    private <T extends E> @NotNull SubscribedListener<K, T, O> addListener(@NotNull SubscribedListener<K, T, O> listener) {
        this.listenerList.holder(listener.eventClass()).modifyListeners(list -> list.add(listener));
        return listener;
    }

    @Override
    public @NotNull BulkSubscriber<K, E, O> bulkSubscriber() {
        return new ListenerBulkSubscriberImpl<>(this.listenerList, this.defaultOrder);
    }

    @Override
    public void unsubscribe(@NotNull SubscribedListener<K, ? extends E, O> subscribedListener) {
        Objects.requireNonNull(subscribedListener, "subscribedListener cannot be null.");

        if (subscribedListener instanceof SubscribedListenerImpl) {
            this.listenerList.holder(subscribedListener.eventClass())
                .modifyListeners(list -> list.remove(subscribedListener));
        }
    }

    @Override
    public void unsubscribeAll(@NotNull Collection<SubscribedListener<K, ? extends E, O>> subscribedListeners) {
        Objects.requireNonNull(subscribedListeners, "subscribedListeners cannot be null.");
        var listenersToUnsubscribe = new ListenersToUnsubscribe(subscribedListeners.size());

        subscribedListeners.stream()
            .filter(listener -> listener instanceof SubscribedListenerImpl)
            .forEach(listenersToUnsubscribe::add);

        listenersToUnsubscribe.unsubscribeAll();
    }

    private class ListenersToUnsubscribe {

        private final Map<Class<? extends E>, TypedListeners<K, ? extends E, O>> listenersToUnsubscribe;

        private ListenersToUnsubscribe(int size) {
            this.listenersToUnsubscribe = new HashMap<>(size, 1.0f);
        }

        private <T extends E> void add(@NotNull SubscribedListener<K, T, O> listener) {
            this.listeners(listener.eventClass()).add(listener);
        }

        @SuppressWarnings("unchecked")
        private <T> @NotNull TypedListeners<K, T, O> listeners(@NotNull Class<T> eventClass) {
            return (TypedListeners<K, T, O>) this.listenersToUnsubscribe.computeIfAbsent((Class<? extends E>) eventClass, TypedListeners::new);
        }

        private void unsubscribeAll() {
            this.listenersToUnsubscribe.values().forEach(this::unsubscribeAll);
        }

        private <T extends E> void unsubscribeAll(@NotNull TypedListeners<K, T, O> listeners) {
            ListenerSubscriberImpl.this.listenerList.holder(listeners.eventClass()).modifyListeners(list -> list.removeAll(listeners.list()));
        }
    }

    @Override
    public void unsubscribeByKey(@NotNull K key) {
        Objects.requireNonNull(key, "key cannot be null.");
        this.unsubscribeIf(listener -> listener.key().equals(key));
    }

    @Override
    public void unsubscribeIf(@NotNull Predicate<SubscribedListener<K, ? extends E, O>> predicate) {
        Objects.requireNonNull(predicate, "predicate cannot be null.");
        this.listenerList.holders().forEach(holder -> holder.modifyListeners(list -> list.removeIf(predicate)));
    }
}
