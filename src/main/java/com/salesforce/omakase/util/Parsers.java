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

package com.salesforce.omakase.util;

import java.util.Optional;

import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.broadcast.Broadcastable;
import com.salesforce.omakase.broadcast.InterestBroadcaster;
import com.salesforce.omakase.broadcast.SingleInterestBroadcaster;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Parser;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.factory.StandardParserFactory;

/**
 * Utilities for working with {@link Parser}s.
 *
 * @author nmcwilliams
 * @see StandardParserFactory
 */
public final class Parsers {
    private Parsers() {}

    /**
     * Parses a {@link NumericalValue} at the beginning of the given string.
     *
     * @param source
     *     Parse this string.
     *
     * @return The {@link NumericalValue}, or an empty {@link Optional} if not present.
     */
    public static Optional<NumericalValue> parseNumerical(String source) {
        return parseNumerical(new Source(source));
    }

    /**
     * Parses a {@link NumericalValue} at the beginning of the given source.
     *
     * @param source
     *     Parse this source.
     *
     * @return The {@link NumericalValue}, or an empty {@link Optional} if not present.
     */
    public static Optional<NumericalValue> parseNumerical(Source source) {
        return parseSimple(source, StandardParserFactory.instance().numericalValueParser(), NumericalValue.class);
    }

    /**
     * Uses the given parser to parse an instance of the given class at the beginning of the given source.
     * <p>
     * The parser must find and broadcast an instance of the given type, and it must be at the beginning of the source.
     * <p>
     * Example:
     * <pre><code>
     * Parsers.parseSimple("10px solid red", ParserFactory.numericalValueParser(), NumericalValue.class);
     * </code></pre>
     *
     * @param source
     *     The source to parse.
     * @param parser
     *     The parser to use.
     * @param klass
     *     The class of the object to parse.
     * @param <T>
     *     Type of the object to parse.
     *
     * @return The parsed instance, or an empty {@link Optional} if not present.
     */
    public static <T extends Broadcastable> Optional<T> parseSimple(String source, Parser parser, Class<T> klass) {
        return parseSimple(new Source(source), parser, klass);
    }

    /**
     * Uses the given parser to parse an instance of the given class at the beginning of the given source.
     * <p>
     * The parser must find and broadcast an instance of the given type, and it must be at the beginning of the source.
     * <p>
     * Example:
     * <pre><code>
     * Parsers.parseSimple(source, ParserFactory.numericalValueParser(), NumericalValue.class);
     * </code></pre>
     *
     * @param source
     *     The source to parse.
     * @param parser
     *     The parser to use.
     * @param klass
     *     The class of the object to parse.
     * @param <T>
     *     Type of the object to parse.
     *
     * @return The parsed instance, or an empty {@link Optional} if not present.
     */
    public static <T extends Broadcastable> Optional<T> parseSimple(Source source, Parser parser, Class<T> klass) {
        InterestBroadcaster<T> broadcaster = SingleInterestBroadcaster.of(klass);
        parser.parse(source, new Grammar(), broadcaster);
        return broadcaster.one();
    }
}
