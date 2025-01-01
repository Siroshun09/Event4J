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

package dev.siroshun.event4j.tree;

import dev.siroshun.event4j.api.listener.ListenerExceptionHandler;
import dev.siroshun.event4j.api.listener.SubscribedListener;
import dev.siroshun.event4j.api.priority.Priority;
import dev.siroshun.event4j.test.helper.event.ExtendedSampleEvent;
import dev.siroshun.event4j.test.helper.event.SampleEvent;
import dev.siroshun.event4j.test.helper.listener.ThrowingListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.siroshun.event4j.tree.TestHelper.newListener;
import static dev.siroshun.event4j.tree.TestHelper.newListenerList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListenerListTest {

    @Test
    void testGetHolder() {
        var list = newListenerList();
        var created = list.holder(SampleEvent.class);
        assertNotNull(created);
        assertSame(created, list.holder(SampleEvent.class));
        assertEquals(created, list.holders().stream().findFirst().orElseThrow());
    }

    @Test
    void testParentHolder() {
        var list = newListenerList();
        var created = list.holder(ExtendedSampleEvent.class);
        var parent = list.holder(SampleEvent.class);
        assertSame(created.parent(), parent);
        assertNull(parent.parent()); // root holder

        var holders = new ArrayList<>(list.holders());
        assertTrue(holders.remove(created));
        assertTrue(holders.remove(parent));
        assertTrue(holders.isEmpty());
    }

    @Test
    void testHolder() {
        var holder = newListenerList().holder(SampleEvent.class);
        assertTrue(holder.listeners().isEmpty());
        assertNull(holder.sortedListenersArray);

        var count = new AtomicInteger(0);
        var first = newListener(e -> count.incrementAndGet(), Priority.LOW);
        var second = newListener(e -> count.incrementAndGet(), Priority.HIGH);

        holder.modifyListeners(listeners -> {
            listeners.add(second);
            listeners.add(first);
        });
        assertEquals(List.of(first, second), holder.listeners());
        assertArrayEquals(new SubscribedListener[]{first, second}, holder.sortedListenersArray);
        assertTrue(holder.postEvent(new SampleEvent(), ListenerExceptionHandler.continueHandler()));
        assertEquals(2, count.getAndSet(0));

        holder.modifyListeners(listeners -> listeners.remove(second));
        assertEquals(List.of(first), holder.listeners());
        assertArrayEquals(new SubscribedListener[]{first}, holder.sortedListenersArray);
        assertTrue(holder.postEvent(new SampleEvent(), ListenerExceptionHandler.continueHandler()));
        assertEquals(1, count.getAndSet(0));

        holder.modifyListeners(listeners -> listeners.remove(first));
        assertTrue(holder.listeners().isEmpty());
        assertNull(holder.sortedListenersArray);
        assertTrue(holder.postEvent(new SampleEvent(), ListenerExceptionHandler.continueHandler()));
        assertEquals(0, count.getAndSet(0));
    }

    @Test
    void testHolderExceptionHandling() {
        var holder = newListenerList().holder(SampleEvent.class);
        var listener = ThrowingListener.<SampleEvent>create();
        var counter = new AtomicInteger(0);

        holder.modifyListeners(listeners -> {
            listeners.add(newListener(listener, Priority.NORMAL));
            listeners.add(newListener(e -> counter.incrementAndGet(), Priority.HIGH));
        });

        var event = new SampleEvent();

        holder.postEvent(event, (e, l, ex) -> {
            assertSame(event, e);
            assertSame(Objects.requireNonNull(holder.sortedListenersArray)[0], l);
            assertSame(listener.originalException(), ex);
            return ListenerExceptionHandler.Result.BREAK;
        });
        assertEquals(0, counter.getAndSet(0));

        assertTrue(holder.postEvent(event, ListenerExceptionHandler.continueHandler()));
        assertEquals(1, counter.getAndSet(0)); // Second listener should be called

        assertFalse(holder.postEvent(event, (e, l, ex) -> ListenerExceptionHandler.Result.BREAK));
        assertEquals(0, counter.getAndSet(0)); // Second listener should NOT be called

        try {
            holder.postEvent(event, (e, l, ex) -> ListenerExceptionHandler.Result.RETHROW);
            Assertions.fail("Did not throw exception");
        } catch (Throwable e) {
            assertSame(listener.originalException(), e);
            assertEquals(0, counter.getAndSet(0)); // Second listener should NOT be called
        }
    }
}
