/*
 *     Copyright (c) 2020-2022 Siroshun09
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

package com.github.siroshun09.event4j.test.bus;

import com.github.siroshun09.event4j.bus.EventBus;
import com.github.siroshun09.event4j.bus.SubscribedListener;
import com.github.siroshun09.event4j.key.Key;
import com.github.siroshun09.event4j.priority.Priority;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent;
import com.github.siroshun09.event4j.test.sample.listener.CountingListener;
import com.github.siroshun09.event4j.test.sample.listener.DummyListener;
import com.github.siroshun09.event4j.test.sample.listener.ThrowingListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventSubscriberTest {

    @Test
    void testCreating() {
        var bus = EventBus.create();

        var subscriber = bus.getSubscriber(SampleEvent.class);

        Assertions.assertTrue(subscriber.getSubscribedListeners().isEmpty());
        Assertions.assertFalse(subscriber.isClosed());
        Assertions.assertSame(SampleEvent.class, subscriber.getEventClass());
    }

    @Test
    void testListenerSubscribing() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);
        var key = Key.random();
        var listener = DummyListener.<SampleEvent>create();

        var subscribed1 = subscriber.subscribe(key, listener);

        Assertions.assertEquals(key, subscribed1.key());
        Assertions.assertEquals(listener, subscribed1.listener());
        Assertions.assertEquals(Priority.NORMAL, subscribed1.priority());
        Assertions.assertEquals(1, subscriber.getSubscribedListeners().size());

        var subscribed2 = subscriber.subscribe(key, listener, Priority.HIGH);

        Assertions.assertEquals(key, subscribed2.key());
        Assertions.assertEquals(listener, subscribed2.listener());
        Assertions.assertEquals(Priority.HIGH, subscribed2.priority());
        Assertions.assertEquals(2, subscriber.getSubscribedListeners().size());
    }

    @Test
    void testListenerUnsubscribing() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);
        var key = Key.random();

        var subscribed = subscriber.subscribe(key, DummyListener.create());

        Assertions.assertTrue(subscriber.unsubscribe(subscribed));
        Assertions.assertTrue(subscriber.getSubscribedListeners().isEmpty());

        subscriber.subscribe(key, DummyListener.create());
        subscriber.subscribe(key, CountingListener.create(new AtomicInteger()));

        Assertions.assertTrue(subscriber.unsubscribeAll(key));
        Assertions.assertTrue(subscriber.getSubscribedListeners().isEmpty());
    }

    @Test
    void testListenerPriority() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);
        var key = Key.random();
        var recorder = new ArrayList<Priority>();

        var expected = List.of(
                Priority.value(-256), Priority.value(-256), Priority.LOWEST, Priority.LOW,
                Priority.NORMAL, Priority.HIGH, Priority.HIGHEST, Priority.value(256)
        );

        var copied = new ArrayList<>(expected);

        Collections.shuffle(copied);

        copied.forEach(priority -> subscriber.subscribe(key, event -> recorder.add(priority), priority));

        subscriber.post(new SampleEvent());

        Assertions.assertEquals(expected, recorder);
    }

    @Test
    void testEventPosting() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);
        var key = Key.random();

        subscriber.subscribe(key, DummyListener.create());

        var event1 = new SampleEvent();
        var result1 = subscriber.post(event1);

        Assertions.assertSame(event1, result1.event());
        Assertions.assertTrue(result1.isSuccess());
        Assertions.assertFalse(result1.isFailure());
        Assertions.assertTrue(result1.exceptions().isEmpty());

        subscriber.subscribe(key, ThrowingListener.create());

        var event2 = new SampleEvent();
        var result2 = subscriber.post(event2);

        Assertions.assertSame(event2, result2.event());
        Assertions.assertFalse(result2.isSuccess());
        Assertions.assertTrue(result2.isFailure());
        Assertions.assertFalse(result2.exceptions().isEmpty());
        Assertions.assertEquals(1, result2.exceptions().size());
    }

    @Test
    void testClosing() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);

        Assertions.assertFalse(subscriber.isClosed());
        Assertions.assertDoesNotThrow(() -> subscriber.post(new SampleEvent()));

        bus.close();
        Assertions.assertTrue(subscriber.isClosed());
        Assertions.assertTrue(subscriber.getSubscribedListeners().isEmpty());
        Assertions.assertThrows(IllegalStateException.class, () -> subscriber.post(new SampleEvent()));
        Assertions.assertThrows(IllegalStateException.class, () -> subscriber.subscribe(Key.random(), DummyListener.create()));
        Assertions.assertThrows(IllegalStateException.class, () -> subscriber.subscribe(Key.random(), DummyListener.create(), Priority.HIGH));
        Assertions.assertThrows(IllegalStateException.class, () -> subscriber.unsubscribe(new SubscribedListener<>(SampleEvent.class, Key.random(), DummyListener.create(), Priority.NORMAL)));
        Assertions.assertThrows(IllegalStateException.class, () -> subscriber.unsubscribeAll(Key.random()));
        Assertions.assertThrows(IllegalStateException.class, () -> subscriber.unsubscribeIf(s -> true));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testIllegalArguments() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);

        Assertions.assertThrows(NullPointerException.class, () -> subscriber.post(null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(Key.random(), null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(null, null, null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(Key.random(), DummyListener.create(), null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(Key.random(), DummyListener.create(), null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(null, null, 0));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.subscribe(Key.random(), null, 0));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.unsubscribe(null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.unsubscribeAll(null));
        Assertions.assertThrows(NullPointerException.class, () -> subscriber.unsubscribeIf(null));
    }
}
