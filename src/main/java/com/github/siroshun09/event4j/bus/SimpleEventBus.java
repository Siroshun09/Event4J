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

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.handlerlist.HandlerList;
import com.github.siroshun09.event4j.handlerlist.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class that implements {@link EventBus}.
 */
public class SimpleEventBus implements EventBus {

    private final Map<Class<?>, HandlerList<?>> handlers = new HashMap<>();

    /**
     * Creates a {@link SimpleEventBus}.
     */
    public SimpleEventBus() {
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public <T extends Event> HandlerList<T> getHandlerList(@NotNull Class<T> eventClass) {
        Objects.requireNonNull(eventClass);

        var handlerList = searchForHandlerList(eventClass);

        if (handlerList == null) {
            handlerList = HandlerList.create();
            handlers.put(eventClass, handlerList);
        }

        return handlerList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribeAll(@NotNull Key key) {
        Objects.requireNonNull(key);

        handlers.values().forEach(handlerList -> handlerList.unsubscribeAll(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Event> T callEvent(@NotNull T event) {
        Objects.requireNonNull(event);

        var eventClass = (Class<T>) event.getClass();
        var handlerList = searchForHandlerList(eventClass);

        if (handlerList != null) {
            handlerList.post(event);
        }

        for (var clazz = eventClass.getSuperclass();
             clazz != null && Event.class.isAssignableFrom(clazz);
             clazz = clazz.getSuperclass()) {

            var subHandlerList = (HandlerList) searchForHandlerList((Class<? extends Event>) clazz);

            if (subHandlerList != null) {
                subHandlerList.post(event);
            }
        }

        return event;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Nullable
    private <T extends Event> HandlerList<T> searchForHandlerList(@NotNull Class<T> eventClass) {
        return (HandlerList<T>) handlers.get(eventClass);
    }

    @Override
    public String toString() {
        return "SimpleEventBus{" +
                "handlers=" + handlers +
                '}';
    }
}
