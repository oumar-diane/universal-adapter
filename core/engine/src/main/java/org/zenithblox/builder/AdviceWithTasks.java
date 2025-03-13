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
package org.zenithblox.builder;

import org.zenithblox.Endpoint;
import org.zenithblox.model.*;
import org.zenithblox.support.PatternHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * {@link AdviceWithTask} tasks which are used by the {@link AdviceWithWorkflowBuilder}.
 */
public final class AdviceWithTasks {

    private static final Logger LOG = LoggerFactory.getLogger(AdviceWithTasks.class);

    private AdviceWithTasks() {
        // utility class
    }

    /**
     * Match by is used for pluggable match by logic.
     */
    private interface MatchBy {

        String getId();

        boolean match(ProcessorDefinition<?> processor);
    }

    /**
     * Will match by id of the processor.
     */
    private static final class MatchById implements MatchBy {

        private final String id;

        private MatchById(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean match(ProcessorDefinition<?> processor) {
            if (id.equals("*")) {
                // make sure the processor which id isn't be set is matched.
                return true;
            }
            return PatternHelper.matchPattern(processor.getId(), id);
        }
    }

    /**
     * Will match by the to string representation of the processor.
     */
    private static final class MatchByToString implements MatchBy {

        private final String toString;

        private MatchByToString(String toString) {
            this.toString = toString;
        }

        @Override
        public String getId() {
            return toString;
        }

        @Override
        public boolean match(ProcessorDefinition<?> processor) {
            return PatternHelper.matchPattern(processor.toString(), toString);
        }
    }

    /**
     * Will match by the sending to endpoint uri representation of the processor.
     */
    private static final class MatchByToUri implements MatchBy {

        private final String toUri;

        private MatchByToUri(String toUri) {
            this.toUri = toUri;
        }

        @Override
        public String getId() {
            return toUri;
        }

        @Override
        public boolean match(ProcessorDefinition<?> processor) {
            if (processor instanceof EndpointRequiredDefinition endpointRequiredDefinition) {
                String uri = endpointRequiredDefinition.getEndpointUri();
                return PatternHelper.matchPattern(uri, toUri);
            } else if (processor instanceof ToDynamicDefinition toDynamicDefinition) {
                String uri = toDynamicDefinition.getUri();
                return PatternHelper.matchPattern(uri, toUri);
            } else if (processor instanceof EnrichDefinition enrichDefinition) {
                var exp = enrichDefinition.getExpression();
                if (exp != null) {
                    String uri = exp.getExpression();
                    return PatternHelper.matchPattern(uri, toUri);
                }
            } else if (processor instanceof PollEnrichDefinition pollEnrichDefinition) {
                var exp = pollEnrichDefinition.getExpression();
                if (exp != null) {
                    String uri = exp.getExpression();
                    return PatternHelper.matchPattern(uri, toUri);
                }
            }
            return false;
        }
    }

    /**
     * Will match by the type of the processor.
     */
    private static final class MatchByType implements MatchBy {

        private final Class<?> type;

        private MatchByType(Class<?> type) {
            this.type = type;
        }

        @Override
        public String getId() {
            return type.getSimpleName();
        }

        @Override
        public boolean match(ProcessorDefinition<?> processor) {
            return type.isAssignableFrom(processor.getClass());
        }
    }

    public static AdviceWithTask replaceByToString(
            final WorkflowDefinition workflow, final String toString, final ProcessorDefinition<?> replace, boolean selectFirst,
            boolean selectLast, int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByToString(toString);
        return doReplace(workflow, matchBy, replace, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask replaceByToUri(
            final WorkflowDefinition workflow, final String toUri, final ProcessorDefinition<?> replace, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByToUri(toUri);
        return doReplace(workflow, matchBy, replace, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask replaceById(
            final WorkflowDefinition workflow, final String id, final ProcessorDefinition<?> replace, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchById(id);
        return doReplace(workflow, matchBy, replace, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask replaceByType(
            final WorkflowDefinition workflow, final Class<?> type, final ProcessorDefinition<?> replace, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByType(type);
        return doReplace(workflow, matchBy, replace, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    private static AdviceWithTask doReplace(
            final WorkflowDefinition workflow, final MatchBy matchBy, final ProcessorDefinition<?> replace, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        return new AdviceWithTask() {
            public void task() throws Exception {
                Iterator<ProcessorDefinition<?>> it = AdviceWithTasks.createMatchByIterator(workflow, matchBy, selectFirst,
                        selectLast, selectFrom, selectTo, maxDeep);
                boolean match = false;
                while (it.hasNext()) {
                    ProcessorDefinition<?> output = it.next();
                    if (matchBy.match(output)) {
                        List<ProcessorDefinition<?>> outputs = getOutputs(output, workflow);
                        if (outputs != null) {
                            int index = outputs.indexOf(output);
                            if (index != -1) {
                                match = true;
                                // flattern as replace uses a workflow as temporary holder
                                ProcessorDefinition<?> flattern = flatternOutput(replace);
                                outputs.add(index + 1, flattern);
                                Object old = outputs.remove(index);
                                // must set parent on the node we added in the workflow
                                ProcessorDefinition<?> parent = output.getParent() != null ? output.getParent() : workflow;
                                flattern.setParent(parent);
                                LOG.info("AdviceWith ({}) : [{}] --> replace [{}]", matchBy.getId(), old, flattern);
                            }
                        }
                    }
                }

                if (!match) {
                    throw new IllegalArgumentException(
                            "There are no outputs which matches: " + matchBy.getId() + " in the workflow: " + workflow);
                }
            }
        };
    }

    public static AdviceWithTask removeByToString(
            final WorkflowDefinition workflow, final String toString, boolean selectFirst, boolean selectLast, int selectFrom,
            int selectTo,
            int maxDeep) {
        MatchBy matchBy = new MatchByToString(toString);
        return doRemove(workflow, matchBy, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask removeByToUri(
            final WorkflowDefinition workflow, final String toUri, boolean selectFirst, boolean selectLast, int selectFrom,
            int selectTo,
            int maxDeep) {
        MatchBy matchBy = new MatchByToUri(toUri);
        return doRemove(workflow, matchBy, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask removeById(
            final WorkflowDefinition workflow, final String id, boolean selectFirst, boolean selectLast, int selectFrom, int selectTo,
            int maxDeep) {
        MatchBy matchBy = new MatchById(id);
        return doRemove(workflow, matchBy, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask removeByType(
            final WorkflowDefinition workflow, final Class<?> type, boolean selectFirst, boolean selectLast, int selectFrom,
            int selectTo,
            int maxDeep) {
        MatchBy matchBy = new MatchByType(type);
        return doRemove(workflow, matchBy, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    private static AdviceWithTask doRemove(
            final WorkflowDefinition workflow, final MatchBy matchBy, boolean selectFirst, boolean selectLast, int selectFrom,
            int selectTo, int maxDeep) {
        return new AdviceWithTask() {
            public void task() throws Exception {
                boolean match = false;
                Iterator<ProcessorDefinition<?>> it = AdviceWithTasks.createMatchByIterator(workflow, matchBy, selectFirst,
                        selectLast, selectFrom, selectTo, maxDeep);
                while (it.hasNext()) {
                    ProcessorDefinition<?> output = it.next();
                    if (matchBy.match(output)) {
                        List<ProcessorDefinition<?>> outputs = getOutputs(output, workflow);
                        if (outputs != null) {
                            int index = outputs.indexOf(output);
                            if (index != -1) {
                                match = true;
                                Object old = outputs.remove(index);
                                LOG.info("AdviceWith ({}) : [{}] --> remove", matchBy.getId(), old);
                            }
                        }
                    }
                }

                if (!match) {
                    throw new IllegalArgumentException(
                            "There are no outputs which matches: " + matchBy.getId() + " in the workflow: " + workflow);
                }
            }
        };
    }

    public static AdviceWithTask beforeByToString(
            final WorkflowDefinition workflow, final String toString, final ProcessorDefinition<?> before, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByToString(toString);
        return doBefore(workflow, matchBy, before, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask beforeByToUri(
            final WorkflowDefinition workflow, final String toUri, final ProcessorDefinition<?> before, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByToUri(toUri);
        return doBefore(workflow, matchBy, before, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask beforeById(
            final WorkflowDefinition workflow, final String id, final ProcessorDefinition<?> before, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchById(id);
        return doBefore(workflow, matchBy, before, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask beforeByType(
            final WorkflowDefinition workflow, final Class<?> type, final ProcessorDefinition<?> before, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByType(type);
        return doBefore(workflow, matchBy, before, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    private static AdviceWithTask doBefore(
            final WorkflowDefinition workflow, final MatchBy matchBy, final ProcessorDefinition<?> before, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        return new AdviceWithTask() {
            public void task() throws Exception {
                boolean match = false;
                Iterator<ProcessorDefinition<?>> it = AdviceWithTasks.createMatchByIterator(workflow, matchBy, selectFirst,
                        selectLast, selectFrom, selectTo, maxDeep);
                while (it.hasNext()) {
                    ProcessorDefinition<?> output = it.next();
                    if (matchBy.match(output)) {
                        List<ProcessorDefinition<?>> outputs = getOutputs(output, workflow);
                        if (outputs != null) {
                            int index = outputs.indexOf(output);
                            if (index != -1) {
                                match = true;
                                // flattern as before uses a workflow as temporary holder
                                ProcessorDefinition<?> flattern = flatternOutput(before);
                                Object existing = outputs.get(index);
                                outputs.add(index, flattern);
                                // must set parent on the node we added in the workflow
                                ProcessorDefinition<?> parent = output.getParent() != null ? output.getParent() : workflow;
                                flattern.setParent(parent);
                                LOG.info("AdviceWith ({}) : [{}] --> before [{}]", matchBy.getId(), existing, flattern);
                            }
                        }
                    }
                }

                if (!match) {
                    throw new IllegalArgumentException(
                            "There are no outputs which matches: " + matchBy.getId() + " in the workflow: " + workflow);
                }
            }
        };
    }

    public static AdviceWithTask afterByToString(
            final WorkflowDefinition workflow, final String toString, final ProcessorDefinition<?> after, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByToString(toString);
        return doAfter(workflow, matchBy, after, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask afterByToUri(
            final WorkflowDefinition workflow, final String toUri, final ProcessorDefinition<?> after, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByToUri(toUri);
        return doAfter(workflow, matchBy, after, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask afterById(
            final WorkflowDefinition workflow, final String id, final ProcessorDefinition<?> after, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchById(id);
        return doAfter(workflow, matchBy, after, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    public static AdviceWithTask afterByType(
            final WorkflowDefinition workflow, final Class<?> type, final ProcessorDefinition<?> after, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        MatchBy matchBy = new MatchByType(type);
        return doAfter(workflow, matchBy, after, selectFirst, selectLast, selectFrom, selectTo, maxDeep);
    }

    private static AdviceWithTask doAfter(
            final WorkflowDefinition workflow, final MatchBy matchBy, final ProcessorDefinition<?> after, boolean selectFirst,
            boolean selectLast,
            int selectFrom, int selectTo, int maxDeep) {
        return new AdviceWithTask() {
            public void task() throws Exception {
                boolean match = false;
                Iterator<ProcessorDefinition<?>> it = AdviceWithTasks.createMatchByIterator(workflow, matchBy, selectFirst,
                        selectLast, selectFrom, selectTo, maxDeep);
                while (it.hasNext()) {
                    ProcessorDefinition<?> output = it.next();
                    if (matchBy.match(output)) {
                        List<ProcessorDefinition<?>> outputs = getOutputs(output, workflow);
                        if (outputs != null) {
                            int index = outputs.indexOf(output);
                            if (index != -1) {
                                match = true;
                                // flattern as after uses a workflow as temporary holder
                                ProcessorDefinition<?> flattern = flatternOutput(after);
                                Object existing = outputs.get(index);
                                outputs.add(index + 1, flattern);
                                // must set parent on the node we added in the workflow
                                ProcessorDefinition<?> parent = output.getParent() != null ? output.getParent() : workflow;
                                flattern.setParent(parent);
                                LOG.info("AdviceWith ({}) : [{}] --> after [{}]", matchBy.getId(), existing, flattern);
                            }
                        }
                    }
                }

                if (!match) {
                    throw new IllegalArgumentException(
                            "There are no outputs which matches: " + matchBy.getId() + " in the workflow: " + workflow);
                }
            }
        };
    }

    /**
     * Gets the outputs to use with advice with from the given child/parent
     * <p/>
     * This implementation deals with that outputs can be abstract and retrieves the <i>correct</i> parent output.
     *
     * @param  node the node
     * @return      <tt>null</tt> if not outputs to be used
     */
    private static List<ProcessorDefinition<?>> getOutputs(ProcessorDefinition<?> node, WorkflowDefinition workflow) {
        if (node == null) {
            return null;
        }
        // for intercept/onException/onCompletion then we want to work on the
        // workflow outputs as they are top-level
        if (node instanceof InterceptDefinition) {
            return workflow.getOutputs();
        } else if (node instanceof InterceptSendToEndpointDefinition) {
            return workflow.getOutputs();
        } else if (node instanceof OnExceptionDefinition) {
            return workflow.getOutputs();
        } else if (node instanceof OnCompletionDefinition) {
            return workflow.getOutputs();
        }

        ProcessorDefinition<?> parent = node.getParent();
        if (parent == null) {
            return null;
        }
        // for CBR then use the outputs from the node itself
        // so we work on the right branch in the CBR (when/otherwise)
        if (parent instanceof ChoiceDefinition choice) {
            // look in which branch the node is from
            for (WhenDefinition when : choice.getWhenClauses()) {
                if (when.getOutputs().contains(node)) {
                    return when.getOutputs();
                }
            }
            if (choice.getOtherwise() != null) {
                return choice.getOtherwise().getOutputs();
            }
        }
        List<ProcessorDefinition<?>> outputs = parent.getOutputs();
        boolean allAbstract = true;
        for (ProcessorDefinition<?> def : outputs) {
            allAbstract = def.isAbstract();
            if (!allAbstract) {
                break;
            }
        }
        if (!outputs.isEmpty() && allAbstract) {
            // if all outputs are abstract then get its last output, as
            outputs = outputs.get(outputs.size() - 1).getOutputs();
        }
        return outputs;
    }

    public static AdviceWithTask replaceFromWith(final WorkflowDefinition workflow, final String uri) {
        return new AdviceWithTask() {
            public void task() throws Exception {
                FromDefinition from = workflow.getInput();
                LOG.info("AdviceWith replace input from [{}] --> [{}]", from.getEndpointUri(), uri);
                from.setEndpoint(null);
                from.setUri(uri);
            }
        };
    }

    public static AdviceWithTask replaceFrom(final WorkflowDefinition workflow, final Endpoint endpoint) {
        return new AdviceWithTask() {
            public void task() throws Exception {
                FromDefinition from = workflow.getInput();
                LOG.info("AdviceWith replace input from [{}] --> [{}]", from.getEndpointUri(), endpoint.getEndpointUri());
                from.setUri(null);
                from.setEndpoint(endpoint);
            }
        };
    }

    /**
     * Create iterator which walks the workflow, and only returns nodes which matches the given set of criteria.
     *
     * @param  workflow       the workflow
     * @param  matchBy     match by which must match
     * @param  selectFirst optional to select only the first
     * @param  selectLast  optional to select only the last
     * @param  selectFrom  optional to select index/range
     * @param  selectTo    optional to select index/range
     * @param  maxDeep     maximum levels deep (is unbounded by default)
     * @return             the iterator
     */
    private static Iterator<ProcessorDefinition<?>> createMatchByIterator(
            final WorkflowDefinition workflow, final MatchBy matchBy, final boolean selectFirst, final boolean selectLast,
            final int selectFrom, final int selectTo, int maxDeep) {

        // first iterator and apply match by
        List<ProcessorDefinition<?>> matched = new ArrayList<>();

        List<ProcessorDefinition<?>> outputs = new ArrayList<>();

        // if we are in first|last mode then we should
        // skip abstract nodes in the beginning as they are cross cutting
        // functionality such as onException, onCompletion etc
        // and the user want to select first or last outputs in the workflow (not
        // cross cutting functionality)
        boolean skip = selectFirst || selectLast;

        for (ProcessorDefinition<?> output : workflow.getOutputs()) {
            // special for transacted, which we need to unwrap
            if (output instanceof TransactedDefinition) {
                outputs.addAll(output.getOutputs());
            } else if (skip) {
                boolean invalid = outputs.isEmpty() && output.isAbstract();
                if (!invalid) {
                    outputs.add(output);
                }
            } else {
                outputs.add(output);
            }
        }

        @SuppressWarnings("rawtypes")
        Collection<ProcessorDefinition> all
                = ProcessorDefinitionHelper.filterTypeInOutputs(outputs, ProcessorDefinition.class, maxDeep);
        for (ProcessorDefinition<?> proc : all) {
            if (matchBy.match(proc)) {
                matched.add(proc);
            }
        }

        // and then apply the selector iterator
        return createSelectorIterator(matched, selectFirst, selectLast, selectFrom, selectTo);
    }

    private static Iterator<ProcessorDefinition<?>> createSelectorIterator(
            final List<ProcessorDefinition<?>> list, final boolean selectFirst, final boolean selectLast,
            final int selectFrom, final int selectTo) {
        return new Iterator<>() {
            private int current;
            private boolean done;

            @Override
            public boolean hasNext() {
                if (list.isEmpty() || done) {
                    return false;
                }

                if (selectFirst) {
                    done = true;
                    // spool to first
                    current = 0;
                    return true;
                }

                if (selectLast) {
                    done = true;
                    // spool to last
                    current = list.size() - 1;
                    return true;
                }

                if (selectFrom >= 0 && selectTo >= 0) {
                    // check for out of bounds
                    if (selectFrom >= list.size() || selectTo >= list.size()) {
                        return false;
                    }
                    if (current < selectFrom) {
                        // spool to beginning of range
                        current = selectFrom;
                    }
                    return current <= selectTo;
                }

                return current < list.size();
            }

            @Override
            public ProcessorDefinition<?> next() {
                ProcessorDefinition<?> answer = list.get(current);
                current++;
                return answer;
            }

            @Override
            public void remove() {
                // noop
            }
        };
    }

    private static ProcessorDefinition<?> flatternOutput(ProcessorDefinition<?> output) {
        if (output instanceof AdviceWithDefinition advice) {
            if (advice.getOutputs().size() == 1) {
                return advice.getOutputs().get(0);
            } else {
                // it should be a workflow
                WorkflowDefinition pipe = new WorkflowDefinition();
                pipe.setOutputs(advice.getOutputs());
                return pipe;
            }
        }
        return output;
    }

}
