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
package org.zenithblox.support.startup;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.StartupCondition;
import org.zenithblox.util.ObjectHelper;

import java.io.File;

/**
 * Startup condition that waits for a file to exists.
 */
public class FileStartupCondition implements StartupCondition {

    private final File file;

    public FileStartupCondition(String name) {
        ObjectHelper.notNullOrEmpty(name, "File");
        this.file = new File(name);
    }

    @Override
    public String getName() {
        return "File";
    }

    @Override
    public String getWaitMessage() {
        return "Waiting for file: " + file;
    }

    @Override
    public String getFailureMessage() {
        return "File: " + file + " does not exist";
    }

    protected boolean fileExists(File file) {
        return file.exists();
    }

    @Override
    public boolean canContinue(ZwangineContext zwangineContext) throws Exception {
        return fileExists(file);
    }

}
