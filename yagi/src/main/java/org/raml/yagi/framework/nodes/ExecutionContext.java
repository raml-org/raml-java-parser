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
package org.raml.yagi.framework.nodes;

import javax.annotation.Nullable;
import java.util.Map;

public class ExecutionContext
{

    private Map<String, Node> parameters;
    private Node contextNode;

    public ExecutionContext(Map<String, Node> parameters, Node contextNode)
    {
        this.parameters = parameters;
        this.contextNode = contextNode;
    }

    public void addVariable(String name, Node value)
    {
        parameters.put(name, value);
    }

    @Nullable
    public Node getVariable(String name)
    {
        return parameters.get(name);
    }

    public boolean containsVariable(String variable)
    {
        return parameters.containsKey(variable);
    }

    public Node getContextNode()
    {
        return contextNode;
    }
}
