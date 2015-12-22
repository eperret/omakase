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

package com.salesforce.omakase.parser.refiner;

import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Subscribable;
import com.salesforce.omakase.plugin.SyntaxPlugin;

/**
 * Represents a strategy for refining an {@link Selector} object. This allows you to add custom syntax with a structure similar to
 * standard selectors. This works in tandem with {@link SyntaxPlugin}s.
 *
 * @author nmcwilliams
 */
public interface SelectorRefiner extends Refiner {
    /**
     * Refines a {@link Selector}.
     * <p/>
     * The information in the given {@link Selector} can be used to determine if the selector is applicable to your custom syntax
     * (e.g., checking {@link Selector#raw()} or even {@link Selector#comments()}).
     * <p/>
     * Utilize the {@link Selector#raw()} to get the raw, unrefined syntax. Note that it's possible for this content to
     * contain comments. Parse this information into your own custom {@link SelectorPart} objects and then optionally broadcast
     * them using the given {@link Broadcaster} (if you intend to broadcast your custom AST objects they must be annotated with
     * the {@link Subscribable} annotation and implement {@link Syntax}). Be sure to actually add the objects to the {@link
     * Selector} by using the {@link Selector#appendAll(Iterable)} method.
     * <p/>
     * Do <b>not</b> use anything on {@link Selector#parts()}, as that will result in infinite recursion!
     *
     * @param selector
     *     The {@link Selector} to refine.
     * @param broadcaster
     *     Used to broadcast any {@link Broadcastable} objects.
     * @param refiner
     *     Pass this refiner to any parser methods that require one.
     *
     * @return One of the {@link Refinement} values.
     */
    Refinement refine(Selector selector, Broadcaster broadcaster, MasterRefiner refiner);
}
