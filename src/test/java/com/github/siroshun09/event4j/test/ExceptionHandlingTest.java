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

package com.github.siroshun09.event4j.test;

import com.github.siroshun09.event4j.bus.EventBus;
import com.github.siroshun09.event4j.handlerlist.Key;
import com.github.siroshun09.event4j.listener.Listener;
import com.github.siroshun09.event4j.test.event.SampleEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExceptionHandlingTest {

    private static final EventBus BUS = EventBus.newEventBus();
    private static final Key KEY = Key.random();

    @Test
    @DisplayName("Exceptions handling test")
    void handleExceptionTest() {
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY, new SampleListener1());
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY, new SampleListener2());

        Assertions.assertDoesNotThrow(() -> BUS.callEvent(new SampleEvent()));
    }

    private static class SampleListener1 implements Listener<SampleEvent> {

        @Override
        public void handle(@NotNull SampleEvent event) {
            throw new RuntimeException();
        }
    }

    private static class SampleListener2 implements Listener<SampleEvent> {

        @Override
        public void handle(@NotNull SampleEvent event) {
            throw new RuntimeException();
        }

        @Override
        public void handleException(@NotNull SampleEvent event, @NotNull Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }
}
