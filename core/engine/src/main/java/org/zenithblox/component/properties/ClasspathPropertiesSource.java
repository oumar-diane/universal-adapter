/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.component.properties;

import org.zenithblox.Ordered;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.OrderedProperties;

import java.io.*;
import java.util.Properties;

public class ClasspathPropertiesSource extends AbstractLocationPropertiesSource implements Ordered {

    private final int order;

    public ClasspathPropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        this(propertiesComponent, location, 400);
    }

    public ClasspathPropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location, int order) {
        super(propertiesComponent, location);
        this.order = order;
    }

    @Override
    public String getName() {
        return "ClasspathPropertiesSource[" + getLocation().getPath() + "]";
    }

    @Override
    public Properties loadPropertiesFromLocation(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        Properties answer = new OrderedProperties();
        String path = location.getPath();

        InputStream is = propertiesComponent.getZwangineContext().getClassResolver().loadResourceAsStream(path);
        Reader reader = null;
        if (is == null) {
            if (!propertiesComponent.isIgnoreMissingLocation() && !location.isOptional()) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(
                        new FileNotFoundException("Properties file " + path + " not found in classpath"));
            }
        } else {
            try {
                if (propertiesComponent.getEncoding() != null) {
                    reader = new BufferedReader(new InputStreamReader(is, propertiesComponent.getEncoding()));
                    answer.load(reader);
                } else {
                    answer.load(is);
                }
            } catch (IOException e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            } finally {
                IOHelper.close(reader, is);
            }
        }
        return answer;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
