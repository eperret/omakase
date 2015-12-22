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

package com.salesforce.omakase.parser.declaration;

import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.parser.AbstractParser;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.parser.refiner.MasterRefiner;
import com.salesforce.omakase.parser.token.Tokens;

/**
 * Parses a {@link HexColorValue}.
 *
 * @author nmcwilliams
 * @see HexColorValue
 */
public final class HexColorValueParser extends AbstractParser {

    @Override
    public boolean parse(Source source, Broadcaster broadcaster, MasterRefiner refiner) {
        // note: important not to skip whitespace anywhere in here, as it could skip over a space operator
        source.collectComments(false);

        // grab current position before parsing
        int line = source.originalLine();
        int column = source.originalColumn();

        // starts with hash and then a valid hex character
        if (Tokens.HASH.matches(source.current()) && Tokens.HEX_COLOR.matches(source.peek())) {
            // skip the hash mark
            source.next();

            // get the color value
            String color = source.chomp(Tokens.HEX_COLOR);

            // check for a valid length
            if (color.length() != 6 && color.length() != 3) throw new ParserException(source, Message.INVALID_HEX, color);

            HexColorValue value = new HexColorValue(line, column, color);
            value.comments(source.flushComments());

            broadcaster.broadcast(value);
            return true;
        }

        return false;
    }

}
