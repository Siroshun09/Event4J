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

package com.github.siroshun09.event4j.handlerlist;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An interface to set the listener's priority.
 */
@FunctionalInterface
public interface Priority extends Comparable<Priority> {

    /**
     * Priority: -128
     */
    Priority LOWEST = of(-128);

    /**
     * Priority: -64
     */
    Priority LOW = of(-64);

    /**
     * Priority: 0
     */
    Priority NORMAL = of(0);

    /**
     * Priority: 64
     */
    Priority HIGH = of(64);

    /**
     * Priority: 128
     */
    Priority HIGHEST = of(128);

    /**
     * Creates a new priority.
     *
     * @param value the priority value
     * @return the priority
     */
    static @NotNull Priority of(int value) {
        return new PriorityImpl(value);
    }

    /**
     * Gets the priority value.
     *
     * @return the priority value
     */
    int getPriority();

    @Override
    default int compareTo(@NotNull Priority other) {
        Objects.requireNonNull(other);
        return Integer.compare(this.getPriority(), other.getPriority());
    }
}
