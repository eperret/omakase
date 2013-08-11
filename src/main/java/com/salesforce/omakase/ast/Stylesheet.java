﻿/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * The root-level {@link Syntax} object.
 * 
 * @author nmcwilliams
 */
@Subscribable
public final class Stylesheet extends AbstractSyntax {
    private final Statement head;

    /**
     * TODO
     * 
     * @param head
     *            TODO
     */
    public Stylesheet(Statement head) {
        super(head.line(), head.column());
        this.head = head;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public LinkableCollection<Statement> statements() {
        return LinkableCollection.of(head);
    }

    /**
     * TODO Description
     * 
     * <p>
     * Avoid if possible, as this method is less efficient. Prefer instead to append the rule or at-rule directly to a
     * specific instance of an existing one.
     * 
     * @param statement
     *            TODO
     * @return this, for chaining.
     */
    public Stylesheet append(Statement statement) {
        Iterables.getLast(statements()).append(statement);
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("statements", head != null ? Joiner.on("\n\n").join(statements()) : null)
            .toString();
    }
}