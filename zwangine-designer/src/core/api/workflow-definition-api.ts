
import {WorkflowElement} from "../model/integration-defintion";
import {
    ProcessorDefinition,
    BeansDeserializer,
    DataFormatsDefinitionDeserializer,
    ErrorHandlerDeserializer,
    OutputAwareFromDefinition,
    AggregateDefinition,
    BeanDefinition,
    BeanFactoryDefinition,
    CatchDefinition,
    ChoiceDefinition,
    CircuitBreakerDefinition,
    ClaimCheckDefinition,
    ContextScanDefinition,
    ConvertBodyDefinition,
    ConvertHeaderDefinition,
    ConvertVariableDefinition,
    DataFormatDefinition,
    DelayDefinition,
    DynamicRouterDefinition,
    EnrichDefinition,
    ErrorHandlerDefinition,
    ExpressionSubElementDefinition,
    FaultToleranceConfigurationDefinition,
    FilterDefinition,
    FinallyDefinition,
    FromDefinition,
    GlobalOptionDefinition,
    GlobalOptionsDefinition,
    IdempotentConsumerDefinition,
    InputTypeDefinition,
    InterceptDefinition,
    InterceptFromDefinition,
    InterceptSendToEndpointDefinition,
    KameletDefinition,
    LoadBalanceDefinition,
    LogDefinition,
    LoopDefinition,
    MarshalDefinition,
    MulticastDefinition,
    OnCompletionDefinition,
    OnExceptionDefinition,
    OnFallbackDefinition,
    OnWhenDefinition,
    OptimisticLockRetryPolicyDefinition,
    OtherwiseDefinition,
    OutputDefinition,
    OutputTypeDefinition,
    PackageScanDefinition,
    PausableDefinition,
    PipelineDefinition,
    PolicyDefinition,
    PollDefinition,
    PollEnrichDefinition,
    ProcessDefinition,
    PropertyDefinition,
    PropertyExpressionDefinition,
    RecipientListDefinition,
    RedeliveryPolicyDefinition,
    RemoveHeaderDefinition,
    RemoveHeadersDefinition,
    RemovePropertiesDefinition,
    RemovePropertyDefinition,
    RemoveVariableDefinition,
    ResequenceDefinition,
    Resilience4jConfigurationDefinition,
    RestContextRefDefinition,
    ResumableDefinition,
    RollbackDefinition,
    RouteBuilderDefinition,
    RouteConfigurationContextRefDefinition,
    RouteConfigurationDefinition,
    RouteContextRefDefinition,
    RouteDefinition,
    RouteTemplateDefinition,
    RouteTemplateParameterDefinition,
    RoutingSlipDefinition,
    SagaActionUriDefinition,
    SagaDefinition,
    SamplingDefinition,
    ScriptDefinition,
    SetBodyDefinition,
    SetExchangePatternDefinition,
    SetHeaderDefinition,
    SetHeadersDefinition,
    SetPropertyDefinition,
    SetVariableDefinition,
    SetVariablesDefinition,
    SortDefinition,
    SplitDefinition,
    StepDefinition,
    StopDefinition,
    TemplatedRouteDefinition,
    TemplatedRouteParameterDefinition,
    ThreadPoolProfileDefinition,
    ThreadsDefinition,
    ThrottleDefinition,
    ThrowExceptionDefinition,
    ToDefinition,
    ToDynamicDefinition,
    TokenizerDefinition,
    TokenizerImplementationDefinition,
    TransactedDefinition,
    TransformDefinition,
    TryDefinition,
    UnmarshalDefinition,
    ValidateDefinition,
    ValueDefinition,
    WhenDefinition,
    WireTapDefinition,
    BeanConstructorDefinition,
    BeanConstructorsDefinition,
    BeanPropertiesDefinition,
    BeanPropertyDefinition,
    ComponentScanDefinition,
    BatchResequencerConfig,
    StreamResequencerConfig,
    ASN1DataFormat,
    AvroDataFormat,
    BarcodeDataFormat,
    Base64DataFormat,
    BeanioDataFormat,
    BindyDataFormat,
    CBORDataFormat,
    CryptoDataFormat,
    CsvDataFormat,
    CustomDataFormat,
    DataFormatsDefinition,
    FhirJsonDataFormat,
    FhirXmlDataFormat,
    FlatpackDataFormat,
    FuryDataFormat,
    GrokDataFormat,
    GzipDeflaterDataFormat,
    HL7DataFormat,
    IcalDataFormat,
    JacksonXMLDataFormat,
    JaxbDataFormat,
    JsonApiDataFormat,
    JsonDataFormat,
    LZFDataFormat,
    MimeMultipartDataFormat,
    PGPDataFormat,
    ParquetAvroDataFormat,
    ProtobufDataFormat,
    RssDataFormat,
    SmooksDataFormat,
    SoapDataFormat,
    SwiftMtDataFormat,
    SwiftMxDataFormat,
    SyslogDataFormat,
    TarFileDataFormat,
    ThriftDataFormat,
    TidyMarkupDataFormat,
    UniVocityCsvDataFormat,
    UniVocityFixedDataFormat,
    UniVocityHeader,
    UniVocityTsvDataFormat,
    XMLSecurityDataFormat,
    YAMLDataFormat,
    YAMLTypeFilterDefinition,
    ZipDeflaterDataFormat,
    ZipFileDataFormat,
    DeadLetterChannelDefinition,
    DefaultErrorHandlerDefinition,
    JtaTransactionErrorHandlerDefinition,
    NoErrorHandlerDefinition,
    RefErrorHandlerDefinition,
    SpringTransactionErrorHandlerDefinition,
    CSimpleExpression,
    ConstantExpression,
    DatasonnetExpression,
    ExchangePropertyExpression,
    ExpressionDefinition,
    GroovyExpression,
    HeaderExpression,
    Hl7TerserExpression,
    JavaExpression,
    JavaScriptExpression,
    JqExpression,
    JsonPathExpression,
    LanguageExpression,
    MethodCallExpression,
    MvelExpression,
    OgnlExpression,
    PythonExpression,
    RefExpression,
    SimpleExpression,
    SpELExpression,
    TokenizerExpression,
    VariableExpression,
    WasmExpression,
    XMLTokenizerExpression,
    XPathExpression,
    XQueryExpression,
    CustomLoadBalancerDefinition,
    FailoverLoadBalancerDefinition,
    RandomLoadBalancerDefinition,
    RoundRobinLoadBalancerDefinition,
    StickyLoadBalancerDefinition,
    TopicLoadBalancerDefinition,
    WeightedLoadBalancerDefinition,
    ApiKeyDefinition,
    BasicAuthDefinition,
    BearerTokenDefinition,
    DeleteDefinition,
    GetDefinition,
    HeadDefinition,
    MutualTLSDefinition,
    OAuth2Definition,
    OpenApiDefinition,
    OpenIdConnectDefinition,
    ParamDefinition,
    PatchDefinition,
    PostDefinition,
    PutDefinition,
    ResponseHeaderDefinition,
    ResponseMessageDefinition,
    RestBindingDefinition,
    RestConfigurationDefinition,
    RestDefinition,
    RestPropertyDefinition,
    RestSecuritiesDefinition,
    RestsDefinition,
    SecurityDefinition,
    LangChain4jCharacterTokenizerDefinition,
    LangChain4jLineTokenizerDefinition,
    LangChain4jParagraphTokenizerDefinition,
    LangChain4jSentenceTokenizerDefinition,
    LangChain4jTokenizerDefinition,
    LangChain4jWordTokenizerDefinition,
    CustomTransformerDefinition,
    DataFormatTransformerDefinition,
    EndpointTransformerDefinition,
    LoadTransformerDefinition,
    TransformersDefinition,
    CustomValidatorDefinition,
    EndpointValidatorDefinition,
    PredicateValidatorDefinition,
    ValidatorsDefinition,
} from '../model/workflow-definition';
import {WorkflowUtil} from './workflow-util';

export class WorkflowDefinitionApi  {

    static createProcessorDefinition = (element: any): ProcessorDefinition => {
        const def = element ? new ProcessorDefinition({...element}) : new ProcessorDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.aggregate !== undefined) {
            def.aggregate = WorkflowDefinitionApi.createAggregateDefinition(element.aggregate);
        }
        if (element?.bean !== undefined) {
            def.bean = WorkflowDefinitionApi.createBeanDefinition(element.bean);
        }
        if (element?.doCatch !== undefined) {
            def.doCatch = WorkflowDefinitionApi.createCatchDefinition(element.doCatch);
        }
        if (element?.choice !== undefined) {
            def.choice = WorkflowDefinitionApi.createChoiceDefinition(element.choice);
        }
        if (element?.circuitBreaker !== undefined) {
            def.circuitBreaker = WorkflowDefinitionApi.createCircuitBreakerDefinition(element.circuitBreaker);
        }
        if (element?.claimCheck !== undefined) {
            def.claimCheck = WorkflowDefinitionApi.createClaimCheckDefinition(element.claimCheck);
        }
        if (element?.convertBodyTo !== undefined) {
            def.convertBodyTo = WorkflowDefinitionApi.createConvertBodyDefinition(element.convertBodyTo);
        }
        if (element?.convertHeaderTo !== undefined) {
            def.convertHeaderTo = WorkflowDefinitionApi.createConvertHeaderDefinition(element.convertHeaderTo);
        }
        if (element?.convertVariableTo !== undefined) {
            def.convertVariableTo = WorkflowDefinitionApi.createConvertVariableDefinition(element.convertVariableTo);
        }
        if (element?.delay !== undefined) {
            def.delay = WorkflowDefinitionApi.createDelayDefinition(element.delay);
        }
        if (element?.dynamicRouter !== undefined) {
            def.dynamicRouter = WorkflowDefinitionApi.createDynamicRouterDefinition(element.dynamicRouter);
        }
        if (element?.enrich !== undefined) {
            def.enrich = WorkflowDefinitionApi.createEnrichDefinition(element.enrich);
        }
        if (element?.filter !== undefined) {
            def.filter = WorkflowDefinitionApi.createFilterDefinition(element.filter);
        }
        if (element?.doFinally !== undefined) {
            def.doFinally = WorkflowDefinitionApi.createFinallyDefinition(element.doFinally);
        }
        if (element?.idempotentConsumer !== undefined) {
            def.idempotentConsumer = WorkflowDefinitionApi.createIdempotentConsumerDefinition(element.idempotentConsumer);
        }
        if (element?.kamelet !== undefined) {
            def.kamelet = WorkflowDefinitionApi.createKameletDefinition(element.kamelet);
        }
        if (element?.loadBalance !== undefined) {
            def.loadBalance = WorkflowDefinitionApi.createLoadBalanceDefinition(element.loadBalance);
        }
        if (element?.log !== undefined) {
            def.log = WorkflowDefinitionApi.createLogDefinition(element.log);
        }
        if (element?.loop !== undefined) {
            def.loop = WorkflowDefinitionApi.createLoopDefinition(element.loop);
        }
        if (element?.marshal !== undefined) {
            def.marshal = WorkflowDefinitionApi.createMarshalDefinition(element.marshal);
        }
        if (element?.multicast !== undefined) {
            def.multicast = WorkflowDefinitionApi.createMulticastDefinition(element.multicast);
        }
        if (element?.pausable !== undefined) {
            def.pausable = WorkflowDefinitionApi.createPausableDefinition(element.pausable);
        }
        if (element?.pipeline !== undefined) {
            def.pipeline = WorkflowDefinitionApi.createPipelineDefinition(element.pipeline);
        }
        if (element?.policy !== undefined) {
            def.policy = WorkflowDefinitionApi.createPolicyDefinition(element.policy);
        }
        if (element?.poll !== undefined) {
            def.poll = WorkflowDefinitionApi.createPollDefinition(element.poll);
        }
        if (element?.pollEnrich !== undefined) {
            def.pollEnrich = WorkflowDefinitionApi.createPollEnrichDefinition(element.pollEnrich);
        }
        if (element?.process !== undefined) {
            def.process = WorkflowDefinitionApi.createProcessDefinition(element.process);
        }
        if (element?.recipientList !== undefined) {
            def.recipientList = WorkflowDefinitionApi.createRecipientListDefinition(element.recipientList);
        }
        if (element?.removeHeader !== undefined) {
            def.removeHeader = WorkflowDefinitionApi.createRemoveHeaderDefinition(element.removeHeader);
        }
        if (element?.removeHeaders !== undefined) {
            def.removeHeaders = WorkflowDefinitionApi.createRemoveHeadersDefinition(element.removeHeaders);
        }
        if (element?.removeProperties !== undefined) {
            def.removeProperties = WorkflowDefinitionApi.createRemovePropertiesDefinition(element.removeProperties);
        }
        if (element?.removeProperty !== undefined) {
            def.removeProperty = WorkflowDefinitionApi.createRemovePropertyDefinition(element.removeProperty);
        }
        if (element?.removeVariable !== undefined) {
            def.removeVariable = WorkflowDefinitionApi.createRemoveVariableDefinition(element.removeVariable);
        }
        if (element?.resequence !== undefined) {
            def.resequence = WorkflowDefinitionApi.createResequenceDefinition(element.resequence);
        }
        if (element?.resumable !== undefined) {
            def.resumable = WorkflowDefinitionApi.createResumableDefinition(element.resumable);
        }
        if (element?.rollback !== undefined) {
            def.rollback = WorkflowDefinitionApi.createRollbackDefinition(element.rollback);
        }
        if (element?.routingSlip !== undefined) {
            def.routingSlip = WorkflowDefinitionApi.createRoutingSlipDefinition(element.routingSlip);
        }
        if (element?.saga !== undefined) {
            def.saga = WorkflowDefinitionApi.createSagaDefinition(element.saga);
        }
        if (element?.sample !== undefined) {
            def.sample = WorkflowDefinitionApi.createSamplingDefinition(element.sample);
        }
        if (element?.script !== undefined) {
            def.script = WorkflowDefinitionApi.createScriptDefinition(element.script);
        }
        if (element?.setBody !== undefined) {
            def.setBody = WorkflowDefinitionApi.createSetBodyDefinition(element.setBody);
        }
        if (element?.setExchangePattern !== undefined) {
            def.setExchangePattern = WorkflowDefinitionApi.createSetExchangePatternDefinition(element.setExchangePattern);
        }
        if (element?.setHeader !== undefined) {
            def.setHeader = WorkflowDefinitionApi.createSetHeaderDefinition(element.setHeader);
        }
        if (element?.setHeaders !== undefined) {
            def.setHeaders = WorkflowDefinitionApi.createSetHeadersDefinition(element.setHeaders);
        }
        if (element?.setProperty !== undefined) {
            def.setProperty = WorkflowDefinitionApi.createSetPropertyDefinition(element.setProperty);
        }
        if (element?.setVariable !== undefined) {
            def.setVariable = WorkflowDefinitionApi.createSetVariableDefinition(element.setVariable);
        }
        if (element?.setVariables !== undefined) {
            def.setVariables = WorkflowDefinitionApi.createSetVariablesDefinition(element.setVariables);
        }
        if (element?.sort !== undefined) {
            def.sort = WorkflowDefinitionApi.createSortDefinition(element.sort);
        }
        if (element?.split !== undefined) {
            def.split = WorkflowDefinitionApi.createSplitDefinition(element.split);
        }
        if (element?.step !== undefined) {
            def.step = WorkflowDefinitionApi.createStepDefinition(element.step);
        }
        if (element?.stop !== undefined) {
            def.stop = WorkflowDefinitionApi.createStopDefinition(element.stop);
        }
        if (element?.threads !== undefined) {
            def.threads = WorkflowDefinitionApi.createThreadsDefinition(element.threads);
        }
        if (element?.throttle !== undefined) {
            def.throttle = WorkflowDefinitionApi.createThrottleDefinition(element.throttle);
        }
        if (element?.throwException !== undefined) {
            def.throwException = WorkflowDefinitionApi.createThrowExceptionDefinition(element.throwException);
        }
        if (element?.tokenizer !== undefined) {
            def.tokenizer = WorkflowDefinitionApi.createTokenizerDefinition(element.tokenizer);
        }
        if (element?.transacted !== undefined) {
            def.transacted = WorkflowDefinitionApi.createTransactedDefinition(element.transacted);
        }
        if (element?.transform !== undefined) {
            def.transform = WorkflowDefinitionApi.createTransformDefinition(element.transform);
        }
        if (element?.doTry !== undefined) {
            def.doTry = WorkflowDefinitionApi.createTryDefinition(element.doTry);
        }
        if (element?.unmarshal !== undefined) {
            def.unmarshal = WorkflowDefinitionApi.createUnmarshalDefinition(element.unmarshal);
        }
        if (element?.validate !== undefined) {
            def.validate = WorkflowDefinitionApi.createValidateDefinition(element.validate);
        }
        if (element?.wireTap !== undefined) {
            def.wireTap = WorkflowDefinitionApi.createWireTapDefinition(element.wireTap);
        }
        return def;
    }

    static createBeansDeserializer = (element: any): BeansDeserializer => {
        const def = element ? new BeansDeserializer({...element}) : new BeansDeserializer();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDataFormatsDefinitionDeserializer = (element: any): DataFormatsDefinitionDeserializer => {
        const def = element ? new DataFormatsDefinitionDeserializer({...element}) : new DataFormatsDefinitionDeserializer();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createErrorHandlerDeserializer = (element: any): ErrorHandlerDeserializer => {
        const def = element ? new ErrorHandlerDeserializer({...element}) : new ErrorHandlerDeserializer();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.deadLetterChannel !== undefined) {
            def.deadLetterChannel = WorkflowDefinitionApi.createDeadLetterChannelDefinition(element.deadLetterChannel);
        }
        if (element?.defaultErrorHandler !== undefined) {
            def.defaultErrorHandler = WorkflowDefinitionApi.createDefaultErrorHandlerDefinition(element.defaultErrorHandler);
        }
        if (element?.jtaTransactionErrorHandler !== undefined) {
            def.jtaTransactionErrorHandler = WorkflowDefinitionApi.createJtaTransactionErrorHandlerDefinition(element.jtaTransactionErrorHandler);
        }
        if (element?.noErrorHandler !== undefined) {
            def.noErrorHandler = WorkflowDefinitionApi.createNoErrorHandlerDefinition(element.noErrorHandler);
        }
        if (element?.refErrorHandler !== undefined) {
            def.refErrorHandler = WorkflowDefinitionApi.createRefErrorHandlerDefinition(element.refErrorHandler);
        }
        if (element?.springTransactionErrorHandler !== undefined) {
            def.springTransactionErrorHandler = WorkflowDefinitionApi.createSpringTransactionErrorHandlerDefinition(element.springTransactionErrorHandler);
        }
        return def;
    }

    static createOutputAwareFromDefinition = (element: any): OutputAwareFromDefinition => {
        const def = element ? new OutputAwareFromDefinition({...element}) : new OutputAwareFromDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createAggregateDefinition = (element: any): AggregateDefinition => {
        const def = element ? new AggregateDefinition({...element}) : new AggregateDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.correlationExpression !== undefined) {
            def.correlationExpression = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.correlationExpression);
        }
        if (element?.completionPredicate !== undefined) {
            def.completionPredicate = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.completionPredicate);
        }
        if (element?.completionTimeoutExpression !== undefined) {
            def.completionTimeoutExpression = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.completionTimeoutExpression);
        }
        if (element?.completionSizeExpression !== undefined) {
            def.completionSizeExpression = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.completionSizeExpression);
        }
        if (element?.optimisticLockRetryPolicy !== undefined) {
            def.optimisticLockRetryPolicy = WorkflowDefinitionApi.createOptimisticLockRetryPolicyDefinition(element.optimisticLockRetryPolicy);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createBeanDefinition = (element: any): BeanDefinition => {
        const def = element ? new BeanDefinition({...element}) : new BeanDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBeanFactoryDefinition = (element: any): BeanFactoryDefinition => {
        const def = element ? new BeanFactoryDefinition({...element}) : new BeanFactoryDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createCatchDefinition = (element: any): CatchDefinition => {
        const def = element ? new CatchDefinition({...element}) : new CatchDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.onWhen !== undefined) {
            def.onWhen = WorkflowDefinitionApi.createOnWhenDefinition(element.onWhen);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createChoiceDefinition = (element: any): ChoiceDefinition => {
        const def = element ? new ChoiceDefinition({...element}) : new ChoiceDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.when = element && element?.when ? element?.when.map((x:any) => WorkflowDefinitionApi.createWhenDefinition(x)) :[];
        if (element?.otherwise !== undefined) {
            def.otherwise = WorkflowDefinitionApi.createOtherwiseDefinition(element.otherwise);
        }
        return def;
    }

    static createCircuitBreakerDefinition = (element: any): CircuitBreakerDefinition => {
        const def = element ? new CircuitBreakerDefinition({...element}) : new CircuitBreakerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.resilience4jConfiguration !== undefined) {
            def.resilience4jConfiguration = WorkflowDefinitionApi.createResilience4jConfigurationDefinition(element.resilience4jConfiguration);
        }
        if (element?.faultToleranceConfiguration !== undefined) {
            def.faultToleranceConfiguration = WorkflowDefinitionApi.createFaultToleranceConfigurationDefinition(element.faultToleranceConfiguration);
        }
        if (element?.onFallback !== undefined) {
            def.onFallback = WorkflowDefinitionApi.createOnFallbackDefinition(element.onFallback);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createClaimCheckDefinition = (element: any): ClaimCheckDefinition => {
        const def = element ? new ClaimCheckDefinition({...element}) : new ClaimCheckDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createContextScanDefinition = (element: any): ContextScanDefinition => {
        const def = element ? new ContextScanDefinition({...element}) : new ContextScanDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createConvertBodyDefinition = (element: any): ConvertBodyDefinition => {
        if (element && typeof element === 'string') {
            element = {type: element};
        }
        const def = element ? new ConvertBodyDefinition({...element}) : new ConvertBodyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createConvertHeaderDefinition = (element: any): ConvertHeaderDefinition => {
        const def = element ? new ConvertHeaderDefinition({...element}) : new ConvertHeaderDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createConvertVariableDefinition = (element: any): ConvertVariableDefinition => {
        const def = element ? new ConvertVariableDefinition({...element}) : new ConvertVariableDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDataFormatDefinition = (element: any): DataFormatDefinition => {
        const def = element ? new DataFormatDefinition({...element}) : new DataFormatDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDelayDefinition = (element: any): DelayDefinition => {
        const def = element ? new DelayDefinition({...element}) : new DelayDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createDynamicRouterDefinition = (element: any): DynamicRouterDefinition => {
        const def = element ? new DynamicRouterDefinition({...element}) : new DynamicRouterDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createEnrichDefinition = (element: any): EnrichDefinition => {
        const def = element ? new EnrichDefinition({...element}) : new EnrichDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createErrorHandlerDefinition = (element: any): ErrorHandlerDefinition => {
        const def = element ? new ErrorHandlerDefinition({...element}) : new ErrorHandlerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.deadLetterChannel !== undefined) {
            def.deadLetterChannel = WorkflowDefinitionApi.createDeadLetterChannelDefinition(element.deadLetterChannel);
        }
        if (element?.defaultErrorHandler !== undefined) {
            def.defaultErrorHandler = WorkflowDefinitionApi.createDefaultErrorHandlerDefinition(element.defaultErrorHandler);
        }
        if (element?.jtaTransactionErrorHandler !== undefined) {
            def.jtaTransactionErrorHandler = WorkflowDefinitionApi.createJtaTransactionErrorHandlerDefinition(element.jtaTransactionErrorHandler);
        }
        if (element?.noErrorHandler !== undefined) {
            def.noErrorHandler = WorkflowDefinitionApi.createNoErrorHandlerDefinition(element.noErrorHandler);
        }
        if (element?.refErrorHandler !== undefined) {
            def.refErrorHandler = WorkflowDefinitionApi.createRefErrorHandlerDefinition(element.refErrorHandler);
        }
        if (element?.springTransactionErrorHandler !== undefined) {
            def.springTransactionErrorHandler = WorkflowDefinitionApi.createSpringTransactionErrorHandlerDefinition(element.springTransactionErrorHandler);
        }
        return def;
    }

    static createExpressionSubElementDefinition = (element: any): ExpressionSubElementDefinition => {
        const def = element ? new ExpressionSubElementDefinition({...element}) : new ExpressionSubElementDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.constant !== undefined) {
            def.constant = WorkflowDefinitionApi.createConstantExpression(element.constant);
        }
        if (element?.csimple !== undefined) {
            def.csimple = WorkflowDefinitionApi.createCSimpleExpression(element.csimple);
        }
        if (element?.datasonnet !== undefined) {
            def.datasonnet = WorkflowDefinitionApi.createDatasonnetExpression(element.datasonnet);
        }
        if (element?.exchangeProperty !== undefined) {
            def.exchangeProperty = WorkflowDefinitionApi.createExchangePropertyExpression(element.exchangeProperty);
        }
        if (element?.groovy !== undefined) {
            def.groovy = WorkflowDefinitionApi.createGroovyExpression(element.groovy);
        }
        if (element?.header !== undefined) {
            def.header = WorkflowDefinitionApi.createHeaderExpression(element.header);
        }
        if (element?.hl7terser !== undefined) {
            def.hl7terser = WorkflowDefinitionApi.createHl7TerserExpression(element.hl7terser);
        }
        if (element?.java !== undefined) {
            def.java = WorkflowDefinitionApi.createJavaExpression(element.java);
        }
        if (element?.jq !== undefined) {
            def.jq = WorkflowDefinitionApi.createJqExpression(element.jq);
        }
        if (element?.js !== undefined) {
            def.js = WorkflowDefinitionApi.createJavaScriptExpression(element.js);
        }
        if (element?.jsonpath !== undefined) {
            def.jsonpath = WorkflowDefinitionApi.createJsonPathExpression(element.jsonpath);
        }
        if (element?.language !== undefined) {
            def.language = WorkflowDefinitionApi.createLanguageExpression(element.language);
        }
        if (element?.method !== undefined) {
            def.method = WorkflowDefinitionApi.createMethodCallExpression(element.method);
        }
        if (element?.mvel !== undefined) {
            def.mvel = WorkflowDefinitionApi.createMvelExpression(element.mvel);
        }
        if (element?.ognl !== undefined) {
            def.ognl = WorkflowDefinitionApi.createOgnlExpression(element.ognl);
        }
        if (element?.python !== undefined) {
            def.python = WorkflowDefinitionApi.createPythonExpression(element.python);
        }
        if (element?.ref !== undefined) {
            def.ref = WorkflowDefinitionApi.createRefExpression(element.ref);
        }
        if (element?.simple !== undefined) {
            def.simple = WorkflowDefinitionApi.createSimpleExpression(element.simple);
        }
        if (element?.spel !== undefined) {
            def.spel = WorkflowDefinitionApi.createSpELExpression(element.spel);
        }
        if (element?.tokenize !== undefined) {
            def.tokenize = WorkflowDefinitionApi.createTokenizerExpression(element.tokenize);
        }
        if (element?.variable !== undefined) {
            def.variable = WorkflowDefinitionApi.createVariableExpression(element.variable);
        }
        if (element?.wasm !== undefined) {
            def.wasm = WorkflowDefinitionApi.createWasmExpression(element.wasm);
        }
        if (element?.xpath !== undefined) {
            def.xpath = WorkflowDefinitionApi.createXPathExpression(element.xpath);
        }
        if (element?.xquery !== undefined) {
            def.xquery = WorkflowDefinitionApi.createXQueryExpression(element.xquery);
        }
        if (element?.xtokenize !== undefined) {
            def.xtokenize = WorkflowDefinitionApi.createXMLTokenizerExpression(element.xtokenize);
        }
        return def;
    }

    static createFaultToleranceConfigurationDefinition = (element: any): FaultToleranceConfigurationDefinition => {
        const def = element ? new FaultToleranceConfigurationDefinition({...element}) : new FaultToleranceConfigurationDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createFilterDefinition = (element: any): FilterDefinition => {
        const def = element ? new FilterDefinition({...element}) : new FilterDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createFinallyDefinition = (element: any): FinallyDefinition => {
        const def = element ? new FinallyDefinition({...element}) : new FinallyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createFromDefinition = (element: any): FromDefinition => {
        if (element && typeof element === 'string') {
            element = { uri: element};
        }
        const def = element ? new FromDefinition({...element}) : new FromDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createGlobalOptionDefinition = (element: any): GlobalOptionDefinition => {
        const def = element ? new GlobalOptionDefinition({...element}) : new GlobalOptionDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createGlobalOptionsDefinition = (element: any): GlobalOptionsDefinition => {
        const def = element ? new GlobalOptionsDefinition({...element}) : new GlobalOptionsDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.globalOption = element && element?.globalOption ? element?.globalOption.map((x:any) => WorkflowDefinitionApi.createGlobalOptionDefinition(x)) :[];
        return def;
    }

    static createIdempotentConsumerDefinition = (element: any): IdempotentConsumerDefinition => {
        const def = element ? new IdempotentConsumerDefinition({...element}) : new IdempotentConsumerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createInputTypeDefinition = (element: any): InputTypeDefinition => {
        const def = element ? new InputTypeDefinition({...element}) : new InputTypeDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createInterceptDefinition = (element: any): InterceptDefinition => {
        const def = element ? new InterceptDefinition({...element}) : new InterceptDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.onWhen !== undefined) {
            def.onWhen = WorkflowDefinitionApi.createOnWhenDefinition(element.onWhen);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createInterceptFromDefinition = (element: any): InterceptFromDefinition => {
        const def = element ? new InterceptFromDefinition({...element}) : new InterceptFromDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.onWhen !== undefined) {
            def.onWhen = WorkflowDefinitionApi.createOnWhenDefinition(element.onWhen);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createInterceptSendToEndpointDefinition = (element: any): InterceptSendToEndpointDefinition => {
        if (element && typeof element === 'string') {
            element = {uri: element};
        }
        const def = element ? new InterceptSendToEndpointDefinition({...element}) : new InterceptSendToEndpointDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.onWhen !== undefined) {
            def.onWhen = WorkflowDefinitionApi.createOnWhenDefinition(element.onWhen);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createKameletDefinition = (element: any): KameletDefinition => {
        if (element && typeof element === 'string') {
            element = {name: element};
        }
        const def = element ? new KameletDefinition({...element}) : new KameletDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLoadBalanceDefinition = (element: any): LoadBalanceDefinition => {
        const def = element ? new LoadBalanceDefinition({...element}) : new LoadBalanceDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.customLoadBalancer !== undefined) {
            def.customLoadBalancer = WorkflowDefinitionApi.createCustomLoadBalancerDefinition(element.customLoadBalancer);
        }
        if (element?.failoverLoadBalancer !== undefined) {
            def.failoverLoadBalancer = WorkflowDefinitionApi.createFailoverLoadBalancerDefinition(element.failoverLoadBalancer);
        }
        if (element?.randomLoadBalancer !== undefined) {
            def.randomLoadBalancer = WorkflowDefinitionApi.createRandomLoadBalancerDefinition(element.randomLoadBalancer);
        }
        if (element?.roundRobinLoadBalancer !== undefined) {
            def.roundRobinLoadBalancer = WorkflowDefinitionApi.createRoundRobinLoadBalancerDefinition(element.roundRobinLoadBalancer);
        }
        if (element?.stickyLoadBalancer !== undefined) {
            def.stickyLoadBalancer = WorkflowDefinitionApi.createStickyLoadBalancerDefinition(element.stickyLoadBalancer);
        }
        if (element?.topicLoadBalancer !== undefined) {
            def.topicLoadBalancer = WorkflowDefinitionApi.createTopicLoadBalancerDefinition(element.topicLoadBalancer);
        }
        if (element?.weightedLoadBalancer !== undefined) {
            def.weightedLoadBalancer = WorkflowDefinitionApi.createWeightedLoadBalancerDefinition(element.weightedLoadBalancer);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createLogDefinition = (element: any): LogDefinition => {
        if (element && typeof element === 'string') {
            element = {message: element};
        }
        const def = element ? new LogDefinition({...element}) : new LogDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLoopDefinition = (element: any): LoopDefinition => {
        const def = element ? new LoopDefinition({...element}) : new LoopDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createMarshalDefinition = (element: any): MarshalDefinition => {
        const def = element ? new MarshalDefinition({...element}) : new MarshalDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.asn1 !== undefined) {
            def.asn1 = WorkflowDefinitionApi.createASN1DataFormat(element.asn1);
        }
        if (element?.avro !== undefined) {
            def.avro = WorkflowDefinitionApi.createAvroDataFormat(element.avro);
        }
        if (element?.barcode !== undefined) {
            def.barcode = WorkflowDefinitionApi.createBarcodeDataFormat(element.barcode);
        }
        if (element?.base64 !== undefined) {
            def.base64 = WorkflowDefinitionApi.createBase64DataFormat(element.base64);
        }
        if (element?.beanio !== undefined) {
            def.beanio = WorkflowDefinitionApi.createBeanioDataFormat(element.beanio);
        }
        if (element?.bindy !== undefined) {
            def.bindy = WorkflowDefinitionApi.createBindyDataFormat(element.bindy);
        }
        if (element?.cbor !== undefined) {
            def.cbor = WorkflowDefinitionApi.createCBORDataFormat(element.cbor);
        }
        if (element?.crypto !== undefined) {
            def.crypto = WorkflowDefinitionApi.createCryptoDataFormat(element.crypto);
        }
        if (element?.csv !== undefined) {
            def.csv = WorkflowDefinitionApi.createCsvDataFormat(element.csv);
        }
        if (element?.custom !== undefined) {
            def.custom = WorkflowDefinitionApi.createCustomDataFormat(element.custom);
        }
        if (element?.fhirJson !== undefined) {
            def.fhirJson = WorkflowDefinitionApi.createFhirJsonDataFormat(element.fhirJson);
        }
        if (element?.fhirXml !== undefined) {
            def.fhirXml = WorkflowDefinitionApi.createFhirXmlDataFormat(element.fhirXml);
        }
        if (element?.flatpack !== undefined) {
            def.flatpack = WorkflowDefinitionApi.createFlatpackDataFormat(element.flatpack);
        }
        if (element?.fury !== undefined) {
            def.fury = WorkflowDefinitionApi.createFuryDataFormat(element.fury);
        }
        if (element?.grok !== undefined) {
            def.grok = WorkflowDefinitionApi.createGrokDataFormat(element.grok);
        }
        if (element?.gzipDeflater !== undefined) {
            def.gzipDeflater = WorkflowDefinitionApi.createGzipDeflaterDataFormat(element.gzipDeflater);
        }
        if (element?.hl7 !== undefined) {
            def.hl7 = WorkflowDefinitionApi.createHL7DataFormat(element.hl7);
        }
        if (element?.ical !== undefined) {
            def.ical = WorkflowDefinitionApi.createIcalDataFormat(element.ical);
        }
        if (element?.jacksonXml !== undefined) {
            def.jacksonXml = WorkflowDefinitionApi.createJacksonXMLDataFormat(element.jacksonXml);
        }
        if (element?.jaxb !== undefined) {
            def.jaxb = WorkflowDefinitionApi.createJaxbDataFormat(element.jaxb);
        }
        if (element?.json !== undefined) {
            def.json = WorkflowDefinitionApi.createJsonDataFormat(element.json);
        }
        if (element?.jsonApi !== undefined) {
            def.jsonApi = WorkflowDefinitionApi.createJsonApiDataFormat(element.jsonApi);
        }
        if (element?.lzf !== undefined) {
            def.lzf = WorkflowDefinitionApi.createLZFDataFormat(element.lzf);
        }
        if (element?.mimeMultipart !== undefined) {
            def.mimeMultipart = WorkflowDefinitionApi.createMimeMultipartDataFormat(element.mimeMultipart);
        }
        if (element?.parquetAvro !== undefined) {
            def.parquetAvro = WorkflowDefinitionApi.createParquetAvroDataFormat(element.parquetAvro);
        }
        if (element?.pgp !== undefined) {
            def.pgp = WorkflowDefinitionApi.createPGPDataFormat(element.pgp);
        }
        if (element?.protobuf !== undefined) {
            def.protobuf = WorkflowDefinitionApi.createProtobufDataFormat(element.protobuf);
        }
        if (element?.rss !== undefined) {
            def.rss = WorkflowDefinitionApi.createRssDataFormat(element.rss);
        }
        if (element?.smooks !== undefined) {
            def.smooks = WorkflowDefinitionApi.createSmooksDataFormat(element.smooks);
        }
        if (element?.soap !== undefined) {
            def.soap = WorkflowDefinitionApi.createSoapDataFormat(element.soap);
        }
        if (element?.swiftMt !== undefined) {
            def.swiftMt = WorkflowDefinitionApi.createSwiftMtDataFormat(element.swiftMt);
        }
        if (element?.swiftMx !== undefined) {
            def.swiftMx = WorkflowDefinitionApi.createSwiftMxDataFormat(element.swiftMx);
        }
        if (element?.syslog !== undefined) {
            def.syslog = WorkflowDefinitionApi.createSyslogDataFormat(element.syslog);
        }
        if (element?.tarFile !== undefined) {
            def.tarFile = WorkflowDefinitionApi.createTarFileDataFormat(element.tarFile);
        }
        if (element?.thrift !== undefined) {
            def.thrift = WorkflowDefinitionApi.createThriftDataFormat(element.thrift);
        }
        if (element?.tidyMarkup !== undefined) {
            def.tidyMarkup = WorkflowDefinitionApi.createTidyMarkupDataFormat(element.tidyMarkup);
        }
        if (element?.univocityCsv !== undefined) {
            def.univocityCsv = WorkflowDefinitionApi.createUniVocityCsvDataFormat(element.univocityCsv);
        }
        if (element?.univocityFixed !== undefined) {
            def.univocityFixed = WorkflowDefinitionApi.createUniVocityFixedDataFormat(element.univocityFixed);
        }
        if (element?.univocityTsv !== undefined) {
            def.univocityTsv = WorkflowDefinitionApi.createUniVocityTsvDataFormat(element.univocityTsv);
        }
        if (element?.xmlSecurity !== undefined) {
            def.xmlSecurity = WorkflowDefinitionApi.createXMLSecurityDataFormat(element.xmlSecurity);
        }
        if (element?.yaml !== undefined) {
            def.yaml = WorkflowDefinitionApi.createYAMLDataFormat(element.yaml);
        }
        if (element?.zipDeflater !== undefined) {
            def.zipDeflater = WorkflowDefinitionApi.createZipDeflaterDataFormat(element.zipDeflater);
        }
        if (element?.zipFile !== undefined) {
            def.zipFile = WorkflowDefinitionApi.createZipFileDataFormat(element.zipFile);
        }
        return def;
    }

    static createMulticastDefinition = (element: any): MulticastDefinition => {
        const def = element ? new MulticastDefinition({...element}) : new MulticastDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createOnCompletionDefinition = (element: any): OnCompletionDefinition => {
        const def = element ? new OnCompletionDefinition({...element}) : new OnCompletionDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.onWhen !== undefined) {
            def.onWhen = WorkflowDefinitionApi.createOnWhenDefinition(element.onWhen);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createOnExceptionDefinition = (element: any): OnExceptionDefinition => {
        const def = element ? new OnExceptionDefinition({...element}) : new OnExceptionDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.onWhen !== undefined) {
            def.onWhen = WorkflowDefinitionApi.createOnWhenDefinition(element.onWhen);
        }
        if (element?.retryWhile !== undefined) {
            def.retryWhile = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.retryWhile);
        }
        if (element?.redeliveryPolicy !== undefined) {
            def.redeliveryPolicy = WorkflowDefinitionApi.createRedeliveryPolicyDefinition(element.redeliveryPolicy);
        }
        if (element?.handled !== undefined) {
            def.handled = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.handled);
        }
        if (element?.continued !== undefined) {
            def.continued = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.continued);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createOnFallbackDefinition = (element: any): OnFallbackDefinition => {
        const def = element ? new OnFallbackDefinition({...element}) : new OnFallbackDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createOnWhenDefinition = (element: any): OnWhenDefinition => {
        const def = element ? new OnWhenDefinition({...element}) : new OnWhenDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createOptimisticLockRetryPolicyDefinition = (element: any): OptimisticLockRetryPolicyDefinition => {
        const def = element ? new OptimisticLockRetryPolicyDefinition({...element}) : new OptimisticLockRetryPolicyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createOtherwiseDefinition = (element: any): OtherwiseDefinition => {
        const def = element ? new OtherwiseDefinition({...element}) : new OtherwiseDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createOutputDefinition = (element: any): OutputDefinition => {
        const def = element ? new OutputDefinition({...element}) : new OutputDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createOutputTypeDefinition = (element: any): OutputTypeDefinition => {
        const def = element ? new OutputTypeDefinition({...element}) : new OutputTypeDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPackageScanDefinition = (element: any): PackageScanDefinition => {
        const def = element ? new PackageScanDefinition({...element}) : new PackageScanDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPausableDefinition = (element: any): PausableDefinition => {
        const def = element ? new PausableDefinition({...element}) : new PausableDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPipelineDefinition = (element: any): PipelineDefinition => {
        const def = element ? new PipelineDefinition({...element}) : new PipelineDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createPolicyDefinition = (element: any): PolicyDefinition => {
        const def = element ? new PolicyDefinition({...element}) : new PolicyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createPollDefinition = (element: any): PollDefinition => {
        if (element && typeof element === 'string') {
            element = {uri: element};
        }
        const def = element ? new PollDefinition({...element}) : new PollDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPollEnrichDefinition = (element: any): PollEnrichDefinition => {
        const def = element ? new PollEnrichDefinition({...element}) : new PollEnrichDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createProcessDefinition = (element: any): ProcessDefinition => {
        const def = element ? new ProcessDefinition({...element}) : new ProcessDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPropertyDefinition = (element: any): PropertyDefinition => {
        const def = element ? new PropertyDefinition({...element}) : new PropertyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPropertyExpressionDefinition = (element: any): PropertyExpressionDefinition => {
        const def = element ? new PropertyExpressionDefinition({...element}) : new PropertyExpressionDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createRecipientListDefinition = (element: any): RecipientListDefinition => {
        const def = element ? new RecipientListDefinition({...element}) : new RecipientListDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createRedeliveryPolicyDefinition = (element: any): RedeliveryPolicyDefinition => {
        const def = element ? new RedeliveryPolicyDefinition({...element}) : new RedeliveryPolicyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRemoveHeaderDefinition = (element: any): RemoveHeaderDefinition => {
        if (element && typeof element === 'string') {
            element = {name: element};
        }
        const def = element ? new RemoveHeaderDefinition({...element}) : new RemoveHeaderDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRemoveHeadersDefinition = (element: any): RemoveHeadersDefinition => {
        if (element && typeof element === 'string') {
            element = {pattern: element};
        }
        const def = element ? new RemoveHeadersDefinition({...element}) : new RemoveHeadersDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRemovePropertiesDefinition = (element: any): RemovePropertiesDefinition => {
        if (element && typeof element === 'string') {
            element = {pattern: element};
        }
        const def = element ? new RemovePropertiesDefinition({...element}) : new RemovePropertiesDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRemovePropertyDefinition = (element: any): RemovePropertyDefinition => {
        if (element && typeof element === 'string') {
            element = {name: element};
        }
        const def = element ? new RemovePropertyDefinition({...element}) : new RemovePropertyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRemoveVariableDefinition = (element: any): RemoveVariableDefinition => {
        if (element && typeof element === 'string') {
            element = {name: element};
        }
        const def = element ? new RemoveVariableDefinition({...element}) : new RemoveVariableDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createResequenceDefinition = (element: any): ResequenceDefinition => {
        const def = element ? new ResequenceDefinition({...element}) : new ResequenceDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        if (element?.batchConfig !== undefined) {
            def.batchConfig = WorkflowDefinitionApi.createBatchResequencerConfig(element.batchConfig);
        }
        if (element?.streamConfig !== undefined) {
            def.streamConfig = WorkflowDefinitionApi.createStreamResequencerConfig(element.streamConfig);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createResilience4jConfigurationDefinition = (element: any): Resilience4jConfigurationDefinition => {
        const def = element ? new Resilience4jConfigurationDefinition({...element}) : new Resilience4jConfigurationDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRestContextRefDefinition = (element: any): RestContextRefDefinition => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new RestContextRefDefinition({...element}) : new RestContextRefDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createResumableDefinition = (element: any): ResumableDefinition => {
        const def = element ? new ResumableDefinition({...element}) : new ResumableDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRollbackDefinition = (element: any): RollbackDefinition => {
        const def = element ? new RollbackDefinition({...element}) : new RollbackDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRouteBuilderDefinition = (element: any): RouteBuilderDefinition => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new RouteBuilderDefinition({...element}) : new RouteBuilderDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRouteConfigurationContextRefDefinition = (element: any): RouteConfigurationContextRefDefinition => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new RouteConfigurationContextRefDefinition({...element}) : new RouteConfigurationContextRefDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRouteConfigurationDefinition = (element: any): RouteConfigurationDefinition => {
        const def = element ? new RouteConfigurationDefinition({...element}) : new RouteConfigurationDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.errorHandler !== undefined) {
            def.errorHandler = WorkflowDefinitionApi.createErrorHandlerDefinition(element.errorHandler);
        }
        def.intercept = element && element?.intercept ? element?.intercept.map((x:any) => WorkflowDefinitionApi.createInterceptDefinition(x)) :[];
        def.interceptFrom = element && element?.interceptFrom ? element?.interceptFrom.map((x:any) => WorkflowDefinitionApi.createInterceptFromDefinition(x)) :[];
        def.interceptSendToEndpoint = element && element?.interceptSendToEndpoint ? element?.interceptSendToEndpoint.map((x:any) => WorkflowDefinitionApi.createInterceptSendToEndpointDefinition(x)) :[];
        def.onException = element && element?.onException ? element?.onException.map((x:any) => WorkflowDefinitionApi.createOnExceptionDefinition(x)) :[];
        def.onCompletion = element && element?.onCompletion ? element?.onCompletion.map((x:any) => WorkflowDefinitionApi.createOnCompletionDefinition(x)) :[];
        return def;
    }

    static createRouteContextRefDefinition = (element: any): RouteContextRefDefinition => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new RouteContextRefDefinition({...element}) : new RouteContextRefDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRouteDefinition = (element: any): RouteDefinition => {
        const def = element ? new RouteDefinition({...element}) : new RouteDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.errorHandler !== undefined) {
            def.errorHandler = WorkflowDefinitionApi.createErrorHandlerDefinition(element.errorHandler);
        }
        if (element?.inputType !== undefined) {
            def.inputType = WorkflowDefinitionApi.createInputTypeDefinition(element.inputType);
        }
        if (element?.outputType !== undefined) {
            def.outputType = WorkflowDefinitionApi.createOutputTypeDefinition(element.outputType);
        }
        if (element?.from !== undefined) {
            def.from = WorkflowDefinitionApi.createFromDefinition(element.from);
        }
        return def;
    }

    static createRouteTemplateDefinition = (element: any): RouteTemplateDefinition => {
        const def = element ? new RouteTemplateDefinition({...element}) : new RouteTemplateDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.route !== undefined) {
            def.route = WorkflowDefinitionApi.createRouteDefinition(element.route);
        }
        def.beans = element && element?.beans ? element?.beans.map((x:any) => WorkflowDefinitionApi.createBeanFactoryDefinition(x)) :[];
        if (element?.from !== undefined) {
            def.from = WorkflowDefinitionApi.createFromDefinition(element.from);
        }
        def.parameters = element && element?.parameters ? element?.parameters.map((x:any) => WorkflowDefinitionApi.createRouteTemplateParameterDefinition(x)) :[];
        return def;
    }

    static createRouteTemplateParameterDefinition = (element: any): RouteTemplateParameterDefinition => {
        const def = element ? new RouteTemplateParameterDefinition({...element}) : new RouteTemplateParameterDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRoutingSlipDefinition = (element: any): RoutingSlipDefinition => {
        const def = element ? new RoutingSlipDefinition({...element}) : new RoutingSlipDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSagaActionUriDefinition = (element: any): SagaActionUriDefinition => {
        if (element && typeof element === 'string') {
            element = {uri: element};
        }
        const def = element ? new SagaActionUriDefinition({...element}) : new SagaActionUriDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSagaDefinition = (element: any): SagaDefinition => {
        const def = element ? new SagaDefinition({...element}) : new SagaDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.option = element && element?.option ? element?.option.map((x:any) => WorkflowDefinitionApi.createPropertyExpressionDefinition(x)) :[];
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createSamplingDefinition = (element: any): SamplingDefinition => {
        const def = element ? new SamplingDefinition({...element}) : new SamplingDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createScriptDefinition = (element: any): ScriptDefinition => {
        const def = element ? new ScriptDefinition({...element}) : new ScriptDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSetBodyDefinition = (element: any): SetBodyDefinition => {
        const def = element ? new SetBodyDefinition({...element}) : new SetBodyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSetExchangePatternDefinition = (element: any): SetExchangePatternDefinition => {
        const def = element ? new SetExchangePatternDefinition({...element}) : new SetExchangePatternDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSetHeaderDefinition = (element: any): SetHeaderDefinition => {
        const def = element ? new SetHeaderDefinition({...element}) : new SetHeaderDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSetHeadersDefinition = (element: any): SetHeadersDefinition => {
        const def = element ? new SetHeadersDefinition({...element}) : new SetHeadersDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.headers = element && element?.headers ? element?.headers.map((x:any) => WorkflowDefinitionApi.createSetHeaderDefinition(x)) :[];
        return def;
    }

    static createSetPropertyDefinition = (element: any): SetPropertyDefinition => {
        const def = element ? new SetPropertyDefinition({...element}) : new SetPropertyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSetVariableDefinition = (element: any): SetVariableDefinition => {
        const def = element ? new SetVariableDefinition({...element}) : new SetVariableDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSetVariablesDefinition = (element: any): SetVariablesDefinition => {
        const def = element ? new SetVariablesDefinition({...element}) : new SetVariablesDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.variables = element && element?.variables ? element?.variables.map((x:any) => WorkflowDefinitionApi.createSetVariableDefinition(x)) :[];
        return def;
    }

    static createSortDefinition = (element: any): SortDefinition => {
        const def = element ? new SortDefinition({...element}) : new SortDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createSplitDefinition = (element: any): SplitDefinition => {
        const def = element ? new SplitDefinition({...element}) : new SplitDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createStepDefinition = (element: any): StepDefinition => {
        const def = element ? new StepDefinition({...element}) : new StepDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createStopDefinition = (element: any): StopDefinition => {
        const def = element ? new StopDefinition({...element}) : new StopDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTemplatedRouteDefinition = (element: any): TemplatedRouteDefinition => {
        const def = element ? new TemplatedRouteDefinition({...element}) : new TemplatedRouteDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.beans = element && element?.beans ? element?.beans.map((x:any) => WorkflowDefinitionApi.createBeanFactoryDefinition(x)) :[];
        def.parameters = element && element?.parameters ? element?.parameters.map((x:any) => WorkflowDefinitionApi.createTemplatedRouteParameterDefinition(x)) :[];
        return def;
    }

    static createTemplatedRouteParameterDefinition = (element: any): TemplatedRouteParameterDefinition => {
        const def = element ? new TemplatedRouteParameterDefinition({...element}) : new TemplatedRouteParameterDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createThreadPoolProfileDefinition = (element: any): ThreadPoolProfileDefinition => {
        const def = element ? new ThreadPoolProfileDefinition({...element}) : new ThreadPoolProfileDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createThreadsDefinition = (element: any): ThreadsDefinition => {
        const def = element ? new ThreadsDefinition({...element}) : new ThreadsDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createThrottleDefinition = (element: any): ThrottleDefinition => {
        const def = element ? new ThrottleDefinition({...element}) : new ThrottleDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        if (element?.correlationExpression !== undefined) {
            def.correlationExpression = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.correlationExpression);
        }
        return def;
    }

    static createThrowExceptionDefinition = (element: any): ThrowExceptionDefinition => {
        const def = element ? new ThrowExceptionDefinition({...element}) : new ThrowExceptionDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createToDefinition = (element: any): ToDefinition => {
        if (element && typeof element === 'string') {
            element = {uri: element};
        }
        const def = element ? new ToDefinition({...element}) : new ToDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createToDynamicDefinition = (element: any): ToDynamicDefinition => {
        if (element && typeof element === 'string') {
            element = {uri: element};
        }
        const def = element ? new ToDynamicDefinition({...element}) : new ToDynamicDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTokenizerDefinition = (element: any): TokenizerDefinition => {
        const def = element ? new TokenizerDefinition({...element}) : new TokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.langChain4jCharacterTokenizer !== undefined) {
            def.langChain4jCharacterTokenizer = WorkflowDefinitionApi.createLangChain4jCharacterTokenizerDefinition(element.langChain4jCharacterTokenizer);
        }
        if (element?.langChain4jLineTokenizer !== undefined) {
            def.langChain4jLineTokenizer = WorkflowDefinitionApi.createLangChain4jTokenizerDefinition(element.langChain4jLineTokenizer);
        }
        if (element?.langChain4jParagraphTokenizer !== undefined) {
            def.langChain4jParagraphTokenizer = WorkflowDefinitionApi.createLangChain4jParagraphTokenizerDefinition(element.langChain4jParagraphTokenizer);
        }
        if (element?.langChain4jSentenceTokenizer !== undefined) {
            def.langChain4jSentenceTokenizer = WorkflowDefinitionApi.createLangChain4jSentenceTokenizerDefinition(element.langChain4jSentenceTokenizer);
        }
        if (element?.langChain4jWordTokenizer !== undefined) {
            def.langChain4jWordTokenizer = WorkflowDefinitionApi.createLangChain4jWordTokenizerDefinition(element.langChain4jWordTokenizer);
        }
        return def;
    }

    static createTokenizerImplementationDefinition = (element: any): TokenizerImplementationDefinition => {
        const def = element ? new TokenizerImplementationDefinition({...element}) : new TokenizerImplementationDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTransactedDefinition = (element: any): TransactedDefinition => {
        const def = element ? new TransactedDefinition({...element}) : new TransactedDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createTransformDefinition = (element: any): TransformDefinition => {
        const def = element ? new TransformDefinition({...element}) : new TransformDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createTryDefinition = (element: any): TryDefinition => {
        const def = element ? new TryDefinition({...element}) : new TryDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.doCatch = element && element?.doCatch ? element?.doCatch.map((x:any) => WorkflowDefinitionApi.createCatchDefinition(x)) :[];
        if (element?.doFinally !== undefined) {
            def.doFinally = WorkflowDefinitionApi.createFinallyDefinition(element.doFinally);
        }
        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createUnmarshalDefinition = (element: any): UnmarshalDefinition => {
        const def = element ? new UnmarshalDefinition({...element}) : new UnmarshalDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.asn1 !== undefined) {
            def.asn1 = WorkflowDefinitionApi.createASN1DataFormat(element.asn1);
        }
        if (element?.avro !== undefined) {
            def.avro = WorkflowDefinitionApi.createAvroDataFormat(element.avro);
        }
        if (element?.barcode !== undefined) {
            def.barcode = WorkflowDefinitionApi.createBarcodeDataFormat(element.barcode);
        }
        if (element?.base64 !== undefined) {
            def.base64 = WorkflowDefinitionApi.createBase64DataFormat(element.base64);
        }
        if (element?.beanio !== undefined) {
            def.beanio = WorkflowDefinitionApi.createBeanioDataFormat(element.beanio);
        }
        if (element?.bindy !== undefined) {
            def.bindy = WorkflowDefinitionApi.createBindyDataFormat(element.bindy);
        }
        if (element?.cbor !== undefined) {
            def.cbor = WorkflowDefinitionApi.createCBORDataFormat(element.cbor);
        }
        if (element?.crypto !== undefined) {
            def.crypto = WorkflowDefinitionApi.createCryptoDataFormat(element.crypto);
        }
        if (element?.csv !== undefined) {
            def.csv = WorkflowDefinitionApi.createCsvDataFormat(element.csv);
        }
        if (element?.custom !== undefined) {
            def.custom = WorkflowDefinitionApi.createCustomDataFormat(element.custom);
        }
        if (element?.fhirJson !== undefined) {
            def.fhirJson = WorkflowDefinitionApi.createFhirJsonDataFormat(element.fhirJson);
        }
        if (element?.fhirXml !== undefined) {
            def.fhirXml = WorkflowDefinitionApi.createFhirXmlDataFormat(element.fhirXml);
        }
        if (element?.flatpack !== undefined) {
            def.flatpack = WorkflowDefinitionApi.createFlatpackDataFormat(element.flatpack);
        }
        if (element?.fury !== undefined) {
            def.fury = WorkflowDefinitionApi.createFuryDataFormat(element.fury);
        }
        if (element?.grok !== undefined) {
            def.grok = WorkflowDefinitionApi.createGrokDataFormat(element.grok);
        }
        if (element?.gzipDeflater !== undefined) {
            def.gzipDeflater = WorkflowDefinitionApi.createGzipDeflaterDataFormat(element.gzipDeflater);
        }
        if (element?.hl7 !== undefined) {
            def.hl7 = WorkflowDefinitionApi.createHL7DataFormat(element.hl7);
        }
        if (element?.ical !== undefined) {
            def.ical = WorkflowDefinitionApi.createIcalDataFormat(element.ical);
        }
        if (element?.jacksonXml !== undefined) {
            def.jacksonXml = WorkflowDefinitionApi.createJacksonXMLDataFormat(element.jacksonXml);
        }
        if (element?.jaxb !== undefined) {
            def.jaxb = WorkflowDefinitionApi.createJaxbDataFormat(element.jaxb);
        }
        if (element?.json !== undefined) {
            def.json = WorkflowDefinitionApi.createJsonDataFormat(element.json);
        }
        if (element?.jsonApi !== undefined) {
            def.jsonApi = WorkflowDefinitionApi.createJsonApiDataFormat(element.jsonApi);
        }
        if (element?.lzf !== undefined) {
            def.lzf = WorkflowDefinitionApi.createLZFDataFormat(element.lzf);
        }
        if (element?.mimeMultipart !== undefined) {
            def.mimeMultipart = WorkflowDefinitionApi.createMimeMultipartDataFormat(element.mimeMultipart);
        }
        if (element?.parquetAvro !== undefined) {
            def.parquetAvro = WorkflowDefinitionApi.createParquetAvroDataFormat(element.parquetAvro);
        }
        if (element?.pgp !== undefined) {
            def.pgp = WorkflowDefinitionApi.createPGPDataFormat(element.pgp);
        }
        if (element?.protobuf !== undefined) {
            def.protobuf = WorkflowDefinitionApi.createProtobufDataFormat(element.protobuf);
        }
        if (element?.rss !== undefined) {
            def.rss = WorkflowDefinitionApi.createRssDataFormat(element.rss);
        }
        if (element?.smooks !== undefined) {
            def.smooks = WorkflowDefinitionApi.createSmooksDataFormat(element.smooks);
        }
        if (element?.soap !== undefined) {
            def.soap = WorkflowDefinitionApi.createSoapDataFormat(element.soap);
        }
        if (element?.swiftMt !== undefined) {
            def.swiftMt = WorkflowDefinitionApi.createSwiftMtDataFormat(element.swiftMt);
        }
        if (element?.swiftMx !== undefined) {
            def.swiftMx = WorkflowDefinitionApi.createSwiftMxDataFormat(element.swiftMx);
        }
        if (element?.syslog !== undefined) {
            def.syslog = WorkflowDefinitionApi.createSyslogDataFormat(element.syslog);
        }
        if (element?.tarFile !== undefined) {
            def.tarFile = WorkflowDefinitionApi.createTarFileDataFormat(element.tarFile);
        }
        if (element?.thrift !== undefined) {
            def.thrift = WorkflowDefinitionApi.createThriftDataFormat(element.thrift);
        }
        if (element?.tidyMarkup !== undefined) {
            def.tidyMarkup = WorkflowDefinitionApi.createTidyMarkupDataFormat(element.tidyMarkup);
        }
        if (element?.univocityCsv !== undefined) {
            def.univocityCsv = WorkflowDefinitionApi.createUniVocityCsvDataFormat(element.univocityCsv);
        }
        if (element?.univocityFixed !== undefined) {
            def.univocityFixed = WorkflowDefinitionApi.createUniVocityFixedDataFormat(element.univocityFixed);
        }
        if (element?.univocityTsv !== undefined) {
            def.univocityTsv = WorkflowDefinitionApi.createUniVocityTsvDataFormat(element.univocityTsv);
        }
        if (element?.xmlSecurity !== undefined) {
            def.xmlSecurity = WorkflowDefinitionApi.createXMLSecurityDataFormat(element.xmlSecurity);
        }
        if (element?.yaml !== undefined) {
            def.yaml = WorkflowDefinitionApi.createYAMLDataFormat(element.yaml);
        }
        if (element?.zipDeflater !== undefined) {
            def.zipDeflater = WorkflowDefinitionApi.createZipDeflaterDataFormat(element.zipDeflater);
        }
        if (element?.zipFile !== undefined) {
            def.zipFile = WorkflowDefinitionApi.createZipFileDataFormat(element.zipFile);
        }
        return def;
    }

    static createValidateDefinition = (element: any): ValidateDefinition => {
        const def = element ? new ValidateDefinition({...element}) : new ValidateDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createValueDefinition = (element: any): ValueDefinition => {
        const def = element ? new ValueDefinition({...element}) : new ValueDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createWhenDefinition = (element: any): WhenDefinition => {
        const def = element ? new WhenDefinition({...element}) : new WhenDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        def.steps = WorkflowDefinitionApi.createSteps(element?.steps);
        return def;
    }

    static createWireTapDefinition = (element: any): WireTapDefinition => {
        const def = element ? new WireTapDefinition({...element}) : new WireTapDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBeanConstructorDefinition = (element: any): BeanConstructorDefinition => {
        const def = element ? new BeanConstructorDefinition({...element}) : new BeanConstructorDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBeanConstructorsDefinition = (element: any): BeanConstructorsDefinition => {
        const def = element ? new BeanConstructorsDefinition({...element}) : new BeanConstructorsDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.constructor = element && element?.constructor ? element?.constructor.map((x:any) => WorkflowDefinitionApi.createBeanConstructorDefinition(x)) :[];
        return def;
    }

    static createBeanPropertiesDefinition = (element: any): BeanPropertiesDefinition => {
        const def = element ? new BeanPropertiesDefinition({...element}) : new BeanPropertiesDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.property = element && element?.property ? element?.property.map((x:any) => WorkflowDefinitionApi.createBeanPropertyDefinition(x)) :[];
        return def;
    }

    static createBeanPropertyDefinition = (element: any): BeanPropertyDefinition => {
        const def = element ? new BeanPropertyDefinition({...element}) : new BeanPropertyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.properties !== undefined) {
            def.properties = WorkflowDefinitionApi.createBeanPropertiesDefinition(element.properties);
        }
        return def;
    }

    static createComponentScanDefinition = (element: any): ComponentScanDefinition => {
        const def = element ? new ComponentScanDefinition({...element}) : new ComponentScanDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBatchResequencerConfig = (element: any): BatchResequencerConfig => {
        const def = element ? new BatchResequencerConfig({...element}) : new BatchResequencerConfig();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createStreamResequencerConfig = (element: any): StreamResequencerConfig => {
        const def = element ? new StreamResequencerConfig({...element}) : new StreamResequencerConfig();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createASN1DataFormat = (element: any): ASN1DataFormat => {
        const def = element ? new ASN1DataFormat({...element}) : new ASN1DataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createAvroDataFormat = (element: any): AvroDataFormat => {
        const def = element ? new AvroDataFormat({...element}) : new AvroDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBarcodeDataFormat = (element: any): BarcodeDataFormat => {
        const def = element ? new BarcodeDataFormat({...element}) : new BarcodeDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBase64DataFormat = (element: any): Base64DataFormat => {
        const def = element ? new Base64DataFormat({...element}) : new Base64DataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBeanioDataFormat = (element: any): BeanioDataFormat => {
        const def = element ? new BeanioDataFormat({...element}) : new BeanioDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBindyDataFormat = (element: any): BindyDataFormat => {
        const def = element ? new BindyDataFormat({...element}) : new BindyDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createCBORDataFormat = (element: any): CBORDataFormat => {
        const def = element ? new CBORDataFormat({...element}) : new CBORDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createCryptoDataFormat = (element: any): CryptoDataFormat => {
        const def = element ? new CryptoDataFormat({...element}) : new CryptoDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createCsvDataFormat = (element: any): CsvDataFormat => {
        const def = element ? new CsvDataFormat({...element}) : new CsvDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createCustomDataFormat = (element: any): CustomDataFormat => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new CustomDataFormat({...element}) : new CustomDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDataFormatsDefinition = (element: any): DataFormatsDefinition => {
        const def = element ? new DataFormatsDefinition({...element}) : new DataFormatsDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.asn1 !== undefined) {
            def.asn1 = WorkflowDefinitionApi.createASN1DataFormat(element.asn1);
        }
        if (element?.avro !== undefined) {
            def.avro = WorkflowDefinitionApi.createAvroDataFormat(element.avro);
        }
        if (element?.barcode !== undefined) {
            def.barcode = WorkflowDefinitionApi.createBarcodeDataFormat(element.barcode);
        }
        if (element?.base64 !== undefined) {
            def.base64 = WorkflowDefinitionApi.createBase64DataFormat(element.base64);
        }
        if (element?.beanio !== undefined) {
            def.beanio = WorkflowDefinitionApi.createBeanioDataFormat(element.beanio);
        }
        if (element?.bindy !== undefined) {
            def.bindy = WorkflowDefinitionApi.createBindyDataFormat(element.bindy);
        }
        if (element?.cbor !== undefined) {
            def.cbor = WorkflowDefinitionApi.createCBORDataFormat(element.cbor);
        }
        if (element?.crypto !== undefined) {
            def.crypto = WorkflowDefinitionApi.createCryptoDataFormat(element.crypto);
        }
        if (element?.csv !== undefined) {
            def.csv = WorkflowDefinitionApi.createCsvDataFormat(element.csv);
        }
        if (element?.custom !== undefined) {
            def.custom = WorkflowDefinitionApi.createCustomDataFormat(element.custom);
        }
        if (element?.fhirJson !== undefined) {
            def.fhirJson = WorkflowDefinitionApi.createFhirJsonDataFormat(element.fhirJson);
        }
        if (element?.fhirXml !== undefined) {
            def.fhirXml = WorkflowDefinitionApi.createFhirXmlDataFormat(element.fhirXml);
        }
        if (element?.flatpack !== undefined) {
            def.flatpack = WorkflowDefinitionApi.createFlatpackDataFormat(element.flatpack);
        }
        if (element?.fury !== undefined) {
            def.fury = WorkflowDefinitionApi.createFuryDataFormat(element.fury);
        }
        if (element?.grok !== undefined) {
            def.grok = WorkflowDefinitionApi.createGrokDataFormat(element.grok);
        }
        if (element?.gzipDeflater !== undefined) {
            def.gzipDeflater = WorkflowDefinitionApi.createGzipDeflaterDataFormat(element.gzipDeflater);
        }
        if (element?.hl7 !== undefined) {
            def.hl7 = WorkflowDefinitionApi.createHL7DataFormat(element.hl7);
        }
        if (element?.ical !== undefined) {
            def.ical = WorkflowDefinitionApi.createIcalDataFormat(element.ical);
        }
        if (element?.jacksonXml !== undefined) {
            def.jacksonXml = WorkflowDefinitionApi.createJacksonXMLDataFormat(element.jacksonXml);
        }
        if (element?.jaxb !== undefined) {
            def.jaxb = WorkflowDefinitionApi.createJaxbDataFormat(element.jaxb);
        }
        if (element?.json !== undefined) {
            def.json = WorkflowDefinitionApi.createJsonDataFormat(element.json);
        }
        if (element?.jsonApi !== undefined) {
            def.jsonApi = WorkflowDefinitionApi.createJsonApiDataFormat(element.jsonApi);
        }
        if (element?.lzf !== undefined) {
            def.lzf = WorkflowDefinitionApi.createLZFDataFormat(element.lzf);
        }
        if (element?.mimeMultipart !== undefined) {
            def.mimeMultipart = WorkflowDefinitionApi.createMimeMultipartDataFormat(element.mimeMultipart);
        }
        if (element?.parquetAvro !== undefined) {
            def.parquetAvro = WorkflowDefinitionApi.createParquetAvroDataFormat(element.parquetAvro);
        }
        if (element?.pgp !== undefined) {
            def.pgp = WorkflowDefinitionApi.createPGPDataFormat(element.pgp);
        }
        if (element?.protobuf !== undefined) {
            def.protobuf = WorkflowDefinitionApi.createProtobufDataFormat(element.protobuf);
        }
        if (element?.rss !== undefined) {
            def.rss = WorkflowDefinitionApi.createRssDataFormat(element.rss);
        }
        if (element?.smooks !== undefined) {
            def.smooks = WorkflowDefinitionApi.createSmooksDataFormat(element.smooks);
        }
        if (element?.soap !== undefined) {
            def.soap = WorkflowDefinitionApi.createSoapDataFormat(element.soap);
        }
        if (element?.swiftMt !== undefined) {
            def.swiftMt = WorkflowDefinitionApi.createSwiftMtDataFormat(element.swiftMt);
        }
        if (element?.swiftMx !== undefined) {
            def.swiftMx = WorkflowDefinitionApi.createSwiftMxDataFormat(element.swiftMx);
        }
        if (element?.syslog !== undefined) {
            def.syslog = WorkflowDefinitionApi.createSyslogDataFormat(element.syslog);
        }
        if (element?.tarFile !== undefined) {
            def.tarFile = WorkflowDefinitionApi.createTarFileDataFormat(element.tarFile);
        }
        if (element?.thrift !== undefined) {
            def.thrift = WorkflowDefinitionApi.createThriftDataFormat(element.thrift);
        }
        if (element?.tidyMarkup !== undefined) {
            def.tidyMarkup = WorkflowDefinitionApi.createTidyMarkupDataFormat(element.tidyMarkup);
        }
        if (element?.univocityCsv !== undefined) {
            def.univocityCsv = WorkflowDefinitionApi.createUniVocityCsvDataFormat(element.univocityCsv);
        }
        if (element?.univocityFixed !== undefined) {
            def.univocityFixed = WorkflowDefinitionApi.createUniVocityFixedDataFormat(element.univocityFixed);
        }
        if (element?.univocityTsv !== undefined) {
            def.univocityTsv = WorkflowDefinitionApi.createUniVocityTsvDataFormat(element.univocityTsv);
        }
        if (element?.xmlSecurity !== undefined) {
            def.xmlSecurity = WorkflowDefinitionApi.createXMLSecurityDataFormat(element.xmlSecurity);
        }
        if (element?.yaml !== undefined) {
            def.yaml = WorkflowDefinitionApi.createYAMLDataFormat(element.yaml);
        }
        if (element?.zipDeflater !== undefined) {
            def.zipDeflater = WorkflowDefinitionApi.createZipDeflaterDataFormat(element.zipDeflater);
        }
        if (element?.zipFile !== undefined) {
            def.zipFile = WorkflowDefinitionApi.createZipFileDataFormat(element.zipFile);
        }
        return def;
    }

    static createFhirJsonDataFormat = (element: any): FhirJsonDataFormat => {
        const def = element ? new FhirJsonDataFormat({...element}) : new FhirJsonDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createFhirXmlDataFormat = (element: any): FhirXmlDataFormat => {
        const def = element ? new FhirXmlDataFormat({...element}) : new FhirXmlDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createFlatpackDataFormat = (element: any): FlatpackDataFormat => {
        const def = element ? new FlatpackDataFormat({...element}) : new FlatpackDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createFuryDataFormat = (element: any): FuryDataFormat => {
        const def = element ? new FuryDataFormat({...element}) : new FuryDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createGrokDataFormat = (element: any): GrokDataFormat => {
        const def = element ? new GrokDataFormat({...element}) : new GrokDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createGzipDeflaterDataFormat = (element: any): GzipDeflaterDataFormat => {
        const def = element ? new GzipDeflaterDataFormat({...element}) : new GzipDeflaterDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createHL7DataFormat = (element: any): HL7DataFormat => {
        const def = element ? new HL7DataFormat({...element}) : new HL7DataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createIcalDataFormat = (element: any): IcalDataFormat => {
        const def = element ? new IcalDataFormat({...element}) : new IcalDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJacksonXMLDataFormat = (element: any): JacksonXMLDataFormat => {
        const def = element ? new JacksonXMLDataFormat({...element}) : new JacksonXMLDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJaxbDataFormat = (element: any): JaxbDataFormat => {
        const def = element ? new JaxbDataFormat({...element}) : new JaxbDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJsonApiDataFormat = (element: any): JsonApiDataFormat => {
        const def = element ? new JsonApiDataFormat({...element}) : new JsonApiDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJsonDataFormat = (element: any): JsonDataFormat => {
        const def = element ? new JsonDataFormat({...element}) : new JsonDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLZFDataFormat = (element: any): LZFDataFormat => {
        const def = element ? new LZFDataFormat({...element}) : new LZFDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createMimeMultipartDataFormat = (element: any): MimeMultipartDataFormat => {
        const def = element ? new MimeMultipartDataFormat({...element}) : new MimeMultipartDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPGPDataFormat = (element: any): PGPDataFormat => {
        const def = element ? new PGPDataFormat({...element}) : new PGPDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createParquetAvroDataFormat = (element: any): ParquetAvroDataFormat => {
        const def = element ? new ParquetAvroDataFormat({...element}) : new ParquetAvroDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createProtobufDataFormat = (element: any): ProtobufDataFormat => {
        const def = element ? new ProtobufDataFormat({...element}) : new ProtobufDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRssDataFormat = (element: any): RssDataFormat => {
        const def = element ? new RssDataFormat({...element}) : new RssDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSmooksDataFormat = (element: any): SmooksDataFormat => {
        const def = element ? new SmooksDataFormat({...element}) : new SmooksDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSoapDataFormat = (element: any): SoapDataFormat => {
        if (element && typeof element === 'string') {
            element = {contextPath: element};
        }
        const def = element ? new SoapDataFormat({...element}) : new SoapDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSwiftMtDataFormat = (element: any): SwiftMtDataFormat => {
        const def = element ? new SwiftMtDataFormat({...element}) : new SwiftMtDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSwiftMxDataFormat = (element: any): SwiftMxDataFormat => {
        const def = element ? new SwiftMxDataFormat({...element}) : new SwiftMxDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSyslogDataFormat = (element: any): SyslogDataFormat => {
        const def = element ? new SyslogDataFormat({...element}) : new SyslogDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTarFileDataFormat = (element: any): TarFileDataFormat => {
        const def = element ? new TarFileDataFormat({...element}) : new TarFileDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createThriftDataFormat = (element: any): ThriftDataFormat => {
        const def = element ? new ThriftDataFormat({...element}) : new ThriftDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTidyMarkupDataFormat = (element: any): TidyMarkupDataFormat => {
        const def = element ? new TidyMarkupDataFormat({...element}) : new TidyMarkupDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createUniVocityCsvDataFormat = (element: any): UniVocityCsvDataFormat => {
        const def = element ? new UniVocityCsvDataFormat({...element}) : new UniVocityCsvDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.univocityHeader = element && element?.univocityHeader ? element?.univocityHeader.map((x:any) => WorkflowDefinitionApi.createUniVocityHeader(x)) :[];
        return def;
    }

    static createUniVocityFixedDataFormat = (element: any): UniVocityFixedDataFormat => {
        const def = element ? new UniVocityFixedDataFormat({...element}) : new UniVocityFixedDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.univocityHeader = element && element?.univocityHeader ? element?.univocityHeader.map((x:any) => WorkflowDefinitionApi.createUniVocityHeader(x)) :[];
        return def;
    }

    static createUniVocityHeader = (element: any): UniVocityHeader => {
        const def = element ? new UniVocityHeader({...element}) : new UniVocityHeader();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createUniVocityTsvDataFormat = (element: any): UniVocityTsvDataFormat => {
        const def = element ? new UniVocityTsvDataFormat({...element}) : new UniVocityTsvDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.univocityHeader = element && element?.univocityHeader ? element?.univocityHeader.map((x:any) => WorkflowDefinitionApi.createUniVocityHeader(x)) :[];
        return def;
    }

    static createXMLSecurityDataFormat = (element: any): XMLSecurityDataFormat => {
        const def = element ? new XMLSecurityDataFormat({...element}) : new XMLSecurityDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createYAMLDataFormat = (element: any): YAMLDataFormat => {
        const def = element ? new YAMLDataFormat({...element}) : new YAMLDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.typeFilter = element && element?.typeFilter ? element?.typeFilter.map((x:any) => WorkflowDefinitionApi.createYAMLTypeFilterDefinition(x)) :[];
        return def;
    }

    static createYAMLTypeFilterDefinition = (element: any): YAMLTypeFilterDefinition => {
        const def = element ? new YAMLTypeFilterDefinition({...element}) : new YAMLTypeFilterDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createZipDeflaterDataFormat = (element: any): ZipDeflaterDataFormat => {
        const def = element ? new ZipDeflaterDataFormat({...element}) : new ZipDeflaterDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createZipFileDataFormat = (element: any): ZipFileDataFormat => {
        const def = element ? new ZipFileDataFormat({...element}) : new ZipFileDataFormat();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDeadLetterChannelDefinition = (element: any): DeadLetterChannelDefinition => {
        const def = element ? new DeadLetterChannelDefinition({...element}) : new DeadLetterChannelDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.redeliveryPolicy !== undefined) {
            def.redeliveryPolicy = WorkflowDefinitionApi.createRedeliveryPolicyDefinition(element.redeliveryPolicy);
        }
        return def;
    }

    static createDefaultErrorHandlerDefinition = (element: any): DefaultErrorHandlerDefinition => {
        const def = element ? new DefaultErrorHandlerDefinition({...element}) : new DefaultErrorHandlerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.redeliveryPolicy !== undefined) {
            def.redeliveryPolicy = WorkflowDefinitionApi.createRedeliveryPolicyDefinition(element.redeliveryPolicy);
        }
        return def;
    }

    static createJtaTransactionErrorHandlerDefinition = (element: any): JtaTransactionErrorHandlerDefinition => {
        const def = element ? new JtaTransactionErrorHandlerDefinition({...element}) : new JtaTransactionErrorHandlerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.redeliveryPolicy !== undefined) {
            def.redeliveryPolicy = WorkflowDefinitionApi.createRedeliveryPolicyDefinition(element.redeliveryPolicy);
        }
        return def;
    }

    static createNoErrorHandlerDefinition = (element: any): NoErrorHandlerDefinition => {
        const def = element ? new NoErrorHandlerDefinition({...element}) : new NoErrorHandlerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRefErrorHandlerDefinition = (element: any): RefErrorHandlerDefinition => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new RefErrorHandlerDefinition({...element}) : new RefErrorHandlerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSpringTransactionErrorHandlerDefinition = (element: any): SpringTransactionErrorHandlerDefinition => {
        const def = element ? new SpringTransactionErrorHandlerDefinition({...element}) : new SpringTransactionErrorHandlerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.redeliveryPolicy !== undefined) {
            def.redeliveryPolicy = WorkflowDefinitionApi.createRedeliveryPolicyDefinition(element.redeliveryPolicy);
        }
        return def;
    }

    static createCSimpleExpression = (element: any): CSimpleExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new CSimpleExpression({...element}) : new CSimpleExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createConstantExpression = (element: any): ConstantExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new ConstantExpression({...element}) : new ConstantExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDatasonnetExpression = (element: any): DatasonnetExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new DatasonnetExpression({...element}) : new DatasonnetExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createExchangePropertyExpression = (element: any): ExchangePropertyExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new ExchangePropertyExpression({...element}) : new ExchangePropertyExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createExpressionDefinition = (element: any): ExpressionDefinition => {
        const def = element ? new ExpressionDefinition({...element}) : new ExpressionDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        element = element !== undefined ? element : {groovy: WorkflowDefinitionApi.createGroovyExpression({expression: ""})}
        if (element?.constant !== undefined) {
            def.constant = WorkflowDefinitionApi.createConstantExpression(element.constant);
        }
        if (element?.csimple !== undefined) {
            def.csimple = WorkflowDefinitionApi.createCSimpleExpression(element.csimple);
        }
        if (element?.datasonnet !== undefined) {
            def.datasonnet = WorkflowDefinitionApi.createDatasonnetExpression(element.datasonnet);
        }
        if (element?.exchangeProperty !== undefined) {
            def.exchangeProperty = WorkflowDefinitionApi.createExchangePropertyExpression(element.exchangeProperty);
        }
        if (element?.groovy !== undefined) {
            def.groovy = WorkflowDefinitionApi.createGroovyExpression(element.groovy);
        }
        if (element?.header !== undefined) {
            def.header = WorkflowDefinitionApi.createHeaderExpression(element.header);
        }
        if (element?.hl7terser !== undefined) {
            def.hl7terser = WorkflowDefinitionApi.createHl7TerserExpression(element.hl7terser);
        }
        if (element?.java !== undefined) {
            def.java = WorkflowDefinitionApi.createJavaExpression(element.java);
        }
        if (element?.jq !== undefined) {
            def.jq = WorkflowDefinitionApi.createJqExpression(element.jq);
        }
        if (element?.js !== undefined) {
            def.js = WorkflowDefinitionApi.createJavaScriptExpression(element.js);
        }
        if (element?.jsonpath !== undefined) {
            def.jsonpath = WorkflowDefinitionApi.createJsonPathExpression(element.jsonpath);
        }
        if (element?.language !== undefined) {
            def.language = WorkflowDefinitionApi.createLanguageExpression(element.language);
        }
        if (element?.method !== undefined) {
            def.method = WorkflowDefinitionApi.createMethodCallExpression(element.method);
        }
        if (element?.mvel !== undefined) {
            def.mvel = WorkflowDefinitionApi.createMvelExpression(element.mvel);
        }
        if (element?.ognl !== undefined) {
            def.ognl = WorkflowDefinitionApi.createOgnlExpression(element.ognl);
        }
        if (element?.python !== undefined) {
            def.python = WorkflowDefinitionApi.createPythonExpression(element.python);
        }
        if (element?.ref !== undefined) {
            def.ref = WorkflowDefinitionApi.createRefExpression(element.ref);
        }
        if (element?.simple !== undefined) {
            def.simple = WorkflowDefinitionApi.createSimpleExpression(element.simple);
        }
        if (element?.spel !== undefined) {
            def.spel = WorkflowDefinitionApi.createSpELExpression(element.spel);
        }
        if (element?.tokenize !== undefined) {
            def.tokenize = WorkflowDefinitionApi.createTokenizerExpression(element.tokenize);
        }
        if (element?.variable !== undefined) {
            def.variable = WorkflowDefinitionApi.createVariableExpression(element.variable);
        }
        if (element?.wasm !== undefined) {
            def.wasm = WorkflowDefinitionApi.createWasmExpression(element.wasm);
        }
        if (element?.xpath !== undefined) {
            def.xpath = WorkflowDefinitionApi.createXPathExpression(element.xpath);
        }
        if (element?.xquery !== undefined) {
            def.xquery = WorkflowDefinitionApi.createXQueryExpression(element.xquery);
        }
        if (element?.xtokenize !== undefined) {
            def.xtokenize = WorkflowDefinitionApi.createXMLTokenizerExpression(element.xtokenize);
        }
        return def;
    }

    static createGroovyExpression = (element: any): GroovyExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new GroovyExpression({...element}) : new GroovyExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createHeaderExpression = (element: any): HeaderExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new HeaderExpression({...element}) : new HeaderExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createHl7TerserExpression = (element: any): Hl7TerserExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new Hl7TerserExpression({...element}) : new Hl7TerserExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJavaExpression = (element: any): JavaExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new JavaExpression({...element}) : new JavaExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJavaScriptExpression = (element: any): JavaScriptExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new JavaScriptExpression({...element}) : new JavaScriptExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJqExpression = (element: any): JqExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new JqExpression({...element}) : new JqExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createJsonPathExpression = (element: any): JsonPathExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new JsonPathExpression({...element}) : new JsonPathExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLanguageExpression = (element: any): LanguageExpression => {
        const def = element ? new LanguageExpression({...element}) : new LanguageExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createMethodCallExpression = (element: any): MethodCallExpression => {
        const def = element ? new MethodCallExpression({...element}) : new MethodCallExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createMvelExpression = (element: any): MvelExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new MvelExpression({...element}) : new MvelExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createOgnlExpression = (element: any): OgnlExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new OgnlExpression({...element}) : new OgnlExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPythonExpression = (element: any): PythonExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new PythonExpression({...element}) : new PythonExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRefExpression = (element: any): RefExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new RefExpression({...element}) : new RefExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSimpleExpression = (element: any): SimpleExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new SimpleExpression({...element}) : new SimpleExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createSpELExpression = (element: any): SpELExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new SpELExpression({...element}) : new SpELExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTokenizerExpression = (element: any): TokenizerExpression => {
        if (element && typeof element === 'string') {
            element = {token: element};
        }
        const def = element ? new TokenizerExpression({...element}) : new TokenizerExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createVariableExpression = (element: any): VariableExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new VariableExpression({...element}) : new VariableExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createWasmExpression = (element: any): WasmExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new WasmExpression({...element}) : new WasmExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createXMLTokenizerExpression = (element: any): XMLTokenizerExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new XMLTokenizerExpression({...element}) : new XMLTokenizerExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.namespace = element && element?.namespace ? element?.namespace.map((x:any) => WorkflowDefinitionApi.createPropertyDefinition(x)) :[];
        return def;
    }

    static createXPathExpression = (element: any): XPathExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new XPathExpression({...element}) : new XPathExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.namespace = element && element?.namespace ? element?.namespace.map((x:any) => WorkflowDefinitionApi.createPropertyDefinition(x)) :[];
        return def;
    }

    static createXQueryExpression = (element: any): XQueryExpression => {
        if (element && typeof element === 'string') {
            element = {expression: element};
        }
        const def = element ? new XQueryExpression({...element}) : new XQueryExpression();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.namespace = element && element?.namespace ? element?.namespace.map((x:any) => WorkflowDefinitionApi.createPropertyDefinition(x)) :[];
        return def;
    }

    static createCustomLoadBalancerDefinition = (element: any): CustomLoadBalancerDefinition => {
        if (element && typeof element === 'string') {
            element = {ref: element};
        }
        const def = element ? new CustomLoadBalancerDefinition({...element}) : new CustomLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createFailoverLoadBalancerDefinition = (element: any): FailoverLoadBalancerDefinition => {
        const def = element ? new FailoverLoadBalancerDefinition({...element}) : new FailoverLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRandomLoadBalancerDefinition = (element: any): RandomLoadBalancerDefinition => {
        const def = element ? new RandomLoadBalancerDefinition({...element}) : new RandomLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRoundRobinLoadBalancerDefinition = (element: any): RoundRobinLoadBalancerDefinition => {
        const def = element ? new RoundRobinLoadBalancerDefinition({...element}) : new RoundRobinLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createStickyLoadBalancerDefinition = (element: any): StickyLoadBalancerDefinition => {
        const def = element ? new StickyLoadBalancerDefinition({...element}) : new StickyLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.correlationExpression !== undefined) {
            def.correlationExpression = WorkflowDefinitionApi.createExpressionSubElementDefinition(element.correlationExpression);
        }
        return def;
    }

    static createTopicLoadBalancerDefinition = (element: any): TopicLoadBalancerDefinition => {
        const def = element ? new TopicLoadBalancerDefinition({...element}) : new TopicLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createWeightedLoadBalancerDefinition = (element: any): WeightedLoadBalancerDefinition => {
        const def = element ? new WeightedLoadBalancerDefinition({...element}) : new WeightedLoadBalancerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createApiKeyDefinition = (element: any): ApiKeyDefinition => {
        const def = element ? new ApiKeyDefinition({...element}) : new ApiKeyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBasicAuthDefinition = (element: any): BasicAuthDefinition => {
        const def = element ? new BasicAuthDefinition({...element}) : new BasicAuthDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createBearerTokenDefinition = (element: any): BearerTokenDefinition => {
        const def = element ? new BearerTokenDefinition({...element}) : new BearerTokenDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDeleteDefinition = (element: any): DeleteDefinition => {
        const def = element ? new DeleteDefinition({...element}) : new DeleteDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionApi.createParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionApi.createResponseMessageDefinition(x)) :[];
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        return def;
    }

    static createGetDefinition = (element: any): GetDefinition => {
        const def = element ? new GetDefinition({...element}) : new GetDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionApi.createParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionApi.createResponseMessageDefinition(x)) :[];
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        return def;
    }

    static createHeadDefinition = (element: any): HeadDefinition => {
        const def = element ? new HeadDefinition({...element}) : new HeadDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionApi.createParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionApi.createResponseMessageDefinition(x)) :[];
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        return def;
    }

    static createMutualTLSDefinition = (element: any): MutualTLSDefinition => {
        const def = element ? new MutualTLSDefinition({...element}) : new MutualTLSDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createOAuth2Definition = (element: any): OAuth2Definition => {
        const def = element ? new OAuth2Definition({...element}) : new OAuth2Definition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.scopes = element && element?.scopes ? element?.scopes.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        return def;
    }

    static createOpenApiDefinition = (element: any): OpenApiDefinition => {
        const def = element ? new OpenApiDefinition({...element}) : new OpenApiDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createOpenIdConnectDefinition = (element: any): OpenIdConnectDefinition => {
        const def = element ? new OpenIdConnectDefinition({...element}) : new OpenIdConnectDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createParamDefinition = (element: any): ParamDefinition => {
        const def = element ? new ParamDefinition({...element}) : new ParamDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.examples = element && element?.examples ? element?.examples.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        return def;
    }

    static createPatchDefinition = (element: any): PatchDefinition => {
        const def = element ? new PatchDefinition({...element}) : new PatchDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionApi.createParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionApi.createResponseMessageDefinition(x)) :[];
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        return def;
    }

    static createPostDefinition = (element: any): PostDefinition => {
        const def = element ? new PostDefinition({...element}) : new PostDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionApi.createParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionApi.createResponseMessageDefinition(x)) :[];
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        return def;
    }

    static createPutDefinition = (element: any): PutDefinition => {
        const def = element ? new PutDefinition({...element}) : new PutDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionApi.createParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionApi.createResponseMessageDefinition(x)) :[];
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        return def;
    }

    static createResponseHeaderDefinition = (element: any): ResponseHeaderDefinition => {
        const def = element ? new ResponseHeaderDefinition({...element}) : new ResponseHeaderDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createResponseMessageDefinition = (element: any): ResponseMessageDefinition => {
        const def = element ? new ResponseMessageDefinition({...element}) : new ResponseMessageDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.header = element && element?.header ? element?.header.map((x:any) => WorkflowDefinitionApi.createResponseHeaderDefinition(x)) :[];
        def.examples = element && element?.examples ? element?.examples.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        return def;
    }

    static createRestBindingDefinition = (element: any): RestBindingDefinition => {
        const def = element ? new RestBindingDefinition({...element}) : new RestBindingDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRestConfigurationDefinition = (element: any): RestConfigurationDefinition => {
        const def = element ? new RestConfigurationDefinition({...element}) : new RestConfigurationDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.componentProperty = element && element?.componentProperty ? element?.componentProperty.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        def.endpointProperty = element && element?.endpointProperty ? element?.endpointProperty.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        def.consumerProperty = element && element?.consumerProperty ? element?.consumerProperty.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        def.dataFormatProperty = element && element?.dataFormatProperty ? element?.dataFormatProperty.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        def.apiProperty = element && element?.apiProperty ? element?.apiProperty.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        def.corsHeaders = element && element?.corsHeaders ? element?.corsHeaders.map((x:any) => WorkflowDefinitionApi.createRestPropertyDefinition(x)) :[];
        return def;
    }

    static createRestDefinition = (element: any): RestDefinition => {
        const def = element ? new RestDefinition({...element}) : new RestDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.openApi !== undefined) {
            def.openApi = WorkflowDefinitionApi.createOpenApiDefinition(element.openApi);
        }
        if (element?.securityDefinitions !== undefined) {
            def.securityDefinitions = WorkflowDefinitionApi.createRestSecuritiesDefinition(element.securityDefinitions);
        }
        def.securityRequirements = element && element?.securityRequirements ? element?.securityRequirements.map((x:any) => WorkflowDefinitionApi.createSecurityDefinition(x)) :[];
        def.delete = element && element?.delete ? element?.delete.map((x:any) => WorkflowDefinitionApi.createDeleteDefinition(x)) :[];
        def.get = element && element?.get ? element?.get.map((x:any) => WorkflowDefinitionApi.createGetDefinition(x)) :[];
        def.head = element && element?.head ? element?.head.map((x:any) => WorkflowDefinitionApi.createHeadDefinition(x)) :[];
        def.patch = element && element?.patch ? element?.patch.map((x:any) => WorkflowDefinitionApi.createPatchDefinition(x)) :[];
        def.post = element && element?.post ? element?.post.map((x:any) => WorkflowDefinitionApi.createPostDefinition(x)) :[];
        def.put = element && element?.put ? element?.put.map((x:any) => WorkflowDefinitionApi.createPutDefinition(x)) :[];
        return def;
    }

    static createRestPropertyDefinition = (element: any): RestPropertyDefinition => {
        const def = element ? new RestPropertyDefinition({...element}) : new RestPropertyDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createRestSecuritiesDefinition = (element: any): RestSecuritiesDefinition => {
        const def = element ? new RestSecuritiesDefinition({...element}) : new RestSecuritiesDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.apiKey !== undefined) {
            def.apiKey = WorkflowDefinitionApi.createApiKeyDefinition(element.apiKey);
        }
        if (element?.basicAuth !== undefined) {
            def.basicAuth = WorkflowDefinitionApi.createBasicAuthDefinition(element.basicAuth);
        }
        if (element?.bearer !== undefined) {
            def.bearer = WorkflowDefinitionApi.createBearerTokenDefinition(element.bearer);
        }
        if (element?.mutualTLS !== undefined) {
            def.mutualTLS = WorkflowDefinitionApi.createMutualTLSDefinition(element.mutualTLS);
        }
        if (element?.oauth2 !== undefined) {
            def.oauth2 = WorkflowDefinitionApi.createOAuth2Definition(element.oauth2);
        }
        if (element?.openIdConnect !== undefined) {
            def.openIdConnect = WorkflowDefinitionApi.createOpenIdConnectDefinition(element.openIdConnect);
        }
        return def;
    }

    static createRestsDefinition = (element: any): RestsDefinition => {
        const def = element ? new RestsDefinition({...element}) : new RestsDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.rest = element && element?.rest ? element?.rest.map((x:any) => WorkflowDefinitionApi.createRestDefinition(x)) :[];
        return def;
    }

    static createSecurityDefinition = (element: any): SecurityDefinition => {
        const def = element ? new SecurityDefinition({...element}) : new SecurityDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLangChain4jCharacterTokenizerDefinition = (element: any): LangChain4jCharacterTokenizerDefinition => {
        const def = element ? new LangChain4jCharacterTokenizerDefinition({...element}) : new LangChain4jCharacterTokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLangChain4jLineTokenizerDefinition = (element: any): LangChain4jLineTokenizerDefinition => {
        const def = element ? new LangChain4jLineTokenizerDefinition({...element}) : new LangChain4jLineTokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLangChain4jParagraphTokenizerDefinition = (element: any): LangChain4jParagraphTokenizerDefinition => {
        const def = element ? new LangChain4jParagraphTokenizerDefinition({...element}) : new LangChain4jParagraphTokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLangChain4jSentenceTokenizerDefinition = (element: any): LangChain4jSentenceTokenizerDefinition => {
        const def = element ? new LangChain4jSentenceTokenizerDefinition({...element}) : new LangChain4jSentenceTokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLangChain4jTokenizerDefinition = (element: any): LangChain4jTokenizerDefinition => {
        const def = element ? new LangChain4jTokenizerDefinition({...element}) : new LangChain4jTokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLangChain4jWordTokenizerDefinition = (element: any): LangChain4jWordTokenizerDefinition => {
        const def = element ? new LangChain4jWordTokenizerDefinition({...element}) : new LangChain4jWordTokenizerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createCustomTransformerDefinition = (element: any): CustomTransformerDefinition => {
        const def = element ? new CustomTransformerDefinition({...element}) : new CustomTransformerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createDataFormatTransformerDefinition = (element: any): DataFormatTransformerDefinition => {
        const def = element ? new DataFormatTransformerDefinition({...element}) : new DataFormatTransformerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.asn1 !== undefined) {
            def.asn1 = WorkflowDefinitionApi.createASN1DataFormat(element.asn1);
        }
        if (element?.avro !== undefined) {
            def.avro = WorkflowDefinitionApi.createAvroDataFormat(element.avro);
        }
        if (element?.barcode !== undefined) {
            def.barcode = WorkflowDefinitionApi.createBarcodeDataFormat(element.barcode);
        }
        if (element?.base64 !== undefined) {
            def.base64 = WorkflowDefinitionApi.createBase64DataFormat(element.base64);
        }
        if (element?.beanio !== undefined) {
            def.beanio = WorkflowDefinitionApi.createBeanioDataFormat(element.beanio);
        }
        if (element?.bindy !== undefined) {
            def.bindy = WorkflowDefinitionApi.createBindyDataFormat(element.bindy);
        }
        if (element?.cbor !== undefined) {
            def.cbor = WorkflowDefinitionApi.createCBORDataFormat(element.cbor);
        }
        if (element?.crypto !== undefined) {
            def.crypto = WorkflowDefinitionApi.createCryptoDataFormat(element.crypto);
        }
        if (element?.csv !== undefined) {
            def.csv = WorkflowDefinitionApi.createCsvDataFormat(element.csv);
        }
        if (element?.custom !== undefined) {
            def.custom = WorkflowDefinitionApi.createCustomDataFormat(element.custom);
        }
        if (element?.fhirJson !== undefined) {
            def.fhirJson = WorkflowDefinitionApi.createFhirJsonDataFormat(element.fhirJson);
        }
        if (element?.fhirXml !== undefined) {
            def.fhirXml = WorkflowDefinitionApi.createFhirXmlDataFormat(element.fhirXml);
        }
        if (element?.flatpack !== undefined) {
            def.flatpack = WorkflowDefinitionApi.createFlatpackDataFormat(element.flatpack);
        }
        if (element?.fury !== undefined) {
            def.fury = WorkflowDefinitionApi.createFuryDataFormat(element.fury);
        }
        if (element?.grok !== undefined) {
            def.grok = WorkflowDefinitionApi.createGrokDataFormat(element.grok);
        }
        if (element?.gzipDeflater !== undefined) {
            def.gzipDeflater = WorkflowDefinitionApi.createGzipDeflaterDataFormat(element.gzipDeflater);
        }
        if (element?.hl7 !== undefined) {
            def.hl7 = WorkflowDefinitionApi.createHL7DataFormat(element.hl7);
        }
        if (element?.ical !== undefined) {
            def.ical = WorkflowDefinitionApi.createIcalDataFormat(element.ical);
        }
        if (element?.jacksonXml !== undefined) {
            def.jacksonXml = WorkflowDefinitionApi.createJacksonXMLDataFormat(element.jacksonXml);
        }
        if (element?.jaxb !== undefined) {
            def.jaxb = WorkflowDefinitionApi.createJaxbDataFormat(element.jaxb);
        }
        if (element?.json !== undefined) {
            def.json = WorkflowDefinitionApi.createJsonDataFormat(element.json);
        }
        if (element?.jsonApi !== undefined) {
            def.jsonApi = WorkflowDefinitionApi.createJsonApiDataFormat(element.jsonApi);
        }
        if (element?.lzf !== undefined) {
            def.lzf = WorkflowDefinitionApi.createLZFDataFormat(element.lzf);
        }
        if (element?.mimeMultipart !== undefined) {
            def.mimeMultipart = WorkflowDefinitionApi.createMimeMultipartDataFormat(element.mimeMultipart);
        }
        if (element?.parquetAvro !== undefined) {
            def.parquetAvro = WorkflowDefinitionApi.createParquetAvroDataFormat(element.parquetAvro);
        }
        if (element?.pgp !== undefined) {
            def.pgp = WorkflowDefinitionApi.createPGPDataFormat(element.pgp);
        }
        if (element?.protobuf !== undefined) {
            def.protobuf = WorkflowDefinitionApi.createProtobufDataFormat(element.protobuf);
        }
        if (element?.rss !== undefined) {
            def.rss = WorkflowDefinitionApi.createRssDataFormat(element.rss);
        }
        if (element?.smooks !== undefined) {
            def.smooks = WorkflowDefinitionApi.createSmooksDataFormat(element.smooks);
        }
        if (element?.soap !== undefined) {
            def.soap = WorkflowDefinitionApi.createSoapDataFormat(element.soap);
        }
        if (element?.swiftMt !== undefined) {
            def.swiftMt = WorkflowDefinitionApi.createSwiftMtDataFormat(element.swiftMt);
        }
        if (element?.swiftMx !== undefined) {
            def.swiftMx = WorkflowDefinitionApi.createSwiftMxDataFormat(element.swiftMx);
        }
        if (element?.syslog !== undefined) {
            def.syslog = WorkflowDefinitionApi.createSyslogDataFormat(element.syslog);
        }
        if (element?.tarFile !== undefined) {
            def.tarFile = WorkflowDefinitionApi.createTarFileDataFormat(element.tarFile);
        }
        if (element?.thrift !== undefined) {
            def.thrift = WorkflowDefinitionApi.createThriftDataFormat(element.thrift);
        }
        if (element?.tidyMarkup !== undefined) {
            def.tidyMarkup = WorkflowDefinitionApi.createTidyMarkupDataFormat(element.tidyMarkup);
        }
        if (element?.univocityCsv !== undefined) {
            def.univocityCsv = WorkflowDefinitionApi.createUniVocityCsvDataFormat(element.univocityCsv);
        }
        if (element?.univocityFixed !== undefined) {
            def.univocityFixed = WorkflowDefinitionApi.createUniVocityFixedDataFormat(element.univocityFixed);
        }
        if (element?.univocityTsv !== undefined) {
            def.univocityTsv = WorkflowDefinitionApi.createUniVocityTsvDataFormat(element.univocityTsv);
        }
        if (element?.xmlSecurity !== undefined) {
            def.xmlSecurity = WorkflowDefinitionApi.createXMLSecurityDataFormat(element.xmlSecurity);
        }
        if (element?.yaml !== undefined) {
            def.yaml = WorkflowDefinitionApi.createYAMLDataFormat(element.yaml);
        }
        if (element?.zipDeflater !== undefined) {
            def.zipDeflater = WorkflowDefinitionApi.createZipDeflaterDataFormat(element.zipDeflater);
        }
        if (element?.zipFile !== undefined) {
            def.zipFile = WorkflowDefinitionApi.createZipFileDataFormat(element.zipFile);
        }
        return def;
    }

    static createEndpointTransformerDefinition = (element: any): EndpointTransformerDefinition => {
        const def = element ? new EndpointTransformerDefinition({...element}) : new EndpointTransformerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createLoadTransformerDefinition = (element: any): LoadTransformerDefinition => {
        const def = element ? new LoadTransformerDefinition({...element}) : new LoadTransformerDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createTransformersDefinition = (element: any): TransformersDefinition => {
        const def = element ? new TransformersDefinition({...element}) : new TransformersDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.customTransformer !== undefined) {
            def.customTransformer = WorkflowDefinitionApi.createCustomTransformerDefinition(element.customTransformer);
        }
        if (element?.dataFormatTransformer !== undefined) {
            def.dataFormatTransformer = WorkflowDefinitionApi.createDataFormatTransformerDefinition(element.dataFormatTransformer);
        }
        if (element?.endpointTransformer !== undefined) {
            def.endpointTransformer = WorkflowDefinitionApi.createEndpointTransformerDefinition(element.endpointTransformer);
        }
        if (element?.loadTransformer !== undefined) {
            def.loadTransformer = WorkflowDefinitionApi.createLoadTransformerDefinition(element.loadTransformer);
        }
        return def;
    }

    static createCustomValidatorDefinition = (element: any): CustomValidatorDefinition => {
        const def = element ? new CustomValidatorDefinition({...element}) : new CustomValidatorDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createEndpointValidatorDefinition = (element: any): EndpointValidatorDefinition => {
        const def = element ? new EndpointValidatorDefinition({...element}) : new EndpointValidatorDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        return def;
    }

    static createPredicateValidatorDefinition = (element: any): PredicateValidatorDefinition => {
        const def = element ? new PredicateValidatorDefinition({...element}) : new PredicateValidatorDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        def.expression = WorkflowDefinitionApi.createExpressionDefinition(element.expression);

        return def;
    }

    static createValidatorsDefinition = (element: any): ValidatorsDefinition => {
        const def = element ? new ValidatorsDefinition({...element}) : new ValidatorsDefinition();
        def.uuid = element?.uuid ? element.uuid : def.uuid;
        if (element?.customValidator !== undefined) {
            def.customValidator = WorkflowDefinitionApi.createCustomValidatorDefinition(element.customValidator);
        }
        if (element?.endpointValidator !== undefined) {
            def.endpointValidator = WorkflowDefinitionApi.createEndpointValidatorDefinition(element.endpointValidator);
        }
        if (element?.predicateValidator !== undefined) {
            def.predicateValidator = WorkflowDefinitionApi.createPredicateValidatorDefinition(element.predicateValidator);
        }
        return def;
    }

    static createStep = (name: string, body: any, clone: boolean = false): WorkflowElement => {
        const newBody = WorkflowUtil.camelizeBody(name, body, clone);
        switch (name) {
            case 'ProcessorDefinition': return WorkflowDefinitionApi.createProcessorDefinition(newBody);
            case 'BeansDeserializer': return WorkflowDefinitionApi.createBeansDeserializer(newBody);
            case 'DataFormatsDefinitionDeserializer': return WorkflowDefinitionApi.createDataFormatsDefinitionDeserializer(newBody);
            case 'ErrorHandlerDeserializer': return WorkflowDefinitionApi.createErrorHandlerDeserializer(newBody);
            case 'OutputAwareFromDefinition': return WorkflowDefinitionApi.createOutputAwareFromDefinition(newBody);
            case 'AggregateDefinition': return WorkflowDefinitionApi.createAggregateDefinition(newBody);
            case 'BeanDefinition': return WorkflowDefinitionApi.createBeanDefinition(newBody);
            case 'BeanFactoryDefinition': return WorkflowDefinitionApi.createBeanFactoryDefinition(newBody);
            case 'CatchDefinition': return WorkflowDefinitionApi.createCatchDefinition(newBody);
            case 'ChoiceDefinition': return WorkflowDefinitionApi.createChoiceDefinition(newBody);
            case 'CircuitBreakerDefinition': return WorkflowDefinitionApi.createCircuitBreakerDefinition(newBody);
            case 'ClaimCheckDefinition': return WorkflowDefinitionApi.createClaimCheckDefinition(newBody);
            case 'ContextScanDefinition': return WorkflowDefinitionApi.createContextScanDefinition(newBody);
            case 'ConvertBodyDefinition': return WorkflowDefinitionApi.createConvertBodyDefinition(newBody);
            case 'ConvertHeaderDefinition': return WorkflowDefinitionApi.createConvertHeaderDefinition(newBody);
            case 'ConvertVariableDefinition': return WorkflowDefinitionApi.createConvertVariableDefinition(newBody);
            case 'DataFormatDefinition': return WorkflowDefinitionApi.createDataFormatDefinition(newBody);
            case 'DelayDefinition': return WorkflowDefinitionApi.createDelayDefinition(newBody);
            case 'DynamicRouterDefinition': return WorkflowDefinitionApi.createDynamicRouterDefinition(newBody);
            case 'EnrichDefinition': return WorkflowDefinitionApi.createEnrichDefinition(newBody);
            case 'ErrorHandlerDefinition': return WorkflowDefinitionApi.createErrorHandlerDefinition(newBody);
            case 'ExpressionSubElementDefinition': return WorkflowDefinitionApi.createExpressionSubElementDefinition(newBody);
            case 'FaultToleranceConfigurationDefinition': return WorkflowDefinitionApi.createFaultToleranceConfigurationDefinition(newBody);
            case 'FilterDefinition': return WorkflowDefinitionApi.createFilterDefinition(newBody);
            case 'FinallyDefinition': return WorkflowDefinitionApi.createFinallyDefinition(newBody);
            case 'FromDefinition': return WorkflowDefinitionApi.createFromDefinition(newBody);
            case 'GlobalOptionDefinition': return WorkflowDefinitionApi.createGlobalOptionDefinition(newBody);
            case 'GlobalOptionsDefinition': return WorkflowDefinitionApi.createGlobalOptionsDefinition(newBody);
            case 'IdempotentConsumerDefinition': return WorkflowDefinitionApi.createIdempotentConsumerDefinition(newBody);
            case 'InputTypeDefinition': return WorkflowDefinitionApi.createInputTypeDefinition(newBody);
            case 'InterceptDefinition': return WorkflowDefinitionApi.createInterceptDefinition(newBody);
            case 'InterceptFromDefinition': return WorkflowDefinitionApi.createInterceptFromDefinition(newBody);
            case 'InterceptSendToEndpointDefinition': return WorkflowDefinitionApi.createInterceptSendToEndpointDefinition(newBody);
            case 'KameletDefinition': return WorkflowDefinitionApi.createKameletDefinition(newBody);
            case 'LoadBalanceDefinition': return WorkflowDefinitionApi.createLoadBalanceDefinition(newBody);
            case 'LogDefinition': return WorkflowDefinitionApi.createLogDefinition(newBody);
            case 'LoopDefinition': return WorkflowDefinitionApi.createLoopDefinition(newBody);
            case 'MarshalDefinition': return WorkflowDefinitionApi.createMarshalDefinition(newBody);
            case 'MulticastDefinition': return WorkflowDefinitionApi.createMulticastDefinition(newBody);
            case 'OnCompletionDefinition': return WorkflowDefinitionApi.createOnCompletionDefinition(newBody);
            case 'OnExceptionDefinition': return WorkflowDefinitionApi.createOnExceptionDefinition(newBody);
            case 'OnFallbackDefinition': return WorkflowDefinitionApi.createOnFallbackDefinition(newBody);
            case 'OnWhenDefinition': return WorkflowDefinitionApi.createOnWhenDefinition(newBody);
            case 'OptimisticLockRetryPolicyDefinition': return WorkflowDefinitionApi.createOptimisticLockRetryPolicyDefinition(newBody);
            case 'OtherwiseDefinition': return WorkflowDefinitionApi.createOtherwiseDefinition(newBody);
            case 'OutputDefinition': return WorkflowDefinitionApi.createOutputDefinition(newBody);
            case 'OutputTypeDefinition': return WorkflowDefinitionApi.createOutputTypeDefinition(newBody);
            case 'PackageScanDefinition': return WorkflowDefinitionApi.createPackageScanDefinition(newBody);
            case 'PausableDefinition': return WorkflowDefinitionApi.createPausableDefinition(newBody);
            case 'PipelineDefinition': return WorkflowDefinitionApi.createPipelineDefinition(newBody);
            case 'PolicyDefinition': return WorkflowDefinitionApi.createPolicyDefinition(newBody);
            case 'PollDefinition': return WorkflowDefinitionApi.createPollDefinition(newBody);
            case 'PollEnrichDefinition': return WorkflowDefinitionApi.createPollEnrichDefinition(newBody);
            case 'ProcessDefinition': return WorkflowDefinitionApi.createProcessDefinition(newBody);
            case 'PropertyDefinition': return WorkflowDefinitionApi.createPropertyDefinition(newBody);
            case 'PropertyExpressionDefinition': return WorkflowDefinitionApi.createPropertyExpressionDefinition(newBody);
            case 'RecipientListDefinition': return WorkflowDefinitionApi.createRecipientListDefinition(newBody);
            case 'RedeliveryPolicyDefinition': return WorkflowDefinitionApi.createRedeliveryPolicyDefinition(newBody);
            case 'RemoveHeaderDefinition': return WorkflowDefinitionApi.createRemoveHeaderDefinition(newBody);
            case 'RemoveHeadersDefinition': return WorkflowDefinitionApi.createRemoveHeadersDefinition(newBody);
            case 'RemovePropertiesDefinition': return WorkflowDefinitionApi.createRemovePropertiesDefinition(newBody);
            case 'RemovePropertyDefinition': return WorkflowDefinitionApi.createRemovePropertyDefinition(newBody);
            case 'RemoveVariableDefinition': return WorkflowDefinitionApi.createRemoveVariableDefinition(newBody);
            case 'ResequenceDefinition': return WorkflowDefinitionApi.createResequenceDefinition(newBody);
            case 'Resilience4jConfigurationDefinition': return WorkflowDefinitionApi.createResilience4jConfigurationDefinition(newBody);
            case 'RestContextRefDefinition': return WorkflowDefinitionApi.createRestContextRefDefinition(newBody);
            case 'ResumableDefinition': return WorkflowDefinitionApi.createResumableDefinition(newBody);
            case 'RollbackDefinition': return WorkflowDefinitionApi.createRollbackDefinition(newBody);
            case 'RouteBuilderDefinition': return WorkflowDefinitionApi.createRouteBuilderDefinition(newBody);
            case 'RouteConfigurationContextRefDefinition': return WorkflowDefinitionApi.createRouteConfigurationContextRefDefinition(newBody);
            case 'RouteConfigurationDefinition': return WorkflowDefinitionApi.createRouteConfigurationDefinition(newBody);
            case 'RouteContextRefDefinition': return WorkflowDefinitionApi.createRouteContextRefDefinition(newBody);
            case 'RouteDefinition': return WorkflowDefinitionApi.createRouteDefinition(newBody);
            case 'RouteTemplateDefinition': return WorkflowDefinitionApi.createRouteTemplateDefinition(newBody);
            case 'RouteTemplateParameterDefinition': return WorkflowDefinitionApi.createRouteTemplateParameterDefinition(newBody);
            case 'RoutingSlipDefinition': return WorkflowDefinitionApi.createRoutingSlipDefinition(newBody);
            case 'SagaActionUriDefinition': return WorkflowDefinitionApi.createSagaActionUriDefinition(newBody);
            case 'SagaDefinition': return WorkflowDefinitionApi.createSagaDefinition(newBody);
            case 'SamplingDefinition': return WorkflowDefinitionApi.createSamplingDefinition(newBody);
            case 'ScriptDefinition': return WorkflowDefinitionApi.createScriptDefinition(newBody);
            case 'SetBodyDefinition': return WorkflowDefinitionApi.createSetBodyDefinition(newBody);
            case 'SetExchangePatternDefinition': return WorkflowDefinitionApi.createSetExchangePatternDefinition(newBody);
            case 'SetHeaderDefinition': return WorkflowDefinitionApi.createSetHeaderDefinition(newBody);
            case 'SetHeadersDefinition': return WorkflowDefinitionApi.createSetHeadersDefinition(newBody);
            case 'SetPropertyDefinition': return WorkflowDefinitionApi.createSetPropertyDefinition(newBody);
            case 'SetVariableDefinition': return WorkflowDefinitionApi.createSetVariableDefinition(newBody);
            case 'SetVariablesDefinition': return WorkflowDefinitionApi.createSetVariablesDefinition(newBody);
            case 'SortDefinition': return WorkflowDefinitionApi.createSortDefinition(newBody);
            case 'SplitDefinition': return WorkflowDefinitionApi.createSplitDefinition(newBody);
            case 'StepDefinition': return WorkflowDefinitionApi.createStepDefinition(newBody);
            case 'StopDefinition': return WorkflowDefinitionApi.createStopDefinition(newBody);
            case 'TemplatedRouteDefinition': return WorkflowDefinitionApi.createTemplatedRouteDefinition(newBody);
            case 'TemplatedRouteParameterDefinition': return WorkflowDefinitionApi.createTemplatedRouteParameterDefinition(newBody);
            case 'ThreadPoolProfileDefinition': return WorkflowDefinitionApi.createThreadPoolProfileDefinition(newBody);
            case 'ThreadsDefinition': return WorkflowDefinitionApi.createThreadsDefinition(newBody);
            case 'ThrottleDefinition': return WorkflowDefinitionApi.createThrottleDefinition(newBody);
            case 'ThrowExceptionDefinition': return WorkflowDefinitionApi.createThrowExceptionDefinition(newBody);
            case 'ToDefinition': return WorkflowDefinitionApi.createToDefinition(newBody);
            case 'ToDynamicDefinition': return WorkflowDefinitionApi.createToDynamicDefinition(newBody);
            case 'TokenizerDefinition': return WorkflowDefinitionApi.createTokenizerDefinition(newBody);
            case 'TokenizerImplementationDefinition': return WorkflowDefinitionApi.createTokenizerImplementationDefinition(newBody);
            case 'TransactedDefinition': return WorkflowDefinitionApi.createTransactedDefinition(newBody);
            case 'TransformDefinition': return WorkflowDefinitionApi.createTransformDefinition(newBody);
            case 'TryDefinition': return WorkflowDefinitionApi.createTryDefinition(newBody);
            case 'UnmarshalDefinition': return WorkflowDefinitionApi.createUnmarshalDefinition(newBody);
            case 'ValidateDefinition': return WorkflowDefinitionApi.createValidateDefinition(newBody);
            case 'ValueDefinition': return WorkflowDefinitionApi.createValueDefinition(newBody);
            case 'WhenDefinition': return WorkflowDefinitionApi.createWhenDefinition(newBody);
            case 'WireTapDefinition': return WorkflowDefinitionApi.createWireTapDefinition(newBody);
            case 'BeanConstructorDefinition': return WorkflowDefinitionApi.createBeanConstructorDefinition(newBody);
            case 'BeanConstructorsDefinition': return WorkflowDefinitionApi.createBeanConstructorsDefinition(newBody);
            case 'BeanPropertiesDefinition': return WorkflowDefinitionApi.createBeanPropertiesDefinition(newBody);
            case 'BeanPropertyDefinition': return WorkflowDefinitionApi.createBeanPropertyDefinition(newBody);
            case 'ComponentScanDefinition': return WorkflowDefinitionApi.createComponentScanDefinition(newBody);
            case 'BatchResequencerConfig': return WorkflowDefinitionApi.createBatchResequencerConfig(newBody);
            case 'StreamResequencerConfig': return WorkflowDefinitionApi.createStreamResequencerConfig(newBody);
            case 'ASN1DataFormat': return WorkflowDefinitionApi.createASN1DataFormat(newBody);
            case 'AvroDataFormat': return WorkflowDefinitionApi.createAvroDataFormat(newBody);
            case 'BarcodeDataFormat': return WorkflowDefinitionApi.createBarcodeDataFormat(newBody);
            case 'Base64DataFormat': return WorkflowDefinitionApi.createBase64DataFormat(newBody);
            case 'BeanioDataFormat': return WorkflowDefinitionApi.createBeanioDataFormat(newBody);
            case 'BindyDataFormat': return WorkflowDefinitionApi.createBindyDataFormat(newBody);
            case 'CBORDataFormat': return WorkflowDefinitionApi.createCBORDataFormat(newBody);
            case 'CryptoDataFormat': return WorkflowDefinitionApi.createCryptoDataFormat(newBody);
            case 'CsvDataFormat': return WorkflowDefinitionApi.createCsvDataFormat(newBody);
            case 'CustomDataFormat': return WorkflowDefinitionApi.createCustomDataFormat(newBody);
            case 'DataFormatsDefinition': return WorkflowDefinitionApi.createDataFormatsDefinition(newBody);
            case 'FhirJsonDataFormat': return WorkflowDefinitionApi.createFhirJsonDataFormat(newBody);
            case 'FhirXmlDataFormat': return WorkflowDefinitionApi.createFhirXmlDataFormat(newBody);
            case 'FlatpackDataFormat': return WorkflowDefinitionApi.createFlatpackDataFormat(newBody);
            case 'FuryDataFormat': return WorkflowDefinitionApi.createFuryDataFormat(newBody);
            case 'GrokDataFormat': return WorkflowDefinitionApi.createGrokDataFormat(newBody);
            case 'GzipDeflaterDataFormat': return WorkflowDefinitionApi.createGzipDeflaterDataFormat(newBody);
            case 'HL7DataFormat': return WorkflowDefinitionApi.createHL7DataFormat(newBody);
            case 'IcalDataFormat': return WorkflowDefinitionApi.createIcalDataFormat(newBody);
            case 'JacksonXMLDataFormat': return WorkflowDefinitionApi.createJacksonXMLDataFormat(newBody);
            case 'JaxbDataFormat': return WorkflowDefinitionApi.createJaxbDataFormat(newBody);
            case 'JsonApiDataFormat': return WorkflowDefinitionApi.createJsonApiDataFormat(newBody);
            case 'JsonDataFormat': return WorkflowDefinitionApi.createJsonDataFormat(newBody);
            case 'LZFDataFormat': return WorkflowDefinitionApi.createLZFDataFormat(newBody);
            case 'MimeMultipartDataFormat': return WorkflowDefinitionApi.createMimeMultipartDataFormat(newBody);
            case 'PGPDataFormat': return WorkflowDefinitionApi.createPGPDataFormat(newBody);
            case 'ParquetAvroDataFormat': return WorkflowDefinitionApi.createParquetAvroDataFormat(newBody);
            case 'ProtobufDataFormat': return WorkflowDefinitionApi.createProtobufDataFormat(newBody);
            case 'RssDataFormat': return WorkflowDefinitionApi.createRssDataFormat(newBody);
            case 'SmooksDataFormat': return WorkflowDefinitionApi.createSmooksDataFormat(newBody);
            case 'SoapDataFormat': return WorkflowDefinitionApi.createSoapDataFormat(newBody);
            case 'SwiftMtDataFormat': return WorkflowDefinitionApi.createSwiftMtDataFormat(newBody);
            case 'SwiftMxDataFormat': return WorkflowDefinitionApi.createSwiftMxDataFormat(newBody);
            case 'SyslogDataFormat': return WorkflowDefinitionApi.createSyslogDataFormat(newBody);
            case 'TarFileDataFormat': return WorkflowDefinitionApi.createTarFileDataFormat(newBody);
            case 'ThriftDataFormat': return WorkflowDefinitionApi.createThriftDataFormat(newBody);
            case 'TidyMarkupDataFormat': return WorkflowDefinitionApi.createTidyMarkupDataFormat(newBody);
            case 'UniVocityCsvDataFormat': return WorkflowDefinitionApi.createUniVocityCsvDataFormat(newBody);
            case 'UniVocityFixedDataFormat': return WorkflowDefinitionApi.createUniVocityFixedDataFormat(newBody);
            case 'UniVocityHeader': return WorkflowDefinitionApi.createUniVocityHeader(newBody);
            case 'UniVocityTsvDataFormat': return WorkflowDefinitionApi.createUniVocityTsvDataFormat(newBody);
            case 'XMLSecurityDataFormat': return WorkflowDefinitionApi.createXMLSecurityDataFormat(newBody);
            case 'YAMLDataFormat': return WorkflowDefinitionApi.createYAMLDataFormat(newBody);
            case 'YAMLTypeFilterDefinition': return WorkflowDefinitionApi.createYAMLTypeFilterDefinition(newBody);
            case 'ZipDeflaterDataFormat': return WorkflowDefinitionApi.createZipDeflaterDataFormat(newBody);
            case 'ZipFileDataFormat': return WorkflowDefinitionApi.createZipFileDataFormat(newBody);
            case 'DeadLetterChannelDefinition': return WorkflowDefinitionApi.createDeadLetterChannelDefinition(newBody);
            case 'DefaultErrorHandlerDefinition': return WorkflowDefinitionApi.createDefaultErrorHandlerDefinition(newBody);
            case 'JtaTransactionErrorHandlerDefinition': return WorkflowDefinitionApi.createJtaTransactionErrorHandlerDefinition(newBody);
            case 'NoErrorHandlerDefinition': return WorkflowDefinitionApi.createNoErrorHandlerDefinition(newBody);
            case 'RefErrorHandlerDefinition': return WorkflowDefinitionApi.createRefErrorHandlerDefinition(newBody);
            case 'SpringTransactionErrorHandlerDefinition': return WorkflowDefinitionApi.createSpringTransactionErrorHandlerDefinition(newBody);
            case 'CSimpleExpression': return WorkflowDefinitionApi.createCSimpleExpression(newBody);
            case 'ConstantExpression': return WorkflowDefinitionApi.createConstantExpression(newBody);
            case 'DatasonnetExpression': return WorkflowDefinitionApi.createDatasonnetExpression(newBody);
            case 'ExchangePropertyExpression': return WorkflowDefinitionApi.createExchangePropertyExpression(newBody);
            case 'ExpressionDefinition': return WorkflowDefinitionApi.createExpressionDefinition(newBody);
            case 'GroovyExpression': return WorkflowDefinitionApi.createGroovyExpression(newBody);
            case 'HeaderExpression': return WorkflowDefinitionApi.createHeaderExpression(newBody);
            case 'Hl7TerserExpression': return WorkflowDefinitionApi.createHl7TerserExpression(newBody);
            case 'JavaExpression': return WorkflowDefinitionApi.createJavaExpression(newBody);
            case 'JavaScriptExpression': return WorkflowDefinitionApi.createJavaScriptExpression(newBody);
            case 'JqExpression': return WorkflowDefinitionApi.createJqExpression(newBody);
            case 'JsonPathExpression': return WorkflowDefinitionApi.createJsonPathExpression(newBody);
            case 'LanguageExpression': return WorkflowDefinitionApi.createLanguageExpression(newBody);
            case 'MethodCallExpression': return WorkflowDefinitionApi.createMethodCallExpression(newBody);
            case 'MvelExpression': return WorkflowDefinitionApi.createMvelExpression(newBody);
            case 'OgnlExpression': return WorkflowDefinitionApi.createOgnlExpression(newBody);
            case 'PythonExpression': return WorkflowDefinitionApi.createPythonExpression(newBody);
            case 'RefExpression': return WorkflowDefinitionApi.createRefExpression(newBody);
            case 'SimpleExpression': return WorkflowDefinitionApi.createSimpleExpression(newBody);
            case 'SpELExpression': return WorkflowDefinitionApi.createSpELExpression(newBody);
            case 'TokenizerExpression': return WorkflowDefinitionApi.createTokenizerExpression(newBody);
            case 'VariableExpression': return WorkflowDefinitionApi.createVariableExpression(newBody);
            case 'WasmExpression': return WorkflowDefinitionApi.createWasmExpression(newBody);
            case 'XMLTokenizerExpression': return WorkflowDefinitionApi.createXMLTokenizerExpression(newBody);
            case 'XPathExpression': return WorkflowDefinitionApi.createXPathExpression(newBody);
            case 'XQueryExpression': return WorkflowDefinitionApi.createXQueryExpression(newBody);
            case 'CustomLoadBalancerDefinition': return WorkflowDefinitionApi.createCustomLoadBalancerDefinition(newBody);
            case 'FailoverLoadBalancerDefinition': return WorkflowDefinitionApi.createFailoverLoadBalancerDefinition(newBody);
            case 'RandomLoadBalancerDefinition': return WorkflowDefinitionApi.createRandomLoadBalancerDefinition(newBody);
            case 'RoundRobinLoadBalancerDefinition': return WorkflowDefinitionApi.createRoundRobinLoadBalancerDefinition(newBody);
            case 'StickyLoadBalancerDefinition': return WorkflowDefinitionApi.createStickyLoadBalancerDefinition(newBody);
            case 'TopicLoadBalancerDefinition': return WorkflowDefinitionApi.createTopicLoadBalancerDefinition(newBody);
            case 'WeightedLoadBalancerDefinition': return WorkflowDefinitionApi.createWeightedLoadBalancerDefinition(newBody);
            case 'ApiKeyDefinition': return WorkflowDefinitionApi.createApiKeyDefinition(newBody);
            case 'BasicAuthDefinition': return WorkflowDefinitionApi.createBasicAuthDefinition(newBody);
            case 'BearerTokenDefinition': return WorkflowDefinitionApi.createBearerTokenDefinition(newBody);
            case 'DeleteDefinition': return WorkflowDefinitionApi.createDeleteDefinition(newBody);
            case 'GetDefinition': return WorkflowDefinitionApi.createGetDefinition(newBody);
            case 'HeadDefinition': return WorkflowDefinitionApi.createHeadDefinition(newBody);
            case 'MutualTLSDefinition': return WorkflowDefinitionApi.createMutualTLSDefinition(newBody);
            case 'OAuth2Definition': return WorkflowDefinitionApi.createOAuth2Definition(newBody);
            case 'OpenApiDefinition': return WorkflowDefinitionApi.createOpenApiDefinition(newBody);
            case 'OpenIdConnectDefinition': return WorkflowDefinitionApi.createOpenIdConnectDefinition(newBody);
            case 'ParamDefinition': return WorkflowDefinitionApi.createParamDefinition(newBody);
            case 'PatchDefinition': return WorkflowDefinitionApi.createPatchDefinition(newBody);
            case 'PostDefinition': return WorkflowDefinitionApi.createPostDefinition(newBody);
            case 'PutDefinition': return WorkflowDefinitionApi.createPutDefinition(newBody);
            case 'ResponseHeaderDefinition': return WorkflowDefinitionApi.createResponseHeaderDefinition(newBody);
            case 'ResponseMessageDefinition': return WorkflowDefinitionApi.createResponseMessageDefinition(newBody);
            case 'RestBindingDefinition': return WorkflowDefinitionApi.createRestBindingDefinition(newBody);
            case 'RestConfigurationDefinition': return WorkflowDefinitionApi.createRestConfigurationDefinition(newBody);
            case 'RestDefinition': return WorkflowDefinitionApi.createRestDefinition(newBody);
            case 'RestPropertyDefinition': return WorkflowDefinitionApi.createRestPropertyDefinition(newBody);
            case 'RestSecuritiesDefinition': return WorkflowDefinitionApi.createRestSecuritiesDefinition(newBody);
            case 'RestsDefinition': return WorkflowDefinitionApi.createRestsDefinition(newBody);
            case 'SecurityDefinition': return WorkflowDefinitionApi.createSecurityDefinition(newBody);
            case 'LangChain4jCharacterTokenizerDefinition': return WorkflowDefinitionApi.createLangChain4jCharacterTokenizerDefinition(newBody);
            case 'LangChain4jLineTokenizerDefinition': return WorkflowDefinitionApi.createLangChain4jLineTokenizerDefinition(newBody);
            case 'LangChain4jParagraphTokenizerDefinition': return WorkflowDefinitionApi.createLangChain4jParagraphTokenizerDefinition(newBody);
            case 'LangChain4jSentenceTokenizerDefinition': return WorkflowDefinitionApi.createLangChain4jSentenceTokenizerDefinition(newBody);
            case 'LangChain4jTokenizerDefinition': return WorkflowDefinitionApi.createLangChain4jTokenizerDefinition(newBody);
            case 'LangChain4jWordTokenizerDefinition': return WorkflowDefinitionApi.createLangChain4jWordTokenizerDefinition(newBody);
            case 'CustomTransformerDefinition': return WorkflowDefinitionApi.createCustomTransformerDefinition(newBody);
            case 'DataFormatTransformerDefinition': return WorkflowDefinitionApi.createDataFormatTransformerDefinition(newBody);
            case 'EndpointTransformerDefinition': return WorkflowDefinitionApi.createEndpointTransformerDefinition(newBody);
            case 'LoadTransformerDefinition': return WorkflowDefinitionApi.createLoadTransformerDefinition(newBody);
            case 'TransformersDefinition': return WorkflowDefinitionApi.createTransformersDefinition(newBody);
            case 'CustomValidatorDefinition': return WorkflowDefinitionApi.createCustomValidatorDefinition(newBody);
            case 'EndpointValidatorDefinition': return WorkflowDefinitionApi.createEndpointValidatorDefinition(newBody);
            case 'PredicateValidatorDefinition': return WorkflowDefinitionApi.createPredicateValidatorDefinition(newBody);
            case 'ValidatorsDefinition': return WorkflowDefinitionApi.createValidatorsDefinition(newBody);
            default: return new WorkflowElement('');
        }
    }

    static createExpression = (name: string, body: any): WorkflowElement => {
        const newBody = WorkflowUtil.camelizeBody(name, body, false);
        delete newBody.expressionName;
        delete newBody.dslName;
        switch (name) {
            case 'ConstantExpression': return WorkflowDefinitionApi.createConstantExpression(newBody);
            case 'CSimpleExpression': return WorkflowDefinitionApi.createCSimpleExpression(newBody);
            case 'DatasonnetExpression': return WorkflowDefinitionApi.createDatasonnetExpression(newBody);
            case 'ExchangePropertyExpression': return WorkflowDefinitionApi.createExchangePropertyExpression(newBody);
            case 'GroovyExpression': return WorkflowDefinitionApi.createGroovyExpression(newBody);
            case 'HeaderExpression': return WorkflowDefinitionApi.createHeaderExpression(newBody);
            case 'Hl7TerserExpression': return WorkflowDefinitionApi.createHl7TerserExpression(newBody);
            case 'JavaExpression': return WorkflowDefinitionApi.createJavaExpression(newBody);
            case 'JqExpression': return WorkflowDefinitionApi.createJqExpression(newBody);
            case 'JavaScriptExpression': return WorkflowDefinitionApi.createJavaScriptExpression(newBody);
            case 'JsonPathExpression': return WorkflowDefinitionApi.createJsonPathExpression(newBody);
            case 'LanguageExpression': return WorkflowDefinitionApi.createLanguageExpression(newBody);
            case 'MethodCallExpression': return WorkflowDefinitionApi.createMethodCallExpression(newBody);
            case 'MvelExpression': return WorkflowDefinitionApi.createMvelExpression(newBody);
            case 'OgnlExpression': return WorkflowDefinitionApi.createOgnlExpression(newBody);
            case 'PythonExpression': return WorkflowDefinitionApi.createPythonExpression(newBody);
            case 'RefExpression': return WorkflowDefinitionApi.createRefExpression(newBody);
            case 'SimpleExpression': return WorkflowDefinitionApi.createSimpleExpression(newBody);
            case 'SpELExpression': return WorkflowDefinitionApi.createSpELExpression(newBody);
            case 'TokenizerExpression': return WorkflowDefinitionApi.createTokenizerExpression(newBody);
            case 'VariableExpression': return WorkflowDefinitionApi.createVariableExpression(newBody);
            case 'WasmExpression': return WorkflowDefinitionApi.createWasmExpression(newBody);
            case 'XPathExpression': return WorkflowDefinitionApi.createXPathExpression(newBody);
            case 'XQueryExpression': return WorkflowDefinitionApi.createXQueryExpression(newBody);
            case 'XMLTokenizerExpression': return WorkflowDefinitionApi.createXMLTokenizerExpression(newBody);
            default: return new GroovyExpression(newBody);
        }
    }

    static createDataFormat = (name: string, body: any): WorkflowElement => {
        const newBody = WorkflowUtil.camelizeBody(name, body, false);
        delete newBody.dataFormatName;
        delete newBody.dslName;
        switch (name) {
            case 'ASN1DataFormat': return WorkflowDefinitionApi.createASN1DataFormat(newBody);
            case 'AvroDataFormat': return WorkflowDefinitionApi.createAvroDataFormat(newBody);
            case 'BarcodeDataFormat': return WorkflowDefinitionApi.createBarcodeDataFormat(newBody);
            case 'Base64DataFormat': return WorkflowDefinitionApi.createBase64DataFormat(newBody);
            case 'BeanioDataFormat': return WorkflowDefinitionApi.createBeanioDataFormat(newBody);
            case 'BindyDataFormat': return WorkflowDefinitionApi.createBindyDataFormat(newBody);
            case 'CBORDataFormat': return WorkflowDefinitionApi.createCBORDataFormat(newBody);
            case 'CryptoDataFormat': return WorkflowDefinitionApi.createCryptoDataFormat(newBody);
            case 'CsvDataFormat': return WorkflowDefinitionApi.createCsvDataFormat(newBody);
            case 'CustomDataFormat': return WorkflowDefinitionApi.createCustomDataFormat(newBody);
            case 'FhirJsonDataFormat': return WorkflowDefinitionApi.createFhirJsonDataFormat(newBody);
            case 'FhirXmlDataFormat': return WorkflowDefinitionApi.createFhirXmlDataFormat(newBody);
            case 'FlatpackDataFormat': return WorkflowDefinitionApi.createFlatpackDataFormat(newBody);
            case 'FuryDataFormat': return WorkflowDefinitionApi.createFuryDataFormat(newBody);
            case 'GrokDataFormat': return WorkflowDefinitionApi.createGrokDataFormat(newBody);
            case 'GzipDeflaterDataFormat': return WorkflowDefinitionApi.createGzipDeflaterDataFormat(newBody);
            case 'HL7DataFormat': return WorkflowDefinitionApi.createHL7DataFormat(newBody);
            case 'IcalDataFormat': return WorkflowDefinitionApi.createIcalDataFormat(newBody);
            case 'JacksonXMLDataFormat': return WorkflowDefinitionApi.createJacksonXMLDataFormat(newBody);
            case 'JaxbDataFormat': return WorkflowDefinitionApi.createJaxbDataFormat(newBody);
            case 'JsonDataFormat': return WorkflowDefinitionApi.createJsonDataFormat(newBody);
            case 'JsonApiDataFormat': return WorkflowDefinitionApi.createJsonApiDataFormat(newBody);
            case 'LZFDataFormat': return WorkflowDefinitionApi.createLZFDataFormat(newBody);
            case 'MimeMultipartDataFormat': return WorkflowDefinitionApi.createMimeMultipartDataFormat(newBody);
            case 'ParquetAvroDataFormat': return WorkflowDefinitionApi.createParquetAvroDataFormat(newBody);
            case 'PGPDataFormat': return WorkflowDefinitionApi.createPGPDataFormat(newBody);
            case 'ProtobufDataFormat': return WorkflowDefinitionApi.createProtobufDataFormat(newBody);
            case 'RssDataFormat': return WorkflowDefinitionApi.createRssDataFormat(newBody);
            case 'SmooksDataFormat': return WorkflowDefinitionApi.createSmooksDataFormat(newBody);
            case 'SoapDataFormat': return WorkflowDefinitionApi.createSoapDataFormat(newBody);
            case 'SwiftMtDataFormat': return WorkflowDefinitionApi.createSwiftMtDataFormat(newBody);
            case 'SwiftMxDataFormat': return WorkflowDefinitionApi.createSwiftMxDataFormat(newBody);
            case 'SyslogDataFormat': return WorkflowDefinitionApi.createSyslogDataFormat(newBody);
            case 'TarFileDataFormat': return WorkflowDefinitionApi.createTarFileDataFormat(newBody);
            case 'ThriftDataFormat': return WorkflowDefinitionApi.createThriftDataFormat(newBody);
            case 'TidyMarkupDataFormat': return WorkflowDefinitionApi.createTidyMarkupDataFormat(newBody);
            case 'UniVocityCsvDataFormat': return WorkflowDefinitionApi.createUniVocityCsvDataFormat(newBody);
            case 'UniVocityFixedDataFormat': return WorkflowDefinitionApi.createUniVocityFixedDataFormat(newBody);
            case 'UniVocityTsvDataFormat': return WorkflowDefinitionApi.createUniVocityTsvDataFormat(newBody);
            case 'XMLSecurityDataFormat': return WorkflowDefinitionApi.createXMLSecurityDataFormat(newBody);
            case 'YAMLDataFormat': return WorkflowDefinitionApi.createYAMLDataFormat(newBody);
            case 'ZipDeflaterDataFormat': return WorkflowDefinitionApi.createZipDeflaterDataFormat(newBody);
            case 'ZipFileDataFormat': return WorkflowDefinitionApi.createZipFileDataFormat(newBody);
            default: return new JsonDataFormat(newBody);
        }
    }

    static createSteps = (elements: any[] | undefined): WorkflowElement[] => {
        const result: WorkflowElement[] = []
        if (elements !== undefined){
            elements.forEach(e => {
                result.push(WorkflowDefinitionApi.createStep(e.dslName, e));
            })
        }
        return result
    }
}