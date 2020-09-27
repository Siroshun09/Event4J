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
import com.github.siroshun09.event4j.test.event.SampleEvent2;
import com.github.siroshun09.event4j.test.event.SampleEvent3;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ListenerUnsubscribeTest {

    private final static EventBus BUS = EventBus.newEventBus();

    private final static Key KEY_1 = Key.of("Key-1");
    private final static Key KEY_2 = Key.of("Key-2");

    private final static Listener<SampleEvent2> SAMPLE_LISTENER_2 = e -> {
    };
    private final static Listener<SampleEvent3> SAMPLE_LISTENER_3 = e -> {
    };

    @Test
    @DisplayName("HandlerList#unsubscribe(Listener) Test")
    void unsubscribeTest() {
        clear();

        BUS.getHandlerList(SampleEvent2.class).subscribe(KEY_1, this::dummyListener);
        BUS.getHandlerList(SampleEvent2.class).subscribe(KEY_1, SAMPLE_LISTENER_2);
        BUS.getHandlerList(SampleEvent2.class).subscribe(KEY_2, SAMPLE_LISTENER_2);

        Assertions.assertTrue(BUS.getHandlerList(SampleEvent2.class).unsubscribe(SAMPLE_LISTENER_2));
        Assertions.assertEquals(1, BUS.getHandlerList(SampleEvent2.class).getSize());
    }

    @Test
    @DisplayName("HandlerList#unsubscribe(Listener, Priority) Test")
    void unsubscribeWithPriorityTest() {
        clear();

        BUS.getHandlerList(SampleEvent3.class).subscribe(KEY_1, this::dummyListener);
        BUS.getHandlerList(SampleEvent3.class).subscribe(KEY_1, SAMPLE_LISTENER_3);
        BUS.getHandlerList(SampleEvent3.class).subscribe(KEY_1, SAMPLE_LISTENER_3, Priority.LOWEST);
        BUS.getHandlerList(SampleEvent3.class).subscribe(KEY_1, SAMPLE_LISTENER_3, Priority.HIGHEST);

        Assertions.assertTrue(BUS.getHandlerList(SampleEvent3.class).unsubscribe(SAMPLE_LISTENER_3, Priority.NORMAL));
        Assertions.assertTrue(BUS.getHandlerList(SampleEvent3.class).unsubscribe(SAMPLE_LISTENER_3, Priority.LOWEST));
        Assertions.assertFalse(BUS.getHandlerList(SampleEvent3.class).unsubscribe(SAMPLE_LISTENER_3, Priority.LOW));
        Assertions.assertEquals(2, BUS.getHandlerList(SampleEvent3.class).getSize());
    }

    @Test
    @DisplayName("HandlerList#unsubscribeAll(Key) Test")
    void unsubscribeAllTest() {
        clear();

        BUS.getHandlerList(SampleEvent.class).subscribe(KEY_1, this::dummyListener);
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY_1, this::dummyListener);
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY_2, this::dummyListener);

        BUS.getHandlerList(SampleEvent.class).unsubscribeAll(KEY_1);
        Assertions.assertEquals(1, BUS.getHandlerList(SampleEvent.class).getSize());
    }

    @Test
    @DisplayName("SimpleEventBus#unsubscribeAll(Key) Test")
    void unsubscribeAllFromBusTest() {
        clear();

        BUS.getHandlerList(SampleEvent.class).subscribe(KEY_1, this::dummyListener);
        BUS.getHandlerList(SampleEvent.class).subscribe(KEY_2, this::dummyListener);
        BUS.getHandlerList(SampleEvent2.class).subscribe(KEY_1, this::dummyListener);
        BUS.getHandlerList(SampleEvent3.class).subscribe(KEY_1, this::dummyListener);

        BUS.unsubscribeAll(KEY_1);

        Assertions.assertEquals(1, BUS.getHandlerList(SampleEvent.class).getSize());
        Assertions.assertEquals(0, BUS.getHandlerList(SampleEvent2.class).getSize());
        Assertions.assertEquals(0, BUS.getHandlerList(SampleEvent3.class).getSize());

        BUS.unsubscribeAll(KEY_2);
        Assertions.assertEquals(0, BUS.getHandlerList(SampleEvent.class).getSize());
    }

    private void dummyListener(Event event) {
    }

    private void clear() {
        BUS.unsubscribeAll(KEY_1);
        BUS.unsubscribeAll(KEY_2);
    }
}
