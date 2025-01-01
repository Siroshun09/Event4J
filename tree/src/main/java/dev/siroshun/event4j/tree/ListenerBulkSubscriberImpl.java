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

import dev.siroshun.event4j.api.listener.ListenerFactory;
import dev.siroshun.event4j.api.listener.ListenerSubscriber;
import dev.siroshun.event4j.api.listener.SubscribedListener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

class ListenerBulkSubscriberImpl<K, E, O> implements ListenerSubscriber.BulkSubscriber<K, E, O> {

    private final ListenerList<K, E, O> listenerList;
    private final O defaultOrder;

    private final Map<Class<? extends E>, TypedListeners<K, ? extends E, O>> listenersByClass = new HashMap<>();

    ListenerBulkSubscriberImpl(@NonNull ListenerList<K, E, O> listenerList, @UnknownNullability O defaultOrder) {
        this.listenerList = listenerList;
        this.defaultOrder = defaultOrder;
    }

    @Override
    public <T extends E> ListenerSubscriber.@NonNull BulkSubscriber<K, E, O> add(@NonNull Class<T> eventClass, @NonNull Consumer<? super ListenerFactory<K, T, O>> builder) {
        Objects.requireNonNull(eventClass, "eventClass cannot be null.");
        Objects.requireNonNull(builder, "builder cannot be null.");

        var factory = new ListenerFactoryImpl<K, T, O>(eventClass, this.defaultOrder);
        builder.accept(factory);

        var listener = factory.build();
        this.listeners(eventClass).add(listener);

        return this;
    }

    @Override
    public <T extends E> ListenerSubscriber.@NonNull BulkSubscriber<K, E, O> add(@NonNull Class<T> eventClass, @NonNull K key, @NonNull Consumer<? super T> consumer) {
        Objects.requireNonNull(eventClass, "eventClass cannot be null.");
        Objects.requireNonNull(key, "key cannot be null.");
        Objects.requireNonNull(consumer, "consumer cannot be null.");

        this.addListener(new SubscribedListenerImpl<>(eventClass, key, consumer, this.defaultOrder));
        return this;
    }

    @Override
    public <T extends E> ListenerSubscriber.@NonNull BulkSubscriber<K, E, O> add(@NonNull Class<T> eventClass, @NonNull K key, @NonNull Consumer<? super T> consumer, @Nullable O order) {
        Objects.requireNonNull(eventClass, "eventClass cannot be null.");
        Objects.requireNonNull(key, "key cannot be null.");
        Objects.requireNonNull(consumer, "consumer cannot be null.");

        this.addListener(new SubscribedListenerImpl<>(eventClass, key, consumer, order != null ? order : this.defaultOrder));
        return this;
    }

    private <T extends E> void addListener(@NonNull SubscribedListener<K, T, O> listener) {
        this.listeners(listener.eventClass()).add(listener);
    }

    @SuppressWarnings("unchecked")
    private <T extends E> @NonNull TypedListeners<K, T, O> listeners(@NonNull Class<T> eventClass) {
        return (TypedListeners<K, T, O>) this.listenersByClass.computeIfAbsent(eventClass, TypedListeners::new);
    }

    @Override
    public @NonNull List<SubscribedListener<K, ? extends E, O>> subscribe() {
        return this.listenersByClass.values().stream().flatMap(this::subscribeAll).toList();
    }

    private <T extends E> @NonNull Stream<SubscribedListener<K, ? extends E, O>> subscribeAll(@NonNull TypedListeners<K, T, O> listeners) {
        this.listenerList.holder(listeners.eventClass()).modifyListeners(list -> list.addAll(listeners.list()));
        return listeners.list().stream().map(Function.identity());
    }
}
