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

package com.salesforce.omakase.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Sets;
import com.salesforce.omakase.ast.selector.ClassSelector;
import com.salesforce.omakase.ast.selector.IdSelector;
import com.salesforce.omakase.ast.selector.PseudoClassSelector;
import com.salesforce.omakase.ast.selector.PseudoElementSelector;
import com.salesforce.omakase.ast.selector.Selector;
import com.salesforce.omakase.ast.selector.SelectorPart;
import com.salesforce.omakase.ast.selector.TypeSelector;

/**
 * Utilities for working with {@link Selector}s and {@link SelectorPart}s.
 *
 * @author nmcwilliams
 */
public final class Selectors {
    /** do not construct */
    private Selectors() {}

    /**
     * Gets the given part as an instance of a {@link ClassSelector}, if it is one.
     *
     * @param part
     *     Check if this part is a {@link ClassSelector}.
     *
     * @return The class selector, or an empty {@link Optional} if the part is a different type.
     */
    public static Optional<ClassSelector> asClassSelector(SelectorPart part) {
        return as(ClassSelector.class, part);
    }

    /**
     * Gets the given part as an instance of an {@link IdSelector}, if it is one.
     *
     * @param part
     *     Check if this part is a {@link IdSelector}.
     *
     * @return The id selector, or an empty {@link Optional} if the part is a different type.
     */
    public static Optional<IdSelector> asIdSelector(SelectorPart part) {
        return as(IdSelector.class, part);
    }

    /**
     * Gets the given part as an instance of a {@link TypeSelector} if it is one.
     *
     * @param part
     *     Check if this part is a {@link TypeSelector}.
     *
     * @return The type selector, or an empty {@link Optional} if the part is a different type.
     */
    public static Optional<TypeSelector> asTypeSelector(SelectorPart part) {
        return as(TypeSelector.class, part);
    }

    /**
     * Gets the given part as an instance of a {@link PseudoElementSelector} if it is one.
     *
     * @param part
     *     Check if this part is a {@link PseudoElementSelector}.
     *
     * @return The pseudo element selector, or an empty {@link Optional} if the part is a different type.
     */
    public static Optional<PseudoElementSelector> asPseudoElementSelector(SelectorPart part) {
        return as(PseudoElementSelector.class, part);
    }

    /**
     * Gets the given part as an instance of a {@link PseudoClassSelector} if it is one.
     *
     * @param part
     *     Check if this part is a {@link PseudoClassSelector}.
     *
     * @return The pseudo element selector, or an empty {@link Optional} if the part is a different type.
     */
    public static Optional<PseudoClassSelector> asPseudoClassSelector(SelectorPart part) {
        return as(PseudoClassSelector.class, part);
    }

    /**
     * Gets the given part as an instance of the given {@link SelectorPart} type.
     *
     * @param klass
     *     Check if the part is an instance of this class.
     * @param part
     *     The part to check.
     * @param <T>
     *     Check if the part is an instance of this class.
     *
     * @return the properly-typed instance, or an empty {@link Optional} if it doesn't match.
     */
    public static <T extends SelectorPart> Optional<T> as(Class<T> klass, SelectorPart part) {
        return klass.isInstance(part) ? Optional.of(klass.cast(part)) : Optional.empty();
    }

    /**
     * Checks the given {@link Selector} for a {@link ClassSelector} that matches the given name. If you don't actually need the
     * instance itself then you can use {@link #hasClassSelector(Selector, String)} instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return The class selector, oran empty {@link Optional} if not found.
     */
    public static Optional<ClassSelector> findClassSelector(Selector selector, String name) {
        return findClassSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link ClassSelector} that matches the given name. If you don't actually need
     * the instance itself then you can use {@link #hasClassSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return The class selector, or an empty {@link Optional} if not found.
     */
    public static Optional<ClassSelector> findClassSelector(Iterable<SelectorPart> parts, String name) {
        for (SelectorPart part : parts) {
            Optional<ClassSelector> cs = asClassSelector(part);
            if (cs.isPresent() && cs.get().name().equals(name)) return cs;
        }
        return Optional.empty();
    }

    /**
     * Checks the given {@link Selector} for the <em>first</em> {@link IdSelector} that matches the given name. If you don't
     * actually need the instance itself then you can use {@link #hasIdSelector(Selector, String)} instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return The class selector, or an empty {@link Optional} if not found.
     */
    public static Optional<IdSelector> findIdSelector(Selector selector, String name) {
        return findIdSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link IdSelector} that matches the given name. If you don't actually need
     * the instance itself then you can use {@link #hasIdSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return The id selector, or an empty {@link Optional} if not found.
     */
    public static Optional<IdSelector> findIdSelector(Iterable<SelectorPart> parts, String name) {
        for (SelectorPart part : parts) {
            Optional<IdSelector> id = asIdSelector(part);
            if (id.isPresent() && id.get().name().equals(name)) return id;
        }
        return Optional.empty();
    }

    /**
     * Checks the given {@link Selector} for the <em>first</em> {@link TypeSelector} that matches the given name. If you don't
     * actually need the instance itself then you can use {@link #hasTypeSelector(Selector, String)} instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return The class selector, or an empty {@link Optional} if not found.
     */
    public static Optional<TypeSelector> findTypeSelector(Selector selector, String name) {
        return findTypeSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link TypeSelector} that matches the given name. If you don't actually need
     * the instance itself then you can use {@link #hasTypeSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return The type selector, or an empty {@link Optional} if not found.
     */
    public static Optional<TypeSelector> findTypeSelector(Iterable<SelectorPart> parts, String name) {
        for (SelectorPart part : parts) {
            Optional<TypeSelector> type = asTypeSelector(part);
            if (type.isPresent() && type.get().name().equals(name)) return type;
        }
        return Optional.empty();
    }

    /**
     * Checks the given {@link Selector} for the <em>first</em> {@link PseudoElementSelector} that matches the given name. If you
     * don't actually need the instance itself then you can use {@link #hasPseudoElementSelector(Selector, String, boolean)}
     * instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link PseudoElementSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return The class selector, or an empty {@link Optional} if not found.
     */
    public static Optional<PseudoElementSelector> findPseudoElementSelector(Selector selector, String name, boolean exact) {
        return findPseudoElementSelector(selector.parts(), name, exact);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link PseudoElementSelector} that matches the given name. If you don't
     * actually need the instance itself then you can use {@link #hasPseudoElementSelector(Iterable, String, boolean)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link PseudoElementSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return The type selector, or an empty {@link Optional} if not found.
     */
    public static Optional<PseudoElementSelector> findPseudoElementSelector(Iterable<SelectorPart> parts, String name, boolean exact) {
        for (SelectorPart part : parts) {
            Optional<PseudoElementSelector> pseudo = asPseudoElementSelector(part);
            if (pseudo.isPresent()) {
                if (exact || pseudo.get().name().charAt(0) != '-') {
                    if (pseudo.get().name().equals(name)) return pseudo;
                } else if (Prefixes.unprefixed(pseudo.get().name()).equals(name)) {
                    return pseudo;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Checks the given {@link Selector} for the <em>first</em> {@link PseudoClassSelector} that matches the given name. If you
     * don't actually need the instance itself then you can use {@link #hasPseudoClassSelector(Selector, String, boolean)}
     * instead.
     *
     * @param selector
     *     The selector to check.
     * @param name
     *     Check for a {@link PseudoClassSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return The class selector, or an empty {@link Optional} if not found.
     */
    public static Optional<PseudoClassSelector> findPseudoClassSelector(Selector selector, String name, boolean exact) {
        return findPseudoClassSelector(selector.parts(), name, exact);
    }

    /**
     * Checks the given parts for the <em>first</em> {@link PseudoClassSelector} that matches the given name. If you don't
     * actually need the instance itself then you can use {@link #hasPseudoClassSelector(Iterable, String, boolean)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link PseudoClassSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return The type selector, or an empty {@link Optional} if not found.
     */
    public static Optional<PseudoClassSelector> findPseudoClassSelector(Iterable<SelectorPart> parts, String name, boolean exact) {
        for (SelectorPart part : parts) {
            Optional<PseudoClassSelector> pseudo = asPseudoClassSelector(part);
            if (pseudo.isPresent()) {
                if (exact || pseudo.get().name().charAt(0) != '-') {
                    if (pseudo.get().name().equals(name)) return pseudo;
                } else if (Prefixes.unprefixed(pseudo.get().name()).equals(name)) {
                    return pseudo;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Checks the given parts for a {@link ClassSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findClassSelector(Selector, String)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return True if one of the parts is a {@link ClassSelector} with the given name.
     */
    public static boolean hasClassSelector(Selector selector, String name) {
        return hasClassSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for a {@link ClassSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findClassSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link ClassSelector} with this name.
     *
     * @return True if one of the parts is a {@link ClassSelector} with the given name.
     */
    public static boolean hasClassSelector(Iterable<SelectorPart> parts, String name) {
        return findClassSelector(parts, name).isPresent();
    }

    /**
     * Checks the given parts for a {@link IdSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findIdSelector(Selector, String)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return True if one of the parts is a {@link IdSelector} with the given name.
     */
    public static boolean hasIdSelector(Selector selector, String name) {
        return hasIdSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for a {@link IdSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findIdSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link IdSelector} with this name.
     *
     * @return True if one of the parts is a {@link IdSelector} with the given name.
     */
    public static boolean hasIdSelector(Iterable<SelectorPart> parts, String name) {
        return findIdSelector(parts, name).isPresent();
    }

    /**
     * Checks the given parts for a {@link TypeSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findTypeSelector(Selector, String)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return True if one of the parts is a {@link TypeSelector} with the given name.
     */
    public static boolean hasTypeSelector(Selector selector, String name) {
        return hasTypeSelector(selector.parts(), name);
    }

    /**
     * Checks the given parts for a {@link TypeSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findTypeSelector(Iterable, String)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link TypeSelector} with this name.
     *
     * @return True if one of the parts is a {@link TypeSelector} with the given name.
     */
    public static boolean hasTypeSelector(Iterable<SelectorPart> parts, String name) {
        return findTypeSelector(parts, name).isPresent();
    }

    /**
     * Checks the given parts for a {@link PseudoElementSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findPseudoElementSelector(Selector, String,
     * boolean)} instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link PseudoElementSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return True if one of the parts is a {@link PseudoElementSelector} with the given name.
     */
    public static boolean hasPseudoElementSelector(Selector selector, String name, boolean exact) {
        return hasPseudoElementSelector(selector.parts(), name, exact);
    }

    /**
     * Checks the given parts for a {@link PseudoElementSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findPseudoElementSelector(Iterable, String,
     * boolean)} instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link PseudoElementSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return True if one of the parts is a {@link PseudoElementSelector} with the given name.
     */
    public static boolean hasPseudoElementSelector(Iterable<SelectorPart> parts, String name, boolean exact) {
        return findPseudoElementSelector(parts, name, exact).isPresent();
    }

    /**
     * Checks the given parts for a {@link PseudoClassSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findPseudoClassSelector(Selector, String, boolean)}
     * instead.
     *
     * @param selector
     *     The {@link Selector} to check.
     * @param name
     *     Check for a {@link PseudoClassSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return True if one of the parts is a {@link PseudoClassSelector} with the given name.
     */
    public static boolean hasPseudoClassSelector(Selector selector, String name, boolean exact) {
        return hasPseudoClassSelector(selector.parts(), name, exact);
    }

    /**
     * Checks the given parts for a {@link PseudoClassSelector} that matches the given name.
     * <p>
     * If you would like access to the found instance itself then use {@link #findPseudoClassSelector(Iterable, String, boolean)}
     * instead.
     *
     * @param parts
     *     The parts to check.
     * @param name
     *     Check for a {@link PseudoClassSelector} with this name.
     * @param exact
     *     Specify true to match the exact name, false to only check the unprefixed portion.
     *
     * @return True if one of the parts is a {@link PseudoClassSelector} with the given name.
     */
    public static boolean hasPseudoClassSelector(Iterable<SelectorPart> parts, String name, boolean exact) {
        return findPseudoClassSelector(parts, name, exact).isPresent();
    }

    /**
     * Gets the non-combinator {@link SelectorPart}s contiguous to this one.
     * <p>
     * If this part is a {@link Combinator} then this method will return a collection of one, containing only this {@link
     * Combinator} instance itself.
     * <p>
     * If this part is <em>not</em> a {@link Combinator} then this method will return all preceding and subsequent {@link
     * SelectorPart}s up until the first encountered {@link Combinator}. In other words, this will return all parts matching a
     * single element, such as multiple class selectors.
     * <p>
     * For example, in this selector:
     * <pre><code>
     * .test1.test2#test3 .testA.testB#testC
     * </code></pre>
     * <p>
     * If <code>this</code> is ".test2", this method will return the ".test1", ".test2", and "#test3" parts.
     *
     * @param part
     *     Get the selector parts adjoining this one.
     *
     * @return The adjoining {@link SelectorPart}s.
     */
    public static Iterable<SelectorPart> adjoining(SelectorPart part) {
        if (part.type().isCombinator()) return Sets.newHashSet(part);

        Deque<SelectorPart> deque = new ArrayDeque<>();

        // add previous parts until we hit a combinator
        Optional<SelectorPart> previous = part.previous();
        while (previous.isPresent() && !previous.get().type().isCombinator()) {
            deque.addFirst(previous.get());
            previous = previous.get().previous();
        }

        // add self
        deque.addLast(part);

        // add all subsequent parts until we hit a combinator
        Optional<SelectorPart> next = part.next();
        while (next.isPresent() && !next.get().type().isCombinator()) {
            deque.addLast(next.get());
            next = next.get().next();
        }

        return deque;
    }

    /**
     * Filters the selector parts in the given {@link Selector} to only the ones of the given class type.
     * <p>
     * Example:
     * <pre>
     * {@code Iterable<ClassSelector> classes = Selectors.filter(ClassSelector.class, selector);}
     * </pre>
     *
     * @param klass
     *     Filter to parts of this class.
     * @param selector
     *     The {@link Selector} to filter.
     * @param <T>
     *     Filters to parts of this type.
     *
     * @return The filtered results.
     */
    public static <T extends SelectorPart> Iterable<T> filter(Class<T> klass, Selector selector) {
        List<T> filtered = new ArrayList<>();
        for (SelectorPart part : selector.parts()) {
            if (klass.isAssignableFrom(part.getClass())) filtered.add(klass.cast(part));
        }
        return filtered;
    }
}
