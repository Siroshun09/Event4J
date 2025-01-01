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

package dev.siroshun.event4j.api.caller;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@NullMarked
final class AsyncEventCaller<E> implements EventCaller<E> {

    private final EventCaller<E> eventCaller;
    private final Executor executor;

    AsyncEventCaller(EventCaller<E> eventCaller, Executor executor) {
        this.eventCaller = eventCaller;
        this.executor = executor;
    }

    @Override
    public void call(E event) {
        Objects.requireNonNull(event, "event cannot be null.");
        this.executor.execute(() -> this.eventCaller.call(event));
    }

    @Override
    public <T extends E> void call(T event, Consumer<? super T> callback) {
        Objects.requireNonNull(event, "event cannot be null.");
        Objects.requireNonNull(callback, "callback cannot be null.");
        this.executor.execute(() -> this.eventCaller.call(event, callback));
    }
}
