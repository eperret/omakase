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

package com.salesforce.omakase.ast.collection;

import com.google.common.base.Optional;
import com.salesforce.omakase.ast.Syntax;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.broadcast.annotation.Rework;

/**
 * Represents an item that appears in a group or chain of other related units, for usage with {@link SyntaxCollection}.
 * <p/>
 * Note that uniqueness within the same {@link SyntaxCollection} is <em>not</em> enforced, which means that if you prepend or
 * append an instance that already exists in the {@link SyntaxCollection} it will be duplicated (multiple references to the same
 * object instance). If this is not what you want then first call {@link #destroy()} on the unit, or consider using {@link
 * SyntaxCollection#moveBefore(Groupable, Groupable)} and {@link SyntaxCollection#moveAfter(Groupable, Groupable)}.
 * <p/>
 * Multiple calls to detach and append/prepend in mass should be minimized for performance reasons. In some cases it may be better
 * to alternatively consider detaching the parent unit itself and attaching the applicable children straight to a new replacement
 * parent node.
 * <p/>
 * Also note that appending or prepending a unit that already exists in one {@link SyntaxCollection} to another {@link
 * SyntaxCollection} will <em>not</em> remove the unit from the first {@link SyntaxCollection}. The unit will exist in both
 * collections. This may or may not be the desired behavior depending on the use-case. If this is not desired then call {@link
 * #destroy()} before appending or prepending the unit to the new parent (or use {@link SyntaxCollection#moveBefore(Groupable,
 * Groupable)} / {@link SyntaxCollection#moveAfter(Groupable, Groupable)}).
 *
 * @param <P>
 *     Type of the (P)arent object containing this collection (e.g., {@link SelectorPart}s have {@link Selector}s as the parent).
 * @param <T>
 *     The (T)ype of units to be grouped with.
 *
 * @author nmcwilliams
 * @see SyntaxCollection
 */
public interface Groupable<P, T extends Groupable<P, T>> extends Syntax<T> {
    /**
     * Gets whether this unit is the first within its group.
     * <p/>
     * Please note, if you are making decisions based on this value then keep in mind that any rework plugins may add or remove
     * new units before or after this one. As such, this usually means it's best that plugins with {@link Rework} methods
     * utilizing this value are registered last.
     *
     * @return True if the unit is first within its group. Always returns true if this unit is detached.
     */
    boolean isFirst();

    /**
     * Gets whether this unit is the last within its group.
     * <p/>
     * Please note, if you are making decisions based on this value then keep in mind that any rework plugins may add or remove
     * new units before or after this one. As such, this usually means it's best that plugins with {@link Rework} methods
     * utilizing this value are registered last.
     *
     * @return True if the unit is last within its group. Always returns true if this unit is detached.
     */
    boolean isLast();

    /**
     * Gets the unit following this one in the same collection, if there is one. This will always return {@link Optional#absent
     * ()} if detached.
     *
     * @return The next unit, or {@link Optional#absent()} if there isn't one.
     */
    Optional<T> next();

    /**
     * Gets the unit preceding this one in the same collection, if there is one. This will always return {@link Optional#absent
     * ()} if detached.
     *
     * @return The previous unit, or {@link Optional#absent()} if there isn't one.
     */
    Optional<T> previous();

    /**
     * Prepends the given unit before this one.
     * <p/>
     * Note that uniqueness within the same {@link SyntaxCollection} is <em>not</em> enforced, which means that if you prepend or
     * append an instance that already exists in the {@link SyntaxCollection} it will be duplicated. If this is not what you want
     * then first call {@link #destroy()} on the unit.
     * <p/>
     * Multiple calls to detach and append/prepend in mass should be minimized for performance reasons. In some cases it may be
     * better to alternatively consider detaching the parent unit itself and attaching the applicable children straight to a new
     * replacement parent node.
     * <p/>
     * Also note that appending or prepending a unit that already exists in one {@link SyntaxCollection} to another {@link
     * SyntaxCollection} will <em>not</em> remove the unit from the first {@link SyntaxCollection}. The unit will exist in both
     * collections. This may or may not be the desired behavior depending on the use-case. If this is not desired then call {@link
     * #destroy()} before appending or prepending the unit to the new parent (or use {@link SyntaxCollection#moveBefore(Groupable,
     * Groupable)} / {@link SyntaxCollection#moveAfter(Groupable, Groupable)}).
     *
     * @param unit
     *     The unit to prepend.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any group).
     */
    Groupable<P, T> prepend(T unit);

    /**
     * Appends the given unit after this one.
     * <p/>
     * Note that uniqueness within the same {@link SyntaxCollection} is <em>not</em> enforced, which means that if you prepend or
     * append an instance that already exists in the {@link SyntaxCollection} it will be duplicated. If this is not what you want
     * then first call {@link #destroy()} on the unit.
     * <p/>
     * Multiple calls to detach and append/prepend in mass should be minimized for performance reasons. In some cases it may be
     * better to alternatively consider detaching the parent unit itself and attaching the applicable children straight to a new
     * replacement parent node.
     * <p/>
     * Also note that appending or prepending a unit that already exists in one {@link SyntaxCollection} to another {@link
     * SyntaxCollection} will <em>not</em> remove the unit from the first {@link SyntaxCollection}. The unit will exist in both
     * collections. This may or may not be the desired behavior depending on the use-case. If this is not desired then call {@link
     * #destroy()} before appending or prepending the unit to the new parent (or use {@link SyntaxCollection#moveBefore(Groupable,
     * Groupable)} / {@link SyntaxCollection#moveAfter(Groupable, Groupable)}).
     *
     * @param unit
     *     The unit to append.
     *
     * @return this, for chaining.
     *
     * @throws IllegalStateException
     *     If this unit is currently detached (doesn't belong to any group).
     */
    Groupable<P, T> append(T unit);

    /** TODO */
    void destroy();

    /** TODO */
    boolean destroyed();

    Groupable<P, T> unlink();

    /**
     * Sets the group. Internal method only! Do not call directly or behavior will be unexpected.
     *
     * @param group
     *     The group group.
     *
     * @return this, for chaining.
     */
    Groupable<P, T> group(SyntaxCollection<P, T> group);

    /**
     * Gets the group {@link SyntaxCollection} of this unit.
     *
     * @return The group {@link SyntaxCollection}, or {@link Optional#absent()} if the group is not specified.
     */
    Optional<SyntaxCollection<P, T>> group();

    /**
     * Gets the parent {@link Syntax} unit that owns the {@link SyntaxCollection} that contains this unit. See {@link
     * SyntaxCollection#parent()}.
     *
     * @return The parent, or {@link Optional#absent()} if the parent is not specified.
     */
    Optional<P> parent();
}
