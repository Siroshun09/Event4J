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
import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.handlerlist.Key;
import com.github.siroshun09.event4j.test.event.SampleEvent;
import com.github.siroshun09.event4j.test.event.SampleEvent2;
import com.github.siroshun09.event4j.test.event.SampleEvent3;
import com.github.siroshun09.event4j.test.event.TestFinishedEvent;
import com.github.siroshun09.event4j.test.event.TestStartedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EventPostingTest {

    private static final EventBus BUS = EventBus.newEventBus();
    private static final Key KEY = Key.random();

    private int receivedCount = 0;

    @Test
    @DisplayName("Event posting test")
    void postEventTest() {
        subscribeListeners();

        BUS.callEvent(new TestStartedEvent());

        for (int i = 0; i < 5; i++) {
            BUS.callEvent(new SampleEvent());
        }

        BUS.callEvent(new SampleEvent3());

        BUS.callEvent(new TestFinishedEvent());

        Assertions.assertEquals(17, receivedCount);
    }

    void subscribeListeners() {
        BUS.getHandlerList(Event.class).subscribe(KEY, this::received);
        BUS.getHandlerList(TestStartedEvent.class).subscribe(KEY, this::received);
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY, this::received);
        BUS.getHandlerList(SampleEvent2.class).subscribe(KEY, this::received);
        BUS.getHandlerList(TestFinishedEvent.class).subscribe(KEY, this::received);
    }

    private void received(Event event) {
        receivedCount++;
    }
}
