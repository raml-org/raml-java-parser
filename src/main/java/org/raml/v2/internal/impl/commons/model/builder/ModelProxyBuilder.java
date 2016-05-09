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
package org.raml.v2.internal.impl.commons.model.builder;

import static org.raml.v2.internal.impl.commons.model.builder.ModelUtils.isPrimitiveOrWrapperOrString;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.impl.commons.model.Api;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;

public class ModelProxyBuilder
{

    public static <T> T createRaml(Class<T> apiInterface, RamlDocumentNode delegateNode)
    {
        return (T) Proxy.newProxyInstance(
                apiInterface.getClassLoader(),
                new Class[] {apiInterface},
                new SimpleProxy(new Api(delegateNode)));
    }

    private static class SimpleProxy implements InvocationHandler
    {

        private Object delegate;

        public SimpleProxy(Object delegate)
        {
            if (delegate == null)
            {
                throw new IllegalArgumentException("delegate cannot be null");
            }
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            final Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            Method delegateMethod = findMatchingMethod(method);

            if (isPrimitiveOrWrapperOrString(returnType) || isObject(returnType))
            {
                return delegateMethod.invoke(delegate, args);
            }

            // antother spec interface
            if (!(genericReturnType instanceof ParameterizedType))
            {
                Object result = delegateMethod.invoke(delegate, args);
                if (result == null)
                {
                    return null;
                }
                // interface expects single element or list depending on version
                // e.g.:
                // List<MimeType> mediaType(); //v10
                // MimeType mediaType(); //v08
                if (result instanceof List)
                {
                    if (((List) result).isEmpty())
                    {
                        return null;
                    }
                    result = ((List) result).get(0);
                }
                return Proxy.newProxyInstance(returnType.getClassLoader(), new Class[] {returnType}, new SimpleProxy(result));
            }

            // list
            if (List.class.isAssignableFrom(returnType))
            {
                List<Object> returnList = new ArrayList<>();
                List<?> result = (List<?>) delegateMethod.invoke(delegate, args);
                Class<?> itemClass = (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                if (isPrimitiveOrWrapperOrString(itemClass))
                {
                    return result;
                }
                for (Object item : result)
                {
                    returnList.add(Proxy.newProxyInstance(itemClass.getClassLoader(), new Class[] {itemClass}, new SimpleProxy(item)));
                }
                return returnList;
            }

            throw new RuntimeException("case not handled yet... " + returnType.getName());
        }

        private boolean isObject(Class<?> type)
        {
            return "java.lang.Object".equals(type.getName());
        }

        private Method findMatchingMethod(Method method)
        {
            try
            {
                return delegate.getClass().getMethod(getActualMethodName(method), method.getParameterTypes());
            }
            catch (NoSuchMethodException e)
            {
                throw new RuntimeException("Method not found: " + delegate.getClass().getName() + "." + method.getName());
            }
        }

        /**
         *  resolves collisions when methods only differ on return types
         */
        private String getActualMethodName(Method method)
        {
            if (method.toGenericString().contains("List<org.raml.v2.api.model.v08.bodies.BodyLike> org.raml.v2.api.model.v08.methods.MethodBase.body()") ||
                method.toGenericString().contains("List<org.raml.v2.api.model.v08.bodies.BodyLike> org.raml.v2.api.model.v08.bodies.Response.body()"))
            {
                return "bodyV08";
            }
            if (method.toGenericString().contains("List<org.raml.v2.api.model.v08.api.GlobalSchema> org.raml.v2.api.model.v08.api.Api.schemas()"))
            {
                return "schemasV08";
            }
            return method.getName();
        }
    }

}
