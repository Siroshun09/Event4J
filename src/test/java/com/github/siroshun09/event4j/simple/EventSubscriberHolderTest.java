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

import com.github.siroshun09.event4j.priority.Priority;
import com.github.siroshun09.event4j.shared.event.Event;
import com.github.siroshun09.event4j.shared.event.SampleEvent;
import com.github.siroshun09.event4j.shared.event.SampleEvent2;
import com.github.siroshun09.event4j.shared.event.SampleEvent3;
import com.github.siroshun09.event4j.subscriber.EventSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

class EventSubscriberHolderTest {

    @Test
    void testGetSubscriber() {
        var holder = new EventSubscriberHolder<>(Event.class, Priority.COMPARATOR, Priority.NORMAL);
        var created = holder.subscriber(SampleEvent.class);

        Assertions.assertSame(created, holder.subscriber(SampleEvent.class));
    }

    @Test
    void testSubscriberCreation() {
        var holder = new EventSubscriberHolder<>(Event.class, Priority.COMPARATOR, Priority.NORMAL);
        holder.subscriber(SampleEvent3.class);
        var expectedClasses = new HashSet<>(Arrays.asList(Event.class, SampleEvent.class, SampleEvent2.class, SampleEvent3.class));

        Assertions.assertTrue(holder.subscribers().stream().map(EventSubscriber::eventClass).allMatch(expectedClasses::remove));
        Assertions.assertTrue(expectedClasses.isEmpty());
    }
}
