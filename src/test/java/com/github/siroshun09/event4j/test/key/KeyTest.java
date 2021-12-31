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

package com.github.siroshun09.event4j.test.key;

import com.github.siroshun09.event4j.key.Key;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class KeyTest {

    @Test
    void testCreatingKey() {
        var key1 = Key.random();
        Assertions.assertDoesNotThrow(() -> UUID.fromString(key1.getName()));

        var key2 = Key.create("test");
        Assertions.assertEquals("test", key2.getName());

        var key3 = Key.create("test", "test");
        Assertions.assertEquals("test:test", key3.getName());

        var key4 = Key.create("test");
        Assertions.assertEquals(key2, key4);
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    @Test
    void testIllegalArguments() {
        Assertions.assertThrows(NullPointerException.class, () -> Key.create(null));
        Assertions.assertThrows(NullPointerException.class, () -> Key.create(null, "test"));
        Assertions.assertThrows(NullPointerException.class, () -> Key.create("test", null));

        Assertions.assertThrows(IllegalArgumentException.class, () -> Key.create(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Key.create("test", ""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Key.create("", "test"));
    }
}
