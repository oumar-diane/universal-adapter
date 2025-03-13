/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.model;

import org.zenithblox.support.PatternHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Used for filtering workflows to only include workflows matching a function.
 */
public final class WorkflowFilters implements Function<WorkflowDefinition, Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowFilters.class);

    private final String includesText;
    private final String excludesText;
    private final String[] includes;
    private final String[] excludes;

    private WorkflowFilters(String include, String exclude) {
        this.includesText = include;
        this.excludesText = exclude;
        this.includes = include != null ? include.split(",") : null;
        this.excludes = exclude != null ? exclude.split(",") : null;
    }

    /**
     * Used for filtering workflows workflows matching the given pattern, which follows the following rules: - Match by workflow
     * id - Match by workflow input endpoint uri The matching is using exact match, by wildcard and regular expression as
     * documented by {@link PatternHelper#matchPattern(String, String)}. For example to only include workflows which starts
     * with foo in their workflow id's, use: include=foo&#42; And to exclude workflows which starts from JMS endpoints, use:
     * exclude=jms:&#42; Multiple patterns can be separated by comma, for example to exclude both foo and bar workflows,
     * use: exclude=foo&#42;,bar&#42; Exclude takes precedence over include.
     *
     * @param include the include pattern
     * @param exclude the exclude pattern
     */
    public static Function<WorkflowDefinition, Boolean> filterByPattern(String include, String exclude) {
        return new WorkflowFilters(include, exclude);
    }

    @Override
    public Boolean apply(WorkflowDefinition workflow) {
        String id = workflow.getId();
        String uri = workflow.getInput() != null ? workflow.getInput().getEndpointUri() : null;

        boolean answer = filter(id, uri);
        LOG.debug("Workflow filter: include={}, exclude={}, id={}, from={} -> {}", includesText, excludesText, id, uri, answer);
        return answer;
    }

    private boolean filter(String id, String uri) {
        boolean match = false;

        // exclude takes precedence
        if (excludes != null) {
            for (String part : excludes) {
                if (PatternHelper.matchPattern(id, part) || PatternHelper.matchPattern(uri, part)) {
                    return false;
                }
            }
        }

        if (includes != null) {
            for (String part : includes) {
                if (PatternHelper.matchPattern(id, part) || PatternHelper.matchPattern(uri, part)) {
                    match = true;
                    break;
                }
            }
        } else {
            // if include has not been set then, we assume its matched as it was
            // not excluded
            match = true;
        }

        return match;
    }

}
