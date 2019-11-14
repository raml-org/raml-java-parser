/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.tagresolver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created. There, you have it.
 */
public class ContextPathTest {

    @Test
    public void isURL() {

        assertTrue(ContextPath.isURL("file:///tmp/file"));
        assertTrue(ContextPath.isURL("http://funk.com/file"));
    }

    @Test
    public void isNotURL() {

        assertTrue(ContextPath.isURL("//tmp/file"));
        assertTrue(ContextPath.isURL("file"));
    }

}