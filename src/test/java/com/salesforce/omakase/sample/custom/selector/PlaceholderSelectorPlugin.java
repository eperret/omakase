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

package com.salesforce.omakase.sample.custom.selector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.factory.TokenFactory;
import com.salesforce.omakase.plugin.GrammarPlugin;

/**
 * This is the actual plugin that gets registered with the parser.
 *
 * @author nmcwilliams
 */
public class PlaceholderSelectorPlugin implements GrammarPlugin {
    private final Map<String, PlaceholderSelector> placeholders = new HashMap<>();

    @Override
    public TokenFactory getTokenFactory() {
        return new PlaceholderTokenFactory();
    }

    @Refine
    public void refine(Selector selector, Grammar grammar, Broadcaster broadcaster) {
        String content = selector.raw().get().content();
        Source source = new Source(content);
        source.skipWhitepace();

        if (source.optionallyPresent(PlaceholderTokens.PERCENTAGE)) {
            // PLACEHOLDER `%name`

            // parse the placeholder name
            Optional<String> name = source.readIdent();
            if (!name.isPresent()) throw new ParserException(source, "Expected to find a valid placeholder selector name");

            // nothing else should be after the placeholder selector name
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

            // create and broadcast our new placeholder selector
            PlaceholderSelector placeholder = new PlaceholderSelector(name.get());
            placeholders.put(placeholder.name(), placeholder);
            broadcaster.broadcast(placeholder);
        } else if (content.contains(PlaceholderTokens.PIPE.symbol())) {
            // PLACEHOLDER REF `.selector|name`

            // parse the normal selectors, they will automatically be broadcasted
            grammar.parser().complexSelectorParser().parse(source, grammar, broadcaster);

            // parse the reference
            source.expect(PlaceholderTokens.PIPE);
            Optional<String> name = source.readIdent();
            if (!name.isPresent()) throw new ParserException(source, "Expected to find a valid placeholder selector name");

            // there should be nothing left
            if (!source.skipWhitepace().eof()) throw new ParserException(source, Message.UNPARSABLE_SELECTOR);

            PlaceholderSelector placeholder = placeholders.get(name.get());
            if (placeholder == null) throw new ParserException(source, "Unknown placeholder selector '" + name.get() + "'");
            placeholder.addReference(selector);
        }
    }
}
