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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PriorityTest {

    @ParameterizedTest
    @MethodSource("testCasesForCompare")
    void testCompare(int priority1, int priority2, int expected) {
        Assertions.assertEquals(expected, Priority.value(priority1).compareTo(Priority.value(priority2)));
    }

    private static Stream<Arguments> testCasesForCompare() {
        return Stream.of(
            Arguments.arguments(0, 1, -1),
            Arguments.arguments(0, 0, 0),
            Arguments.arguments(1, 0, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testCasesForComparator")
    void testComparator(List<Priority> priorities) {
        var shuffled = new ArrayList<>(priorities);
        Collections.shuffle(shuffled);
        shuffled.sort(Priority.COMPARATOR);
        Assertions.assertEquals(priorities, shuffled);
    }

    private static Stream<List<Priority>> testCasesForComparator() {
        return Stream.of(
                IntStream.of(-1, 0, 1),
                Stream.of(Priority.LOW, Priority.NORMAL, Priority.HIGH).mapToInt(Priority::value)
            )
            .map(stream -> stream.mapToObj(Priority::value))
            .map(Stream::toList);
    }
}
