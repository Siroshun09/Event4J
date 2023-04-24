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

package com.github.siroshun09.event4j.test.bus;

import com.github.siroshun09.event4j.bus.EventBus;
import com.github.siroshun09.event4j.bus.EventSubscriber;
import com.github.siroshun09.event4j.bus.PostResult;
import com.github.siroshun09.event4j.bus.SubscribedListener;
import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.key.Key;
import com.github.siroshun09.event4j.priority.Priority;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent2;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent3;
import com.github.siroshun09.event4j.test.sample.listener.CountingListener;
import com.github.siroshun09.event4j.test.sample.listener.CountingMultipleListeners;
import com.github.siroshun09.event4j.test.sample.listener.DummyListener;
import com.github.siroshun09.event4j.test.sample.listener.DummyMultipleListeners;
import com.github.siroshun09.event4j.test.sample.listener.PrioritizedListeners;
import com.github.siroshun09.event4j.test.sample.listener.ThrowingListener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
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

        bus.addResultConsumer(result -> {
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
    void testResultConsumer() {
        var bus = EventBus.create();
        var counter = new AtomicInteger();

        var consumer1 = (Consumer<PostResult<?>>) result -> counter.incrementAndGet();
        bus.addResultConsumer(consumer1);

        bus.callEvent(new SampleEvent());

        Assertions.assertEquals(2, counter.getAndSet(0));

        var consumer2 = (Consumer<PostResult<?>>) result -> counter.incrementAndGet();
        bus.addResultConsumer(consumer2);

        bus.callEvent(new SampleEvent());

        Assertions.assertEquals(4, counter.getAndSet(0));

        Assertions.assertTrue(bus.removeResultConsumer(consumer1));
        Assertions.assertFalse(bus.removeResultConsumer(result -> new Object()));

        bus.callEvent(new SampleEvent());

        Assertions.assertEquals(2, counter.get());
    }

    @Test
    void testSubscribingMultipleListeners() {
        var bus = EventBus.create();
        var counter = new AtomicInteger();

        var listeners = CountingMultipleListeners.create(counter);

        Assertions.assertEquals(3, bus.subscribeAll(Key.random(), listeners).size());
        Assertions.assertEquals(3, bus.getSubscribers().stream().map(EventSubscriber::getSubscribedListeners).mapToInt(List::size).sum());

        bus.callEvent(new SampleEvent());
        bus.callEvent(new SampleEvent2());
        Assertions.assertEquals(5, counter.get());

        Assertions.assertDoesNotThrow(() -> bus.subscribeAll(Key.random(), DummyMultipleListeners.create()));
        Assertions.assertDoesNotThrow(() -> bus.subscribeAll(Key.random(), CountingMultipleListeners.create(counter)));
    }

    @Test
    void testUnsubscribingMultipleListeners() {
        var bus = EventBus.create();
        var counter = new AtomicInteger();

        var listeners = CountingMultipleListeners.create(counter);

        var subscribedListeners = bus.subscribeAll(Key.random(), listeners);

        Assertions.assertEquals(3, getNumberOfListeners(bus));

        bus.unsubscribeAll(subscribedListeners);

        Assertions.assertEquals(0, getNumberOfListeners(bus));
    }

    @Test
    void testUnsubscribe() {
        var bus = EventBus.create();

        var subscribed = bus.getSubscriber(SampleEvent.class).subscribe(Key.random(), DummyListener.create());

        Assertions.assertEquals(1, getNumberOfListeners(bus));

        Assertions.assertFalse(bus.unsubscribe(subscribed));

        Assertions.assertEquals(0, getNumberOfListeners(bus));
    }

    @Test
    void testUnsubscribeAll() {
        var bus = EventBus.create();

        var subscribed = bus.subscribeAll(Key.random(), DummyMultipleListeners.create());

        Assertions.assertEquals(3, getNumberOfListeners(bus));

        bus.unsubscribeAll(subscribed);

        Assertions.assertEquals(0, getNumberOfListeners(bus));

        var key = Key.random();

        bus.subscribeAll(key, DummyMultipleListeners.create());

        Assertions.assertEquals(3, getNumberOfListeners(bus));

        bus.unsubscribeAll(key);

        Assertions.assertEquals(0, getNumberOfListeners(bus));
    }

    @Test
    void testPrioritizedListeners() {
        var bus = EventBus.create();

        var listeners = PrioritizedListeners.create();

        bus.subscribeAll(Key.random(), listeners);
        bus.callEvent(new SampleEvent());

        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, listeners.getResult());
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
        Assertions.assertThrows(IllegalStateException.class, () -> bus.subscribeAll(Key.random(), DummyMultipleListeners.create()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.unsubscribe(new SubscribedListener<>(Event.class, Key.random(), DummyListener.create(), Priority.NORMAL)));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.unsubscribeAll(Collections.emptyList()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.unsubscribeAll(Key.random()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.unsubscribeIf(l -> true));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.callEvent(new SampleEvent()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.callEventAsync(new SampleEvent()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.callEventAsync(new SampleEvent(), Runnable::run));
        Assertions.assertThrows(IllegalStateException.class, bus::close);
        Assertions.assertThrows(IllegalStateException.class, () -> bus.addResultConsumer(r -> new Object()));
        Assertions.assertThrows(IllegalStateException.class, () -> bus.removeResultConsumer(r -> new Object()));
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
        Assertions.assertThrows(NullPointerException.class, () -> bus.unsubscribe(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.subscribeAll(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.subscribeAll(Key.random(), null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.unsubscribeAll((List<SubscribedListener<?>>) null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.unsubscribeAll((Key) null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.unsubscribeIf(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEvent(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(null, Runnable::run));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(new SampleEvent(), (Executor) null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.callEventAsync(null, (Function<Supplier<Event>, CompletableFuture<Event>>) null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.addResultConsumer(null));
        Assertions.assertThrows(NullPointerException.class, () -> bus.removeResultConsumer(null));
    }

    private int getNumberOfListeners(@NotNull EventBus<?> bus) {
        return bus.getSubscribers().stream().map(EventSubscriber::getSubscribedListeners).mapToInt(List::size).sum();
    }
}
