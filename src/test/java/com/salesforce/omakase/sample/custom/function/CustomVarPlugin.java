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

package com.salesforce.omakase.sample.custom.function;

import java.util.Map;

import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.parser.Grammar;
import com.salesforce.omakase.parser.Source;
import com.salesforce.omakase.plugin.Plugin;

/**
 * This is the actual plugin that gets registered with the parser.
 *
 * @author nmcwilliams
 */
public class CustomVarPlugin implements Plugin {
    public enum Mode {PASSTHROUGH, RESOLVE}

    private final Mode mode;
    private final Map<String, String> vars;

    public CustomVarPlugin(Mode mode, Map<String, String> vars) {
        this.mode = mode;
        this.vars = vars;
    }

    @Refine(CustomVarFunction.NAME)
    public void refine(RawFunction raw, Grammar grammar, Broadcaster broadcaster) {
        // do some simple validation
        String arg = raw.args();
        if (!arg.matches("[a-zA-Z\\-]+")) {
            throw new RuntimeException("invalid custom-var arg: " + arg);
        } else if (!vars.containsKey(arg)) {
            throw new RuntimeException("unknown custom-var arg: " + arg);
        }

        if (mode == Mode.PASSTHROUGH) {
            // don't resolve, just convert to our custom AST object
            CustomVarFunction function = new CustomVarFunction(arg);

            // by broadcasting, it automatically gets added to the declaration since it is a Term. Also this is what
            // enables delivery to any subscription methods for the AST object.
            broadcaster.broadcast(function);
        } else {
            // parse the arg to real Term objects. Just by running the built-in parser, the terms will be automatically
            // broadcasted and thus associated with the declaration. Note that these terms are delivered to any applicable
            // subscription methods as normal.
            Source source = new Source(vars.get(arg), raw.line(), raw.column());
            grammar.parser().termSequenceParser().parse(source, grammar, broadcaster);
        }
    }
}
