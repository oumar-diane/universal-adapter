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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A context path matcher when using rest-dsl that allows components to reuse the same matching logic.
 * <p/>
 * The component should use the {@link #matchBestPath(String, String, List)} with the request details and the matcher
 * returns the best matched, or <tt>null</tt> if none could be determined.
 * <p/>
 * The {@link ConsumerPath} is used for the components to provide the details to the matcher.
 */
public final class RestConsumerContextPathMatcher {

    private static final Pattern CONSUMER_PATH_PARAMETER_PATTERN = Pattern.compile("([^{]*)(\\{.*?\\})([^{]*)");
    private static final Map<String, Pattern> PATH_PATTERN = new ConcurrentHashMap<>();

    private RestConsumerContextPathMatcher() {
    }

    /**
     * Consumer path details which must be implemented and provided by the components.
     */
    public interface ConsumerPath<T> {

        /**
         * Any HTTP restrict method that would not be allowed
         */
        String getRestrictMethod();

        /**
         * The consumer context-path which may include wildcards
         */
        String getConsumerPath();

        /**
         * The consumer implementation
         */
        T getConsumer();

        /**
         * Whether the consumer match on uri prefix
         */
        boolean isMatchOnUriPrefix();

    }

    /**
     * Does the incoming request match the given consumer path (ignore case)
     *
     * @param  requestPath      the incoming request context path
     * @param  consumerPath     a consumer path
     * @param  matchOnUriPrefix whether to use the matchOnPrefix option
     * @return                  <tt>true</tt> if matched, <tt>false</tt> otherwise
     */
    public static boolean matchPath(String requestPath, String consumerPath, boolean matchOnUriPrefix) {
        // deal with null parameters
        if (requestPath == null && consumerPath == null) {
            return true;
        }
        if (requestPath == null || consumerPath == null) {
            return false;
        }

        // remove starting/ending slashes
        requestPath = removePathSlashes(requestPath);
        // remove starting/ending slashes
        consumerPath = removePathSlashes(consumerPath);

        if (matchOnUriPrefix && requestPath.toLowerCase(Locale.ENGLISH).startsWith(consumerPath.toLowerCase(Locale.ENGLISH))) {
            return true;
        }

        if (requestPath.equalsIgnoreCase(consumerPath)) {
            return true;
        }

        return false;
    }

    /**
     * Finds the best matching of the list of consumer paths that should service the incoming request.
     *
     * @param  requestMethod the incoming request HTTP method
     * @param  requestPath   the incoming request context path
     * @param  consumerPaths the list of consumer context path details
     * @return               the best matched consumer, or <tt>null</tt> if none could be determined.
     */
    public static <
            T> ConsumerPath<T> matchBestPath(String requestMethod, String requestPath, List<ConsumerPath<T>> consumerPaths) {
        ConsumerPath<T> answer = null;

        List<ConsumerPath<T>> candidates = new ArrayList<>();

        // first match by http method
        for (ConsumerPath<T> entry : consumerPaths) {
            if (matchRestMethod(requestMethod, entry.getRestrictMethod())) {
                candidates.add(entry);
            }
        }

        // then see if we got a direct match
        Iterator<ConsumerPath<T>> it = candidates.iterator();
        while (it.hasNext()) {
            ConsumerPath<T> consumer = it.next();
            if (matchRestPath(requestPath, consumer.getConsumerPath(), false)) {
                answer = consumer;
                break;
            }
        }

        // we could not find a direct match, and if the request is OPTIONS then we need all candidates
        if (answer == null && isOptionsMethod(requestMethod)) {
            candidates.clear();
            candidates.addAll(consumerPaths);

            // then try again to see if we can find a direct match
            it = candidates.iterator();
            while (it.hasNext()) {
                ConsumerPath<T> consumer = it.next();
                if (matchRestPath(requestPath, consumer.getConsumerPath(), false)) {
                    answer = consumer;
                    break;
                }
            }
        }

        // if there are no uri template, then select the matching with the longest path
        boolean noCurlyBraces = candidates.stream().allMatch(p -> countCurlyBraces(p.getConsumerPath()) == 0);
        if (noCurlyBraces) {
            // grab first which is the longest that matched the request path
            answer = candidates.stream()
                    .filter(c -> matchPath(requestPath, c.getConsumerPath(), c.isMatchOnUriPrefix()))
                    // sort by longest by inverting the sort by multiply with -1
                    .sorted(Comparator.comparingInt(o -> -1 * o.getConsumerPath().length())).findFirst().orElse(null);
        }

        // is there a direct match by with a different VERB, as then this call is not allowed
        if (answer == null) {
            for (ConsumerPath<T> entry : consumerPaths) {
                if (matchRestPath(requestPath, entry.getConsumerPath(), false)) {
                    // okay we have direct match but for another VERB so this call is not allowed
                    return null;
                }
            }
        }

        if (answer != null) {
            return answer;
        }

        // then match by uri template path
        it = candidates.iterator();
        List<ConsumerPath<T>> uriTemplateCandidates = new ArrayList<>();
        while (it.hasNext()) {
            ConsumerPath<T> consumer = it.next();
            // filter non matching paths
            if (matchRestPath(requestPath, consumer.getConsumerPath(), true)) {
                uriTemplateCandidates.add(consumer);
            }
        }

        // if there is multiple candidates with uri template then pick anyone with the least number of uri template
        ConsumerPath<T> best = null;
        Map<Integer, List<ConsumerPath<T>>> pathMap = new HashMap<>();
        if (uriTemplateCandidates.size() > 1) {
            it = uriTemplateCandidates.iterator();
            while (it.hasNext()) {
                ConsumerPath<T> entry = it.next();
                int curlyBraces = countCurlyBraces(entry.getConsumerPath());
                if (curlyBraces > 0) {
                    List<ConsumerPath<T>> consumerPathsList = pathMap.computeIfAbsent(curlyBraces, key -> new ArrayList<>());
                    consumerPathsList.add(entry);
                }
            }

            OptionalInt min = pathMap.keySet().stream().mapToInt(Integer::intValue).min();
            if (min.isPresent()) {
                List<ConsumerPath<T>> bestConsumerPaths = pathMap.get(min.getAsInt());
                if (bestConsumerPaths.size() > 1 && !canBeAmbiguous(requestMethod, requestMethod)) {
                    String exceptionMsg = "Ambiguous paths " + bestConsumerPaths.stream().map(ConsumerPath::getConsumerPath)
                            .collect(Collectors.joining(",")) + " for request path " + requestPath;
                    throw new IllegalStateException(exceptionMsg);
                }
                best = bestConsumerPaths.get(0);
            }

            if (best != null) {
                // pick the best among uri template
                answer = best;
            }
        }

        // if there is one left then it's our answer
        if (answer == null && uriTemplateCandidates.size() == 1) {
            return uriTemplateCandidates.get(0);
        }

        // last match by wildcard path
        it = candidates.iterator();
        while (it.hasNext()) {
            ConsumerPath<T> consumer = it.next();
            // filter non matching paths
            if (matchWildCard(requestPath, consumer.getConsumerPath())) {
                answer = consumer;
                break;
            }
        }

        return answer;
    }

    /**
     * Pre-compiled consumer path for wildcard match
     *
     * @param consumerPath a consumer path
     */
    public static void register(String consumerPath) {
        // Remove hyphens and underscores from parameter names
        // as these are not supported in regex named group names
        String regex = prepareConsumerPathRegex(consumerPath);

        // Convert URI template to a regex pattern
        regex = regex
                .replace("/", "\\/")
                .replace("-", "\\-")
                .replace("{", "(?<")
                .replace("}", ">[^\\/]+)");

        // Add support for wildcard * as path suffix
        regex = regex.replace("*", ".*");

        // Match the provided path against the regex pattern
        Pattern pattern = Pattern.compile(regex);
        PATH_PATTERN.put(consumerPath, pattern);
    }

    /**
     * if the rest consumer is removed, we also remove pattern cache.
     *
     * @param consumerPath a consumer path
     */
    public static void unRegister(String consumerPath) {
        PATH_PATTERN.remove(consumerPath);
    }

    /**
     *
     * @param  requestMethod The request method
     * @param  requestPath   The request path
     * @return               if the request method and path can escape from the ambiguous exception
     */
    private static boolean canBeAmbiguous(String requestMethod, String requestPath) {
        return requestMethod.equalsIgnoreCase("options");
    }

    /**
     * Matches the given request HTTP method with the configured HTTP method of the consumer.
     *
     * @param  method   the request HTTP method
     * @param  restrict the consumer configured HTTP restrict method
     * @return          <tt>true</tt> if matched, <tt>false</tt> otherwise
     */
    private static boolean matchRestMethod(String method, String restrict) {
        if (restrict == null) {
            return true;
        }

        return restrict.toLowerCase(Locale.ENGLISH).contains(method.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Is the request method OPTIONS
     *
     * @return <tt>true</tt> if matched, <tt>false</tt> otherwise
     */
    private static boolean isOptionsMethod(String method) {
        return "options".equalsIgnoreCase(method);
    }

    /**
     * Matches the given request path with the configured consumer path
     *
     * @param  requestPath   the request path
     * @param  isUriTemplate the consumer path which may use { } tokens
     * @return               <tt>true</tt> if matched, <tt>false</tt> otherwise
     */
    private static boolean matchRestPath(String requestPath, String consumerPath, boolean isUriTemplate) {
        // deal with null parameters
        if (requestPath == null && consumerPath == null) {
            return true;
        }
        if (requestPath == null || consumerPath == null) {
            return false;
        }

        // remove starting/ending slashes
        requestPath = removePathSlashes(requestPath);
        // remove starting/ending slashes
        consumerPath = removePathSlashes(consumerPath);

        // split using single char / is optimized in the jdk
        String[] requestPaths = requestPath.split("/");
        String[] consumerPaths = consumerPath.split("/");

        // must be same number of path's
        if (requestPaths.length != consumerPaths.length) {
            return false;
        }

        for (int i = 0; i < requestPaths.length; i++) {
            String p1 = requestPaths[i];
            String p2 = consumerPaths[i];

            if (isUriTemplate && p2.startsWith("{") && p2.endsWith("}")) {
                // always matches
                continue;
            }

            if (!matchPath(p1, p2, false)) {
                return false;
            }
        }

        // assume matching
        return true;
    }

    private static String removePathSlashes(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Counts the number of uri template's curlyBraces in the path
     *
     * @param  consumerPath the consumer path which may use { } tokens
     * @return              number of curlyBraces, or <tt>0</tt> if no curlyBraces
     */
    private static int countCurlyBraces(String consumerPath) {
        int curlyBraces = 0;

        // remove starting/ending slashes
        consumerPath = removePathSlashes(consumerPath);

        String[] consumerPaths = consumerPath.split("/");
        for (String p2 : consumerPaths) {
            if (p2.startsWith("{") && p2.endsWith("}")) {
                curlyBraces++;
            }
        }

        return curlyBraces;
    }

    private static boolean matchWildCard(String requestPath, String consumerPath) {
        if (!requestPath.endsWith("/")) {
            requestPath = requestPath + "/";
        }

        Pattern pattern = PATH_PATTERN.get(consumerPath);
        if (pattern == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(requestPath);

        return matcher.matches();
    }

    /**
     * Removes any hyphens or underscores from parameter names to create valid java regex named group names
     *
     * @param  consumerPath
     * @return
     */
    private static String prepareConsumerPathRegex(String consumerPath) {
        Matcher m = CONSUMER_PATH_PARAMETER_PATTERN.matcher(consumerPath);
        StringBuilder regexBuilder = new StringBuilder(256);
        while (m.find()) {
            m.appendReplacement(regexBuilder, m.group(1) + m.group(2).replaceAll("[\\_\\-]", "") + m.group(3));
        }
        // No matches so return the original path
        if (regexBuilder.isEmpty()) {
            return consumerPath;
        } else {
            return regexBuilder.toString();
        }
    }

}
