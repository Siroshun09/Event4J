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

import dev.siroshun.event4j.api.listener.ListenerExceptionHandler;
import dev.siroshun.event4j.api.priority.Priority;
import dev.siroshun.event4j.test.helper.event.ExtendedSampleEvent;
import dev.siroshun.event4j.test.helper.event.SampleEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static dev.siroshun.event4j.tree.TestHelper.newListener;
import static dev.siroshun.event4j.tree.TestHelper.newListenerList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventCallerTest {

    @Test
    void testCall() {
        var list = newListenerList();
        var counter = new AtomicInteger(0);
        list.holder(SampleEvent.class).modifyListeners(listeners -> listeners.add(newListener(e -> counter.incrementAndGet(), Priority.NORMAL)));
        list.holder(ExtendedSampleEvent.class).modifyListeners(listeners -> listeners.add(newListener(ExtendedSampleEvent.class, e -> counter.incrementAndGet(), Priority.NORMAL)));
        var caller = new EventCallerImpl<>(list, ListenerExceptionHandler.continueHandler());

        caller.call(new SampleEvent());
        assertEquals(1, counter.getAndSet(0));

        caller.call(new ExtendedSampleEvent());
        assertEquals(2, counter.getAndSet(0));
    }

    @ParameterizedTest
    @MethodSource("orderTestCases")
    void testCallOrder(@NotNull OrderTestCase testCase) {
        var list = newListenerList();
        var counter = new AtomicInteger(testCase.expectedCount());
        var caller = new EventCallerImpl<>(list, ListenerExceptionHandler.continueHandler());

        addOrderCheckListener(list, counter, SampleEvent.class, 1);
        addOrderCheckListener(list, counter, ExtendedSampleEvent.class, 2);
        addOrderCheckListener(list, counter, ExtendedSampleEvent2.class, 3);
        addOrderCheckListener(list, counter, SampleEvent2.class, 2);

        caller.call(testCase.event());
        assertEquals(0, counter.get());
    }

    private static Stream<OrderTestCase> orderTestCases() {
        return Stream.of(
            new OrderTestCase(new SampleEvent(), 1),
            new OrderTestCase(new ExtendedSampleEvent(), 2),
            new OrderTestCase(new ExtendedSampleEvent2(), 3),
            new OrderTestCase(new SampleEvent2(), 2)
        );
    }

    private record OrderTestCase(@NotNull SampleEvent event, int expectedCount) {
    }

    private static <E extends SampleEvent> void addOrderCheckListener(@NotNull ListenerList<String, SampleEvent, Priority> list, @NotNull AtomicInteger counter, @NotNull Class<E> eventClass, int order) {
        list.holder(eventClass).modifyListeners(listeners -> listeners.add(newListener(
            eventClass,
            e -> assertEquals(order, counter.getAndDecrement()),
            Priority.NORMAL)
        ));
    }

    private static class ExtendedSampleEvent2 extends ExtendedSampleEvent {
    }

    private static class SampleEvent2 extends SampleEvent {
    }
}
