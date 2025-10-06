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

package dev.siroshun.event4j.api.caller;

import dev.siroshun.event4j.test.helper.event.SampleEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

class AsyncEventCallerTest {

    @Test
    void testCall() {
        SampleEvent originalEvent = new SampleEvent();

        callAsync(
            caller -> caller.call(originalEvent),
            event -> Assertions.assertSame(originalEvent, event)
        );
    }

    @Test
    void testCallback() {
        SampleEvent originalEvent = new SampleEvent();
        AtomicReference<SampleEvent> callbackResult = new AtomicReference<>();

        callAsync(
            caller -> caller.call(originalEvent, callbackResult::set),
            event -> Assertions.assertSame(originalEvent, event)
        );

        Assertions.assertSame(originalEvent, callbackResult.get());
    }

    private static void callAsync(Consumer<EventCaller<SampleEvent>> call, Consumer<SampleEvent> calledEventConsumer) {
        AtomicInteger counter = new AtomicInteger(0);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            call.accept(EventCaller.asyncCaller(
                event -> {
                    calledEventConsumer.accept(event);
                    counter.incrementAndGet();
                },
                r -> executor.execute(() -> {
                    r.run();
                    future.complete(true);
                })
            ));
        }

        Assertions.assertTrue(() -> {
            try {
                return future.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Assertions.fail(e);
                return false;
            }
        });
        Assertions.assertEquals(1, counter.get());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArgument() {
        AtomicInteger counter = new AtomicInteger(0);
        EventCaller<SampleEvent> caller = e -> counter.incrementAndGet();
        var async = EventCaller.asyncCaller(caller, run -> {
            throw new IllegalStateException("Unexpected executor call");
        });

        Assertions.assertThrows(NullPointerException.class, () -> EventCaller.asyncCaller(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> EventCaller.asyncCaller(caller, null));
        Assertions.assertThrows(NullPointerException.class, () -> async.call(null));
        Assertions.assertThrows(NullPointerException.class, () -> async.call(null, event -> {
        }));
        Assertions.assertThrows(NullPointerException.class, () -> async.call(new SampleEvent(), null));

        Assertions.assertEquals(0, counter.get());
    }
}
