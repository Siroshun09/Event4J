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

package com.github.siroshun09.event4j.caller;

import com.github.siroshun09.event4j.shared.event.Event;
import com.github.siroshun09.event4j.shared.event.SampleEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

class AsyncEventCallerTest {

    @Test
    void testCall() {
        var caller = new CountingEventCaller();
        caller.asAsync().call(new SampleEvent());
        Assertions.assertEquals(1, caller.counter.get());
    }

    @Test
    void testCallAsync() throws Exception {
        var caller = new CountingEventCaller();
        caller.asAsync().callAsync(new SampleEvent());
        Thread.sleep(10);
        Assertions.assertEquals(1, caller.counter.get());
    }

    @Test
    void testCallAsyncAndCallback() {
        var caller = new CountingEventCaller();

        var event = new SampleEvent();
        var future = new CompletableFuture<SampleEvent>();

        caller.asAsync().callAsync(event, future::complete);

        Assertions.assertSame(event, future.join());
        Assertions.assertEquals(1, caller.counter.get());
    }

    private static final class CountingEventCaller implements EventCaller<Event> {

        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public <T extends Event> void call(@NotNull T event) {
            this.counter.incrementAndGet();
        }

        private @NotNull AsyncEventCaller<Event> asAsync() {
            return AsyncEventCaller.create(this, ForkJoinPool.commonPool());
        }
    }
}
