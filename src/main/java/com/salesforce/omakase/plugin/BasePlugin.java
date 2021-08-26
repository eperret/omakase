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

package com.salesforce.omakase.plugin;

import com.salesforce.omakase.ast.RawFunction;
import com.salesforce.omakase.ast.Rule;
import com.salesforce.omakase.ast.Statement;
import com.salesforce.omakase.ast.Stylesheet;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.atrule.AtRule;
import com.salesforce.omakase.ast.atrule.FontDescriptor;
import com.salesforce.omakase.ast.atrule.MediaQueryList;
import com.salesforce.omakase.ast.declaration.Declaration;
import com.salesforce.omakase.ast.declaration.FunctionValue;
import com.salesforce.omakase.ast.declaration.GenericFunctionValue;
import com.salesforce.omakase.ast.declaration.HexColorValue;
import com.salesforce.omakase.ast.declaration.KeywordValue;
import com.salesforce.omakase.ast.declaration.LinearGradientFunctionValue;
import com.salesforce.omakase.ast.declaration.NumericalValue;
import com.salesforce.omakase.ast.declaration.PropertyValue;
import com.salesforce.omakase.ast.declaration.StringValue;
import com.salesforce.omakase.ast.declaration.Term;
import com.salesforce.omakase.ast.declaration.UnicodeRangeValue;
import com.salesforce.omakase.ast.declaration.UrlFunctionValue;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;
import com.salesforce.omakase.ast.extended.UnquotedIEFilter;
import com.salesforce.omakase.ast.selector.AttributeSelector;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.ast.selector.SimpleSelector;
import com.salesforce.omakase.ast.selector.TypeSelector;
import com.salesforce.omakase.ast.selector.UniversalSelector;

/**
 * An optional base {@link Plugin} that can be extended from or used to see which types of subscriptions are possible.
 * <p>
 * It is <em>not</em> recommended that you override each one of these methods. Note that some methods are more generic
 * subscriptions that will also be covered by their more specific counterparts. For example, a {@link ClassSelector} will be sent
 * to {@link #classSelector(ClassSelector)} and {@link #selectorPart(SelectorPart)}.
 * <p>
 * See the notes on {@link Plugin} about invocation order for subscription methods.
 *
 * @author nmcwilliams
 */
public class BasePlugin implements Plugin {
    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for ALL Syntax units.
     *
     * @param syntax
     *     The {@link Syntax} instance.
     */
    public void syntax(Syntax syntax) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Stylesheet}.
     *
     * @param stylesheet
     *     The {@link Stylesheet} instance.
     */
    public void stylesheet(Stylesheet stylesheet) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Statement}.
     *
     * @param statement
     *     The {@link Statement} instance.
     */
    public void statement(Statement statement) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Rule}.
     *
     * @param rule
     *     The {@link Rule} instance.
     */
    public void rule(Rule rule) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link AtRule}.
     *
     * @param rule
     *     The {@link AtRule} instance.
     */
    public void atRule(AtRule rule) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Selector}.
     *
     * @param selector
     *     The {@link Selector} instance.
     */
    public void selector(Selector selector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link SelectorPart} ({@link SimpleSelector}s or {@link Combinator}s).
     *
     * @param selectorPart
     *     The {@link SelectorPart} instance.
     */
    public void selectorPart(SelectorPart selectorPart) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link SimpleSelector}.
     *
     * @param simpleSelector
     *     The {@link SimpleSelector} instance.
     */
    public void simpleSelector(SimpleSelector simpleSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link TypeSelector}.
     *
     * @param typeSelector
     *     The {@link TypeSelector} instance.
     */
    public void typeSelector(TypeSelector typeSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UniversalSelector}.
     *
     * @param universalSelector
     *     The {@link UniversalSelector} instance.
     */
    public void universalSelector(UniversalSelector universalSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link IdSelector}.
     *
     * @param idSelector
     *     The {@link IdSelector} instance.
     */
    public void idSelector(IdSelector idSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link ClassSelector}.
     *
     * @param classSelector
     *     The {@link ClassSelector} instance.
     */
    public void classSelector(ClassSelector classSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link AttributeSelector}.
     *
     * @param attributeSelector
     *     The {@link AttributeSelector} instance.
     */
    public void attributeSelector(AttributeSelector attributeSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link PseudoClassSelector}.
     *
     * @param pseudoClassSelector
     *     The {@link PseudoClassSelector} instance.
     */
    public void pseudoClassSelector(PseudoClassSelector pseudoClassSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link PseudoElementSelector}.
     *
     * @param pseudoElementSelector
     *     The {@link PseudoElementSelector} instance.
     */
    public void pseudoElementSelector(PseudoElementSelector pseudoElementSelector) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Declaration}.
     *
     * @param declaration
     *     The {@link Declaration} instance.
     */
    public void declaration(Declaration declaration) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link PropertyValue}.
     *
     * @param propertyValue
     *     The {@link PropertyValue} instance.
     */
    public void propertyValue(PropertyValue propertyValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link Term}.
     *
     * @param term
     *     The {@link Term} instance.
     */
    public void term(Term term) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link RawFunction}.
     *
     * @param raw
     *     The {@link RawFunction} instance.
     */
    public void rawFunction(RawFunction raw) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link FunctionValue}.
     *
     * @param function
     *     The {@link FunctionValue} instance.
     */
    public void functionValue(FunctionValue function) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link GenericFunctionValue}.
     *
     * @param function
     *     The {@link GenericFunctionValue} instance.
     */
    public void genericFunction(GenericFunctionValue function) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UrlFunctionValue}.
     *
     * @param url
     *     The {@link UrlFunctionValue} instance.
     */
    public void urlValue(UrlFunctionValue url) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link LinearGradientFunctionValue}.
     *
     * @param linearGradient
     *     The {@link LinearGradientFunctionValue} instance.
     */
    public void linearGradientValue(LinearGradientFunctionValue linearGradient) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link HexColorValue}.
     *
     * @param hexColorValue
     *     The {@link HexColorValue} instance.
     */
    public void hexColorValue(HexColorValue hexColorValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link KeywordValue}.
     *
     * @param keywordValue
     *     The {@link KeywordValue} instance.
     */
    public void keywordValue(KeywordValue keywordValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link NumericalValue}.
     *
     * @param numericalValue
     *     The {@link NumericalValue} instance.
     */
    public void numericalValue(NumericalValue numericalValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link StringValue}.
     *
     * @param stringValue
     *     The {@link StringValue} instance.
     */
    public void stringValue(StringValue stringValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UnicodeRangeValue}.
     *
     * @param unicodeRangeValue
     *     The {@link UnicodeRangeValue} instance.
     */
    public void unicodeRangeValue(UnicodeRangeValue unicodeRangeValue) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link UnquotedIEFilter} (only enabled with the {@link UnquotedIEFilterPlugin} plugin.
     *
     * @param filter
     *     The {@link UnquotedIEFilter} instance.
     */
    public void unquotedIEFilter(UnquotedIEFilter filter) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link ConditionalAtRuleBlock} (only enabled with the {@link Conditionals} plugin.
     *
     * @param block
     *     The {@link ConditionalAtRuleBlock} instance.
     */
    public void conditionalAtRuleBlock(ConditionalAtRuleBlock block) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link MediaQueryList}.
     *
     * @param list
     *     The {@link MediaQueryList} instance.
     */
    public void mediaQueryList(MediaQueryList list) {}

    /**
     * Override this method and add the {@link Rework}, {@link Observe} or {@link Validate} annotation in order to receive events
     * for {@link Syntax} units of type {@link FontDescriptor}.
     *
     * @param descriptor
     *     The {@link FontDescriptor} instance.
     */
    public void fontFaceDescriptor(FontDescriptor descriptor) {}
}
