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
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Folder property which adds environment variables to a folder.
 */
public class EnvVarsFolderProperty extends FolderProperty<Folder> {

    private String properties;
    private transient Map<String, String> propertyMap;

    @DataBoundConstructor
    public EnvVarsFolderProperty(String properties) throws IOException {
        setProperties(properties);
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
        this.propertyMap = null;
    }

    public Map<String, String> getPropertyMap() throws IOException {
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