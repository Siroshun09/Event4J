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

package com.github.siroshun09.event4j.priority;

import org.jetbrains.annotations.NotNull;

/**
 * An interface to set the listener's priority.
 * <p>
 * {@link Priority} is used to determine the order of listener calls.
 * <p>
 * {@link Priority} is compared by {@link Integer#compare(int, int)}.
 * The smaller value, the {@link com.github.siroshun09.event4j.listener.Listener} is called earlier;
 * the larger value, it is called later.
 * For the same value, it is not defined which is called first.
 */
public interface Priority extends Comparable<Priority> {

    /**
     * the value of {@link #LOWEST}.
     * <p>
     * This is <b>NOT</b> the minimum numeric value that {@link Priority#value(int)} will accept.
     */
    int LOWEST_VALUE = -128;

    /**
     * the value of {@link #LOW}.
     */
    int LOW_VALUE = -64;

    /**
     * the value of {@link #NORMAL}.
     */
    int NORMAL_VALUE = 0;

    /**
     * the value of {@link #HIGH}.
     */
    int HIGH_VALUE = 64;

    /**
     * the value of {@link #HIGHEST}.
     * <p>
     * This is <b>NOT</b> the maximum numeric value that {@link Priority#value(int)} will accept.
     */
    int HIGHEST_VALUE = 128;

    /**
     * Priority value: -128
     */
    Priority LOWEST = new PriorityImpl(LOWEST_VALUE);

    /**
     * Priority value: -64
     */
    Priority LOW = new PriorityImpl(LOW_VALUE);

    /**
     * Priority value: 0
     */
    Priority NORMAL = new PriorityImpl(NORMAL_VALUE);

    /**
     * Priority value: 64
     */
    Priority HIGH = new PriorityImpl(Priority.HIGH_VALUE);

    /**
     * Priority value: 128
     */
    Priority HIGHEST = new PriorityImpl(Priority.HIGHEST_VALUE);

    /**
     * Creates a new priority.
     *
     * @param value the priority value
     * @return the priority
     */
    static @NotNull Priority value(int value) {
        return switch (value) {
            case LOWEST_VALUE -> LOWEST;
            case LOW_VALUE -> LOW;
            case NORMAL_VALUE -> NORMAL;
            case HIGH_VALUE -> HIGH;
            case HIGHEST_VALUE -> HIGHEST;
            default -> new PriorityImpl(value);
        };
    }

    /**
     * Gets the priority value.
     *
     * @return the priority value
     */
    int getPriority();
}
