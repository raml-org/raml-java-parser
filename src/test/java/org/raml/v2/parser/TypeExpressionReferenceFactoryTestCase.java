/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.v2.parser;

import org.junit.Assert;
import org.junit.Test;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.v10.nodes.factory.TypeExpressionReferenceFactory;
import org.raml.v2.internal.utils.TreeDumper;

import static org.hamcrest.CoreMatchers.equalTo;

public class TypeExpressionReferenceFactoryTestCase
{
    @Test
    public void SimpleReference()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "user");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void LibraryExpression()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "a.b.user");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "    LibraryRefNode b -> {null} (Start: -1 , End: -1)\n" +
                                        "        LibraryRefNode a -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void SimpleArray()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "a.b.user[]");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "        LibraryRefNode b -> {null} (Start: -1 , End: -1)\n" +
                                        "            LibraryRefNode a -> {null} (Start: -1 , End: -1)"));
    }


    @Test
    public void ArrayBiDimensional()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "a.b.user[][]");
        final String dump = new TreeDumper().dump(user).trim();
        System.out.println("dump = " + dump);
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "        NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "            LibraryRefNode b -> {null} (Start: -1 , End: -1)\n" +
                                        "                LibraryRefNode a -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void UnionOfReference()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "a.b.user | c.d.cat");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "        LibraryRefNode b -> {null} (Start: -1 , End: -1)\n" +
                                        "            LibraryRefNode a -> {null} (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode cat -> {null} (Start: -1 , End: -1)\n" +
                                        "        LibraryRefNode d -> {null} (Start: -1 , End: -1)\n" +
                                        "            LibraryRefNode c -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void UnionOfArray()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "a.b.user[] | cat[]");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "        NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "            LibraryRefNode b -> {null} (Start: -1 , End: -1)\n" +
                                        "                LibraryRefNode a -> {null} (Start: -1 , End: -1)\n" +
                                        "    ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "        NamedTypeExpressionNode cat -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void MultipleUnion()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "user | cat[] | hamster | fish");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "    ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "        NamedTypeExpressionNode cat -> {null} (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode hamster -> {null} (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode fish -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void Parenthesis()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "(user | cat)[]");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    UnionTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "        NamedTypeExpressionNode user -> {null} (Start: -1 , End: -1)\n" +
                                        "        NamedTypeExpressionNode cat -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void ArrayOfString()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "string[]");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    NativeTypeExpressionNode: \"string\" (Start: -1 , End: -1)"));
    }

    @Test
    public void UnionOfNativeAndRef()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "string | Person");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode (Start: -1 , End: -1)\n" +
                                        "    NativeTypeExpressionNode: \"string\" (Start: -1 , End: -1)\n" +
                                        "    NamedTypeExpressionNode Person -> {null} (Start: -1 , End: -1)"));
    }

    @Test
    public void UnbalancedParenthesis()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "(user | cat[]");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax. Caused by : Parenthesis are not correctly balanced. at character : 13\" (Start: -1 , End: -1)"));
    }


    @Test
    public void BadArrayExpression()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "[]cat");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax. Caused by : Expecting a type expression before [. at character : 0\" (Start: -1 , End: -1)"));
    }

    @Test
    public void BadArrayExpression2()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "cat[]cat");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax. Caused by : Invalid array type expression. at character : 0\" (Start: -1 , End: -1)"));
    }

    @Test
    public void BadUnionType()
    {
        final Node user = new TypeExpressionReferenceFactory().create(null, "a | ");
        final String dump = new TreeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax. Caused by : Invalid union type expression. at character : 0\" (Start: -1 , End: -1)"));
    }
}
