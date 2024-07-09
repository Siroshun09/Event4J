/*
 *     Copyright (c) 2020-2024 Siroshun09
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

package dev.siroshun.event4j.api.priority;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * An interface to set the listener's priority.
 * <p>
 * {@link Priority} is used to determine the order of listener calls.
 * <p>
 * {@link Priority} is compared by {@link Integer#compare(int, int)}.
 * The smaller value, the listener is called earlier; the larger value, it is called later.
 * For the same value, it is not defined which is called first.
 */
public record Priority(int value) implements Comparable<Priority> {

    /**
     * A {@link Comparator} to compare two {@link Priority}.
     */
    public static final Comparator<Priority> COMPARATOR = Comparator.naturalOrder();

    /**
     * Priority value: -64
     */
    public static final Priority LOW = new Priority(-128);

    /**
     * Priority value: 0
     */
    public static final Priority NORMAL = new Priority(0);

    /**
     * Priority value: 64
     */
    public static final Priority HIGH = new Priority(128);

    /**
     * Creates a new priority.
     *
     * @param value the priority value
     * @return the priority
     */
    public static @NotNull Priority value(int value) {
        return switch (value) {
            case -128 -> LOW;
            case 0 -> NORMAL;
            case 128 -> HIGH;
            default -> new Priority(value);
        };
    }

    @Override
    public int compareTo(@NotNull Priority o) {
        return Integer.compare(this.value, o.value);
    }
}
