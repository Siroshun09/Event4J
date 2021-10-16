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

package com.github.siroshun09.event4j.handlerlist;

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.listener.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

final class HandlerListImpl<T extends Event> implements HandlerList<T> {

    private final List<SubscribedListener<T>> subscribedListeners = new CopyOnWriteArrayList<>();
    private List<Listener<T>> sortedListeners = Collections.emptyList();
    private Logger exceptionLogger;

    HandlerListImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void post(@NotNull T event) {
        Objects.requireNonNull(event);

        var listeners = sortedListeners;

        for (var listener : listeners) {
            try {
                listener.handle(event);
            } catch (Throwable exception1) {
                if (exceptionLogger != null) {
                    exceptionLogger.log(
                            Level.WARNING,
                            "An exception occurred while posting the event '"
                                    + event.getEventName()
                                    + '\'',
                            exception1
                    );
                }

                try {
                    listener.handleException(event, exception1);
                } catch (Throwable exception2) {
                    if (exceptionLogger != null) {
                        exceptionLogger.log(
                                Level.WARNING,
                                "An exception occurred while handling the exception",
                                exception2
                        );
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean subscribe(@NotNull Key key, @NotNull Listener<T> listener) {
        return subscribe(key, listener, Priority.NORMAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean subscribe(@NotNull Key key, @NotNull Listener<T> listener, @NotNull Priority priority) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(listener);
        Objects.requireNonNull(priority);

        var listenerToSubscribe = new SubscribedListener<>(key, listener, priority);

        if (subscribedListeners.contains(listenerToSubscribe)) {
            return false;
        }

        subscribedListeners.add(listenerToSubscribe);
        updateSortedListeners();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unsubscribe(@NotNull Listener<T> listener) {
        Objects.requireNonNull(listener);

        var matchedListeners =
                subscribedListeners.stream()
                        .filter(subscribed -> subscribed.listener.equals(listener))
                        .collect(Collectors.toUnmodifiableSet());

        if (matchedListeners.isEmpty()) {
            return false;
        } else {
            matchedListeners.forEach(subscribedListeners::remove);
            updateSortedListeners();
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unsubscribe(@NotNull Listener<T> listener, @NotNull Priority priority) {
        Objects.requireNonNull(listener);
        Objects.requireNonNull(priority);

        var matchedListeners =
                subscribedListeners.stream()
                        .filter(subscribed -> subscribed.listener.equals(listener))
                        .filter(subscribed -> subscribed.priority.compareTo(priority) == 0)
                        .collect(Collectors.toUnmodifiableSet());

        if (matchedListeners.isEmpty()) {
            return false;
        } else {
            matchedListeners.forEach(subscribedListeners::remove);
            updateSortedListeners();
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribeAll(@NotNull Key key) {
        Objects.requireNonNull(key);

        var matchedListeners =
                subscribedListeners.stream()
                        .filter(subscribed -> subscribed.key.equals(key))
                        .collect(Collectors.toUnmodifiableSet());

        if (!matchedListeners.isEmpty()) {
            matchedListeners.forEach(subscribedListeners::remove);
            updateSortedListeners();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return subscribedListeners.size();
    }

    @Override
    public void setExceptionLogger(@Nullable Logger logger) {
        this.exceptionLogger = logger;
    }

    private void updateSortedListeners() {
        sortedListeners = subscribedListeners.stream().sorted()
                .map(SubscribedListener::getListener)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String toString() {
        return "HandlerListImpl{" +
                "subscribedListeners=" + subscribedListeners +
                '}';
    }

    private static class SubscribedListener<T extends Event> implements Comparable<SubscribedListener<T>> {

        private final Key key;
        private final Listener<T> listener;
        private final Priority priority;

        private SubscribedListener(@NotNull Key key, @NotNull Listener<T> listener, @NotNull Priority priority) {
            this.key = key;
            this.listener = listener;
            this.priority = priority;
        }

        private Listener<T> getListener() {
            return listener;
        }

        @Override
        public int compareTo(@NotNull HandlerListImpl.SubscribedListener<T> o) {
            return priority.compareTo(o.priority);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof HandlerListImpl.SubscribedListener)) {
                return false;
            }

            var that = (SubscribedListener<?>) o;

            return Objects.equals(key, that.key) &&
                    Objects.equals(listener, that.listener) &&
                    Objects.equals(priority, that.priority);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, listener, priority);
        }

        @Override
        public String toString() {
            return "SubscribedListener{" +
                    "key=" + key.getName() +
                    ", listener=" + listener.getClass().getSimpleName() +
                    ", priority=" + priority.getPriority() +
                    '}';
        }
    }
}
