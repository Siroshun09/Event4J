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
import com.github.siroshun09.event4j.listener.Listener;
import com.github.siroshun09.event4j.priority.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

class SimpleEventSubscriber<E> implements EventSubscriber<E> {

    private final Class<E> eventClass;
    private final List<SubscribedListener<E>> subscribedListeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private List<SubscribedListener<E>> sortedListeners = Collections.emptyList();

    SimpleEventSubscriber(@NotNull Class<E> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public @NotNull Class<E> getEventClass() {
        return eventClass;
    }

    @Override
    public @NotNull PostResult<E> post(@NotNull E event) {
        Objects.requireNonNull(event);
        checkClosed();

        if (sortedListeners.isEmpty()) {
            return PostResult.success(event);
        }

        Map<SubscribedListener<E>, Throwable> exceptions = null;

        for (var subscribed : sortedListeners) {
            try {
                subscribed.listener().handle(event);
            } catch (Throwable exception) {
                if (exceptions == null) {
                    exceptions = new HashMap<>();
                }

                exceptions.put(subscribed, exception);

                try {
                    subscribed.listener().handleException(event, exception);
                } catch (Throwable ignored) {
                }
            }
        }

        if (exceptions == null) {
            return PostResult.success(event);
        } else {
            return PostResult.failure(event, exceptions);
        }
    }

    @Override
    public @NotNull SubscribedListener<E> subscribe(@NotNull Key key, @NotNull Listener<E> listener) {
        return subscribe(key, listener, Priority.NORMAL);
    }

    @Override
    public @NotNull SubscribedListener<E> subscribe(@NotNull Key key, @NotNull Listener<E> listener, @NotNull Priority priority) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(listener);
        Objects.requireNonNull(priority);
        checkClosed();

        var subscription = new SubscribedListener<>(eventClass, key, listener, priority);

        subscribedListeners.add(subscription);
        sortListeners();

        return subscription;
    }

    @Override
    public @NotNull SubscribedListener<E> subscribe(@NotNull Key key, @NotNull Listener<E> listener, int priorityValue) {
        return subscribe(key, listener, Priority.value(priorityValue));
    }

    @Override
    public boolean unsubscribe(@NotNull SubscribedListener<E> subscribedListener) {
        Objects.requireNonNull(subscribedListener);
        checkClosed();

        if (subscribedListeners.remove(subscribedListener)) {
            sortListeners();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unsubscribeAll(@NotNull Key key) {
        Objects.requireNonNull(key);

        return unsubscribeIf(listener -> listener.key().equals(key));
    }

    @Override
    public boolean unsubscribeIf(@NotNull Predicate<SubscribedListener<?>> predicate) {
        Objects.requireNonNull(predicate);
        checkClosed();

        if (subscribedListeners.removeIf(predicate)) {
            sortListeners();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @NotNull @UnmodifiableView List<SubscribedListener<E>> getSubscribedListeners() {
        return Collections.unmodifiableList(subscribedListeners);
    }

    void close() {
        closed.set(true);

        subscribedListeners.clear();
        sortedListeners = Collections.emptyList();
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public String toString() {
        return "SimpleEventSubscriber{" +
                "eventClass=" + eventClass +
                ", subscribedListeners=" + subscribedListeners +
                ", closed=" + closed +
                '}';
    }

    private void sortListeners() {
        sortedListeners =
                subscribedListeners.stream()
                        .sorted(Comparator.comparing(SubscribedListener::priority))
                        .toList();
    }

    private void checkClosed() {
        if (closed.get()) {
            throw new IllegalStateException("The event subscriber is closed.");
        }
    }
}
