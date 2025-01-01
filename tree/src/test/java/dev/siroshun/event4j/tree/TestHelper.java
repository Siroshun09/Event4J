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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.function.Consumer;

final class TestHelper {

    static @NotNull ListenerList<String, SampleEvent, Priority> newListenerList() {
        return new ListenerList<>(SampleEvent.class, Priority.COMPARATOR);
    }

    static @NotNull SubscribedListenerImpl<String, SampleEvent, Priority> newListener(@NotNull Consumer<SampleEvent> listener, @NotNull Priority priority) {
        return newListener(SampleEvent.class, listener, priority);
    }

    static <E> @NotNull SubscribedListenerImpl<String, E, Priority> newListener(@NotNull Class<E> eventClass, @NotNull Consumer<E> listener, @NotNull Priority priority) {
        return new SubscribedListenerImpl<>(eventClass, priority.toString(), classCheckingConsumer(eventClass, listener), priority);
    }

    static <E> @NotNull Consumer<E> classCheckingConsumer(@NotNull Class<E> eventClass, @NotNull Consumer<E> consumer) {
        return e -> consumer.accept(Assertions.assertInstanceOf(eventClass, e));
    }

    static <E> @NotNull Consumer<E> emptyConsumer() {
        return e -> {
        };
    }
}
