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