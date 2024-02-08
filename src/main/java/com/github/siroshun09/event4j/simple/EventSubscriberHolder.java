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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EventSubscriberHolder<K, E, O> {

    private final Class<E> eventClass;
    private final Comparator<O> orderComparator;
    private final O defaultOrder;

    private final Map<Class<? extends E>, SinglyLinkedNode<SimpleEventSubscriber<K, E, O>>> subscriberMap = new ConcurrentHashMap<>();

    EventSubscriberHolder(@NotNull Class<E> eventClass, @NotNull Comparator<O> orderComparator, @UnknownNullability O defaultOrder) {
        this.eventClass = eventClass;
        this.orderComparator = orderComparator;
        this.defaultOrder = defaultOrder;
    }

    @NotNull Class<E> getEventClass() {
        return this.eventClass;
    }

    @SuppressWarnings("unchecked")
    <T extends E> @NotNull SimpleEventSubscriber<K, T, O> subscriber(@NotNull Class<T> eventClass) {
        return (SimpleEventSubscriber<K, T, O>) this.subscriberNode(eventClass).value();
    }

    @NotNull SinglyLinkedNode<SimpleEventSubscriber<K, E, O>> subscriberNode(@NotNull Class<? extends E> eventClass) {
        var existing = this.subscriberMap.get(eventClass);
        return existing != null ? existing : this.createAndPutSubscriberNode(eventClass);
    }

    private @NotNull SinglyLinkedNode<SimpleEventSubscriber<K, E, O>> createAndPutSubscriberNode(@NotNull Class<? extends E> eventClass) {
        var created = this.createSubscriberNode(eventClass);
        var existing = this.subscriberMap.putIfAbsent(eventClass, created);
        return existing != null ? existing : created;
    }

    private @NotNull SinglyLinkedNode<SimpleEventSubscriber<K, E, O>> createSubscriberNode(@NotNull Class<? extends E> eventClass) {
        SimpleEventSubscriber<K, E, O> subscriber = new SimpleEventSubscriber<>(eventClass.asSubclass(this.eventClass), this.orderComparator, this.defaultOrder);

        Class<?> superClass = eventClass.getSuperclass();
        SinglyLinkedNode<SimpleEventSubscriber<K, E, O>> parent;

        if (superClass != null && this.eventClass.isAssignableFrom(superClass)) {
            parent = this.subscriberNode(superClass.asSubclass(this.eventClass));
        } else {
            parent = null;
        }

        return new SinglyLinkedNode<>(subscriber, parent);
    }

    @NotNull @Unmodifiable Collection<EventSubscriber<K, ? extends E, O>> subscribers() {
        Collection<SinglyLinkedNode<SimpleEventSubscriber<K, E, O>>> nodes = this.subscriberNodes();
        List<EventSubscriber<K, ? extends E, O>> subscribers = new ArrayList<>(nodes.size());

        for (var node : nodes) {
            subscribers.add(node.value());
        }

        return Collections.unmodifiableList(subscribers);
    }

    @NotNull @Unmodifiable Collection<SinglyLinkedNode<SimpleEventSubscriber<K, E, O>>> subscriberNodes() {
        return this.subscriberMap.values();
    }

    record SinglyLinkedNode<T>(@UnknownNullability T value,
                               @Nullable EventSubscriberHolder.SinglyLinkedNode<T> next) {
    }
}
