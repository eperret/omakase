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

package com.salesforce.omakase.broadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to subscribe to {@link Syntax} objects when the method is expected to change or modify the object/source
 * code.
 * <p>
 * Examples of rework include adding cache-busters to urls, changing class names, flipping directions for RTL support, etc... It
 * basically represents changing the content.
 * <p>
 * The one and only parameter for methods with this annotation should be one of the {@link Syntax} types. It is generally better
 * to subscribe the the most specific unit as possible (e.g., {@link UrlFunctionValue} instead of {@link Declaration}), however
 * note that lower-level syntax types will not be delivered if the parent type is not refined. See the main readme file for more
 * information about refinement. Generally, if you are parsing with {@link AutoRefine} or {@link StandardValidation} then all
 * syntax types will be delivered.
 * <p>
 * If the method does not intend to change the content or object, use {@link Observe} instead.
 * <p>
 * All {@link Rework} subscriptions will be delivered <em>after</em> {@link Refine} methods have finished executing.
 * <p>
 * Inside of a rework method, you can remove a unit from the syntax tree by calling {@link Groupable#destroy()}. Once a unit is
 * destroyed it is no longer broadcasted to any subsequent plugins, including validation. Destroyed units cannot be added to the
 * tree again, however they can still be copied. If you are storing the units in a cache then you will probably want to check
 * {@link Groupable#isDestroyed()} upon later access as the unit may have been destroyed by another plugin.
 * <p>
 * See SimpleReworkTest.java for same rework method implementations.
 *
 * @author nmcwilliams
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rework {
}
