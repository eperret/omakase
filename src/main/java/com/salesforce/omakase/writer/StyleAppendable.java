/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.writer;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A wrapper around an {@link Appendable} that provides a few convenience functions.
 * <p/>
 * When not specifying a particular {@link Appendable} then use {@link #toString()} to get the final output.
 *
 * @author nmcwilliams
 */
public final class StyleAppendable {
    private final Appendable appendable;

    /** Creates a new {@link StyleAppendable} using a {@link StringBuilder}. Use {@link #toString()} to get the final output. */
    public StyleAppendable() {
        this(new StringBuilder(256));
    }

    /**
     * Creates a new {@link StyleAppendable} using the given {@link Appendable}.
     *
     * @param appendable
     *     Write to this {@link Appendable}.
     */
    public StyleAppendable(Appendable appendable) {
        this.appendable = checkNotNull(appendable, "appendable cannot be null");
    }

    /**
     * Appends the specified character. Prefer this over {@link #append(CharSequence)}.
     *
     * @param c
     *     The character to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(char c) throws IOException {
        appendable.append(c);
        return this;
    }

    /**
     * Appends the specified integer.
     *
     * @param i
     *     The integer to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(int i) throws IOException {
        return append(Integer.toString(i));
    }

    /**
     * Appends the specified double.
     *
     * @param d
     *     The double to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(double d) throws IOException {
        return append(Double.toString(d));
    }

    /**
     * Appends the specified long.
     *
     * @param l
     *     The long to write.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(long l) throws IOException {
        return append(Long.toString(l));
    }

    /**
     * Appends the specified {@link CharSequence} or String.
     *
     * @param sequence
     *     The character sequence to append.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable append(CharSequence sequence) throws IOException {
        appendable.append(sequence);
        return this;
    }

    /**
     * Appends a newline character.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable newline() throws IOException {
        return append('\n');
    }

    /**
     * Appends a newline character only if the given condition is true.
     *
     * @param condition
     *     Only append a newline if this condition is true.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable newlineIf(boolean condition) throws IOException {
        if (condition) newline();
        return this;
    }

    /**
     * Appends a single space character.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable space() throws IOException {
        return append(' ');
    }

    /**
     * Appends a single space character only if the given condition is true.
     *
     * @param condition
     *     Only append a newline if this condition is true.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable spaceIf(boolean condition) throws IOException {
        if (condition) space();
        return this;
    }

    /**
     * Appends spaces for indentation.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable indent() throws IOException {
        return append(' ').append(' ');
    }

    /**
     * Appends spaces for indentation only if the given condition is true.
     *
     * @param condition
     *     Only append a newline if this condition is true.
     *
     * @return this, for chaining.
     *
     * @throws IOException
     *     If an I/O error occurs.
     */
    public StyleAppendable indentIf(boolean condition) throws IOException {
        if (condition) indent();
        return this;
    }

    @Override
    public String toString() {
        return appendable.toString();
    }
}
