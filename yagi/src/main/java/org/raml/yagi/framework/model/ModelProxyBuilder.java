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
package org.raml.yagi.framework.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.util.NodeSelector;
import org.raml.yagi.framework.util.NodeUtils;

public class ModelProxyBuilder
{

    public static <T> T createModel(Class<T> apiInterface, NodeModel delegateNode, ModelBindingConfiguration bindingConfiguration)
    {
        return (T) Proxy.newProxyInstance(apiInterface.getClassLoader(),
                new Class[] {apiInterface, NodeModel.class},
                new SimpleProxy(delegateNode, bindingConfiguration));
    }

    private static class SimpleProxy implements InvocationHandler
    {
        private NodeModel delegate;
        private ModelBindingConfiguration bindingConfiguration;

        public SimpleProxy(NodeModel delegate, ModelBindingConfiguration bindingConfiguration)
        {
            this.bindingConfiguration = bindingConfiguration;
            if (delegate == null)
            {
                throw new IllegalArgumentException("delegate cannot be null");
            }
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            final Type genericReturnType = method.getGenericReturnType();
            final Method delegateMethod = findMatchingMethod(method);
            try
            {
                if (delegateMethod == null)
                {
                    return fromNodeKey(method, genericReturnType);
                }
                else
                {
                    return fromMethod(args, genericReturnType, delegateMethod);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Internal error while trying to call " + method.toGenericString(), e);
            }
        }

        protected Object fromNodeKey(Method method, Type genericReturnType)
        {
            final String propertyName = method.getName();
            if (delegate != null && method.getParameterTypes().length == 0)
            {
                return resolveValue(genericReturnType, NodeSelector.selectFrom(propertyName, delegate.getNode()));
            }
            else
            {
                throw new RuntimeException("Can not resolve method : " + method.getDeclaringClass().getName() + " from " + method.toGenericString() + " on " + delegate.getClass().getName());
            }
        }

        protected Object fromMethod(Object[] args, Type genericReturnType, Method delegateMethod) throws IllegalAccessException, InvocationTargetException
        {
            final Object invoke = delegateMethod.invoke(delegate, args);
            return resolveValue(genericReturnType, invoke);
        }

        @Nullable
        private Object resolveValue(Type genericReturnType, Object invoke)
        {
            Class<?> returnType = ModelUtils.toClass(genericReturnType);
            if (invoke == null || ModelUtils.isPrimitiveOrWrapperOrString(returnType) || ModelUtils.isObject(returnType))
            {
                return invoke;
            }
            else if (List.class.isAssignableFrom(returnType))
            {
                final List<Object> returnList = new ArrayList<>();
                final List<?> result = (List<?>) invoke;
                final Type itemClass = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                if (ModelUtils.isPrimitiveOrWrapperOrString(ModelUtils.toClass(itemClass)))
                {
                    return result;
                }
                for (Object item : result)
                {
                    returnList.add(resolveValue(itemClass, item));
                }
                return returnList;
            }
            else if (returnType.isAssignableFrom(invoke.getClass()))
            {
                // No need for proxy the object is already an instance of the expected value
                return invoke;
            }
            else
            {
                NodeModelFactory nodeModelFactory = bindingConfiguration.bindingOf(returnType);
                Class<?> proxyInterface = nodeModelFactory.polymorphic() ? bindingConfiguration.reverseBindingOf((NodeModel) invoke) : returnType;
                return createModel(proxyInterface, (NodeModel) invoke, bindingConfiguration);
            }
        }

        protected Object resolveValue(Type returnType, Node node)
        {
            final SimpleValueTransformer[] values = SimpleValueTransformer.values();
            final Class<?> returnClass = ModelUtils.toClass(returnType);
            for (SimpleValueTransformer value : values)
            {
                if (value.accepts(returnClass))
                {
                    return value.adaptTo(node, returnClass);
                }
            }

            // If it is not a simple type then it can be a list or an pojo
            if (List.class.isAssignableFrom(returnClass) && returnType instanceof ParameterizedType)
            {
                final Type itemClass = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                final List<Object> returnList = new ArrayList<>();
                if (NodeUtils.isNull(node))
                {
                    return returnList;
                }
                else if (node instanceof ArrayNode || node instanceof ObjectNode)
                {
                    final List<Node> children = node.getChildren();
                    for (Node child : children)
                    {
                        returnList.add(resolveValue(itemClass, child));
                    }
                }
                else
                {
                    returnList.add(resolveValue(itemClass, node));
                }
                return returnList;
            }
            else if (NodeUtils.isNull(node))
            {
                return null;
            }
            else if (returnClass.equals(Object.class))
            {
                if (node instanceof SimpleTypeNode)
                {
                    return ((SimpleTypeNode) node).getValue();
                }
                else
                {
                    // TODO: Here we should map it to map or list of maps
                    return null;
                }
            }
            else
            {
                final NodeModelFactory nodeModelFactory = bindingConfiguration.bindingOf(returnClass);
                final NodeModel nodeModel = nodeModelFactory.create(node);
                final Class<?> proxyInterface;
                if (nodeModelFactory.polymorphic())
                {
                    proxyInterface = bindingConfiguration.reverseBindingOf(nodeModel);
                }
                else
                {
                    proxyInterface = returnClass;
                }

                return createModel(proxyInterface, nodeModel, bindingConfiguration);
            }
        }

        @Nullable
        private Method findMatchingMethod(Method method)
        {
            try
            {
                return delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
            }
            catch (NoSuchMethodException e)
            {
                return null;
            }
        }

    }

}
