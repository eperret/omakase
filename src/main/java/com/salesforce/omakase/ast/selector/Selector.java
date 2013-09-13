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

package com.salesforce.omakase.ast.selector;

import com.google.common.collect.Lists;
import com.salesforce.omakase.As;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.OrphanedComment;
import com.salesforce.omakase.ast.RawSyntax;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.collection.AbstractGroupable;
import com.salesforce.omakase.ast.collection.StandardSyntaxCollection;
import com.salesforce.omakase.ast.collection.SyntaxCollection;
import com.salesforce.omakase.broadcaster.Broadcaster;
import com.salesforce.omakase.broadcaster.QueryableBroadcaster;
import com.salesforce.omakase.emitter.Description;
import com.salesforce.omakase.emitter.Subscribable;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.ParserFactory;
import com.salesforce.omakase.parser.Stream;
import com.salesforce.omakase.parser.selector.ComplexSelectorParser;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;

import java.io.IOException;
import java.util.List;

import static com.salesforce.omakase.emitter.SubscribableRequirement.AUTOMATIC;

/**
 * TESTME
 * <p/>
 * Represents a CSS selector.
 * <p/>
 * {@link Selector}s are lists of {@link SelectorPart}s. Individual {@link Selector}s are separated by commas. For example, in
 * <code>.class, .class #id</code> there are two selectors, <code>.class</code> and <code>.class #id</code>.
 * <p/>
 * It's important to note that the raw members may contain grammatically incorrect CSS. Refining the object will perform basic
 * grammar validation. See the notes on {@link Refinable}.
 *
 * @author nmcwilliams
 * @see ComplexSelectorParser
 */
@Subscribable
@Description(broadcasted = AUTOMATIC)
public class Selector extends AbstractGroupable<Selector> implements Refinable<Selector> {
    private final SyntaxCollection<SelectorPart> parts;
    private final RawSyntax rawContent;

    /**
     * Creates a new instance of a {@link Selector} with the given raw content. This selector can be further refined to the
     * individual {@link SelectorPart}s by using {@link #refine()}.
     *
     * @param rawContent
     *     The selector content.
     * @param broadcaster
     *     The {@link Broadcaster} to use when {@link #refine()} is called.
     */
    public Selector(RawSyntax rawContent, Broadcaster broadcaster) {
        super(rawContent.line(), rawContent.column(), broadcaster);
        this.rawContent = rawContent;
        this.parts = StandardSyntaxCollection.create(broadcaster);
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param parts
     *     The parts within the selector.
     */
    public Selector(SelectorPart... parts) {
        this(Lists.newArrayList(parts));
    }

    /**
     * Creates a new instance with no line or number specified (used for dynamically created {@link Syntax} units).
     *
     * @param parts
     *     The parts within the selector.
     */
    public Selector(Iterable<SelectorPart> parts) {
        this.rawContent = null;
        this.parts = StandardSyntaxCollection.create();
        this.parts.appendAll(parts);
    }

    /**
     * Gets the original, raw, non-validated selector content.
     *
     * @return The raw selector content.
     */
    public RawSyntax rawContent() {
        return rawContent;
    }

    /**
     * Gets the individual parts of the selector.
     *
     * @return The list of {@link SelectorPart} members.
     */
    public SyntaxCollection<SelectorPart> parts() {
        return parts;
    }

    @Override
    public boolean isRefined() {
        return !parts.isEmpty();
    }

    @Override
    public Selector refine() {
        if (!isRefined()) {
            QueryableBroadcaster qb = new QueryableBroadcaster(broadcaster());
            Stream stream = new Stream(rawContent, false);

            // parse the contents
            ParserFactory.complexSelectorParser().parse(stream, qb);

            // there should be nothing left
            if (!stream.eof()) throw new ParserException(stream, Message.UNPARSABLE_SELECTOR);

            // store the parsed selector parts
            parts.appendAll(qb.filter(SelectorPart.class));

            // check for orphaned comments
            Iterable<OrphanedComment> orphaned = qb.filter(OrphanedComment.class);
        }

        return this;
    }

    @Override
    public Syntax broadcaster(Broadcaster broadcaster) {
        parts.broadcaster(broadcaster);
        return super.broadcaster(broadcaster);
    }

    @Override
    public void propagateBroadcast(Broadcaster broadcaster) {
        super.propagateBroadcast(broadcaster);
        parts.propagateBroadcast(broadcaster);
    }

    @Override
    protected Selector self() {
        return this;
    }

    @Override
    public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        if (isDetached()) return;

        if (isRefined()) {
            for (SelectorPart part : parts) {
                writer.write(part, appendable);
            }
        } else {
            writer.write(rawContent, appendable);
        }
    }

    public List<OrphanedComment> orphanedComments() {
        return null;
    }

    @Override
    public String toString() {
        return As.string(this)
            .indent()
            .add("position", super.toString())
            .add("raw", rawContent)
            .add("parts", parts())
            .toString();
    }
}
