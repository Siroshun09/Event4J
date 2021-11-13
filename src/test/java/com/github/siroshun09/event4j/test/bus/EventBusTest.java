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

package com.github.siroshun09.event4j.test.bus;

import com.github.siroshun09.event4j.bus.EventBus;
import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.key.Key;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent2;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent3;
import com.github.siroshun09.event4j.test.sample.listener.CountingListener;
import com.github.siroshun09.event4j.test.sample.listener.ThrowingListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public class EventBusTest {

    @Test
    void testCreatingBus() {
        var bus1 = EventBus.create();
        Assertions.assertEquals(Event.class, bus1.getEventClass());

        var bus2 = EventBus.create(SampleEvent.class);
        Assertions.assertEquals(SampleEvent.class, bus2.getEventClass());

        var executor = Executors.newSingleThreadExecutor();
        var bus3 = EventBus.create(executor);
        Assertions.assertEquals(Event.class, bus3.getEventClass());

        var bus4 = EventBus.create(SampleEvent.class, executor);
        Assertions.assertEquals(SampleEvent.class, bus4.getEventClass());

        executor.shutdownNow();
    }

    @Test
    void testGettingSubscriber() {
        var bus = EventBus.create();
        Assertions.assertTrue(bus.getSubscribers().isEmpty());

        bus.getSubscriber(SampleEvent.class);

        Assertions.assertEquals(1, bus.getSubscribers().size());

        bus.callEvent(new SampleEvent2());

        Assertions.assertEquals(1, bus.getSubscribers().size());
    }

    @Test
    void testEventPosting() {
        var bus = EventBus.create();
        var key = Key.random();
        var counter = new AtomicInteger();

        List.of(
                Event.class, SampleEvent2.class,
                SampleEvent.class, SampleEvent.class // duplicated listeners to the same event
        ).forEach(clazz -> bus.getSubscriber(clazz).subscribe(key, CountingListener.create(counter)));

        bus.callEvent(new SampleEvent()); // 3 increased
        bus.callEvent(new SampleEvent3()); // 4 increased

        Assertions.assertEquals(7, counter.get());
    }

    @Test
    void testAsyncEventPosting() {
        var bus = EventBus.create();
        var key = Key.random();
        var mainThread = Thread.currentThread();
        var threadReference = new AtomicReference<Thread>();

        bus.getSubscriber(SampleEvent.class).subscribe(key, e -> threadReference.set(Thread.currentThread()));

        bus.callEvent(new SampleEvent());
        Assertions.assertEquals(mainThread, threadReference.get());

        bus.callEventAsync(new SampleEvent()).join();
        Assertions.assertNotEquals(mainThread, threadReference.get());
    }

    @Test
    void testCustomEventType() {
        var bus = EventBus.create(SampleEvent.class);
        var key = Key.random();
        var counter = new AtomicInteger();

        List.of(SampleEvent.class, SampleEvent2.class)
                .forEach(clazz -> bus.getSubscriber(clazz).subscribe(key, CountingListener.create(counter)));

        bus.callEvent(new SampleEvent()); // 1 increased
        bus.callEvent(new SampleEvent3()); // 2 increased

        Assertions.assertEquals(3, counter.get());
    }

    @Test
    void testExceptionHandling() {
        var bus = EventBus.create();
        var key = Key.random();
        var listener = ThrowingListener.<SampleEvent>create();
        var subscribed = bus.getSubscriber(SampleEvent.class).subscribe(key, listener);

        var isSuccess = new AtomicBoolean(true);
        var isFailure = new AtomicBoolean();
        var exceptionReference = new AtomicReference<Throwable>();

        bus.setResultConsumer(result -> {
            var exception = result.exceptions().get(subscribed);

            if (exception != null) {
                isSuccess.set(result.isSuccess());
                isFailure.set(result.isFailure());

                exceptionReference.set(exception);
            }
        });

        Assertions.assertDoesNotThrow(() -> bus.callEvent(new SampleEvent()));

        Assertions.assertFalse(isSuccess.get());
        Assertions.assertTrue(isFailure.get());

        Assertions.assertEquals(listener.getOriginalException(), exceptionReference.get());
        Assertions.assertEquals(listener.getOriginalException(), listener.getHandledException());
    }

    @Test
    void testClosing() {
        var bus = EventBus.create();
        var subscriber = bus.getSubscriber(SampleEvent.class);

        Assertions.assertFalse(bus.isClosed());
        Assertions.assertFalse(subscriber.isClosed());
        Assertions.assertDoesNotThrow(() -> bus.callEvent(new SampleEvent()));

        bus.close();
        Assertions.assertTrue(bus.isClosed());
        Assertions.assertTrue(bus.getSubscribers().isEmpty());
        Assertions.assertTrue(subscriber.isClosed());

        Assertions.assertThrows(IllegalStateException.class, () -> bus.getSubscriber(Event.class));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.unsubscribeAll(Key.random()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.unsubscribeIf(l -> true));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.callEvent(new SampleEvent()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.callEventAsync(new SampleEvent()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.callEventAsync(new SampleEvent(), Runnable::run));
        Assertions.assertThrows(IllegalStateException.class, bus::close);
        Assertions.assertThrows(IllegalStateException.class, () -> bus.setResultConsumer(r -> new Object()));
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Test
    void testIllegalArguments() {
        Assertions.assertThrows(NullPointerException.class, () -> EventBus.create((Class<?>) null));
        Assertions.assertThrows(NullPointerException.class, () -> EventBus.create((Executor) null));
        Assertions.assertThrows(NullPointerException.class, () -> EventBus.create((Class<?>) null, Runnable::run));
        Assertions.assertThrows(NullPointerException.class, () -> EventBus.create(Event.class, null));

        var bus = EventBus.create();

        Assertions.assertThrows(NullPointerException.class, () -> bus.getSubscriber(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.unsubscribeAll(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.unsubscribeIf(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEvent(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(null, Runnable::run));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(new SampleEvent(), (Executor) null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(null, (Function<Supplier<Event>, CompletableFuture<Event>>) null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.setResultConsumer(null));
    }
}
