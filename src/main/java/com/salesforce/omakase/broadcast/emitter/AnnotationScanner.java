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

package com.salesforce.omakase.broadcast.emitter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.ast.Refinable;
import com.salesforce.omakase.broadcast.Broadcaster;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Refine;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.parser.Grammar;

/**
 * Scans for annotated methods on classes.
 *
 * @author nmcwilliams
 */
final class AnnotationScanner {
    private static final Set<String> SKIP = ImmutableSet.of(
        "wait", "equals", "hashCode", "getClass", "notify", "notifyAll", "toString", "dependencies");

    /** cache of which methods on a {@link Plugin} are {@link Subscription} methods */
    private static final LoadingCache<Class<?>, Set<SubscriptionMetadata>> subscriptionCache = CacheBuilder.newBuilder()
        .weakKeys()
        .build(new CacheLoader<Class<?>, Set<SubscriptionMetadata>>() {
            @Override
            public Set<SubscriptionMetadata> load(Class<?> klass) throws Exception {
                return readSubscriptionAnnotations(klass);
            }
        });

    private static Set<SubscriptionMetadata> readSubscriptionAnnotations(Class<?> klass) {
        Set<SubscriptionMetadata> set = new HashSet<>();

        for (Method method : klass.getMethods()) {
            if (SKIP.contains(method.getName())) continue;

            boolean annotated = false;

            // the observe annotation
            if (method.isAnnotationPresent(Observe.class)) {
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PROCESS, null));
            }

            // the rework annotation
            if (method.isAnnotationPresent(Rework.class)) {
                if (annotated) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PROCESS, null));
            }

            // the validate annotation
            if (method.isAnnotationPresent(Validate.class)) {
                if (annotated) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                // must have exactly two parameters
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 2) throw new SubscriptionException(Message.TWO_PARAMS, method);

                // second param must be an error manager
                boolean errorManager = ErrorManager.class.isAssignableFrom(params[1]);
                if (!errorManager) throw new SubscriptionException(Message.MISSING_ERROR_MANAGER, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.VALIDATE, null));
            }

            // the refine annotation
            if (method.isAnnotationPresent(Refine.class)) {
                if (annotated) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                Refine refine = method.getAnnotation(Refine.class);

                // must have exactly three parameters
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 3) throw new SubscriptionException(Message.THREE_PARAMS, method);

                // first param must be instance of Refinable
                boolean refinable = Refinable.class.isAssignableFrom(params[0]);
                if (!refinable) throw new SubscriptionException(Message.MISSING_REFINABLE, method);

                // second param must be Refiner
                boolean grammar = Grammar.class.isAssignableFrom(params[1]);
                if (!grammar) throw new SubscriptionException(Message.MISSING_GRAMMAR, method);

                // third param must be Broadcaster
                boolean broadcaster = Broadcaster.class.isAssignableFrom(params[2]);
                if (!broadcaster) throw new SubscriptionException(Message.MISSING_BROADCASTER, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.REFINE, refine.value()));
            }

            // this is required for anonymous inner classes
            if (annotated && Modifier.isPublic(method.getModifiers())) {
                method.setAccessible(true);
            }
        }

        return set;
    }

    /**
     * Creates subscription objects for each subscribed event on the class of the given instance.
     *
     * @param subscriber
     *     The class with the subscription methods.
     *
     * @return A multimap of syntax object (event) to subscription object.
     */
    public Multimap<Class<?>, Subscription> scanSubscriptions(Object subscriber) {
        // linked multimap because we need to maintain insertion order
        Multimap<Class<?>, Subscription> subscriptions = LinkedHashMultimap.create();

        for (SubscriptionMetadata sm : subscriptionCache.getUnchecked(subscriber.getClass())) {
            subscriptions.put(sm.event, new Subscription(sm.phase, subscriber, sm.method, sm.name));
        }

        return subscriptions;
    }

    /** data object */
    private static final class SubscriptionMetadata {
        final Method method;
        final Class<?> event;
        final SubscriptionPhase phase;
        final String name;

        public SubscriptionMetadata(Method method, Class<?> event, SubscriptionPhase phase, String name) {
            this.method = method;
            this.event = event;
            this.phase = phase;
            this.name = Strings.emptyToNull(name);
        }
    }
}
