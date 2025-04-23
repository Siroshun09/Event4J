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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.function.Consumer;

class ListenerFactoryImpl<K, E, O> implements ListenerFactory<K, E, O> {

    private final Class<E> eventClass;

    private K key;
    private Consumer<? super E> consumer;
    private O order;

    ListenerFactoryImpl(@NotNull Class<E> eventClass, @UnknownNullability O defaultOrder) {
        this.eventClass = eventClass;
        this.order = defaultOrder;
    }

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

    @NotNull
    SubscribedListenerImpl<K, E, O> build() {
        Objects.requireNonNull(this.key, "key is not set.");
        Objects.requireNonNull(this.consumer, "consumer is not set.");
        return new SubscribedListenerImpl<>(this.eventClass, this.key, this.consumer, this.order);
    }
}
