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
package org.raml.v2.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public abstract class TestDataProvider
{

    protected File input;
    protected File expectedOutput;
    protected String name;

    protected String dump;
    protected String expected;

    @Rule
    public TestWatcher watchman = new TestWatcher()
    {
        @Override
        protected void failed(Throwable e, Description description)
        {
            System.out.println(StringUtils.repeat("=", 120));
            System.out.println("\ndump\n----\n" + dump);
            updateTests();
            System.out.println(StringUtils.repeat("=", 120));
        }

        private void updateTests()
        {
            if (System.getProperty("updateTests") != null)
            {
                try
                {
                    String path = expectedOutput.getPath();
                    path = path.replace("target/test-classes", "src/test/resources");
                    Files.write(Paths.get(path), dump.getBytes("UTF-8"));
                    String idx = "/test/resources";
                    System.out.println(StringUtils.repeat("-", 120));
                    System.out.println("---> rewriting output file: " + path.substring(path.indexOf(idx) + idx.length() + 1) + " <---");
                }
                catch (IOException ioe)
                {
                    throw new RuntimeException(ioe);
                }
            }
        }
    };


    public TestDataProvider(File input, File expectedOutput, String name)
    {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.name = name;
    }

    public static Collection<Object[]> getData(URI baseFolder, String inputFileName, String outputFileName) throws URISyntaxException
    {
        return scanPath(StringUtils.EMPTY, baseFolder, inputFileName, outputFileName);
    }

    private static List<Object[]> scanPath(String folderPath, URI baseFolder, String inputFileName, String outputFileName)
    {
        final File testFolder = new File(baseFolder);
        final File[] scenarios = testFolder.listFiles();
        List<Object[]> result = new ArrayList<>();
        for (File scenario : scenarios)
        {
            if (scenario.isDirectory())
            {
                File input = new File(scenario, inputFileName);
                File output = new File(scenario, outputFileName);
                if (input.isFile() && (output.isFile() || existsOutputIgnoreFile(scenario, outputFileName)))
                {
                    result.add(new Object[] {input, output, folderPath + scenario.getName()});
                }
                else if (scenario.listFiles().length > 0)
                {
                    result.addAll(scanPath(folderPath + scenario.getName() + ".", scenario.toURI(), inputFileName, outputFileName));
                }
            }
        }
        return result;
    }

    private static boolean existsOutputIgnoreFile(File scenario, String outputFileName)
    {
        return new File(scenario, outputFileName + ".ignore").isFile();
    }

    @Before
    public void ignoreTestIfAppropriate()
    {
        Assume.assumeFalse(existsOutputIgnoreFile(expectedOutput.getParentFile(), expectedOutput.getName()));
    }

    protected boolean jsonEquals(String produced, String expected)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.disableDefaultTyping();
            JsonNode beforeNode = filterNodes(mapper.readTree(expected));
            JsonNode afterNode = filterNodes(mapper.readTree(produced));
            JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
            String diffs = patch.toString();
            if ("[]".equals(diffs))
            {
                return true;
            }
            System.out.println("json diff: " + diffs);
            return false;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private JsonNode filterNodes(JsonNode jsonNode)
    {
        for (String filterNode : getKeysToFilter())
        {
            JsonNode parent;
            while ((parent = jsonNode.findParent(filterNode)) != null)
            {
                if (parent instanceof ObjectNode)
                {
                    JsonNode remove = ((ObjectNode) parent).remove(filterNode);
                    System.out.println("    >removed node \"" + filterNode + "\": " + remove);
                }
            }

        }

        return jsonNode;
    }

    protected String[] getKeysToFilter()
    {
        return new String[] {};
    }

}
