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

package com.github.siroshun09.event4j.caller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

final class DefaultAsyncEventCaller<E> implements AsyncEventCaller<E> {

    private final EventCaller<E> eventCaller;
    private final Executor executor;

    DefaultAsyncEventCaller(@NotNull EventCaller<E> eventCaller, @NotNull Executor executor) {
        this.eventCaller = eventCaller;
        this.executor = executor;
    }

    @Override
    public <T extends E> void call(@NotNull T event) {
        this.eventCaller.call(event);
    }

    @Override
    public <T extends E> void callAsync(@NotNull T event) {
        this.executor.execute(() -> this.eventCaller.call(event));
    }

    @Override
    public <T extends E> void callAsync(@NotNull T event, @Nullable Consumer<? super T> callback) {
        this.executor.execute(() -> {
            this.eventCaller.call(event);

            if (callback != null) {
                callback.accept(event);
            }
        });
    }
}
