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

import dev.siroshun.event4j.api.priority.Priority;
import dev.siroshun.event4j.test.helper.event.SampleEvent;
import org.jetbrains.annotations.NotNullByDefault;
import org.junit.jupiter.api.Test;

import static dev.siroshun.event4j.tree.TestHelper.emptyConsumer;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NotNullByDefault
class ListenerFactoryTest {

    @Test
    void testBuild() {
        var eventClass = SampleEvent.class;
        var key = "test";
        var consumer = emptyConsumer();
        var order = Priority.HIGH;

        var factory = newFactory(eventClass);
        factory.key(key).consumer(consumer).order(order);
        var listener = factory.build();

        assertSame(eventClass, listener.eventClass());
        assertSame(key, listener.key());
        assertSame(consumer, listener.consumer());
        assertSame(order, listener.order());
    }

    @Test
    void testBuildWithoutOrder() {
        var eventClass = SampleEvent.class;
        var key = "test";
        var consumer = emptyConsumer();

        var factory = newFactory(eventClass);
        factory.key(key).consumer(consumer);
        var listener = factory.build();

        assertSame(eventClass, listener.eventClass());
        assertSame(key, listener.key());
        assertSame(consumer, listener.consumer());
        assertSame(Priority.NORMAL, listener.order());
    }

    private static <E> ListenerFactoryImpl<String, E, Priority> newFactory(Class<E> eventClass) {
        return new ListenerFactoryImpl<>(eventClass, Priority.NORMAL);
    }

    @Test
    void testNotSet() {
        var factory = newFactory(SampleEvent.class);

        var e1 = assertThrows(NullPointerException.class, factory::build);
        assertEquals("key is not set.", e1.getMessage());

        factory.key("test");
        var e2 = assertThrows(NullPointerException.class, factory::build);
        assertEquals("consumer is not set.", e2.getMessage());

        factory.consumer(e -> {
        });
        assertDoesNotThrow(factory::build); // Can build after setting key and consumer
    }
}
