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

package com.salesforce.omakase.broadcast;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;

/**
 * Unit tests for {@link QueryableBroadcaster}.
 *
 * @author nmcwilliams
 */
public class QueryableBroadcasterTest {

    private final ClassSelector sample1 = new ClassSelector(1, 1, "test");
    private final ClassSelector sample1a = new ClassSelector(1, 1, "test");
    private final ClassSelector sample1b = new ClassSelector(1, 1, "test");
    private final IdSelector sample2 = new IdSelector(1, 1, "test");

    @Test
    public void filterMatches() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<ClassSelector> filtered = qb.filter(ClassSelector.class);
        assertThat(filtered).hasSize(1);
        assertThat(Iterables.get(filtered, 0)).isSameAs(sample1);
    }

    @Test
    public void filterHigherHierarchy() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        @SuppressWarnings("rawtypes")
        Iterable<Syntax> filtered = qb.filter(Syntax.class);
        assertThat(filtered).hasSize(2);
        assertThat(Iterables.get(filtered, 0)).isSameAs(sample1);
        assertThat(Iterables.get(filtered, 1)).isSameAs(sample2);
    }

    @Test
    public void filterDoesntMatch() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<PseudoElementSelector> filtered = qb.filter(PseudoElementSelector.class);
        assertThat(filtered).isEmpty();
    }

    @Test
    public void findExists() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample1a);
        qb.broadcast(sample1b);
        Optional<ClassSelector> found = qb.find(ClassSelector.class);
        assertThat(found.get()).isSameAs(sample1);
    }

    @Test
    public void findDoesntExist() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        Optional<IdSelector> found = qb.find(IdSelector.class);
        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void all() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample2);
        Iterable<Broadcastable> filtered = qb.all();
        assertThat(filtered).hasSize(2);
        assertThat(Iterables.get(filtered, 0)).isSameAs(sample1);
        assertThat(Iterables.get(filtered, 1)).isSameAs(sample2);
    }

    @Test
    public void relaysToInnerBroadcaster() {
        InnerBroadcaster ib = new InnerBroadcaster();
        QueryableBroadcaster qb = new QueryableBroadcaster(ib);
        qb.broadcast(sample1);
        assertThat(ib.called).isTrue();
    }

    @Test
    public void count() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample1a);
        qb.broadcast(sample1b);
        assertThat(qb.count()).isEqualTo(3);
    }

    @Test
    public void hasAnyTrue() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        qb.broadcast(sample1);
        qb.broadcast(sample1a);
        qb.broadcast(sample1b);
        assertThat(qb.hasAny()).isTrue();
    }

    @Test
    public void hasAnyFalse() {
        QueryableBroadcaster qb = new QueryableBroadcaster();
        assertThat(qb.hasAny()).isFalse();
    }

    public static final class InnerBroadcaster extends AbstractBroadcaster {
        boolean called = false;

        @Override
        public void broadcast(Broadcastable broadcastable) {
            called = true;
        }
    }
}
