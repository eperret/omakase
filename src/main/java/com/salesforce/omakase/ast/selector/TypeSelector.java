/*
 * Copyright (c) 2015, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.omakase.ast.selector;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.salesforce.omakase.broadcast.BroadcastRequirement.REFINED_SELECTOR;

/**
 * Represents a CSS type selector (also known as an element type selector).
 * <p>
 * Do not use this for universal "*" selectors, but use {@link UniversalSelector} instead.
 *
 * @author nmcwilliams
 */
@Subscribable
@Description(value = "type/element selector segment", broadcasted = REFINED_SELECTOR)
public final class TypeSelector extends AbstractSelectorPart implements SimpleSelector {
    private String name;

    /**
     * Constructs a new {@link TypeSelector} instance with the given name.
     * <p>
     * If dynamically creating a new instance then use {@link #TypeSelector(String)} instead.
     *
     * @param line
     *     The line number.
     * @param column
     *     The column number.
     * @param name
     *     Name of the element / type.
     */
    public TypeSelector(int line, int column, String name) {
        super(line, column);
        this.name = name.toLowerCase();
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param name
     *     Name of the element / type.
     */
    public TypeSelector(String name) {
        name(name);
    }

    /**
     * Sets the name.
     *
     * @param name
     *     The element name.
     *
     * @return this, for chaining.
     */
    public TypeSelector name(String name) {
        checkNotNull(name, "name cannot be null");
        this.name = name.toLowerCase();
        return this;
    }

    /**
     * Gets the name of the selector.
     *
     * @return The name.
     */
    public String name() {
        return name;
    }

    @Override
    public SelectorPartType type() {
        return SelectorPartType.TYPE_SELECTOR;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        appendable.append(name);
    }

    @Override
    public TypeSelector copy() {
        return new TypeSelector(name).copiedFrom(this);
    }
}
