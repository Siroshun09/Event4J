/*
 *     Copyright (c) 2020 Siroshun09
 *
 *     This file is part of Event.
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
import com.github.siroshun09.event4j.handlerlist.Priority;
import com.github.siroshun09.event4j.listener.Listener;
import com.github.siroshun09.event4j.test.event.SampleEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PriorityTest {

    private final static EventBus BUS = EventBus.newEventBus();
    private final static Key KEY = Key.random();
    private final static List<Integer> RECORDER = new ArrayList<>();

    @Test
    @DisplayName("Priority test")
    void priorityTest() {
        subscribe(this::handleCustom1, () -> -256);
        subscribe(this::handleLowest, Priority.LOWEST);
        subscribe(this::handleLow, Priority.LOW);
        subscribe(this::handleHighest, Priority.HIGHEST);
        subscribe(this::handleCustom2, () -> 256);
        subscribe(this::handleNormal, Priority.NORMAL);
        subscribe(this::handleHigh, Priority.HIGH);

        BUS.callEvent(new SampleEvent());

        for (int i = 0; i < RECORDER.size(); i++) {
            Assertions.assertEquals(i, RECORDER.get(i));
        }
    }

    private void subscribe(Listener<SampleEvent> listener, Priority priority) {
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY, listener, priority);
    }

    private void handleCustom1(Event event) {
        RECORDER.add(0);
    }

    private void handleLowest(Event event) {
        RECORDER.add(1);
    }

    private void handleLow(Event event) {
        RECORDER.add(2);
    }

    private void handleNormal(Event event) {
        RECORDER.add(3);
    }

    private void handleHigh(Event event) {
        RECORDER.add(4);
    }

    private void handleHighest(Event event) {
        RECORDER.add(5);
    }

    private void handleCustom2(Event event) {
        RECORDER.add(6);
    }
}
