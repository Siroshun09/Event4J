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

package com.github.siroshun09.event4j.test.priority;

import com.github.siroshun09.event4j.priority.Priority;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PriorityTest {

    @Test
    void testDefaultPriorities() {
        Assertions.assertEquals(-128, Priority.LOWEST.getPriority());
        Assertions.assertEquals(-64, Priority.LOW.getPriority());
        Assertions.assertEquals(0, Priority.NORMAL.getPriority());
        Assertions.assertEquals(64, Priority.HIGH.getPriority());
        Assertions.assertEquals(128, Priority.HIGHEST.getPriority());
    }

    @Test
    void testComparing() {
        var expected = List.of(
                Priority.value(-300),
                Priority.LOWEST, Priority.LOW,
                Priority.NORMAL,
                Priority.HIGH, Priority.HIGHEST,
                Priority.value(300)
        );

        var copied = new ArrayList<>(expected);
        Collections.shuffle(copied);

        Assertions.assertEquals(expected, copied.stream().sorted().collect(Collectors.toList()));
    }
}
