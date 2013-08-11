﻿/**
 * ADD LICENSE
 */
package com.salesforce.omakase.ast;

import com.google.common.base.Objects;
import com.salesforce.omakase.LinkableCollection;
import com.salesforce.omakase.emitter.Subscribable;

/**
 * TODO Description
 * 
 * @author nmcwilliams
 */
@Subscribable
public class Selector extends AbstractLinkableSyntax<Selector> implements Refinable<RefinedSelector>, RefinedSelector {
    private final RawSyntax rawContent;
    private SelectorPart head;

    /**
     * @param rawContent
     *            TODO
     */
    public Selector(RawSyntax rawContent) {
        super(rawContent.line(), rawContent.column());
        this.rawContent = rawContent;
    }

    /**
     * TODO Description
     * 
     * @return TODO
     */
    public RawSyntax rawContent() {
        return rawContent;
    }

    @Override
    public LinkableCollection<SelectorPart> parts() {
        return LinkableCollection.of(head);
    }

    @Override
    public RefinedSelector refine() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Selector self() {
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("line", line())
            .add("column", column())
            .add("rawContent", rawContent)
            .add("selectorParts", head != null ? parts() : null)
            .toString();
    }
}
