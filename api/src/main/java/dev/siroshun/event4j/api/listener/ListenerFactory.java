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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An interface to create/subscribe a listener.
 *
 * @param <K> the key type
 * @param <E> the event type
 * @param <O> the order type
 */
public interface ListenerFactory<K, E, O> {

    /**
     * Sets the key.
     *
     * @param key the key
     * @return this {@link ListenerFactory}
     */
    @Contract("_ -> this")
    @NotNull
    ListenerFactory<K, E, O> key(K key);

    /**
     * Sets the {@link Consumer}.
     *
     * @param consumer the {@link Consumer}
     * @return this {@link ListenerFactory}
     */
    @Contract("_ -> this")
    @NotNull
    ListenerFactory<K, E, O> consumer(Consumer<? super E> consumer);

    /**
     * Sets the order.
     *
     * @param order the order
     * @return this {@link ListenerFactory}
     */
    @Contract("_ -> this")
    @NotNull
    ListenerFactory<K, E, O> order(O order);

}
