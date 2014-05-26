/*
 * The MIT License
 *
 * Copyright 2013 Wilco Greven.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.wgreven.jenkins.envvarsfolderproperty;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.EnvVars;
import hudson.model.*;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EnvVarsFolderPropertyTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testEnvironmentProperty() throws Exception {
        Folder folder = jenkinsRule.jenkins.createProject(Folder.class, "folder");
        String env = "TEST_VAR=TEST_VALUE";
        folder.addProperty(new EnvVarsFolderProperty(env));
        FreeStyleProject project = folder.createProject(FreeStyleProject.class, "project");
        assertEquals("TEST_VALUE", project.getEnvironment(jenkinsRule.jenkins, TaskListener.NULL).get("TEST_VAR"));
    }

    @Test
    public void testEnvironmentProperty_nestedFolders() throws Exception {
        Folder folder1 = jenkinsRule.jenkins.createProject(Folder.class, "folder1");
        String env = "TEST_VAR=TEST_VALUE";
        folder1.addProperty(new EnvVarsFolderProperty(env));
        Folder folder2 = folder1.createProject(Folder.class, "folder2");
        FreeStyleProject project = folder2.createProject(FreeStyleProject.class, "project");
        assertEquals("TEST_VALUE", project.getEnvironment(jenkinsRule.jenkins, TaskListener.NULL).get("TEST_VAR"));
    }

    @Test
    public void testEnvironmentProperty_nestedFolders_overriddenProperty() throws Exception {
        Folder folder1 = jenkinsRule.jenkins.createProject(Folder.class, "folder1");
        String env1 = "TEST_VAR=TEST_VALUE1";
        folder1.addProperty(new EnvVarsFolderProperty(env1));
        Folder folder2 = folder1.createProject(Folder.class, "folder2");
        String env2 = "TEST_VAR=TEST_VALUE2";
        folder2.addProperty(new EnvVarsFolderProperty(env2));
        FreeStyleProject project = folder2.createProject(FreeStyleProject.class, "project");
        assertEquals("TEST_VALUE2", project.getEnvironment(jenkinsRule.jenkins, TaskListener.NULL).get("TEST_VAR"));
    }
}