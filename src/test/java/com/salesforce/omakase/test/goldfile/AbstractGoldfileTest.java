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

package com.salesforce.omakase.test.goldfile;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.salesforce.omakase.plugin.Plugin;
import com.salesforce.omakase.writer.StyleWriter;
import com.salesforce.omakase.writer.WriterMode;

/**
 * Base class for goldfile tests.
 *
 * @author nmcwilliams
 */
@RunWith(Parameterized.class)
public abstract class AbstractGoldfileTest {
    @Parameters(name = "mode={0} refined={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {WriterMode.VERBOSE, true},
            {WriterMode.VERBOSE, false},
            {WriterMode.INLINE, true},
            {WriterMode.INLINE, false},
            {WriterMode.COMPRESSED, true},
            {WriterMode.COMPRESSED, false},
        });
    }

    @Parameter(0) public WriterMode mode;
    @Parameter(1) public boolean autoRefine;

    public abstract String name();

    @Test
    public void goldfile() throws IOException {
        StyleWriter writer = new StyleWriter(mode);
        applyAdditionalWriterConfig(writer);
        Goldfile.test(name(), writer, autoRefine, plugins());
    }

    /** add any additional settings to the writer */
    protected void applyAdditionalWriterConfig(StyleWriter writer) {}

    /** add any additional plugins to run */
    protected Iterable<Plugin> plugins() {return ImmutableList.of();}
}
