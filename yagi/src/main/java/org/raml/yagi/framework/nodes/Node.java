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

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Node
{

    /**
     * The start position of this node
     * @return The position
     */
    @Nonnull
    Position getStartPosition();

    /**
     * The end position of this node
     * @return The position
     */
    @Nonnull
    Position getEndPosition();

    /**
     * The ancestor that does not has any parent. It is the first node that it parent returns null in the parent path.
     * @return The root node.
     */
    Node getRootNode();

    /**
     * The parent of this node
     * @return The parent
     */
    @Nullable
    Node getParent();

    /**
     * All the children
     * @return The Children
     */
    @Nonnull
    List<Node> getChildren();

    /**
     * Adds a new child.  The new child is going to have this as a parent
     * @param node Adds a new child to this node
     */
    void addChild(Node node);

    /**
     * Removes a child.  The new child is going to have this as a parent
     * @param node Adds a new child to this node
     */
    void removeChild(Node node);

    /**
     * Sets the parent node to this node.
     * @param parent The new parent node
     */
    void setParent(Node parent);

    /**
     * The source of this node. This is used at transformation phases when the original Yaml node are being replaced by a more specialized node.
     * This way the node specialization can be tracked on all the history changes.
     * @param source The original node.
     */
    void setSource(Node source);

    /**
     * Returns the list of descendants nodes that are instances of the specified class
     *
     * @param nodeType The class that the node should implement
     * @param <T>      The type of the class
     * @return The matching types
     */
    @Nonnull
    <T extends Node> List<T> findDescendantsWith(Class<T> nodeType);

    /**
     * Returns the nearest ancestor node that is instance of the specified class
     *
     * @param nodeType The class that the node should implement
     * @param <T>      The type of the class
     * @return The matching type or null if none
     */
    @Nullable
    <T extends Node> T findAncestorWith(Class<T> nodeType);

    /**
     * Return the source of this node.
     * @return The source cause.
     */
    @Nullable
    Node getSource();

    /**
     * Returns the child node that matches the desired selector or <code>null</code> if there is no match 
     */
    @Nullable
    Node get(String selector);

    /**
     * Replace this node in the tree with a new specialized node. The new node will have as a source this node.
     * @param newNode The new node
     */
    void replaceWith(Node newNode);

    /**
     * Sets a child at a specified index. The new child is going to have this as a parent
     * @param idx The index
     * @param newNode The new child
     */
    void setChild(int idx, Node newNode);

    /**
     * Adds a child at a specified index. The new child is going to have this as a parent
     * @param idx The index
     * @param newNode The new child
     */
    void addChild(int idx, Node newNode);

    /**
     * Creates a new copy of this node
     * @return a new copy of this node
     */
    @Nonnull
    Node copy();

    /**
     * Returns the type of this node
     * @return The node type
     */
    NodeType getType();

    /**
     * Removes all the children of this given node.
     */
    void removeChildren();

    /**
     * Adds an annotation to this node
     * @param annotation The annotation
     */
    void annotate(NodeAnnotation annotation);

    /**
     * Returns all the annotations of this node
     * @return All the annotations
     */
    Collection<NodeAnnotation> annotations();

    /**
     * Replace this subtree with the given one. Current children are not going to be transferred to the new one
     * @param newSubTree The new subtree
     */
    void replaceTree(Node newSubTree);

    /**
     * Sets the context node. This is used to provide a global context when parsing an isolated fragment, i.e without
     * parent node.
     * @param node The root node.
     */
    void setContextNode(Node node);
}
