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

package com.github.siroshun09.event4j.test.sample.listener;

import com.github.siroshun09.event4j.event.Event;
import com.github.siroshun09.event4j.listener.MultipleListeners;
import com.github.siroshun09.event4j.listener.Subscribe;
import com.github.siroshun09.event4j.priority.Priority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class PrioritizedListeners implements MultipleListeners {

    @Contract(value = " -> new", pure = true)
    public static @NotNull PrioritizedListeners create() {
        return new PrioritizedListeners();
    }

    private final int[] result = new int[5];
    private final AtomicInteger counter = new AtomicInteger();

    private PrioritizedListeners() {
    }

    @Subscribe(priority = Priority.LOWEST_VALUE)
    public void onEventLowest(@NotNull Event event) {
        result[counter.getAndIncrement()] = 1;
    }

    @Subscribe(priority = Priority.LOW_VALUE)
    public void onEventLow(@NotNull Event event) {
        result[counter.getAndIncrement()] = 2;
    }

    @Subscribe
    public void onEventNormal(@NotNull Event event) {
        result[counter.getAndIncrement()] = 3;
    }

    @Subscribe(priority = Priority.HIGH_VALUE)
    public void onEventHigh(@NotNull Event event) {
        result[counter.getAndIncrement()] = 4;
    }

    @Subscribe(priority = Priority.HIGHEST_VALUE)
    public void onEventHighest(@NotNull Event event) {
        result[counter.getAndIncrement()] = 5;
    }

    public int[] getResult() {
        return result;
    }
}
