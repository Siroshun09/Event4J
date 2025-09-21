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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Comparator;
import java.util.Objects;

class TreeEventServiceImpl<K, E, O> implements TreeEventService<K, E, O> {

    private final ListenerSubscriber<K, E, O> subscriber;
    private final EventCaller<E> eventCaller;

    TreeEventServiceImpl(@NotNull Class<E> eventClass, @NotNull Comparator<O> sorter, @UnknownNullability O defaultOrder,
                         @NotNull ListenerExceptionHandler<K, E, O> exceptionHandler) {
        var listenerList = new ListenerList<K, E, O>(eventClass, sorter);
        this.subscriber = new ListenerSubscriberImpl<>(listenerList, defaultOrder);
        this.eventCaller = new EventCallerImpl<>(listenerList, exceptionHandler);
    }

    @Override
    public @NotNull EventCaller<E> caller() {
        return this.eventCaller;
    }

    @Override
    public @NotNull ListenerSubscriber<K, E, O> subscriber() {
        return this.subscriber;
    }

    record FactoryImpl<K, E, O>(Class<E> eventClass,
                                Comparator<O> orderComparator,
                                O defaultOrder) implements Factory<K, E, O> {

        @SuppressWarnings("unchecked")
        @Override
        public <K1> @NotNull Factory<K1, E, O> keyClass(Class<? extends K1> keyClass) {
            return (Factory<K1, E, O>) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E1> @NotNull Factory<K, E1, O> eventClass(Class<? extends E1> eventClass) {
            return new FactoryImpl<>((Class<E1>) eventClass, this.orderComparator, this.defaultOrder);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <O1> @NotNull Factory<K, E, O1> orderComparator(Comparator<? super O1> orderComparator) {
            return new FactoryImpl<>(this.eventClass, (Comparator<O1>) orderComparator, null);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <O1> @NotNull Factory<K, E, O1> orderComparator(Comparator<? super O1> orderComparator, O1 defaultOrder) {
            return new FactoryImpl<>(this.eventClass, (Comparator<O1>) orderComparator, defaultOrder);
        }

        @Override
        public <O1 extends Comparable<O1>> @NotNull Factory<K, E, O1> defaultOrder(@NotNull O1 defaultOrder) {
            Objects.requireNonNull(defaultOrder, "defaultOrder cannot be null without orderComparator");
            return new FactoryImpl<>(this.eventClass, Comparator.naturalOrder(), defaultOrder);
        }

        @Override
        public @NotNull TreeEventService<K, E, O> create() {
            return this.create(ListenerExceptionHandler.continueHandler());
        }

        @Override
        public @NotNull TreeEventService<K, E, O> create(@NotNull ListenerExceptionHandler<K, E, O> exceptionHandler) {
            Objects.requireNonNull(exceptionHandler, "exceptionHandler cannot be null.");
            Objects.requireNonNull(this.eventClass, "eventClass is not set.");
            Objects.requireNonNull(this.orderComparator, "orderComparator is not set.");
            return new TreeEventServiceImpl<>(this.eventClass, this.orderComparator, this.defaultOrder, exceptionHandler);
        }
    }
}
