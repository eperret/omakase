/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.omakase.util;

import com.salesforce.omakase.SupportMatrix;
import com.salesforce.omakase.ast.declaration.*;
import com.salesforce.omakase.data.Keyword;
import com.salesforce.omakase.data.Prefix;
import com.salesforce.omakase.writer.StyleAppendable;
import com.salesforce.omakase.writer.StyleWriter;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

/** Unit tests for {@link Values}. */
@SuppressWarnings("JavaDoc")
public class ValuesTest {
    PropertyValue value;

    @Test
    public void asTermListPresent() {
        value = TermList.singleValue(NumericalValue.of(1));
        assertThat(Values.asTermList(value).isPresent()).isTrue();
    }

    @Test
    public void asTermListAbsent() {
        assertThat(Values.asTermList(new OtherPropertyValue()).isPresent()).isFalse();
    }

    @Test
    public void asHexColorPresentInTermList() {
        value = TermList.singleValue(new HexColorValue("fff"));
        assertThat(Values.asHexColor(value).isPresent()).isTrue();
    }

    @Test
    public void asHexColorNotPresentInTermList() {
        value = TermList.singleValue(new KeywordValue("none"));
        assertThat(Values.asHexColor(value).isPresent()).isFalse();
    }

    @Test
    public void asHexColorNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SPACE, HexColorValue.of("fff"), NumericalValue.of(1));
        assertThat(Values.asHexColor(value).isPresent()).isFalse();
    }

    @Test
    public void asKeywordPresentInTermList() {
        value = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Values.asKeyword(value).isPresent()).isTrue();
    }

    @Test
    public void asKeywordNotPresentInTermList() {
        value = TermList.singleValue(new HexColorValue("fff"));
        assertThat(Values.asKeyword(value).isPresent()).isFalse();
    }

    @Test
    public void asKeywordNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SPACE, KeywordValue.of(Keyword.NONE), NumericalValue.of(1));
        assertThat(Values.asKeyword(value).isPresent()).isFalse();
    }

    @Test
    public void asNumericalPresentInTermList() {
        value = TermList.singleValue(NumericalValue.of(1, "px"));
        assertThat(Values.asNumerical(value).isPresent()).isTrue();
    }

    @Test
    public void asNumericalNotPresentInTermList() {
        value = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Values.asNumerical(value).isPresent()).isFalse();
    }

    @Test
    public void asNumericalNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SLASH, KeywordValue.of(Keyword.NONE), NumericalValue.of(1));
        assertThat(Values.asNumerical(value).isPresent()).isFalse();
    }

    @Test
    public void asStringPresentInTermList() {
        value = TermList.singleValue(StringValue.of(QuotationMode.DOUBLE, "helloworld"));
        assertThat(Values.asString(value).isPresent()).isTrue();
    }

    @Test
    public void asStringNotPresentInTermList() {
        value = TermList.singleValue(KeywordValue.of(Keyword.NONE));
        assertThat(Values.asString(value).isPresent()).isFalse();
    }

    @Test
    public void asStringNotOnlyOneInTermList() {
        value = TermList.ofValues(OperatorType.SPACE, StringValue.of(QuotationMode.DOUBLE, "h"), NumericalValue.of(1));
        assertThat(Values.asString(value).isPresent()).isFalse();
    }

    private static final class OtherPropertyValue extends AbstractPropertyValue implements PropertyValue {
        @Override
        public void write(StyleWriter writer, StyleAppendable appendable) throws IOException {
        }

        @Override
        public boolean isImportant() {
            return false;
        }

        @Override
        public PropertyValue important(boolean important) {
            return this;
        }

        @Override
        public PropertyValue copy() {
            return null;
        }

        @Override
        public PropertyValue copyWithPrefix(Prefix prefix, SupportMatrix support) {
            return null;
        }
    }
}