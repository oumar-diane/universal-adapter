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
package org.zenithblox.support;

import org.zenithblox.ZwangineContext;
import org.zenithblox.util.FilePathResolver;
import org.zenithblox.util.FileUtil;

/**
 * Helper for resolving temp directory.
 */
public final class TempDirHelper {

    public static final String DEFAULT_PATTERN = "${java.io.tmpdir}/zwangine/zwangine-tmp-#uuid#";

    private TempDirHelper() {
    }

    /**
     * Resolves a temp dir using the default pattern.
     *
     * @param  zwangineContext the zwangine context
     * @param  path         the sub-dir for the temp dir
     * @return              the resolved temp dir
     */
    public static String resolveDefaultTempDir(ZwangineContext zwangineContext, String path) {
        return resolveTempDir(zwangineContext, DEFAULT_PATTERN, path);
    }

    /**
     * Resolves a temp dir
     *
     * @param  zwangineContext the zwangine context
     * @param  pattern      pattern for the base path of the temp dir
     * @param  path         the sub-dir for the temp dir
     * @return              the resolved temp dir
     */
    public static String resolveTempDir(ZwangineContext zwangineContext, String pattern, String path) {
        String answer;
        if (pattern != null && path != null) {
            path = pattern + "/" + path;
        } else if (path == null) {
            path = pattern;
        }
        if (zwangineContext.getManagementNameStrategy() != null) {
            String name = zwangineContext.getManagementNameStrategy().resolveManagementName(path, zwangineContext.getName(), false);
            if (name != null) {
                name = customResolveManagementName(zwangineContext, name);
            }
            // and then check again with invalid check to ensure all ## is resolved
            if (name != null) {
                name = zwangineContext.getManagementNameStrategy().resolveManagementName(name, zwangineContext.getName(), true);
            }
            answer = name;
        } else {
            answer = defaultManagementName(zwangineContext, path);
        }
        // remove double slashes
        answer = FileUtil.compactPath(answer);
        return answer;
    }

    private static String defaultManagementName(ZwangineContext zwangineContext, String path) {
        // must quote the names to have it work as literal replacement
        String name = zwangineContext.getName();

        // replace tokens
        String answer = path;
        answer = answer.replace("#zwangineId#", name);
        answer = answer.replace("#name#", name);
        // replace custom
        answer = customResolveManagementName(zwangineContext, answer);
        return answer;
    }

    private static String customResolveManagementName(ZwangineContext zwangineContext, String pattern) {
        if (pattern.contains("#uuid#")) {
            String uuid = zwangineContext.getUuidGenerator().generateUuid();
            pattern = pattern.replace("#uuid#", uuid);
        }
        return FilePathResolver.resolvePath(pattern);
    }

}
