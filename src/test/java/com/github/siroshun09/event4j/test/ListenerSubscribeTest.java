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
import com.github.siroshun09.event4j.listener.Listener;
import com.github.siroshun09.event4j.test.event.SampleEvent;
import com.github.siroshun09.event4j.test.event.SampleEvent2;
import com.github.siroshun09.event4j.test.event.SampleEvent3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListenerSubscribeTest {

    private final static EventBus BUS = EventBus.newEventBus();

    private final static Key KEY_1 = Key.random();
    private final static Key KEY_2 = Key.random();

    @Test
    @DisplayName("Listener subscription test")
    void subscribeListenerTest() {
        assertTrue(subscribeWithKey1(SampleEvent.class));
        assertTrue(subscribeWithKey1(SampleEvent2.class));
        assertTrue(subscribeWithKey1(SampleEvent3.class));

        Listener<SampleEvent> example = this::dummyListener;

        assertTrue(subscribeWithKey1(SampleEvent.class, example));
        assertFalse(subscribeWithKey1(SampleEvent.class, example));
        assertTrue(subscribeWithKey2(SampleEvent.class, example));
        assertTrue(subscribeWithKey2(SampleEvent2.class));

        assertEquals(3, getSize(SampleEvent.class));
        assertEquals(2, getSize(SampleEvent2.class));
        assertEquals(1, getSize(SampleEvent3.class));
    }

    private boolean subscribeWithKey1(Class<? extends Event> eventClass) {
        return subscribeWithKey1(eventClass, this::dummyListener);
    }

    private <T extends Event> boolean subscribeWithKey1(Class<T> eventClass, Listener<T> listener) {
        return subscribe(KEY_1, eventClass, listener);
    }

    private boolean subscribeWithKey2(Class<? extends Event> eventClass) {
        return subscribeWithKey2(eventClass, this::dummyListener);
    }

    private <T extends Event> boolean subscribeWithKey2(Class<T> eventClass, Listener<T> listener) {
        return subscribe(KEY_2, eventClass, listener);
    }

    private <T extends Event> boolean subscribe(Key key, Class<T> eventClass, Listener<T> listener) {
        return BUS.getHandlerList(eventClass).subscribe(key, listener);
    }

    private int getSize(Class<? extends Event> eventClass) {
        return BUS.getHandlerList(eventClass).getSize();
    }

    private void dummyListener(Event event) {
    }
}
