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

package com.github.siroshun09.event4j.test.sample.listener;

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.listener.MultipleListeners;
import com.github.siroshun09.event4j.listener.Subscribe;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent;
import com.github.siroshun09.event4j.test.sample.event.SampleEvent2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DummyMultipleListeners implements MultipleListeners {

    @Contract(value = " -> new", pure = true)
    public static @NotNull DummyMultipleListeners create() {
        return new DummyMultipleListeners();
    }

    private DummyMultipleListeners() {
    }

    @Subscribe
    public void onEvent(@NotNull Event event) {
    }

    @Subscribe
    public void onSampleEvent(@NotNull SampleEvent event) {
    }

    @Subscribe
    public void onSampleEvent2(@NotNull SampleEvent2 event2) {
    }
}
