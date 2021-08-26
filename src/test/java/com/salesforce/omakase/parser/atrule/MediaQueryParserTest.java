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

package com.salesforce.omakase.parser.atrule;

import static com.salesforce.omakase.test.util.TemplatesHelper.withExpectedResult;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.atrule.MediaQuery;
import com.salesforce.omakase.ast.atrule.MediaRestriction;
import com.salesforce.omakase.parser.AbstractParserTest;
import com.salesforce.omakase.parser.ParserException;
import com.salesforce.omakase.test.util.TemplatesHelper;
import com.salesforce.omakase.test.util.TemplatesHelper.SourceWithExpectedResult;

/**
 * Unit tests for {@link MediaQueryParser}.
 *
 * @author nmcwilliams
 */
public class MediaQueryParserTest extends AbstractParserTest<MediaQueryParser> {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Override
    public List<String> invalidSources() {
        return ImmutableList.of(
            "  ",
            "\n",
            "$blah",
            "1test"
        );
    }

    @Override
    public List<String> validSources() {
        return ImmutableList.of(
            "all",
            "all and (min-width: 800px)",
            "only screen and (min-width: 800px)",
            "(min-width: 700px)",
            "(orientation: landscape) ",
            "tv and (min-width: 700px)",
            "  handheld and (orientation: landscape)",
            "all and (color)",
            "screen and (min-aspect-ratio: 1/1)",
            "(max-width: 15em)",
            "only SCREEN and (max-device-width: 480px)",
            "not screen AND (color)",
            "\tscreen and (min-width: 900px)",
            "screen and (min-width: 600px) and (max-width: 900px)",
            "screen and (min-width: 600px) AND (max-width: 900px)"
        );
    }

    @Override
    public List<SourceWithExpectedResult<Integer>> validSourcesWithExpectedEndIndex() {
        return ImmutableList.of(
            withExpectedResult("all", 3),
            withExpectedResult("(min-width: 700px)  , handheld and (orientation: landscape)", 20),
            withExpectedResult("all and (min-width:800px  )", 27),
            withExpectedResult("screen and (device-aspect-ratio: 16/9), screen", 38),
            withExpectedResult("screen and (min-width: 600px) and (max-width: 900px), all and (min-width:900px", 52));
    }

    @Override
    public String validSourceForPositionTesting() {
        return Iterables.get(validSources(), 0);
    }

    @Override
    public boolean allowedToTrimLeadingWhitespace() {
        return true;
    }

    @Test
    @Override
    public void matchesExpectedBroadcastCount() {
        List<ParseResult<Integer>> results = parseWithExpected(ImmutableList.of(
            withExpectedResult("(color)", 2),
            withExpectedResult("(min-width:800px)", 2),
            withExpectedResult("all and (min-width:800px)", 2)
        ));

        for (ParseResult<Integer> result : results) {
            assertThat(result.broadcasted).describedAs(result.source.toString()).hasSize(result.expected);
        }
    }

    @Test
    @Override
    public void matchesExpectedBroadcastContent() {
        List<ParseResult<String>> results = parseWithExpected(
            withExpectedResult("(color)", ""),
            withExpectedResult("(min-width:800px)", ""),
            withExpectedResult("all", "all"),
            withExpectedResult("all and (min-width:800px)", "all"),
            withExpectedResult("only screen and (max-device-width: 480px)", "screen"),
            withExpectedResult("  handheld and (orientation: landscape)", "handheld"),
            withExpectedResult("not screen and (color)", "screen"),
            withExpectedResult("screen and (min-width: 600px) and (max-width: 900px)", "screen")
        );

        for (ParseResult<String> result : results) {
            MediaQuery mq = result.broadcaster.find(MediaQuery.class).get();
            if (result.expected.isEmpty()) {
                assertThat(mq.type().isPresent()).isFalse();
            } else {
                assertThat(mq.type().get()).isEqualTo(result.expected);
            }
        }
    }

    @Test
    public void matchesExpectedRestriction() {
        List<ParseResult<MediaRestriction>> results = parseWithExpected(
            TemplatesHelper.withExpectedResult("(color)", null),
            TemplatesHelper.withExpectedResult("(min-width:800px)", null),
            TemplatesHelper.withExpectedResult("all", null),
            TemplatesHelper.withExpectedResult("all and (min-width:800px)", null),
            TemplatesHelper.withExpectedResult("  handheld and (orientation: landscape)", null),
            withExpectedResult("only screen and (max-device-width: 480px)", MediaRestriction.ONLY),
            withExpectedResult("ONLY screen and (max-device-width: 480px)", MediaRestriction.ONLY),
            withExpectedResult("not screen and (color)", MediaRestriction.NOT),
            withExpectedResult("NOT screen and (color)", MediaRestriction.NOT)
        );

        for (ParseResult<MediaRestriction> result : results) {
            MediaQuery mq = result.broadcaster.find(MediaQuery.class).get();
            if (result.expected == null) {
                assertThat(mq.restriction().isPresent()).isFalse();
            } else {
                assertThat(mq.restriction().get()).isSameAs(result.expected);
            }
        }
    }

    @Test
    public void matchesExpectedExpressions() {
        List<ParseResult<Integer>> results = parseWithExpected(
            withExpectedResult("(color)", 1),
            withExpectedResult("(min-width:800px)", 1),
            withExpectedResult("all", 0),
            withExpectedResult("all and (min-width:800px)", 1),
            withExpectedResult("only screen and (max-device-width: 480px)", 1),
            withExpectedResult("  handheld and (orientation: landscape)", 1),
            withExpectedResult("not screen and (color)", 1),
            withExpectedResult("screen and (min-width: 600px) and (max-width: 900px)", 2),
            withExpectedResult("handheld and (grid) and (max-width: 15em)", 2),
            withExpectedResult("handheld and (grid) AND (max-width: 15em)", 2)
        );

        for (ParseResult<Integer> result : results) {
            MediaQuery mq = result.broadcaster.find(MediaQuery.class).get();
            assertThat(mq.expressions().size()).isEqualTo(result.expected);
        }
    }

    @Test
    public void errorsIfRestrictionAndNoMediaType() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_MEDIA_TYPE);
        parse("only (min-width:800px");
    }

    @Test
    public void errorsIfRestrictionAndNoMediaTypeButHasAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_MEDIA_TYPE);
        parse("only and (min-width:800px");
    }

    @Test
    public void errorsIfMissingAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.MISSING_AND);
        parse("all (min-width:800px)");
    }

    @Test
    public void errorsIfTrailingAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.TRAILING_AND);
        parse("all and (min-width:800px) and ");
    }

    @Test
    public void errorsIfTrailingAndNoExpression() {
        exception.expect(ParserException.class);
        exception.expectMessage(Message.TRAILING_AND);
        parse("all and ");
    }

    @Test
    public void errorsIfNoSpacAfterAnd() {
        exception.expect(ParserException.class);
        exception.expectMessage("Expected to find");
        parse("all and(min-width:800px)");
    }
}
