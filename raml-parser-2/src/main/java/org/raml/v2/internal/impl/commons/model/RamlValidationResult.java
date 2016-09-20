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
package org.raml.v2.internal.impl.commons.model;

import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Position;

public class RamlValidationResult implements org.raml.v2.api.model.common.ValidationResult
{

    private String message;
    private String errorPath;
    private Position start;
    private Position end;

    public RamlValidationResult(String message)
    {
        this.message = message;
    }

    public RamlValidationResult(ErrorNode errorNode)
    {
        this.message = errorNode.getErrorMessage();
        this.errorPath = errorNode.getPath();
        this.start = errorNode.getStartPosition();
        this.end = errorNode.getEndPosition();
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    @Override
    public String getPath()
    {
        return errorPath;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(message);
        if (start != null)
        {
            if (start.getLine() != Position.UNKNOWN)
            {
                builder.append(" -- ").append(start.getPath());
                builder.append(" [line=").append(start.getLine() + 1)
                       .append(", col=").append(start.getColumn() + 1).append("]");
            }
        }
        return builder.toString();
    }

}
