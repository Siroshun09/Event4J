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

package com.github.siroshun09.event4j.simple;

import com.github.siroshun09.event4j.listener.ListenerBase;
import com.github.siroshun09.event4j.priority.Priority;
import com.github.siroshun09.event4j.shared.event.SampleEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

class SimpleSubscriberTest {

    private static final Consumer<SampleEvent> CONSUMER = ignored -> {
    };

    @Test
    void testSubscribeAndUnsubscribe() {
        var subscriber = new SimpleEventSubscriber<>(SampleEvent.class, Priority.COMPARATOR, Priority.NORMAL);

        Assertions.assertEquals(List.of(), subscriber.getSubscribedListeners());

        var subscribed_1 = subscriber.subscribe("test-1", CONSUMER);

        Assertions.assertEquals("test-1", subscribed_1.key());
        Assertions.assertSame(CONSUMER, subscribed_1.consumer());
        Assertions.assertEquals(Priority.NORMAL, subscribed_1.order());
        Assertions.assertEquals(List.of(subscribed_1), subscriber.getSubscribedListeners());

        var subscribed_2 = subscriber.subscribe("test-2", CONSUMER, Priority.value(1));

        Assertions.assertEquals("test-2", subscribed_2.key());
        Assertions.assertSame(CONSUMER, subscribed_2.consumer());
        Assertions.assertEquals(Priority.value(1), subscribed_2.order());
        Assertions.assertEquals(List.of(subscribed_1, subscribed_2), subscriber.getSubscribedListeners());

        var subscribed_3 = subscriber.listenerFactory().key("test-3").consumer(CONSUMER).order(Priority.value(-1)).subscribe();

        Assertions.assertEquals("test-3", subscribed_3.key());
        Assertions.assertSame(CONSUMER, subscribed_3.consumer());
        Assertions.assertEquals(Priority.value(-1), subscribed_3.order());
        Assertions.assertEquals(List.of(subscribed_3, subscribed_1, subscribed_2), subscriber.getSubscribedListeners());

        Assertions.assertTrue(subscriber.unsubscribe(subscribed_1));
        Assertions.assertEquals(List.of(subscribed_3, subscribed_2), subscriber.getSubscribedListeners());

        Assertions.assertTrue(subscriber.unsubscribe(subscribed_2));
        Assertions.assertEquals(List.of(subscribed_3), subscriber.getSubscribedListeners());

        Assertions.assertTrue(subscriber.unsubscribe(subscribed_3));
        Assertions.assertEquals(List.of(), subscriber.getSubscribedListeners());

        Assertions.assertFalse(subscriber.unsubscribe(subscribed_1));
        Assertions.assertFalse(subscriber.unsubscribe(subscribed_2));
        Assertions.assertFalse(subscriber.unsubscribe(subscribed_3));
    }

    @Test
    void testSubscribeAndUnsubscribeAll() {
        var subscriber = new SimpleEventSubscriber<>(SampleEvent.class, Priority.COMPARATOR, Priority.NORMAL);

        var subscribedListeners = subscriber.subscribeAll(List.of(
                new ListenerBase<>(SampleEvent.class, "test-1", CONSUMER, null),
                new ListenerBase<>(SampleEvent.class, "test-2", CONSUMER, Priority.value(1)),
                new ListenerBase<>(SampleEvent.class, "test-3", CONSUMER, Priority.value(-1))
        ));

        Assertions.assertEquals(3, subscribedListeners.size());
        Assertions.assertEquals(3, subscriber.getSubscribedListeners().size());

        Assertions.assertTrue(subscriber.unsubscribeAll(subscribedListeners));
        Assertions.assertEquals(List.of(), subscriber.getSubscribedListeners());
        Assertions.assertFalse(subscriber.unsubscribeAll(subscribedListeners));
    }
}
