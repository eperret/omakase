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

package com.salesforce.omakase.plugin.basic;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.salesforce.omakase.As;
import com.salesforce.omakase.ast.extended.ConditionalAtRuleBlock;

import java.util.Set;

/**
 * Manages the set of "trueConditions" as used by the {@link Conditionals} plugin and individual {@link ConditionalAtRuleBlock}
 * objects.
 * <p/>
 * This can be used to add to or remove from the set of "trueConditions". You can also specify "passthroughMode" as true, which
 * indicates that the {@link ConditionalAtRuleBlock} should write itself out without stripping the actual outer block
 * (<code>@if(...) { ... }</code>) away. This might be useful if you intend to deal with the conditionals as part of a subsequent
 * parsing operation.
 *
 * @author nmcwilliams
 */
public final class ConditionalsManager {
    private final Set<String> trueConditions = Sets.newHashSet();
    private boolean passthroughMode;

    /**
     * Gets whether the given condition is "true".
     *
     * @param condition
     *     Check if this condition is contained within the "trueConditions" set. This should be lower-cased (unless you otherwise
     *     knowingly and explicitly arranged for parsed conditionals to <em>not</em> be automatically lower-cased in contrast to
     *     the default behavior).
     *
     * @return True if the given condition is "true".
     */
    public boolean hasCondition(String condition) {
        return trueConditions.contains(condition);
    }

    /**
     * Returns an immutable copy of the trueConditions set.
     *
     * @return An immutable copy of the trueConditions set.
     */
    public ImmutableSet<String> trueConditions() {
        return ImmutableSet.copyOf(trueConditions);
    }

    /**
     * Adds the given strings to the trueConditions set. Each string will be automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     The strings that should evaluate to "true".
     *
     * @return this, for chaining.
     */
    public ConditionalsManager addTrueConditions(String... trueConditions) {
        return addTrueConditions(Sets.newHashSet(trueConditions));
    }

    /**
     * Adds the given strings to the trueConditions set. Each string will be automatically lower-cased for comparison purposes.
     *
     * @param trueConditions
     *     Iterable of the strings that should evaluate to "true".
     *
     * @return this, for chaining.
     */
    public ConditionalsManager addTrueConditions(Iterable<String> trueConditions) {
        // add each condition, making sure it's lower-cased for comparison purposes
        for (String condition : trueConditions) {
            this.trueConditions.add(condition.toLowerCase());
        }

        return this;
    }

    /**
     * Removes the given condition from the trueConditions set. This condition will no longer evaluate as "true".
     *
     * @param condition
     *     The condition to remove.
     *
     * @return this, for chaining.
     */
    public ConditionalsManager removeTrueCondition(String condition) {
        trueConditions.remove(condition);
        return this;
    }

    /**
     * Removes all current trueConditions.
     *
     * @return this, for chaining.
     */
    public ConditionalsManager clearTrueConditions() {
        trueConditions.clear();
        return this;
    }

    /**
     * Sets the passthroughMode status.
     *
     * @param passthroughMode
     *     Whether passthroughMode is true or false.
     *
     * @return this, for chaining.
     */
    public ConditionalsManager passthroughMode(boolean passthroughMode) {
        this.passthroughMode = passthroughMode;
        return this;
    }

    /**
     * Gets whether passthroughMode is true or false.
     *
     * @return Whether passthroughMode is true or false.
     */
    public boolean isPassthroughMode() {
        return passthroughMode;
    }

    @Override
    public String toString() {
        return As.string(this)
            .add("trueConditions", trueConditions)
            .add("passthroughMode", passthroughMode)
            .toString();
    }
}