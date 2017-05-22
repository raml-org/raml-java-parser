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
package org.raml.v2.internal.impl.commons.model.factory;

import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.model.NodeModelFactory;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.model.type.AnyTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.ArrayTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.BooleanTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.DateTimeOnlyTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.DateTimeTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.DateTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.FileTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.IntegerTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.JSONTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.NullTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.NumberTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.ObjectTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.StringTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.TimeOnlyTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.TypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.UnionTypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.XMLTypeDeclaration;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.impl.v10.type.AnyResolvedType;
import org.raml.v2.internal.impl.v10.type.ArrayResolvedType;
import org.raml.v2.internal.impl.v10.type.BooleanResolvedType;
import org.raml.v2.internal.impl.v10.type.DateOnlyResolvedType;
import org.raml.v2.internal.impl.v10.type.DateTimeOnlyResolvedType;
import org.raml.v2.internal.impl.v10.type.DateTimeResolvedType;
import org.raml.v2.internal.impl.v10.type.FileResolvedType;
import org.raml.v2.internal.impl.v10.type.IntegerResolvedType;
import org.raml.v2.internal.impl.v10.type.NullResolvedType;
import org.raml.v2.internal.impl.v10.type.NumberResolvedType;
import org.raml.v2.internal.impl.v10.type.ObjectResolvedType;
import org.raml.v2.internal.impl.v10.type.StringResolvedType;
import org.raml.v2.internal.impl.v10.type.TimeOnlyResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeVisitor;
import org.raml.v2.internal.impl.v10.type.UnionResolvedType;
import org.raml.yagi.framework.nodes.StringNodeImpl;

public class TypeDeclarationModelFactory implements NodeModelFactory
{

    @Override
    public TypeDeclaration create(Node node)
    {
        if (node instanceof KeyValueNode && ((KeyValueNode) node).getValue() instanceof TypeDeclarationNode)
        {
            final KeyValueNode keyValueNode = (KeyValueNode) node;
            final TypeDeclarationNode typeDeclarationNode = (TypeDeclarationNode) keyValueNode.getValue();
            final ResolvedType resolvedType = typeDeclarationNode.getResolvedType();
            return new NodeModelTypeFactory(keyValueNode).create(resolvedType);
        }
        else if (node instanceof TypeDeclarationNode)
        {
            final TypeDeclarationNode typeDeclarationNode = (TypeDeclarationNode) node;
            final ResolvedType resolvedType = typeDeclarationNode.getResolvedType();
            return createTypeDeclaration(typeDeclarationNode, resolvedType);
        }
        else
        {
            throw new RuntimeException("Invalid node for type creation " + node.getClass());
        }
    }

    public TypeDeclaration createTypeDeclaration(TypeExpressionNode node, ResolvedType resolvedType)
    {
        KeyValueNode keyValueNode = node.findAncestorWith(KeyValueNode.class);
        if (keyValueNode == null)
        {
            keyValueNode = new KeyValueNodeImpl(new StringNodeImpl("__DataType_Fragment__"), node);
        }
        return new NodeModelTypeFactory(keyValueNode).create(resolvedType);
    }

    @Override
    public boolean polymorphic()
    {
        return true;
    }

    public TypeDeclaration create(ResolvedType type)
    {
        return createTypeDeclaration(type.getTypeExpressionNode(), type);
    }

    private static class NodeModelTypeFactory implements TypeVisitor<TypeDeclaration>
    {
        private final KeyValueNode keyValueNode;

        public NodeModelTypeFactory(KeyValueNode keyValueNode)
        {
            this.keyValueNode = keyValueNode;
        }

        @Override
        public TypeDeclaration visitString(StringResolvedType stringTypeDefinition)
        {
            return new StringTypeDeclaration(keyValueNode, stringTypeDefinition);
        }

        @Override
        public TypeDeclaration visitObject(ObjectResolvedType objectTypeDefinition)
        {
            return new ObjectTypeDeclaration(keyValueNode, objectTypeDefinition);
        }

        @Override
        public TypeDeclaration visitBoolean(BooleanResolvedType booleanTypeDefinition)
        {
            return new BooleanTypeDeclaration(keyValueNode, booleanTypeDefinition);
        }

        @Override
        public TypeDeclaration visitInteger(IntegerResolvedType integerTypeDefinition)
        {
            return new IntegerTypeDeclaration(keyValueNode, integerTypeDefinition);
        }

        @Override
        public TypeDeclaration visitNumber(NumberResolvedType numberTypeDefinition)
        {
            return new NumberTypeDeclaration(keyValueNode, numberTypeDefinition);
        }

        @Override
        public TypeDeclaration visitDateTimeOnly(DateTimeOnlyResolvedType dateTimeOnlyTypeDefinition)
        {
            return new DateTimeOnlyTypeDeclaration(keyValueNode, dateTimeOnlyTypeDefinition);
        }

        @Override
        public TypeDeclaration visitDate(DateOnlyResolvedType dateOnlyTypeDefinition)
        {
            return new DateTypeDeclaration(keyValueNode, dateOnlyTypeDefinition);
        }

        @Override
        public TypeDeclaration visitDateTime(DateTimeResolvedType dateTimeTypeDefinition)
        {
            return new DateTimeTypeDeclaration(keyValueNode, dateTimeTypeDefinition);
        }

        @Override
        public TypeDeclaration visitFile(FileResolvedType fileTypeDefinition)
        {
            return new FileTypeDeclaration(keyValueNode, fileTypeDefinition);
        }

        @Override
        public TypeDeclaration visitNull(NullResolvedType nullTypeDefinition)
        {
            return new NullTypeDeclaration(keyValueNode, nullTypeDefinition);
        }

        @Override
        public TypeDeclaration visitArray(ArrayResolvedType arrayTypeDefinition)
        {
            return new ArrayTypeDeclaration(keyValueNode, arrayTypeDefinition);
        }

        @Override
        public TypeDeclaration visitUnion(UnionResolvedType unionTypeDefinition)
        {
            return new UnionTypeDeclaration(keyValueNode, unionTypeDefinition);
        }

        @Override
        public TypeDeclaration visitTimeOnly(TimeOnlyResolvedType timeOnlyTypeDefinition)
        {
            return new TimeOnlyTypeDeclaration(keyValueNode, timeOnlyTypeDefinition);
        }

        @Override
        public TypeDeclaration visitJson(JsonSchemaExternalType jsonTypeDefinition)
        {
            return new JSONTypeDeclaration(keyValueNode, jsonTypeDefinition);
        }

        @Override
        public TypeDeclaration visitXml(XmlSchemaExternalType xmlTypeDefinition)
        {
            return new XMLTypeDeclaration(keyValueNode, xmlTypeDefinition);
        }

        @Override
        public TypeDeclaration visitAny(AnyResolvedType anyResolvedType)
        {
            return new AnyTypeDeclaration(keyValueNode, anyResolvedType);
        }

        public TypeDeclaration create(ResolvedType resolvedType)
        {
            return resolvedType.visit(this);
        }
    }
}
