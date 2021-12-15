/*
 *     Copyright (c) 2020-2021 Siroshun09
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

package com.github.siroshun09.event4j.listener;

/**
 * An interface to indicate that the class has multiple {@link Listener}s.
 * <p>
 * Example:
 *
 * <pre>{@code
 *     public class SampleListeners implements MultipleListeners {
 *         &#064;Subscribe
 *         public void onEventNormal(@NotNull Event event) {
 *             // do something
 *         }
 *
 *         &#064;Subscribe(priority = Priority.HIGH_VALUE)
 *         public void onEventHigh(@NotNull Event event) {
 *             // do something
 *         }
 *     }
 * }</pre>
 * <p>
 * The requirements for methods to be subscribed
 * by {@link com.github.siroshun09.event4j.bus.EventBus#subscribeAll} are the following:
 * <ol>
 * <li>The method is annotated with the {@link Subscribe} annotation.</li>
 * <li>Not an inherited method from parent classes or interfaces.</li>
 * <li>The number of arguments is 1, and the type of the argument inherits
 * from event class of the {@link com.github.siroshun09.event4j.bus.EventBus} to subscribed.</li>
 * </ol>
 */
public interface MultipleListeners {
}
