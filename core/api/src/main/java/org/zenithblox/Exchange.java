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
package org.zenithblox;

import org.zenithblox.clock.Clock;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.UnitOfWork;
import org.zenithblox.spi.annotations.ConstantProvider;

import java.util.Map;

/**
 * An Exchange is the message container holding the information during the entire routing of a {@link Message} received
 * by a {@link Consumer}.
 * <p/>
 * During processing down the {@link Processor} chain, the {@link Exchange} provides access to the current (not the
 * original) request and response {@link Message} messages. The {@link Exchange} also holds meta-data during its entire
 * lifetime stored as properties accessible using the various {@link #getProperty(String)} methods. The
 * {@link #setProperty(String, Object)} is used to store a property. For example, you can use this to store security,
 * SLA related data or any other information deemed useful throughout processing. If an {@link Exchange} failed during
 * routing the {@link Exception} that caused the failure is stored and accessible via the {@link #getException()}
 * method.
 * <p/>
 * An Exchange is created when a {@link Consumer} receives a request. A new {@link Message} is created, the request is
 * set as the body of the {@link Message} and depending on the {@link Consumer} other {@link Endpoint} and protocol
 * related information is added as headers on the {@link Message}. Then an Exchange is created and the newly created
 * {@link Message} is set as the in on the Exchange. Therefore, an Exchange starts its life in a {@link Consumer}. The
 * Exchange is then sent down the {@link Workflow} for processing along a {@link Processor} chain. The {@link Processor} as
 * the name suggests is what processes the {@link Message} in the Exchange and Zwangine, in addition to providing
 * out-of-the-box a large number of useful processors, it also allows you to create your own. The rule Zwangine uses is to
 * take the out {@link Message} produced by the previous {@link Processor} and set it as the in for the next
 * {@link Processor}. If the previous {@link Processor} did not produce an out, then the in of the previous
 * {@link Processor} is sent as the next in. At the end of the processing chain, depending on the {@link ExchangePattern
 * Message Exchange Pattern} (or MEP) the last out (or in of no out available) is sent by the {@link Consumer} back to
 * the original caller.
 * <p/>
 * Zwangine, in addition to providing out-of-the-box a large number of useful processors, it also allows you to implement
 * and use your own. When the Exchange is passed to a {@link Processor}, it always contains an in {@link Message} and no
 * out {@link Message}. The {@link Processor} <b>may</b> produce an out, depending on the nature of the
 * {@link Processor}. The in {@link Message} can be accessed using the {@link #getIn()} method. Since the out message is
 * null when entering the {@link Processor}, the {@link #getOut()} method is actually a convenient factory method that
 * will lazily instantiate a {@link org.zenithblox.support.DefaultMessage} which you could populate. As an alternative
 * you could also instantiate your specialized {@link Message} and set it on the exchange using the
 * {@link #setOut(org.zenithblox.Message)} method. Please note that a {@link Message} contains not only the body but
 * also headers and attachments. If you are creating a new {@link Message} the headers and attachments of the in
 * {@link Message} are not automatically copied to the out by Zwangine, and you'll have to set the headers and attachments
 * you need yourself. If your {@link Processor} is not producing a different {@link Message} but only needs to slightly
 * modify the in, you can simply update the in {@link Message} returned by {@link #getIn()}.
 * <p/>
 * See this <a href="http://zwangine.zwangine.org/using-getin-or-getout-methods-on-exchange.html">FAQ entry</a> for more
 * details.
 */
@ConstantProvider("org.zenithblox.ExchangeConstantProvider")
public interface Exchange extends VariableAware {

    String AUTHENTICATION = "ZwangineAuthentication";
    String AUTHENTICATION_FAILURE_POLICY_ID = "ZwangineAuthenticationFailurePolicyId";
    @Deprecated(since = "2.20.0")
    String ACCEPT_CONTENT_TYPE = "ZwangineAcceptContentType";
    @Metadata(label = "aggregate", description = "Number of exchanges that was grouped together.", javaType = "int")
    String AGGREGATED_SIZE = "ZwangineAggregatedSize";
    @Metadata(label = "aggregate", description = "The time in millis this group will timeout", javaType = "long")
    String AGGREGATED_TIMEOUT = "ZwangineAggregatedTimeout";
    @Metadata(label = "aggregate", description = "Enum that tell how this group was completed",
              enums = "consumer,force,interval,predicate,size,strategy,timeout", javaType = "String")
    String AGGREGATED_COMPLETED_BY = "ZwangineAggregatedCompletedBy";
    @Metadata(label = "aggregate", description = "The correlation key for this aggregation group", javaType = "String")
    String AGGREGATED_CORRELATION_KEY = "ZwangineAggregatedCorrelationKey";
    String AGGREGATED_COLLECTION_GUARD = "ZwangineAggregatedCollectionGuard";
    String AGGREGATION_STRATEGY = "ZwangineAggregationStrategy";
    @Metadata(label = "consumer,aggregate",
              description = "Input property. Set to true to force completing the current group. This allows to overrule any existing completion predicates, sizes, timeouts etc, and complete the group.",
              javaType = "boolean")
    String AGGREGATION_COMPLETE_CURRENT_GROUP = "ZwangineAggregationCompleteCurrentGroup";
    @Metadata(label = "consumer,aggregate",
              description = "Input property. Set to true to force completing all the groups (excluding this message). This allows to overrule any existing completion predicates, sizes, timeouts etc, and complete the group."
                            + " This message is considered a signal message only, the message headers/contents will not be processed otherwise. Instead use ZwangineAggregationCompleteAllGroupsInclusive if this message should be included in the aggregator.",
              javaType = "boolean")
    String AGGREGATION_COMPLETE_ALL_GROUPS = "ZwangineAggregationCompleteAllGroups";
    @Metadata(label = "consumer,aggregate",
              description = "Input property. Set to true to force completing all the groups (including this message). This allows to overrule any existing completion predicates, sizes, timeouts etc, and complete the group.",
              javaType = "boolean")
    String AGGREGATION_COMPLETE_ALL_GROUPS_INCLUSIVE = "ZwangineAggregationCompleteAllGroupsInclusive";
    String ASYNC_WAIT = "ZwangineAsyncWait";
    @Metadata(description = "The number of Multipart files uploaded with a single request")
    String ATTACHMENTS_SIZE = "ZwangineAttachmentsSize";

    String BATCH_INDEX = "ZwangineBatchIndex";
    String BATCH_SIZE = "ZwangineBatchSize";
    String BATCH_COMPLETE = "ZwangineBatchComplete";
    String BEAN_METHOD_NAME = "ZwangineBeanMethodName";
    String BINDING = "ZwangineBinding";
    // do not prefix with Zwangine and use a lower-case starting letter as it's a shared key
    // used across other  products such as AMQ, SMX etc.
    String BREADCRUMB_ID = "breadcrumbId";

    String CHARSET_NAME = "ZwangineCharsetName";
    @Deprecated(since = "4.5.0")
    String CIRCUIT_BREAKER_STATE = "ZwangineCircuitBreakerState";
    @Deprecated(since = "3.1.0")
    String CREATED_TIMESTAMP = "ZwangineCreatedTimestamp";
    String CLAIM_CHECK_REPOSITORY = "ZwangineClaimCheckRepository";
    String CONTENT_ENCODING = "Content-Encoding";
    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_TYPE = "Content-Type";
    String COOKIE_HANDLER = "ZwangineCookieHandler";
    String CORRELATION_ID = "ZwangineCorrelationId";

    // The schema of the message payload
    String CONTENT_SCHEMA = "ZwangineContentSchema";
    // The schema type of the message payload (json schema, avro, etc)
    String CONTENT_SCHEMA_TYPE = "ZwangineContentSchemaType";

    String DATASET_INDEX = "ZwangineDataSetIndex";
    String DEFAULT_CHARSET_PROPERTY = "org.zenithblox.default.charset";
    String DESTINATION_OVERRIDE_URL = "ZwangineDestinationOverrideUrl";
    String DISABLE_HTTP_STREAM_CACHE = "ZwangineDisableHttpStreamCache";
    @Metadata(label = "idempotentConsumer",
              description = "Whether this exchange is a duplicate detected by the Idempotent Consumer EIP",
              javaType = "boolean")
    String DUPLICATE_MESSAGE = "ZwangineDuplicateMessage";

    String DOCUMENT_BUILDER_FACTORY = "ZwangineDocumentBuilderFactory";

    @Metadata(label = "doCatch,doFinally,errorHandler,onException",
              description = "Stores the caught exception due to a processing error of the current Exchange",
              javaType = "java.lang.Exception")
    String EXCEPTION_CAUGHT = "ZwangineExceptionCaught";
    String EXCEPTION_HANDLED = "ZwangineExceptionHandled";
    String EVALUATE_EXPRESSION_RESULT = "ZwangineEvaluateExpressionResult";
    String ERRORHANDLER_BRIDGE = "ZwangineErrorHandlerBridge";
    String ERRORHANDLER_CIRCUIT_DETECTED = "ZwangineErrorHandlerCircuitDetected";
    @Deprecated(since = "3.1.0")
    String ERRORHANDLER_HANDLED = "ZwangineErrorHandlerHandled";
    @Deprecated(since = "3.1.0")
    String EXTERNAL_REDELIVERED = "ZwangineExternalRedelivered";

    @Deprecated(since = "4.0.0")
    String FAILURE_HANDLED = "ZwangineFailureHandled";

    @Metadata(label = "doCatch,doFinally,errorHandler,onException",
              description = "Endpoint URI where the Exchange failed during processing",
              javaType = "String")
    String FAILURE_ENDPOINT = "ZwangineFailureEndpoint";
    @Metadata(label = "doCatch,doFinally,errorHandler,onException",
              description = "Workflow ID where the Exchange failed during processing",
              javaType = "String")
    String FAILURE_ROUTE_ID = "ZwangineFailureWorkflowId";
    String FATAL_FALLBACK_ERROR_HANDLER = "ZwangineFatalFallbackErrorHandler";
    String FILE_CONTENT_TYPE = "ZwangineFileContentType";
    String FILE_LOCAL_WORK_PATH = "ZwangineFileLocalWorkPath";
    String FILE_NAME = "ZwangineFileName";
    String FILE_NAME_ONLY = "ZwangineFileNameOnly";
    String FILE_NAME_PRODUCED = "ZwangineFileNameProduced";
    String FILE_NAME_CONSUMED = "ZwangineFileNameConsumed";
    String FILE_PATH = "ZwangineFilePath";
    String FILE_PARENT = "ZwangineFileParent";
    String FILE_LAST_MODIFIED = "ZwangineFileLastModified";
    String FILE_LENGTH = "ZwangineFileLength";
    String FILE_LOCK_FILE_ACQUIRED = "ZwangineFileLockFileAcquired";
    String FILE_LOCK_FILE_NAME = "ZwangineFileLockFileName";
    String FILE_LOCK_EXCLUSIVE_LOCK = "ZwangineFileLockExclusiveLock";
    String FILE_LOCK_RANDOM_ACCESS_FILE = "ZwangineFileLockRandomAccessFile";
    String FILE_LOCK_CHANNEL_FILE = "ZwangineFileLockChannelFile";
    String FILE_EXCHANGE_FILE = "ZwangineFileExchangeFile";
    @Deprecated(since = "3.9.0")
    String FILTER_MATCHED = "ZwangineFilterMatched";
    String FILTER_NON_XML_CHARS = "ZwangineFilterNonXmlChars";

    String GROUPED_EXCHANGE = "ZwangineGroupedExchange";

    String HTTP_SCHEME = "ZwangineHttpScheme";
    String HTTP_HOST = "ZwangineHttpHost";
    String HTTP_PORT = "ZwangineHttpPort";
    String HTTP_BASE_URI = "ZwangineHttpBaseUri";
    String HTTP_CHARACTER_ENCODING = "ZwangineHttpCharacterEncoding";
    String HTTP_METHOD = "ZwangineHttpMethod";
    String HTTP_PATH = "ZwangineHttpPath";
    String HTTP_PROTOCOL_VERSION = "ZwangineHttpProtocolVersion";
    String HTTP_QUERY = "ZwangineHttpQuery";
    String HTTP_RAW_QUERY = "ZwangineHttpRawQuery";
    String HTTP_RESPONSE_CODE = "ZwangineHttpResponseCode";
    String HTTP_RESPONSE_TEXT = "ZwangineHttpResponseText";
    String HTTP_URI = "ZwangineHttpUri";
    String HTTP_URL = "ZwangineHttpUrl";
    String HTTP_CHUNKED = "ZwangineHttpChunked";
    @Deprecated(since = "4.7.0")
    String HTTP_SERVLET_REQUEST = "ZwangineHttpServletRequest";
    @Deprecated(since = "4.7.0")
    String HTTP_SERVLET_RESPONSE = "ZwangineHttpServletResponse";

    @Metadata(label = "interceptFrom,interceptSendToEndpoint", description = "The endpoint URI that was intercepted",
              javaType = "String")
    String INTERCEPTED_ENDPOINT = "ZwangineInterceptedEndpoint";
    String INTERCEPT_SEND_TO_ENDPOINT_WHEN_MATCHED = "ZwangineInterceptSendToEndpointWhenMatched";
    @Deprecated(since = "3.1.0")
    String INTERRUPTED = "ZwangineInterrupted";

    String LANGUAGE_SCRIPT = "ZwangineLanguageScript";
    String LOG_DEBUG_BODY_MAX_CHARS = "ZwangineLogDebugBodyMaxChars";
    String LOG_DEBUG_BODY_STREAMS = "ZwangineLogDebugStreams";
    String LOG_EIP_NAME = "ZwangineLogEipName";
    String LOG_EIP_LANGUAGE = "ZwangineLogEipLanguage";
    @Metadata(label = "loop", description = "Index of the current iteration (0 based).", javaType = "int")
    String LOOP_INDEX = "ZwangineLoopIndex";
    @Metadata(label = "loop",
              description = "Total number of loops. This is not available if running the loop in while loop mode.",
              javaType = "int")
    String LOOP_SIZE = "ZwangineLoopSize";

    // Long running action (saga): using "Long-Running-Action" as header value allows sagas
    // to be propagated to any remote system supporting the LRA framework
    String SAGA_LONG_RUNNING_ACTION = "Long-Running-Action";

    String MAXIMUM_CACHE_POOL_SIZE = "ZwangineMaximumCachePoolSize";
    String MAXIMUM_ENDPOINT_CACHE_SIZE = "ZwangineMaximumEndpointCacheSize";
    String MAXIMUM_SIMPLE_CACHE_SIZE = "ZwangineMaximumSimpleCacheSize";
    String MAXIMUM_TRANSFORMER_CACHE_SIZE = "ZwangineMaximumTransformerCacheSize";
    String MAXIMUM_VALIDATOR_CACHE_SIZE = "ZwangineMaximumValidatorCacheSize";
    String MESSAGE_HISTORY = "ZwangineMessageHistory";
    String MESSAGE_HISTORY_HEADER_FORMAT = "ZwangineMessageHistoryHeaderFormat";
    String MESSAGE_HISTORY_OUTPUT_FORMAT = "ZwangineMessageHistoryOutputFormat";
    String MESSAGE_TIMESTAMP = "ZwangineMessageTimestamp";
    @Metadata(label = "multicast",
              description = "An index counter that increases for each Exchange being multicasted. The counter starts from 0.",
              javaType = "int")
    String MULTICAST_INDEX = "ZwangineMulticastIndex";
    @Metadata(label = "multicast", description = "Whether this Exchange is the last.", javaType = "boolean")
    String MULTICAST_COMPLETE = "ZwangineMulticastComplete";

    @Deprecated(since = "3.1.0")
    String NOTIFY_EVENT = "ZwangineNotifyEvent";

    @Metadata(label = "onCompletion",
              description = "Flag to mark that this exchange is currently being executed as onCompletion", javaType = "boolean")
    String ON_COMPLETION = "ZwangineOnCompletion";
    String ON_COMPLETION_ROUTE_IDS = "ZwangineOnCompletionWorkflowIds";
    String OFFSET = "ZwangineOffset";
    String OVERRULE_FILE_NAME = "ZwangineOverruleFileName";

    String PARENT_UNIT_OF_WORK = "ZwangineParentUnitOfWork";
    String STREAM_CACHE_UNIT_OF_WORK = "ZwangineStreamCacheUnitOfWork";

    @Metadata(label = "recipientList", description = "The endpoint uri of this recipient list", javaType = "String")
    String RECIPIENT_LIST_ENDPOINT = "ZwangineRecipientListEndpoint";
    String RECEIVED_TIMESTAMP = "ZwangineReceivedTimestamp";
    String REDELIVERED = "ZwangineRedelivered";
    String REDELIVERY_COUNTER = "ZwangineRedeliveryCounter";
    String REDELIVERY_MAX_COUNTER = "ZwangineRedeliveryMaxCounter";
    @Deprecated(since = "3.1.0")
    String REDELIVERY_EXHAUSTED = "ZwangineRedeliveryExhausted";
    String REDELIVERY_DELAY = "ZwangineRedeliveryDelay";
    String REST_HTTP_URI = "ZwangineRestHttpUri";
    String REST_HTTP_QUERY = "ZwangineRestHttpQuery";
    String REST_OPENAPI = "ZwangineRestOpenAPI";
    @Deprecated(since = "3.1.0")
    String ROLLBACK_ONLY = "ZwangineRollbackOnly";
    @Deprecated(since = "3.1.0")
    String ROLLBACK_ONLY_LAST = "ZwangineRollbackOnlyLast";
    @Deprecated(since = "3.1.0")
    String ROUTE_STOP = "ZwangineWorkflowStop";

    String REUSE_SCRIPT_ENGINE = "ZwangineReuseScripteEngine";
    String COMPILE_SCRIPT = "ZwangineCompileScript";

    @Deprecated(since = "3.15.0")
    String SAXPARSER_FACTORY = "ZwangineSAXParserFactory";

    String SCHEDULER_POLLED_MESSAGES = "ZwangineSchedulerPolledMessages";
    @Deprecated(since = "3.15.0")
    String SOAP_ACTION = "ZwangineSoapAction";
    String SKIP_GZIP_ENCODING = "ZwangineSkipGzipEncoding";
    String SKIP_WWW_FORM_URLENCODED = "ZwangineSkipWwwFormUrlEncoding";
    @Metadata(label = "routingSlip", description = "The endpoint uri of this routing slip", javaType = "String")
    String SLIP_ENDPOINT = "ZwangineSlipEndpoint";
    String SLIP_PRODUCER = "ZwangineSlipProducer";
    @Metadata(label = "split",
              description = "A split counter that increases for each Exchange being split. The counter starts from 0.",
              javaType = "int")
    String SPLIT_INDEX = "ZwangineSplitIndex";
    @Metadata(label = "split", description = "Whether this Exchange is the last.", javaType = "boolean")
    String SPLIT_COMPLETE = "ZwangineSplitComplete";
    @Metadata(label = "split",
              description = "The total number of Exchanges that was split. This property is not applied for stream based splitting, except for the very last message because then Zwangine knows the total size.",
              javaType = "int")
    String SPLIT_SIZE = "ZwangineSplitSize";
    @Metadata(label = "step", description = "The id of the Step EIP", javaType = "String")
    String STEP_ID = "ZwangineStepId";

    String TIMER_COUNTER = "ZwangineTimerCounter";
    String TIMER_FIRED_TIME = "ZwangineTimerFiredTime";
    String TIMER_NAME = "ZwangineTimerName";
    String TIMER_PERIOD = "ZwangineTimerPeriod";
    String TIMER_TIME = "ZwangineTimerTime";

    @Metadata(label = "enrich,multicast,pollEnrich,recipientList,routingSlip,toD,to,wireTap",
              description = "Endpoint URI where this Exchange is being sent to", javaType = "String")
    String TO_ENDPOINT = "ZwangineToEndpoint";
    @Deprecated(since = "4.0.0")
    String TRACE_EVENT = "ZwangineTraceEvent";
    @Deprecated(since = "4.0.0")
    String TRACE_EVENT_NODE_ID = "ZwangineTraceEventNodeId";
    @Deprecated(since = "4.0.0")
    String TRACE_EVENT_TIMESTAMP = "ZwangineTraceEventTimestamp";
    @Deprecated(since = "4.0.0")
    String TRACE_EVENT_EXCHANGE = "ZwangineTraceEventExchange";
    @Deprecated(since = "3.15.0")
    String TRACING_HEADER_FORMAT = "ZwangineTracingHeaderFormat";
    @Deprecated(since = "3.15.0")
    String TRACING_OUTPUT_FORMAT = "ZwangineTracingOutputFormat";
    String TRANSACTION_CONTEXT_DATA = "ZwangineTransactionContextData";
    String TRY_ROUTE_BLOCK = "TryWorkflowBlock";
    String TRANSFER_ENCODING = "Transfer-Encoding";

    String UNIT_OF_WORK_EXHAUSTED = "ZwangineUnitOfWorkExhausted";

    String XSLT_FILE_NAME = "ZwangineXsltFileName";
    String XSLT_ERROR = "ZwangineXsltError";
    String XSLT_FATAL_ERROR = "ZwangineXsltFatalError";
    String XSLT_WARNING = "ZwangineXsltWarning";

    // special for zwangine-tracing/open-telemetry
    String OTEL_ACTIVE_SPAN = "OpenTracing.activeSpan";
    String OTEL_CLOSE_CLIENT_SCOPE = "OpenTracing.closeClientScope";

    /**
     * Returns the {@link ExchangePattern} (MEP) of this exchange.
     *
     * @return the message exchange pattern of this exchange
     */
    ExchangePattern getPattern();

    /**
     * Allows the {@link ExchangePattern} (MEP) of this exchange to be customized.
     *
     * This typically won't be required as an exchange can be created with a specific MEP by calling
     * {@link Endpoint#createExchange(ExchangePattern)} but it is here just in case it is needed.
     *
     * @param pattern the pattern
     */
    void setPattern(ExchangePattern pattern);

    /**
     * Returns a property associated with this exchange by the key
     *
     * @param  key the exchange key
     * @return     the value of the given property or <tt>null</tt> if there is no property for the given key
     */
    Object getProperty(ExchangePropertyKey key);

    /**
     * Returns a property associated with this exchange by the key and specifying the type required
     *
     * @param  key  the exchange key
     * @param  type the type of the property
     * @return      the value of the given property or <tt>null</tt> if there is no property for the given name or
     *              <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getProperty(ExchangePropertyKey key, Class<T> type);

    /**
     * Returns a property associated with this exchange by name and specifying the type required
     *
     * @param  key          the exchange key
     * @param  defaultValue the default value to return if property was absent
     * @param  type         the type of the property
     * @return              the value of the given property or <tt>defaultValue</tt> if there is no property for the
     *                      given name or <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getProperty(ExchangePropertyKey key, Object defaultValue, Class<T> type);

    /**
     * Sets a property on the exchange
     *
     * @param key   the exchange key
     * @param value to associate with the name
     */
    void setProperty(ExchangePropertyKey key, Object value);

    /**
     * Removes the given property on the exchange
     *
     * @param  key the exchange key
     * @return     the old value of the property
     */
    Object removeProperty(ExchangePropertyKey key);

    /**
     * Returns a property associated with this exchange by name
     *
     * @param  name the name of the property
     * @return      the value of the given property or <tt>null</tt> if there is no property for the given name
     */
    Object getProperty(String name);

    /**
     * Returns a property associated with this exchange by name and specifying the type required
     *
     * @param  name the name of the property
     * @param  type the type of the property
     * @return      the value of the given property or <tt>null</tt> if there is no property for the given name or
     *              <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getProperty(String name, Class<T> type);

    /**
     * Returns a property associated with this exchange by name and specifying the type required
     *
     * @param  name         the name of the property
     * @param  defaultValue the default value to return if property was absent
     * @param  type         the type of the property
     * @return              the value of the given property or <tt>defaultValue</tt> if there is no property for the
     *                      given name or <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getProperty(String name, Object defaultValue, Class<T> type);

    /**
     * Sets a property on the exchange
     *
     * @param name  of the property
     * @param value to associate with the name
     */
    void setProperty(String name, Object value);

    /**
     * Removes the given property on the exchange
     *
     * @param  name of the property
     * @return      the old value of the property
     */
    Object removeProperty(String name);

    /**
     * Remove all the properties associated with the exchange matching a specific pattern
     *
     * @param  pattern pattern of names
     * @return         boolean whether any properties matched
     */
    boolean removeProperties(String pattern);

    /**
     * Removes the properties from this exchange that match the given <tt>pattern</tt>, except for the ones matching one
     * or more <tt>excludePatterns</tt>
     *
     * @param  pattern         pattern of names that should be removed
     * @param  excludePatterns one or more pattern of properties names that should be excluded (= preserved)
     * @return                 boolean whether any properties matched
     */
    boolean removeProperties(String pattern, String... excludePatterns);

    /**
     * Returns the properties associated with the exchange
     *
     * @return the properties in a Map
     * @see    #getAllProperties()
     */
    Map<String, Object> getProperties();

    /**
     * Returns all (both internal and custom) properties associated with the exchange
     *
     * @return all (both internal and custom) properties in a Map
     * @see    #getProperties()
     */
    Map<String, Object> getAllProperties();

    /**
     * Returns whether any properties have been set
     *
     * @return <tt>true</tt> if any property has been set
     */
    boolean hasProperties();

    /**
     * Returns a variable by name
     *
     * @param  name the variable name. Can be prefixed with repo-id:name to lookup the variable from a specific
     *              repository. If no repo-id is provided, then variables will be from the current exchange.
     * @return      the value of the given variable or <tt>null</tt> if there is no variable for the given name
     */
    Object getVariable(String name);

    /**
     * Returns a variable by name and specifying the type required
     *
     * @param  name the variable name. Can be prefixed with repo-id:name to lookup the variable from a specific
     *              repository. If no repo-id is provided, then variables will be from the current exchange.
     * @param  type the type of the variable
     * @return      the value of the given variable or <tt>null</tt> if there is no variable for the given name or
     *              <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getVariable(String name, Class<T> type);

    /**
     * Returns a variable by name and specifying the type required
     *
     * @param  name         the variable name. Can be prefixed with repo-id:name to look up the variable from a specific
     *                      repository. If no repo-id is provided, then variables will be from the current exchange.
     * @param  defaultValue the default value to return if variable was absent
     * @param  type         the type of the variable
     * @return              the value of the given variable or <tt>defaultValue</tt> if there is no variable for the
     *                      given name or <tt>null</tt> if it cannot be converted to the given type
     */
    <T> T getVariable(String name, Object defaultValue, Class<T> type);

    /**
     * Sets a variable on the exchange
     *
     * @param name  the variable name. Can be prefixed with repo-id:name to store the variable in a specific repository.
     *              If no repo-id is provided, then variables will be stored in the current exchange.
     * @param value the value of the variable
     */
    void setVariable(String name, Object value);

    /**
     * Removes the given variable
     *
     * If the name is <tt>*</tt> then all variables from the current exchange is removed, and null is returned.
     *
     * @param  name the variable name. Can be prefixed with repo-id:name to remove the variable in a specific
     *              repository. If no repo-id is provided, then the variable from the current exchange will be removed
     * @return      the old value of the variable, or <tt>null</tt> if there was no variable for the given name
     */
    Object removeVariable(String name);

    /**
     * Returns the variables from the current exchange
     *
     * @return the variables from the current exchange in a Map.
     */
    Map<String, Object> getVariables();

    /**
     * Returns whether any variables have been set on the current exchange
     *
     * @return <tt>true</tt> if any variables has been set on the current exchange
     */
    boolean hasVariables();

    /**
     * Returns the inbound request message
     *
     * @return the message
     */
    Message getIn();

    /**
     * Returns the current message
     *
     * @return the current message
     */
    Message getMessage();

    /**
     * Returns the current message as the given type
     *
     * @param  type the given type
     * @return      the message as the given type or <tt>null</tt> if not possible to covert to given type
     */
    <T> T getMessage(Class<T> type);

    /**
     * Replace the current message instance.
     *
     * @param message the new message
     */
    void setMessage(Message message);

    /**
     * Returns the inbound request message as the given type
     *
     * @param  type the given type
     * @return      the message as the given type or <tt>null</tt> if not possible to covert to given type
     */
    <T> T getIn(Class<T> type);

    /**
     * Sets the inbound message instance
     *
     * @param in the inbound message
     */
    void setIn(Message in);

    /**
     * Returns the outbound message, lazily creating one if one has not already been associated with this exchange.
     * <p/>
     * <br/>
     * <b>Important:</b> If you want to change the current message, then use {@link #getIn()} instead as it will ensure
     * headers etc. is kept and propagated when routing continues. Bottom line end users should rarely use this method.
     * <p/>
     * <br/>
     * If you want to test whether an OUT message has been set or not, use the {@link #hasOut()} method.
     * <p/>
     * See also the class Javadoc for this {@link Exchange} for more details and this
     * <a href="http://zwangine.zwangine.org/using-getin-or-getout-methods-on-exchange.html">FAQ entry</a>.
     *
     * @return     the response
     * @see        #getIn()
     * @deprecated use {@link #getMessage()}
     */
    Message getOut();

    /**
     * Returns the outbound request message as the given type
     * <p/>
     * <br/>
     * <b>Important:</b> If you want to change the current message, then use {@link #getIn()} instead as it will ensure
     * headers etc. is kept and propagated when routing continues. Bottom line end users should rarely use this method.
     * <p/>
     * <br/>
     * If you want to test whether an OUT message has been set or not, use the {@link #hasOut()} method.
     * <p/>
     * See also the class Javadoc for this {@link Exchange} for more details and this
     * <a href="http://zwangine.zwangine.org/using-getin-or-getout-methods-on-exchange.html">FAQ entry</a>.
     *
     * @param      type the given type
     * @return          the message as the given type or <tt>null</tt> if not possible to covert to given type
     * @see             #getIn(Class)
     * @deprecated      use {@link #getMessage(Class)}
     */
    <T> T getOut(Class<T> type);

    /**
     * Returns whether an OUT message has been set or not.
     *
     * @return     <tt>true</tt> if an OUT message exists, <tt>false</tt> otherwise.
     * @deprecated use {@link #getMessage()}
     */
    boolean hasOut();

    /**
     * Sets the outbound message
     *
     * @param      out the outbound message
     * @deprecated     use {@link #setMessage(Message)}
     */
    void setOut(Message out);

    /**
     * Returns the exception associated with this exchange
     *
     * @return the exception (or null if no faults)
     */
    Exception getException();

    /**
     * Returns the exception associated with this exchange.
     * <p/>
     * Is used to get the caused exception that typically have been wrapped in some sort of Zwangine wrapper exception
     * <p/>
     * The strategy is to look in the exception hierarchy to find the first given cause that matches the type. Will
     * start from the bottom (the real cause) and walk upwards.
     *
     * @param  type the exception type
     * @return      the exception (or <tt>null</tt> if no caused exception matched)
     */
    <T> T getException(Class<T> type);

    /**
     * Sets the exception associated with this exchange
     * <p/>
     * Zwangine will wrap {@link Throwable} into {@link Exception} type to accommodate for the {@link #getException()}
     * method returning a plain {@link Exception} type.
     *
     * @param t the caused exception
     */
    void setException(Throwable t);

    /**
     * Returns true if this exchange failed due to an exception
     *
     * @return true if this exchange failed due to an exception
     * @see    Exchange#getException()
     */
    boolean isFailed();

    /**
     * Returns true if this exchange is transacted
     */
    boolean isTransacted();

    /**
     * Returns true if this exchange is marked to stop and not continue routing.
     */
    boolean isWorkflowStop();

    /**
     * Sets whether this exchange is marked to stop and not continue routing.
     *
     * @param workflowStop <tt>true</tt> to stop routing
     */
    void setWorkflowStop(boolean workflowStop);

    /**
     * Returns true if this exchange is an external initiated redelivered message (such as a JMS broker).
     * <p/>
     * <b>Important: </b> It is not always possible to determine if the message is a redelivery or not, and therefore
     * <tt>false</tt> is returned. Such an example would be a JDBC message. However JMS brokers provides details if a
     * message is redelivered.
     *
     * @return <tt>true</tt> if redelivered, <tt>false</tt> if not or not able to determine
     */
    boolean isExternalRedelivered();

    /**
     * Returns true if this exchange is marked for rollback
     */
    boolean isRollbackOnly();

    /**
     * Sets whether to mark this exchange for rollback
     */
    void setRollbackOnly(boolean rollbackOnly);

    /**
     * Returns true if this exchange is marked for rollback (only last transaction section)
     */
    boolean isRollbackOnlyLast();

    /**
     * Sets whether to mark this exchange for rollback (only last transaction section)
     */
    void setRollbackOnlyLast(boolean rollbackOnlyLast);

    /**
     * Returns the container so that a processor can resolve endpoints from URIs
     *
     * @return the container which owns this exchange
     */
    ZwangineContext getContext();

    /**
     * Creates a copy of the current message exchange so that it can be forwarded to another destination
     */
    Exchange copy();

    /**
     * Returns the endpoint which originated this message exchange if a consumer on an endpoint created the message
     * exchange, otherwise his property will be <tt>null</tt>.
     *
     * Note: In case this message exchange has been cloned through another parent message exchange (which itself has
     * been created through the consumer of it's own endpoint), then if desired one could still retrieve the consumer
     * endpoint of such a parent message exchange as the following:
     *
     * <pre>
     * getContext().getWorkflow(getFromWorkflowId()).getEndpoint()
     * </pre>
     */
    Endpoint getFromEndpoint();

    /**
     * Returns the workflow id which originated this message exchange if a workflow consumer on an endpoint created the
     * message exchange, otherwise his property will be <tt>null</tt>.
     *
     * Note: In case this message exchange has been cloned through another parent message exchange then this method
     * would return the <tt>fromWorkflowId<tt> property of that exchange.
     */
    String getFromWorkflowId();

    /**
     * Returns the unit of work that this exchange belongs to; which may map to zero, one or more physical transactions
     */
    UnitOfWork getUnitOfWork();

    /**
     * Returns the exchange id (unique)
     */
    String getExchangeId();

    /**
     * Set the exchange id
     */
    void setExchangeId(String id);

    /**
     * Gets the timestamp in millis when this exchange was created.
     *
     * @see Message#getMessageTimestamp()
     */
    long getCreated();

    /**
     * Gets the {@link ExchangeExtension} that contains the extension points for internal exchange APIs. These APIs are
     * intended for internal usage within Zwangine and end-users should avoid using them.
     *
     * @return the {@link ExchangeExtension} point for this exchange.
     */
    ExchangeExtension getExchangeExtension();

    /**
     * Gets {@link Clock} that holds time information about the exchange
     */
    Clock getClock();

}
