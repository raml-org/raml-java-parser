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
package org.raml.v2.internal.utils;

import java.util.Map.Entry;

public class Pair<L, R>
{

    private L left;
    private R right;

    public Pair(L left, R right)
    {
        super();
        this.left = left;
        this.right = right;
    }


    public static <A, B> Pair<A, B> of(A left, B right)
    {
        return new Pair<A, B>(left, right);
    }

    public static <A, B> Pair<A, B> fromEntry(Entry<A, B> entry)
    {
        return Pair.of(entry.getKey(), entry.getValue());
    }

    public L getLeft()
    {
        return left;
    }

    public R getRight()
    {
        return right;
    }


    @Override
    public String toString()
    {
        return "Pair [left=" + left + ", right=" + right + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (left == null)
        {
            if (other.left != null)
                return false;
        }
        else if (!left.equals(other.left))
            return false;
        if (right == null)
        {
            if (other.right != null)
                return false;
        }
        else if (!right.equals(other.right))
            return false;
        return true;
    }


}
