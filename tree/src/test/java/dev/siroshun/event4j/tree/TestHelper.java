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

import dev.siroshun.event4j.api.priority.Priority;
import dev.siroshun.event4j.test.helper.event.SampleEvent;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;

import java.util.function.Consumer;

@NullMarked
final class TestHelper {

    static ListenerList<String, SampleEvent, Priority> newListenerList() {
        return new ListenerList<>(SampleEvent.class, Priority.COMPARATOR);
    }

    static SubscribedListenerImpl<String, SampleEvent, Priority> newListener(Consumer<SampleEvent> listener, Priority priority) {
        return newListener(SampleEvent.class, listener, priority);
    }

    static <E> SubscribedListenerImpl<String, E, Priority> newListener(Class<E> eventClass, Consumer<E> listener, Priority priority) {
        return new SubscribedListenerImpl<>(eventClass, priority.toString(), classCheckingConsumer(eventClass, listener), priority);
    }

    static <E> Consumer<E> classCheckingConsumer(Class<E> eventClass, Consumer<E> consumer) {
        return e -> consumer.accept(Assertions.assertInstanceOf(eventClass, e));
    }

    static <E> Consumer<E> emptyConsumer() {
        return e -> {
        };
    }
}
