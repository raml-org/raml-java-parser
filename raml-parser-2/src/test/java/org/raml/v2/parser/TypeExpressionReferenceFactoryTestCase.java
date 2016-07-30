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
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.impl.v10.nodes.factory.TypeExpressionReferenceFactory;
import org.raml.v2.internal.utils.RamlTreeNodeDumper;

import static org.hamcrest.CoreMatchers.equalTo;

public class TypeExpressionReferenceFactoryTestCase
{
    @Test
    public void SimpleReference()
    {
        final Node user = new TypeExpressionReferenceFactory().create(new StringNodeImpl("user"), "user");
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("NamedTypeExpressionNode user -> {null} (Start: 3 , End: 7, On: [artificial node])"));
    }

    @Test
    public void LibraryExpression()
    {
        final StringNodeImpl expression = new StringNodeImpl("a.user");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("NamedTypeExpressionNode user -> {null} (Start: 7 , End: 13, On: [artificial node])\n" +
                                        "    LibraryRefNode a -> {null} (Start: 6 , End: 12, On: [artificial node])"));
    }

    @Test
    public void SimpleArray()
    {
        final StringNodeImpl expression = new StringNodeImpl("a.user[]");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode: user[] (Start: 7 , End: 15, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode user -> {null} (Start: 7 , End: 13, On: [artificial node])\n" +
                                        "        LibraryRefNode a -> {null} (Start: 6 , End: 12, On: [artificial node])"));
    }


    @Test
    public void ArrayBiDimensional()
    {
        final StringNodeImpl expression = new StringNodeImpl("a.user[][]");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode: user[][] (Start: 7 , End: 17, On: [artificial node])\n" +
                                        "    ArrayTypeExpressionNode: user[] (Start: 7 , End: 15, On: [artificial node])\n" +
                                        "        NamedTypeExpressionNode user -> {null} (Start: 7 , End: 13, On: [artificial node])\n" +
                                        "            LibraryRefNode a -> {null} (Start: 6 , End: 12, On: [artificial node])"));
    }

    @Test
    public void UnionOfReference()
    {
        final StringNodeImpl expression = new StringNodeImpl("a.user | c.cat");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode: user | cat (Start: 8 , End: 20, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode user -> {null} (Start: 8 , End: 14, On: [artificial node])\n" +
                                        "        LibraryRefNode a -> {null} (Start: 7 , End: 13, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode cat -> {null} (Start: 15 , End: 20, On: [artificial node])\n" +
                                        "        LibraryRefNode c -> {null} (Start: 14 , End: 19, On: [artificial node])"));
    }

    @Test
    public void UnionOfArray()
    {
        final StringNodeImpl expression = new StringNodeImpl("a.user[] | cat[]");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode: user[] | cat[] (Start: 7 , End: 18, On: [artificial node])\n" +
                                        "    ArrayTypeExpressionNode: user[] (Start: 7 , End: 15, On: [artificial node])\n" +
                                        "        NamedTypeExpressionNode user -> {null} (Start: 7 , End: 13, On: [artificial node])\n" +
                                        "            LibraryRefNode a -> {null} (Start: 6 , End: 12, On: [artificial node])\n" +
                                        "    ArrayTypeExpressionNode: cat[] (Start: 13 , End: 18, On: [artificial node])\n" +
                                        "        NamedTypeExpressionNode cat -> {null} (Start: 13 , End: 16, On: [artificial node])"));
    }

    @Test
    public void MultipleUnion()
    {
        final StringNodeImpl expression = new StringNodeImpl("user | cat[] | hamster | fish");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode: user | cat[] | hamster | fish (Start: 4 , End: 32, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode user -> {null} (Start: 4 , End: 8, On: [artificial node])\n" +
                                        "    ArrayTypeExpressionNode: cat[] (Start: 9 , End: 14, On: [artificial node])\n" +
                                        "        NamedTypeExpressionNode cat -> {null} (Start: 9 , End: 12, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode hamster -> {null} (Start: 22 , End: 29, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode fish -> {null} (Start: 28 , End: 32, On: [artificial node])"));
    }

    @Test
    public void Parenthesis()
    {
        final StringNodeImpl expression = new StringNodeImpl("(user | cat)[]");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode: (user | cat)[] (Start: 5 , End: 15, On: [artificial node])\n" +
                                        "    UnionTypeExpressionNode: user | cat (Start: 5 , End: 13, On: [artificial node])\n" +
                                        "        NamedTypeExpressionNode user -> {null} (Start: 5 , End: 9, On: [artificial node])\n" +
                                        "        NamedTypeExpressionNode cat -> {null} (Start: 10 , End: 13, On: [artificial node])"));
    }

    @Test
    public void ArrayOfString()
    {
        final StringNodeImpl expression = new StringNodeImpl("string[]");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ArrayTypeExpressionNode: string[] (Start: -1 , End: 7)\n" +
                                        "    NativeTypeExpressionNode: \"string\" (Start: -1 , End: 5)"));
    }

    @Test
    public void UnionOfNativeAndRef()
    {
        final StringNodeImpl expression = new StringNodeImpl("string | Person");
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("UnionTypeExpressionNode: string | Person (Start: 0 , End: 20, On: [artificial node])\n" +
                                        "    NativeTypeExpressionNode: \"string\" (Start: 0 , End: 6, On: [artificial node])\n" +
                                        "    NamedTypeExpressionNode Person -> {null} (Start: 14 , End: 20, On: [artificial node])"));
    }

    @Test
    public void UnbalancedParenthesis()
    {
        String invalidExpression = "(user | cat[]";
        final StringNodeImpl expression = new StringNodeImpl(invalidExpression);
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax: \"" + invalidExpression +
                                        "\". Caused by : Parenthesis are not correctly balanced. at character : 13\" (Start: -1 , End: -1)"));
    }


    @Test
    public void BadArrayExpression()
    {
        String invalidExpression = "[]cat";
        final StringNodeImpl expression = new StringNodeImpl(invalidExpression);
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax: \"" + invalidExpression +
                                        "\". Caused by : Expecting a type expression before [. at character : 0\" (Start: -1 , End: -1)"));
    }

    @Test
    public void BadArrayExpression2()
    {
        String invalidExpression = "cat[]cat";
        final StringNodeImpl expression = new StringNodeImpl(invalidExpression);
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax: \"" + invalidExpression +
                                        "\". Caused by : Invalid array type expression. at character : 8\" (Start: -1 , End: -1)"));
    }

    @Test
    public void BadUnionType()
    {
        String invalidExpression = "a |";
        final StringNodeImpl expression = new StringNodeImpl(invalidExpression);
        final Node user = new TypeExpressionReferenceFactory().create(expression, expression.getLiteralValue());
        final String dump = new RamlTreeNodeDumper().dump(user).trim();
        Assert.assertThat(dump, equalTo("ErrorNode: \"Invalid type expression syntax: \"" + invalidExpression +
                                        "\". Caused by : Invalid union type expression. at character : 3\" (Start: -1 , End: -1)"));
    }
}
