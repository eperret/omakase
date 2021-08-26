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

package com.salesforce.omakase.plugin.conditionals;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.salesforce.omakase.Omakase;
import com.salesforce.omakase.PluginRegistry;
import com.salesforce.omakase.error.ProblemSummaryException;
import com.salesforce.omakase.parser.ParserException;

/**
 * Unit tests for {@link ConditionalsValidator}.
 *
 * @author nmcwilliams
 */
public class ConditionalsValidatorTest {
    @SuppressWarnings("deprecation")
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void validatesBadBlockSyntax() {
        String src = "@if {.class{color:red}}";
        exception.expect(ParserException.class);
        Omakase.source(src).use(new ConditionalsValidator()).process();
    }

    @Test
    public void validatesBadInnerSyntax() {
        String src = "@if(ie7) {.class{color:red}";
        exception.expect(ParserException.class);
        Omakase.source(src).use(new ConditionalsValidator()).process();
    }

    @Test
    public void noErrorIfConditionAllowed() {
        String src = "@if(ie7) {.class{color:red}}";
        Omakase.source(src).use(new ConditionalsValidator("ie7")).process();
        // no error
    }

    @Test
    public void errorsIfConditionNotAllowed() {
        String src = "@if(ie8) {.class{color:red}}";
        exception.expect(ProblemSummaryException.class);
        exception.expectMessage("Invalid condition");
        Omakase.source(src).use(new ConditionalsValidator("ie7")).process();
    }

    @Test
    public void errorsIfSomeConditionsNotAllowed() {
        String src = "@if(ie7 || mobile) {.class{color:red}}";
        exception.expect(ProblemSummaryException.class);
        exception.expectMessage("Invalid condition");
        Omakase.source(src).use(new ConditionalsValidator("ie7", "desktop")).process();
    }

    @Test
    public void dependenciesWhenConditionalsPresent() {
        String src = "@if(ie8) {.class{color:red}}";
        PluginRegistry registry = Omakase.source(src)
            .use(new Conditionals())
            .use(new ConditionalsValidator())
            .process();

        assertThat(registry.retrieve(Conditionals.class).get().config().isPassthroughMode()).isFalse();
    }

    @Test
    public void dependenciesWhenConditionalsNotPresent() {
        String src = "@if(ie8) {.class{color:red}}";
        PluginRegistry registry = Omakase.source(src)
            .use(new ConditionalsValidator())
            .process();

        assertThat(registry.retrieve(Conditionals.class).get().config().isPassthroughMode()).isTrue();
    }
}
