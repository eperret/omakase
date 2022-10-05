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

package com.salesforce.omakase.tools;

import java.util.Comparator;
import java.util.List;

import org.reflections.Reflections;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.salesforce.omakase.broadcast.annotation.Description;
import com.salesforce.omakase.broadcast.annotation.Subscribable;

/**
 * Lists all {@link Subscribable} {@link Syntax} units. Also available via the 'script/omakase.sh' script.
 *
 * @author nmcwilliams
 */
public class PrintSubscribableSyntaxTable {
    public static void main(String[] args) {
        new PrintSubscribableSyntaxTable().run();
    }

    public void run() {
        Reflections reflections = new Reflections("com.salesforce.omakase.ast");
        List<Class<?>> list = Lists.newArrayList(reflections.getTypesAnnotatedWith(Subscribable.class, true));

        list.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> class1, Class<?> class2) {
                return ComparisonChain.start()
                    .compare(class1.getPackage().getName(), class2.getPackage().getName())
                    .compareTrueFirst(class1.isInterface(), class2.isInterface())
                    .compare(class1.getSimpleName(), class2.getSimpleName())
                    .result();
            }
        });

        System.out.println();

        String header = "%s%-28s   %-55s   %-25s   %s";
        String line1 = String.format(header,
            space(4),
            "Name",
            "Description",
            "Enablement / Dependency",
            "Type");

        String line2 = String.format(header,
            space(4),
            dash(28),
            dash(55),
            dash(25),
            dash(15));

        System.out.println(line1);
        System.out.println(line2);

        int i = 1;
        for (Class<?> klass : list) {
            Description d = klass.getAnnotation(Description.class);
            if (d == null) throw new RuntimeException("missing @Description for " + klass);

            String s = "%02d: %-28s   %-55s   %-25s   %s";
            System.out.println(String.format(s,
                i++,
                klass.getSimpleName(),
                d.value(),
                d.broadcasted(),
                type(klass)));
        }

        System.out.println("\nGenerated by " + PrintSubscribableSyntaxTable.class.getSimpleName() + ".java");
    }

    private static String type(Class<?> klass) {
        return klass.isInterface() ? "interface" : "class";
    }

    private static String space(int number) {
        return repeat(" ", number);
    }

    private static String dash(int number) {
        return repeat("-", number);
    }

    private static String repeat(String string, int number) {
        return new String(new char[number]).replace("\0", string);
    }
}
