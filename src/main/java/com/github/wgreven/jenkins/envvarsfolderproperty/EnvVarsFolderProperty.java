/*
 * The MIT License
 *
 * Copyright (c) 2013 Wilco Greven
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
import com.cloudbees.hudson.plugins.folder.FolderProperty;
import com.cloudbees.hudson.plugins.folder.FolderPropertyDescriptor;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentContributor;
import hudson.model.ItemGroup;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Folder property which adds environment variables to a folder.
 */
public class EnvVarsFolderProperty extends FolderProperty<Folder> {

    public final String properties;

    private transient Map<String, String> propertyMap;

    @DataBoundConstructor
    public EnvVarsFolderProperty(String properties) {
        this.properties = properties;
    }

    private Map<String, String> getPropertyMap() throws IOException {
        Map<String, String> result = propertyMap;
        if (result == null) {
            result = mapFromProperties(propertiesFromString(properties));
            propertyMap = result;
        }
        return result;
    }

    @Extension
    public static class DescriptorImpl extends FolderPropertyDescriptor {

        @Override
        public String getDisplayName() {
            return "Environment Variables";
        }
    }

    /**
     * Contributes environment variables set in folders to builds inside these folders.
     */
    @Extension
    public static class EnvironmentContributorImpl extends EnvironmentContributor {

        @Override
        public void buildEnvironmentFor(Run run, EnvVars envVars, TaskListener taskListener) throws IOException, InterruptedException {
            addFolderEnvironment(envVars, run.getParent().getParent());
        }

        private void addFolderEnvironment(EnvVars envVars, ItemGroup itemGroup) throws IOException, InterruptedException {
            if (itemGroup instanceof Folder) {
                Folder folder = (Folder) itemGroup;
                addFolderEnvironment(envVars, folder.getParent());
                envVars.putAll(getFolderEnvironment(folder));
            }
        }

        private Map<String,String> getFolderEnvironment(Folder folder) throws IOException {
            EnvVarsFolderProperty folderProperty = folder.getProperties().get(EnvVarsFolderProperty.class);
            if (folderProperty != null) {
                return folderProperty.getPropertyMap();
            } else {
                return Collections.emptyMap();
            }
        }
    }

    private static Properties propertiesFromString(String input) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(input));
        return properties;
    }

    private static Map<String, String> mapFromProperties(Properties properties) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        Enumeration e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            map.put(key, properties.getProperty(key));
        }
        return map;
    }
}