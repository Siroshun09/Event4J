/*
 *     Copyright (c) 2020-2024 Siroshun09
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

import com.github.siroshun09.event4j.listener.ListenerExceptionHandler;
import com.github.siroshun09.event4j.listener.SubscribedListener;
import com.github.siroshun09.event4j.priority.Priority;
import com.github.siroshun09.event4j.shared.event.Event;
import com.github.siroshun09.event4j.shared.event.SampleEvent;
import com.github.siroshun09.event4j.shared.event.SampleEvent2;
import com.github.siroshun09.event4j.shared.event.SampleEvent3;
import com.github.siroshun09.event4j.shared.listener.ThrowingListener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class SimpleEventCallerTest {

    @Test
    void testCall() {
        var holder = new EventSubscriberHolder<>(Event.class, Priority.COMPARATOR, Priority.NORMAL);
        var caller = new SimpleEventCaller<>(holder, (event, listener, exception) -> ListenerExceptionHandler.Result.CONTINUE);

        var counter = new AtomicInteger();

        List.of(
                Event.class, SampleEvent2.class,
                SampleEvent.class, SampleEvent.class // duplicated listeners to the same event
        ).forEach(clazz -> holder.subscriber(clazz).subscribe("test", ignored -> counter.incrementAndGet()));

        caller.call(new SampleEvent()); // 3 increased
        caller.call(new SampleEvent3()); // 4 increased

        Assertions.assertEquals(7, counter.get());
    }

    @Test
    void testExceptionHandling() {
        var holder = new EventSubscriberHolder<>(Event.class, Priority.COMPARATOR, Priority.NORMAL);
        var listener = ThrowingListener.create();
        var event = new SampleEvent();

        var first = holder.subscriber(SampleEvent.class).subscribe("first", listener, Priority.value(1));
        var second = holder.subscriber(SampleEvent.class).subscribe("second", listener, Priority.value(2));

        var counter = new AtomicInteger();
        holder.subscriber(SampleEvent.class).subscribe("third", ignored -> counter.incrementAndGet(), Priority.value(3));

        var continueHandler = new ExceptionHandler(ListenerExceptionHandler.Result.CONTINUE);
        new SimpleEventCaller<>(holder, continueHandler).call(event);

        Assertions.assertSame(continueHandler.event, event);
        Assertions.assertSame(continueHandler.listener, second);
        Assertions.assertSame(continueHandler.exception, listener.getOriginalException());
        Assertions.assertEquals(2, continueHandler.count);

        Assertions.assertEquals(1, counter.getAndSet(0));

        var breakHandler = new ExceptionHandler(ListenerExceptionHandler.Result.BREAK);
        new SimpleEventCaller<>(holder, breakHandler).call(event);

        Assertions.assertSame(breakHandler.event, event);
        Assertions.assertSame(breakHandler.listener, first);
        Assertions.assertSame(breakHandler.exception, listener.getOriginalException());
        Assertions.assertEquals(1, breakHandler.count);

        Assertions.assertEquals(0, counter.get());

        var rethrowHandler = new ExceptionHandler(ListenerExceptionHandler.Result.RETHROW);
        Assertions.assertThrows(RuntimeException.class, () -> new SimpleEventCaller<>(holder, rethrowHandler).call(event));

        Assertions.assertSame(rethrowHandler.event, event);
        Assertions.assertSame(rethrowHandler.listener, first);
        Assertions.assertSame(rethrowHandler.exception, listener.getOriginalException());
        Assertions.assertEquals(1, rethrowHandler.count);

        Assertions.assertEquals(0, counter.get());
    }

    private static final class ExceptionHandler implements ListenerExceptionHandler<Object, Event, Priority> {

        private final Result result;
        private Event event;
        private SubscribedListener<Object, ? extends Event, Priority> listener;
        private Throwable exception;
        private int count = 0;

        private ExceptionHandler(@NotNull Result result) {
            this.result = result;
        }

        @Override
        public @NotNull Result handleException(@NotNull Event event, @NotNull SubscribedListener<Object, ? extends Event, Priority> listener, @NotNull Throwable exception) {
            this.event = event;
            this.listener = listener;
            this.exception = exception;
            this.count++;

            return this.result;
        }
    }
}
