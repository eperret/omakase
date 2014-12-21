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

package com.salesforce.omakase.broadcast.emitter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.salesforce.omakase.Message;
import com.salesforce.omakase.broadcast.annotation.Restrict;
import com.salesforce.omakase.broadcast.annotation.Observe;
import com.salesforce.omakase.broadcast.annotation.Rework;
import com.salesforce.omakase.broadcast.annotation.Validate;
import com.salesforce.omakase.error.ErrorManager;
import com.salesforce.omakase.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Scans for annotated methods on classes.
 *
 * @author nmcwilliams
 */
final class AnnotationScanner {
    private static final Set<String> SKIP = ImmutableSet.of("wait", "equals", "hashCode", "getClass", "notify", "notifyAll",
        "toString", "dependencies");

    /** cache of which methods on a {@link Plugin} are {@link Subscription} methods */
    private static final LoadingCache<Class<?>, Set<SubscriptionMetadata>> cache = CacheBuilder.newBuilder()
        .weakKeys()
        .build(new CacheLoader<Class<?>, Set<SubscriptionMetadata>>() {
            @Override
            public Set<SubscriptionMetadata> load(Class<?> klass) throws Exception {
                return parse(klass);
            }
        });

    /**
     * Creates subscription objects for each subscribed event on the class of the given instance.
     *
     * @param subscriber
     *     The class with the subscription methods.
     *
     * @return A multimap of syntax object (event) to subscription object.
     */
    public Multimap<Class<?>, Subscription> scan(Object subscriber) {
        // linked multimap because we need to maintain insertion order
        Multimap<Class<?>, Subscription> subscriptions = LinkedHashMultimap.create();

        for (SubscriptionMetadata md : cache.getUnchecked(subscriber.getClass())) {
            subscriptions.put(md.event, new Subscription(md.phase, subscriber, md.method, md.filter));
        }

        return subscriptions;
    }

    private static Set<SubscriptionMetadata> parse(Class<?> klass) {
        Set<SubscriptionMetadata> set = new HashSet<>();

        for (Method method : klass.getMethods()) {
            if (SKIP.contains(method.getName())) continue;

            boolean annotated = false;

            // the restrict annotation
            final Restrict filter = method.getAnnotation(Restrict.class);

            // the observe annotation
            if (method.isAnnotationPresent(Observe.class)) {
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PROCESS, filter));
            }

            // the rework annotation
            if (method.isAnnotationPresent(Rework.class)) {
                if (annotated) throw new SubscriptionException(Message.ANNOTATION_EXCLUSIVE, method);
                annotated = true;

                // must have exactly one parameter
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) throw new SubscriptionException(Message.ONE_PARAM, method);

                // add the metadata
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.PROCESS, filter));
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
                set.add(new SubscriptionMetadata(method, params[0], SubscriptionPhase.VALIDATE, filter));
            }

            // this is required for anonymous inner classes
            if (annotated && Modifier.isPublic(method.getModifiers())) {
                method.setAccessible(true);
            }
        }

        return set;
    }

    /** data object */
    private static final class SubscriptionMetadata {
        final Method method;
        final Class<?> event;
        final SubscriptionPhase phase;
        final Restrict filter;

        public SubscriptionMetadata(Method method, Class<?> event, SubscriptionPhase phase, Restrict filter) {
            this.method = method;
            this.event = event;
            this.phase = phase;
            this.filter = filter;
        }
    }
}
