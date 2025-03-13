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

public class FilePropertiesSource extends AbstractLocationPropertiesSource implements Ordered {

    private final int order;

    protected FilePropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        this(propertiesComponent, location, 300);
    }

    protected FilePropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location, int order) {
        super(propertiesComponent, location);
        this.order = order;
    }

    @Override
    public String getName() {
        return "FilePropertiesSource[" + getLocation().getPath() + "]";
    }

    @Override
    public Properties loadPropertiesFromLocation(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        Properties answer = new OrderedProperties();
        String path = location.getPath();

        Reader reader = null;
        try (InputStream is = new FileInputStream(path)) {
            if (propertiesComponent.getEncoding() != null) {
                reader = new BufferedReader(new InputStreamReader(is, propertiesComponent.getEncoding()));
                answer.load(reader);
            } else {
                answer.load(is);
            }
        } catch (FileNotFoundException e) {
            if (!propertiesComponent.isIgnoreMissingLocation() && !location.isOptional()) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        } catch (IOException e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        } finally {
            IOHelper.close(reader);
        }

        return answer;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
