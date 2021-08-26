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

package com.salesforce.omakase.ast.declaration;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.Status;
import com.salesforce.omakase.broadcast.emitter.SubscriptionPhase;
import com.salesforce.omakase.writer.StyleWriter;

/**
 * Unit tests for {@link RawFunction}.
 *
 * @author nmcwilliams
 */
public class RawFunctionTest {

    @Test
    public void testName() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        assertThat(raw.name()).isEqualTo("name");

        raw.name("changed");
        assertThat(raw.name()).isEqualTo("changed");
    }

    @Test
    public void testArgs() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        assertThat(raw.args()).isEqualTo("args args");

        raw.args("changed");
        assertThat(raw.args()).isEqualTo("changed");
    }

    @Test
    public void write() {
        String out = StyleWriter.inline().writeSingle(new RawFunction(1, 1, "name", "args args"));
        assertThat(out).isEqualTo("name(args args)");
    }

    @Test
    public void copyNotSupported() {
        assertThrows(UnsupportedOperationException.class, () -> new RawFunction(1, 1, "name", "args args").copy());
    }

    @Test
    public void breakBroadcastIfNeverEmit() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        raw.status(Status.NEVER_EMIT);
        assertThat(raw.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void breakBroadcastIfParsed() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        raw.status(Status.PARSED);
        assertThat(raw.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isTrue();
    }

    @Test
    public void dontBreakBroadcastIfNotParsed() {
        RawFunction raw = new RawFunction(1, 1, "name", "args args");
        assertThat(raw.shouldBreakBroadcast(SubscriptionPhase.REFINE)).isFalse();
    }
}
