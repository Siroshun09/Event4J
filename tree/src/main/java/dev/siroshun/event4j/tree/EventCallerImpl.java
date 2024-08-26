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

import dev.siroshun.event4j.api.caller.EventCaller;
import dev.siroshun.event4j.api.listener.ListenerExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class EventCallerImpl<K, E, O> implements EventCaller<E> {

    private final ListenerList<K, E, O> listenerList;
    private final ListenerExceptionHandler<K, E, O> exceptionHandler;

    EventCallerImpl(@NotNull ListenerList<K, E, O> listenerList,
                    @NotNull ListenerExceptionHandler<K, E, O> exceptionHandler) {
        this.listenerList = listenerList;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void call(@NotNull E event) {
        Objects.requireNonNull(event);

        var firstHolder = this.listenerList.holder(event.getClass().asSubclass(this.listenerList.eventClass()));
        for (var holder = firstHolder; holder != null; holder = holder.parent()) {
            if (!holder.postEvent(event, this.exceptionHandler)) {
                break;
            }
        }
    }
}
