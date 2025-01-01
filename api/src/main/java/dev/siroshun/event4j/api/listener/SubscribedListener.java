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

package dev.siroshun.event4j.api.listener;

import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

/**
 * An interface to hold the subscribed listener.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
@NullMarked
public interface SubscribedListener<K, E, O> {

    /**
     * Gets the key of this listener.
     *
     * @return the key of this listener
     */
    K key();

    /**
     * Gets the event class of this listener.
     *
     * @return the event class of this listener
     */
    Class<E> eventClass();

    /**
     * Gets the {@link Consumer} of this listener.
     *
     * @return the {@link Consumer} of this listener
     */
    Consumer<? super E> consumer();

    /**
     * Gets the order of this listener.
     *
     * @return the order of this listener
     */
    @UnknownNullability O order();

}
