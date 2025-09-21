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

import dev.siroshun.event4j.api.listener.ListenerExceptionHandler;
import dev.siroshun.event4j.api.listener.SubscribedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

class ListenerList<K, E, O> {

    private final Class<E> eventClass;
    private final Comparator<O> orderComparator;

    private final Map<Class<? extends E>, Holder<E>> holderMap = new ConcurrentHashMap<>();

    ListenerList(@NotNull Class<E> eventClass, @NotNull Comparator<O> orderComparator) {
        this.eventClass = eventClass;
        this.orderComparator = orderComparator;
    }

    @NotNull Class<E> eventClass() {
        return this.eventClass;
    }

    @SuppressWarnings("unchecked")
    <T extends E> @NotNull Holder<T> holder(@NotNull Class<T> eventClass) {
        var existing = this.holderMap.get(eventClass);
        return (Holder<T>) (existing != null ? existing : this.createAndPutHolder(eventClass));
    }

    private @NotNull Holder<E> createAndPutHolder(@NotNull Class<? extends E> eventClass) {
        var created = this.createHolder(eventClass);
        return Objects.requireNonNullElse(this.holderMap.putIfAbsent(eventClass, created), created);
    }

    @SuppressWarnings("unchecked")
    private @NotNull Holder<E> createHolder(@NotNull Class<? extends E> eventClass) {
        var superClass = eventClass.getSuperclass();
        return new Holder<>(
            superClass != null && this.eventClass.isAssignableFrom(superClass) ?
                this.holder(superClass.asSubclass(this.eventClass)) :
                null,
            this.orderComparator
        );
    }

    @NotNull Collection<Holder<E>> holders() {
        return this.holderMap.values();
    }

    class Holder<T> {

        private final @Nullable Holder<E> parent;
        private final Comparator<SubscribedListener<K, T, O>> sorter;

        private final StampedLock lock = new StampedLock();
        private final List<SubscribedListener<K, T, O>> listeners = new ArrayList<>();

        volatile SubscribedListener<K, T, O> @Nullable [] sortedListenersArray;

        Holder(@Nullable Holder<E> parent, @NotNull Comparator<O> orderComparator) {
            this.parent = parent;
            this.sorter = Comparator.comparing(SubscribedListener::order, orderComparator);
        }

        public @Nullable Holder<E> parent() {
            return this.parent;
        }

        @NotNull @Unmodifiable List<SubscribedListener<K, T, O>> listeners() {
            long readLock = this.lock.readLock();
            List<SubscribedListener<K, T, O>> copiedListeners;

            try {
                copiedListeners = new ArrayList<>(this.listeners);
            } finally {
                this.lock.unlockRead(readLock);
            }

            return Collections.unmodifiableList(copiedListeners);
        }

        @SuppressWarnings("unchecked")
        void modifyListeners(@NotNull Consumer<List<SubscribedListener<K, T, O>>> modifier) {
            long writeLock = this.lock.writeLock();

            try {
                modifier.accept(this.listeners);

                if (this.listeners.isEmpty()) {
                    this.sortedListenersArray = null;
                } else {
                    this.listeners.sort(this.sorter);
                    this.sortedListenersArray = this.listeners.toArray(SubscribedListener[]::new);
                }
            } finally {
                this.lock.unlockWrite(writeLock);
            }
        }

        @SuppressWarnings({"unchecked", "UnnecessaryContinue"})
        boolean postEvent(@NotNull E event, @NotNull ListenerExceptionHandler<K, E, O> exceptionHandler) {
            var listeners = this.sortedListenersArray;

            if (listeners == null) {
                return true;
            }

            for (var listener : listeners) {
                try {
                    listener.consumer().accept((T) event);
                } catch (Throwable e) {
                    switch (exceptionHandler.handleException(event, (SubscribedListener<K, ? extends E, O>) listener, e)) {
                        case BREAK -> {
                            return false;
                        }
                        case CONTINUE -> {
                            continue;
                        }
                        case RETHROW -> {
                            rethrow(e);
                            throw new Error(e);
                        }
                    }
                }
            }

            return true;
        }

        @SuppressWarnings("unchecked")
        private static <T extends Throwable> void rethrow(@NotNull Throwable exception) throws T {
            throw (T) exception;
        }
    }
}
