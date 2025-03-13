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
import {WorkflowUtil} from './workflow-util.ts';
import {WorkflowMetadataApi} from '../model/workflow-metadata';
import {ComponentApi} from './component-api';

export class WorkflowDefinitionYamlStep {

    static readProcessorDefinition = (element: any): ProcessorDefinition => {

        const def = element ? new ProcessorDefinition({...element}) : new ProcessorDefinition();
        if (element?.idempotentConsumer !== undefined) {
            if (Array.isArray(element.idempotentConsumer)) {
                def.idempotentConsumer = WorkflowDefinitionYamlStep.readIdempotentConsumerDefinition(element.idempotentConsumer[0]);
            } else {
                def.idempotentConsumer = WorkflowDefinitionYamlStep.readIdempotentConsumerDefinition(element.idempotentConsumer);
            }
        }
        if (element?.resumable !== undefined) {
            if (Array.isArray(element.resumable)) {
                def.resumable = WorkflowDefinitionYamlStep.readResumableDefinition(element.resumable[0]);
            } else {
                def.resumable = WorkflowDefinitionYamlStep.readResumableDefinition(element.resumable);
            }
        }
        if (element?.doTry !== undefined) {
            if (Array.isArray(element.doTry)) {
                def.doTry = WorkflowDefinitionYamlStep.readTryDefinition(element.doTry[0]);
            } else {
                def.doTry = WorkflowDefinitionYamlStep.readTryDefinition(element.doTry);
            }
        }
        if (element?.convertBodyTo !== undefined) {
            if (Array.isArray(element.convertBodyTo)) {
                def.convertBodyTo = WorkflowDefinitionYamlStep.readConvertBodyDefinition(element.convertBodyTo[0]);
            } else {
                def.convertBodyTo = WorkflowDefinitionYamlStep.readConvertBodyDefinition(element.convertBodyTo);
            }
        }
        if (element?.poll !== undefined) {
            if (Array.isArray(element.poll)) {
                def.poll = WorkflowDefinitionYamlStep.readPollDefinition(element.poll[0]);
            } else {
                def.poll = WorkflowDefinitionYamlStep.readPollDefinition(element.poll);
            }
        }
        if (element?.recipientList !== undefined) {
            if (Array.isArray(element.recipientList)) {
                def.recipientList = WorkflowDefinitionYamlStep.readRecipientListDefinition(element.recipientList[0]);
            } else {
                def.recipientList = WorkflowDefinitionYamlStep.readRecipientListDefinition(element.recipientList);
            }
        }
        if (element?.setHeader !== undefined) {
            if (Array.isArray(element.setHeader)) {
                def.setHeader = WorkflowDefinitionYamlStep.readSetHeaderDefinition(element.setHeader[0]);
            } else {
                def.setHeader = WorkflowDefinitionYamlStep.readSetHeaderDefinition(element.setHeader);
            }
        }
        if (element?.split !== undefined) {
            if (Array.isArray(element.split)) {
                def.split = WorkflowDefinitionYamlStep.readSplitDefinition(element.split[0]);
            } else {
                def.split = WorkflowDefinitionYamlStep.readSplitDefinition(element.split);
            }
        }
        if (element?.loop !== undefined) {
            if (Array.isArray(element.loop)) {
                def.loop = WorkflowDefinitionYamlStep.readLoopDefinition(element.loop[0]);
            } else {
                def.loop = WorkflowDefinitionYamlStep.readLoopDefinition(element.loop);
            }
        }
        if (element?.setExchangePattern !== undefined) {
            if (Array.isArray(element.setExchangePattern)) {
                def.setExchangePattern = WorkflowDefinitionYamlStep.readSetExchangePatternDefinition(element.setExchangePattern[0]);
            } else {
                def.setExchangePattern = WorkflowDefinitionYamlStep.readSetExchangePatternDefinition(element.setExchangePattern);
            }
        }
        if (element?.marshal !== undefined) {
            if (Array.isArray(element.marshal)) {
                def.marshal = WorkflowDefinitionYamlStep.readMarshalDefinition(element.marshal[0]);
            } else {
                def.marshal = WorkflowDefinitionYamlStep.readMarshalDefinition(element.marshal);
            }
        }
        if (element?.circuitBreaker !== undefined) {
            if (Array.isArray(element.circuitBreaker)) {
                def.circuitBreaker = WorkflowDefinitionYamlStep.readCircuitBreakerDefinition(element.circuitBreaker[0]);
            } else {
                def.circuitBreaker = WorkflowDefinitionYamlStep.readCircuitBreakerDefinition(element.circuitBreaker);
            }
        }
        if (element?.enrich !== undefined) {
            if (Array.isArray(element.enrich)) {
                def.enrich = WorkflowDefinitionYamlStep.readEnrichDefinition(element.enrich[0]);
            } else {
                def.enrich = WorkflowDefinitionYamlStep.readEnrichDefinition(element.enrich);
            }
        }
        if (element?.kamelet !== undefined) {
            if (Array.isArray(element.kamelet)) {
                def.kamelet = WorkflowDefinitionYamlStep.readKameletDefinition(element.kamelet[0]);
            } else {
                def.kamelet = WorkflowDefinitionYamlStep.readKameletDefinition(element.kamelet);
            }
        }
        if (element?.saga !== undefined) {
            if (Array.isArray(element.saga)) {
                def.saga = WorkflowDefinitionYamlStep.readSagaDefinition(element.saga[0]);
            } else {
                def.saga = WorkflowDefinitionYamlStep.readSagaDefinition(element.saga);
            }
        }
        if (element?.bean !== undefined) {
            if (Array.isArray(element.bean)) {
                def.bean = WorkflowDefinitionYamlStep.readBeanDefinition(element.bean[0]);
            } else {
                def.bean = WorkflowDefinitionYamlStep.readBeanDefinition(element.bean);
            }
        }
        if (element?.sort !== undefined) {
            if (Array.isArray(element.sort)) {
                def.sort = WorkflowDefinitionYamlStep.readSortDefinition(element.sort[0]);
            } else {
                def.sort = WorkflowDefinitionYamlStep.readSortDefinition(element.sort);
            }
        }
        if (element?.loadBalance !== undefined) {
            if (Array.isArray(element.loadBalance)) {
                def.loadBalance = WorkflowDefinitionYamlStep.readLoadBalanceDefinition(element.loadBalance[0]);
            } else {
                def.loadBalance = WorkflowDefinitionYamlStep.readLoadBalanceDefinition(element.loadBalance);
            }
        }
        if (element?.script !== undefined) {
            if (Array.isArray(element.script)) {
                def.script = WorkflowDefinitionYamlStep.readScriptDefinition(element.script[0]);
            } else {
                def.script = WorkflowDefinitionYamlStep.readScriptDefinition(element.script);
            }
        }
        if (element?.removeHeader !== undefined) {
            if (Array.isArray(element.removeHeader)) {
                def.removeHeader = WorkflowDefinitionYamlStep.readRemoveHeaderDefinition(element.removeHeader[0]);
            } else {
                def.removeHeader = WorkflowDefinitionYamlStep.readRemoveHeaderDefinition(element.removeHeader);
            }
        }
        if (element?.delay !== undefined) {
            if (Array.isArray(element.delay)) {
                def.delay = WorkflowDefinitionYamlStep.readDelayDefinition(element.delay[0]);
            } else {
                def.delay = WorkflowDefinitionYamlStep.readDelayDefinition(element.delay);
            }
        }
        if (element?.stop !== undefined) {
            if (Array.isArray(element.stop)) {
                def.stop = WorkflowDefinitionYamlStep.readStopDefinition(element.stop[0]);
            } else {
                def.stop = WorkflowDefinitionYamlStep.readStopDefinition(element.stop);
            }
        }
        if (element?.setProperty !== undefined) {
            if (Array.isArray(element.setProperty)) {
                def.setProperty = WorkflowDefinitionYamlStep.readSetPropertyDefinition(element.setProperty[0]);
            } else {
                def.setProperty = WorkflowDefinitionYamlStep.readSetPropertyDefinition(element.setProperty);
            }
        }
        if (element?.removeProperty !== undefined) {
            if (Array.isArray(element.removeProperty)) {
                def.removeProperty = WorkflowDefinitionYamlStep.readRemovePropertyDefinition(element.removeProperty[0]);
            } else {
                def.removeProperty = WorkflowDefinitionYamlStep.readRemovePropertyDefinition(element.removeProperty);
            }
        }
        if (element?.convertHeaderTo !== undefined) {
            if (Array.isArray(element.convertHeaderTo)) {
                def.convertHeaderTo = WorkflowDefinitionYamlStep.readConvertHeaderDefinition(element.convertHeaderTo[0]);
            } else {
                def.convertHeaderTo = WorkflowDefinitionYamlStep.readConvertHeaderDefinition(element.convertHeaderTo);
            }
        }
        if (element?.pausable !== undefined) {
            if (Array.isArray(element.pausable)) {
                def.pausable = WorkflowDefinitionYamlStep.readPausableDefinition(element.pausable[0]);
            } else {
                def.pausable = WorkflowDefinitionYamlStep.readPausableDefinition(element.pausable);
            }
        }
        if (element?.throttle !== undefined) {
            if (Array.isArray(element.throttle)) {
                def.throttle = WorkflowDefinitionYamlStep.readThrottleDefinition(element.throttle[0]);
            } else {
                def.throttle = WorkflowDefinitionYamlStep.readThrottleDefinition(element.throttle);
            }
        }
        if (element?.doFinally !== undefined) {
            if (Array.isArray(element.doFinally)) {
                def.doFinally = WorkflowDefinitionYamlStep.readFinallyDefinition(element.doFinally[0]);
            } else {
                def.doFinally = WorkflowDefinitionYamlStep.readFinallyDefinition(element.doFinally);
            }
        }
        if (element?.log !== undefined) {
            if (Array.isArray(element.log)) {
                def.log = WorkflowDefinitionYamlStep.readLogDefinition(element.log[0]);
            } else {
                def.log = WorkflowDefinitionYamlStep.readLogDefinition(element.log);
            }
        }
        if (element?.doCatch !== undefined) {
            if (Array.isArray(element.doCatch)) {
                def.doCatch = WorkflowDefinitionYamlStep.readCatchDefinition(element.doCatch[0]);
            } else {
                def.doCatch = WorkflowDefinitionYamlStep.readCatchDefinition(element.doCatch);
            }
        }
        if (element?.transacted !== undefined) {
            if (Array.isArray(element.transacted)) {
                def.transacted = WorkflowDefinitionYamlStep.readTransactedDefinition(element.transacted[0]);
            } else {
                def.transacted = WorkflowDefinitionYamlStep.readTransactedDefinition(element.transacted);
            }
        }
        if (element?.claimCheck !== undefined) {
            if (Array.isArray(element.claimCheck)) {
                def.claimCheck = WorkflowDefinitionYamlStep.readClaimCheckDefinition(element.claimCheck[0]);
            } else {
                def.claimCheck = WorkflowDefinitionYamlStep.readClaimCheckDefinition(element.claimCheck);
            }
        }
        if (element?.pollEnrich !== undefined) {
            if (Array.isArray(element.pollEnrich)) {
                def.pollEnrich = WorkflowDefinitionYamlStep.readPollEnrichDefinition(element.pollEnrich[0]);
            } else {
                def.pollEnrich = WorkflowDefinitionYamlStep.readPollEnrichDefinition(element.pollEnrich);
            }
        }
        if (element?.removeHeaders !== undefined) {
            if (Array.isArray(element.removeHeaders)) {
                def.removeHeaders = WorkflowDefinitionYamlStep.readRemoveHeadersDefinition(element.removeHeaders[0]);
            } else {
                def.removeHeaders = WorkflowDefinitionYamlStep.readRemoveHeadersDefinition(element.removeHeaders);
            }
        }
        if (element?.aggregate !== undefined) {
            if (Array.isArray(element.aggregate)) {
                def.aggregate = WorkflowDefinitionYamlStep.readAggregateDefinition(element.aggregate[0]);
            } else {
                def.aggregate = WorkflowDefinitionYamlStep.readAggregateDefinition(element.aggregate);
            }
        }
        if (element?.resequence !== undefined) {
            if (Array.isArray(element.resequence)) {
                def.resequence = WorkflowDefinitionYamlStep.readResequenceDefinition(element.resequence[0]);
            } else {
                def.resequence = WorkflowDefinitionYamlStep.readResequenceDefinition(element.resequence);
            }
        }
        if (element?.routingSlip !== undefined) {
            if (Array.isArray(element.routingSlip)) {
                def.routingSlip = WorkflowDefinitionYamlStep.readRoutingSlipDefinition(element.routingSlip[0]);
            } else {
                def.routingSlip = WorkflowDefinitionYamlStep.readRoutingSlipDefinition(element.routingSlip);
            }
        }
        if (element?.transform !== undefined) {
            if (Array.isArray(element.transform)) {
                def.transform = WorkflowDefinitionYamlStep.readTransformDefinition(element.transform[0]);
            } else {
                def.transform = WorkflowDefinitionYamlStep.readTransformDefinition(element.transform);
            }
        }
        if (element?.removeProperties !== undefined) {
            if (Array.isArray(element.removeProperties)) {
                def.removeProperties = WorkflowDefinitionYamlStep.readRemovePropertiesDefinition(element.removeProperties[0]);
            } else {
                def.removeProperties = WorkflowDefinitionYamlStep.readRemovePropertiesDefinition(element.removeProperties);
            }
        }
        if (element?.removeVariable !== undefined) {
            if (Array.isArray(element.removeVariable)) {
                def.removeVariable = WorkflowDefinitionYamlStep.readRemoveVariableDefinition(element.removeVariable[0]);
            } else {
                def.removeVariable = WorkflowDefinitionYamlStep.readRemoveVariableDefinition(element.removeVariable);
            }
        }
        if (element?.policy !== undefined) {
            if (Array.isArray(element.policy)) {
                def.policy = WorkflowDefinitionYamlStep.readPolicyDefinition(element.policy[0]);
            } else {
                def.policy = WorkflowDefinitionYamlStep.readPolicyDefinition(element.policy);
            }
        }
        if (element?.validate !== undefined) {
            if (Array.isArray(element.validate)) {
                def.validate = WorkflowDefinitionYamlStep.readValidateDefinition(element.validate[0]);
            } else {
                def.validate = WorkflowDefinitionYamlStep.readValidateDefinition(element.validate);
            }
        }
        if (element?.rollback !== undefined) {
            if (Array.isArray(element.rollback)) {
                def.rollback = WorkflowDefinitionYamlStep.readRollbackDefinition(element.rollback[0]);
            } else {
                def.rollback = WorkflowDefinitionYamlStep.readRollbackDefinition(element.rollback);
            }
        }
        if (element?.setHeaders !== undefined) {
            if (Array.isArray(element.setHeaders)) {
                def.setHeaders = WorkflowDefinitionYamlStep.readSetHeadersDefinition(element.setHeaders[0]);
            } else {
                def.setHeaders = WorkflowDefinitionYamlStep.readSetHeadersDefinition(element.setHeaders);
            }
        }
        if (element?.process !== undefined) {
            if (Array.isArray(element.process)) {
                def.process = WorkflowDefinitionYamlStep.readProcessDefinition(element.process[0]);
            } else {
                def.process = WorkflowDefinitionYamlStep.readProcessDefinition(element.process);
            }
        }
        if (element?.setVariables !== undefined) {
            if (Array.isArray(element.setVariables)) {
                def.setVariables = WorkflowDefinitionYamlStep.readSetVariablesDefinition(element.setVariables[0]);
            } else {
                def.setVariables = WorkflowDefinitionYamlStep.readSetVariablesDefinition(element.setVariables);
            }
        }
        if (element?.convertVariableTo !== undefined) {
            if (Array.isArray(element.convertVariableTo)) {
                def.convertVariableTo = WorkflowDefinitionYamlStep.readConvertVariableDefinition(element.convertVariableTo[0]);
            } else {
                def.convertVariableTo = WorkflowDefinitionYamlStep.readConvertVariableDefinition(element.convertVariableTo);
            }
        }
        if (element?.setVariable !== undefined) {
            if (Array.isArray(element.setVariable)) {
                def.setVariable = WorkflowDefinitionYamlStep.readSetVariableDefinition(element.setVariable[0]);
            } else {
                def.setVariable = WorkflowDefinitionYamlStep.readSetVariableDefinition(element.setVariable);
            }
        }
        if (element?.threads !== undefined) {
            if (Array.isArray(element.threads)) {
                def.threads = WorkflowDefinitionYamlStep.readThreadsDefinition(element.threads[0]);
            } else {
                def.threads = WorkflowDefinitionYamlStep.readThreadsDefinition(element.threads);
            }
        }
        if (element?.setBody !== undefined) {
            if (Array.isArray(element.setBody)) {
                def.setBody = WorkflowDefinitionYamlStep.readSetBodyDefinition(element.setBody[0]);
            } else {
                def.setBody = WorkflowDefinitionYamlStep.readSetBodyDefinition(element.setBody);
            }
        }
        if (element?.sample !== undefined) {
            if (Array.isArray(element.sample)) {
                def.sample = WorkflowDefinitionYamlStep.readSamplingDefinition(element.sample[0]);
            } else {
                def.sample = WorkflowDefinitionYamlStep.readSamplingDefinition(element.sample);
            }
        }
        if (element?.throwException !== undefined) {
            if (Array.isArray(element.throwException)) {
                def.throwException = WorkflowDefinitionYamlStep.readThrowExceptionDefinition(element.throwException[0]);
            } else {
                def.throwException = WorkflowDefinitionYamlStep.readThrowExceptionDefinition(element.throwException);
            }
        }
        if (element?.dynamicRouter !== undefined) {
            if (Array.isArray(element.dynamicRouter)) {
                def.dynamicRouter = WorkflowDefinitionYamlStep.readDynamicRouterDefinition(element.dynamicRouter[0]);
            } else {
                def.dynamicRouter = WorkflowDefinitionYamlStep.readDynamicRouterDefinition(element.dynamicRouter);
            }
        }
        if (element?.multicast !== undefined) {
            if (Array.isArray(element.multicast)) {
                def.multicast = WorkflowDefinitionYamlStep.readMulticastDefinition(element.multicast[0]);
            } else {
                def.multicast = WorkflowDefinitionYamlStep.readMulticastDefinition(element.multicast);
            }
        }
        if (element?.tokenizer !== undefined) {
            if (Array.isArray(element.tokenizer)) {
                def.tokenizer = WorkflowDefinitionYamlStep.readTokenizerDefinition(element.tokenizer[0]);
            } else {
                def.tokenizer = WorkflowDefinitionYamlStep.readTokenizerDefinition(element.tokenizer);
            }
        }
        if (element?.filter !== undefined) {
            if (Array.isArray(element.filter)) {
                def.filter = WorkflowDefinitionYamlStep.readFilterDefinition(element.filter[0]);
            } else {
                def.filter = WorkflowDefinitionYamlStep.readFilterDefinition(element.filter);
            }
        }
        if (element?.pipeline !== undefined) {
            if (Array.isArray(element.pipeline)) {
                def.pipeline = WorkflowDefinitionYamlStep.readPipelineDefinition(element.pipeline[0]);
            } else {
                def.pipeline = WorkflowDefinitionYamlStep.readPipelineDefinition(element.pipeline);
            }
        }
        if (element?.unmarshal !== undefined) {
            if (Array.isArray(element.unmarshal)) {
                def.unmarshal = WorkflowDefinitionYamlStep.readUnmarshalDefinition(element.unmarshal[0]);
            } else {
                def.unmarshal = WorkflowDefinitionYamlStep.readUnmarshalDefinition(element.unmarshal);
            }
        }
        if (element?.wireTap !== undefined) {
            if (Array.isArray(element.wireTap)) {
                def.wireTap = WorkflowDefinitionYamlStep.readWireTapDefinition(element.wireTap[0]);
            } else {
                def.wireTap = WorkflowDefinitionYamlStep.readWireTapDefinition(element.wireTap);
            }
        }
        if (element?.step !== undefined) {
            if (Array.isArray(element.step)) {
                def.step = WorkflowDefinitionYamlStep.readStepDefinition(element.step[0]);
            } else {
                def.step = WorkflowDefinitionYamlStep.readStepDefinition(element.step);
            }
        }
        if (element?.choice !== undefined) {
            if (Array.isArray(element.choice)) {
                def.choice = WorkflowDefinitionYamlStep.readChoiceDefinition(element.choice[0]);
            } else {
                def.choice = WorkflowDefinitionYamlStep.readChoiceDefinition(element.choice);
            }
        }

        return def;
    }

    static readBeansDeserializer = (element: any): BeansDeserializer => {

        const def = element ? new BeansDeserializer({...element}) : new BeansDeserializer();

        return def;
    }

    static readDataFormatsDefinitionDeserializer = (element: any): DataFormatsDefinitionDeserializer => {

        const def = element ? new DataFormatsDefinitionDeserializer({...element}) : new DataFormatsDefinitionDeserializer();

        return def;
    }

    static readErrorHandlerDeserializer = (element: any): ErrorHandlerDeserializer => {

        const def = element ? new ErrorHandlerDeserializer({...element}) : new ErrorHandlerDeserializer();
        if (element?.deadLetterChannel !== undefined) {
            if (Array.isArray(element.deadLetterChannel)) {
                def.deadLetterChannel = WorkflowDefinitionYamlStep.readDeadLetterChannelDefinition(element.deadLetterChannel[0]);
            } else {
                def.deadLetterChannel = WorkflowDefinitionYamlStep.readDeadLetterChannelDefinition(element.deadLetterChannel);
            }
        }
        if (element?.noErrorHandler !== undefined) {
            if (Array.isArray(element.noErrorHandler)) {
                def.noErrorHandler = WorkflowDefinitionYamlStep.readNoErrorHandlerDefinition(element.noErrorHandler[0]);
            } else {
                def.noErrorHandler = WorkflowDefinitionYamlStep.readNoErrorHandlerDefinition(element.noErrorHandler);
            }
        }
        if (element?.jtaTransactionErrorHandler !== undefined) {
            if (Array.isArray(element.jtaTransactionErrorHandler)) {
                def.jtaTransactionErrorHandler = WorkflowDefinitionYamlStep.readJtaTransactionErrorHandlerDefinition(element.jtaTransactionErrorHandler[0]);
            } else {
                def.jtaTransactionErrorHandler = WorkflowDefinitionYamlStep.readJtaTransactionErrorHandlerDefinition(element.jtaTransactionErrorHandler);
            }
        }
        if (element?.defaultErrorHandler !== undefined) {
            if (Array.isArray(element.defaultErrorHandler)) {
                def.defaultErrorHandler = WorkflowDefinitionYamlStep.readDefaultErrorHandlerDefinition(element.defaultErrorHandler[0]);
            } else {
                def.defaultErrorHandler = WorkflowDefinitionYamlStep.readDefaultErrorHandlerDefinition(element.defaultErrorHandler);
            }
        }
        if (element?.springTransactionErrorHandler !== undefined) {
            if (Array.isArray(element.springTransactionErrorHandler)) {
                def.springTransactionErrorHandler = WorkflowDefinitionYamlStep.readSpringTransactionErrorHandlerDefinition(element.springTransactionErrorHandler[0]);
            } else {
                def.springTransactionErrorHandler = WorkflowDefinitionYamlStep.readSpringTransactionErrorHandlerDefinition(element.springTransactionErrorHandler);
            }
        }
        if (element?.refErrorHandler !== undefined) {
            if (Array.isArray(element.refErrorHandler)) {
                def.refErrorHandler = WorkflowDefinitionYamlStep.readRefErrorHandlerDefinition(element.refErrorHandler[0]);
            } else {
                def.refErrorHandler = WorkflowDefinitionYamlStep.readRefErrorHandlerDefinition(element.refErrorHandler);
            }
        }

        return def;
    }

    static readOutputAwareFromDefinition = (element: any): OutputAwareFromDefinition => {

        let def = element ? new OutputAwareFromDefinition({...element}) : new OutputAwareFromDefinition();
        def = ComponentApi.parseElementUri(def);
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readAggregateDefinition = (element: any): AggregateDefinition => {

        const def = element ? new AggregateDefinition({...element}) : new AggregateDefinition();
        if (element?.completionTimeoutExpression !== undefined) {
            if (Array.isArray(element.completionTimeoutExpression)) {
                def.completionTimeoutExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.completionTimeoutExpression[0]);
            } else {
                def.completionTimeoutExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.completionTimeoutExpression);
            }
        }
        if (element?.correlationExpression !== undefined) {
            if (Array.isArray(element.correlationExpression)) {
                def.correlationExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.correlationExpression[0]);
            } else {
                def.correlationExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.correlationExpression);
            }
        }
        if (element?.completionPredicate !== undefined) {
            if (Array.isArray(element.completionPredicate)) {
                def.completionPredicate = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.completionPredicate[0]);
            } else {
                def.completionPredicate = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.completionPredicate);
            }
        }
        if (element?.optimisticLockRetryPolicy !== undefined) {
            if (Array.isArray(element.optimisticLockRetryPolicy)) {
                def.optimisticLockRetryPolicy = WorkflowDefinitionYamlStep.readOptimisticLockRetryPolicyDefinition(element.optimisticLockRetryPolicy[0]);
            } else {
                def.optimisticLockRetryPolicy = WorkflowDefinitionYamlStep.readOptimisticLockRetryPolicyDefinition(element.optimisticLockRetryPolicy);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);
        if (element?.completionSizeExpression !== undefined) {
            if (Array.isArray(element.completionSizeExpression)) {
                def.completionSizeExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.completionSizeExpression[0]);
            } else {
                def.completionSizeExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.completionSizeExpression);
            }
        }

        return def;
    }

    static readBeanDefinition = (element: any): BeanDefinition => {
        return element ? new BeanDefinition({...element}) : new BeanDefinition();
    }

    static readBeanFactoryDefinition = (element: any): BeanFactoryDefinition => {
        return element ? new BeanFactoryDefinition({...element}) : new BeanFactoryDefinition();
    }

    static readCatchDefinition = (element: any): CatchDefinition => {

        const def = element ? new CatchDefinition({...element}) : new CatchDefinition();
        if (element?.onWhen !== undefined) {
            if (Array.isArray(element.onWhen)) {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen[0]);
            } else {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readChoiceDefinition = (element: any): ChoiceDefinition => {

        const def = element ? new ChoiceDefinition({...element}) : new ChoiceDefinition();
        if (element?.otherwise !== undefined) {
            if (Array.isArray(element.otherwise)) {
                def.otherwise = WorkflowDefinitionYamlStep.readOtherwiseDefinition(element.otherwise[0]);
            } else {
                def.otherwise = WorkflowDefinitionYamlStep.readOtherwiseDefinition(element.otherwise);
            }
        }
        def.when = element && element?.when ? element?.when.map((x:any) => WorkflowDefinitionYamlStep.readWhenDefinition(x)) :[];

        return def;
    }

    static readCircuitBreakerDefinition = (element: any): CircuitBreakerDefinition => {

        const def = element ? new CircuitBreakerDefinition({...element}) : new CircuitBreakerDefinition();
        if (element?.faultToleranceConfiguration !== undefined) {
            if (Array.isArray(element.faultToleranceConfiguration)) {
                def.faultToleranceConfiguration = WorkflowDefinitionYamlStep.readFaultToleranceConfigurationDefinition(element.faultToleranceConfiguration[0]);
            } else {
                def.faultToleranceConfiguration = WorkflowDefinitionYamlStep.readFaultToleranceConfigurationDefinition(element.faultToleranceConfiguration);
            }
        }
        if (element?.resilience4jConfiguration !== undefined) {
            if (Array.isArray(element.resilience4jConfiguration)) {
                def.resilience4jConfiguration = WorkflowDefinitionYamlStep.readResilience4jConfigurationDefinition(element.resilience4jConfiguration[0]);
            } else {
                def.resilience4jConfiguration = WorkflowDefinitionYamlStep.readResilience4jConfigurationDefinition(element.resilience4jConfiguration);
            }
        }
        if (element?.onFallback !== undefined) {
            if (Array.isArray(element.onFallback)) {
                def.onFallback = WorkflowDefinitionYamlStep.readOnFallbackDefinition(element.onFallback[0]);
            } else {
                def.onFallback = WorkflowDefinitionYamlStep.readOnFallbackDefinition(element.onFallback);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readClaimCheckDefinition = (element: any): ClaimCheckDefinition => {

        return element ? new ClaimCheckDefinition({...element}) : new ClaimCheckDefinition();

    }

    static readContextScanDefinition = (element: any): ContextScanDefinition => {
        return element ? new ContextScanDefinition({...element}) : new ContextScanDefinition();
    }

    static readConvertBodyDefinition = (element: any): ConvertBodyDefinition => {
        if (element && typeof element === 'string') element = {type: element};
        return element ? new ConvertBodyDefinition({...element}) : new ConvertBodyDefinition();
    }

    static readConvertHeaderDefinition = (element: any): ConvertHeaderDefinition => {
        return element ? new ConvertHeaderDefinition({...element}) : new ConvertHeaderDefinition();
    }

    static readConvertVariableDefinition = (element: any): ConvertVariableDefinition => {

        return element ? new ConvertVariableDefinition({...element}) : new ConvertVariableDefinition();
    }

    static readDataFormatDefinition = (element: any): DataFormatDefinition => {
        return element ? new DataFormatDefinition({...element}) : new DataFormatDefinition();
    }

    static readDelayDefinition = (element: any): DelayDefinition => {

        const  def = element ? new DelayDefinition({...element}) : new DelayDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readDynamicRouterDefinition = (element: any): DynamicRouterDefinition => {

        const def = element ? new DynamicRouterDefinition({...element}) : new DynamicRouterDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readEnrichDefinition = (element: any): EnrichDefinition => {

        const def = element ? new EnrichDefinition({...element}) : new EnrichDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readErrorHandlerDefinition = (element: any): ErrorHandlerDefinition => {

        const def = element ? new ErrorHandlerDefinition({...element}) : new ErrorHandlerDefinition();
        if (element?.deadLetterChannel !== undefined) {
            if (Array.isArray(element.deadLetterChannel)) {
                def.deadLetterChannel = WorkflowDefinitionYamlStep.readDeadLetterChannelDefinition(element.deadLetterChannel[0]);
            } else {
                def.deadLetterChannel = WorkflowDefinitionYamlStep.readDeadLetterChannelDefinition(element.deadLetterChannel);
            }
        }
        if (element?.noErrorHandler !== undefined) {
            if (Array.isArray(element.noErrorHandler)) {
                def.noErrorHandler = WorkflowDefinitionYamlStep.readNoErrorHandlerDefinition(element.noErrorHandler[0]);
            } else {
                def.noErrorHandler = WorkflowDefinitionYamlStep.readNoErrorHandlerDefinition(element.noErrorHandler);
            }
        }
        if (element?.jtaTransactionErrorHandler !== undefined) {
            if (Array.isArray(element.jtaTransactionErrorHandler)) {
                def.jtaTransactionErrorHandler = WorkflowDefinitionYamlStep.readJtaTransactionErrorHandlerDefinition(element.jtaTransactionErrorHandler[0]);
            } else {
                def.jtaTransactionErrorHandler = WorkflowDefinitionYamlStep.readJtaTransactionErrorHandlerDefinition(element.jtaTransactionErrorHandler);
            }
        }
        if (element?.defaultErrorHandler !== undefined) {
            if (Array.isArray(element.defaultErrorHandler)) {
                def.defaultErrorHandler = WorkflowDefinitionYamlStep.readDefaultErrorHandlerDefinition(element.defaultErrorHandler[0]);
            } else {
                def.defaultErrorHandler = WorkflowDefinitionYamlStep.readDefaultErrorHandlerDefinition(element.defaultErrorHandler);
            }
        }
        if (element?.springTransactionErrorHandler !== undefined) {
            if (Array.isArray(element.springTransactionErrorHandler)) {
                def.springTransactionErrorHandler = WorkflowDefinitionYamlStep.readSpringTransactionErrorHandlerDefinition(element.springTransactionErrorHandler[0]);
            } else {
                def.springTransactionErrorHandler = WorkflowDefinitionYamlStep.readSpringTransactionErrorHandlerDefinition(element.springTransactionErrorHandler);
            }
        }
        if (element?.refErrorHandler !== undefined) {
            if (Array.isArray(element.refErrorHandler)) {
                def.refErrorHandler = WorkflowDefinitionYamlStep.readRefErrorHandlerDefinition(element.refErrorHandler[0]);
            } else {
                def.refErrorHandler = WorkflowDefinitionYamlStep.readRefErrorHandlerDefinition(element.refErrorHandler);
            }
        }

        return def;
    }

    static readExpressionSubElementDefinition = (element: any): ExpressionSubElementDefinition => {

        const def = element ? new ExpressionSubElementDefinition({...element}) : new ExpressionSubElementDefinition();
        if (element?.constant !== undefined) {
            if (Array.isArray(element.constant)) {
                def.constant = WorkflowDefinitionYamlStep.readConstantExpression(element.constant[0]);
            } else {
                def.constant = WorkflowDefinitionYamlStep.readConstantExpression(element.constant);
            }
        }
        if (element?.datasonnet !== undefined) {
            if (Array.isArray(element.datasonnet)) {
                def.datasonnet = WorkflowDefinitionYamlStep.readDatasonnetExpression(element.datasonnet[0]);
            } else {
                def.datasonnet = WorkflowDefinitionYamlStep.readDatasonnetExpression(element.datasonnet);
            }
        }
        if (element?.jq !== undefined) {
            if (Array.isArray(element.jq)) {
                def.jq = WorkflowDefinitionYamlStep.readJqExpression(element.jq[0]);
            } else {
                def.jq = WorkflowDefinitionYamlStep.readJqExpression(element.jq);
            }
        }
        if (element?.js !== undefined) {
            if (Array.isArray(element.js)) {
                def.js = WorkflowDefinitionYamlStep.readJavaScriptExpression(element.js[0]);
            } else {
                def.js = WorkflowDefinitionYamlStep.readJavaScriptExpression(element.js);
            }
        }
        if (element?.language !== undefined) {
            if (Array.isArray(element.language)) {
                def.language = WorkflowDefinitionYamlStep.readLanguageExpression(element.language[0]);
            } else {
                def.language = WorkflowDefinitionYamlStep.readLanguageExpression(element.language);
            }
        }
        if (element?.simple !== undefined) {
            if (Array.isArray(element.simple)) {
                def.simple = WorkflowDefinitionYamlStep.readSimpleExpression(element.simple[0]);
            } else {
                def.simple = WorkflowDefinitionYamlStep.readSimpleExpression(element.simple);
            }
        }
        if (element?.tokenize !== undefined) {
            if (Array.isArray(element.tokenize)) {
                def.tokenize = WorkflowDefinitionYamlStep.readTokenizerExpression(element.tokenize[0]);
            } else {
                def.tokenize = WorkflowDefinitionYamlStep.readTokenizerExpression(element.tokenize);
            }
        }
        if (element?.ref !== undefined) {
            if (Array.isArray(element.ref)) {
                def.ref = WorkflowDefinitionYamlStep.readRefExpression(element.ref[0]);
            } else {
                def.ref = WorkflowDefinitionYamlStep.readRefExpression(element.ref);
            }
        }
        if (element?.xpath !== undefined) {
            if (Array.isArray(element.xpath)) {
                def.xpath = WorkflowDefinitionYamlStep.readXPathExpression(element.xpath[0]);
            } else {
                def.xpath = WorkflowDefinitionYamlStep.readXPathExpression(element.xpath);
            }
        }
        if (element?.java !== undefined) {
            if (Array.isArray(element.java)) {
                def.java = WorkflowDefinitionYamlStep.readJavaExpression(element.java[0]);
            } else {
                def.java = WorkflowDefinitionYamlStep.readJavaExpression(element.java);
            }
        }
        if (element?.wasm !== undefined) {
            if (Array.isArray(element.wasm)) {
                def.wasm = WorkflowDefinitionYamlStep.readWasmExpression(element.wasm[0]);
            } else {
                def.wasm = WorkflowDefinitionYamlStep.readWasmExpression(element.wasm);
            }
        }
        if (element?.csimple !== undefined) {
            if (Array.isArray(element.csimple)) {
                def.csimple = WorkflowDefinitionYamlStep.readCSimpleExpression(element.csimple[0]);
            } else {
                def.csimple = WorkflowDefinitionYamlStep.readCSimpleExpression(element.csimple);
            }
        }
        if (element?.jsonpath !== undefined) {
            if (Array.isArray(element.jsonpath)) {
                def.jsonpath = WorkflowDefinitionYamlStep.readJsonPathExpression(element.jsonpath[0]);
            } else {
                def.jsonpath = WorkflowDefinitionYamlStep.readJsonPathExpression(element.jsonpath);
            }
        }
        if (element?.ognl !== undefined) {
            if (Array.isArray(element.ognl)) {
                def.ognl = WorkflowDefinitionYamlStep.readOgnlExpression(element.ognl[0]);
            } else {
                def.ognl = WorkflowDefinitionYamlStep.readOgnlExpression(element.ognl);
            }
        }
        if (element?.python !== undefined) {
            if (Array.isArray(element.python)) {
                def.python = WorkflowDefinitionYamlStep.readPythonExpression(element.python[0]);
            } else {
                def.python = WorkflowDefinitionYamlStep.readPythonExpression(element.python);
            }
        }
        if (element?.mvel !== undefined) {
            if (Array.isArray(element.mvel)) {
                def.mvel = WorkflowDefinitionYamlStep.readMvelExpression(element.mvel[0]);
            } else {
                def.mvel = WorkflowDefinitionYamlStep.readMvelExpression(element.mvel);
            }
        }
        if (element?.method !== undefined) {
            if (Array.isArray(element.method)) {
                def.method = WorkflowDefinitionYamlStep.readMethodCallExpression(element.method[0]);
            } else {
                def.method = WorkflowDefinitionYamlStep.readMethodCallExpression(element.method);
            }
        }
        if (element?.xquery !== undefined) {
            if (Array.isArray(element.xquery)) {
                def.xquery = WorkflowDefinitionYamlStep.readXQueryExpression(element.xquery[0]);
            } else {
                def.xquery = WorkflowDefinitionYamlStep.readXQueryExpression(element.xquery);
            }
        }
        if (element?.hl7terser !== undefined) {
            if (Array.isArray(element.hl7terser)) {
                def.hl7terser = WorkflowDefinitionYamlStep.readHl7TerserExpression(element.hl7terser[0]);
            } else {
                def.hl7terser = WorkflowDefinitionYamlStep.readHl7TerserExpression(element.hl7terser);
            }
        }
        if (element?.spel !== undefined) {
            if (Array.isArray(element.spel)) {
                def.spel = WorkflowDefinitionYamlStep.readSpELExpression(element.spel[0]);
            } else {
                def.spel = WorkflowDefinitionYamlStep.readSpELExpression(element.spel);
            }
        }
        if (element?.groovy !== undefined) {
            if (Array.isArray(element.groovy)) {
                def.groovy = WorkflowDefinitionYamlStep.readGroovyExpression(element.groovy[0]);
            } else {
                def.groovy = WorkflowDefinitionYamlStep.readGroovyExpression(element.groovy);
            }
        }
        if (element?.exchangeProperty !== undefined) {
            if (Array.isArray(element.exchangeProperty)) {
                def.exchangeProperty = WorkflowDefinitionYamlStep.readExchangePropertyExpression(element.exchangeProperty[0]);
            } else {
                def.exchangeProperty = WorkflowDefinitionYamlStep.readExchangePropertyExpression(element.exchangeProperty);
            }
        }
        if (element?.variable !== undefined) {
            if (Array.isArray(element.variable)) {
                def.variable = WorkflowDefinitionYamlStep.readVariableExpression(element.variable[0]);
            } else {
                def.variable = WorkflowDefinitionYamlStep.readVariableExpression(element.variable);
            }
        }
        if (element?.header !== undefined) {
            if (Array.isArray(element.header)) {
                def.header = WorkflowDefinitionYamlStep.readHeaderExpression(element.header[0]);
            } else {
                def.header = WorkflowDefinitionYamlStep.readHeaderExpression(element.header);
            }
        }
        if (element?.xtokenize !== undefined) {
            if (Array.isArray(element.xtokenize)) {
                def.xtokenize = WorkflowDefinitionYamlStep.readXMLTokenizerExpression(element.xtokenize[0]);
            } else {
                def.xtokenize = WorkflowDefinitionYamlStep.readXMLTokenizerExpression(element.xtokenize);
            }
        }

        return def;
    }

    static readFaultToleranceConfigurationDefinition = (element: any): FaultToleranceConfigurationDefinition => {
        return element ? new FaultToleranceConfigurationDefinition({...element}) : new FaultToleranceConfigurationDefinition();
    }

    static readFilterDefinition = (element: any): FilterDefinition => {

        const def = element ? new FilterDefinition({...element}) : new FilterDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readFinallyDefinition = (element: any): FinallyDefinition => {

        const def = element ? new FinallyDefinition({...element}) : new FinallyDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readFromDefinition = (element: any): FromDefinition => {
        if (element && typeof element === 'string') element = { uri: element};
        let def = element ? new FromDefinition({...element}) : new FromDefinition();
        def = ComponentApi.parseElementUri(def);
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readGlobalOptionDefinition = (element: any): GlobalOptionDefinition => {

        return element ? new GlobalOptionDefinition({...element}) : new GlobalOptionDefinition();
    }

    static readGlobalOptionsDefinition = (element: any): GlobalOptionsDefinition => {

        const def = element ? new GlobalOptionsDefinition({...element}) : new GlobalOptionsDefinition();
        def.globalOption = element && element?.globalOption ? element?.globalOption.map((x:any) => WorkflowDefinitionYamlStep.readGlobalOptionDefinition(x)) :[];

        return def;
    }

    static readIdempotentConsumerDefinition = (element: any): IdempotentConsumerDefinition => {

        const def = element ? new IdempotentConsumerDefinition({...element}) : new IdempotentConsumerDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readInputTypeDefinition = (element: any): InputTypeDefinition => {
        return element ? new InputTypeDefinition({...element}) : new InputTypeDefinition();
    }

    static readInterceptDefinition = (element: any): InterceptDefinition => {

        const def = element ? new InterceptDefinition({...element}) : new InterceptDefinition();
        if (element?.onWhen !== undefined) {
            if (Array.isArray(element.onWhen)) {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen[0]);
            } else {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readInterceptFromDefinition = (element: any): InterceptFromDefinition => {

        let def = element ? new InterceptFromDefinition({...element}) : new InterceptFromDefinition();
        def = ComponentApi.parseElementUri(def);
        if (element?.onWhen !== undefined) {
            if (Array.isArray(element.onWhen)) {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen[0]);
            } else {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readInterceptSendToEndpointDefinition = (element: any): InterceptSendToEndpointDefinition => {
        if (element && typeof element === 'string') element = {uri: element};
        let def = element ? new InterceptSendToEndpointDefinition({...element}) : new InterceptSendToEndpointDefinition();
        def = ComponentApi.parseElementUri(def);
        if (element?.onWhen !== undefined) {
            if (Array.isArray(element.onWhen)) {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen[0]);
            } else {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readKameletDefinition = (element: any): KameletDefinition => {
        if (element && typeof element === 'string') element = {name: element};
        return element ? new KameletDefinition({...element}) : new KameletDefinition();
    }

    static readLoadBalanceDefinition = (element: any): LoadBalanceDefinition => {

        const def = element ? new LoadBalanceDefinition({...element}) : new LoadBalanceDefinition();
        if (element?.roundRobinLoadBalancer !== undefined) {
            if (Array.isArray(element.roundRobinLoadBalancer)) {
                def.roundRobinLoadBalancer = WorkflowDefinitionYamlStep.readRoundRobinLoadBalancerDefinition(element.roundRobinLoadBalancer[0]);
            } else {
                def.roundRobinLoadBalancer = WorkflowDefinitionYamlStep.readRoundRobinLoadBalancerDefinition(element.roundRobinLoadBalancer);
            }
        }
        if (element?.stickyLoadBalancer !== undefined) {
            if (Array.isArray(element.stickyLoadBalancer)) {
                def.stickyLoadBalancer = WorkflowDefinitionYamlStep.readStickyLoadBalancerDefinition(element.stickyLoadBalancer[0]);
            } else {
                def.stickyLoadBalancer = WorkflowDefinitionYamlStep.readStickyLoadBalancerDefinition(element.stickyLoadBalancer);
            }
        }
        if (element?.customLoadBalancer !== undefined) {
            if (Array.isArray(element.customLoadBalancer)) {
                def.customLoadBalancer = WorkflowDefinitionYamlStep.readCustomLoadBalancerDefinition(element.customLoadBalancer[0]);
            } else {
                def.customLoadBalancer = WorkflowDefinitionYamlStep.readCustomLoadBalancerDefinition(element.customLoadBalancer);
            }
        }
        if (element?.topicLoadBalancer !== undefined) {
            if (Array.isArray(element.topicLoadBalancer)) {
                def.topicLoadBalancer = WorkflowDefinitionYamlStep.readTopicLoadBalancerDefinition(element.topicLoadBalancer[0]);
            } else {
                def.topicLoadBalancer = WorkflowDefinitionYamlStep.readTopicLoadBalancerDefinition(element.topicLoadBalancer);
            }
        }
        if (element?.failoverLoadBalancer !== undefined) {
            if (Array.isArray(element.failoverLoadBalancer)) {
                def.failoverLoadBalancer = WorkflowDefinitionYamlStep.readFailoverLoadBalancerDefinition(element.failoverLoadBalancer[0]);
            } else {
                def.failoverLoadBalancer = WorkflowDefinitionYamlStep.readFailoverLoadBalancerDefinition(element.failoverLoadBalancer);
            }
        }
        if (element?.weightedLoadBalancer !== undefined) {
            if (Array.isArray(element.weightedLoadBalancer)) {
                def.weightedLoadBalancer = WorkflowDefinitionYamlStep.readWeightedLoadBalancerDefinition(element.weightedLoadBalancer[0]);
            } else {
                def.weightedLoadBalancer = WorkflowDefinitionYamlStep.readWeightedLoadBalancerDefinition(element.weightedLoadBalancer);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);
        if (element?.randomLoadBalancer !== undefined) {
            if (Array.isArray(element.randomLoadBalancer)) {
                def.randomLoadBalancer = WorkflowDefinitionYamlStep.readRandomLoadBalancerDefinition(element.randomLoadBalancer[0]);
            } else {
                def.randomLoadBalancer = WorkflowDefinitionYamlStep.readRandomLoadBalancerDefinition(element.randomLoadBalancer);
            }
        }

        return def;
    }

    static readLogDefinition = (element: any): LogDefinition => {
        if (element && typeof element === 'string') element = {message: element};
        return element ? new LogDefinition({...element}) : new LogDefinition();
    }

    static readLoopDefinition = (element: any): LoopDefinition => {

        const def = element ? new LoopDefinition({...element}) : new LoopDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readMarshalDefinition = (element: any): MarshalDefinition => {

        const def = element ? new MarshalDefinition({...element}) : new MarshalDefinition();
        if (element?.univocityCsv !== undefined) {
            if (Array.isArray(element.univocityCsv)) {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv[0]);
            } else {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv);
            }
        }
        if (element?.protobuf !== undefined) {
            if (Array.isArray(element.protobuf)) {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf[0]);
            } else {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf);
            }
        }
        if (element?.tarFile !== undefined) {
            if (Array.isArray(element.tarFile)) {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile[0]);
            } else {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile);
            }
        }
        if (element?.tidyMarkup !== undefined) {
            if (Array.isArray(element.tidyMarkup)) {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup[0]);
            } else {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup);
            }
        }
        if (element?.csv !== undefined) {
            if (Array.isArray(element.csv)) {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv[0]);
            } else {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv);
            }
        }
        if (element?.base64 !== undefined) {
            if (Array.isArray(element.base64)) {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64[0]);
            } else {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64);
            }
        }
        if (element?.zipDeflater !== undefined) {
            if (Array.isArray(element.zipDeflater)) {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater[0]);
            } else {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater);
            }
        }
        if (element?.bindy !== undefined) {
            if (Array.isArray(element.bindy)) {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy[0]);
            } else {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy);
            }
        }
        if (element?.syslog !== undefined) {
            if (Array.isArray(element.syslog)) {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog[0]);
            } else {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog);
            }
        }
        if (element?.zipFile !== undefined) {
            if (Array.isArray(element.zipFile)) {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile[0]);
            } else {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile);
            }
        }
        if (element?.jaxb !== undefined) {
            if (Array.isArray(element.jaxb)) {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb[0]);
            } else {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb);
            }
        }
        if (element?.parquetAvro !== undefined) {
            if (Array.isArray(element.parquetAvro)) {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro[0]);
            } else {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro);
            }
        }
        if (element?.rss !== undefined) {
            if (Array.isArray(element.rss)) {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss[0]);
            } else {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss);
            }
        }
        if (element?.smooks !== undefined) {
            if (Array.isArray(element.smooks)) {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks[0]);
            } else {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks);
            }
        }
        if (element?.mimeMultipart !== undefined) {
            if (Array.isArray(element.mimeMultipart)) {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart[0]);
            } else {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart);
            }
        }
        if (element?.asn1 !== undefined) {
            if (Array.isArray(element.asn1)) {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1[0]);
            } else {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1);
            }
        }
        if (element?.pgp !== undefined) {
            if (Array.isArray(element.pgp)) {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp[0]);
            } else {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp);
            }
        }
        if (element?.thrift !== undefined) {
            if (Array.isArray(element.thrift)) {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift[0]);
            } else {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift);
            }
        }
        if (element?.json !== undefined) {
            if (Array.isArray(element.json)) {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json[0]);
            } else {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json);
            }
        }
        if (element?.lzf !== undefined) {
            if (Array.isArray(element.lzf)) {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf[0]);
            } else {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf);
            }
        }
        if (element?.fhirXml !== undefined) {
            if (Array.isArray(element.fhirXml)) {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml[0]);
            } else {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml);
            }
        }
        if (element?.barcode !== undefined) {
            if (Array.isArray(element.barcode)) {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode[0]);
            } else {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode);
            }
        }
        if (element?.avro !== undefined) {
            if (Array.isArray(element.avro)) {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro[0]);
            } else {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro);
            }
        }
        if (element?.yaml !== undefined) {
            if (Array.isArray(element.yaml)) {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml[0]);
            } else {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml);
            }
        }
        if (element?.beanio !== undefined) {
            if (Array.isArray(element.beanio)) {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio[0]);
            } else {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio);
            }
        }
        if (element?.fhirJson !== undefined) {
            if (Array.isArray(element.fhirJson)) {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson[0]);
            } else {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson);
            }
        }
        if (element?.fury !== undefined) {
            if (Array.isArray(element.fury)) {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury[0]);
            } else {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury);
            }
        }
        if (element?.custom !== undefined) {
            if (Array.isArray(element.custom)) {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom[0]);
            } else {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom);
            }
        }
        if (element?.flatpack !== undefined) {
            if (Array.isArray(element.flatpack)) {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack[0]);
            } else {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack);
            }
        }
        if (element?.swiftMx !== undefined) {
            if (Array.isArray(element.swiftMx)) {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx[0]);
            } else {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx);
            }
        }
        if (element?.cbor !== undefined) {
            if (Array.isArray(element.cbor)) {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor[0]);
            } else {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor);
            }
        }
        if (element?.crypto !== undefined) {
            if (Array.isArray(element.crypto)) {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto[0]);
            } else {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto);
            }
        }
        if (element?.swiftMt !== undefined) {
            if (Array.isArray(element.swiftMt)) {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt[0]);
            } else {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt);
            }
        }
        if (element?.univocityTsv !== undefined) {
            if (Array.isArray(element.univocityTsv)) {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv[0]);
            } else {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv);
            }
        }
        if (element?.hl7 !== undefined) {
            if (Array.isArray(element.hl7)) {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7[0]);
            } else {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7);
            }
        }
        if (element?.jsonApi !== undefined) {
            if (Array.isArray(element.jsonApi)) {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi[0]);
            } else {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi);
            }
        }
        if (element?.xmlSecurity !== undefined) {
            if (Array.isArray(element.xmlSecurity)) {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity[0]);
            } else {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity);
            }
        }
        if (element?.ical !== undefined) {
            if (Array.isArray(element.ical)) {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical[0]);
            } else {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical);
            }
        }
        if (element?.univocityFixed !== undefined) {
            if (Array.isArray(element.univocityFixed)) {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed[0]);
            } else {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed);
            }
        }
        if (element?.jacksonXml !== undefined) {
            if (Array.isArray(element.jacksonXml)) {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml[0]);
            } else {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml);
            }
        }
        if (element?.grok !== undefined) {
            if (Array.isArray(element.grok)) {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok[0]);
            } else {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok);
            }
        }
        if (element?.gzipDeflater !== undefined) {
            if (Array.isArray(element.gzipDeflater)) {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater[0]);
            } else {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater);
            }
        }
        if (element?.soap !== undefined) {
            if (Array.isArray(element.soap)) {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap[0]);
            } else {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap);
            }
        }

        return def;
    }

    static readMulticastDefinition = (element: any): MulticastDefinition => {
        const def = element ? new MulticastDefinition({...element}) : new MulticastDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);
        return def;
    }

    static readOnCompletionDefinition = (element: any): OnCompletionDefinition => {
        const def = element ? new OnCompletionDefinition({...element}) : new OnCompletionDefinition();
        if (element?.onWhen !== undefined) {
            if (Array.isArray(element.onWhen)) {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen[0]);
            } else {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readOnExceptionDefinition = (element: any): OnExceptionDefinition => {
        const def = element ? new OnExceptionDefinition({...element}) : new OnExceptionDefinition();
        if (element?.retryWhile !== undefined) {
            if (Array.isArray(element.retryWhile)) {
                def.retryWhile = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.retryWhile[0]);
            } else {
                def.retryWhile = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.retryWhile);
            }
        }
        if (element?.redeliveryPolicy !== undefined) {
            if (Array.isArray(element.redeliveryPolicy)) {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy[0]);
            } else {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy);
            }
        }
        if (element?.handled !== undefined) {
            if (Array.isArray(element.handled)) {
                def.handled = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.handled[0]);
            } else {
                def.handled = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.handled);
            }
        }
        if (element?.onWhen !== undefined) {
            if (Array.isArray(element.onWhen)) {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen[0]);
            } else {
                def.onWhen = WorkflowDefinitionYamlStep.readOnWhenDefinition(element.onWhen);
            }
        }
        if (element?.continued !== undefined) {
            if (Array.isArray(element.continued)) {
                def.continued = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.continued[0]);
            } else {
                def.continued = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.continued);
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readOnFallbackDefinition = (element: any): OnFallbackDefinition => {

        const def = element ? new OnFallbackDefinition({...element}) : new OnFallbackDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readOnWhenDefinition = (element: any): OnWhenDefinition => {

        const def = element ? new OnWhenDefinition({...element}) : new OnWhenDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readOptimisticLockRetryPolicyDefinition = (element: any): OptimisticLockRetryPolicyDefinition => {
        return element ? new OptimisticLockRetryPolicyDefinition({...element}) : new OptimisticLockRetryPolicyDefinition();
    }

    static readOtherwiseDefinition = (element: any): OtherwiseDefinition => {

        const def = element ? new OtherwiseDefinition({...element}) : new OtherwiseDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readOutputDefinition = (element: any): OutputDefinition => {

        const def = element ? new OutputDefinition({...element}) : new OutputDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readOutputTypeDefinition = (element: any): OutputTypeDefinition => {
        return element ? new OutputTypeDefinition({...element}) : new OutputTypeDefinition();
    }

    static readPackageScanDefinition = (element: any): PackageScanDefinition => {
        return element ? new PackageScanDefinition({...element}) : new PackageScanDefinition();
    }

    static readPausableDefinition = (element: any): PausableDefinition => {
        return element ? new PausableDefinition({...element}) : new PausableDefinition();
    }

    static readPipelineDefinition = (element: any): PipelineDefinition => {

        const def = element ? new PipelineDefinition({...element}) : new PipelineDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readPolicyDefinition = (element: any): PolicyDefinition => {
        const def = element ? new PolicyDefinition({...element}) : new PolicyDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);
        return def;
    }

    static readPollDefinition = (element: any): PollDefinition => {
        if (element && typeof element === 'string') element = {uri: element};
        let def = element ? new PollDefinition({...element}) : new PollDefinition();
        def = ComponentApi.parseElementUri(def);

        return def;
    }

    static readPollEnrichDefinition = (element: any): PollEnrichDefinition => {
        const def = element ? new PollEnrichDefinition({...element}) : new PollEnrichDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readProcessDefinition = (element: any): ProcessDefinition => {
        return element ? new ProcessDefinition({...element}) : new ProcessDefinition();
    }

    static readPropertyDefinition = (element: any): PropertyDefinition => {
        return element ? new PropertyDefinition({...element}) : new PropertyDefinition();
    }

    static readPropertyExpressionDefinition = (element: any): PropertyExpressionDefinition => {

        const def = element ? new PropertyExpressionDefinition({...element}) : new PropertyExpressionDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readRecipientListDefinition = (element: any): RecipientListDefinition => {
        const def = element ? new RecipientListDefinition({...element}) : new RecipientListDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readRedeliveryPolicyDefinition = (element: any): RedeliveryPolicyDefinition => {
        return element ? new RedeliveryPolicyDefinition({...element}) : new RedeliveryPolicyDefinition();
    }

    static readRemoveHeaderDefinition = (element: any): RemoveHeaderDefinition => {
        if (element && typeof element === 'string') element = {name: element};
        return element ? new RemoveHeaderDefinition({...element}) : new RemoveHeaderDefinition();
    }

    static readRemoveHeadersDefinition = (element: any): RemoveHeadersDefinition => {
        if (element && typeof element === 'string') element = {pattern: element};
        return element ? new RemoveHeadersDefinition({...element}) : new RemoveHeadersDefinition();
    }

    static readRemovePropertiesDefinition = (element: any): RemovePropertiesDefinition => {
        if (element && typeof element === 'string') element = {pattern: element};
        return element ? new RemovePropertiesDefinition({...element}) : new RemovePropertiesDefinition();
    }

    static readRemovePropertyDefinition = (element: any): RemovePropertyDefinition => {
        if (element && typeof element === 'string') element = {name: element};
        return element ? new RemovePropertyDefinition({...element}) : new RemovePropertyDefinition();
    }

    static readRemoveVariableDefinition = (element: any): RemoveVariableDefinition => {
        if (element && typeof element === 'string') element = {name: element};
        return element ? new RemoveVariableDefinition({...element}) : new RemoveVariableDefinition();
    }

    static readResequenceDefinition = (element: any): ResequenceDefinition => {

        const def = element ? new ResequenceDefinition({...element}) : new ResequenceDefinition();
        if (element?.streamConfig !== undefined) {
            if (Array.isArray(element.streamConfig)) {
                def.streamConfig = WorkflowDefinitionYamlStep.readStreamResequencerConfig(element.streamConfig[0]);
            } else {
                def.streamConfig = WorkflowDefinitionYamlStep.readStreamResequencerConfig(element.streamConfig);
            }
        }
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);
        if (element?.batchConfig !== undefined) {
            if (Array.isArray(element.batchConfig)) {
                def.batchConfig = WorkflowDefinitionYamlStep.readBatchResequencerConfig(element.batchConfig[0]);
            } else {
                def.batchConfig = WorkflowDefinitionYamlStep.readBatchResequencerConfig(element.batchConfig);
            }
        }

        return def;
    }

    static readResilience4jConfigurationDefinition = (element: any): Resilience4jConfigurationDefinition => {
        return element ? new Resilience4jConfigurationDefinition({...element}) : new Resilience4jConfigurationDefinition();
    }

    static readRestContextRefDefinition = (element: any): RestContextRefDefinition => {
        if (element && typeof element === 'string') element = {ref: element};
        return element ? new RestContextRefDefinition({...element}) : new RestContextRefDefinition();
    }

    static readResumableDefinition = (element: any): ResumableDefinition => {
        return element ? new ResumableDefinition({...element}) : new ResumableDefinition();
    }

    static readRollbackDefinition = (element: any): RollbackDefinition => {
        return element ? new RollbackDefinition({...element}) : new RollbackDefinition();
    }

    static readRouteBuilderDefinition = (element: any): RouteBuilderDefinition => {
        if (element && typeof element === 'string') element = {ref: element};
        return element ? new RouteBuilderDefinition({...element}) : new RouteBuilderDefinition();
    }

    static readRouteConfigurationContextRefDefinition = (element: any): RouteConfigurationContextRefDefinition => {
        if (element && typeof element === 'string') element = {ref: element};
        return element ? new RouteConfigurationContextRefDefinition({...element}) : new RouteConfigurationContextRefDefinition();
    }

    static readRouteConfigurationDefinition = (element: any): RouteConfigurationDefinition => {

        const def = element ? new RouteConfigurationDefinition({...element}) : new RouteConfigurationDefinition();
        def.onCompletion = element && element?.onCompletion ? element?.onCompletion.map((x:any) => WorkflowDefinitionYamlStep.readOnCompletionDefinition(x.onCompletion)) :[];
        def.interceptSendToEndpoint = element && element?.interceptSendToEndpoint ? element?.interceptSendToEndpoint.map((x:any) => WorkflowDefinitionYamlStep.readInterceptSendToEndpointDefinition(x.interceptSendToEndpoint)) :[];
        def.intercept = element && element?.intercept ? element?.intercept.map((x:any) => WorkflowDefinitionYamlStep.readInterceptDefinition(x.intercept)) :[];
        if (element?.errorHandler !== undefined) {
            if (Array.isArray(element.errorHandler)) {
                def.errorHandler = WorkflowDefinitionYamlStep.readErrorHandlerDefinition(element.errorHandler[0]);
            } else {
                def.errorHandler = WorkflowDefinitionYamlStep.readErrorHandlerDefinition(element.errorHandler);
            }
        }
        def.onException = element && element?.onException ? element?.onException.map((x:any) => WorkflowDefinitionYamlStep.readOnExceptionDefinition(x.onException)) :[];
        def.interceptFrom = element && element?.interceptFrom ? element?.interceptFrom.map((x:any) => WorkflowDefinitionYamlStep.readInterceptFromDefinition(x.interceptFrom)) :[];

        return def;
    }

    static readRouteContextRefDefinition = (element: any): RouteContextRefDefinition => {
        if (element && typeof element === 'string') element = {ref: element};
        return element ? new RouteContextRefDefinition({...element}) : new RouteContextRefDefinition();
    }

    static readRouteDefinition = (element: any): RouteDefinition => {

        const def = element ? new RouteDefinition({...element}) : new RouteDefinition();
        if (element?.outputType !== undefined) {
            if (Array.isArray(element.outputType)) {
                def.outputType = WorkflowDefinitionYamlStep.readOutputTypeDefinition(element.outputType[0]);
            } else {
                def.outputType = WorkflowDefinitionYamlStep.readOutputTypeDefinition(element.outputType);
            }
        }
        if (element?.errorHandler !== undefined) {
            if (Array.isArray(element.errorHandler)) {
                def.errorHandler = WorkflowDefinitionYamlStep.readErrorHandlerDefinition(element.errorHandler[0]);
            } else {
                def.errorHandler = WorkflowDefinitionYamlStep.readErrorHandlerDefinition(element.errorHandler);
            }
        }
        if (element?.inputType !== undefined) {
            if (Array.isArray(element.inputType)) {
                def.inputType = WorkflowDefinitionYamlStep.readInputTypeDefinition(element.inputType[0]);
            } else {
                def.inputType = WorkflowDefinitionYamlStep.readInputTypeDefinition(element.inputType);
            }
        }
        if (element?.from !== undefined) {
            if (Array.isArray(element.from)) {
                def.from = WorkflowDefinitionYamlStep.readFromDefinition(element.from[0]);
            } else {
                def.from = WorkflowDefinitionYamlStep.readFromDefinition(element.from);
            }
        }

        return def;
    }

    static readRouteTemplateDefinition = (element: any): RouteTemplateDefinition => {

        const def = element ? new RouteTemplateDefinition({...element}) : new RouteTemplateDefinition();
        if (element?.route !== undefined) {
            if (Array.isArray(element.route)) {
                def.route = WorkflowDefinitionYamlStep.readRouteDefinition(element.route[0]);
            } else {
                def.route = WorkflowDefinitionYamlStep.readRouteDefinition(element.route);
            }
        }
        def.beans = element && element?.beans ? element?.beans.map((x:any) => WorkflowDefinitionYamlStep.readBeanFactoryDefinition(x)) :[];
        if (element?.from !== undefined) {
            if (Array.isArray(element.from)) {
                def.from = WorkflowDefinitionYamlStep.readFromDefinition(element.from[0]);
            } else {
                def.from = WorkflowDefinitionYamlStep.readFromDefinition(element.from);
            }
        }
        def.parameters = element && element?.parameters ? element?.parameters.map((x:any) => WorkflowDefinitionYamlStep.readRouteTemplateParameterDefinition(x)) :[];

        return def;
    }

    static readRouteTemplateParameterDefinition = (element: any): RouteTemplateParameterDefinition => {
        return element ? new RouteTemplateParameterDefinition({...element}) : new RouteTemplateParameterDefinition();
    }

    static readRoutingSlipDefinition = (element: any): RoutingSlipDefinition => {

        const def = element ? new RoutingSlipDefinition({...element}) : new RoutingSlipDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSagaActionUriDefinition = (element: any): SagaActionUriDefinition => {
        if (element && typeof element === 'string') element = {uri: element};
        let def = element ? new SagaActionUriDefinition({...element}) : new SagaActionUriDefinition();
        def = ComponentApi.parseElementUri(def);

        return def;
    }

    static readSagaDefinition = (element: any): SagaDefinition => {

        const def = element ? new SagaDefinition({...element}) : new SagaDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);
        def.option = element && element?.option ? element?.option.map((x:any) => WorkflowDefinitionYamlStep.readPropertyExpressionDefinition(x)) :[];

        return def;
    }

    static readSamplingDefinition = (element: any): SamplingDefinition => {
        return element ? new SamplingDefinition({...element}) : new SamplingDefinition();
    }

    static readScriptDefinition = (element: any): ScriptDefinition => {

        const def = element ? new ScriptDefinition({...element}) : new ScriptDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSetBodyDefinition = (element: any): SetBodyDefinition => {

        const def = element ? new SetBodyDefinition({...element}) : new SetBodyDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSetExchangePatternDefinition = (element: any): SetExchangePatternDefinition => {
        return element ? new SetExchangePatternDefinition({...element}) : new SetExchangePatternDefinition();
    }

    static readSetHeaderDefinition = (element: any): SetHeaderDefinition => {

        const def = element ? new SetHeaderDefinition({...element}) : new SetHeaderDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSetHeadersDefinition = (element: any): SetHeadersDefinition => {

        const def = element ? new SetHeadersDefinition({...element}) : new SetHeadersDefinition();
        def.headers = element && element?.headers ? element?.headers.map((x:any) => WorkflowDefinitionYamlStep.readSetHeaderDefinition(x)) :[];

        return def;
    }

    static readSetPropertyDefinition = (element: any): SetPropertyDefinition => {

        const def = element ? new SetPropertyDefinition({...element}) : new SetPropertyDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSetVariableDefinition = (element: any): SetVariableDefinition => {

        const def = element ? new SetVariableDefinition({...element}) : new SetVariableDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSetVariablesDefinition = (element: any): SetVariablesDefinition => {

        const def = element ? new SetVariablesDefinition({...element}) : new SetVariablesDefinition();
        def.variables = element && element?.variables ? element?.variables.map((x:any) => WorkflowDefinitionYamlStep.readSetVariableDefinition(x)) :[];

        return def;
    }

    static readSortDefinition = (element: any): SortDefinition => {

        const def = element ? new SortDefinition({...element}) : new SortDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readSplitDefinition = (element: any): SplitDefinition => {

        const def = element ? new SplitDefinition({...element}) : new SplitDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readStepDefinition = (element: any): StepDefinition => {

        const def = element ? new StepDefinition({...element}) : new StepDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readStopDefinition = (element: any): StopDefinition => {
        return element ? new StopDefinition({...element}) : new StopDefinition();
    }

    static readTemplatedRouteDefinition = (element: any): TemplatedRouteDefinition => {
        const def = element ? new TemplatedRouteDefinition({...element}) : new TemplatedRouteDefinition();
        def.beans = element && element?.beans ? element?.beans.map((x:any) => WorkflowDefinitionYamlStep.readBeanFactoryDefinition(x)) :[];
        def.parameters = element && element?.parameters ? element?.parameters.map((x:any) => WorkflowDefinitionYamlStep.readTemplatedRouteParameterDefinition(x)) :[];
        return def;
    }

    static readTemplatedRouteParameterDefinition = (element: any): TemplatedRouteParameterDefinition => {
        const def = element ? new TemplatedRouteParameterDefinition({...element}) : new TemplatedRouteParameterDefinition();
        return def;
    }

    static readThreadPoolProfileDefinition = (element: any): ThreadPoolProfileDefinition => {
        return element ? new ThreadPoolProfileDefinition({...element}) : new ThreadPoolProfileDefinition();
    }

    static readThreadsDefinition = (element: any): ThreadsDefinition => {
        return element ? new ThreadsDefinition({...element}) : new ThreadsDefinition();
    }

    static readThrottleDefinition = (element: any): ThrottleDefinition => {

        const def = element ? new ThrottleDefinition({...element}) : new ThrottleDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        if (element?.correlationExpression !== undefined) {
            if (Array.isArray(element.correlationExpression)) {
                def.correlationExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.correlationExpression[0]);
            } else {
                def.correlationExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.correlationExpression);
            }
        }

        return def;
    }

    static readThrowExceptionDefinition = (element: any): ThrowExceptionDefinition => {
        return element ? new ThrowExceptionDefinition({...element}) : new ThrowExceptionDefinition();
    }

    static readToDefinition = (element: any): ToDefinition => {
        if (element && typeof element === 'string') element = {uri: element};
        let def = element ? new ToDefinition({...element}) : new ToDefinition();
        def = ComponentApi.parseElementUri(def);

        return def;
    }

    static readToDynamicDefinition = (element: any): ToDynamicDefinition => {
        if (element && typeof element === 'string') element = {uri: element};
        let def = element ? new ToDynamicDefinition({...element}) : new ToDynamicDefinition();
        def = ComponentApi.parseElementUri(def);

        return def;
    }

    static readTokenizerDefinition = (element: any): TokenizerDefinition => {

        const def = element ? new TokenizerDefinition({...element}) : new TokenizerDefinition();
        if (element?.langChain4jLineTokenizer !== undefined) {
            if (Array.isArray(element.langChain4jLineTokenizer)) {
                def.langChain4jLineTokenizer = WorkflowDefinitionYamlStep.readLangChain4jTokenizerDefinition(element.langChain4jLineTokenizer[0]);
            } else {
                def.langChain4jLineTokenizer = WorkflowDefinitionYamlStep.readLangChain4jTokenizerDefinition(element.langChain4jLineTokenizer);
            }
        }
        if (element?.langChain4jCharacterTokenizer !== undefined) {
            if (Array.isArray(element.langChain4jCharacterTokenizer)) {
                def.langChain4jCharacterTokenizer = WorkflowDefinitionYamlStep.readLangChain4jCharacterTokenizerDefinition(element.langChain4jCharacterTokenizer[0]);
            } else {
                def.langChain4jCharacterTokenizer = WorkflowDefinitionYamlStep.readLangChain4jCharacterTokenizerDefinition(element.langChain4jCharacterTokenizer);
            }
        }
        if (element?.langChain4jSentenceTokenizer !== undefined) {
            if (Array.isArray(element.langChain4jSentenceTokenizer)) {
                def.langChain4jSentenceTokenizer = WorkflowDefinitionYamlStep.readLangChain4jSentenceTokenizerDefinition(element.langChain4jSentenceTokenizer[0]);
            } else {
                def.langChain4jSentenceTokenizer = WorkflowDefinitionYamlStep.readLangChain4jSentenceTokenizerDefinition(element.langChain4jSentenceTokenizer);
            }
        }
        if (element?.langChain4jWordTokenizer !== undefined) {
            if (Array.isArray(element.langChain4jWordTokenizer)) {
                def.langChain4jWordTokenizer = WorkflowDefinitionYamlStep.readLangChain4jWordTokenizerDefinition(element.langChain4jWordTokenizer[0]);
            } else {
                def.langChain4jWordTokenizer = WorkflowDefinitionYamlStep.readLangChain4jWordTokenizerDefinition(element.langChain4jWordTokenizer);
            }
        }
        if (element?.langChain4jParagraphTokenizer !== undefined) {
            if (Array.isArray(element.langChain4jParagraphTokenizer)) {
                def.langChain4jParagraphTokenizer = WorkflowDefinitionYamlStep.readLangChain4jParagraphTokenizerDefinition(element.langChain4jParagraphTokenizer[0]);
            } else {
                def.langChain4jParagraphTokenizer = WorkflowDefinitionYamlStep.readLangChain4jParagraphTokenizerDefinition(element.langChain4jParagraphTokenizer);
            }
        }

        return def;
    }

    static readTokenizerImplementationDefinition = (element: any): TokenizerImplementationDefinition => {
        return element ? new TokenizerImplementationDefinition({...element}) : new TokenizerImplementationDefinition();
    }

    static readTransactedDefinition = (element: any): TransactedDefinition => {

        const def = element ? new TransactedDefinition({...element}) : new TransactedDefinition();
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readTransformDefinition = (element: any): TransformDefinition => {

        const def = element ? new TransformDefinition({...element}) : new TransformDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readTryDefinition = (element: any): TryDefinition => {

        const def = element ? new TryDefinition({...element}) : new TryDefinition();
        if (element?.doFinally !== undefined) {
            if (Array.isArray(element.doFinally)) {
                def.doFinally = WorkflowDefinitionYamlStep.readFinallyDefinition(element.doFinally[0]);
            } else {
                def.doFinally = WorkflowDefinitionYamlStep.readFinallyDefinition(element.doFinally);
            }
        }
        def.doCatch = element && element?.doCatch ? element?.doCatch.map((x:any) => WorkflowDefinitionYamlStep.readCatchDefinition(x)) :[];
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readUnmarshalDefinition = (element: any): UnmarshalDefinition => {

        const def = element ? new UnmarshalDefinition({...element}) : new UnmarshalDefinition();
        if (element?.univocityCsv !== undefined) {
            if (Array.isArray(element.univocityCsv)) {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv[0]);
            } else {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv);
            }
        }
        if (element?.protobuf !== undefined) {
            if (Array.isArray(element.protobuf)) {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf[0]);
            } else {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf);
            }
        }
        if (element?.tarFile !== undefined) {
            if (Array.isArray(element.tarFile)) {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile[0]);
            } else {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile);
            }
        }
        if (element?.tidyMarkup !== undefined) {
            if (Array.isArray(element.tidyMarkup)) {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup[0]);
            } else {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup);
            }
        }
        if (element?.csv !== undefined) {
            if (Array.isArray(element.csv)) {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv[0]);
            } else {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv);
            }
        }
        if (element?.base64 !== undefined) {
            if (Array.isArray(element.base64)) {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64[0]);
            } else {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64);
            }
        }
        if (element?.zipDeflater !== undefined) {
            if (Array.isArray(element.zipDeflater)) {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater[0]);
            } else {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater);
            }
        }
        if (element?.bindy !== undefined) {
            if (Array.isArray(element.bindy)) {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy[0]);
            } else {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy);
            }
        }
        if (element?.syslog !== undefined) {
            if (Array.isArray(element.syslog)) {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog[0]);
            } else {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog);
            }
        }
        if (element?.zipFile !== undefined) {
            if (Array.isArray(element.zipFile)) {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile[0]);
            } else {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile);
            }
        }
        if (element?.jaxb !== undefined) {
            if (Array.isArray(element.jaxb)) {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb[0]);
            } else {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb);
            }
        }
        if (element?.parquetAvro !== undefined) {
            if (Array.isArray(element.parquetAvro)) {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro[0]);
            } else {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro);
            }
        }
        if (element?.rss !== undefined) {
            if (Array.isArray(element.rss)) {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss[0]);
            } else {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss);
            }
        }
        if (element?.smooks !== undefined) {
            if (Array.isArray(element.smooks)) {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks[0]);
            } else {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks);
            }
        }
        if (element?.mimeMultipart !== undefined) {
            if (Array.isArray(element.mimeMultipart)) {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart[0]);
            } else {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart);
            }
        }
        if (element?.asn1 !== undefined) {
            if (Array.isArray(element.asn1)) {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1[0]);
            } else {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1);
            }
        }
        if (element?.pgp !== undefined) {
            if (Array.isArray(element.pgp)) {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp[0]);
            } else {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp);
            }
        }
        if (element?.thrift !== undefined) {
            if (Array.isArray(element.thrift)) {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift[0]);
            } else {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift);
            }
        }
        if (element?.json !== undefined) {
            if (Array.isArray(element.json)) {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json[0]);
            } else {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json);
            }
        }
        if (element?.lzf !== undefined) {
            if (Array.isArray(element.lzf)) {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf[0]);
            } else {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf);
            }
        }
        if (element?.fhirXml !== undefined) {
            if (Array.isArray(element.fhirXml)) {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml[0]);
            } else {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml);
            }
        }
        if (element?.barcode !== undefined) {
            if (Array.isArray(element.barcode)) {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode[0]);
            } else {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode);
            }
        }
        if (element?.avro !== undefined) {
            if (Array.isArray(element.avro)) {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro[0]);
            } else {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro);
            }
        }
        if (element?.yaml !== undefined) {
            if (Array.isArray(element.yaml)) {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml[0]);
            } else {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml);
            }
        }
        if (element?.beanio !== undefined) {
            if (Array.isArray(element.beanio)) {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio[0]);
            } else {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio);
            }
        }
        if (element?.fhirJson !== undefined) {
            if (Array.isArray(element.fhirJson)) {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson[0]);
            } else {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson);
            }
        }
        if (element?.fury !== undefined) {
            if (Array.isArray(element.fury)) {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury[0]);
            } else {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury);
            }
        }
        if (element?.custom !== undefined) {
            if (Array.isArray(element.custom)) {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom[0]);
            } else {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom);
            }
        }
        if (element?.flatpack !== undefined) {
            if (Array.isArray(element.flatpack)) {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack[0]);
            } else {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack);
            }
        }
        if (element?.swiftMx !== undefined) {
            if (Array.isArray(element.swiftMx)) {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx[0]);
            } else {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx);
            }
        }
        if (element?.cbor !== undefined) {
            if (Array.isArray(element.cbor)) {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor[0]);
            } else {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor);
            }
        }
        if (element?.crypto !== undefined) {
            if (Array.isArray(element.crypto)) {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto[0]);
            } else {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto);
            }
        }
        if (element?.swiftMt !== undefined) {
            if (Array.isArray(element.swiftMt)) {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt[0]);
            } else {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt);
            }
        }
        if (element?.univocityTsv !== undefined) {
            if (Array.isArray(element.univocityTsv)) {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv[0]);
            } else {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv);
            }
        }
        if (element?.hl7 !== undefined) {
            if (Array.isArray(element.hl7)) {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7[0]);
            } else {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7);
            }
        }
        if (element?.jsonApi !== undefined) {
            if (Array.isArray(element.jsonApi)) {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi[0]);
            } else {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi);
            }
        }
        if (element?.xmlSecurity !== undefined) {
            if (Array.isArray(element.xmlSecurity)) {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity[0]);
            } else {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity);
            }
        }
        if (element?.ical !== undefined) {
            if (Array.isArray(element.ical)) {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical[0]);
            } else {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical);
            }
        }
        if (element?.univocityFixed !== undefined) {
            if (Array.isArray(element.univocityFixed)) {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed[0]);
            } else {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed);
            }
        }
        if (element?.jacksonXml !== undefined) {
            if (Array.isArray(element.jacksonXml)) {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml[0]);
            } else {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml);
            }
        }
        if (element?.grok !== undefined) {
            if (Array.isArray(element.grok)) {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok[0]);
            } else {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok);
            }
        }
        if (element?.gzipDeflater !== undefined) {
            if (Array.isArray(element.gzipDeflater)) {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater[0]);
            } else {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater);
            }
        }
        if (element?.soap !== undefined) {
            if (Array.isArray(element.soap)) {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap[0]);
            } else {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap);
            }
        }

        return def;
    }

    static readValidateDefinition = (element: any): ValidateDefinition => {

        let def = element ? new ValidateDefinition({...element}) : new ValidateDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readValueDefinition = (element: any): ValueDefinition => {
        return element ? new ValueDefinition({...element}) : new ValueDefinition();
    }

    static readWhenDefinition = (element: any): WhenDefinition => {

        const def = element ? new WhenDefinition({...element}) : new WhenDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }
        def.steps = WorkflowDefinitionYamlStep.readSteps(element?.steps);

        return def;
    }

    static readWireTapDefinition = (element: any): WireTapDefinition => {

        let def = element ? new WireTapDefinition({...element}) : new WireTapDefinition();
        def = ComponentApi.parseElementUri(def);

        return def;
    }

    static readBeanConstructorDefinition = (element: any): BeanConstructorDefinition => {
        return element ? new BeanConstructorDefinition({...element}) : new BeanConstructorDefinition();
    }

    static readBeanConstructorsDefinition = (element: any): BeanConstructorsDefinition => {

        const def = element ? new BeanConstructorsDefinition({...element}) : new BeanConstructorsDefinition();
        def.constructor = element && element?.constructor ? element?.constructor.map((x:any) => WorkflowDefinitionYamlStep.readBeanConstructorDefinition(x)) :[];

        return def;
    }

    static readBeanPropertiesDefinition = (element: any): BeanPropertiesDefinition => {
        const def = element ? new BeanPropertiesDefinition({...element}) : new BeanPropertiesDefinition();
        def.property = element && element?.property ? element?.property.map((x:any) => WorkflowDefinitionYamlStep.readBeanPropertyDefinition(x)) :[];
        return def;
    }

    static readBeanPropertyDefinition = (element: any): BeanPropertyDefinition => {

        const def = element ? new BeanPropertyDefinition({...element}) : new BeanPropertyDefinition();
        if (element?.properties !== undefined) {
            if (Array.isArray(element.properties)) {
                def.properties = WorkflowDefinitionYamlStep.readBeanPropertiesDefinition(element.properties[0]);
            } else {
                def.properties = WorkflowDefinitionYamlStep.readBeanPropertiesDefinition(element.properties);
            }
        }

        return def;
    }

    static readComponentScanDefinition = (element: any): ComponentScanDefinition => {
        return element ? new ComponentScanDefinition({...element}) : new ComponentScanDefinition();
    }

    static readBatchResequencerConfig = (element: any): BatchResequencerConfig => {
        return element ? new BatchResequencerConfig({...element}) : new BatchResequencerConfig();
    }

    static readStreamResequencerConfig = (element: any): StreamResequencerConfig => {
        return element ? new StreamResequencerConfig({...element}) : new StreamResequencerConfig();
    }

    static readASN1DataFormat = (element: any): ASN1DataFormat => {
        return element ? new ASN1DataFormat({...element}) : new ASN1DataFormat();
    }

    static readAvroDataFormat = (element: any): AvroDataFormat => {
        return element ? new AvroDataFormat({...element}) : new AvroDataFormat();
    }

    static readBarcodeDataFormat = (element: any): BarcodeDataFormat => {
        return element ? new BarcodeDataFormat({...element}) : new BarcodeDataFormat();
    }

    static readBase64DataFormat = (element: any): Base64DataFormat => {
        return element ? new Base64DataFormat({...element}) : new Base64DataFormat();
    }

    static readBeanioDataFormat = (element: any): BeanioDataFormat => {
        return element ? new BeanioDataFormat({...element}) : new BeanioDataFormat();
    }

    static readBindyDataFormat = (element: any): BindyDataFormat => {
        return element ? new BindyDataFormat({...element}) : new BindyDataFormat();
    }

    static readCBORDataFormat = (element: any): CBORDataFormat => {
        return element ? new CBORDataFormat({...element}) : new CBORDataFormat();
    }

    static readCryptoDataFormat = (element: any): CryptoDataFormat => {
        return element ? new CryptoDataFormat({...element}) : new CryptoDataFormat();
    }

    static readCsvDataFormat = (element: any): CsvDataFormat => {
        return element ? new CsvDataFormat({...element}) : new CsvDataFormat();
    }

    static readCustomDataFormat = (element: any): CustomDataFormat => {
        if (element && typeof element === 'string') element = {ref: element};
       return element ? new CustomDataFormat({...element}) : new CustomDataFormat();
    }

    static readDataFormatsDefinition = (element: any): DataFormatsDefinition => {

        const def = element ? new DataFormatsDefinition({...element}) : new DataFormatsDefinition();
        if (element?.univocityCsv !== undefined) {
            if (Array.isArray(element.univocityCsv)) {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv[0]);
            } else {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv);
            }
        }
        if (element?.protobuf !== undefined) {
            if (Array.isArray(element.protobuf)) {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf[0]);
            } else {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf);
            }
        }
        if (element?.tarFile !== undefined) {
            if (Array.isArray(element.tarFile)) {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile[0]);
            } else {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile);
            }
        }
        if (element?.tidyMarkup !== undefined) {
            if (Array.isArray(element.tidyMarkup)) {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup[0]);
            } else {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup);
            }
        }
        if (element?.csv !== undefined) {
            if (Array.isArray(element.csv)) {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv[0]);
            } else {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv);
            }
        }
        if (element?.base64 !== undefined) {
            if (Array.isArray(element.base64)) {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64[0]);
            } else {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64);
            }
        }
        if (element?.zipDeflater !== undefined) {
            if (Array.isArray(element.zipDeflater)) {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater[0]);
            } else {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater);
            }
        }
        if (element?.bindy !== undefined) {
            if (Array.isArray(element.bindy)) {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy[0]);
            } else {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy);
            }
        }
        if (element?.syslog !== undefined) {
            if (Array.isArray(element.syslog)) {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog[0]);
            } else {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog);
            }
        }
        if (element?.zipFile !== undefined) {
            if (Array.isArray(element.zipFile)) {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile[0]);
            } else {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile);
            }
        }
        if (element?.jaxb !== undefined) {
            if (Array.isArray(element.jaxb)) {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb[0]);
            } else {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb);
            }
        }
        if (element?.parquetAvro !== undefined) {
            if (Array.isArray(element.parquetAvro)) {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro[0]);
            } else {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro);
            }
        }
        if (element?.rss !== undefined) {
            if (Array.isArray(element.rss)) {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss[0]);
            } else {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss);
            }
        }
        if (element?.smooks !== undefined) {
            if (Array.isArray(element.smooks)) {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks[0]);
            } else {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks);
            }
        }
        if (element?.mimeMultipart !== undefined) {
            if (Array.isArray(element.mimeMultipart)) {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart[0]);
            } else {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart);
            }
        }
        if (element?.asn1 !== undefined) {
            if (Array.isArray(element.asn1)) {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1[0]);
            } else {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1);
            }
        }
        if (element?.pgp !== undefined) {
            if (Array.isArray(element.pgp)) {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp[0]);
            } else {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp);
            }
        }
        if (element?.thrift !== undefined) {
            if (Array.isArray(element.thrift)) {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift[0]);
            } else {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift);
            }
        }
        if (element?.json !== undefined) {
            if (Array.isArray(element.json)) {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json[0]);
            } else {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json);
            }
        }
        if (element?.lzf !== undefined) {
            if (Array.isArray(element.lzf)) {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf[0]);
            } else {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf);
            }
        }
        if (element?.fhirXml !== undefined) {
            if (Array.isArray(element.fhirXml)) {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml[0]);
            } else {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml);
            }
        }
        if (element?.barcode !== undefined) {
            if (Array.isArray(element.barcode)) {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode[0]);
            } else {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode);
            }
        }
        if (element?.avro !== undefined) {
            if (Array.isArray(element.avro)) {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro[0]);
            } else {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro);
            }
        }
        if (element?.yaml !== undefined) {
            if (Array.isArray(element.yaml)) {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml[0]);
            } else {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml);
            }
        }
        if (element?.beanio !== undefined) {
            if (Array.isArray(element.beanio)) {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio[0]);
            } else {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio);
            }
        }
        if (element?.fhirJson !== undefined) {
            if (Array.isArray(element.fhirJson)) {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson[0]);
            } else {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson);
            }
        }
        if (element?.fury !== undefined) {
            if (Array.isArray(element.fury)) {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury[0]);
            } else {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury);
            }
        }
        if (element?.custom !== undefined) {
            if (Array.isArray(element.custom)) {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom[0]);
            } else {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom);
            }
        }
        if (element?.flatpack !== undefined) {
            if (Array.isArray(element.flatpack)) {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack[0]);
            } else {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack);
            }
        }
        if (element?.swiftMx !== undefined) {
            if (Array.isArray(element.swiftMx)) {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx[0]);
            } else {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx);
            }
        }
        if (element?.cbor !== undefined) {
            if (Array.isArray(element.cbor)) {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor[0]);
            } else {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor);
            }
        }
        if (element?.crypto !== undefined) {
            if (Array.isArray(element.crypto)) {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto[0]);
            } else {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto);
            }
        }
        if (element?.swiftMt !== undefined) {
            if (Array.isArray(element.swiftMt)) {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt[0]);
            } else {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt);
            }
        }
        if (element?.univocityTsv !== undefined) {
            if (Array.isArray(element.univocityTsv)) {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv[0]);
            } else {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv);
            }
        }
        if (element?.hl7 !== undefined) {
            if (Array.isArray(element.hl7)) {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7[0]);
            } else {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7);
            }
        }
        if (element?.jsonApi !== undefined) {
            if (Array.isArray(element.jsonApi)) {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi[0]);
            } else {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi);
            }
        }
        if (element?.xmlSecurity !== undefined) {
            if (Array.isArray(element.xmlSecurity)) {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity[0]);
            } else {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity);
            }
        }
        if (element?.ical !== undefined) {
            if (Array.isArray(element.ical)) {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical[0]);
            } else {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical);
            }
        }
        if (element?.univocityFixed !== undefined) {
            if (Array.isArray(element.univocityFixed)) {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed[0]);
            } else {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed);
            }
        }
        if (element?.jacksonXml !== undefined) {
            if (Array.isArray(element.jacksonXml)) {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml[0]);
            } else {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml);
            }
        }
        if (element?.grok !== undefined) {
            if (Array.isArray(element.grok)) {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok[0]);
            } else {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok);
            }
        }
        if (element?.gzipDeflater !== undefined) {
            if (Array.isArray(element.gzipDeflater)) {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater[0]);
            } else {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater);
            }
        }
        if (element?.soap !== undefined) {
            if (Array.isArray(element.soap)) {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap[0]);
            } else {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap);
            }
        }

        return def;
    }

    static readFhirJsonDataFormat = (element: any): FhirJsonDataFormat => {
        return element ? new FhirJsonDataFormat({...element}) : new FhirJsonDataFormat();
    }

    static readFhirXmlDataFormat = (element: any): FhirXmlDataFormat => {
        return element ? new FhirXmlDataFormat({...element}) : new FhirXmlDataFormat();
    }

    static readFlatpackDataFormat = (element: any): FlatpackDataFormat => {
        return element ? new FlatpackDataFormat({...element}) : new FlatpackDataFormat();
    }

    static readFuryDataFormat = (element: any): FuryDataFormat => {
        return element ? new FuryDataFormat({...element}) : new FuryDataFormat();
    }

    static readGrokDataFormat = (element: any): GrokDataFormat => {
        return element ? new GrokDataFormat({...element}) : new GrokDataFormat();
    }

    static readGzipDeflaterDataFormat = (element: any): GzipDeflaterDataFormat => {
        return element ? new GzipDeflaterDataFormat({...element}) : new GzipDeflaterDataFormat();
    }

    static readHL7DataFormat = (element: any): HL7DataFormat => {
        return element ? new HL7DataFormat({...element}) : new HL7DataFormat();
    }

    static readIcalDataFormat = (element: any): IcalDataFormat => {
        return element ? new IcalDataFormat({...element}) : new IcalDataFormat();
    }

    static readJacksonXMLDataFormat = (element: any): JacksonXMLDataFormat => {
        return element ? new JacksonXMLDataFormat({...element}) : new JacksonXMLDataFormat();
    }

    static readJaxbDataFormat = (element: any): JaxbDataFormat => {
        return element ? new JaxbDataFormat({...element}) : new JaxbDataFormat();
    }

    static readJsonApiDataFormat = (element: any): JsonApiDataFormat => {
        return element ? new JsonApiDataFormat({...element}) : new JsonApiDataFormat();
    }

    static readJsonDataFormat = (element: any): JsonDataFormat => {
        return element ? new JsonDataFormat({...element}) : new JsonDataFormat();
    }

    static readLZFDataFormat = (element: any): LZFDataFormat => {
        return element ? new LZFDataFormat({...element}) : new LZFDataFormat();
    }

    static readMimeMultipartDataFormat = (element: any): MimeMultipartDataFormat => {
        return element ? new MimeMultipartDataFormat({...element}) : new MimeMultipartDataFormat();
    }

    static readPGPDataFormat = (element: any): PGPDataFormat => {
        return element ? new PGPDataFormat({...element}) : new PGPDataFormat();
    }

    static readParquetAvroDataFormat = (element: any): ParquetAvroDataFormat => {
        return element ? new ParquetAvroDataFormat({...element}) : new ParquetAvroDataFormat();
    }

    static readProtobufDataFormat = (element: any): ProtobufDataFormat => {
        return element ? new ProtobufDataFormat({...element}) : new ProtobufDataFormat();
    }

    static readRssDataFormat = (element: any): RssDataFormat => {
        return element ? new RssDataFormat({...element}) : new RssDataFormat();
    }

    static readSmooksDataFormat = (element: any): SmooksDataFormat => {
        return element ? new SmooksDataFormat({...element}) : new SmooksDataFormat();
    }

    static readSoapDataFormat = (element: any): SoapDataFormat => {
        if (element && typeof element === 'string') element = {contextPath: element};
        const def = element ? new SoapDataFormat({...element}) : new SoapDataFormat();
        return def;
    }

    static readSwiftMtDataFormat = (element: any): SwiftMtDataFormat => {
        return element ? new SwiftMtDataFormat({...element}) : new SwiftMtDataFormat();
    }

    static readSwiftMxDataFormat = (element: any): SwiftMxDataFormat => {
        return element ? new SwiftMxDataFormat({...element}) : new SwiftMxDataFormat();
    }

    static readSyslogDataFormat = (element: any): SyslogDataFormat => {
        return element ? new SyslogDataFormat({...element}) : new SyslogDataFormat();
    }

    static readTarFileDataFormat = (element: any): TarFileDataFormat => {
        return element ? new TarFileDataFormat({...element}) : new TarFileDataFormat();
    }

    static readThriftDataFormat = (element: any): ThriftDataFormat => {
        return element ? new ThriftDataFormat({...element}) : new ThriftDataFormat();
    }

    static readTidyMarkupDataFormat = (element: any): TidyMarkupDataFormat => {
        return element ? new TidyMarkupDataFormat({...element}) : new TidyMarkupDataFormat();
    }

    static readUniVocityCsvDataFormat = (element: any): UniVocityCsvDataFormat => {
        const def = element ? new UniVocityCsvDataFormat({...element}) : new UniVocityCsvDataFormat();
        def.univocityHeader = element && element?.univocityHeader ? element?.univocityHeader.map((x:any) => WorkflowDefinitionYamlStep.readUniVocityHeader(x)) :[];
        return def;
    }

    static readUniVocityFixedDataFormat = (element: any): UniVocityFixedDataFormat => {

        const def = element ? new UniVocityFixedDataFormat({...element}) : new UniVocityFixedDataFormat();
        def.univocityHeader = element && element?.univocityHeader ? element?.univocityHeader.map((x:any) => WorkflowDefinitionYamlStep.readUniVocityHeader(x)) :[];
        return def;
    }

    static readUniVocityHeader = (element: any): UniVocityHeader => {
        return element ? new UniVocityHeader({...element}) : new UniVocityHeader();
    }

    static readUniVocityTsvDataFormat = (element: any): UniVocityTsvDataFormat => {
        const def = element ? new UniVocityTsvDataFormat({...element}) : new UniVocityTsvDataFormat();
        def.univocityHeader = element && element?.univocityHeader ? element?.univocityHeader.map((x:any) => WorkflowDefinitionYamlStep.readUniVocityHeader(x)) :[];
        return def;
    }

    static readXMLSecurityDataFormat = (element: any): XMLSecurityDataFormat => {
        return element ? new XMLSecurityDataFormat({...element}) : new XMLSecurityDataFormat();
    }

    static readYAMLDataFormat = (element: any): YAMLDataFormat => {

        const def = element ? new YAMLDataFormat({...element}) : new YAMLDataFormat();
        def.typeFilter = element && element?.typeFilter ? element?.typeFilter.map((x:any) => WorkflowDefinitionYamlStep.readYAMLTypeFilterDefinition(x)) :[];
        if (element.constructor !== undefined) {
            def._constructor = element.constructor;
            delete (def as any).constructor;
        }
        return def;
    }

    static readYAMLTypeFilterDefinition = (element: any): YAMLTypeFilterDefinition => {
        return element ? new YAMLTypeFilterDefinition({...element}) : new YAMLTypeFilterDefinition();
    }

    static readZipDeflaterDataFormat = (element: any): ZipDeflaterDataFormat => {
        return element ? new ZipDeflaterDataFormat({...element}) : new ZipDeflaterDataFormat();
    }

    static readZipFileDataFormat = (element: any): ZipFileDataFormat => {
        return element ? new ZipFileDataFormat({...element}) : new ZipFileDataFormat();
    }

    static readDeadLetterChannelDefinition = (element: any): DeadLetterChannelDefinition => {
        const def = element ? new DeadLetterChannelDefinition({...element}) : new DeadLetterChannelDefinition();
        if (element?.redeliveryPolicy !== undefined) {
            if (Array.isArray(element.redeliveryPolicy)) {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy[0]);
            } else {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy);
            }
        }
        return def;
    }

    static readDefaultErrorHandlerDefinition = (element: any): DefaultErrorHandlerDefinition => {
        const def = element ? new DefaultErrorHandlerDefinition({...element}) : new DefaultErrorHandlerDefinition();
        if (element?.redeliveryPolicy !== undefined) {
            if (Array.isArray(element.redeliveryPolicy)) {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy[0]);
            } else {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy);
            }
        }
        return def;
    }

    static readJtaTransactionErrorHandlerDefinition = (element: any): JtaTransactionErrorHandlerDefinition => {
        const def = element ? new JtaTransactionErrorHandlerDefinition({...element}) : new JtaTransactionErrorHandlerDefinition();
        if (element?.redeliveryPolicy !== undefined) {
            if (Array.isArray(element.redeliveryPolicy)) {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy[0]);
            } else {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy);
            }
        }

        return def;
    }

    static readNoErrorHandlerDefinition = (element: any): NoErrorHandlerDefinition => {
        return element ? new NoErrorHandlerDefinition({...element}) : new NoErrorHandlerDefinition();
    }

    static readRefErrorHandlerDefinition = (element: any): RefErrorHandlerDefinition => {
        if (element && typeof element === 'string') element = {ref: element};
        return element ? new RefErrorHandlerDefinition({...element}) : new RefErrorHandlerDefinition();
    }

    static readSpringTransactionErrorHandlerDefinition = (element: any): SpringTransactionErrorHandlerDefinition => {

        const def = element ? new SpringTransactionErrorHandlerDefinition({...element}) : new SpringTransactionErrorHandlerDefinition();
        if (element?.redeliveryPolicy !== undefined) {
            if (Array.isArray(element.redeliveryPolicy)) {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy[0]);
            } else {
                def.redeliveryPolicy = WorkflowDefinitionYamlStep.readRedeliveryPolicyDefinition(element.redeliveryPolicy);
            }
        }

        return def;
    }

    static readCSimpleExpression = (element: any): CSimpleExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new CSimpleExpression({...element}) : new CSimpleExpression();
    }

    static readConstantExpression = (element: any): ConstantExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new ConstantExpression({...element}) : new ConstantExpression();
    }

    static readDatasonnetExpression = (element: any): DatasonnetExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new DatasonnetExpression({...element}) : new DatasonnetExpression();
    }

    static readExchangePropertyExpression = (element: any): ExchangePropertyExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new ExchangePropertyExpression({...element}) : new ExchangePropertyExpression();
    }

    static readExpressionDefinition = (element: any): ExpressionDefinition => {
        const def = element ? new ExpressionDefinition({...element}) : new ExpressionDefinition();
        if (element?.constant !== undefined) {
            if (Array.isArray(element.constant)) {
                def.constant = WorkflowDefinitionYamlStep.readConstantExpression(element.constant[0]);
            } else {
                def.constant = WorkflowDefinitionYamlStep.readConstantExpression(element.constant);
            }
        }
        if (element?.datasonnet !== undefined) {
            if (Array.isArray(element.datasonnet)) {
                def.datasonnet = WorkflowDefinitionYamlStep.readDatasonnetExpression(element.datasonnet[0]);
            } else {
                def.datasonnet = WorkflowDefinitionYamlStep.readDatasonnetExpression(element.datasonnet);
            }
        }
        if (element?.jq !== undefined) {
            if (Array.isArray(element.jq)) {
                def.jq = WorkflowDefinitionYamlStep.readJqExpression(element.jq[0]);
            } else {
                def.jq = WorkflowDefinitionYamlStep.readJqExpression(element.jq);
            }
        }
        if (element?.js !== undefined) {
            if (Array.isArray(element.js)) {
                def.js = WorkflowDefinitionYamlStep.readJavaScriptExpression(element.js[0]);
            } else {
                def.js = WorkflowDefinitionYamlStep.readJavaScriptExpression(element.js);
            }
        }
        if (element?.language !== undefined) {
            if (Array.isArray(element.language)) {
                def.language = WorkflowDefinitionYamlStep.readLanguageExpression(element.language[0]);
            } else {
                def.language = WorkflowDefinitionYamlStep.readLanguageExpression(element.language);
            }
        }
        if (element?.simple !== undefined) {
            if (Array.isArray(element.simple)) {
                def.simple = WorkflowDefinitionYamlStep.readSimpleExpression(element.simple[0]);
            } else {
                def.simple = WorkflowDefinitionYamlStep.readSimpleExpression(element.simple);
            }
        }
        if (element?.tokenize !== undefined) {
            if (Array.isArray(element.tokenize)) {
                def.tokenize = WorkflowDefinitionYamlStep.readTokenizerExpression(element.tokenize[0]);
            } else {
                def.tokenize = WorkflowDefinitionYamlStep.readTokenizerExpression(element.tokenize);
            }
        }
        if (element?.ref !== undefined) {
            if (Array.isArray(element.ref)) {
                def.ref = WorkflowDefinitionYamlStep.readRefExpression(element.ref[0]);
            } else {
                def.ref = WorkflowDefinitionYamlStep.readRefExpression(element.ref);
            }
        }
        if (element?.xpath !== undefined) {
            if (Array.isArray(element.xpath)) {
                def.xpath = WorkflowDefinitionYamlStep.readXPathExpression(element.xpath[0]);
            } else {
                def.xpath = WorkflowDefinitionYamlStep.readXPathExpression(element.xpath);
            }
        }
        if (element?.java !== undefined) {
            if (Array.isArray(element.java)) {
                def.java = WorkflowDefinitionYamlStep.readJavaExpression(element.java[0]);
            } else {
                def.java = WorkflowDefinitionYamlStep.readJavaExpression(element.java);
            }
        }
        if (element?.wasm !== undefined) {
            if (Array.isArray(element.wasm)) {
                def.wasm = WorkflowDefinitionYamlStep.readWasmExpression(element.wasm[0]);
            } else {
                def.wasm = WorkflowDefinitionYamlStep.readWasmExpression(element.wasm);
            }
        }
        if (element?.csimple !== undefined) {
            if (Array.isArray(element.csimple)) {
                def.csimple = WorkflowDefinitionYamlStep.readCSimpleExpression(element.csimple[0]);
            } else {
                def.csimple = WorkflowDefinitionYamlStep.readCSimpleExpression(element.csimple);
            }
        }
        if (element?.jsonpath !== undefined) {
            if (Array.isArray(element.jsonpath)) {
                def.jsonpath = WorkflowDefinitionYamlStep.readJsonPathExpression(element.jsonpath[0]);
            } else {
                def.jsonpath = WorkflowDefinitionYamlStep.readJsonPathExpression(element.jsonpath);
            }
        }
        if (element?.ognl !== undefined) {
            if (Array.isArray(element.ognl)) {
                def.ognl = WorkflowDefinitionYamlStep.readOgnlExpression(element.ognl[0]);
            } else {
                def.ognl = WorkflowDefinitionYamlStep.readOgnlExpression(element.ognl);
            }
        }
        if (element?.python !== undefined) {
            if (Array.isArray(element.python)) {
                def.python = WorkflowDefinitionYamlStep.readPythonExpression(element.python[0]);
            } else {
                def.python = WorkflowDefinitionYamlStep.readPythonExpression(element.python);
            }
        }
        if (element?.mvel !== undefined) {
            if (Array.isArray(element.mvel)) {
                def.mvel = WorkflowDefinitionYamlStep.readMvelExpression(element.mvel[0]);
            } else {
                def.mvel = WorkflowDefinitionYamlStep.readMvelExpression(element.mvel);
            }
        }
        if (element?.method !== undefined) {
            if (Array.isArray(element.method)) {
                def.method = WorkflowDefinitionYamlStep.readMethodCallExpression(element.method[0]);
            } else {
                def.method = WorkflowDefinitionYamlStep.readMethodCallExpression(element.method);
            }
        }
        if (element?.xquery !== undefined) {
            if (Array.isArray(element.xquery)) {
                def.xquery = WorkflowDefinitionYamlStep.readXQueryExpression(element.xquery[0]);
            } else {
                def.xquery = WorkflowDefinitionYamlStep.readXQueryExpression(element.xquery);
            }
        }
        if (element?.hl7terser !== undefined) {
            if (Array.isArray(element.hl7terser)) {
                def.hl7terser = WorkflowDefinitionYamlStep.readHl7TerserExpression(element.hl7terser[0]);
            } else {
                def.hl7terser = WorkflowDefinitionYamlStep.readHl7TerserExpression(element.hl7terser);
            }
        }
        if (element?.spel !== undefined) {
            if (Array.isArray(element.spel)) {
                def.spel = WorkflowDefinitionYamlStep.readSpELExpression(element.spel[0]);
            } else {
                def.spel = WorkflowDefinitionYamlStep.readSpELExpression(element.spel);
            }
        }
        if (element?.groovy !== undefined) {
            if (Array.isArray(element.groovy)) {
                def.groovy = WorkflowDefinitionYamlStep.readGroovyExpression(element.groovy[0]);
            } else {
                def.groovy = WorkflowDefinitionYamlStep.readGroovyExpression(element.groovy);
            }
        }
        if (element?.exchangeProperty !== undefined) {
            if (Array.isArray(element.exchangeProperty)) {
                def.exchangeProperty = WorkflowDefinitionYamlStep.readExchangePropertyExpression(element.exchangeProperty[0]);
            } else {
                def.exchangeProperty = WorkflowDefinitionYamlStep.readExchangePropertyExpression(element.exchangeProperty);
            }
        }
        if (element?.variable !== undefined) {
            if (Array.isArray(element.variable)) {
                def.variable = WorkflowDefinitionYamlStep.readVariableExpression(element.variable[0]);
            } else {
                def.variable = WorkflowDefinitionYamlStep.readVariableExpression(element.variable);
            }
        }
        if (element?.header !== undefined) {
            if (Array.isArray(element.header)) {
                def.header = WorkflowDefinitionYamlStep.readHeaderExpression(element.header[0]);
            } else {
                def.header = WorkflowDefinitionYamlStep.readHeaderExpression(element.header);
            }
        }
        if (element?.xtokenize !== undefined) {
            if (Array.isArray(element.xtokenize)) {
                def.xtokenize = WorkflowDefinitionYamlStep.readXMLTokenizerExpression(element.xtokenize[0]);
            } else {
                def.xtokenize = WorkflowDefinitionYamlStep.readXMLTokenizerExpression(element.xtokenize);
            }
        }

        return def;
    }

    static readGroovyExpression = (element: any): GroovyExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new GroovyExpression({...element}) : new GroovyExpression();
    }

    static readHeaderExpression = (element: any): HeaderExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new HeaderExpression({...element}) : new HeaderExpression();
    }

    static readHl7TerserExpression = (element: any): Hl7TerserExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new Hl7TerserExpression({...element}) : new Hl7TerserExpression();
    }

    static readJavaExpression = (element: any): JavaExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new JavaExpression({...element}) : new JavaExpression();
    }

    static readJavaScriptExpression = (element: any): JavaScriptExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new JavaScriptExpression({...element}) : new JavaScriptExpression();
    }

    static readJqExpression = (element: any): JqExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new JqExpression({...element}) : new JqExpression();
    }

    static readJsonPathExpression = (element: any): JsonPathExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new JsonPathExpression({...element}) : new JsonPathExpression();
    }

    static readLanguageExpression = (element: any): LanguageExpression => {
        return element ? new LanguageExpression({...element}) : new LanguageExpression();
    }

    static readMethodCallExpression = (element: any): MethodCallExpression => {
        return element ? new MethodCallExpression({...element}) : new MethodCallExpression();
    }

    static readMvelExpression = (element: any): MvelExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new MvelExpression({...element}) : new MvelExpression();
    }

    static readOgnlExpression = (element: any): OgnlExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return  element ? new OgnlExpression({...element}) : new OgnlExpression();
    }

    static readPythonExpression = (element: any): PythonExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new PythonExpression({...element}) : new PythonExpression();
    }

    static readRefExpression = (element: any): RefExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new RefExpression({...element}) : new RefExpression();
    }

    static readSimpleExpression = (element: any): SimpleExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new SimpleExpression({...element}) : new SimpleExpression();
    }

    static readSpELExpression = (element: any): SpELExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new SpELExpression({...element}) : new SpELExpression();
    }

    static readTokenizerExpression = (element: any): TokenizerExpression => {
        if (element && typeof element === 'string') element = {token: element};
        return element ? new TokenizerExpression({...element}) : new TokenizerExpression();
    }

    static readVariableExpression = (element: any): VariableExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new VariableExpression({...element}) : new VariableExpression();
    }

    static readWasmExpression = (element: any): WasmExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        return element ? new WasmExpression({...element}) : new WasmExpression();
    }

    static readXMLTokenizerExpression = (element: any): XMLTokenizerExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        const def = element ? new XMLTokenizerExpression({...element}) : new XMLTokenizerExpression();
        def.namespace = element && element?.namespace ? element?.namespace.map((x:any) => WorkflowDefinitionYamlStep.readPropertyDefinition(x)) :[];
        return def;
    }

    static readXPathExpression = (element: any): XPathExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        const def = element ? new XPathExpression({...element}) : new XPathExpression();
        def.namespace = element && element?.namespace ? element?.namespace.map((x:any) => WorkflowDefinitionYamlStep.readPropertyDefinition(x)) :[];
        return def;
    }

    static readXQueryExpression = (element: any): XQueryExpression => {
        if (element && typeof element === 'string') element = {expression: element};
        const def = element ? new XQueryExpression({...element}) : new XQueryExpression();
        def.namespace = element && element?.namespace ? element?.namespace.map((x:any) => WorkflowDefinitionYamlStep.readPropertyDefinition(x)) :[];
        return def;
    }

    static readCustomLoadBalancerDefinition = (element: any): CustomLoadBalancerDefinition => {
        if (element && typeof element === 'string') element = {ref: element};
        return element ? new CustomLoadBalancerDefinition({...element}) : new CustomLoadBalancerDefinition();
    }

    static readFailoverLoadBalancerDefinition = (element: any): FailoverLoadBalancerDefinition => {
        return element ? new FailoverLoadBalancerDefinition({...element}) : new FailoverLoadBalancerDefinition();
    }

    static readRandomLoadBalancerDefinition = (element: any): RandomLoadBalancerDefinition => {
        return element ? new RandomLoadBalancerDefinition({...element}) : new RandomLoadBalancerDefinition();
    }

    static readRoundRobinLoadBalancerDefinition = (element: any): RoundRobinLoadBalancerDefinition => {
        return element ? new RoundRobinLoadBalancerDefinition({...element}) : new RoundRobinLoadBalancerDefinition();
    }

    static readStickyLoadBalancerDefinition = (element: any): StickyLoadBalancerDefinition => {
        const def = element ? new StickyLoadBalancerDefinition({...element}) : new StickyLoadBalancerDefinition();
        if (element?.correlationExpression !== undefined) {
            if (Array.isArray(element.correlationExpression)) {
                def.correlationExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.correlationExpression[0]);
            } else {
                def.correlationExpression = WorkflowDefinitionYamlStep.readExpressionSubElementDefinition(element.correlationExpression);
            }
        }

        return def;
    }

    static readTopicLoadBalancerDefinition = (element: any): TopicLoadBalancerDefinition => {

        return element ? new TopicLoadBalancerDefinition({...element}) : new TopicLoadBalancerDefinition();
    }

    static readWeightedLoadBalancerDefinition = (element: any): WeightedLoadBalancerDefinition => {
        return element ? new WeightedLoadBalancerDefinition({...element}) : new WeightedLoadBalancerDefinition();
    }

    static readApiKeyDefinition = (element: any): ApiKeyDefinition => {
        return element ? new ApiKeyDefinition({...element}) : new ApiKeyDefinition();
    }

    static readBasicAuthDefinition = (element: any): BasicAuthDefinition => {
        return element ? new BasicAuthDefinition({...element}) : new BasicAuthDefinition();
    }

    static readBearerTokenDefinition = (element: any): BearerTokenDefinition => {
        return element ? new BearerTokenDefinition({...element}) : new BearerTokenDefinition();
    }

    static readDeleteDefinition = (element: any): DeleteDefinition => {

        const def = element ? new DeleteDefinition({...element}) : new DeleteDefinition();
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionYamlStep.readParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionYamlStep.readResponseMessageDefinition(x)) :[];

        return def;
    }

    static readGetDefinition = (element: any): GetDefinition => {

        const def = element ? new GetDefinition({...element}) : new GetDefinition();
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionYamlStep.readParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionYamlStep.readResponseMessageDefinition(x)) :[];

        return def;
    }

    static readHeadDefinition = (element: any): HeadDefinition => {

        const def = element ? new HeadDefinition({...element}) : new HeadDefinition();
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionYamlStep.readParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionYamlStep.readResponseMessageDefinition(x)) :[];

        return def;
    }

    static readMutualTLSDefinition = (element: any): MutualTLSDefinition => {
        return element ? new MutualTLSDefinition({...element}) : new MutualTLSDefinition();
    }

    static readOAuth2Definition = (element: any): OAuth2Definition => {

        const def = element ? new OAuth2Definition({...element}) : new OAuth2Definition();
        def.scopes = element && element?.scopes ? element?.scopes.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];

        return def;
    }

    static readOpenApiDefinition = (element: any): OpenApiDefinition => {
        return element ? new OpenApiDefinition({...element}) : new OpenApiDefinition();
    }

    static readOpenIdConnectDefinition = (element: any): OpenIdConnectDefinition => {
        return element ? new OpenIdConnectDefinition({...element}) : new OpenIdConnectDefinition();
    }

    static readParamDefinition = (element: any): ParamDefinition => {
        const def = element ? new ParamDefinition({...element}) : new ParamDefinition();
        def.examples = element && element?.examples ? element?.examples.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        return def;
    }

    static readPatchDefinition = (element: any): PatchDefinition => {
        const def = element ? new PatchDefinition({...element}) : new PatchDefinition();
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionYamlStep.readParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionYamlStep.readResponseMessageDefinition(x)) :[];

        return def;
    }

    static readPostDefinition = (element: any): PostDefinition => {

        const def = element ? new PostDefinition({...element}) : new PostDefinition();
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionYamlStep.readParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionYamlStep.readResponseMessageDefinition(x)) :[];

        return def;
    }

    static readPutDefinition = (element: any): PutDefinition => {

        const def = element ? new PutDefinition({...element}) : new PutDefinition();
        def.security = element && element?.security ? element?.security.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.param = element && element?.param ? element?.param.map((x:any) => WorkflowDefinitionYamlStep.readParamDefinition(x)) :[];
        def.responseMessage = element && element?.responseMessage ? element?.responseMessage.map((x:any) => WorkflowDefinitionYamlStep.readResponseMessageDefinition(x)) :[];

        return def;
    }

    static readResponseHeaderDefinition = (element: any): ResponseHeaderDefinition => {
        return element ? new ResponseHeaderDefinition({...element}) : new ResponseHeaderDefinition();
    }

    static readResponseMessageDefinition = (element: any): ResponseMessageDefinition => {
        const def = element ? new ResponseMessageDefinition({...element}) : new ResponseMessageDefinition();
        def.examples = element && element?.examples ? element?.examples.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        def.header = element && element?.header ? element?.header.map((x:any) => WorkflowDefinitionYamlStep.readResponseHeaderDefinition(x)) :[];
        return def;
    }

    static readRestBindingDefinition = (element: any): RestBindingDefinition => {
        return element ? new RestBindingDefinition({...element}) : new RestBindingDefinition();
    }

    static readRestConfigurationDefinition = (element: any): RestConfigurationDefinition => {
        const def = element ? new RestConfigurationDefinition({...element}) : new RestConfigurationDefinition();
        def.corsHeaders = element && element?.corsHeaders ? element?.corsHeaders.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        def.dataFormatProperty = element && element?.dataFormatProperty ? element?.dataFormatProperty.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        def.consumerProperty = element && element?.consumerProperty ? element?.consumerProperty.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        def.endpointProperty = element && element?.endpointProperty ? element?.endpointProperty.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        def.apiProperty = element && element?.apiProperty ? element?.apiProperty.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        def.componentProperty = element && element?.componentProperty ? element?.componentProperty.map((x:any) => WorkflowDefinitionYamlStep.readRestPropertyDefinition(x)) :[];
        return def;
    }

    static readRestDefinition = (element: any): RestDefinition => {
        const def = element ? new RestDefinition({...element}) : new RestDefinition();
        def.head = element && element?.head ? element?.head.map((x:any) => WorkflowDefinitionYamlStep.readHeadDefinition(x)) :[];
        def.patch = element && element?.patch ? element?.patch.map((x:any) => WorkflowDefinitionYamlStep.readPatchDefinition(x)) :[];
        if (element?.openApi !== undefined) {
            if (Array.isArray(element.openApi)) {
                def.openApi = WorkflowDefinitionYamlStep.readOpenApiDefinition(element.openApi[0]);
            } else {
                def.openApi = WorkflowDefinitionYamlStep.readOpenApiDefinition(element.openApi);
            }
        }
        def.post = element && element?.post ? element?.post.map((x:any) => WorkflowDefinitionYamlStep.readPostDefinition(x)) :[];
        def.securityRequirements = element && element?.securityRequirements ? element?.securityRequirements.map((x:any) => WorkflowDefinitionYamlStep.readSecurityDefinition(x)) :[];
        def.get = element && element?.get ? element?.get.map((x:any) => WorkflowDefinitionYamlStep.readGetDefinition(x)) :[];
        if (element?.securityDefinitions !== undefined) {
            if (Array.isArray(element.securityDefinitions)) {
                def.securityDefinitions = WorkflowDefinitionYamlStep.readRestSecuritiesDefinition(element.securityDefinitions[0]);
            } else {
                def.securityDefinitions = WorkflowDefinitionYamlStep.readRestSecuritiesDefinition(element.securityDefinitions);
            }
        }
        def.delete = element && element?.delete ? element?.delete.map((x:any) => WorkflowDefinitionYamlStep.readDeleteDefinition(x)) :[];
        def.put = element && element?.put ? element?.put.map((x:any) => WorkflowDefinitionYamlStep.readPutDefinition(x)) :[];

        return def;
    }

    static readRestPropertyDefinition = (element: any): RestPropertyDefinition => {
        return element ? new RestPropertyDefinition({...element}) : new RestPropertyDefinition();
    }

    static readRestSecuritiesDefinition = (element: any): RestSecuritiesDefinition => {

        const def = element ? new RestSecuritiesDefinition({...element}) : new RestSecuritiesDefinition();
        if (element?.openIdConnect !== undefined) {
            if (Array.isArray(element.openIdConnect)) {
                def.openIdConnect = WorkflowDefinitionYamlStep.readOpenIdConnectDefinition(element.openIdConnect[0]);
            } else {
                def.openIdConnect = WorkflowDefinitionYamlStep.readOpenIdConnectDefinition(element.openIdConnect);
            }
        }
        if (element?.apiKey !== undefined) {
            if (Array.isArray(element.apiKey)) {
                def.apiKey = WorkflowDefinitionYamlStep.readApiKeyDefinition(element.apiKey[0]);
            } else {
                def.apiKey = WorkflowDefinitionYamlStep.readApiKeyDefinition(element.apiKey);
            }
        }
        if (element?.basicAuth !== undefined) {
            if (Array.isArray(element.basicAuth)) {
                def.basicAuth = WorkflowDefinitionYamlStep.readBasicAuthDefinition(element.basicAuth[0]);
            } else {
                def.basicAuth = WorkflowDefinitionYamlStep.readBasicAuthDefinition(element.basicAuth);
            }
        }
        if (element?.mutualTLS !== undefined) {
            if (Array.isArray(element.mutualTLS)) {
                def.mutualTLS = WorkflowDefinitionYamlStep.readMutualTLSDefinition(element.mutualTLS[0]);
            } else {
                def.mutualTLS = WorkflowDefinitionYamlStep.readMutualTLSDefinition(element.mutualTLS);
            }
        }
        if (element?.bearer !== undefined) {
            if (Array.isArray(element.bearer)) {
                def.bearer = WorkflowDefinitionYamlStep.readBearerTokenDefinition(element.bearer[0]);
            } else {
                def.bearer = WorkflowDefinitionYamlStep.readBearerTokenDefinition(element.bearer);
            }
        }
        if (element?.oauth2 !== undefined) {
            if (Array.isArray(element.oauth2)) {
                def.oauth2 = WorkflowDefinitionYamlStep.readOAuth2Definition(element.oauth2[0]);
            } else {
                def.oauth2 = WorkflowDefinitionYamlStep.readOAuth2Definition(element.oauth2);
            }
        }

        return def;
    }

    static readRestsDefinition = (element: any): RestsDefinition => {

        const def = element ? new RestsDefinition({...element}) : new RestsDefinition();
        def.rest = element && element?.rest ? element?.rest.map((x:any) => WorkflowDefinitionYamlStep.readRestDefinition(x)) :[];
        return def;
    }

    static readSecurityDefinition = (element: any): SecurityDefinition => {
        return  element ? new SecurityDefinition({...element}) : new SecurityDefinition();
    }

    static readLangChain4jCharacterTokenizerDefinition = (element: any): LangChain4jCharacterTokenizerDefinition => {
        return element ? new LangChain4jCharacterTokenizerDefinition({...element}) : new LangChain4jCharacterTokenizerDefinition();
    }

    static readLangChain4jLineTokenizerDefinition = (element: any): LangChain4jLineTokenizerDefinition => {
        return element ? new LangChain4jLineTokenizerDefinition({...element}) : new LangChain4jLineTokenizerDefinition();
    }

    static readLangChain4jParagraphTokenizerDefinition = (element: any): LangChain4jParagraphTokenizerDefinition => {
        return  element ? new LangChain4jParagraphTokenizerDefinition({...element}) : new LangChain4jParagraphTokenizerDefinition();
    }

    static readLangChain4jSentenceTokenizerDefinition = (element: any): LangChain4jSentenceTokenizerDefinition => {
        return element ? new LangChain4jSentenceTokenizerDefinition({...element}) : new LangChain4jSentenceTokenizerDefinition();
    }

    static readLangChain4jTokenizerDefinition = (element: any): LangChain4jTokenizerDefinition => {
        return element ? new LangChain4jTokenizerDefinition({...element}) : new LangChain4jTokenizerDefinition();
    }

    static readLangChain4jWordTokenizerDefinition = (element: any): LangChain4jWordTokenizerDefinition => {
        return element ? new LangChain4jWordTokenizerDefinition({...element}) : new LangChain4jWordTokenizerDefinition();
    }

    static readCustomTransformerDefinition = (element: any): CustomTransformerDefinition => {
        return element ? new CustomTransformerDefinition({...element}) : new CustomTransformerDefinition();
    }

    static readDataFormatTransformerDefinition = (element: any): DataFormatTransformerDefinition => {
        const def = element ? new DataFormatTransformerDefinition({...element}) : new DataFormatTransformerDefinition();
        if (element?.univocityCsv !== undefined) {
            if (Array.isArray(element.univocityCsv)) {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv[0]);
            } else {
                def.univocityCsv = WorkflowDefinitionYamlStep.readUniVocityCsvDataFormat(element.univocityCsv);
            }
        }
        if (element?.protobuf !== undefined) {
            if (Array.isArray(element.protobuf)) {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf[0]);
            } else {
                def.protobuf = WorkflowDefinitionYamlStep.readProtobufDataFormat(element.protobuf);
            }
        }
        if (element?.tarFile !== undefined) {
            if (Array.isArray(element.tarFile)) {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile[0]);
            } else {
                def.tarFile = WorkflowDefinitionYamlStep.readTarFileDataFormat(element.tarFile);
            }
        }
        if (element?.tidyMarkup !== undefined) {
            if (Array.isArray(element.tidyMarkup)) {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup[0]);
            } else {
                def.tidyMarkup = WorkflowDefinitionYamlStep.readTidyMarkupDataFormat(element.tidyMarkup);
            }
        }
        if (element?.csv !== undefined) {
            if (Array.isArray(element.csv)) {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv[0]);
            } else {
                def.csv = WorkflowDefinitionYamlStep.readCsvDataFormat(element.csv);
            }
        }
        if (element?.base64 !== undefined) {
            if (Array.isArray(element.base64)) {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64[0]);
            } else {
                def.base64 = WorkflowDefinitionYamlStep.readBase64DataFormat(element.base64);
            }
        }
        if (element?.zipDeflater !== undefined) {
            if (Array.isArray(element.zipDeflater)) {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater[0]);
            } else {
                def.zipDeflater = WorkflowDefinitionYamlStep.readZipDeflaterDataFormat(element.zipDeflater);
            }
        }
        if (element?.bindy !== undefined) {
            if (Array.isArray(element.bindy)) {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy[0]);
            } else {
                def.bindy = WorkflowDefinitionYamlStep.readBindyDataFormat(element.bindy);
            }
        }
        if (element?.syslog !== undefined) {
            if (Array.isArray(element.syslog)) {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog[0]);
            } else {
                def.syslog = WorkflowDefinitionYamlStep.readSyslogDataFormat(element.syslog);
            }
        }
        if (element?.zipFile !== undefined) {
            if (Array.isArray(element.zipFile)) {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile[0]);
            } else {
                def.zipFile = WorkflowDefinitionYamlStep.readZipFileDataFormat(element.zipFile);
            }
        }
        if (element?.jaxb !== undefined) {
            if (Array.isArray(element.jaxb)) {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb[0]);
            } else {
                def.jaxb = WorkflowDefinitionYamlStep.readJaxbDataFormat(element.jaxb);
            }
        }
        if (element?.parquetAvro !== undefined) {
            if (Array.isArray(element.parquetAvro)) {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro[0]);
            } else {
                def.parquetAvro = WorkflowDefinitionYamlStep.readParquetAvroDataFormat(element.parquetAvro);
            }
        }
        if (element?.rss !== undefined) {
            if (Array.isArray(element.rss)) {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss[0]);
            } else {
                def.rss = WorkflowDefinitionYamlStep.readRssDataFormat(element.rss);
            }
        }
        if (element?.smooks !== undefined) {
            if (Array.isArray(element.smooks)) {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks[0]);
            } else {
                def.smooks = WorkflowDefinitionYamlStep.readSmooksDataFormat(element.smooks);
            }
        }
        if (element?.mimeMultipart !== undefined) {
            if (Array.isArray(element.mimeMultipart)) {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart[0]);
            } else {
                def.mimeMultipart = WorkflowDefinitionYamlStep.readMimeMultipartDataFormat(element.mimeMultipart);
            }
        }
        if (element?.asn1 !== undefined) {
            if (Array.isArray(element.asn1)) {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1[0]);
            } else {
                def.asn1 = WorkflowDefinitionYamlStep.readASN1DataFormat(element.asn1);
            }
        }
        if (element?.pgp !== undefined) {
            if (Array.isArray(element.pgp)) {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp[0]);
            } else {
                def.pgp = WorkflowDefinitionYamlStep.readPGPDataFormat(element.pgp);
            }
        }
        if (element?.thrift !== undefined) {
            if (Array.isArray(element.thrift)) {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift[0]);
            } else {
                def.thrift = WorkflowDefinitionYamlStep.readThriftDataFormat(element.thrift);
            }
        }
        if (element?.json !== undefined) {
            if (Array.isArray(element.json)) {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json[0]);
            } else {
                def.json = WorkflowDefinitionYamlStep.readJsonDataFormat(element.json);
            }
        }
        if (element?.lzf !== undefined) {
            if (Array.isArray(element.lzf)) {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf[0]);
            } else {
                def.lzf = WorkflowDefinitionYamlStep.readLZFDataFormat(element.lzf);
            }
        }
        if (element?.fhirXml !== undefined) {
            if (Array.isArray(element.fhirXml)) {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml[0]);
            } else {
                def.fhirXml = WorkflowDefinitionYamlStep.readFhirXmlDataFormat(element.fhirXml);
            }
        }
        if (element?.barcode !== undefined) {
            if (Array.isArray(element.barcode)) {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode[0]);
            } else {
                def.barcode = WorkflowDefinitionYamlStep.readBarcodeDataFormat(element.barcode);
            }
        }
        if (element?.avro !== undefined) {
            if (Array.isArray(element.avro)) {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro[0]);
            } else {
                def.avro = WorkflowDefinitionYamlStep.readAvroDataFormat(element.avro);
            }
        }
        if (element?.yaml !== undefined) {
            if (Array.isArray(element.yaml)) {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml[0]);
            } else {
                def.yaml = WorkflowDefinitionYamlStep.readYAMLDataFormat(element.yaml);
            }
        }
        if (element?.beanio !== undefined) {
            if (Array.isArray(element.beanio)) {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio[0]);
            } else {
                def.beanio = WorkflowDefinitionYamlStep.readBeanioDataFormat(element.beanio);
            }
        }
        if (element?.fhirJson !== undefined) {
            if (Array.isArray(element.fhirJson)) {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson[0]);
            } else {
                def.fhirJson = WorkflowDefinitionYamlStep.readFhirJsonDataFormat(element.fhirJson);
            }
        }
        if (element?.fury !== undefined) {
            if (Array.isArray(element.fury)) {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury[0]);
            } else {
                def.fury = WorkflowDefinitionYamlStep.readFuryDataFormat(element.fury);
            }
        }
        if (element?.custom !== undefined) {
            if (Array.isArray(element.custom)) {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom[0]);
            } else {
                def.custom = WorkflowDefinitionYamlStep.readCustomDataFormat(element.custom);
            }
        }
        if (element?.flatpack !== undefined) {
            if (Array.isArray(element.flatpack)) {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack[0]);
            } else {
                def.flatpack = WorkflowDefinitionYamlStep.readFlatpackDataFormat(element.flatpack);
            }
        }
        if (element?.swiftMx !== undefined) {
            if (Array.isArray(element.swiftMx)) {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx[0]);
            } else {
                def.swiftMx = WorkflowDefinitionYamlStep.readSwiftMxDataFormat(element.swiftMx);
            }
        }
        if (element?.cbor !== undefined) {
            if (Array.isArray(element.cbor)) {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor[0]);
            } else {
                def.cbor = WorkflowDefinitionYamlStep.readCBORDataFormat(element.cbor);
            }
        }
        if (element?.crypto !== undefined) {
            if (Array.isArray(element.crypto)) {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto[0]);
            } else {
                def.crypto = WorkflowDefinitionYamlStep.readCryptoDataFormat(element.crypto);
            }
        }
        if (element?.swiftMt !== undefined) {
            if (Array.isArray(element.swiftMt)) {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt[0]);
            } else {
                def.swiftMt = WorkflowDefinitionYamlStep.readSwiftMtDataFormat(element.swiftMt);
            }
        }
        if (element?.univocityTsv !== undefined) {
            if (Array.isArray(element.univocityTsv)) {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv[0]);
            } else {
                def.univocityTsv = WorkflowDefinitionYamlStep.readUniVocityTsvDataFormat(element.univocityTsv);
            }
        }
        if (element?.hl7 !== undefined) {
            if (Array.isArray(element.hl7)) {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7[0]);
            } else {
                def.hl7 = WorkflowDefinitionYamlStep.readHL7DataFormat(element.hl7);
            }
        }
        if (element?.jsonApi !== undefined) {
            if (Array.isArray(element.jsonApi)) {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi[0]);
            } else {
                def.jsonApi = WorkflowDefinitionYamlStep.readJsonApiDataFormat(element.jsonApi);
            }
        }
        if (element?.xmlSecurity !== undefined) {
            if (Array.isArray(element.xmlSecurity)) {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity[0]);
            } else {
                def.xmlSecurity = WorkflowDefinitionYamlStep.readXMLSecurityDataFormat(element.xmlSecurity);
            }
        }
        if (element?.ical !== undefined) {
            if (Array.isArray(element.ical)) {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical[0]);
            } else {
                def.ical = WorkflowDefinitionYamlStep.readIcalDataFormat(element.ical);
            }
        }
        if (element?.univocityFixed !== undefined) {
            if (Array.isArray(element.univocityFixed)) {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed[0]);
            } else {
                def.univocityFixed = WorkflowDefinitionYamlStep.readUniVocityFixedDataFormat(element.univocityFixed);
            }
        }
        if (element?.jacksonXml !== undefined) {
            if (Array.isArray(element.jacksonXml)) {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml[0]);
            } else {
                def.jacksonXml = WorkflowDefinitionYamlStep.readJacksonXMLDataFormat(element.jacksonXml);
            }
        }
        if (element?.grok !== undefined) {
            if (Array.isArray(element.grok)) {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok[0]);
            } else {
                def.grok = WorkflowDefinitionYamlStep.readGrokDataFormat(element.grok);
            }
        }
        if (element?.gzipDeflater !== undefined) {
            if (Array.isArray(element.gzipDeflater)) {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater[0]);
            } else {
                def.gzipDeflater = WorkflowDefinitionYamlStep.readGzipDeflaterDataFormat(element.gzipDeflater);
            }
        }
        if (element?.soap !== undefined) {
            if (Array.isArray(element.soap)) {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap[0]);
            } else {
                def.soap = WorkflowDefinitionYamlStep.readSoapDataFormat(element.soap);
            }
        }

        return def;
    }

    static readEndpointTransformerDefinition = (element: any): EndpointTransformerDefinition => {

        let def = element ? new EndpointTransformerDefinition({...element}) : new EndpointTransformerDefinition();
        def = ComponentApi.parseElementUri(def);

        return def;
    }

    static readLoadTransformerDefinition = (element: any): LoadTransformerDefinition => {
        return element ? new LoadTransformerDefinition({...element}) : new LoadTransformerDefinition();
    }

    static readTransformersDefinition = (element: any): TransformersDefinition => {
        const def = element ? new TransformersDefinition({...element}) : new TransformersDefinition();
        if (element?.endpointTransformer !== undefined) {
            if (Array.isArray(element.endpointTransformer)) {
                def.endpointTransformer = WorkflowDefinitionYamlStep.readEndpointTransformerDefinition(element.endpointTransformer[0]);
            } else {
                def.endpointTransformer = WorkflowDefinitionYamlStep.readEndpointTransformerDefinition(element.endpointTransformer);
            }
        }
        if (element?.customTransformer !== undefined) {
            if (Array.isArray(element.customTransformer)) {
                def.customTransformer = WorkflowDefinitionYamlStep.readCustomTransformerDefinition(element.customTransformer[0]);
            } else {
                def.customTransformer = WorkflowDefinitionYamlStep.readCustomTransformerDefinition(element.customTransformer);
            }
        }
        if (element?.loadTransformer !== undefined) {
            if (Array.isArray(element.loadTransformer)) {
                def.loadTransformer = WorkflowDefinitionYamlStep.readLoadTransformerDefinition(element.loadTransformer[0]);
            } else {
                def.loadTransformer = WorkflowDefinitionYamlStep.readLoadTransformerDefinition(element.loadTransformer);
            }
        }
        if (element?.dataFormatTransformer !== undefined) {
            if (Array.isArray(element.dataFormatTransformer)) {
                def.dataFormatTransformer = WorkflowDefinitionYamlStep.readDataFormatTransformerDefinition(element.dataFormatTransformer[0]);
            } else {
                def.dataFormatTransformer = WorkflowDefinitionYamlStep.readDataFormatTransformerDefinition(element.dataFormatTransformer);
            }
        }

        return def;
    }

    static readCustomValidatorDefinition = (element: any): CustomValidatorDefinition => {
        return element ? new CustomValidatorDefinition({...element}) : new CustomValidatorDefinition();
    }

    static readEndpointValidatorDefinition = (element: any): EndpointValidatorDefinition => {

        let def = element ? new EndpointValidatorDefinition({...element}) : new EndpointValidatorDefinition();
        def = ComponentApi.parseElementUri(def);
        return def;
    }

    static readPredicateValidatorDefinition = (element: any): PredicateValidatorDefinition => {

        const def = element ? new PredicateValidatorDefinition({...element}) : new PredicateValidatorDefinition();
        if (element?.expression !== undefined) {
            def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(element.expression);
        } else {
            const languageName: string | undefined = Object.keys(element).filter(key => WorkflowMetadataApi.hasLanguage(key))[0] || undefined;
            if (languageName){
                const exp:any = {};
                exp[languageName] = element[languageName]
                def.expression = WorkflowDefinitionYamlStep.readExpressionDefinition(exp);
                delete (def as any)[languageName];
            }
        }

        return def;
    }

    static readValidatorsDefinition = (element: any): ValidatorsDefinition => {

        const def = element ? new ValidatorsDefinition({...element}) : new ValidatorsDefinition();
        if (element?.predicateValidator !== undefined) {
            if (Array.isArray(element.predicateValidator)) {
                def.predicateValidator = WorkflowDefinitionYamlStep.readPredicateValidatorDefinition(element.predicateValidator[0]);
            } else {
                def.predicateValidator = WorkflowDefinitionYamlStep.readPredicateValidatorDefinition(element.predicateValidator);
            }
        }
        if (element?.endpointValidator !== undefined) {
            if (Array.isArray(element.endpointValidator)) {
                def.endpointValidator = WorkflowDefinitionYamlStep.readEndpointValidatorDefinition(element.endpointValidator[0]);
            } else {
                def.endpointValidator = WorkflowDefinitionYamlStep.readEndpointValidatorDefinition(element.endpointValidator);
            }
        }
        if (element?.customValidator !== undefined) {
            if (Array.isArray(element.customValidator)) {
                def.customValidator = WorkflowDefinitionYamlStep.readCustomValidatorDefinition(element.customValidator[0]);
            } else {
                def.customValidator = WorkflowDefinitionYamlStep.readCustomValidatorDefinition(element.customValidator);
            }
        }

        return def;
    }

    static readStep = (body: any, clone: boolean = false): WorkflowElement => {
        const name = Object.getOwnPropertyNames(body)[0];
        const newBody = WorkflowUtil.normalizeBody(name, body[name], clone);
        switch (name) {
            case 'aggregate': return WorkflowDefinitionYamlStep.readAggregateDefinition(newBody);
            case 'bean': return WorkflowDefinitionYamlStep.readBeanDefinition(newBody);
            case 'doCatch': return WorkflowDefinitionYamlStep.readCatchDefinition(newBody);
            case 'choice': return WorkflowDefinitionYamlStep.readChoiceDefinition(newBody);
            case 'circuitBreaker': return WorkflowDefinitionYamlStep.readCircuitBreakerDefinition(newBody);
            case 'claimCheck': return WorkflowDefinitionYamlStep.readClaimCheckDefinition(newBody);
            case 'convertBodyTo': return WorkflowDefinitionYamlStep.readConvertBodyDefinition(newBody);
            case 'convertHeaderTo': return WorkflowDefinitionYamlStep.readConvertHeaderDefinition(newBody);
            case 'convertVariableTo': return WorkflowDefinitionYamlStep.readConvertVariableDefinition(newBody);
            case 'delay': return WorkflowDefinitionYamlStep.readDelayDefinition(newBody);
            case 'dynamicRouter': return WorkflowDefinitionYamlStep.readDynamicRouterDefinition(newBody);
            case 'enrich': return WorkflowDefinitionYamlStep.readEnrichDefinition(newBody);
            case 'filter': return WorkflowDefinitionYamlStep.readFilterDefinition(newBody);
            case 'doFinally': return WorkflowDefinitionYamlStep.readFinallyDefinition(newBody);
            case 'idempotentConsumer': return WorkflowDefinitionYamlStep.readIdempotentConsumerDefinition(newBody);
            case 'kamelet': return WorkflowDefinitionYamlStep.readKameletDefinition(newBody);
            case 'loadBalance': return WorkflowDefinitionYamlStep.readLoadBalanceDefinition(newBody);
            case 'log': return WorkflowDefinitionYamlStep.readLogDefinition(newBody);
            case 'loop': return WorkflowDefinitionYamlStep.readLoopDefinition(newBody);
            case 'marshal': return WorkflowDefinitionYamlStep.readMarshalDefinition(newBody);
            case 'multicast': return WorkflowDefinitionYamlStep.readMulticastDefinition(newBody);
            case 'pausable': return WorkflowDefinitionYamlStep.readPausableDefinition(newBody);
            case 'pipeline': return WorkflowDefinitionYamlStep.readPipelineDefinition(newBody);
            case 'policy': return WorkflowDefinitionYamlStep.readPolicyDefinition(newBody);
            case 'poll': return WorkflowDefinitionYamlStep.readPollDefinition(newBody);
            case 'pollEnrich': return WorkflowDefinitionYamlStep.readPollEnrichDefinition(newBody);
            case 'process': return WorkflowDefinitionYamlStep.readProcessDefinition(newBody);
            case 'recipientList': return WorkflowDefinitionYamlStep.readRecipientListDefinition(newBody);
            case 'removeHeader': return WorkflowDefinitionYamlStep.readRemoveHeaderDefinition(newBody);
            case 'removeHeaders': return WorkflowDefinitionYamlStep.readRemoveHeadersDefinition(newBody);
            case 'removeProperties': return WorkflowDefinitionYamlStep.readRemovePropertiesDefinition(newBody);
            case 'removeProperty': return WorkflowDefinitionYamlStep.readRemovePropertyDefinition(newBody);
            case 'removeVariable': return WorkflowDefinitionYamlStep.readRemoveVariableDefinition(newBody);
            case 'resequence': return WorkflowDefinitionYamlStep.readResequenceDefinition(newBody);
            case 'resumable': return WorkflowDefinitionYamlStep.readResumableDefinition(newBody);
            case 'rollback': return WorkflowDefinitionYamlStep.readRollbackDefinition(newBody);
            case 'routingSlip': return WorkflowDefinitionYamlStep.readRoutingSlipDefinition(newBody);
            case 'saga': return WorkflowDefinitionYamlStep.readSagaDefinition(newBody);
            case 'sample': return WorkflowDefinitionYamlStep.readSamplingDefinition(newBody);
            case 'script': return WorkflowDefinitionYamlStep.readScriptDefinition(newBody);
            case 'setBody': return WorkflowDefinitionYamlStep.readSetBodyDefinition(newBody);
            case 'setExchangePattern': return WorkflowDefinitionYamlStep.readSetExchangePatternDefinition(newBody);
            case 'setHeader': return WorkflowDefinitionYamlStep.readSetHeaderDefinition(newBody);
            case 'setHeaders': return WorkflowDefinitionYamlStep.readSetHeadersDefinition(newBody);
            case 'setProperty': return WorkflowDefinitionYamlStep.readSetPropertyDefinition(newBody);
            case 'setVariable': return WorkflowDefinitionYamlStep.readSetVariableDefinition(newBody);
            case 'setVariables': return WorkflowDefinitionYamlStep.readSetVariablesDefinition(newBody);
            case 'sort': return WorkflowDefinitionYamlStep.readSortDefinition(newBody);
            case 'split': return WorkflowDefinitionYamlStep.readSplitDefinition(newBody);
            case 'step': return WorkflowDefinitionYamlStep.readStepDefinition(newBody);
            case 'stop': return WorkflowDefinitionYamlStep.readStopDefinition(newBody);
            case 'threads': return WorkflowDefinitionYamlStep.readThreadsDefinition(newBody);
            case 'throttle': return WorkflowDefinitionYamlStep.readThrottleDefinition(newBody);
            case 'throwException': return WorkflowDefinitionYamlStep.readThrowExceptionDefinition(newBody);
            case 'to': return WorkflowDefinitionYamlStep.readToDefinition(newBody);
            case 'toD': return WorkflowDefinitionYamlStep.readToDynamicDefinition(newBody);
            case 'tokenizer': return WorkflowDefinitionYamlStep.readTokenizerDefinition(newBody);
            case 'transacted': return WorkflowDefinitionYamlStep.readTransactedDefinition(newBody);
            case 'transform': return WorkflowDefinitionYamlStep.readTransformDefinition(newBody);
            case 'doTry': return WorkflowDefinitionYamlStep.readTryDefinition(newBody);
            case 'unmarshal': return WorkflowDefinitionYamlStep.readUnmarshalDefinition(newBody);
            case 'validate': return WorkflowDefinitionYamlStep.readValidateDefinition(newBody);
            case 'wireTap': return WorkflowDefinitionYamlStep.readWireTapDefinition(newBody);
            default: return new WorkflowElement('');
        }
    }

    static readSteps = (elements: any[] | undefined): WorkflowElement[] => {
        const result: WorkflowElement[] = []
        if (elements !== undefined){
            elements.forEach(e => {
                result.push(WorkflowDefinitionYamlStep.readStep(e));
            })
        }
        return result
    }
}