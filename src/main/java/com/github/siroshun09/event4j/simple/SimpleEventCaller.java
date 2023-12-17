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

import com.github.siroshun09.event4j.caller.EventCaller;
import com.github.siroshun09.event4j.listener.ListenerExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SimpleEventCaller<K, E, O> implements EventCaller<E> {

    private final EventSubscriberHolder<K, E, O> subscriberHolder;
    private final ListenerExceptionHandler<K, E, O> exceptionHandler;

    SimpleEventCaller(@NotNull EventSubscriberHolder<K, E, O> subscriberHolder,
                      @Nullable ListenerExceptionHandler<K, E, O> exceptionHandler) {
        this.subscriberHolder = subscriberHolder;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public <T extends E> void call(@NotNull T event) {
        var startNode = this.subscriberHolder.subscriberNode(event.getClass().asSubclass(this.subscriberHolder.getEventClass()));

        for (var node = startNode; node != null; node = node.next()) {
            if (!postEvent(event, node.value(), this.exceptionHandler)) {
                break;
            }
        }
    }

    private static <K, E, O> boolean postEvent(E event, SimpleEventSubscriber<K, E, O> subscriber, @Nullable ListenerExceptionHandler<K, E, O> exceptionHandler) {
        var array = subscriber.sortedListenersArray;

        if (array == null) {
            return true;
        }

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, l = array.length; i < l; i++) {
            var listener = array[i];
            try {
                listener.consumer().accept(event);
            } catch (Throwable e) {
                if (exceptionHandler != null) {
                    switch (exceptionHandler.handleException(event, listener, e)) {
                        case BREAK -> {
                            return false;
                        }
                        case RETHROW -> rethrow(e);
                    }
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void rethrow(Throwable exception) throws T {
        throw (T) exception;
    }
}
