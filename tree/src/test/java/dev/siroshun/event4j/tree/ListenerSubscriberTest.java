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
import dev.siroshun.event4j.test.helper.event.ExtendedSampleEvent;
import dev.siroshun.event4j.test.helper.event.SampleEvent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static dev.siroshun.event4j.tree.TestHelper.emptyConsumer;
import static dev.siroshun.event4j.tree.TestHelper.newListenerList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListenerSubscriberTest {

    @Test
    void testSubscribe() {
        var subscriber = new ListenerSubscriberImpl<>(newListenerList(), Priority.NORMAL);

        assertTrue(subscriber.allListeners().isEmpty());
        assertTrue(subscriber.listenersFor(SampleEvent.class).isEmpty());

        var subscribed1 = subscriber.subscribe(SampleEvent.class, "test-1", emptyConsumer());
        assertEquals(subscribed1.eventClass(), SampleEvent.class);
        assertEquals(subscribed1.key(), "test-1");
        assertEquals(subscribed1.order(), Priority.NORMAL);
        assertEquals(List.of(subscribed1), subscriber.allListeners());
        assertEquals(List.of(subscribed1), subscriber.listenersFor(SampleEvent.class));

        var subscribed2 = subscriber.subscribe(SampleEvent.class, "test-2", emptyConsumer(), Priority.HIGH);
        assertEquals(subscribed2.eventClass(), SampleEvent.class);
        assertEquals(subscribed2.key(), "test-2");
        assertEquals(subscribed2.order(), Priority.HIGH);
        assertEquals(List.of(subscribed1, subscribed2), subscriber.allListeners());
        assertEquals(List.of(subscribed1, subscribed2), subscriber.listenersFor(SampleEvent.class));

        var subscribed3 = subscriber.subscribe(SampleEvent.class, factory -> factory.key("test-3").consumer(emptyConsumer()).order(Priority.LOW));
        assertEquals(subscribed3.eventClass(), SampleEvent.class);
        assertEquals(subscribed3.key(), "test-3");
        assertEquals(subscribed3.order(), Priority.LOW);
        assertEquals(List.of(subscribed3, subscribed1, subscribed2), subscriber.allListeners());
        assertEquals(List.of(subscribed3, subscribed1, subscribed2), subscriber.listenersFor(SampleEvent.class));

        subscriber.unsubscribe(subscribed3);
        assertEquals(List.of(subscribed1, subscribed2), subscriber.allListeners());
        assertEquals(List.of(subscribed1, subscribed2), subscriber.listenersFor(SampleEvent.class));

        subscriber.unsubscribeIf(listener -> listener == subscribed2);
        assertEquals(List.of(subscribed1), subscriber.allListeners());
        assertEquals(List.of(subscribed1), subscriber.listenersFor(SampleEvent.class));

        subscriber.unsubscribeByKey("test-1");
        assertTrue(subscriber.allListeners().isEmpty());
        assertTrue(subscriber.listenersFor(SampleEvent.class).isEmpty());
    }

    @Test
    void testUnsubscribe() {
        var subscriber = new ListenerSubscriberImpl<>(newListenerList(), Priority.NORMAL);
        subscriber.subscribe(SampleEvent.class, "test-1", emptyConsumer());
        subscriber.subscribe(SampleEvent.class, "test-2", emptyConsumer(), Priority.HIGH);
        subscriber.subscribe(ExtendedSampleEvent.class, "test-3", emptyConsumer(), Priority.LOW);

        var listeners = new ArrayList<>(subscriber.allListeners());

        for (int i = 0, size = listeners.size(); i < size; i++) {
            var subscribed = listeners.remove(0);
            subscriber.unsubscribe(subscribed);
            assertEquals(listeners, subscriber.allListeners());
        }
    }

    @Test
    void testUnsubscribeIf() {
        var subscriber = new ListenerSubscriberImpl<>(newListenerList(), Priority.NORMAL);
        subscriber.subscribe(SampleEvent.class, "test-1", emptyConsumer());
        subscriber.subscribe(SampleEvent.class, "test-2", emptyConsumer(), Priority.HIGH);

        var differentEventListener = subscriber.subscribe(ExtendedSampleEvent.class, "test-3", emptyConsumer(), Priority.LOW);
        subscriber.unsubscribeIf(listener -> listener.eventClass() == SampleEvent.class);

        assertEquals(List.of(differentEventListener), subscriber.allListeners());
        assertEquals(List.of(), subscriber.listenersFor(SampleEvent.class));
        assertEquals(List.of(differentEventListener), subscriber.listenersFor(ExtendedSampleEvent.class));
    }

    @Test
    void testUnsubscribeByKey() {
        var subscriber = new ListenerSubscriberImpl<>(newListenerList(), Priority.NORMAL);
        subscriber.subscribe(SampleEvent.class, "test-1", emptyConsumer());
        subscriber.subscribe(SampleEvent.class, "test-1", emptyConsumer(), Priority.HIGH);

        var differentKeyListener = subscriber.subscribe(SampleEvent.class, "test-2", emptyConsumer(), Priority.HIGH);
        subscriber.unsubscribeByKey("test-1");
        assertEquals(List.of(differentKeyListener), subscriber.allListeners());
        assertEquals(List.of(differentKeyListener), subscriber.listenersFor(SampleEvent.class));
    }

    @Test
    void testUnsubscribeAll() {
        var subscriber = new ListenerSubscriberImpl<>(newListenerList(), Priority.NORMAL);
        var all = List.of(
            subscriber.subscribe(SampleEvent.class, "test-1", emptyConsumer()),
            subscriber.subscribe(SampleEvent.class, "test-2", emptyConsumer(), Priority.HIGH),
            subscriber.subscribe(ExtendedSampleEvent.class, "test-3", emptyConsumer(), Priority.LOW),
            subscriber.subscribe(SampleEvent.class, "test-4", emptyConsumer(), Priority.HIGH)
        );

        subscriber.unsubscribeAll(all);
        assertEquals(List.of(), subscriber.allListeners());
        assertEquals(List.of(), subscriber.listenersFor(SampleEvent.class));
        assertEquals(List.of(), subscriber.listenersFor(ExtendedSampleEvent.class));
    }
}
