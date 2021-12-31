/*
 *     Copyright (c) 2020-2022 Siroshun09
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

package com.github.siroshun09.event4j.test.bus;

import com.github.siroshun09.event4j.bus.PostResult;
import com.github.siroshun09.event4j.bus.SubscribedListener;
import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.key.Key;
import com.github.siroshun09.event4j.priority.Priority;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent;
import com.github.siroshun09.event4j.test.sample.listener.DummyListener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

public class PostResultTest {

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    @Test
    void testIllegalArguments() {
        Assertions.assertThrows(NullPointerException.class, () -> PostResult.success(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PostResult.failure(null, Collections.emptyMap()));
        Assertions.assertThrows(NullPointerException.class, () -> PostResult.failure(new SampleEvent(), null));
        Assertions.assertThrows(NullPointerException.class, () -> PostResult.failure(null, createDummyExceptionMap(Event.class)));
    }

    @Test
    void testState() {
        var success = PostResult.success(new SampleEvent());
        Assertions.assertTrue(success.isSuccess());
        Assertions.assertFalse(success.isFailure());

        var failure = PostResult.failure(new SampleEvent(), createDummyExceptionMap(SampleEvent.class));
        Assertions.assertFalse(failure.isSuccess());
        Assertions.assertTrue(failure.isFailure());
    }

    private <E> @NotNull Map<SubscribedListener<E>, Throwable> createDummyExceptionMap(@NotNull Class<E> eventClass) {
        return Map.of(new SubscribedListener<>(eventClass, Key.random(), DummyListener.create(), Priority.HIGH), new Throwable());
    }
}
