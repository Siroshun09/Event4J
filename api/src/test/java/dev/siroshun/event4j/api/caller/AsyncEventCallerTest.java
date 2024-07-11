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

package dev.siroshun.event4j.api.caller;

import dev.siroshun.event4j.test.helper.event.SampleEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class AsyncEventCallerTest {

    @Test
    void testCall() throws Exception {
        var caller = new CountingEventCaller();
        caller.callAsync(new SampleEvent());
        Thread.sleep(5);
        Assertions.assertEquals(1, caller.counter.get());
    }

    @Test
    void testCallback() {
        var caller = new CountingEventCaller();

        var event = new SampleEvent();
        var future = new CompletableFuture<SampleEvent>();

        caller.callAsync(event, future::complete);

        Assertions.assertSame(event, future.join());
        Assertions.assertEquals(1, caller.counter.get());
    }

    private static final class CountingEventCaller implements EventCaller<SampleEvent> {

        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public void call(@NotNull SampleEvent event) {
            this.counter.incrementAndGet();
        }

        private void callAsync(@NotNull SampleEvent event) {
            EventCaller.asyncCaller(this, ForkJoinPool.commonPool()).call(event);
        }

        private void callAsync(@NotNull SampleEvent event, @NotNull Consumer<SampleEvent> consumer) {
            EventCaller.asyncCaller(this, ForkJoinPool.commonPool()).call(event, consumer);
        }
    }
}
