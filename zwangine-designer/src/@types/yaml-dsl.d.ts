/* eslint-disable */


/**
 * Fully qualified class name of builder class to use for creating and configuring the bean. The builder will use the properties values to configure the bean.
 */
export type BuilderClass = string;
/**
 * Name of method when using builder class. This method is invoked after configuring to create the actual bean. This method is often named build (used by default).
 */
export type BuilderMethod = string;
/**
 * The name of the custom destroy method to invoke on bean shutdown, such as when Ua is shutting down. The method must have no arguments, but may throw any exception.
 */
export type DestroyMethod = string;
/**
 * Name of factory bean (bean id) to use for creating the bean.
 */
export type FactoryBean = string;
/**
 * Name of method to invoke when creating the bean via a factory bean.
 */
export type FactoryMethod = string;
/**
 * The name of the custom initialization method to invoke after setting bean properties. The method must have no arguments, but may throw any exception.
 */
export type InitMethod = string;
/**
 * The name of the bean (bean id)
 */
export type Name = string;
/**
 * The script to execute that creates the bean when using scripting languages. If the script use the prefix resource: such as resource:classpath:com/foo/myscript.groovy, resource:file:/var/myscript.groovy, then its loaded from the external resource.
 */
export type Script = string;
/**
 * The script language to use when using inlined script for creating the bean, such as groovy, java, javascript etc.
 */
export type ScriptLanguage = string;
/**
 * The class name (fully qualified) of the bean
 */
export type Type = string;
export type BeansDeserializer = BeanFactory[];
/**
 * Encode and decode data structures using Abstract Syntax Notation One (ASN.1).
 */
export type ASN1File =
    | string
    | {
    id?: Id;
    unmarshalType?: UnmarshalType;
    usingIterator?: UsingIterator;
};
/**
 * The id of this node
 */
export type Id = string;
/**
 * Class to use when unmarshalling.
 */
export type UnmarshalType = string;
/**
 * If the asn1 file has more than one entry, the setting this option to true, allows working with the splitter EIP, to split the data using an iterator in a streaming mode.
 */
export type UsingIterator = boolean;
/**
 * Serialize and deserialize messages using Apache Avro binary data format.
 */
export type Avro =
    | string
    | {
    allowJmsType?: AllowJmsType;
    allowUnmarshallType?: AllowUnmarshallType;
    autoDiscoverObjectMapper?: AutoDiscoverObjectMapper;
    autoDiscoverSchemaResolver?: AutoDiscoverSchemaResolver;
    collectionType?: CollectionType;
    contentTypeHeader?: ContentTypeHeader;
    disableFeatures?: DisableFeatures;
    enableFeatures?: EnableFeatures;
    id?: Id1;
    include?: Include;
    instanceClassName?: InstanceClassName;
    jsonView?: JsonView;
    library?: Library;
    moduleClassNames?: ModuleClassNames;
    moduleRefs?: ModuleRefs;
    objectMapper?: ObjectMapper;
    schemaResolver?: SchemaResolver;
    timezone?: Timezone;
    unmarshalType?: UnmarshalType1;
    useDefaultObjectMapper?: UseDefaultObjectMapper;
    useList?: UseList;
};
/**
 * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to unmarshal to.
 */
export type AllowJmsType = boolean;
/**
 * If enabled then Jackson is allowed to attempt to use the UaJacksonUnmarshalType header during the unmarshalling. This should only be enabled when desired to be used.
 */
export type AllowUnmarshallType = boolean;
/**
 * If set to true then Jackson will lookup for an objectMapper into the registry
 */
export type AutoDiscoverObjectMapper = boolean;
/**
 * When not disabled, the SchemaResolver will be looked up into the registry
 */
export type AutoDiscoverSchemaResolver = boolean;
/**
 * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but allows to use different collection types than java.util.Collection based as default.
 */
export type CollectionType = string;
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader = boolean;
/**
 * Set of features to disable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type DisableFeatures = string;
/**
 * Set of features to enable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type EnableFeatures = string;
/**
 * The id of this node
 */
export type Id1 = string;
/**
 * If you want to marshal a pojo to JSON, and the pojo has some fields with null values. And you want to skip these null values, you can set this option to NON_NULL
 */
export type Include = string;
/**
 * Class name to use for marshal and unmarshalling
 */
export type InstanceClassName = string;
/**
 * When marshalling a POJO to JSON you might want to exclude certain fields from the JSON output. With Jackson you can use JSON views to accomplish this. This option is to refer to the class which has JsonView annotations
 */
export type JsonView = string;
/**
 * Which Avro library to use.
 */
export type Library = "ApacheAvro" | "Jackson";
/**
 * To use custom Jackson modules com.fasterxml.jackson.databind.Module specified as a String with FQN class names. Multiple classes can be separated by comma.
 */
export type ModuleClassNames = string;
/**
 * To use custom Jackson modules referred from the Ua registry. Multiple modules can be separated by comma.
 */
export type ModuleRefs = string;
/**
 * Lookup and use the existing ObjectMapper with the given id when using Jackson.
 */
export type ObjectMapper = string;
/**
 * Optional schema resolver used to lookup schemas for the data in transit.
 */
export type SchemaResolver = string;
/**
 * If set then Jackson will use the Timezone when marshalling/unmarshalling.
 */
export type Timezone = string;
/**
 * Class name of the java type to use when unmarshalling
 */
export type UnmarshalType1 = string;
/**
 * Whether to lookup and use default Jackson ObjectMapper from the registry.
 */
export type UseDefaultObjectMapper = boolean;
/**
 * To unmarshal to a List of Map or a List of Pojo.
 */
export type UseList = boolean;
/**
 * Barcode format such as QR-Code
 */
export type BarcodeFormat = string;
/**
 * Height of the barcode
 */
export type Height = number;
/**
 * The id of this node
 */
export type Id2 = string;
/**
 * Image type of the barcode such as png
 */
export type ImageType = string;
/**
 * Width of the barcode
 */
export type Width = number;
/**
 * The id of this node
 */
export type Id3 = string;
/**
 * To specific a maximum line length for the encoded data. By default 76 is used.
 */
export type LineLength = number & string;
/**
 * The line separators to use. Uses new line characters (CRLF) by default.
 */
export type LineSeparator = string;
/**
 * Instead of emitting '' and '/' we emit '-' and '_' respectively. urlSafe is only applied to encode operations. Decoding seamlessly handles both modes. Is by default false.
 */
export type UrlSafe = boolean;
/**
 * To use a custom org.apache.camel.dataformat.beanio.BeanIOErrorHandler as error handler while parsing. Configure the fully qualified class name of the error handler. Notice the options ignoreUnidentifiedRecords, ignoreUnexpectedRecords, and ignoreInvalidRecords may not be in use when you use a custom error handler.
 */
export type BeanReaderErrorHandlerType = string;
/**
 * The charset to use. Is by default the JVM platform default charset.
 */
export type Encoding = string;
/**
 * The id of this node
 */
export type Id4 = string;
/**
 * Whether to ignore invalid records.
 */
export type IgnoreInvalidRecords = boolean;
/**
 * Whether to ignore unexpected records.
 */
export type IgnoreUnexpectedRecords = boolean;
/**
 * Whether to ignore unidentified records.
 */
export type IgnoreUnidentifiedRecords = boolean;
/**
 * The BeanIO mapping file. Is by default loaded from the classpath. You can prefix with file:, http:, or classpath: to denote from where to load the mapping file.
 */
export type Mapping = string;
/**
 * The name of the stream to use.
 */
export type StreamName = string;
/**
 * This options controls whether to unmarshal as a list of objects or as a single object only. The former is the default mode, and the latter is only intended in special use-cases where beanio maps the Ua message to a single POJO bean.
 */
export type UnmarshalSingleObject = boolean;
/**
 * Whether to allow empty streams in the unmarshal process. If true, no exception will be thrown when a body without records is provided.
 */
export type AllowEmptyStream = boolean;
/**
 * Name of model class to use.
 */
export type ClassType = string;
/**
 * The id of this node
 */
export type Id5 = string;
/**
 * To configure a default locale to use, such as us for united states. To use the JVM platform default locale then use the name default
 */
export type Locale = string;
/**
 * Whether to use Csv, Fixed, or KeyValue.
 */
export type Type1 = "Csv" | "Fixed" | "KeyValue";
/**
 * When unmarshalling should a single instance be unwrapped and returned instead of wrapped in a java.util.List.
 */
export type UnwrapSingleInstance = boolean;
/**
 * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to unmarshal to.
 */
export type AllowJmsType1 = boolean;
/**
 * If enabled then Jackson CBOR is allowed to attempt to use the UaCBORUnmarshalType header during the unmarshalling. This should only be enabled when desired to be used.
 */
export type AllowUnmarshallType1 = boolean;
/**
 * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but allows to use different collection types than java.util.Collection based as default.
 */
export type CollectionType1 = string;
/**
 * Set of features to disable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type DisableFeatures1 = string;
/**
 * Set of features to enable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type EnableFeatures1 = string;
/**
 * The id of this node
 */
export type Id6 = string;
/**
 * Lookup and use the existing CBOR ObjectMapper with the given id when using Jackson.
 */
export type ObjectMapper1 = string;
/**
 * To enable pretty printing output nicely formatted. Is by default false.
 */
export type PrettyPrint = boolean;
/**
 * Class name of the java type to use when unmarshalling
 */
export type UnmarshalType2 = string;
/**
 * Whether to lookup and use default Jackson CBOR ObjectMapper from the registry.
 */
export type UseDefaultObjectMapper1 = boolean;
/**
 * To unmarshal to a List of Map or a List of Pojo.
 */
export type UseList1 = boolean;
/**
 * The JCE algorithm name indicating the cryptographic algorithm that will be used.
 */
export type Algorithm = string;
/**
 * A JCE AlgorithmParameterSpec used to initialize the Cipher. Will lookup the type using the given name as a java.security.spec.AlgorithmParameterSpec type.
 */
export type AlgorithmParameterRef = string;
/**
 * The size of the buffer used in the signature process.
 */
export type BufferSize = number & string;
/**
 * The name of the JCE Security Provider that should be used.
 */
export type CryptoProvider = string;
/**
 * The id of this node
 */
export type Id7 = string;
/**
 * Refers to a byte array containing the Initialization Vector that will be used to initialize the Cipher.
 */
export type InitVectorRef = string;
/**
 * Flag indicating that the configured IV should be inlined into the encrypted data stream. Is by default false.
 */
export type Inline = boolean;
/**
 * Refers to the secret key to lookup from the register to use.
 */
export type KeyRef = string;
/**
 * The JCE algorithm name indicating the Message Authentication algorithm.
 */
export type MacAlgorithm = string;
/**
 * Flag indicating that a Message Authentication Code should be calculated and appended to the encrypted data.
 */
export type ShouldAppendHMAC = boolean;
/**
 * Handle CSV (Comma Separated Values) payloads.
 */
export type CSV =
    | string
    | {
    allowMissingColumnNames?: AllowMissingColumnNames;
    captureHeaderRecord?: CaptureHeaderRecord;
    commentMarker?: CommentMarker;
    commentMarkerDisabled?: CommentMarkerDisabled;
    delimiter?: Delimiter;
    escape?: Escape;
    escapeDisabled?: EscapeDisabled;
    formatName?: FormatName;
    formatRef?: FormatRef;
    header?: Header;
    headerDisabled?: HeaderDisabled;
    id?: Id8;
    ignoreEmptyLines?: IgnoreEmptyLines;
    ignoreHeaderCase?: IgnoreHeaderCase;
    ignoreSurroundingSpaces?: IgnoreSurroundingSpaces;
    lazyLoad?: LazyLoad;
    marshallerFactoryRef?: MarshallerFactoryRef;
    nullString?: NullString;
    nullStringDisabled?: NullStringDisabled;
    quote?: Quote;
    quoteDisabled?: QuoteDisabled;
    quoteMode?: QuoteMode;
    recordConverterRef?: RecordConverterRef;
    recordSeparator?: RecordSeparator;
    recordSeparatorDisabled?: RecordSeparatorDisabled;
    skipHeaderRecord?: SkipHeaderRecord;
    trailingDelimiter?: TrailingDelimiter;
    trim?: Trim;
    useMaps?: UseMaps;
    useOrderedMaps?: UseOrderedMaps;
};
/**
 * Whether to allow missing column names.
 */
export type AllowMissingColumnNames = boolean;
/**
 * Whether the unmarshalling should capture the header record and store it in the message header
 */
export type CaptureHeaderRecord = boolean;
/**
 * Sets the comment marker of the reference format.
 */
export type CommentMarker = string;
/**
 * Disables the comment marker of the reference format.
 */
export type CommentMarkerDisabled = boolean;
/**
 * Sets the delimiter to use. The default value is , (comma)
 */
export type Delimiter = string;
/**
 * Sets the escape character to use
 */
export type Escape = string;
/**
 * Use for disabling using escape character
 */
export type EscapeDisabled = boolean;
/**
 * The name of the format to use, the default value is CSVFormat.DEFAULT
 */
export type FormatName = "DEFAULT" | "EXCEL" | "INFORMIX_UNLOAD" | "INFORMIX_UNLOAD_CSV" | "MYSQL" | "RFC4180";
/**
 * The reference format to use, it will be updated with the other format options, the default value is CSVFormat.DEFAULT
 */
export type FormatRef = string;
/**
 * To configure the CSV headers
 */
export type Header = string[];
/**
 * Use for disabling headers
 */
export type HeaderDisabled = boolean;
/**
 * The id of this node
 */
export type Id8 = string;
/**
 * Whether to ignore empty lines.
 */
export type IgnoreEmptyLines = boolean;
/**
 * Sets whether or not to ignore case when accessing header names.
 */
export type IgnoreHeaderCase = boolean;
/**
 * Whether to ignore surrounding spaces
 */
export type IgnoreSurroundingSpaces = boolean;
/**
 * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must be read at one.
 */
export type LazyLoad = boolean;
/**
 * Sets the implementation of the CsvMarshallerFactory interface which is able to customize marshalling/unmarshalling behavior by extending CsvMarshaller or creating it from scratch.
 */
export type MarshallerFactoryRef = string;
/**
 * Sets the null string
 */
export type NullString = string;
/**
 * Used to disable null strings
 */
export type NullStringDisabled = boolean;
/**
 * Sets the quote to use which by default is double-quote character
 */
export type Quote = string;
/**
 * Used to disable quotes
 */
export type QuoteDisabled = boolean;
/**
 * Sets the quote mode
 */
export type QuoteMode = "ALL" | "ALL_NON_NULL" | "MINIMAL" | "NON_NUMERIC" | "NONE";
/**
 * Refers to a custom CsvRecordConverter to lookup from the registry to use.
 */
export type RecordConverterRef = string;
/**
 * Sets the record separator (aka new line) which by default is new line characters (CRLF)
 */
export type RecordSeparator = string;
/**
 * Used for disabling record separator
 */
export type RecordSeparatorDisabled = string;
/**
 * Whether to skip the header record in the output
 */
export type SkipHeaderRecord = boolean;
/**
 * Sets whether or not to add a trailing delimiter.
 */
export type TrailingDelimiter = boolean;
/**
 * Sets whether or not to trim leading and trailing blanks.
 */
export type Trim = boolean;
/**
 * Whether the unmarshalling should produce maps (HashMap)for the lines values instead of lists. It requires to have header (either defined or collected).
 */
export type UseMaps = boolean;
/**
 * Whether the unmarshalling should produce ordered maps (LinkedHashMap) for the lines values instead of lists. It requires to have header (either defined or collected).
 */
export type UseOrderedMaps = boolean;
/**
 * Delegate to a custom org.apache.camel.spi.DataFormat implementation via Ua registry.
 */
export type Custom =
    | string
    | {
    id?: Id9;
    ref?: Ref;
};
/**
 * The id of this node
 */
export type Id9 = string;
/**
 * Reference to the custom org.apache.camel.spi.DataFormat to lookup from the Ua registry.
 */
export type Ref = string;
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader1 = boolean;
/**
 * If provided, specifies the elements which should NOT be encoded. Multiple elements can be separated by comma when using String parameter. Valid values for this field would include: Patient - Don't encode patient and all its children Patient.name - Don't encode the patient's name Patient.name.family - Don't encode the patient's family name .text - Don't encode the text element on any resource (only the very first position may contain a wildcard) DSTU2 note: Note that values including meta, such as Patient.meta will work for DSTU2 parsers, but values with subelements on meta such as Patient.meta.lastUpdated will only work in DSTU3 mode.
 */
export type DontEncodeElements = string;
/**
 * If supplied value(s), any resource references at the specified paths will have their resource versions encoded instead of being automatically stripped during the encoding process. This setting has no effect on the parsing process. Multiple elements can be separated by comma when using String parameter. This method provides a finer-grained level of control than setStripVersionsFromReferences(String) and any paths specified by this method will be encoded even if setStripVersionsFromReferences(String) has been set to true (which is the default)
 */
export type DontStripVersionsFromReferencesAtPaths = string;
/**
 * If provided, specifies the elements which should be encoded, to the exclusion of all others. Multiple elements can be separated by comma when using String parameter. Valid values for this field would include: Patient - Encode patient and all its children Patient.name - Encode only the patient's name Patient.name.family - Encode only the patient's family name .text - Encode the text element on any resource (only the very first position may contain a wildcard) .(mandatory) - This is a special case which causes any mandatory fields (min 0) to be encoded
 */
export type EncodeElements = string;
/**
 * If set to true (default is false), the values supplied to setEncodeElements(Set) will not be applied to the root resource (typically a Bundle), but will be applied to any sub-resources contained within it (i.e. search result resources in that bundle)
 */
export type EncodeElementsAppliesToChildResourcesOnly = boolean;
/**
 * To use a custom fhir context. Reference to object of type ca.uhn.fhir.context.FhirContext
 */
export type FhirContext = string;
/**
 * The version of FHIR to use. Possible values are: DSTU2,DSTU2_HL7ORG,DSTU2_1,DSTU3,R4,R5
 */
export type FhirVersion = "DSTU2" | "DSTU2_HL7ORG" | "DSTU2_1" | "DSTU3" | "R4" | "R5";
/**
 * When encoding, force this resource ID to be encoded as the resource ID. Reference to object of type org.hl7.fhir.instance.model.api.IIdType
 */
export type ForceResourceId = string;
/**
 * The id of this node
 */
export type Id10 = string;
/**
 * If set to true (default is false) the ID of any resources being encoded will not be included in the output. Note that this does not apply to contained resources, only to root resources. In other words, if this is set to true, contained resources will still have local IDs but the outer/containing ID will not have an ID.
 */
export type OmitResourceId = boolean;
/**
 * If set to true (which is the default), the Bundle.entry.fullUrl will override the Bundle.entry.resource's resource id if the fullUrl is defined. This behavior happens when parsing the source data into a Bundle object. Set this to false if this is not the desired behavior (e.g. the client code wishes to perform additional validation checks between the fullUrl and the resource id).
 */
export type OverrideResourceIdWithBundleEntryFullUrl = boolean;
/**
 * Registers an error handler which will be invoked when any parse errors are found. Reference to object of type ca.uhn.fhir.parser.IParserErrorHandler
 */
export type ParserErrorHandler = string;
/**
 * Sets the parser options object which will be used to supply default options to newly created parsers. Reference to object of type ca.uhn.fhir.context.ParserOptions.
 */
export type ParserOptions = string;
/**
 * If set (FQN class names), when parsing resources the parser will try to use the given types when possible, in the order that they are provided (from highest to lowest priority). For example, if a custom type which declares to implement the Patient resource is passed in here, and the parser is parsing a Bundle containing a Patient resource, the parser will use the given custom type. Multiple class names can be separated by comma.
 */
export type PreferTypes = string;
/**
 * Sets the pretty print flag, meaning that the parser will encode resources with human-readable spacing and newlines between elements instead of condensing output as much as possible.
 */
export type PrettyPrint1 = boolean;
/**
 * Sets the server's base URL used by this parser. If a value is set, resource references will be turned into relative references if they are provided as absolute URLs but have a base matching the given base.
 */
export type ServerBaseUrl = string;
/**
 * If set to true (which is the default), resource references containing a version will have the version removed when the resource is encoded. This is generally good behaviour because in most situations, references from one resource to another should be to the resource by ID, not by ID and version. In some cases though, it may be desirable to preserve the version in resource links. In that case, this value should be set to false. This method provides the ability to globally disable reference encoding. If finer-grained control is needed, use setDontStripVersionsFromReferencesAtPaths(List)
 */
export type StripVersionsFromReferences = boolean;
/**
 * If set to true (default is false) only elements marked by the FHIR specification as being summary elements will be included.
 */
export type SummaryMode = boolean;
/**
 * If set to true (default is false), narratives will not be included in the encoded values.
 */
export type SuppressNarratives = boolean;
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader2 = boolean;
/**
 * If provided, specifies the elements which should NOT be encoded. Multiple elements can be separated by comma when using String parameter. Valid values for this field would include: Patient - Don't encode patient and all its children Patient.name - Don't encode the patient's name Patient.name.family - Don't encode the patient's family name .text - Don't encode the text element on any resource (only the very first position may contain a wildcard) DSTU2 note: Note that values including meta, such as Patient.meta will work for DSTU2 parsers, but values with subelements on meta such as Patient.meta.lastUpdated will only work in DSTU3 mode.
 */
export type DontEncodeElements1 = string;
/**
 * If supplied value(s), any resource references at the specified paths will have their resource versions encoded instead of being automatically stripped during the encoding process. This setting has no effect on the parsing process. Multiple elements can be separated by comma when using String parameter. This method provides a finer-grained level of control than setStripVersionsFromReferences(String) and any paths specified by this method will be encoded even if setStripVersionsFromReferences(String) has been set to true (which is the default)
 */
export type DontStripVersionsFromReferencesAtPaths1 = string;
/**
 * If provided, specifies the elements which should be encoded, to the exclusion of all others. Multiple elements can be separated by comma when using String parameter. Valid values for this field would include: Patient - Encode patient and all its children Patient.name - Encode only the patient's name Patient.name.family - Encode only the patient's family name .text - Encode the text element on any resource (only the very first position may contain a wildcard) .(mandatory) - This is a special case which causes any mandatory fields (min 0) to be encoded
 */
export type EncodeElements1 = string;
/**
 * If set to true (default is false), the values supplied to setEncodeElements(Set) will not be applied to the root resource (typically a Bundle), but will be applied to any sub-resources contained within it (i.e. search result resources in that bundle)
 */
export type EncodeElementsAppliesToChildResourcesOnly1 = boolean;
/**
 * To use a custom fhir context. Reference to object of type ca.uhn.fhir.context.FhirContext
 */
export type FhirContext1 = string;
/**
 * The version of FHIR to use. Possible values are: DSTU2,DSTU2_HL7ORG,DSTU2_1,DSTU3,R4,R5
 */
export type FhirVersion1 = "DSTU2" | "DSTU2_HL7ORG" | "DSTU2_1" | "DSTU3" | "R4" | "R5";
/**
 * When encoding, force this resource ID to be encoded as the resource ID. Reference to object of type org.hl7.fhir.instance.model.api.IIdType
 */
export type ForceResourceId1 = string;
/**
 * The id of this node
 */
export type Id11 = string;
/**
 * If set to true (default is false) the ID of any resources being encoded will not be included in the output. Note that this does not apply to contained resources, only to root resources. In other words, if this is set to true, contained resources will still have local IDs but the outer/containing ID will not have an ID.
 */
export type OmitResourceId1 = boolean;
/**
 * If set to true (which is the default), the Bundle.entry.fullUrl will override the Bundle.entry.resource's resource id if the fullUrl is defined. This behavior happens when parsing the source data into a Bundle object. Set this to false if this is not the desired behavior (e.g. the client code wishes to perform additional validation checks between the fullUrl and the resource id).
 */
export type OverrideResourceIdWithBundleEntryFullUrl1 = boolean;
/**
 * Registers an error handler which will be invoked when any parse errors are found. Reference to object of type ca.uhn.fhir.parser.IParserErrorHandler
 */
export type ParserErrorHandler1 = string;
/**
 * Sets the parser options object which will be used to supply default options to newly created parsers. Reference to object of type ca.uhn.fhir.context.ParserOptions.
 */
export type ParserOptions1 = string;
/**
 * If set (FQN class names), when parsing resources the parser will try to use the given types when possible, in the order that they are provided (from highest to lowest priority). For example, if a custom type which declares to implement the Patient resource is passed in here, and the parser is parsing a Bundle containing a Patient resource, the parser will use the given custom type. Multiple class names can be separated by comma.
 */
export type PreferTypes1 = string;
/**
 * Sets the pretty print flag, meaning that the parser will encode resources with human-readable spacing and newlines between elements instead of condensing output as much as possible.
 */
export type PrettyPrint2 = boolean;
/**
 * Sets the server's base URL used by this parser. If a value is set, resource references will be turned into relative references if they are provided as absolute URLs but have a base matching the given base.
 */
export type ServerBaseUrl1 = string;
/**
 * If set to true (which is the default), resource references containing a version will have the version removed when the resource is encoded. This is generally good behaviour because in most situations, references from one resource to another should be to the resource by ID, not by ID and version. In some cases though, it may be desirable to preserve the version in resource links. In that case, this value should be set to false. This method provides the ability to globally disable reference encoding. If finer-grained control is needed, use setDontStripVersionsFromReferencesAtPaths(List)
 */
export type StripVersionsFromReferences1 = boolean;
/**
 * If set to true (default is false) only elements marked by the FHIR specification as being summary elements will be included.
 */
export type SummaryMode1 = boolean;
/**
 * If set to true (default is false), narratives will not be included in the encoded values.
 */
export type SuppressNarratives1 = boolean;
/**
 * Allows for lines to be shorter than expected and ignores the extra characters
 */
export type AllowShortLines = boolean;
/**
 * The flatpack pzmap configuration file. Can be omitted in simpler situations, but its preferred to use the pzmap.
 */
export type Definition = string;
/**
 * The delimiter char (could be ; , or similar)
 */
export type Delimiter1 = string;
/**
 * Delimited or fixed. Is by default false = delimited
 */
export type Fixed = boolean;
/**
 * The id of this node
 */
export type Id12 = string;
/**
 * Allows for lines to be longer than expected and ignores the extra characters.
 */
export type IgnoreExtraColumns = boolean;
/**
 * Whether the first line is ignored for delimited files (for the column headers). Is by default true.
 */
export type IgnoreFirstRecord = boolean;
/**
 * References to a custom parser factory to lookup in the registry
 */
export type ParserFactoryRef = string;
/**
 * If the text is qualified with a character. Uses quote character by default.
 */
export type TextQualifier = string;
/**
 * Whether to auto-discover Fury from the registry
 */
export type AllowAutoWiredFury = boolean;
/**
 * The id of this node
 */
export type Id13 = string;
/**
 * Whether to require register classes
 */
export type RequireClassRegistration = boolean;
/**
 * Whether to use the threadsafe fury
 */
export type ThreadSafe = boolean;
/**
 * Class of the java type to use when unmarshalling
 */
export type UnmarshalType3 = string;
/**
 * If false, every line of input is matched for pattern only once. Otherwise the line can be scanned multiple times when non-terminal pattern is used.
 */
export type AllowMultipleMatchesPerLine = boolean;
/**
 * Turns on flattened mode. In flattened mode the exception is thrown when there are multiple pattern matches with same key.
 */
export type Flattened = boolean;
/**
 * The id of this node
 */
export type Id14 = string;
/**
 * Whether to capture named expressions only or not (i.e. %{IP:ip} but not ${IP})
 */
export type NamedOnly = boolean;
/**
 * The grok pattern to match lines of input
 */
export type Pattern = string;
/**
 * The id of this node
 */
export type Id15 = string;
/**
 * The id of this node
 */
export type Id16 = string;
/**
 * Whether to validate the HL7 message Is by default true.
 */
export type Validate = boolean;
/**
 * The id of this node
 */
export type Id17 = string;
/**
 * Whether to validate.
 */
export type Validating = boolean;
/**
 * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to unmarshal to.
 */
export type AllowJmsType2 = boolean;
/**
 * If enabled then Jackson is allowed to attempt to use the UaJacksonUnmarshalType header during the unmarshalling. This should only be enabled when desired to be used.
 */
export type AllowUnmarshallType2 = boolean;
/**
 * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but allows to use different collection types than java.util.Collection based as default.
 */
export type CollectionType2 = string;
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader3 = boolean;
/**
 * Set of features to disable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type DisableFeatures2 = string;
/**
 * Set of features to enable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type EnableFeatures2 = string;
/**
 * Whether to enable the JAXB annotations module when using jackson. When enabled then JAXB annotations can be used by Jackson.
 */
export type EnableJaxbAnnotationModule = boolean;
/**
 * The id of this node
 */
export type Id18 = string;
/**
 * If you want to marshal a pojo to JSON, and the pojo has some fields with null values. And you want to skip these null values, you can set this option to NON_NULL
 */
export type Include1 = string;
/**
 * When marshalling a POJO to JSON you might want to exclude certain fields from the JSON output. With Jackson you can use JSON views to accomplish this. This option is to refer to the class which has JsonView annotations
 */
export type JsonView1 = string;
/**
 * To use custom Jackson modules com.fasterxml.jackson.databind.Module specified as a String with FQN class names. Multiple classes can be separated by comma.
 */
export type ModuleClassNames1 = string;
/**
 * To use custom Jackson modules referred from the Ua registry. Multiple modules can be separated by comma.
 */
export type ModuleRefs1 = string;
/**
 * To enable pretty printing output nicely formatted. Is by default false.
 */
export type PrettyPrint3 = boolean;
/**
 * If set then Jackson will use the Timezone when marshalling/unmarshalling.
 */
export type Timezone1 = string;
/**
 * Class name of the java type to use when unmarshalling
 */
export type UnmarshalType4 = string;
/**
 * To unmarshal to a List of Map or a List of Pojo.
 */
export type UseList2 = boolean;
/**
 * Lookup and use the existing XmlMapper with the given id.
 */
export type XmlMapper = string;
/**
 * Only in use if schema validation has been enabled. Restrict access to the protocols specified for external reference set by the schemaLocation attribute, Import and Include element. Examples of protocols are file, http, jar:file. false or none to deny all access to external references; a specific protocol, such as file, to give permission to only the protocol; the keyword all to grant permission to all protocols.
 */
export type AccessExternalSchemaProtocols = string;
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader4 = boolean;
/**
 * Package name where your JAXB classes are located.
 */
export type ContextPath = string;
/**
 * This can be set to true to mark that the contextPath is referring to a classname and not a package name.
 */
export type ContextPathIsClassName = boolean;
/**
 * To overrule and use a specific encoding
 */
export type Encoding1 = string;
/**
 * To ignore non xml characheters and replace them with an empty space.
 */
export type FilterNonXmlChars = boolean;
/**
 * To turn on marshalling XML fragment trees. By default JAXB looks for XmlRootElement annotation on given class to operate on whole XML tree. This is useful but not always - sometimes generated code does not have XmlRootElement annotation, sometimes you need unmarshall only part of tree. In that case you can use partial unmarshalling. To enable this behaviours you need set property partClass. Ua will pass this class to JAXB's unmarshaler.
 */
export type Fragment = boolean;
/**
 * The id of this node
 */
export type Id19 = string;
/**
 * Whether to ignore JAXBElement elements - only needed to be set to false in very special use-cases.
 */
export type IgnoreJAXBElement = boolean;
/**
 * Refers to a custom java.util.Map to lookup in the registry containing custom JAXB provider properties to be used with the JAXB marshaller.
 */
export type JaxbProviderProperties = string;
/**
 * Whether marhsalling must be java objects with JAXB annotations. And if not then it fails. This option can be set to false to relax that, such as when the data is already in XML format.
 */
export type MustBeJAXBElement = boolean;
/**
 * When marshalling using JAXB or SOAP then the JAXB implementation will automatic assign namespace prefixes, such as ns2, ns3, ns4 etc. To control this mapping, Ua allows you to refer to a map which contains the desired mapping.
 */
export type NamespacePrefixRef = string;
/**
 * To define the location of the namespaceless schema
 */
export type NoNamespaceSchemaLocation = string;
/**
 * Whether to allow using ObjectFactory classes to create the POJO classes during marshalling. This only applies to POJO classes that has not been annotated with JAXB and providing jaxb.index descriptor files.
 */
export type ObjectFactory = boolean;
/**
 * Name of class used for fragment parsing. See more details at the fragment option.
 */
export type PartClass = string;
/**
 * XML namespace to use for fragment parsing. See more details at the fragment option.
 */
export type PartNamespace = string;
/**
 * To enable pretty printing output nicely formatted. Is by default false.
 */
export type PrettyPrint4 = boolean;
/**
 * To validate against an existing schema. Your can use the prefix classpath:, file: or http: to specify how the resource should be resolved. You can separate multiple schema files by using the ',' character.
 */
export type Schema = string;
/**
 * To define the location of the schema
 */
export type SchemaLocation = string;
/**
 * Sets the schema severity level to use when validating against a schema. This level determines the minimum severity error that triggers JAXB to stop continue parsing. The default value of 0 (warning) means that any error (warning, error or fatal error) will trigger JAXB to stop. There are the following three levels: 0=warning, 1=error, 2=fatal error.
 */
export type SchemaSeverityLevel = "0" | "1" | "2";
/**
 * To use a custom xml stream writer.
 */
export type XmlStreamWriterWrapper = string;
/**
 * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to unmarshal to.
 */
export type AllowJmsType3 = boolean;
/**
 * If enabled then Jackson is allowed to attempt to use the UaJacksonUnmarshalType header during the unmarshalling. This should only be enabled when desired to be used.
 */
export type AllowUnmarshallType3 = boolean;
/**
 * If set to true then Jackson will look for an objectMapper to use from the registry
 */
export type AutoDiscoverObjectMapper1 = boolean;
/**
 * When not disabled, the SchemaResolver will be looked up into the registry
 */
export type AutoDiscoverSchemaResolver1 = boolean;
/**
 * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but allows using different collection types than java.util.Collection based as default.
 */
export type CollectionType3 = string;
/**
 * Force generator that outputs JSON content to combine surrogate pairs (if any) into 4-byte characters. This should be preferred when using 4-byte characters such as Japanese.
 */
export type CombineUnicodeSurrogates = boolean;
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader5 = boolean;
/**
 * To configure the date format while marshall or unmarshall Date fields in JSON using Gson
 */
export type DateFormatPattern = string;
/**
 * Set of features to disable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type DisableFeatures3 = string;
/**
 * Set of features to enable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type EnableFeatures3 = string;
/**
 * The id of this node
 */
export type Id20 = string;
/**
 * If you want to marshal a pojo to JSON, and the pojo has some fields with null values. And you want to skip these null values, you can set this option to NON_NULL
 */
export type Include2 = string;
/**
 * When marshalling a POJO to JSON you might want to exclude certain fields from the JSON output. With Jackson you can use JSON views to accomplish this. This option is to refer to the class which has JsonView annotations
 */
export type JsonView2 = string;
/**
 * Which json library to use.
 */
export type Library1 = "Fastjson" | "Gson" | "Jackson" | "Johnzon" | "Jsonb";
/**
 * To use custom Jackson modules com.fasterxml.jackson.databind.Module specified as a String with FQN class names. Multiple classes can be separated by comma.
 */
export type ModuleClassNames2 = string;
/**
 * To use custom Jackson modules referred from the Ua registry. Multiple modules can be separated by comma.
 */
export type ModuleRefs2 = string;
/**
 * If set then Jackson will use the the defined Property Naming Strategy.Possible values are: LOWER_CAMEL_CASE, LOWER_DOT_CASE, LOWER_CASE, KEBAB_CASE, SNAKE_CASE and UPPER_CAMEL_CASE
 */
export type NamingStrategy = string;
/**
 * Lookup and use the existing ObjectMapper with the given id when using Jackson.
 */
export type ObjectMapper2 = string;
/**
 * To enable pretty printing output nicely formatted. Is by default false.
 */
export type PrettyPrint5 = boolean;
/**
 * Optional schema resolver used to lookup schemas for the data in transit.
 */
export type SchemaResolver1 = string;
/**
 * If set then Jackson will use the Timezone when marshalling/unmarshalling. This option will have no effect on the others Json DataFormat, like gson and fastjson.
 */
export type Timezone2 = string;
/**
 * Class name of the java type to use when unmarshalling
 */
export type UnmarshalType5 = string;
/**
 * Whether to lookup and use default Jackson ObjectMapper from the registry.
 */
export type UseDefaultObjectMapper2 = boolean;
/**
 * To unmarshal to a List of Map or a List of Pojo.
 */
export type UseList3 = boolean;
/**
 * The classes to take into account for the marshalling. Multiple classes can be separated by comma.
 */
export type DataFormatTypes = string;
/**
 * The id of this node
 */
export type Id21 = string;
/**
 * The class to take into account while unmarshalling.
 */
export type MainFormatType = string;
/**
 * The id of this node
 */
export type Id22 = string;
/**
 * Enable encoding (compress) using multiple processing cores.
 */
export type UsingParallelCompression = boolean;
/**
 * Defines whether the content of binary parts in the MIME multipart is binary (true) or Base-64 encoded (false) Default is false.
 */
export type BinaryContent = boolean;
/**
 * Defines whether the MIME-Multipart headers are part of the message body (true) or are set as Ua headers (false). Default is false.
 */
export type HeadersInline = boolean;
/**
 * The id of this node
 */
export type Id23 = string;
/**
 * A regex that defines which Ua headers are also included as MIME headers into the MIME multipart. This will only work if headersInline is set to true. Default is to include no headers
 */
export type IncludeHeaders = string;
/**
 * Specify the subtype of the MIME Multipart. Default is mixed.
 */
export type MultipartSubType = string;
/**
 * Defines whether a message without attachment is also marshaled into a MIME Multipart (with only one body part). Default is false.
 */
export type MultipartWithoutAttachment = boolean;
/**
 * Parquet Avro serialization and de-serialization.
 */
export type ParquetFile =
    | string
    | {
    compressionCodecName?: CompressionCodecName;
    id?: Id24;
    lazyLoad?: LazyLoad1;
    unmarshalType?: UnmarshalType6;
};
/**
 * Compression codec to use when marshalling.
 */
export type CompressionCodecName = "UNCOMPRESSED" | "SNAPPY" | "GZIP" | "LZO" | "BROTLI" | "LZ4" | "ZSTD" | "LZ4_RAW";
/**
 * The id of this node
 */
export type Id24 = string;
/**
 * Whether the unmarshalling should produce an iterator of records or read all the records at once.
 */
export type LazyLoad1 = boolean;
/**
 * Class to use when (un)marshalling. If omitted, parquet files are converted into Avro's GenericRecords for unmarshalling and input objects are assumed as GenericRecords for marshalling.
 */
export type UnmarshalType6 = string;
/**
 * Symmetric key encryption algorithm; possible values are defined in org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags; for example 2 (= TRIPLE DES), 3 (= CAST5), 4 (= BLOWFISH), 6 (= DES), 7 (= AES_128). Only relevant for encrypting.
 */
export type Algorithm1 = number;
/**
 * This option will cause PGP to base64 encode the encrypted text, making it available for copy/paste, etc.
 */
export type Armored = boolean;
/**
 * Compression algorithm; possible values are defined in org.bouncycastle.bcpg.CompressionAlgorithmTags; for example 0 (= UNCOMPRESSED), 1 (= ZIP), 2 (= ZLIB), 3 (= BZIP2). Only relevant for encrypting.
 */
export type CompressionAlgorithm = number;
/**
 * Signature hash algorithm; possible values are defined in org.bouncycastle.bcpg.HashAlgorithmTags; for example 2 (= SHA1), 8 (= SHA256), 9 (= SHA384), 10 (= SHA512), 11 (=SHA224). Only relevant for signing.
 */
export type HashAlgorithm = number;
/**
 * The id of this node
 */
export type Id25 = string;
/**
 * Adds an integrity check/sign into the encryption file. The default value is true.
 */
export type Integrity = boolean;
/**
 * Filename of the keyring; must be accessible as a classpath resource (but you can specify a location in the file system by using the file: prefix).
 */
export type KeyFileName = string;
/**
 * The user ID of the key in the PGP keyring used during encryption. Can also be only a part of a user ID. For example, if the user ID is Test User then you can use the part Test User or to address the user ID.
 */
export type KeyUserid = string;
/**
 * Password used when opening the private key (not used for encryption).
 */
export type Password = string;
/**
 * Java Cryptography Extension (JCE) provider, default is Bouncy Castle (BC). Alternatively you can use, for example, the IAIK JCE provider; in this case the provider must be registered beforehand and the Bouncy Castle provider must not be registered beforehand. The Sun JCE provider does not work.
 */
export type Provider = string;
/**
 * Filename of the keyring to use for signing (during encryption) or for signature verification (during decryption); must be accessible as a classpath resource (but you can specify a location in the file system by using the file: prefix).
 */
export type SignatureKeyFileName = string;
/**
 * Keyring used for signing/verifying as byte array. You can not set the signatureKeyFileName and signatureKeyRing at the same time.
 */
export type SignatureKeyRing = string;
/**
 * User ID of the key in the PGP keyring used for signing (during encryption) or signature verification (during decryption). During the signature verification process the specified User ID restricts the public keys from the public keyring which can be used for the verification. If no User ID is specified for the signature verficiation then any public key in the public keyring can be used for the verification. Can also be only a part of a user ID. For example, if the user ID is Test User then you can use the part Test User or to address the User ID.
 */
export type SignatureKeyUserid = string;
/**
 * Password used when opening the private key used for signing (during encryption).
 */
export type SignaturePassword = string;
/**
 * Controls the behavior for verifying the signature during unmarshaling. There are 4 values possible: optional: The PGP message may or may not contain signatures; if it does contain signatures, then a signature verification is executed. required: The PGP message must contain at least one signature; if this is not the case an exception (PGPException) is thrown. A signature verification is executed. ignore: Contained signatures in the PGP message are ignored; no signature verification is executed. no_signature_allowed: The PGP message must not contain a signature; otherwise an exception (PGPException) is thrown.
 */
export type SignatureVerificationOption = string;
/**
 * Serialize and deserialize Java objects using Google's Protocol buffers.
 */
export type Protobuf =
    | string
    | {
    allowJmsType?: AllowJmsType4;
    allowUnmarshallType?: AllowUnmarshallType4;
    autoDiscoverObjectMapper?: AutoDiscoverObjectMapper2;
    autoDiscoverSchemaResolver?: AutoDiscoverSchemaResolver2;
    collectionType?: CollectionType4;
    contentTypeFormat?: ContentTypeFormat;
    contentTypeHeader?: ContentTypeHeader6;
    disableFeatures?: DisableFeatures4;
    enableFeatures?: EnableFeatures4;
    id?: Id26;
    include?: Include3;
    instanceClass?: InstanceClass;
    jsonView?: JsonView3;
    library?: Library2;
    moduleClassNames?: ModuleClassNames3;
    moduleRefs?: ModuleRefs3;
    objectMapper?: ObjectMapper3;
    schemaResolver?: SchemaResolver2;
    timezone?: Timezone3;
    unmarshalType?: UnmarshalType7;
    useDefaultObjectMapper?: UseDefaultObjectMapper3;
    useList?: UseList4;
};
/**
 * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname to use to unmarshal to.
 */
export type AllowJmsType4 = boolean;
/**
 * If enabled then Jackson is allowed to attempt to use the UaJacksonUnmarshalType header during the unmarshalling. This should only be enabled when desired to be used.
 */
export type AllowUnmarshallType4 = boolean;
/**
 * If set to true then Jackson will lookup for an objectMapper into the registry
 */
export type AutoDiscoverObjectMapper2 = boolean;
/**
 * When not disabled, the SchemaResolver will be looked up into the registry
 */
export type AutoDiscoverSchemaResolver2 = boolean;
/**
 * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but allows to use different collection types than java.util.Collection based as default.
 */
export type CollectionType4 = string;
/**
 * Defines a content type format in which protobuf message will be serialized/deserialized from(to) the Java been. The format can either be native or json for either native protobuf or json fields representation. The default value is native.
 */
export type ContentTypeFormat = "native" | "json";
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader6 = boolean;
/**
 * Set of features to disable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type DisableFeatures4 = string;
/**
 * Set of features to enable on the Jackson com.fasterxml.jackson.databind.ObjectMapper. The features should be a name that matches a enum from com.fasterxml.jackson.databind.SerializationFeature, com.fasterxml.jackson.databind.DeserializationFeature, or com.fasterxml.jackson.databind.MapperFeature Multiple features can be separated by comma
 */
export type EnableFeatures4 = string;
/**
 * The id of this node
 */
export type Id26 = string;
/**
 * If you want to marshal a pojo to JSON, and the pojo has some fields with null values. And you want to skip these null values, you can set this option to NON_NULL
 */
export type Include3 = string;
/**
 * Name of class to use when unmarshalling
 */
export type InstanceClass = string;
/**
 * When marshalling a POJO to JSON you might want to exclude certain fields from the JSON output. With Jackson you can use JSON views to accomplish this. This option is to refer to the class which has JsonView annotations
 */
export type JsonView3 = string;
/**
 * Which Protobuf library to use.
 */
export type Library2 = "GoogleProtobuf" | "Jackson";
/**
 * To use custom Jackson modules com.fasterxml.jackson.databind.Module specified as a String with FQN class names. Multiple classes can be separated by comma.
 */
export type ModuleClassNames3 = string;
/**
 * To use custom Jackson modules referred from the Ua registry. Multiple modules can be separated by comma.
 */
export type ModuleRefs3 = string;
/**
 * Lookup and use the existing ObjectMapper with the given id when using Jackson.
 */
export type ObjectMapper3 = string;
/**
 * Optional schema resolver used to lookup schemas for the data in transit.
 */
export type SchemaResolver2 = string;
/**
 * If set then Jackson will use the Timezone when marshalling/unmarshalling.
 */
export type Timezone3 = string;
/**
 * Class name of the java type to use when unmarshalling
 */
export type UnmarshalType7 = string;
/**
 * Whether to lookup and use default Jackson ObjectMapper from the registry.
 */
export type UseDefaultObjectMapper3 = boolean;
/**
 * To unmarshal to a List of Map or a List of Pojo.
 */
export type UseList4 = boolean;
/**
 * The id of this node
 */
export type Id27 = string;
/**
 * The id of this node
 */
export type Id28 = string;
/**
 * Path to the Smooks configuration file.
 */
export type SmooksConfig = string;
/**
 * Marshal Java objects to SOAP messages and back.
 */
export type SOAP =
    | string
    | {
    contextPath?: ContextPath1;
    elementNameStrategyRef?: ElementNameStrategyRef;
    encoding?: Encoding2;
    id?: Id29;
    namespacePrefixRef?: NamespacePrefixRef1;
    schema?: Schema1;
    version?: Version;
};
/**
 * Package name where your JAXB classes are located.
 */
export type ContextPath1 = string;
/**
 * Refers to an element strategy to lookup from the registry. An element name strategy is used for two purposes. The first is to find a xml element name for a given object and soap action when marshaling the object into a SOAP message. The second is to find an Exception class for a given soap fault name. The following three element strategy class name is provided out of the box. QNameStrategy - Uses a fixed qName that is configured on instantiation. Exception lookup is not supported TypeNameStrategy - Uses the name and namespace from the XMLType annotation of the given type. If no namespace is set then package-info is used. Exception lookup is not supported ServiceInterfaceStrategy - Uses information from a webservice interface to determine the type name and to find the exception class for a SOAP fault All three classes is located in the package name org.apache.camel.dataformat.soap.name If you have generated the web service stub code with cxf-codegen or a similar tool then you probably will want to use the ServiceInterfaceStrategy. In the case you have no annotated service interface you should use QNameStrategy or TypeNameStrategy.
 */
export type ElementNameStrategyRef = string;
/**
 * To overrule and use a specific encoding
 */
export type Encoding2 = string;
/**
 * The id of this node
 */
export type Id29 = string;
/**
 * When marshalling using JAXB or SOAP then the JAXB implementation will automatic assign namespace prefixes, such as ns2, ns3, ns4 etc. To control this mapping, Ua allows you to refer to a map which contains the desired mapping.
 */
export type NamespacePrefixRef1 = string;
/**
 * To validate against an existing schema. Your can use the prefix classpath:, file: or http: to specify how the resource should be resolved. You can separate multiple schema files by using the ',' character.
 */
export type Schema1 = string;
/**
 * SOAP version should either be 1.1 or 1.2. Is by default 1.1
 */
export type Version = "1.1" | "1.2";
/**
 * Encode and decode SWIFT MT messages.
 */
export type SWIFTMT =
    | string
    | {
    id?: Id30;
    writeInJson?: WriteInJson;
};
/**
 * The id of this node
 */
export type Id30 = string;
/**
 * The flag indicating that messages must be marshalled in a JSON format.
 */
export type WriteInJson = boolean;
/**
 * The id of this node
 */
export type Id31 = string;
/**
 * Refers to a specific configuration to use when unmarshalling an input stream to lookup from the registry.
 */
export type ReadConfigRef = string;
/**
 * The type of MX message to produce when unmarshalling an input stream. If not set, it will be automatically detected from the namespace used.
 */
export type ReadMessageId = string;
/**
 * Refers to a specific configuration to use when marshalling a message to lookup from the registry.
 */
export type WriteConfigRef = string;
/**
 * The flag indicating that messages must be marshalled in a JSON format.
 */
export type WriteInJson1 = boolean;
/**
 * The id of this node
 */
export type Id32 = string;
/**
 * If the tar file has more than one entry, setting this option to true, allows to get the iterator even if the directory is empty
 */
export type AllowEmptyDirectory = boolean;
/**
 * The id of this node
 */
export type Id33 = string;
/**
 * Set the maximum decompressed size of a tar file (in bytes). The default value if not specified corresponds to 1 gigabyte. An IOException will be thrown if the decompressed size exceeds this amount. Set to -1 to disable setting a maximum decompressed size.
 */
export type MaxDecompressedSize = number & string;
/**
 * If the file name contains path elements, setting this option to true, allows the path to be maintained in the tar file.
 */
export type PreservePathElements = boolean;
/**
 * If the tar file has more than one entry, the setting this option to true, allows working with the splitter EIP, to split the data using an iterator in a streaming mode.
 */
export type UsingIterator1 = boolean;
/**
 * Serialize and deserialize messages using Apache Thrift binary data format.
 */
export type Thrift =
    | string
    | {
    contentTypeFormat?: ContentTypeFormat1;
    contentTypeHeader?: ContentTypeHeader7;
    id?: Id34;
    instanceClass?: InstanceClass1;
};
/**
 * Defines a content type format in which thrift message will be serialized/deserialized from(to) the Java been. The format can either be native or json for either native binary thrift, json or simple json fields representation. The default value is binary.
 */
export type ContentTypeFormat1 = "binary" | "json" | "sjson";
/**
 * Whether the data format should set the Content-Type header with the type from the data format. For example application/xml for data formats marshalling to XML, or application/json for data formats marshalling to JSON
 */
export type ContentTypeHeader7 = boolean;
/**
 * The id of this node
 */
export type Id34 = string;
/**
 * Name of class to use when unmarshalling
 */
export type InstanceClass1 = string;
/**
 * What data type to unmarshal as, can either be org.w3c.dom.Node or java.lang.String. Is by default org.w3c.dom.Node
 */
export type DataObjectType = "org.w3c.dom.Node" | "java.lang.String";
/**
 * The id of this node
 */
export type Id35 = string;
/**
 * When returning a String, do we omit the XML declaration in the top.
 */
export type OmitXmlDeclaration = boolean;
/**
 * Whether the unmarshalling should produce maps for the lines values instead of lists. It requires to have header (either defined or collected). The default value is false
 */
export type AsMap = boolean;
/**
 * The comment symbol. The default value is #
 */
export type Comment = string;
/**
 * The delimiter of values
 */
export type Delimiter2 = string;
/**
 * The String representation of an empty value.
 */
export type EmptyValue = string;
/**
 * Whether or not the header must be read in the first line of the test document. The default value is false
 */
export type HeaderExtractionEnabled = boolean;
/**
 * Whether or not the headers are disabled. When defined, this option explicitly sets the headers as null which indicates that there is no header. The default value is false
 */
export type HeadersDisabled = boolean;
/**
 * The id of this node
 */
export type Id36 = string;
/**
 * Whether or not the leading white spaces must be ignored. The default value is true
 */
export type IgnoreLeadingWhitespaces = boolean;
/**
 * Whether or not the trailing white spaces must be ignored. The default value is true
 */
export type IgnoreTrailingWhitespaces = boolean;
/**
 * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must be read at once. The default value is false
 */
export type LazyLoad2 = boolean;
/**
 * The line separator of the files. The default value is to use the JVM platform line separator
 */
export type LineSeparator1 = string;
/**
 * The normalized line separator of the files. The default value is a new line character.
 */
export type NormalizedLineSeparator = string;
/**
 * The string representation of a null value. The default value is null
 */
export type NullValue = string;
/**
 * The maximum number of record to read.
 */
export type NumberOfRecordsToRead = number;
/**
 * The quote symbol.
 */
export type Quote1 = string;
/**
 * Whether or not all values must be quoted when writing them.
 */
export type QuoteAllFields = boolean;
/**
 * The quote escape symbol
 */
export type QuoteEscape = string;
/**
 * Whether or not the empty lines must be ignored. The default value is true
 */
export type SkipEmptyLines = boolean;
/**
 * Header length
 */
export type Length = string;
/**
 * Header name
 */
export type Name1 = string;
/**
 * Whether the unmarshalling should produce maps for the lines values instead of lists. It requires to have header (either defined or collected). The default value is false
 */
export type AsMap1 = boolean;
/**
 * The comment symbol. The default value is #
 */
export type Comment1 = string;
/**
 * The String representation of an empty value.
 */
export type EmptyValue1 = string;
/**
 * Whether or not the header must be read in the first line of the test document. The default value is false
 */
export type HeaderExtractionEnabled1 = boolean;
/**
 * Whether or not the headers are disabled. When defined, this option explicitly sets the headers as null which indicates that there is no header. The default value is false
 */
export type HeadersDisabled1 = boolean;
/**
 * The id of this node
 */
export type Id37 = string;
/**
 * Whether or not the leading white spaces must be ignored. The default value is true
 */
export type IgnoreLeadingWhitespaces1 = boolean;
/**
 * Whether or not the trailing white spaces must be ignored. The default value is true
 */
export type IgnoreTrailingWhitespaces1 = boolean;
/**
 * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must be read at once. The default value is false
 */
export type LazyLoad3 = boolean;
/**
 * The line separator of the files. The default value is to use the JVM platform line separator
 */
export type LineSeparator2 = string;
/**
 * The normalized line separator of the files. The default value is a new line character.
 */
export type NormalizedLineSeparator1 = string;
/**
 * The string representation of a null value. The default value is null
 */
export type NullValue1 = string;
/**
 * The maximum number of record to read.
 */
export type NumberOfRecordsToRead1 = number;
/**
 * The padding character. The default value is a space
 */
export type Padding = string;
/**
 * Whether or not the record ends on new line. The default value is false
 */
export type RecordEndsOnNewline = boolean;
/**
 * Whether or not the empty lines must be ignored. The default value is true
 */
export type SkipEmptyLines1 = boolean;
/**
 * Whether or not the trailing characters until new line must be ignored. The default value is false
 */
export type SkipTrailingCharsUntilNewline = boolean;
/**
 * Whether the unmarshalling should produce maps for the lines values instead of lists. It requires to have header (either defined or collected). The default value is false
 */
export type AsMap2 = boolean;
/**
 * The comment symbol. The default value is #
 */
export type Comment2 = string;
/**
 * The String representation of an empty value.
 */
export type EmptyValue2 = string;
/**
 * The escape character.
 */
export type EscapeChar = string;
/**
 * Whether or not the header must be read in the first line of the test document. The default value is false
 */
export type HeaderExtractionEnabled2 = boolean;
/**
 * Whether or not the headers are disabled. When defined, this option explicitly sets the headers as null which indicates that there is no header. The default value is false
 */
export type HeadersDisabled2 = boolean;
/**
 * The id of this node
 */
export type Id38 = string;
/**
 * Whether or not the leading white spaces must be ignored. The default value is true
 */
export type IgnoreLeadingWhitespaces2 = boolean;
/**
 * Whether or not the trailing white spaces must be ignored. The default value is true
 */
export type IgnoreTrailingWhitespaces2 = boolean;
/**
 * Whether the unmarshalling should produce an iterator that reads the lines on the fly or if all the lines must be read at once. The default value is false
 */
export type LazyLoad4 = boolean;
/**
 * The line separator of the files. The default value is to use the JVM platform line separator
 */
export type LineSeparator3 = string;
/**
 * The normalized line separator of the files. The default value is a new line character.
 */
export type NormalizedLineSeparator2 = string;
/**
 * The string representation of a null value. The default value is null
 */
export type NullValue2 = string;
/**
 * The maximum number of record to read.
 */
export type NumberOfRecordsToRead2 = number;
/**
 * Whether or not the empty lines must be ignored. The default value is true
 */
export type SkipEmptyLines2 = boolean;
/**
 * Whether to add the public key used to encrypt the session key as a KeyValue in the EncryptedKey structure or not.
 */
export type AddKeyValueForEncryptedKey = boolean;
/**
 * The digest algorithm to use with the RSA OAEP algorithm. The available choices are: XMLCipher.SHA1 XMLCipher.SHA256 XMLCipher.SHA512 The default value is XMLCipher.SHA1
 */
export type DigestAlgorithm = "SHA1" | "SHA256" | "SHA512";
/**
 * The id of this node
 */
export type Id39 = string;
/**
 * The cipher algorithm to be used for encryption/decryption of the asymmetric key. The available choices are: XMLCipher.RSA_v1dot5 XMLCipher.RSA_OAEP XMLCipher.RSA_OAEP_11 The default value is XMLCipher.RSA_OAEP
 */
export type KeyCipherAlgorithm = "RSA_v1dot5" | "RSA_OAEP" | "RSA_OAEP_11";
/**
 * Refers to a KeyStore instance to lookup in the registry, which is used for configuration options for creating and loading a KeyStore instance that represents the sender's trustStore or recipient's keyStore.
 */
export type KeyOrTrustStoreParametersRef = string;
/**
 * The password to be used for retrieving the private key from the KeyStore. This key is used for asymmetric decryption.
 */
export type KeyPassword = string;
/**
 * The MGF Algorithm to use with the RSA OAEP algorithm. The available choices are: EncryptionConstants.MGF1_SHA1 EncryptionConstants.MGF1_SHA256 EncryptionConstants.MGF1_SHA512 The default value is EncryptionConstants.MGF1_SHA1
 */
export type MgfAlgorithm = "MGF1_SHA1" | "MGF1_SHA256" | "MGF1_SHA512";
/**
 * A String used as passPhrase to encrypt/decrypt content. The passPhrase has to be provided. The passPhrase needs to be put together in conjunction with the appropriate encryption algorithm. For example using TRIPLEDES the passPhase can be a Only another 24 Byte key
 */
export type PassPhrase = string;
/**
 * A byte used as passPhrase to encrypt/decrypt content. The passPhrase has to be provided. The passPhrase needs to be put together in conjunction with the appropriate encryption algorithm. For example using TRIPLEDES the passPhase can be a Only another 24 Byte key
 */
export type PassPhraseByte = string;
/**
 * The key alias to be used when retrieving the recipient's public or private key from a KeyStore when performing asymmetric key encryption or decryption.
 */
export type RecipientKeyAlias = string;
/**
 * The XPath reference to the XML Element selected for encryption/decryption. If no tag is specified, the entire payload is encrypted/decrypted.
 */
export type SecureTag = string;
/**
 * A boolean value to specify whether the XML Element is to be encrypted or the contents of the XML Element. false = Element Level. true = Element Content Level.
 */
export type SecureTagContents = boolean;
/**
 * The cipher algorithm to be used for encryption/decryption of the XML message content. The available choices are: XMLCipher.TRIPLEDES XMLCipher.AES_128 XMLCipher.AES_128_GCM XMLCipher.AES_192 XMLCipher.AES_192_GCM XMLCipher.AES_256 XMLCipher.AES_256_GCM XMLCipher.SEED_128 XMLCipher.CAMELLIA_128 XMLCipher.CAMELLIA_192 XMLCipher.CAMELLIA_256 The default value is XMLCipher.AES_256_GCM
 */
export type XmlCipherAlgorithm =
    | "TRIPLEDES"
    | "AES_128"
    | "AES_128_GCM"
    | "AES_192"
    | "AES_192_GCM"
    | "AES_256"
    | "AES_256_GCM"
    | "SEED_128"
    | "CAMELLIA_128"
    | "CAMELLIA_192"
    | "CAMELLIA_256";
/**
 * Allow any class to be un-marshaled
 */
export type AllowAnyType = boolean;
/**
 * Set whether recursive keys are allowed.
 */
export type AllowRecursiveKeys = boolean;
/**
 * BaseConstructor to construct incoming documents.
 */
export type Constructor = string;
/**
 * DumperOptions to configure outgoing objects.
 */
export type DumperOptions = string;
/**
 * The id of this node
 */
export type Id40 = string;
/**
 * Which yaml library to use. By default it is SnakeYAML
 */
export type Library3 = "SnakeYAML";
/**
 * Set the maximum amount of aliases allowed for collections.
 */
export type MaxAliasesForCollections = number & string;
/**
 * Force the emitter to produce a pretty YAML document when using the flow style.
 */
export type PrettyFlow = boolean;
/**
 * Representer to emit outgoing objects.
 */
export type Representer = string;
/**
 * Resolver to detect implicit type
 */
export type Resolver = string;
/**
 * Whether to filter by class type or regular expression
 */
export type Type2 = string;
/**
 * Value of type such as class name or regular expression
 */
export type Value = string;
/**
 * Set the types SnakeYAML is allowed to un-marshall
 */
export type TypeFilter = YAMLTypeFilter[];
/**
 * Class name of the java type to use when unmarshalling
 */
export type UnmarshalType8 = string;
/**
 * Use ApplicationContextClassLoader as custom ClassLoader
 */
export type UseApplicationContextClassLoader = boolean;
/**
 * To specify a specific compression between 0-9. -1 is default compression, 0 is no compression, and 9 is the best compression.
 */
export type CompressionLevel = "-1" | "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9";
/**
 * The id of this node
 */
export type Id41 = string;
/**
 * If the zip file has more than one entry, setting this option to true, allows to get the iterator even if the directory is empty
 */
export type AllowEmptyDirectory1 = boolean;
/**
 * The id of this node
 */
export type Id42 = string;
/**
 * Set the maximum decompressed size of a zip file (in bytes). The default value if not specified corresponds to 1 gigabyte. An IOException will be thrown if the decompressed size exceeds this amount. Set to -1 to disable setting a maximum decompressed size.
 */
export type MaxDecompressedSize1 = number & string;
/**
 * If the file name contains path elements, setting this option to true, allows the path to be maintained in the zip file.
 */
export type PreservePathElements1 = boolean;
/**
 * If the zip file has more than one entry, the setting this option to true, allows working with the splitter EIP, to split the data using an iterator in a streaming mode.
 */
export type UsingIterator2 = boolean;
export type DataFormatsDefinitionDeserializer = DataFormats[];
export type ErrorHandlerDeserializer =
    | {
    deadLetterChannel: DeadLetterChannel;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    defaultErrorHandler: DefaultErrorHandler;
    [k: string]: unknown;
}
    | {
    jtaTransactionErrorHandler: JtaTransactionErrorHandler;
    [k: string]: unknown;
}
    | {
    noErrorHandler: NoErrorHandler;
    [k: string]: unknown;
}
    | {
    refErrorHandler: RefErrorHandler;
    [k: string]: unknown;
}
    | {
    springTransactionErrorHandler: SpringTransactionErrorHandler;
    [k: string]: unknown;
};
/**
 * Whether the dead letter channel should handle (and ignore) any new exception that may been thrown during sending the message to the dead letter endpoint. The default value is true which means any such kind of exception is handled and ignored. Set this to false to let the exception be propagated back on the org.apache.camel.Exchange . This can be used in situations where you use transactions, and want to use Ua's dead letter channel to deal with exceptions during routing, but if the dead letter channel itself fails because of a new exception being thrown, then by setting this to false the new exceptions is propagated back and set on the org.apache.camel.Exchange , which allows the transaction to detect the exception, and rollback.
 */
export type DeadLetterHandleNewException = boolean;
/**
 * The dead letter endpoint uri for the Dead Letter error handler.
 */
export type DeadLetterUri = string;
/**
 * Sets a reference to a thread pool to be used by the error handler
 */
export type ExecutorServiceRef = string;
/**
 * The id of this node
 */
export type Id43 = string;
/**
 * Logging level to use by error handler
 */
export type Level = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Name of the logger to use by the error handler
 */
export type LogName = string;
/**
 * References to a logger to use as logger for the error handler
 */
export type LoggerRef = string;
/**
 * Sets a reference to a processor that should be processed just after an exception occurred. Can be used to perform custom logging about the occurred exception at the exact time it happened. Important: Any exception thrown from this processor will be ignored.
 */
export type OnExceptionOccurredRef = string;
/**
 * Sets a reference to a processor to prepare the org.apache.camel.Exchange before handled by the failure processor / dead letter channel. This allows for example to enrich the message before sending to a dead letter queue.
 */
export type OnPrepareFailureRef = string;
/**
 * Sets a reference to a processor that should be processed before a redelivery attempt. Can be used to change the org.apache.camel.Exchange before its being redelivered.
 */
export type OnRedeliveryRef = string;
/**
 * Controls whether to allow redelivery while stopping/shutting down a workflow that uses error handling.
 */
export type AllowRedeliveryWhileStopping = boolean;
/**
 * Allow asynchronous delayed redelivery. The workflow, in particular the consumer's component, must support the Asynchronous Routing Engine (e.g. seda).
 */
export type AsyncDelayedRedelivery = boolean;
/**
 * Sets the back off multiplier
 */
export type BackOffMultiplier = number & string;
/**
 * Sets the collision avoidance factor
 */
export type CollisionAvoidanceFactor = number & string;
/**
 * Sets the delay pattern with delay intervals.
 */
export type DelayPattern = string;
/**
 * Disables redelivery (same as setting maximum redeliveries to 0)
 */
export type DisableRedelivery = boolean;
/**
 * Sets the reference of the instance of org.apache.camel.spi.ExchangeFormatter to generate the log message from exchange.
 */
export type ExchangeFormatterRef = string;
/**
 * The id of this node
 */
export type Id44 = string;
/**
 * Sets whether continued exceptions should be logged or not. Can be used to include or reduce verbose.
 */
export type LogContinued = boolean;
/**
 * Sets whether exhausted exceptions should be logged or not. Can be used to include or reduce verbose.
 */
export type LogExhausted = boolean;
/**
 * Sets whether exhausted message body should be logged including message history or not (supports property placeholders). Can be used to include or reduce verbose. Requires logExhaustedMessageHistory to be enabled.
 */
export type LogExhaustedMessageBody = boolean;
/**
 * Sets whether exhausted exceptions should be logged including message history or not (supports property placeholders). Can be used to include or reduce verbose.
 */
export type LogExhaustedMessageHistory = boolean;
/**
 * Sets whether handled exceptions should be logged or not. Can be used to include or reduce verbose.
 */
export type LogHandled = boolean;
/**
 * Sets whether new exceptions should be logged or not. Can be used to include or reduce verbose. A new exception is an exception that was thrown while handling a previous exception.
 */
export type LogNewException = boolean;
/**
 * Sets whether retry attempts should be logged or not. Can be used to include or reduce verbose.
 */
export type LogRetryAttempted = boolean;
/**
 * Sets whether stack traces should be logged when an retry attempt failed. Can be used to include or reduce verbose.
 */
export type LogRetryStackTrace = boolean;
/**
 * Sets whether stack traces should be logged. Can be used to include or reduce verbose.
 */
export type LogStackTrace = boolean;
/**
 * Sets the maximum redeliveries x = redeliver at most x times 0 = no redeliveries -1 = redeliver forever
 */
export type MaximumRedeliveries = number;
/**
 * Sets the maximum delay between redelivery
 */
export type MaximumRedeliveryDelay = string;
/**
 * Sets the initial redelivery delay
 */
export type RedeliveryDelay = string;
/**
 * Sets the logging level to use when retries have been exhausted
 */
export type RetriesExhaustedLogLevel = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Sets the interval to use for logging retry attempts
 */
export type RetryAttemptedLogInterval = number & string;
/**
 * Sets the logging level to use for logging retry attempts
 */
export type RetryAttemptedLogLevel = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Turn on collision avoidance.
 */
export type UseCollisionAvoidance = boolean;
/**
 * Turn on exponential back off
 */
export type UseExponentialBackOff = boolean;
/**
 * Sets a reference to a RedeliveryPolicy to be used for redelivery settings.
 */
export type RedeliveryPolicyRef = string;
/**
 * Sets a retry while predicate. Will continue retrying until the predicate evaluates to false.
 */
export type RetryWhileRef = string;
/**
 * Will use the original input org.apache.camel.Message body (original body only) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalBody = boolean;
/**
 * Will use the original input org.apache.camel.Message (original body and headers) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalMessage = boolean;
/**
 * Sets a reference to a thread pool to be used by the error handler
 */
export type ExecutorServiceRef1 = string;
/**
 * The id of this node
 */
export type Id45 = string;
/**
 * Logging level to use by error handler
 */
export type Level1 = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Name of the logger to use by the error handler
 */
export type LogName1 = string;
/**
 * References to a logger to use as logger for the error handler
 */
export type LoggerRef1 = string;
/**
 * Sets a reference to a processor that should be processed just after an exception occurred. Can be used to perform custom logging about the occurred exception at the exact time it happened. Important: Any exception thrown from this processor will be ignored.
 */
export type OnExceptionOccurredRef1 = string;
/**
 * Sets a reference to a processor to prepare the org.apache.camel.Exchange before handled by the failure processor / dead letter channel. This allows for example to enrich the message before sending to a dead letter queue.
 */
export type OnPrepareFailureRef1 = string;
/**
 * Sets a reference to a processor that should be processed before a redelivery attempt. Can be used to change the org.apache.camel.Exchange before its being redelivered.
 */
export type OnRedeliveryRef1 = string;
/**
 * Sets a reference to a RedeliveryPolicy to be used for redelivery settings.
 */
export type RedeliveryPolicyRef1 = string;
/**
 * Sets a retry while predicate. Will continue retrying until the predicate evaluates to false.
 */
export type RetryWhileRef1 = string;
/**
 * Will use the original input org.apache.camel.Message body (original body only) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalBody1 = boolean;
/**
 * Will use the original input org.apache.camel.Message (original body and headers) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalMessage1 = boolean;
/**
 * Sets a reference to a thread pool to be used by the error handler
 */
export type ExecutorServiceRef2 = string;
/**
 * The id of this node
 */
export type Id46 = string;
/**
 * Logging level to use by error handler
 */
export type Level2 = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Name of the logger to use by the error handler
 */
export type LogName2 = string;
/**
 * References to a logger to use as logger for the error handler
 */
export type LoggerRef2 = string;
/**
 * Sets a reference to a processor that should be processed just after an exception occurred. Can be used to perform custom logging about the occurred exception at the exact time it happened. Important: Any exception thrown from this processor will be ignored.
 */
export type OnExceptionOccurredRef2 = string;
/**
 * Sets a reference to a processor to prepare the org.apache.camel.Exchange before handled by the failure processor / dead letter channel. This allows for example to enrich the message before sending to a dead letter queue.
 */
export type OnPrepareFailureRef2 = string;
/**
 * Sets a reference to a processor that should be processed before a redelivery attempt. Can be used to change the org.apache.camel.Exchange before its being redelivered.
 */
export type OnRedeliveryRef2 = string;
/**
 * Sets a reference to a RedeliveryPolicy to be used for redelivery settings.
 */
export type RedeliveryPolicyRef2 = string;
/**
 * Sets a retry while predicate. Will continue retrying until the predicate evaluates to false.
 */
export type RetryWhileRef2 = string;
/**
 * Sets the logging level to use for logging transactional rollback. This option is default WARN.
 */
export type RollbackLoggingLevel = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * The transacted policy to use that is configured for either Spring or JTA based transactions. If no policy has been configured then Ua will attempt to auto-discover.
 */
export type TransactedPolicyRef = string;
/**
 * Will use the original input org.apache.camel.Message body (original body only) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalBody2 = boolean;
/**
 * Will use the original input org.apache.camel.Message (original body and headers) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalMessage2 = boolean;
/**
 * The id of this node
 */
export type Id47 = string;
/**
 * References to an existing or custom error handler.
 */
export type RefErrorHandler =
    | string
    | {
    id?: Id48;
    ref?: Ref1;
};
/**
 * The id of this node
 */
export type Id48 = string;
/**
 * References to an existing or custom error handler.
 */
export type Ref1 = string;
/**
 * Sets a reference to a thread pool to be used by the error handler
 */
export type ExecutorServiceRef3 = string;
/**
 * The id of this node
 */
export type Id49 = string;
/**
 * Logging level to use by error handler
 */
export type Level3 = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Name of the logger to use by the error handler
 */
export type LogName3 = string;
/**
 * References to a logger to use as logger for the error handler
 */
export type LoggerRef3 = string;
/**
 * Sets a reference to a processor that should be processed just after an exception occurred. Can be used to perform custom logging about the occurred exception at the exact time it happened. Important: Any exception thrown from this processor will be ignored.
 */
export type OnExceptionOccurredRef3 = string;
/**
 * Sets a reference to a processor to prepare the org.apache.camel.Exchange before handled by the failure processor / dead letter channel. This allows for example to enrich the message before sending to a dead letter queue.
 */
export type OnPrepareFailureRef3 = string;
/**
 * Sets a reference to a processor that should be processed before a redelivery attempt. Can be used to change the org.apache.camel.Exchange before its being redelivered.
 */
export type OnRedeliveryRef3 = string;
/**
 * Sets a reference to a RedeliveryPolicy to be used for redelivery settings.
 */
export type RedeliveryPolicyRef3 = string;
/**
 * Sets a retry while predicate. Will continue retrying until the predicate evaluates to false.
 */
export type RetryWhileRef3 = string;
/**
 * Sets the logging level to use for logging transactional rollback. This option is default WARN.
 */
export type RollbackLoggingLevel1 = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * The transacted policy to use that is configured for either Spring or JTA based transactions. If no policy has been configured then Ua will attempt to auto-discover.
 */
export type TransactedPolicyRef1 = string;
/**
 * Will use the original input org.apache.camel.Message body (original body only) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalBody3 = boolean;
/**
 * Will use the original input org.apache.camel.Message (original body and headers) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the splitted message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalMessage3 = boolean;
/**
 * To use a org.apache.camel.processor.aggregate.AggregateController to allow external sources to control this aggregator.
 */
export type AggregateController = string;
/**
 * The AggregationRepository to use. Sets the custom aggregate repository to use. Will by default use org.apache.camel.processor.aggregate.MemoryAggregationRepository
 */
export type AggregationRepository = string;
/**
 * The AggregationStrategy to use. For example to lookup a bean with the name foo, the value is simply just #bean:foo. Configuring an AggregationStrategy is required, and is used to merge the incoming Exchange with the existing already merged exchanges. At first call the oldExchange parameter is null. On subsequent invocations the oldExchange contains the merged exchanges and newExchange is of course the new incoming Exchange.
 */
export type AggregationStrategy = string;
/**
 * If this option is false then the aggregate method is not used for the very first aggregation. If this option is true then null values is used as the oldExchange (at the very first aggregation), when using beans as the AggregationStrategy.
 */
export type AggregationStrategyMethodAllowNull = boolean;
/**
 * This option can be used to explicit declare the method name to use, when using beans as the AggregationStrategy.
 */
export type AggregationStrategyMethodName = string;
/**
 * Closes a correlation key when its complete. Any late received exchanges which has a correlation key that has been closed, it will be defined and a ClosedCorrelationKeyException is thrown.
 */
export type CloseCorrelationKeyOnCompletion = number;
/**
 * Indicates to wait to complete all current and partial (pending) aggregated exchanges when the context is stopped. This also means that we will wait for all pending exchanges which are stored in the aggregation repository to complete so the repository is empty before we can stop. You may want to enable this when using the memory based aggregation repository that is memory based only, and do not store data on disk. When this option is enabled, then the aggregator is waiting to complete all those exchanges before its stopped, when stopping UaContext or the workflow using it.
 */
export type CompleteAllOnStop = boolean;
/**
 * Enables the batch completion mode where we aggregate from a org.apache.camel.BatchConsumer and aggregate the total number of exchanges the org.apache.camel.BatchConsumer has reported as total by checking the exchange property org.apache.camel.Exchange#BATCH_COMPLETE when its complete. This option cannot be used together with discardOnAggregationFailure.
 */
export type CompletionFromBatchConsumer = boolean;
/**
 * A repeating period in millis by which the aggregator will complete all current aggregated exchanges. Ua has a background task which is triggered every period. You cannot use this option together with completionTimeout, only one of them can be used.
 */
export type CompletionInterval = string;
/**
 * Enables completion on all previous groups when a new incoming correlation group. This can for example be used to complete groups with same correlation keys when they are in consecutive order. Notice when this is enabled then only 1 correlation group can be in progress as when a new correlation group starts, then the previous groups is forced completed.
 */
export type CompletionOnNewCorrelationGroup = boolean;
/**
 * A Predicate to indicate when an aggregated exchange is complete. If this is not specified and the AggregationStrategy object implements Predicate, the aggregationStrategy object will be used as the completionPredicate.
 */
export type CompletionPredicate =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * A fixed value set only once during the workflow startup.
 */
export type Constant =
    | string
    | {
    expression?: Expression;
    id?: Id50;
    resultType?: ResultType;
    trim?: Trim1;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression = string;
/**
 * Sets the id of this node
 */
export type Id50 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim1 = boolean;
/**
 * Evaluate a compiled simple expression.
 */
export type CSimple =
    | string
    | {
    expression?: Expression1;
    id?: Id51;
    resultType?: ResultType1;
    trim?: Trim2;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression1 = string;
/**
 * Sets the id of this node
 */
export type Id51 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType1 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim2 = boolean;
/**
 * To use DataSonnet scripts for message transformations.
 */
export type DataSonnet =
    | string
    | {
    bodyMediaType?: BodyMediaType;
    expression?: Expression2;
    id?: Id52;
    outputMediaType?: OutputMediaType;
    resultType?: ResultType2;
    source?: Source;
    trim?: Trim3;
};
/**
 * The String representation of the message's body MediaType
 */
export type BodyMediaType = string;
/**
 * The expression value in your chosen language syntax
 */
export type Expression2 = string;
/**
 * Sets the id of this node
 */
export type Id52 = string;
/**
 * The String representation of the MediaType to output
 */
export type OutputMediaType = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType2 = string;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim3 = boolean;
/**
 * Gets a property from the Exchange.
 */
export type ExchangeProperty =
    | string
    | {
    expression?: Expression3;
    id?: Id53;
    trim?: Trim4;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression3 = string;
/**
 * Sets the id of this node
 */
export type Id53 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim4 = boolean;
/**
 * Evaluates a Groovy script.
 */
export type Groovy =
    | string
    | {
    expression?: Expression4;
    id?: Id54;
    resultType?: ResultType3;
    trim?: Trim5;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression4 = string;
/**
 * Sets the id of this node
 */
export type Id54 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType3 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim5 = boolean;
/**
 * Gets a header from the Exchange.
 */
export type Header1 =
    | string
    | {
    expression?: Expression5;
    id?: Id55;
    trim?: Trim6;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression5 = string;
/**
 * Sets the id of this node
 */
export type Id55 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim6 = boolean;
/**
 * Get the value of a HL7 message field specified by terse location specification syntax.
 */
export type HL7Terser =
    | string
    | {
    expression?: Expression6;
    id?: Id56;
    resultType?: ResultType4;
    source?: Source1;
    trim?: Trim7;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression6 = string;
/**
 * Sets the id of this node
 */
export type Id56 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType4 = string;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source1 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim7 = boolean;
/**
 * Evaluates a Java (Java compiled once at runtime) expression.
 */
export type Java =
    | string
    | {
    expression?: Expression7;
    id?: Id57;
    preCompile?: PreCompile;
    resultType?: ResultType5;
    singleQuotes?: SingleQuotes;
    trim?: Trim8;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression7 = string;
/**
 * Sets the id of this node
 */
export type Id57 = string;
/**
 * Whether the expression should be pre compiled once during initialization phase. If this is turned off, then the expression is reloaded and compiled on each evaluation.
 */
export type PreCompile = boolean;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType5 = string;
/**
 * Whether single quotes can be used as replacement for double quotes. This is convenient when you need to work with strings inside strings.
 */
export type SingleQuotes = boolean;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim8 = boolean;
/**
 * Evaluates a jOOR (Java compiled once at runtime) expression.
 */
export type JOOR =
    | string
    | {
    expression?: Expression8;
    id?: Id58;
    preCompile?: PreCompile1;
    resultType?: ResultType6;
    singleQuotes?: SingleQuotes1;
    trim?: Trim9;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression8 = string;
/**
 * Sets the id of this node
 */
export type Id58 = string;
/**
 * Whether the expression should be pre compiled once during initialization phase. If this is turned off, then the expression is reloaded and compiled on each evaluation.
 */
export type PreCompile1 = boolean;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType6 = string;
/**
 * Whether single quotes can be used as replacement for double quotes. This is convenient when you need to work with strings inside strings.
 */
export type SingleQuotes1 = boolean;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim9 = boolean;
/**
 * Evaluates a JQ expression against a JSON message body.
 */
export type JQ =
    | string
    | {
    expression?: Expression9;
    id?: Id59;
    resultType?: ResultType7;
    source?: Source2;
    trim?: Trim10;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression9 = string;
/**
 * Sets the id of this node
 */
export type Id59 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType7 = string;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source2 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim10 = boolean;
/**
 * Evaluates a JavaScript expression.
 */
export type JavaScript =
    | string
    | {
    expression?: Expression10;
    id?: Id60;
    resultType?: ResultType8;
    trim?: Trim11;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression10 = string;
/**
 * Sets the id of this node
 */
export type Id60 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType8 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim11 = boolean;
/**
 * Evaluates a JSONPath expression against a JSON message body.
 */
export type JSONPath =
    | string
    | {
    allowEasyPredicate?: AllowEasyPredicate;
    allowSimple?: AllowSimple;
    expression?: Expression11;
    id?: Id61;
    option?: Option;
    resultType?: ResultType9;
    source?: Source3;
    suppressExceptions?: SuppressExceptions;
    trim?: Trim12;
    unpackArray?: UnpackArray;
    writeAsString?: WriteAsString;
};
/**
 * Whether to allow using the easy predicate parser to pre-parse predicates.
 */
export type AllowEasyPredicate = boolean;
/**
 * Whether to allow in inlined Simple exceptions in the JSONPath expression
 */
export type AllowSimple = boolean;
/**
 * The expression value in your chosen language syntax
 */
export type Expression11 = string;
/**
 * Sets the id of this node
 */
export type Id61 = string;
/**
 * To configure additional options on JSONPath. Multiple values can be separated by comma.
 */
export type Option =
    | "DEFAULT_PATH_LEAF_TO_NULL"
    | "ALWAYS_RETURN_LIST"
    | "AS_PATH_LIST"
    | "SUPPRESS_EXCEPTIONS"
    | "REQUIRE_PROPERTIES";
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType9 = string;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source3 = string;
/**
 * Whether to suppress exceptions such as PathNotFoundException.
 */
export type SuppressExceptions = boolean;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim12 = boolean;
/**
 * Whether to unpack a single element json-array into an object.
 */
export type UnpackArray = boolean;
/**
 * Whether to write the output of each row/element as a JSON String value instead of a Map/POJO value.
 */
export type WriteAsString = boolean;
/**
 * The expression value in your chosen language syntax
 */
export type Expression12 = string;
/**
 * Sets the id of this node
 */
export type Id62 = string;
/**
 * The name of the language to use
 */
export type Language1 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim13 = boolean;
/**
 * Calls a Java bean method.
 */
export type BeanMethod =
    | string
    | {
    beanType?: BeanType;
    id?: Id63;
    method?: Method;
    ref?: Ref2;
    resultType?: ResultType10;
    scope?: Scope;
    trim?: Trim14;
    validate?: Validate1;
};
/**
 * Class name (fully qualified) of the bean to use Will lookup in registry and if there is a single instance of the same type, then the existing bean is used, otherwise a new bean is created (requires a default no-arg constructor).
 */
export type BeanType = string;
/**
 * Sets the id of this node
 */
export type Id63 = string;
/**
 * Name of method to call
 */
export type Method = string;
/**
 * Reference to an existing bean (bean id) to lookup in the registry
 */
export type Ref2 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType10 = string;
/**
 * Scope of bean. When using singleton scope (default) the bean is created or looked up only once and reused for the lifetime of the endpoint. The bean should be thread-safe in case concurrent threads is calling the bean at the same time. When using request scope the bean is created or looked up once per request (exchange). This can be used if you want to store state on a bean while processing a request and you want to call the same bean instance multiple times while processing the request. The bean does not have to be thread-safe as the instance is only called from the same request. When using prototype scope, then the bean will be looked up or created per call. However in case of lookup then this is delegated to the bean registry such as Spring or CDI (if in use), which depends on their configuration can act as either singleton or prototype scope. So when using prototype scope then this depends on the bean registry implementation.
 */
export type Scope = "Singleton" | "Request" | "Prototype";
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim14 = boolean;
/**
 * Whether to validate the bean has the configured method.
 */
export type Validate1 = boolean;
/**
 * Evaluates a MVEL template.
 */
export type MVEL =
    | string
    | {
    expression?: Expression13;
    id?: Id64;
    resultType?: ResultType11;
    trim?: Trim15;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression13 = string;
/**
 * Sets the id of this node
 */
export type Id64 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType11 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim15 = boolean;
/**
 * Evaluates an OGNL expression (Apache Commons OGNL).
 */
export type OGNL =
    | string
    | {
    expression?: Expression14;
    id?: Id65;
    resultType?: ResultType12;
    trim?: Trim16;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression14 = string;
/**
 * Sets the id of this node
 */
export type Id65 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType12 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim16 = boolean;
/**
 * Evaluates a Python expression.
 */
export type Python =
    | string
    | {
    expression?: Expression15;
    id?: Id66;
    resultType?: ResultType13;
    trim?: Trim17;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression15 = string;
/**
 * Sets the id of this node
 */
export type Id66 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType13 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim17 = boolean;
/**
 * Uses an existing expression from the registry.
 */
export type Ref3 =
    | string
    | {
    expression?: Expression16;
    id?: Id67;
    resultType?: ResultType14;
    trim?: Trim18;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression16 = string;
/**
 * Sets the id of this node
 */
export type Id67 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType14 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim18 = boolean;
/**
 * Evaluates a Ua simple expression.
 */
export type Simple =
    | string
    | {
    expression?: Expression17;
    id?: Id68;
    resultType?: ResultType15;
    trim?: Trim19;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression17 = string;
/**
 * Sets the id of this node
 */
export type Id68 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType15 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim19 = boolean;
/**
 * Evaluates a Spring expression (SpEL).
 */
export type SpEL =
    | string
    | {
    expression?: Expression18;
    id?: Id69;
    resultType?: ResultType16;
    trim?: Trim20;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression18 = string;
/**
 * Sets the id of this node
 */
export type Id69 = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType16 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim20 = boolean;
/**
 * Tokenize text payloads using delimiter patterns.
 */
export type Tokenize =
    | string
    | {
    endToken?: EndToken;
    group?: Group;
    groupDelimiter?: GroupDelimiter;
    id?: Id70;
    includeTokens?: IncludeTokens;
    inheritNamespaceTagName?: InheritNamespaceTagName;
    regex?: Regex;
    resultType?: ResultType17;
    skipFirst?: SkipFirst;
    source?: Source4;
    token?: Token;
    trim?: Trim21;
    xml?: Xml;
};
/**
 * The end token to use as tokenizer if using start/end token pairs. You can use simple language as the token to support dynamic tokens.
 */
export type EndToken = string;
/**
 * To group N parts together, for example to split big files into chunks of 1000 lines. You can use simple language as the group to support dynamic group sizes.
 */
export type Group = string;
/**
 * Sets the delimiter to use when grouping. If this has not been set then token will be used as the delimiter.
 */
export type GroupDelimiter = string;
/**
 * Sets the id of this node
 */
export type Id70 = string;
/**
 * Whether to include the tokens in the parts when using pairs. When including tokens then the endToken property must also be configured (to use pair mode). The default value is false
 */
export type IncludeTokens = boolean;
/**
 * To inherit namespaces from a root/parent tag name when using XML You can use simple language as the tag name to support dynamic names.
 */
export type InheritNamespaceTagName = string;
/**
 * If the token is a regular expression pattern. The default value is false
 */
export type Regex = boolean;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType17 = string;
/**
 * To skip the very first element
 */
export type SkipFirst = boolean;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source4 = string;
/**
 * The (start) token to use as tokenizer, for example you can use the new line token. You can use simple language as the token to support dynamic tokens.
 */
export type Token = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim21 = boolean;
/**
 * Whether the input is XML messages. This option must be set to true if working with XML payloads.
 */
export type Xml = boolean;
/**
 * Gets a variable
 */
export type Variable =
    | string
    | {
    expression?: Expression19;
    id?: Id71;
    trim?: Trim22;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression19 = string;
/**
 * Sets the id of this node
 */
export type Id71 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim22 = boolean;
/**
 * Call a wasm (web assembly) function.
 */
export type Wasm =
    | string
    | {
    expression?: Expression20;
    id?: Id72;
    module?: Module;
    resultType?: ResultType18;
    trim?: Trim23;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression20 = string;
/**
 * Sets the id of this node
 */
export type Id72 = string;
/**
 * Set the module (the distributable, loadable, and executable unit of code in WebAssembly) resource that provides the expression function.
 */
export type Module = string;
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType18 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim23 = boolean;
/**
 * Evaluates an XPath expression against an XML payload.
 */
export type XPath =
    | string
    | {
    documentType?: DocumentType;
    expression?: Expression21;
    factoryRef?: FactoryRef;
    id?: Id73;
    logNamespaces?: LogNamespaces;
    namespace?: Namespace;
    objectModel?: ObjectModel;
    preCompile?: PreCompile2;
    resultQName?: ResultQName;
    resultType?: ResultType19;
    saxon?: Saxon;
    source?: Source5;
    threadSafety?: ThreadSafety;
    trim?: Trim24;
};
/**
 * Name of class for document type The default value is org.w3c.dom.Document
 */
export type DocumentType = string;
/**
 * The expression value in your chosen language syntax
 */
export type Expression21 = string;
/**
 * References to a custom XPathFactory to lookup in the registry
 */
export type FactoryRef = string;
/**
 * Sets the id of this node
 */
export type Id73 = string;
/**
 * Whether to log namespaces which can assist during troubleshooting
 */
export type LogNamespaces = boolean;
/**
 * The name of the property
 */
export type Key = string;
/**
 * The property value.
 */
export type Value1 = string;
/**
 * Injects the XML Namespaces of prefix - uri mappings
 */
export type Namespace = Property[];
/**
 * The XPath object model to use
 */
export type ObjectModel = string;
/**
 * Whether to enable pre-compiling the xpath expression during initialization phase. pre-compile is enabled by default. This can be used to turn off, for example in cases the compilation phase is desired at the starting phase, such as if the application is ahead of time compiled (for example with camel-quarkus) which would then load the xpath factory of the built operating system, and not a JVM runtime.
 */
export type PreCompile2 = boolean;
/**
 * Sets the output type supported by XPath.
 */
export type ResultQName = "NUMBER" | "STRING" | "BOOLEAN" | "NODESET" | "NODE";
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType19 = string;
/**
 * Whether to use Saxon.
 */
export type Saxon = boolean;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source5 = string;
/**
 * Whether to enable thread-safety for the returned result of the xpath expression. This applies to when using NODESET as the result type, and the returned set has multiple elements. In this situation there can be thread-safety issues if you process the NODESET concurrently such as from a Ua RowSplitter EIP in parallel processing mode. This option prevents concurrency issues by doing defensive copies of the nodes. It is recommended to turn this option on if you are using camel-saxon or Saxon in your application. Saxon has thread-safety issues which can be prevented by turning this option on.
 */
export type ThreadSafety = boolean;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim24 = boolean;
/**
 * Evaluates an XQuery expressions against an XML payload.
 */
export type XQuery =
    | string
    | {
    configurationRef?: ConfigurationRef;
    expression?: Expression22;
    id?: Id74;
    namespace?: Namespace1;
    resultType?: ResultType20;
    source?: Source6;
    trim?: Trim25;
};
/**
 * Reference to a saxon configuration instance in the registry to use for xquery (requires camel-saxon). This may be needed to add custom functions to a saxon configuration, so these custom functions can be used in xquery expressions.
 */
export type ConfigurationRef = string;
/**
 * The expression value in your chosen language syntax
 */
export type Expression22 = string;
/**
 * Sets the id of this node
 */
export type Id74 = string;
/**
 * Injects the XML Namespaces of prefix - uri mappings
 */
export type Namespace1 = Property[];
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType20 = string;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source6 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim25 = boolean;
/**
 * Tokenize XML payloads.
 */
export type XMLTokenize =
    | string
    | {
    expression?: Expression23;
    group?: Group1;
    id?: Id75;
    mode?: Mode;
    namespace?: Namespace2;
    resultType?: ResultType21;
    source?: Source7;
    trim?: Trim26;
};
/**
 * The expression value in your chosen language syntax
 */
export type Expression23 = string;
/**
 * To group N parts together
 */
export type Group1 = number;
/**
 * Sets the id of this node
 */
export type Id75 = string;
/**
 * The extraction mode. The available extraction modes are: i - injecting the contextual namespace bindings into the extracted token (default) w - wrapping the extracted token in its ancestor context u - unwrapping the extracted token to its child content t - extracting the text content of the specified element
 */
export type Mode = "i" | "w" | "u" | "t";
/**
 * Injects the XML Namespaces of prefix - uri mappings
 */
export type Namespace2 = Property[];
/**
 * Sets the class of the result type (type from output)
 */
export type ResultType21 = string;
/**
 * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the message body.
 */
export type Source7 = string;
/**
 * Whether to trim the value to remove leading and trailing whitespaces and line breaks
 */
export type Trim26 = boolean;
/**
 * Number of messages aggregated before the aggregation is complete. This option can be set as either a fixed value or using an Expression which allows you to evaluate a size dynamically - will use Integer as result. If both are set Ua will fallback to use the fixed value if the Expression result was null or 0.
 */
export type CompletionSize = number;
/**
 * Number of messages aggregated before the aggregation is complete. This option can be set as either a fixed value or using an Expression which allows you to evaluate a size dynamically - will use Integer as result. If both are set Ua will fallback to use the fixed value if the Expression result was null or 0.
 */
export type CompletionSizeExpression =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Time in millis that an aggregated exchange should be inactive before its complete (timeout). This option can be set as either a fixed value or using an Expression which allows you to evaluate a timeout dynamically - will use Long as result. If both are set Ua will fallback to use the fixed value if the Expression result was null or 0. You cannot use this option together with completionInterval, only one of the two can be used. By default the timeout checker runs every second, you can use the completionTimeoutCheckerInterval option to configure how frequently to run the checker. The timeout is an approximation and there is no guarantee that the a timeout is triggered exactly after the timeout value. It is not recommended to use very low timeout values or checker intervals.
 */
export type CompletionTimeout = string;
/**
 * Interval in millis that is used by the background task that checks for timeouts ( org.apache.camel.TimeoutMap ). By default the timeout checker runs every second. The timeout is an approximation and there is no guarantee that the a timeout is triggered exactly after the timeout value. It is not recommended to use very low timeout values or checker intervals.
 */
export type CompletionTimeoutCheckerInterval = string;
/**
 * Time in millis that an aggregated exchange should be inactive before its complete (timeout). This option can be set as either a fixed value or using an Expression which allows you to evaluate a timeout dynamically - will use Long as result. If both are set Ua will fallback to use the fixed value if the Expression result was null or 0. You cannot use this option together with completionInterval, only one of the two can be used. By default the timeout checker runs every second, you can use the completionTimeoutCheckerInterval option to configure how frequently to run the checker. The timeout is an approximation and there is no guarantee that the a timeout is triggered exactly after the timeout value. It is not recommended to use very low timeout values or checker intervals.
 */
export type CompletionTimeoutExpression =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * The expression used to calculate the correlation key to use for aggregation. The Exchange which has the same correlation key is aggregated together. If the correlation key could not be evaluated an Exception is thrown. You can disable this by using the ignoreBadCorrelationKeys option.
 */
export type CorrelationExpression =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled = boolean;
/**
 * Discards the aggregated message when aggregation failed (an exception was thrown from AggregationStrategy . This means the partly aggregated message is dropped and not sent out of the aggregator. This option cannot be used together with completionFromBatchConsumer.
 */
export type DiscardOnAggregationFailure = boolean;
/**
 * Discards the aggregated message on completion timeout. This means on timeout the aggregated message is dropped and not sent out of the aggregator.
 */
export type DiscardOnCompletionTimeout = boolean;
/**
 * Use eager completion checking which means that the completionPredicate will use the incoming Exchange. As opposed to without eager completion checking the completionPredicate will use the aggregated Exchange.
 */
export type EagerCheckCompletion = boolean;
/**
 * If using parallelProcessing you can specify a custom thread pool to be used. In fact also if you are not using parallelProcessing this custom thread pool is used to send out aggregated exchanges as well.
 */
export type ExecutorService = string;
/**
 * Indicates to complete all current aggregated exchanges when the context is stopped
 */
export type ForceCompletionOnStop = boolean;
/**
 * Sets the id of this node
 */
export type Id76 = string;
/**
 * If a correlation key cannot be successfully evaluated it will be ignored by logging a DEBUG and then just ignore the incoming Exchange.
 */
export type IgnoreInvalidCorrelationKeys = boolean;
/**
 * Enable exponential backoff
 */
export type ExponentialBackOff = boolean;
/**
 * Sets the maximum number of retries
 */
export type MaximumRetries = number;
/**
 * Sets the upper value of retry in millis between retries, when using exponential or random backoff
 */
export type MaximumRetryDelay = string;
/**
 * Enables random backoff
 */
export type RandomBackOff = boolean;
/**
 * Sets the delay in millis between retries
 */
export type RetryDelay = string;
/**
 * Turns on using optimistic locking, which requires the aggregationRepository being used, is supporting this by implementing org.apache.camel.spi.OptimisticLockingAggregationRepository .
 */
export type OptimisticLocking = boolean;
/**
 * When aggregated are completed they are being send out of the aggregator. This option indicates whether or not Ua should use a thread pool with multiple threads for concurrency. If no custom thread pool has been specified then Ua creates a default pool with 10 concurrent threads.
 */
export type ParallelProcessing = boolean;
/**
 * If using either of the completionTimeout, completionTimeoutExpression, or completionInterval options a background thread is created to check for the completion for every aggregator. Set this option to provide a custom thread pool to be used rather than creating a new thread for every aggregator.
 */
export type TimeoutCheckerExecutorService = string;
/**
 * Calls a Java bean
 */
export type Bean =
    | string
    | {
    beanType?: BeanType1;
    description?: Description1;
    disabled?: Disabled1;
    id?: Id77;
    method?: Method1;
    ref?: Ref4;
    scope?: Scope1;
};
/**
 * Sets the class name (fully qualified) of the bean to use
 */
export type BeanType1 = string;
/**
 * Sets the description of this node
 */
export type Description1 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled1 = boolean;
/**
 * Sets the id of this node
 */
export type Id77 = string;
/**
 * Sets the method name on the bean to use
 */
export type Method1 = string;
/**
 * Sets a reference to an existing bean to use, which is looked up from the registry
 */
export type Ref4 = string;
/**
 * Scope of bean. When using singleton scope (default) the bean is created or looked up only once and reused for the lifetime of the endpoint. The bean should be thread-safe in case concurrent threads is calling the bean at the same time. When using request scope the bean is created or looked up once per request (exchange). This can be used if you want to store state on a bean while processing a request and you want to call the same bean instance multiple times while processing the request. The bean does not have to be thread-safe as the instance is only called from the same request. When using prototype scope, then the bean will be looked up or created per call. However in case of lookup then this is delegated to the bean registry such as Spring or CDI (if in use), which depends on their configuration can act as either singleton or prototype scope. So when using prototype scope then this depends on the bean registry implementation.
 */
export type Scope1 = "Singleton" | "Request" | "Prototype";
/**
 * Sets the description of this node
 */
export type Description2 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled2 = boolean;
/**
 * The exception(s) to catch.
 */
export type Exception = string[];
/**
 * Sets the id of this node
 */
export type Id78 = string;
/**
 * Sets an additional predicate that should be true before the onCatch is triggered. To be used for fine grained controlling whether a thrown exception should be intercepted by this exception type or not.
 */
export type OnWhen =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition1;
    [k: string]: unknown;
};
export type ExpressionDefinition =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Expression used as the predicate to evaluate whether this when should trigger and workflow the message or not.
 */
export type ExpressionDefinition1 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description3 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled3 = boolean;
/**
 * Sets the id of this node
 */
export type Id79 = string;
/**
 * Sets the description of this node
 */
export type Description4 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled4 = boolean;
/**
 * Sets the id of this node
 */
export type Id80 = string;
/**
 * Indicates whether this Choice EIP is in precondition mode or not. If so its branches (when/otherwise) are evaluated during startup to keep at runtime only the branch that matched.
 */
export type Precondition = boolean;
/**
 * Triggers a workflow when the expression evaluates to true
 */
export type When1 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition1;
    [k: string]: unknown;
};
/**
 * Sets the when nodes
 */
export type When = When1[];
/**
 * Refers to a circuit breaker configuration (such as resillience4j, or microprofile-fault-tolerance) to use for configuring the circuit breaker EIP.
 */
export type Configuration = string;
/**
 * Sets the description of this node
 */
export type Description5 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled5 = boolean;
/**
 * Whether bulkhead is enabled or not on the circuit breaker. Default is false.
 */
export type BulkheadEnabled = boolean;
/**
 * References to a custom thread pool to use when bulkhead is enabled.
 */
export type BulkheadExecutorService = string;
/**
 * Configures the max amount of concurrent calls the bulkhead will support.
 */
export type BulkheadMaxConcurrentCalls = number & string;
/**
 * Configures the task queue size for holding waiting tasks to be processed by the bulkhead.
 */
export type BulkheadWaitingTaskQueue = number & string;
/**
 * Refers to an existing io.smallrye.faulttolerance.core.circuit.breaker.CircuitBreaker instance to lookup and use from the registry. When using this, then any other circuit breaker options are not in use.
 */
export type CircuitBreaker1 = string;
/**
 * Control how long the circuit breaker stays open. The default is 5 seconds.
 */
export type Delay = string;
/**
 * Configures the failure rate threshold in percentage. If the failure rate is equal or greater than the threshold the CircuitBreaker transitions to open and starts short-circuiting calls. The threshold must be greater than 0 and not greater than 100. Default value is 50 percentage.
 */
export type FailureRatio = number & string;
/**
 * The id of this node
 */
export type Id81 = string;
/**
 * Controls the size of the rolling window used when the circuit breaker is closed
 */
export type RequestVolumeThreshold = number & string;
/**
 * Controls the number of trial calls which are allowed when the circuit breaker is half-open
 */
export type SuccessThreshold = number & string;
/**
 * Configures the thread execution timeout. Default value is 1 second.
 */
export type TimeoutDuration = string;
/**
 * Whether timeout is enabled or not on the circuit breaker. Default is false.
 */
export type TimeoutEnabled = boolean;
/**
 * Configures the pool size of the thread pool when timeout is enabled. Default value is 10.
 */
export type TimeoutPoolSize = number & string;
/**
 * References to a custom thread pool to use when timeout is enabled
 */
export type TimeoutScheduledExecutorService = string;
/**
 * Sets the id of this node
 */
export type Id82 = string;
/**
 * Sets the description of this node
 */
export type Description6 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled6 = boolean;
/**
 * Whether the fallback goes over the network. If the fallback will go over the network it is another possible point of failure. It is important to execute the fallback command on a separate thread-pool, otherwise if the main command were to become latent and fill the thread-pool this would prevent the fallback from running if the two commands share the same pool.
 */
export type FallbackViaNetwork = boolean;
/**
 * Sets the id of this node
 */
export type Id83 = string;
/**
 * Enables automatic transition from OPEN to HALF_OPEN state once the waitDurationInOpenState has passed.
 */
export type AutomaticTransitionFromOpenToHalfOpenEnabled = boolean;
/**
 * Whether bulkhead is enabled or not on the circuit breaker. Default is false.
 */
export type BulkheadEnabled1 = boolean;
/**
 * Configures the max amount of concurrent calls the bulkhead will support.
 */
export type BulkheadMaxConcurrentCalls1 = number & string;
/**
 * Configures a maximum amount of time which the calling thread will wait to enter the bulkhead. If bulkhead has space available, entry is guaranteed and immediate. If bulkhead is full, calling threads will contest for space, if it becomes available. maxWaitDuration can be set to 0. Note: for threads running on an event-loop or equivalent (rx computation pool, etc), setting maxWaitDuration to 0 is highly recommended. Blocking an event-loop thread will most likely have a negative effect on application throughput.
 */
export type BulkheadMaxWaitDuration = number & string;
/**
 * Refers to an existing io.github.resilience4j.circuitbreaker.CircuitBreaker instance to lookup and use from the registry. When using this, then any other circuit breaker options are not in use.
 */
export type CircuitBreaker2 = string;
/**
 * Refers to an existing io.github.resilience4j.circuitbreaker.CircuitBreakerConfig instance to lookup and use from the registry.
 */
export type Config = string;
/**
 * Configures the failure rate threshold in percentage. If the failure rate is equal or greater than the threshold the CircuitBreaker transitions to open and starts short-circuiting calls. The threshold must be greater than 0 and not greater than 100. Default value is 50 percentage.
 */
export type FailureRateThreshold = number & string;
/**
 * The id of this node
 */
export type Id84 = string;
/**
 * Configure a list of exceptions that are ignored and neither count as a failure nor success. Any exception matching or inheriting from one of the list will not count as a failure nor success, even if the exceptions is part of recordExceptions.
 */
export type IgnoreException = string[];
/**
 * Configures the minimum number of calls which are required (per sliding window period) before the CircuitBreaker can calculate the error rate. For example, if minimumNumberOfCalls is 10, then at least 10 calls must be recorded, before the failure rate can be calculated. If only 9 calls have been recorded the CircuitBreaker will not transition to open even if all 9 calls have failed. Default minimumNumberOfCalls is 100
 */
export type MinimumNumberOfCalls = number & string;
/**
 * Configures the number of permitted calls when the CircuitBreaker is half open. The size must be greater than 0. Default size is 10.
 */
export type PermittedNumberOfCallsInHalfOpenState = number & string;
/**
 * Configure a list of exceptions that are recorded as a failure and thus increase the failure rate. Any exception matching or inheriting from one of the list counts as a failure, unless explicitly ignored via ignoreExceptions.
 */
export type RecordException = string[];
/**
 * Configures the size of the sliding window which is used to record the outcome of calls when the CircuitBreaker is closed. slidingWindowSize configures the size of the sliding window. Sliding window can either be count-based or time-based. If slidingWindowType is COUNT_BASED, the last slidingWindowSize calls are recorded and aggregated. If slidingWindowType is TIME_BASED, the calls of the last slidingWindowSize seconds are recorded and aggregated. The slidingWindowSize must be greater than 0. The minimumNumberOfCalls must be greater than 0. If the slidingWindowType is COUNT_BASED, the minimumNumberOfCalls cannot be greater than slidingWindowSize . If the slidingWindowType is TIME_BASED, you can pick whatever you want. Default slidingWindowSize is 100.
 */
export type SlidingWindowSize = number & string;
/**
 * Configures the type of the sliding window which is used to record the outcome of calls when the CircuitBreaker is closed. Sliding window can either be count-based or time-based. If slidingWindowType is COUNT_BASED, the last slidingWindowSize calls are recorded and aggregated. If slidingWindowType is TIME_BASED, the calls of the last slidingWindowSize seconds are recorded and aggregated. Default slidingWindowType is COUNT_BASED.
 */
export type SlidingWindowType = "TIME_BASED" | "COUNT_BASED";
/**
 * Configures the duration threshold (seconds) above which calls are considered as slow and increase the slow calls percentage. Default value is 60 seconds.
 */
export type SlowCallDurationThreshold = number & string;
/**
 * Configures a threshold in percentage. The CircuitBreaker considers a call as slow when the call duration is greater than slowCallDurationThreshold Duration. When the percentage of slow calls is equal or greater the threshold, the CircuitBreaker transitions to open and starts short-circuiting calls. The threshold must be greater than 0 and not greater than 100. Default value is 100 percentage which means that all recorded calls must be slower than slowCallDurationThreshold.
 */
export type SlowCallRateThreshold = number & string;
/**
 * Whether to throw io.github.resilience4j.circuitbreaker.CallNotPermittedException when the call is rejected due circuit breaker is half open or open.
 */
export type ThrowExceptionWhenHalfOpenOrOpenState = boolean;
/**
 * Configures whether cancel is called on the running future. Defaults to true.
 */
export type TimeoutCancelRunningFuture = boolean;
/**
 * Configures the thread execution timeout. Default value is 1 second.
 */
export type TimeoutDuration1 = number & string;
/**
 * Whether timeout is enabled or not on the circuit breaker. Default is false.
 */
export type TimeoutEnabled1 = boolean;
/**
 * References to a custom thread pool to use when timeout is enabled (uses ForkJoinPool#commonPool() by default)
 */
export type TimeoutExecutorService = string;
/**
 * Configures the wait duration (in seconds) which specifies how long the CircuitBreaker should stay open, before it switches to half open. Default value is 60 seconds.
 */
export type WaitDurationInOpenState = number & string;
/**
 * Enables writable stack traces. When set to false, Exception.getStackTrace returns a zero length array. This may be used to reduce log spam when the circuit breaker is open as the cause of the exceptions is already known (the circuit breaker is short-circuiting calls).
 */
export type WritableStackTraceEnabled = boolean;
/**
 * To use a custom AggregationStrategy instead of the default implementation. Notice you cannot use both custom aggregation strategy and configure data at the same time.
 */
export type AggregationStrategy1 = string;
/**
 * This option can be used to explicit declare the method name to use, when using POJOs as the AggregationStrategy.
 */
export type AggregationStrategyMethodName1 = string;
/**
 * Sets the description of this node
 */
export type Description7 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled7 = boolean;
/**
 * Specify a filter to control what data gets merged data back from the claim check repository. The following syntax is supported: body - to aggregate the message body attachments - to aggregate all the message attachments headers - to aggregate all the message headers header:pattern - to aggregate all the message headers that matches the pattern. The following pattern rules are applied in this order: exact match, returns true wildcard match (pattern ends with a and the name starts with the pattern), returns true regular expression match, returns true otherwise returns false You can specify multiple rules separated by comma. For example, the following includes the message body and all headers starting with foo: body,header:foo. The syntax supports the following prefixes which can be used to specify include,exclude, or remove - to include (which is the default mode) - - to exclude (exclude takes precedence over include) -- - to remove (remove takes precedence) For example to exclude a header name foo, and remove all headers starting with bar, -header:foo,--headers:bar Note you cannot have both include and exclude header:pattern at the same time.
 */
export type Filter = string;
/**
 * Sets the id of this node
 */
export type Id85 = string;
/**
 * To use a specific key for claim check id (for dynamic keys use simple language syntax as the key).
 */
export type Key1 = string;
/**
 * The claim check operation to use. The following operations are supported: Get - Gets (does not remove) the claim check by the given key. GetAndRemove - Gets and removes the claim check by the given key. Set - Sets a new (will override if key already exists) claim check with the given key. Push - Sets a new claim check on the stack (does not use key). Pop - Gets the latest claim check from the stack (does not use key).
 */
export type Operation = "Get" | "GetAndRemove" | "Set" | "Push" | "Pop";
/**
 * Converts the message body to another type
 */
export type ConvertBodyTo =
    | string
    | {
    charset?: Charset;
    description?: Description8;
    disabled?: Disabled8;
    id?: Id86;
    mandatory?: Mandatory;
    type?: Type3;
};
/**
 * To use a specific charset when converting
 */
export type Charset = string;
/**
 * Sets the description of this node
 */
export type Description8 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled8 = boolean;
/**
 * Sets the id of this node
 */
export type Id86 = string;
/**
 * When mandatory then the conversion must return a value (cannot be null), if this is not possible then NoTypeConversionAvailableException is thrown. Setting this to false could mean conversion is not possible and the value is null.
 */
export type Mandatory = boolean;
/**
 * The java type to convert to
 */
export type Type3 = string;
/**
 * To use a specific charset when converting
 */
export type Charset1 = string;
/**
 * Sets the description of this node
 */
export type Description9 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled9 = boolean;
/**
 * Sets the id of this node
 */
export type Id87 = string;
/**
 * When mandatory then the conversion must return a value (cannot be null), if this is not possible then NoTypeConversionAvailableException is thrown. Setting this to false could mean conversion is not possible and the value is null.
 */
export type Mandatory1 = boolean;
/**
 * Name of message header to convert its value The simple language can be used to define a dynamic evaluated header name to be used. Otherwise a constant name will be used.
 */
export type Name2 = string;
/**
 * To use another header to store the result. By default, the result is stored in the same header. This option allows to use another header. The simple language can be used to define a dynamic evaluated header name to be used. Otherwise a constant name will be used.
 */
export type ToName = string;
/**
 * The java type to convert to
 */
export type Type4 = string;
/**
 * To use a specific charset when converting
 */
export type Charset2 = string;
/**
 * Sets the description of this node
 */
export type Description10 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled10 = boolean;
/**
 * Sets the id of this node
 */
export type Id88 = string;
/**
 * When mandatory then the conversion must return a value (cannot be null), if this is not possible then NoTypeConversionAvailableException is thrown. Setting this to false could mean conversion is not possible and the value is null.
 */
export type Mandatory2 = boolean;
/**
 * Name of variable to convert its value The simple language can be used to define a dynamic evaluated header name to be used. Otherwise a constant name will be used.
 */
export type Name3 = string;
/**
 * To use another variable to store the result. By default, the result is stored in the same variable. This option allows to use another variable. The simple language can be used to define a dynamic evaluated variable name to be used. Otherwise a constant name will be used.
 */
export type ToName1 = string;
/**
 * The java type to convert to
 */
export type Type5 = string;
/**
 * Delays processing for a specified length of time
 */
export type Delay1 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition2;
    [k: string]: unknown;
};
/**
 * Expression to define how long time to wait (in millis)
 */
export type ExpressionDefinition2 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Workflow messages based on dynamic rules
 */
export type DynamicWorkflowr =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition3;
    [k: string]: unknown;
};
/**
 * Expression to call that returns the endpoint(s) to workflow to in the dynamic routing. Important: The expression will be called in a while loop fashion, until the expression returns null which means the dynamic workflow is finished.
 */
export type ExpressionDefinition3 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Enriches a message with data from a secondary resource
 */
export type Enrich =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition4;
    [k: string]: unknown;
};
/**
 * Expression that computes the endpoint uri to use as the resource endpoint to enrich from
 */
export type ExpressionDefinition4 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Filter out messages based using a predicate
 */
export type Filter1 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition5;
    [k: string]: unknown;
};
/**
 * Expression to determine if the message should be filtered or not. If the expression returns an empty value or false then the message is filtered (dropped), otherwise the message is continued being workflowd.
 */
export type ExpressionDefinition5 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description11 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled11 = boolean;
/**
 * Sets the id of this node
 */
export type Id89 = string;
/**
 * Filters out duplicate messages
 */
export type IdempotentConsumer =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition6;
    [k: string]: unknown;
};
/**
 * Expression used to calculate the correlation key to use for duplicate check. The Exchange which has the same correlation key is regarded as a duplicate and will be rejected.
 */
export type ExpressionDefinition6 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
export type KameletDefinition =
    | string
    | {
    name?: string;
    parameters?: {
        [k: string]: unknown;
    };
    steps?: ProcessorDefinition[];
};
/**
 * Balances message processing among a number of nodes
 */
export type LoadBalance =
    | {
    customLoadBalancer: CustomLoadBalancer;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    failoverLoadBalancer: FailoverLoadBalancer;
    [k: string]: unknown;
}
    | {
    randomLoadBalancer: RandomLoadBalancer;
    [k: string]: unknown;
}
    | {
    roundRobinLoadBalancer: RoundRobinLoadBalancer;
    [k: string]: unknown;
}
    | {
    stickyLoadBalancer: StickyLoadBalancer;
    [k: string]: unknown;
}
    | {
    topicLoadBalancer: TopicLoadBalancer;
    [k: string]: unknown;
}
    | {
    weightedLoadBalancer: WeightedLoadBalancer;
    [k: string]: unknown;
};
/**
 * To use a custom load balancer implementation.
 */
export type CustomLoadBalancer =
    | string
    | {
    id?: Id90;
    ref?: Ref5;
};
/**
 * The id of this node
 */
export type Id90 = string;
/**
 * Refers to the custom load balancer to lookup from the registry
 */
export type Ref5 = string;
/**
 * A list of class names for specific exceptions to monitor. If no exceptions are configured then all exceptions are monitored
 */
export type Exception1 = string[];
/**
 * The id of this node
 */
export type Id91 = string;
/**
 * A value to indicate after X failover attempts we should exhaust (give up). Use -1 to indicate never give up and continuously try to failover. Use 0 to never failover. And use e.g. 3 to failover at most 3 times before giving up. This option can be used whether roundRobin is enabled or not.
 */
export type MaximumFailoverAttempts = string;
/**
 * Whether or not the failover load balancer should operate in round robin mode or not. If not, then it will always start from the first endpoint when a new message is to be processed. In other words it restart from the top for every message. If round robin is enabled, then it keeps state and will continue with the next endpoint in a round robin fashion. You can also enable sticky mode together with round robin, if so then it will pick the last known good endpoint to use when starting the load balancing (instead of using the next when starting).
 */
export type RoundRobin = string;
/**
 * Whether or not the failover load balancer should operate in sticky mode or not. If not, then it will always start from the first endpoint when a new message is to be processed. In other words it restart from the top for every message. If sticky is enabled, then it keeps state and will continue with the last known good endpoint. You can also enable sticky mode together with round robin, if so then it will pick the last known good endpoint to use when starting the load balancing (instead of using the next when starting).
 */
export type Sticky = string;
/**
 * The id of this node
 */
export type Id92 = string;
/**
 * The id of this node
 */
export type Id93 = string;
/**
 * The correlation expression to use to calculate the correlation key
 */
export type CorrelationExpression1 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * The id of this node
 */
export type Id94 = string;
/**
 * The id of this node
 */
export type Id95 = string;
/**
 * The distribution ratio is a delimited String consisting on integer weights separated by delimiters for example 2,3,5. The distributionRatio must match the number of endpoints and/or processors specified in the load balancer list.
 */
export type DistributionRatio = string;
/**
 * Delimiter used to specify the distribution ratio. The default value is , (comma)
 */
export type DistributionRatioDelimiter = string;
/**
 * The id of this node
 */
export type Id96 = string;
/**
 * To enable round robin mode. By default the weighted distribution mode is used. The default value is false.
 */
export type RoundRobin1 = boolean;
/**
 * Used for printing custom messages to the logger.
 */
export type Logger =
    | string
    | {
    description?: Description12;
    disabled?: Disabled12;
    id?: Id97;
    logName?: LogName4;
    logger?: Logger1;
    loggingLevel?: LoggingLevel;
    marker?: Marker;
    message?: Message;
};
/**
 * Sets the description of this node
 */
export type Description12 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled12 = boolean;
/**
 * Sets the id of this node
 */
export type Id97 = string;
/**
 * Sets the name of the logger. The name is default the workflowId or the source:line if source location is enabled. You can also specify the name using tokens: ${class} - the logger class name (org.apache.camel.processor.LogProcessor) ${contextId} - the camel context id ${workflowId} - the workflow id ${groupId} - the workflow group id ${nodeId} - the node id ${nodePrefixId} - the node prefix id ${source} - the source:line (source location must be enabled) ${source.name} - the source filename (source location must be enabled) ${source.line} - the source line number (source location must be enabled) For example to use the workflow and node id you can specify the name as: ${workflowId}/${nodeId}
 */
export type LogName4 = string;
/**
 * To refer to a custom logger instance to lookup from the registry.
 */
export type Logger1 = string;
/**
 * Sets the logging level. The default value is INFO
 */
export type LoggingLevel = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * To use slf4j marker
 */
export type Marker = string;
/**
 * Sets the log message (uses simple language)
 */
export type Message = string;
/**
 * Processes a message multiple times
 */
export type Loop =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition7;
    [k: string]: unknown;
};
/**
 * Expression to define how many times we should loop. Notice the expression is only evaluated once, and should return a number as how many times to loop. A value of zero or negative means no looping. The loop is like a for-loop fashion, if you want a while loop, then the dynamic workflow may be a better choice.
 */
export type ExpressionDefinition7 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Marshals data into a specified format for transmission over a transport or component
 */
export type Marshal =
    | {
    asn1: ASN1File;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    avro: Avro;
    [k: string]: unknown;
}
    | {
    barcode: Barcode;
    [k: string]: unknown;
}
    | {
    base64: Base64;
    [k: string]: unknown;
}
    | {
    beanio: BeanIO;
    [k: string]: unknown;
}
    | {
    bindy: Bindy;
    [k: string]: unknown;
}
    | {
    cbor: CBOR;
    [k: string]: unknown;
}
    | {
    crypto: CryptoJavaCryptographicExtension;
    [k: string]: unknown;
}
    | {
    csv: CSV;
    [k: string]: unknown;
}
    | {
    custom: Custom;
    [k: string]: unknown;
}
    | {
    fhirJson: FHIRJSon;
    [k: string]: unknown;
}
    | {
    fhirXml: FHIRXML;
    [k: string]: unknown;
}
    | {
    flatpack: Flatpack;
    [k: string]: unknown;
}
    | {
    fury: Fury;
    [k: string]: unknown;
}
    | {
    grok: Grok;
    [k: string]: unknown;
}
    | {
    gzipDeflater: GZipDeflater;
    [k: string]: unknown;
}
    | {
    hl7: HL7;
    [k: string]: unknown;
}
    | {
    ical: ICal;
    [k: string]: unknown;
}
    | {
    jacksonXml: JacksonXML;
    [k: string]: unknown;
}
    | {
    jaxb: JAXB;
    [k: string]: unknown;
}
    | {
    json: JSon;
    [k: string]: unknown;
}
    | {
    jsonApi: JSonApi;
    [k: string]: unknown;
}
    | {
    lzf: LZFDeflateCompression;
    [k: string]: unknown;
}
    | {
    mimeMultipart: MIMEMultipart;
    [k: string]: unknown;
}
    | {
    parquetAvro: ParquetFile;
    [k: string]: unknown;
}
    | {
    pgp: PGP;
    [k: string]: unknown;
}
    | {
    protobuf: Protobuf;
    [k: string]: unknown;
}
    | {
    rss: RSS;
    [k: string]: unknown;
}
    | {
    smooks: Smooks;
    [k: string]: unknown;
}
    | {
    soap: SOAP;
    [k: string]: unknown;
}
    | {
    swiftMt: SWIFTMT;
    [k: string]: unknown;
}
    | {
    swiftMx: SWIFTMX;
    [k: string]: unknown;
}
    | {
    syslog: Syslog;
    [k: string]: unknown;
}
    | {
    tarFile: TarFile;
    [k: string]: unknown;
}
    | {
    thrift: Thrift;
    [k: string]: unknown;
}
    | {
    tidyMarkup: TidyMarkup;
    [k: string]: unknown;
}
    | {
    univocityCsv: UniVocityCSV;
    [k: string]: unknown;
}
    | {
    univocityFixed: UniVocityFixedLength;
    [k: string]: unknown;
}
    | {
    univocityTsv: UniVocityTSV;
    [k: string]: unknown;
}
    | {
    xmlSecurity: XMLSecurity;
    [k: string]: unknown;
}
    | {
    yaml: YAML;
    [k: string]: unknown;
}
    | {
    zipDeflater: ZipDeflater;
    [k: string]: unknown;
}
    | {
    zipFile: ZipFile;
    [k: string]: unknown;
};
/**
 * Refers to an AggregationStrategy to be used to assemble the replies from the multicasts, into a single outgoing message from the Multicast. By default Ua will use the last reply as the outgoing message. You can also use a POJO as the AggregationStrategy
 */
export type AggregationStrategy2 = string;
/**
 * If this option is false then the aggregate method is not used if there was no data to enrich. If this option is true then null values is used as the oldExchange (when no data to enrich), when using POJOs as the AggregationStrategy
 */
export type AggregationStrategyMethodAllowNull1 = boolean;
/**
 * This option can be used to explicit declare the method name to use, when using POJOs as the AggregationStrategy.
 */
export type AggregationStrategyMethodName2 = string;
/**
 * Sets the description of this node
 */
export type Description13 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled13 = boolean;
/**
 * Refers to a custom Thread Pool to be used for parallel processing. Notice if you set this option, then parallel processing is automatic implied, and you do not have to enable that option as well.
 */
export type ExecutorService1 = string;
/**
 * Sets the id of this node
 */
export type Id98 = string;
/**
 * Uses the Processor when preparing the org.apache.camel.Exchange to be send. This can be used to deep-clone messages that should be send, or any custom logic needed before the exchange is send.
 */
export type OnPrepare = string;
/**
 * If enabled then the aggregate method on AggregationStrategy can be called concurrently. Notice that this would require the implementation of AggregationStrategy to be implemented as thread-safe. By default this is false meaning that Ua synchronizes the call to the aggregate method. Though in some use-cases this can be used to archive higher performance when the AggregationStrategy is implemented as thread-safe.
 */
export type ParallelAggregate = boolean;
/**
 * If enabled then sending messages to the multicasts occurs concurrently. Note the caller thread will still wait until all messages has been fully processed, before it continues. Its only the sending and processing the replies from the multicasts which happens concurrently. When parallel processing is enabled, then the Ua routing engin will continue processing using last used thread from the parallel thread pool. However, if you want to use the original thread that called the multicast, then make sure to enable the synchronous option as well.
 */
export type ParallelProcessing1 = boolean;
/**
 * Shares the org.apache.camel.spi.UnitOfWork with the parent and each of the sub messages. Multicast will by default not share unit of work between the parent exchange and each multicasted exchange. This means each sub exchange has its own individual unit of work.
 */
export type ShareUnitOfWork = boolean;
/**
 * Will now stop further processing if an exception or failure occurred during processing of an org.apache.camel.Exchange and the caused exception will be thrown. Will also stop if processing the exchange failed (has a fault message) or an exception was thrown and handled by the error handler (such as using onException). In all situations the multicast will stop further processing. This is the same behavior as in pipeline, which is used by the routing engine. The default behavior is to not stop but continue processing till the end
 */
export type StopOnException = boolean;
/**
 * If enabled then Ua will process replies out-of-order, eg in the order they come back. If disabled, Ua will process replies in the same order as defined by the multicast.
 */
export type Streaming = boolean;
/**
 * Sets whether synchronous processing should be strictly used. When enabled then the same thread is used to continue routing after the multicast is complete, even if parallel processing is enabled.
 */
export type Synchronous = boolean;
/**
 * Sets a total timeout specified in millis, when using parallel processing. If the Multicast hasn't been able to send and process all replies within the given timeframe, then the timeout triggers and the Multicast breaks out and continues. Notice if you provide a TimeoutAwareAggregationStrategy then the timeout method is invoked before breaking out. If the timeout is reached with running tasks still remaining, certain tasks for which it is difficult for Ua to shut down in a graceful manner may continue to run. So use this option with a bit of care.
 */
export type Timeout = string;
/**
 * Sets the consumer listener to use
 */
export type ConsumerListener = string;
/**
 * Sets the description of this node
 */
export type Description14 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled14 = boolean;
/**
 * Sets the id of this node
 */
export type Id99 = string;
/**
 * References to a java.util.function.Predicate to use for until checks. The predicate is responsible for evaluating whether the processing can resume or not. Such predicate should return true if the consumption can resume, or false otherwise. The exact point of when the predicate is called is dependent on the component, and it may be called on either one of the available events. Implementations should not assume the predicate to be called at any specific point.
 */
export type UntilCheck = string;
/**
 * Sets the description of this node
 */
export type Description15 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled15 = boolean;
/**
 * Sets the id of this node
 */
export type Id100 = string;
/**
 * Sets the description of this node
 */
export type Description16 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled16 = boolean;
/**
 * Sets the id of this node
 */
export type Id101 = string;
/**
 * Sets a reference to use for lookup the policy in the registry.
 */
export type Ref6 = string;
/**
 * Polls a message from a static endpoint
 */
export type Poll =
    | string
    | {
    description?: Description17;
    disabled?: Disabled17;
    id?: Id102;
    parameters?: {
        [k: string]: unknown;
    };
    timeout?: Timeout1;
    uri?: Uri;
    variableReceive?: VariableReceive;
};
/**
 * Sets the description of this node
 */
export type Description17 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled17 = boolean;
/**
 * Sets the id of this node
 */
export type Id102 = string;
/**
 * Timeout in millis when polling from the external service. The timeout has influence about the poll enrich behavior. It basically operations in three different modes: negative value - Waits until a message is available and then returns it. Warning that this method could block indefinitely if no messages are available. 0 - Attempts to receive a message exchange immediately without waiting and returning null if a message exchange is not available yet. positive value - Attempts to receive a message exchange, waiting up to the given timeout to expire if a message is not yet available. Returns null if timed out The default value is 20000 (20 seconds).
 */
export type Timeout1 = string;
/**
 * Sets the uri of the endpoint to poll from.
 */
export type Uri = string;
/**
 * To use a variable to store the received message body (only body, not headers). This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using receive variable then the received body is stored only in this variable and not on the current message.
 */
export type VariableReceive = string;
/**
 * Enriches messages with data polled from a secondary resource
 */
export type PollEnrich =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition8;
    [k: string]: unknown;
};
/**
 * Expression that computes the endpoint uri to use as the resource endpoint to enrich from
 */
export type ExpressionDefinition8 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description18 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled18 = boolean;
/**
 * Sets the id of this node
 */
export type Id103 = string;
/**
 * Reference to the Processor to lookup in the registry to use. A Processor is a class of type org.apache.camel.Processor, which can are to be called by this EIP. In this processor you have custom Java code, that can work with the message, such as to do custom business logic, special message manipulations and so on. By default, the ref, will lookup the bean in the Ua registry. The ref can use prefix that controls how the processor is obtained. You can use #bean:myBean where myBean is the id of the Ua processor (lookup). Can also be used for creating new beans by their class name by prefixing with #class, eg #class:com.foo.MyClassType. And it is also possible to refer to singleton beans by their type in the registry by prefixing with #type: syntax, eg #type:com.foo.MyClassType
 */
export type Ref7 = string;
/**
 * Workflow messages to a number of dynamically specified recipients
 */
export type RecipientList =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition9;
    [k: string]: unknown;
};
/**
 * Expression that returns which endpoints (url) to send the message to (the recipients). If the expression return an empty value then the message is not sent to any recipients.
 */
export type ExpressionDefinition9 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Removes a named header from the message
 */
export type RemoveHeader =
    | string
    | {
    description?: Description19;
    disabled?: Disabled19;
    id?: Id104;
    name?: Name4;
};
/**
 * Sets the description of this node
 */
export type Description19 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled19 = boolean;
/**
 * Sets the id of this node
 */
export type Id104 = string;
/**
 * Name of header to remove
 */
export type Name4 = string;
/**
 * Removes message headers whose name matches a specified pattern
 */
export type RemoveHeaders =
    | string
    | {
    description?: Description20;
    disabled?: Disabled20;
    excludePattern?: ExcludePattern;
    id?: Id105;
    pattern?: Pattern1;
};
/**
 * Sets the description of this node
 */
export type Description20 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled20 = boolean;
/**
 * Name or patter of headers to not remove. The pattern is matched in the following order: 1 = exact match 2 = wildcard (pattern ends with a and the name starts with the pattern) 3 = regular expression (all of above is case in-sensitive).
 */
export type ExcludePattern = string;
/**
 * Sets the id of this node
 */
export type Id105 = string;
/**
 * Name or pattern of headers to remove. The pattern is matched in the following order: 1 = exact match 2 = wildcard (pattern ends with a and the name starts with the pattern) 3 = regular expression (all of above is case in-sensitive).
 */
export type Pattern1 = string;
/**
 * Removes message exchange properties whose name matches a specified pattern
 */
export type RemoveProperties =
    | string
    | {
    description?: Description21;
    disabled?: Disabled21;
    excludePattern?: ExcludePattern1;
    id?: Id106;
    pattern?: Pattern2;
};
/**
 * Sets the description of this node
 */
export type Description21 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled21 = boolean;
/**
 * Name or pattern of properties to not remove. The pattern is matched in the following order: 1 = exact match 2 = wildcard (pattern ends with a and the name starts with the pattern) 3 = regular expression (all of above is case in-sensitive).
 */
export type ExcludePattern1 = string;
/**
 * Sets the id of this node
 */
export type Id106 = string;
/**
 * Name or pattern of properties to remove. The pattern is matched in the following order: 1 = exact match 2 = wildcard (pattern ends with a and the name starts with the pattern) 3 = regular expression (all of above is case in-sensitive).
 */
export type Pattern2 = string;
/**
 * Removes a named property from the message exchange
 */
export type RemoveProperty =
    | string
    | {
    description?: Description22;
    disabled?: Disabled22;
    id?: Id107;
    name?: Name5;
};
/**
 * Sets the description of this node
 */
export type Description22 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled22 = boolean;
/**
 * Sets the id of this node
 */
export type Id107 = string;
/**
 * Name of property to remove.
 */
export type Name5 = string;
/**
 * Removes a named variable
 */
export type RemoveVariable =
    | string
    | {
    description?: Description23;
    disabled?: Disabled23;
    id?: Id108;
    name?: Name6;
};
/**
 * Sets the description of this node
 */
export type Description23 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled23 = boolean;
/**
 * Sets the id of this node
 */
export type Id108 = string;
/**
 * Name of variable to remove.
 */
export type Name6 = string;
/**
 * Resequences (re-order) messages based on an expression
 */
export type Resequence =
    | (
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition10;
    [k: string]: unknown;
}
    )
    | (
    | {
    batchConfig: BatchConfig;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    streamConfig: StreamConfig;
    [k: string]: unknown;
}
    );
/**
 * Expression to use for re-ordering the messages, such as a header with a sequence number
 */
export type ExpressionDefinition10 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Whether to allow duplicates.
 */
export type AllowDuplicates = boolean;
/**
 * Sets the size of the batch to be re-ordered. The default size is 100.
 */
export type BatchSize = number & string;
/**
 * Sets the timeout for collecting elements to be re-ordered. The default timeout is 1000 msec.
 */
export type BatchTimeout = string;
/**
 * Whether to ignore invalid exchanges
 */
export type IgnoreInvalidExchanges = boolean;
/**
 * Whether to reverse the ordering.
 */
export type Reverse = boolean;
/**
 * Sets the capacity of the resequencer inbound queue.
 */
export type Capacity = number & string;
/**
 * To use a custom comparator as a org.apache.camel.processor.resequencer.ExpressionResultComparator type.
 */
export type Comparator = string;
/**
 * Sets the interval in milliseconds the stream resequencer will at most wait while waiting for condition of being able to deliver.
 */
export type DeliveryAttemptInterval = string;
/**
 * Whether to ignore invalid exchanges
 */
export type IgnoreInvalidExchanges1 = boolean;
/**
 * If true, throws an exception when messages older than the last delivered message are processed
 */
export type RejectOld = boolean;
/**
 * Sets minimum time (milliseconds) to wait for missing elements (messages).
 */
export type Timeout2 = string;
/**
 * Sets the description of this node
 */
export type Description24 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled24 = boolean;
/**
 * Sets the id of this node
 */
export type Id109 = string;
/**
 * Sets whether the offsets will be intermittently present or whether they must be present in every exchange
 */
export type Intermittent = boolean;
export type LoggingLevel1 = "TRACE" | "DEBUG" | "INFO" | "WARN" | "ERROR" | "OFF";
/**
 * Sets the resume strategy to use
 */
export type ResumeStrategy = string;
/**
 * Forces a rollback by stopping routing the message
 */
export type Rollback =
    | string
    | {
    description?: Description25;
    disabled?: Disabled25;
    id?: Id110;
    markRollbackOnly?: MarkRollbackOnly;
    markRollbackOnlyLast?: MarkRollbackOnlyLast;
    message?: Message1;
};
/**
 * Sets the description of this node
 */
export type Description25 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled25 = boolean;
/**
 * Sets the id of this node
 */
export type Id110 = string;
/**
 * Mark the transaction for rollback only (cannot be overruled to commit)
 */
export type MarkRollbackOnly = boolean;
/**
 * Mark only last sub transaction for rollback only. When using sub transactions (if the transaction manager support this)
 */
export type MarkRollbackOnlyLast = boolean;
/**
 * Message to use in rollback exception
 */
export type Message1 = string;
/**
 * Workflows a message through a series of steps that are pre-determined (the slip)
 */
export type RoutingSlip =
    | string
    | (
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition11;
    [k: string]: unknown;
}
    );
/**
 * Expression to define the routing slip, which defines which endpoints to workflow the message in a pipeline style. Notice the expression is evaluated once, if you want a more dynamic style, then the dynamic workflow eip is a better choice.
 */
export type ExpressionDefinition11 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * The compensation endpoint URI that must be called to compensate all changes done in the workflow. The workflow corresponding to the compensation URI must perform compensation and complete without error. If errors occur during compensation, the saga service may call again the compensation URI to retry.
 */
export type Compensation =
    | string
    | {
    description?: string;
    disabled?: boolean;
    id?: string;
    parameters?: {
        [k: string]: unknown;
    };
    uri?: string;
};
/**
 * The completion endpoint URI that will be called when the Saga is completed successfully. The workflow corresponding to the completion URI must perform completion tasks and terminate without error. If errors occur during completion, the saga service may call again the completion URI to retry.
 */
export type Completion =
    | string
    | {
    description?: string;
    disabled?: boolean;
    id?: string;
    parameters?: {
        [k: string]: unknown;
    };
    uri?: string;
};
/**
 * Determine how the saga should be considered complete. When set to AUTO, the saga is completed when the exchange that initiates the saga is processed successfully, or compensated when it completes exceptionally. When set to MANUAL, the user must complete or compensate the saga using the saga:complete or saga:compensate endpoints.
 */
export type CompletionMode = "AUTO" | "MANUAL";
/**
 * Sets the description of this node
 */
export type Description26 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled26 = boolean;
/**
 * Sets the id of this node
 */
export type Id111 = string;
/**
 * A key value pair where the value is an expression.
 */
export type PropertyExpression =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition12;
    [k: string]: unknown;
};
/**
 * Property values as an expression
 */
export type ExpressionDefinition12 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Allows to save properties of the current exchange in order to re-use them in a compensation/completion callback workflow. Options are usually helpful e.g. to store and retrieve identifiers of objects that should be deleted in compensating actions. Option values will be transformed into input headers of the compensation/completion exchange.
 */
export type Option1 = PropertyExpression[];
/**
 * Set the Saga propagation mode (REQUIRED, REQUIRES_NEW, MANDATORY, SUPPORTS, NOT_SUPPORTED, NEVER).
 */
export type Propagation = "REQUIRED" | "REQUIRES_NEW" | "MANDATORY" | "SUPPORTS" | "NOT_SUPPORTED" | "NEVER";
/**
 * Refers to the id to lookup in the registry for the specific UaSagaService to use.
 */
export type SagaService = string;
/**
 * Set the maximum amount of time for the Saga. After the timeout is expired, the saga will be compensated automatically (unless a different decision has been taken in the meantime).
 */
export type Timeout3 = string;
/**
 * Extract a sample of the messages passing through a workflow
 */
export type Sample =
    | string
    | {
    description?: Description27;
    disabled?: Disabled27;
    id?: Id112;
    messageFrequency?: MessageFrequency;
    samplePeriod?: SamplePeriod;
};
/**
 * Sets the description of this node
 */
export type Description27 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled27 = boolean;
/**
 * Sets the id of this node
 */
export type Id112 = string;
/**
 * Sets the sample message count which only a single Exchange will pass through after this many received.
 */
export type MessageFrequency = number;
/**
 * Sets the sample period during which only a single Exchange will pass through.
 */
export type SamplePeriod = string;
/**
 * Executes a script from a language which does not change the message body.
 */
export type Script1 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition13;
    [k: string]: unknown;
};
/**
 * Expression to return the transformed message body (the new message body to use)
 */
export type ExpressionDefinition13 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the contents of the message body
 */
export type SetBody =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition14;
    [k: string]: unknown;
};
/**
 * Expression that returns the new body to use
 */
export type ExpressionDefinition14 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the exchange pattern on the message exchange
 */
export type SetExchangePattern =
    | string
    | {
    description?: Description28;
    disabled?: Disabled28;
    id?: Id113;
    pattern?: Pattern3;
};
/**
 * Sets the description of this node
 */
export type Description28 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled28 = boolean;
/**
 * Sets the id of this node
 */
export type Id113 = string;
/**
 * Sets the new exchange pattern of the Exchange to be used from this point forward
 */
export type Pattern3 = "InOnly" | "InOut";
/**
 * Sets the value of a message header
 */
export type SetHeader =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition15;
    [k: string]: unknown;
};
/**
 * Expression to return the value of the header
 */
export type ExpressionDefinition15 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description29 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled29 = boolean;
/**
 * Contains the headers to be set
 */
export type Headers = SetHeader[];
/**
 * Sets the id of this node
 */
export type Id114 = string;
/**
 * Sets a named property on the message exchange
 */
export type SetProperty =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition16;
    [k: string]: unknown;
};
/**
 * Expression to return the value of the message exchange property
 */
export type ExpressionDefinition16 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the value of a variable
 */
export type SetVariable =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition17;
    [k: string]: unknown;
};
/**
 * Expression to return the value of the variable
 */
export type ExpressionDefinition17 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description30 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled30 = boolean;
/**
 * Sets the id of this node
 */
export type Id115 = string;
/**
 * Contains the variables to be set
 */
export type Variables = SetVariable[];
/**
 * Sorts the contents of the message
 */
export type Sort =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition18;
    [k: string]: unknown;
};
/**
 * Optional expression to sort by something else than the message body
 */
export type ExpressionDefinition18 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Splits a single message into many sub-messages.
 */
export type Split =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition19;
    [k: string]: unknown;
};
/**
 * Expression of how to split the message body, such as as-is, using a tokenizer, or using a xpath.
 */
export type ExpressionDefinition19 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description31 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled31 = boolean;
/**
 * Sets the id of this node
 */
export type Id116 = string;
/**
 * Sets the description of this node
 */
export type Description32 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled32 = boolean;
/**
 * Sets the id of this node
 */
export type Id117 = string;
/**
 * Whether idle core threads are allowed to timeout and therefore can shrink the pool size below the core pool size Is by default false
 */
export type AllowCoreThreadTimeOut = boolean;
/**
 * Whether or not to use as caller runs as fallback when a task is rejected being added to the thread pool (when its full). This is only used as fallback if no rejectedPolicy has been configured, or the thread pool has no configured rejection handler. Is by default true
 */
export type CallerRunsWhenRejected = string;
/**
 * Sets the description of this node
 */
export type Description33 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled33 = boolean;
/**
 * To use a custom thread pool
 */
export type ExecutorService2 = string;
/**
 * Sets the id of this node
 */
export type Id118 = string;
/**
 * Sets the keep alive time for idle threads
 */
export type KeepAliveTime = number;
/**
 * Sets the maximum pool size
 */
export type MaxPoolSize = number;
/**
 * Sets the maximum number of tasks in the work queue. Use -1 or Integer.MAX_VALUE for an unbounded queue
 */
export type MaxQueueSize = number;
/**
 * Sets the core pool size
 */
export type PoolSize = number;
/**
 * Sets the handler for tasks which cannot be executed by the thread pool.
 */
export type RejectedPolicy = "Abort" | "CallerRuns";
/**
 * Sets the thread name to use.
 */
export type ThreadName = string;
/**
 * Sets the keep alive time unit. By default SECONDS is used.
 */
export type TimeUnit = "NANOSECONDS" | "MICROSECONDS" | "MILLISECONDS" | "SECONDS" | "MINUTES" | "HOURS" | "DAYS";
/**
 * Controls the rate at which messages are passed to the next node in the workflow
 */
export type Throttle =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition20;
    [k: string]: unknown;
};
/**
 * Expression to configure the maximum number of messages to throttle per request
 */
export type ExpressionDefinition20 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description34 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled34 = boolean;
/**
 * The class of the exception to create using the message.
 */
export type ExceptionType = string;
/**
 * Sets the id of this node
 */
export type Id119 = string;
/**
 * To create a new exception instance and use the given message as caused message (supports simple language)
 */
export type Message2 = string;
/**
 * Reference to the exception instance to lookup from the registry to throw
 */
export type Ref8 = string;
/**
 * Sends the message to a static endpoint
 */
export type To =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the description of this node
 */
export type Description35 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled35 = boolean;
/**
 * Sets the id of this node
 */
export type Id120 = string;
/**
 * Sets the optional ExchangePattern used to invoke this endpoint
 */
export type Pattern4 = "InOnly" | "InOut";
/**
 * Sets the uri of the endpoint to send to.
 */
export type Uri1 = string;
/**
 * To use a variable to store the received message body (only body, not headers). This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using receive variable then the received body is stored only in this variable and not on the current message.
 */
export type VariableReceive1 = string;
/**
 * To use a variable as the source for the message body to send. This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using send variable then the message body is taken from this variable instead of the current message, however the headers from the message will still be used as well. In other words, the variable is used instead of the message body, but everything else is as usual.
 */
export type VariableSend = string;
/**
 * Sends the message to a dynamic endpoint
 */
export type ToD =
    | string
    | {
    allowOptimisedComponents?: AllowOptimisedComponents;
    autoStartComponents?: AutoStartComponents;
    cacheSize?: CacheSize;
    description?: Description36;
    disabled?: Disabled36;
    id?: Id121;
    ignoreInvalidEndpoint?: IgnoreInvalidEndpoint;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern5;
    uri?: Uri2;
    variableReceive?: VariableReceive2;
    variableSend?: VariableSend1;
};
/**
 * Whether to allow components to optimise toD if they are org.apache.camel.spi.SendDynamicAware .
 */
export type AllowOptimisedComponents = boolean;
/**
 * Whether to auto startup components when toD is starting up.
 */
export type AutoStartComponents = boolean;
/**
 * Sets the maximum size used by the org.apache.camel.spi.ProducerCache which is used to cache and reuse producers when using this recipient list, when uris are reused. Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic endpoint is unique then its best to turn off caching by setting this to -1, which allows Ua to not cache both the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use. This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches. However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on the default size (1000). If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help reduce memory usage to avoid storing too many non frequent used producers.
 */
export type CacheSize = number;
/**
 * Sets the description of this node
 */
export type Description36 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled36 = boolean;
/**
 * Sets the id of this node
 */
export type Id121 = string;
/**
 * Whether to ignore invalid endpoint URIs and skip sending the message.
 */
export type IgnoreInvalidEndpoint = boolean;
/**
 * Sets the optional ExchangePattern used to invoke this endpoint
 */
export type Pattern5 = "InOnly" | "InOut";
/**
 * The uri of the endpoint to send to. The uri can be dynamic computed using the org.apache.camel.language.simple.SimpleLanguage expression.
 */
export type Uri2 = string;
/**
 * To use a variable as the source for the message body to send. This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using send variable then the message body is taken from this variable instead of the current Message , however the headers from the Message will still be used as well. In other words, the variable is used instead of the message body, but everything else is as usual.
 */
export type VariableReceive2 = string;
/**
 * To use a variable as the source for the message body to send. This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using send variable then the message body is taken from this variable instead of the current message, however the headers from the message will still be used as well. In other words, the variable is used instead of the message body, but everything else is as usual.
 */
export type VariableSend1 = string;
/**
 * Represents a Ua tokenizer for AI.
 */
export type SpecializedTokenizerForAIApplications =
    | {
    langChain4jCharacterTokenizer: LangChain4JTokenizerWithCharacterSplitter;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    langChain4jLineTokenizer: LangChain4JTokenizerDefinition;
    [k: string]: unknown;
}
    | {
    langChain4jParagraphTokenizer: LangChain4JTokenizerWithParagraphSplitter;
    [k: string]: unknown;
}
    | {
    langChain4jSentenceTokenizer: LangChain4JTokenizerWithSentenceSplitter;
    [k: string]: unknown;
}
    | {
    langChain4jWordTokenizer: LangChain4JTokenizerWithWordSplitter;
    [k: string]: unknown;
};
/**
 * The id of this node
 */
export type Id122 = string;
/**
 * Sets the maximum number of tokens that can overlap in each segment
 */
export type MaxOverlap = number;
/**
 * Sets the maximum number of tokens on each segment
 */
export type MaxTokens = number;
/**
 * Sets the tokenizer type
 */
export type TokenizerType = "OPEN_AI" | "AZURE" | "QWEN";
/**
 * The id of this node
 */
export type Id123 = string;
/**
 * Sets the maximum number of tokens that can overlap in each segment
 */
export type MaxOverlap1 = number;
/**
 * Sets the maximum number of tokens on each segment
 */
export type MaxTokens1 = number;
/**
 * Sets the tokenizer type
 */
export type TokenizerType1 = "OPEN_AI" | "AZURE" | "QWEN";
/**
 * The id of this node
 */
export type Id124 = string;
/**
 * Sets the maximum number of tokens that can overlap in each segment
 */
export type MaxOverlap2 = number;
/**
 * Sets the maximum number of tokens on each segment
 */
export type MaxTokens2 = number;
/**
 * Sets the tokenizer type
 */
export type TokenizerType2 = "OPEN_AI" | "AZURE" | "QWEN";
/**
 * The id of this node
 */
export type Id125 = string;
/**
 * Sets the maximum number of tokens that can overlap in each segment
 */
export type MaxOverlap3 = number;
/**
 * Sets the maximum number of tokens on each segment
 */
export type MaxTokens3 = number;
/**
 * Sets the tokenizer type
 */
export type TokenizerType3 = "OPEN_AI" | "AZURE" | "QWEN";
/**
 * Sets the description of this node
 */
export type Description37 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled37 = boolean;
/**
 * Sets the id of this node
 */
export type Id126 = string;
/**
 * Sets a reference to use for lookup the policy in the registry.
 */
export type Ref9 = string;
/**
 * Transforms the message body based on an expression
 */
export type Transform =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition21;
    [k: string]: unknown;
};
/**
 * Expression to return the transformed message body (the new message body to use)
 */
export type ExpressionDefinition21 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description38 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled38 = boolean;
/**
 * Sets the id of this node
 */
export type Id127 = string;
/**
 * Converts the message data received from the wire into a format that Apache Ua processors can consume
 */
export type Unmarshal =
    | {
    asn1: ASN1File;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    avro: Avro;
    [k: string]: unknown;
}
    | {
    barcode: Barcode;
    [k: string]: unknown;
}
    | {
    base64: Base64;
    [k: string]: unknown;
}
    | {
    beanio: BeanIO;
    [k: string]: unknown;
}
    | {
    bindy: Bindy;
    [k: string]: unknown;
}
    | {
    cbor: CBOR;
    [k: string]: unknown;
}
    | {
    crypto: CryptoJavaCryptographicExtension;
    [k: string]: unknown;
}
    | {
    csv: CSV;
    [k: string]: unknown;
}
    | {
    custom: Custom;
    [k: string]: unknown;
}
    | {
    fhirJson: FHIRJSon;
    [k: string]: unknown;
}
    | {
    fhirXml: FHIRXML;
    [k: string]: unknown;
}
    | {
    flatpack: Flatpack;
    [k: string]: unknown;
}
    | {
    fury: Fury;
    [k: string]: unknown;
}
    | {
    grok: Grok;
    [k: string]: unknown;
}
    | {
    gzipDeflater: GZipDeflater;
    [k: string]: unknown;
}
    | {
    hl7: HL7;
    [k: string]: unknown;
}
    | {
    ical: ICal;
    [k: string]: unknown;
}
    | {
    jacksonXml: JacksonXML;
    [k: string]: unknown;
}
    | {
    jaxb: JAXB;
    [k: string]: unknown;
}
    | {
    json: JSon;
    [k: string]: unknown;
}
    | {
    jsonApi: JSonApi;
    [k: string]: unknown;
}
    | {
    lzf: LZFDeflateCompression;
    [k: string]: unknown;
}
    | {
    mimeMultipart: MIMEMultipart;
    [k: string]: unknown;
}
    | {
    parquetAvro: ParquetFile;
    [k: string]: unknown;
}
    | {
    pgp: PGP;
    [k: string]: unknown;
}
    | {
    protobuf: Protobuf;
    [k: string]: unknown;
}
    | {
    rss: RSS;
    [k: string]: unknown;
}
    | {
    smooks: Smooks;
    [k: string]: unknown;
}
    | {
    soap: SOAP;
    [k: string]: unknown;
}
    | {
    swiftMt: SWIFTMT;
    [k: string]: unknown;
}
    | {
    swiftMx: SWIFTMX;
    [k: string]: unknown;
}
    | {
    syslog: Syslog;
    [k: string]: unknown;
}
    | {
    tarFile: TarFile;
    [k: string]: unknown;
}
    | {
    thrift: Thrift;
    [k: string]: unknown;
}
    | {
    tidyMarkup: TidyMarkup;
    [k: string]: unknown;
}
    | {
    univocityCsv: UniVocityCSV;
    [k: string]: unknown;
}
    | {
    univocityFixed: UniVocityFixedLength;
    [k: string]: unknown;
}
    | {
    univocityTsv: UniVocityTSV;
    [k: string]: unknown;
}
    | {
    xmlSecurity: XMLSecurity;
    [k: string]: unknown;
}
    | {
    yaml: YAML;
    [k: string]: unknown;
}
    | {
    zipDeflater: ZipDeflater;
    [k: string]: unknown;
}
    | {
    zipFile: ZipFile;
    [k: string]: unknown;
};
/**
 * Validates a message based on an expression
 */
export type Validate2 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition22;
    [k: string]: unknown;
};
/**
 * Expression to use for validation as a predicate. The expression should return either true or false. If returning false the message is invalid and an exception is thrown.
 */
export type ExpressionDefinition22 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Predicate to determine if the message should be sent or not to the endpoint, when using interceptSentToEndpoint.
 */
export type WhenSkipSendToEndpoint =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition23;
    [k: string]: unknown;
};
/**
 * Expression used as the predicate to evaluate whether the message should be sent or not to the endpoint
 */
export type ExpressionDefinition23 =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Whether to allow components to optimise toD if they are org.apache.camel.spi.SendDynamicAware .
 */
export type AllowOptimisedComponents1 = boolean;
/**
 * Whether to auto startup components when toD is starting up.
 */
export type AutoStartComponents1 = boolean;
/**
 * Sets the maximum size used by the org.apache.camel.spi.ProducerCache which is used to cache and reuse producers when using this recipient list, when uris are reused. Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic endpoint is unique then its best to turn off caching by setting this to -1, which allows Ua to not cache both the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use. This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches. However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on the default size (1000). If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help reduce memory usage to avoid storing too many non frequent used producers.
 */
export type CacheSize1 = number;
/**
 * Uses a copy of the original exchange
 */
export type Copy = boolean;
/**
 * Sets the description of this node
 */
export type Description39 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled39 = boolean;
/**
 * Whether the uri is dynamic or static. If the uri is dynamic then the simple language is used to evaluate a dynamic uri to use as the wire-tap destination, for each incoming message. This works similar to how the toD EIP pattern works. If static then the uri is used as-is as the wire-tap destination.
 */
export type DynamicUri = boolean;
/**
 * Uses a custom thread pool
 */
export type ExecutorService3 = string;
/**
 * Sets the id of this node
 */
export type Id128 = string;
/**
 * Whether to ignore invalid endpoint URIs and skip sending the message.
 */
export type IgnoreInvalidEndpoint1 = boolean;
/**
 * Uses the Processor when preparing the org.apache.camel.Exchange to be sent. This can be used to deep-clone messages that should be sent, or any custom logic needed before the exchange is sent.
 */
export type OnPrepare1 = string;
/**
 * Sets the optional ExchangePattern used to invoke this endpoint
 */
export type Pattern6 = "InOnly" | "InOut";
/**
 * The uri of the endpoint to send to. The uri can be dynamic computed using the org.apache.camel.language.simple.SimpleLanguage expression.
 */
export type Uri3 = string;
/**
 * To use a variable as the source for the message body to send. This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using send variable then the message body is taken from this variable instead of the current Message , however the headers from the Message will still be used as well. In other words, the variable is used instead of the message body, but everything else is as usual.
 */
export type VariableReceive3 = string;
/**
 * To use a variable as the source for the message body to send. This makes it handy to use variables for user data and to easily control what data to use for sending and receiving. Important: When using send variable then the message body is taken from this variable instead of the current message, however the headers from the message will still be used as well. In other words, the variable is used instead of the message body, but everything else is as usual.
 */
export type VariableSend2 = string;
/**
 * To call remote services
 */
export type ServiceCall =
    | string
    | (
    | (
    | {
    blacklistServiceFilter: BlacklistServiceFilter;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    combinedServiceFilter: CombinedServiceFilter;
    [k: string]: unknown;
}
    | {
    customServiceFilter: CustomServiceFilter;
    [k: string]: unknown;
}
    | {
    healthyServiceFilter: HealthyServiceFilter;
    [k: string]: unknown;
}
    | {
    passThroughServiceFilter: PassThroughServiceFilter;
    [k: string]: unknown;
}
    )
    | (
    | {
    cachingServiceDiscovery: CachingServiceDiscovery;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    combinedServiceDiscovery: CombinedServiceDiscovery;
    [k: string]: unknown;
}
    | {
    consulServiceDiscovery: ConsulServiceDiscovery;
    [k: string]: unknown;
}
    | {
    dnsServiceDiscovery: DnsServiceDiscovery;
    [k: string]: unknown;
}
    | {
    kubernetesServiceDiscovery: KubernetesServiceDiscovery;
    [k: string]: unknown;
}
    | {
    staticServiceDiscovery: StaticServiceDiscovery;
    [k: string]: unknown;
}
    | {
    zookeeperServiceDiscovery: ZookeeperServiceDiscovery;
    [k: string]: unknown;
}
    )
    | (
    | {
    defaultLoadBalancer: DefaultLoadBalancer;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    )
    | (
    | {
    expression: Expression24;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    )
    );
/**
 * The id of this node
 */
export type Id129 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties1 = Property[];
/**
 * Sets the server blacklist. Each entry can be a list of servers separated by comma in the format: servicehost:port,servicehost2:port,servicehost3:port
 */
export type Servers = string[];
/**
 * The id of this node
 */
export type Id130 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties2 = Property[];
/**
 * Reference of a ServiceFilter
 */
export type Ref10 = string;
/**
 * The id of this node
 */
export type Id131 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties3 = Property[];
/**
 * The id of this node
 */
export type Id132 = string;
/**
 * The id of this node
 */
export type Id133 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties4 = Property[];
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties5 = Property[];
/**
 */
export type CachingServiceDiscovery =
    | {
    combinedServiceDiscovery: CombinedServiceDiscovery;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    consulServiceDiscovery: ConsulServiceDiscovery;
    [k: string]: unknown;
}
    | {
    dnsServiceDiscovery: DnsServiceDiscovery;
    [k: string]: unknown;
}
    | {
    kubernetesServiceDiscovery: KubernetesServiceDiscovery;
    [k: string]: unknown;
}
    | {
    staticServiceDiscovery: StaticServiceDiscovery;
    [k: string]: unknown;
};
/**
 * Sets the ACL token to be used with Consul
 */
export type AclToken = string;
/**
 * The seconds to wait for a watch event, default 10 seconds
 */
export type BlockSeconds = number & string;
/**
 * Connect timeout for OkHttpClient
 */
export type ConnectTimeoutMillis = number;
/**
 * The data center
 */
export type Datacenter = string;
/**
 * The id of this node
 */
export type Id134 = string;
/**
 * Sets the password to be used for basic authentication
 */
export type Password1 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties6 = Property[];
/**
 * Read timeout for OkHttpClient
 */
export type ReadTimeoutMillis = number;
/**
 * The Consul agent URL
 */
export type Url = string;
/**
 * Sets the username to be used for basic authentication
 */
export type UserName = string;
/**
 * Write timeout for OkHttpClient
 */
export type WriteTimeoutMillis = number;
/**
 * The domain name;
 */
export type Domain = string;
/**
 * The id of this node
 */
export type Id135 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties7 = Property[];
/**
 * The transport protocol of the desired service.
 */
export type Proto = string;
/**
 * The id of this node
 */
export type Id136 = string;
/**
 * Sets the API version when using client lookup
 */
export type ApiVersion = string;
/**
 * Sets the Certificate Authority data when using client lookup
 */
export type CaCertData = string;
/**
 * Sets the Certificate Authority data that are loaded from the file when using client lookup
 */
export type CaCertFile = string;
/**
 * Sets the Client Certificate data when using client lookup
 */
export type ClientCertData = string;
/**
 * Sets the Client Certificate data that are loaded from the file when using client lookup
 */
export type ClientCertFile = string;
/**
 * Sets the Client Keystore algorithm, such as RSA when using client lookup
 */
export type ClientKeyAlgo = string;
/**
 * Sets the Client Keystore data when using client lookup
 */
export type ClientKeyData = string;
/**
 * Sets the Client Keystore data that are loaded from the file when using client lookup
 */
export type ClientKeyFile = string;
/**
 * Sets the Client Keystore passphrase when using client lookup
 */
export type ClientKeyPassphrase = string;
/**
 * Sets the DNS domain to use for DNS lookup.
 */
export type DnsDomain = string;
/**
 * The id of this node
 */
export type Id137 = string;
/**
 * How to perform service lookup. Possible values: client, dns, environment. When using client, then the client queries the kubernetes master to obtain a list of active pods that provides the service, and then random (or round robin) select a pod. When using dns the service name is resolved as name.namespace.svc.dnsDomain. When using dnssrv the service name is resolved with SRV query for _._...svc... When using environment then environment variables are used to lookup the service. By default environment is used.
 */
export type Lookup = "environment" | "dns" | "client";
/**
 * Sets the URL to the master when using client lookup
 */
export type MasterUrl = string;
/**
 * Sets the namespace to use. Will by default use namespace from the ENV variable KUBERNETES_MASTER.
 */
export type Namespace3 = string;
/**
 * Sets the OAUTH token for authentication (instead of username/password) when using client lookup
 */
export type OauthToken = string;
/**
 * Sets the password for authentication when using client lookup
 */
export type Password2 = string;
/**
 * Sets the Port Name to use for DNS/DNSSRV lookup.
 */
export type PortName = string;
/**
 * Sets the Port Protocol to use for DNS/DNSSRV lookup.
 */
export type PortProtocol = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties8 = Property[];
/**
 * Sets whether to turn on trust certificate check when using client lookup
 */
export type TrustCerts = boolean;
/**
 * Sets the username for authentication when using client lookup
 */
export type Username = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties9 = Property[];
/**
 * The id of this node
 */
export type Id138 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties10 = Property[];
/**
 * Sets the server list. Each entry can be a list of servers separated by comma in the format: servicehost:port,servicehost2:port,servicehost3:port
 */
export type Servers1 = string[];
/**
 * Set the base path to store in ZK
 */
export type BasePath = string;
/**
 * Connection timeout.
 */
export type ConnectionTimeout = string;
/**
 * The id of this node
 */
export type Id139 = string;
/**
 * As ZooKeeper is a shared space, users of a given cluster should stay within a pre-defined namespace. If a namespace is set here, all paths will get pre-pended with the namespace
 */
export type Namespace4 = string;
/**
 * A comma separate list of servers to connect to in the form host:port
 */
export type Nodes = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties11 = Property[];
/**
 * Initial amount of time to wait between retries.
 */
export type ReconnectBaseSleepTime = string;
/**
 * Max number of times to retry
 */
export type ReconnectMaxRetries = string;
/**
 * Max time in ms to sleep on each retry
 */
export type ReconnectMaxSleepTime = string;
/**
 * Session timeout.
 */
export type SessionTimeout = string;
/**
 * The id of this node
 */
export type Id140 = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties12 = Property[];
/**
 * The header that holds the service host information, default ServiceCallConstants.SERVICE_HOST
 */
export type HostHeader = string;
/**
 * The id of this node
 */
export type Id141 = string;
/**
 * The header that holds the service port information, default ServiceCallConstants.SERVICE_PORT
 */
export type PortHeader = string;
/**
 * Set client properties to use. These properties are specific to what service call implementation are in use. For example if using a different one, then the client properties are defined according to the specific service in use.
 */
export type Properties13 = Property[];
/**
 * Sets the description of this node
 */
export type Description40 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled40 = boolean;
/**
 * Sets the id of this node
 */
export type Id142 = string;
/**
 * Intercepts incoming messages
 */
export type InterceptFrom =
    | string
    | {
    description?: Description41;
    disabled?: Disabled41;
    id?: Id143;
    steps?: ProcessorDefinition[];
    uri?: Uri4;
};
/**
 * Sets the description of this node
 */
export type Description41 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled41 = boolean;
/**
 * Sets the id of this node
 */
export type Id143 = string;
/**
 * Intercept incoming messages from the uri or uri pattern. If this option is not configured, then all incoming messages is intercepted.
 */
export type Uri4 = string;
/**
 * Intercepts messages being sent to an endpoint
 */
export type InterceptSendToEndpoint =
    | string
    | {
    afterUri?: AfterUri;
    description?: Description42;
    disabled?: Disabled42;
    id?: Id144;
    skipSendToOriginalEndpoint?: SkipSendToOriginalEndpoint;
    steps?: ProcessorDefinition[];
    uri?: Uri5;
};
/**
 * After sending to the endpoint then send the message to this uri which allows to process its result.
 */
export type AfterUri = string;
/**
 * Sets the description of this node
 */
export type Description42 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled42 = boolean;
/**
 * Sets the id of this node
 */
export type Id144 = string;
/**
 * If set to true then the message is not sent to the original endpoint. By default (false) the message is both intercepted and then sent to the original endpoint.
 */
export type SkipSendToOriginalEndpoint = string;
/**
 * Intercept sending to the uri or uri pattern.
 */
export type Uri5 = string;
/**
 * Sets the description of this node
 */
export type Description43 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled43 = boolean;
/**
 * To use a custom Thread Pool to be used for parallel processing. Notice if you set this option, then parallel processing is automatic implied, and you do not have to enable that option as well.
 */
export type ExecutorService4 = string;
/**
 * Sets the id of this node
 */
export type Id145 = string;
/**
 * Sets the on completion mode. The default value is AfterConsumer
 */
export type Mode1 = "AfterConsumer" | "BeforeConsumer";
/**
 * Will only synchronize when the org.apache.camel.Exchange completed successfully (no errors).
 */
export type OnCompleteOnly = boolean;
/**
 * Will only synchronize when the org.apache.camel.Exchange ended with failure (exception or FAULT message).
 */
export type OnFailureOnly = boolean;
/**
 * Triggers a workflow when the expression evaluates to true
 */
export type When2 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition1;
    [k: string]: unknown;
};
/**
 * If enabled then the on completion process will run asynchronously by a separate thread from a thread pool. By default this is false, meaning the on completion process will run synchronously using the same caller thread as from the workflow.
 */
export type ParallelProcessing2 = boolean;
/**
 * Will use the original input message body when an org.apache.camel.Exchange for this on completion. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the split message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalMessage4 = boolean;
/**
 * Sets whether the exchange should handle and continue routing from the point of failure. If this option is enabled then its considered handled as well.
 */
export type Continued =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description44 = string;
/**
 * Whether to disable this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled44 = boolean;
/**
 * A set of exceptions to react upon.
 */
export type Exception2 = string[];
/**
 * Sets whether the exchange should be marked as handled or not.
 */
export type Handled =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Sets the id of this node
 */
export type Id146 = string;
/**
 * Sets a reference to a processor that should be processed just after an exception occurred. Can be used to perform custom logging about the occurred exception at the exact time it happened. Important: Any exception thrown from this processor will be ignored.
 */
export type OnExceptionOccurredRef4 = string;
/**
 * Sets a reference to a processor that should be processed before a redelivery attempt. Can be used to change the org.apache.camel.Exchange before its being redelivered.
 */
export type OnRedeliveryRef4 = string;
/**
 * Triggers a workflow when the expression evaluates to true
 */
export type When3 =
    | ExpressionDefinition
    | {
    [k: string]: unknown;
}
    | {
    expression: ExpressionDefinition1;
    [k: string]: unknown;
};
/**
 * Sets a reference to a redelivery policy to lookup in the org.apache.camel.spi.Registry to be used.
 */
export type RedeliveryPolicyRef4 = string;
/**
 * Sets the retry while predicate. Will continue retrying until predicate returns false.
 */
export type RetryWhile =
    | {
    constant: Constant;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    csimple: CSimple;
    [k: string]: unknown;
}
    | {
    datasonnet: DataSonnet;
    [k: string]: unknown;
}
    | {
    exchangeProperty: ExchangeProperty;
    [k: string]: unknown;
}
    | {
    groovy: Groovy;
    [k: string]: unknown;
}
    | {
    header: Header1;
    [k: string]: unknown;
}
    | {
    hl7terser: HL7Terser;
    [k: string]: unknown;
}
    | {
    java: Java;
    [k: string]: unknown;
}
    | {
    joor: JOOR;
    [k: string]: unknown;
}
    | {
    jq: JQ;
    [k: string]: unknown;
}
    | {
    js: JavaScript;
    [k: string]: unknown;
}
    | {
    jsonpath: JSONPath;
    [k: string]: unknown;
}
    | {
    language: Language;
    [k: string]: unknown;
}
    | {
    method: BeanMethod;
    [k: string]: unknown;
}
    | {
    mvel: MVEL;
    [k: string]: unknown;
}
    | {
    ognl: OGNL;
    [k: string]: unknown;
}
    | {
    python: Python;
    [k: string]: unknown;
}
    | {
    ref: Ref3;
    [k: string]: unknown;
}
    | {
    simple: Simple;
    [k: string]: unknown;
}
    | {
    spel: SpEL;
    [k: string]: unknown;
}
    | {
    tokenize: Tokenize;
    [k: string]: unknown;
}
    | {
    variable: Variable;
    [k: string]: unknown;
}
    | {
    wasm: Wasm;
    [k: string]: unknown;
}
    | {
    xpath: XPath;
    [k: string]: unknown;
}
    | {
    xquery: XQuery;
    [k: string]: unknown;
}
    | {
    xtokenize: XMLTokenize;
    [k: string]: unknown;
};
/**
 * Will use the original input org.apache.camel.Message body (original body only) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the split message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalBody4 = boolean;
/**
 * Will use the original input org.apache.camel.Message (original body and headers) when an org.apache.camel.Exchange is moved to the dead letter queue. Notice: this only applies when all redeliveries attempt have failed and the org.apache.camel.Exchange is doomed for failure. Instead of using the current inprogress org.apache.camel.Exchange IN message we use the original IN message instead. This allows you to store the original input in the dead letter queue instead of the inprogress snapshot of the IN message. For instance if you workflow transform the IN body during routing and then failed. With the original exchange store in the dead letter queue it might be easier to manually re submit the org.apache.camel.Exchange again as the IN message is the same as when Ua received it. So you should be able to send the org.apache.camel.Exchange to the same input. The difference between useOriginalMessage and useOriginalBody is that the former includes both the original body and headers, where as the latter only includes the original body. You can use the latter to enrich the message with custom headers and include the original message body. The former wont let you do this, as its using the original message body and headers as they are. You cannot enable both useOriginalMessage and useOriginalBody. The original input message is defensively copied, and the copied message body is converted to org.apache.camel.StreamCache if possible (stream caching is enabled, can be disabled globally or on the original workflow), to ensure the body can be read when the original message is being used later. If the body is converted to org.apache.camel.StreamCache then the message body on the current org.apache.camel.Exchange is replaced with the org.apache.camel.StreamCache body. If the body is not converted to org.apache.camel.StreamCache then the body will not be able to re-read when accessed later. Important: The original input means the input message that are bounded by the current org.apache.camel.spi.UnitOfWork . An unit of work typically spans one workflow, or multiple workflows if they are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary for the messages in their sub-workflow (eg the split message); however these EIPs have an option named shareUnitOfWork which allows to combine with the parent unit of work in regard to error handling and therefore use the parent original message. By default this feature is off.
 */
export type UseOriginalMessage5 = boolean;
/**
 * Ua error handling.
 */
export type ErrorHandler =
    | {
    deadLetterChannel: DeadLetterChannel;
    [k: string]: unknown;
}
    | {
    [k: string]: unknown;
}
    | {
    defaultErrorHandler: DefaultErrorHandler;
    [k: string]: unknown;
}
    | {
    jtaTransactionErrorHandler: JtaTransactionErrorHandler;
    [k: string]: unknown;
}
    | {
    noErrorHandler: NoErrorHandler;
    [k: string]: unknown;
}
    | {
    refErrorHandler: RefErrorHandler;
    [k: string]: unknown;
}
    | {
    springTransactionErrorHandler: SpringTransactionErrorHandler;
    [k: string]: unknown;
};
/**
 * Sets the description of this node
 */
export type Description45 = string;
/**
 * Sets the id of this node
 */
export type Id147 = string;
/**
 * The input type URN.
 */
export type Urn = string;
/**
 * Whether if validation is required for this input type.
 */
export type Validate3 = boolean;
/**
 * Sets the description of this node
 */
export type Description46 = string;
/**
 * Sets the id of this node
 */
export type Id148 = string;
/**
 * Set output type URN.
 */
export type Urn1 = string;
/**
 * Whether if validation is required for this output type.
 */
export type Validate4 = boolean;
/**
 * Default value of the parameter. If a default value is provided then the parameter is implied not to be required.
 */
export type DefaultValue = string;
/**
 * Description of the parameter
 */
export type Description47 = string;
/**
 * The name of the parameter
 */
export type Name7 = string;
/**
 * Whether the parameter is required or not. A parameter is required unless this option is set to false or a default value has been configured.
 */
export type Required = boolean;
/**
 * The name of the parameter
 */
export type Name8 = string;
/**
 * The value of the parameter.
 */
export type Value2 = string;
/**
 * The name of the Ua component to use as the REST API. If no API Component has been explicit configured, then Ua will lookup if there is a Ua component responsible for servicing and generating the REST API documentation, or if a org.apache.camel.spi.RestApiProcessorFactory is registered in the registry. If either one is found, then that is being used.
 */
export type ApiComponent = "openapi" | "swagger";
/**
 * Sets a leading context-path the REST API will be using. This can be used when using components such as camel-servlet where the deployed web application is deployed using a context-path.
 */
export type ApiContextPath = string;
/**
 * Sets the workflow id to use for the workflow that services the REST API. The workflow will by default use an auto assigned workflow id.
 */
export type ApiContextWorkflowId = string;
/**
 * To use a specific hostname for the API documentation (such as swagger or openapi) This can be used to override the generated host with this configured hostname
 */
export type ApiHost = string;
/**
 * Property key
 */
export type Key2 = string;
/**
 * Property value
 */
export type Value3 = string;
/**
 * Allows to configure as many additional properties for the api documentation. For example set property api.title to my cool stuff
 */
export type ApiProperty = RestProperty[];
/**
 * Whether vendor extension is enabled in the Rest APIs. If enabled then Ua will include additional information as vendor extension (eg keys starting with x-) such as workflow ids, class names etc. Not all 3rd party API gateways and tools supports vendor-extensions when importing your API docs.
 */
export type ApiVendorExtension = boolean;
/**
 * Sets the binding mode to use. The default value is off
 */
export type BindingMode = "auto" | "json" | "json_xml" | "off" | "xml";
/**
 * Package name to use as base (offset) for classpath scanning of POJO classes are located when using binding mode is enabled for JSon or XML. Multiple package names can be separated by comma.
 */
export type BindingPackageScan = string;
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation = boolean;
/**
 * The Ua Rest component to use for the REST transport (consumer), such as netty-http, jetty, servlet, undertow. If no component has been explicit configured, then Ua will lookup if there is a Ua component that integrates with the Rest DSL, or if a org.apache.camel.spi.RestConsumerFactory is registered in the registry. If either one is found, then that is being used.
 */
export type Component = "platform-http" | "servlet" | "jetty" | "undertow" | "netty-http" | "coap";
/**
 * Allows to configure as many additional properties for the rest component in use.
 */
export type ComponentProperty = RestProperty[];
/**
 * Allows to configure as many additional properties for the rest consumer in use.
 */
export type ConsumerProperty = RestProperty[];
/**
 * Sets a leading context-path the REST services will be using. This can be used when using components such as camel-servlet where the deployed web application is deployed using a context-path. Or for components such as camel-jetty or camel-netty-http that includes a HTTP server.
 */
export type ContextPath2 = string;
/**
 * Allows to configure custom CORS headers.
 */
export type CorsHeaders = RestProperty[];
/**
 * Allows to configure as many additional properties for the data formats in use. For example set property prettyPrint to true to have json outputted in pretty mode. The properties can be prefixed to denote the option is only for either JSON or XML and for either the IN or the OUT. The prefixes are: json.in. json.out. xml.in. xml.out. For example a key with value xml.out.mustBeJAXBElement is only for the XML data format for the outgoing. A key without a prefix is a common key for all situations.
 */
export type DataFormatProperty = RestProperty[];
/**
 * Whether to enable CORS headers in the HTTP response. The default value is false.
 */
export type EnableCORS = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse = boolean;
/**
 * Allows to configure as many additional properties for the rest endpoint in use.
 */
export type EndpointProperty = RestProperty[];
/**
 * The hostname to use for exposing the REST service.
 */
export type Host = string;
/**
 * If no hostname has been explicit configured, then this resolver is used to compute the hostname the REST service will be using.
 */
export type HostNameResolver = "allLocalIp" | "localHostName" | "localIp" | "none";
/**
 * Inline workflows in rest-dsl which are linked using direct endpoints. Each service in Rest DSL is an individual workflow, meaning that you would have at least two workflows per service (rest-dsl, and the workflow linked from rest-dsl). By inlining (default) allows Ua to optimize and inline this as a single workflow, however this requires to use direct endpoints, which must be unique per service. If a workflow is not using direct endpoint then the rest-dsl is not inlined, and will become an individual workflow. This option is default true.
 */
export type InlineWorkflows = boolean;
/**
 * Name of specific json data format to use. By default jackson will be used. Important: This option is only for setting a custom name of the data format, not to refer to an existing data format instance.
 */
export type JsonDataFormat = string;
/**
 * The port number to use for exposing the REST service. Notice if you use servlet component then the port number configured here does not apply, as the port number in use is the actual port number the servlet component is using. eg if using Apache Tomcat its the tomcat http port, if using Apache Karaf its the HTTP service in Karaf that uses port 8181 by default etc. Though in those situations setting the port number here, allows tooling and JMX to know the port number, so its recommended to set the port number to the number that the servlet engine uses.
 */
export type Port = string;
/**
 * Sets the location of the api document the REST producer will use to validate the REST uri and query parameters are valid accordingly to the api document. The location of the api document is loaded from classpath by default, but you can use file: or http: to refer to resources to load from file or http url.
 */
export type ProducerApiDoc = string;
/**
 * Sets the name of the Ua component to use as the REST producer
 */
export type ProducerComponent = "vertx-http" | "http" | "undertow" | "netty-http";
/**
 * The scheme to use for exposing the REST service. Usually http or https is supported. The default value is http
 */
export type Scheme = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do.
 */
export type SkipBindingOnErrorCode = boolean;
/**
 * Whether to use X-Forward headers to set host etc. for OpenApi. This may be needed in special cases involving reverse-proxy and networking going from HTTP to HTTPS etc. Then the proxy can send X-Forward headers (X-Forwarded-Proto) that influences the host names in the OpenAPI schema that camel-openapi-java generates from Rest DSL workflows.
 */
export type UseXForwardHeaders = boolean;
/**
 * Name of specific XML data format to use. By default jaxb will be used. Important: This option is only for setting a custom name of the data format, not to refer to an existing data format instance.
 */
export type XmlDataFormat = string;
/**
 * Whether to include or exclude this rest operation in API documentation. This option will override what may be configured on a parent level. The default value is true.
 */
export type ApiDocs = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is auto
 */
export type BindingMode1 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation1 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes = string;
/**
 * Whether to include or exclude this rest operation in API documentation. The default value is true.
 */
export type ApiDocs1 = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is off
 */
export type BindingMode2 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation2 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes1 = string;
/**
 * Marks this rest operation as deprecated in OpenApi documentation.
 */
export type Deprecated = boolean;
/**
 * Sets the description of this node
 */
export type Description48 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled45 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS1 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse1 = boolean;
/**
 * Sets the id of this node
 */
export type Id149 = string;
/**
 * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what may be configured on a parent level The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type OutType = string;
/**
 * A single value
 */
export type Value4 =
    | string
    | {
    value?: Value5;
};
/**
 * Property value
 */
export type Value5 = string;
/**
 * Sets the parameter list of allowable values (enum).
 */
export type AllowableValues = Value4[];
/**
 * Sets the parameter array type. Required if data type is array. Describes the type of items in the array.
 */
export type ArrayType = string;
/**
 * Sets the parameter collection format.
 */
export type CollectionFormat = "csv" | "multi" | "pipes" | "ssv" | "tsv";
/**
 * Sets the parameter data format.
 */
export type DataFormat = string;
/**
 * Sets the parameter data type.
 */
export type DataType = string;
/**
 * Sets the parameter default value.
 */
export type DefaultValue1 = string;
/**
 * Sets the parameter description.
 */
export type Description49 = string;
/**
 * Sets the parameter examples.
 */
export type Examples = RestProperty[];
/**
 * Sets the parameter name.
 */
export type Name9 = string;
/**
 * Sets the parameter required flag.
 */
export type Required1 = boolean;
/**
 * Sets the parameter type.
 */
export type Type6 = "body" | "formData" | "header" | "path" | "query";
/**
 * The path mapping URIs of this REST operation such as /{id}.
 */
export type Path = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces = string;
/**
 * The response code such as a HTTP status code
 */
export type Code = string;
/**
 * Examples of response messages
 */
export type Examples1 = RestProperty[];
/**
 * Sets the parameter list of allowable values.
 */
export type AllowableValues1 = Value4[];
/**
 * Sets the parameter array type. Required if data type is array. Describes the type of items in the array.
 */
export type ArrayType1 = string;
/**
 * Sets the parameter collection format.
 */
export type CollectionFormat1 = "csv" | "multi" | "pipes" | "ssv" | "tsv";
/**
 * Sets the parameter data format.
 */
export type DataFormat1 = string;
/**
 * Sets the header data type.
 */
export type DataType1 = string;
/**
 * Description of the parameter.
 */
export type Description50 = string;
/**
 * Sets the example
 */
export type Example = string;
/**
 * Name of the parameter. This option is mandatory.
 */
export type Name10 = string;
/**
 * Adds a response header
 */
export type Header2 = ResponseHeader[];
/**
 * The response message (description)
 */
export type Message3 = string;
/**
 * The response model
 */
export type ResponseModel = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId = string;
/**
 * Key used to refer to this security definition
 */
export type Key3 = string;
/**
 * The scopes to allow (separate multiple scopes by comma)
 */
export type Scopes = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode1 = boolean;
/**
 * Whether stream caching is enabled on this rest operation.
 */
export type StreamCache = boolean;
/**
 * Sends the message to a static endpoint
 */
export type To1 =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the class name to use for binding from input to POJO for the incoming data This option will override what may be configured on a parent level. The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type Type7 = string;
/**
 * Sets the description of this node
 */
export type Description51 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled46 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS2 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse2 = boolean;
/**
 * Whether to include or exclude this rest operation in API documentation. The default value is true.
 */
export type ApiDocs2 = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is off
 */
export type BindingMode3 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation3 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes2 = string;
/**
 * Marks this rest operation as deprecated in OpenApi documentation.
 */
export type Deprecated1 = boolean;
/**
 * Sets the description of this node
 */
export type Description52 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled47 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS3 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse3 = boolean;
/**
 * Sets the id of this node
 */
export type Id150 = string;
/**
 * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what may be configured on a parent level The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type OutType1 = string;
/**
 * The path mapping URIs of this REST operation such as /{id}.
 */
export type Path1 = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces1 = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId1 = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode2 = boolean;
/**
 * Whether stream caching is enabled on this rest operation.
 */
export type StreamCache1 = boolean;
/**
 * Sends the message to a static endpoint
 */
export type To2 =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the class name to use for binding from input to POJO for the incoming data This option will override what may be configured on a parent level. The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type Type8 = string;
/**
 * Whether to include or exclude this rest operation in API documentation. The default value is true.
 */
export type ApiDocs3 = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is off
 */
export type BindingMode4 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation4 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes3 = string;
/**
 * Marks this rest operation as deprecated in OpenApi documentation.
 */
export type Deprecated2 = boolean;
/**
 * Sets the description of this node
 */
export type Description53 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled48 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS4 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse4 = boolean;
/**
 * Sets the id of this node
 */
export type Id151 = string;
/**
 * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what may be configured on a parent level The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type OutType2 = string;
/**
 * The path mapping URIs of this REST operation such as /{id}.
 */
export type Path2 = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces2 = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId2 = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode3 = boolean;
/**
 * Whether stream caching is enabled on this rest operation.
 */
export type StreamCache2 = boolean;
/**
 * Sends the message to a static endpoint
 */
export type To3 =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the class name to use for binding from input to POJO for the incoming data This option will override what may be configured on a parent level. The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type Type9 = string;
/**
 * Sets the id of this node
 */
export type Id152 = string;
/**
 * Sets the description of this node
 */
export type Description54 = string;
/**
 * Whether to disable all the REST services from the OpenAPI contract from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled49 = boolean;
/**
 * Sets the id of this node
 */
export type Id153 = string;
/**
 * Whether to fail, ignore or return a mock response for OpenAPI operations that are not mapped to a corresponding workflow.
 */
export type MissingOperation = "fail" | "ignore" | "mock";
/**
 * Used for inclusive filtering of mock data from directories. The pattern is using Ant-path style pattern. Multiple patterns can be specified separated by comma.
 */
export type MockIncludePattern = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId3 = string;
/**
 * Path to the OpenApi specification file.
 */
export type Specification = string;
/**
 * Whether to include or exclude this rest operation in API documentation. The default value is true.
 */
export type ApiDocs4 = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is off
 */
export type BindingMode5 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation5 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes4 = string;
/**
 * Marks this rest operation as deprecated in OpenApi documentation.
 */
export type Deprecated3 = boolean;
/**
 * Sets the description of this node
 */
export type Description55 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled50 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS5 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse5 = boolean;
/**
 * Sets the id of this node
 */
export type Id154 = string;
/**
 * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what may be configured on a parent level The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type OutType3 = string;
/**
 * The path mapping URIs of this REST operation such as /{id}.
 */
export type Path3 = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces3 = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId4 = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode4 = boolean;
/**
 * Whether stream caching is enabled on this rest operation.
 */
export type StreamCache3 = boolean;
/**
 * Sends the message to a static endpoint
 */
export type To4 =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the class name to use for binding from input to POJO for the incoming data This option will override what may be configured on a parent level. The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type Type10 = string;
/**
 * Path of the rest service, such as /foo
 */
export type Path4 = string;
/**
 * Whether to include or exclude this rest operation in API documentation. The default value is true.
 */
export type ApiDocs5 = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is off
 */
export type BindingMode6 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation6 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes5 = string;
/**
 * Marks this rest operation as deprecated in OpenApi documentation.
 */
export type Deprecated4 = boolean;
/**
 * Sets the description of this node
 */
export type Description56 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled51 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS6 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse6 = boolean;
/**
 * Sets the id of this node
 */
export type Id155 = string;
/**
 * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what may be configured on a parent level The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type OutType4 = string;
/**
 * The path mapping URIs of this REST operation such as /{id}.
 */
export type Path5 = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces4 = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId5 = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode5 = boolean;
/**
 * Whether stream caching is enabled on this rest operation.
 */
export type StreamCache4 = boolean;
/**
 * Sends the message to a static endpoint
 */
export type To5 =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the class name to use for binding from input to POJO for the incoming data This option will override what may be configured on a parent level. The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type Type11 = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces5 = string;
/**
 * Whether to include or exclude this rest operation in API documentation. The default value is true.
 */
export type ApiDocs6 = boolean;
/**
 * Sets the binding mode to use. This option will override what may be configured on a parent level The default value is off
 */
export type BindingMode7 = "off" | "auto" | "json" | "xml" | "json_xml";
/**
 * Whether to enable validation of the client request to check: 1) Content-Type header matches what the Rest DSL consumes; returns HTTP Status 415 if validation error. 2) Accept header matches what the Rest DSL produces; returns HTTP Status 406 if validation error. 3) Missing required data (query parameters, HTTP headers, body); returns HTTP Status 400 if validation error. 4) Parsing error of the message body (JSon, XML or Auto binding mode must be enabled); returns HTTP Status 400 if validation error.
 */
export type ClientRequestValidation7 = boolean;
/**
 * To define the content type what the REST service consumes (accept as input), such as application/xml or application/json. This option will override what may be configured on a parent level
 */
export type Consumes6 = string;
/**
 * Marks this rest operation as deprecated in OpenApi documentation.
 */
export type Deprecated5 = boolean;
/**
 * Sets the description of this node
 */
export type Description57 = string;
/**
 * Whether to disable this REST service from the workflow during build time. Once an REST service has been disabled then it cannot be enabled later at runtime.
 */
export type Disabled52 = boolean;
/**
 * Whether to enable CORS headers in the HTTP response. This option will override what may be configured on a parent level The default value is false.
 */
export type EnableCORS7 = boolean;
/**
 * Whether to return HTTP 204 with an empty body when a response contains an empty JSON object or XML root object. The default value is false.
 */
export type EnableNoContentResponse7 = boolean;
/**
 * Sets the id of this node
 */
export type Id156 = string;
/**
 * Sets the class name to use for binding from POJO to output for the outgoing data This option will override what may be configured on a parent level The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type OutType5 = string;
/**
 * The path mapping URIs of this REST operation such as /{id}.
 */
export type Path6 = string;
/**
 * To define the content type what the REST service produces (uses for output), such as application/xml or application/json This option will override what may be configured on a parent level
 */
export type Produces6 = string;
/**
 * Sets the id of the workflow
 */
export type WorkflowId6 = string;
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode6 = boolean;
/**
 * Whether stream caching is enabled on this rest operation.
 */
export type StreamCache5 = boolean;
/**
 * Sends the message to a static endpoint
 */
export type To6 =
    | string
    | {
    description?: Description35;
    disabled?: Disabled35;
    id?: Id120;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern4;
    uri?: Uri1;
    variableReceive?: VariableReceive1;
    variableSend?: VariableSend;
};
/**
 * Sets the class name to use for binding from input to POJO for the incoming data This option will override what may be configured on a parent level. The name of the class of the input data. Append a to the end of the name if you want the input to be an array type.
 */
export type Type12 = string;
/**
 * A short description for security scheme.
 */
export type Description58 = string;
/**
 * To use a cookie as the location of the API key.
 */
export type InCookie = boolean;
/**
 * To use header as the location of the API key.
 */
export type InHeader = boolean;
/**
 * To use query parameter as the location of the API key.
 */
export type InQuery = boolean;
/**
 * Key used to refer to this security definition
 */
export type Key4 = string;
/**
 * The name of the header or query parameter to be used.
 */
export type Name11 = string;
/**
 * A short description for security scheme.
 */
export type Description59 = string;
/**
 * Key used to refer to this security definition
 */
export type Key5 = string;
/**
 * A short description for security scheme.
 */
export type Description60 = string;
/**
 * A hint to the client to identify how the bearer token is formatted.
 */
export type Format = string;
/**
 * Key used to refer to this security definition
 */
export type Key6 = string;
/**
 * A short description for security scheme.
 */
export type Description61 = string;
/**
 * Key used to refer to this security definition
 */
export type Key7 = string;
/**
 * The authorization URL to be used for this flow. This SHOULD be in the form of a URL. Required for implicit and access code flows
 */
export type AuthorizationUrl = string;
/**
 * A short description for security scheme.
 */
export type Description62 = string;
/**
 * The flow used by the OAuth2 security scheme. Valid values are implicit, password, application or accessCode.
 */
export type Flow = "implicit" | "password" | "application" | "clientCredentials" | "accessCode" | "authorizationCode";
/**
 * Key used to refer to this security definition
 */
export type Key8 = string;
/**
 * The URL to be used for obtaining refresh tokens. This MUST be in the form of a URL.
 */
export type RefreshUrl = string;
/**
 * The available scopes for an OAuth2 security scheme
 */
export type Scopes1 = RestProperty[];
/**
 * The token URL to be used for this flow. This SHOULD be in the form of a URL. Required for password, application, and access code flows.
 */
export type TokenUrl = string;
/**
 * A short description for security scheme.
 */
export type Description63 = string;
/**
 * Key used to refer to this security definition
 */
export type Key9 = string;
/**
 * OpenId Connect URL to discover OAuth2 configuration values.
 */
export type Url1 = string;
/**
 * Sets the security requirement(s) for all endpoints.
 */
export type SecurityRequirements = RestSecurity[];
/**
 * Whether to skip binding on output if there is a custom HTTP error code header. This allows to build custom error messages that do not bind to json / xml etc, as success messages otherwise will do. This option will override what may be configured on a parent level
 */
export type SkipBindingOnErrorCode7 = boolean;
/**
 * To configure a special tag for the operations within this rest definition.
 */
export type Tag = string;
export type YamlDsl = {
    beans?: BeansDeserializer;
    dataFormats?: DataFormatsDefinitionDeserializer;
    errorHandler?: ErrorHandlerDeserializer;
    from?: WorkflowFromDefinitionDeserializer;
    intercept?: Intercept;
    interceptFrom?: InterceptFrom;
    interceptSendToEndpoint?: InterceptSendToEndpoint;
    onCompletion?: OnCompletion;
    onException?: OnException;
    workflowConfiguration?: WorkflowConfigurationDefinition;
    workflow?: WorkflowDefinition;
    workflowTemplate?: WorkflowTemplateDefinition;
    templatedWorkflow?: TemplatedWorkflowDefinition;
    restConfiguration?: RestConfiguration;
    rest?: Rest;
}[];

/**
 * Define custom beans that can be used in your Ua workflows and in general.
 */
export interface BeanFactory {
    builderClass?: BuilderClass;
    builderMethod?: BuilderMethod;
    constructors?: Constructors;
    destroyMethod?: DestroyMethod;
    factoryBean?: FactoryBean;
    factoryMethod?: FactoryMethod;
    initMethod?: InitMethod;
    name: Name;
    properties?: Properties;
    script?: Script;
    scriptLanguage?: ScriptLanguage;
    type: Type;
}
/**
 * Optional constructor arguments for creating the bean. Arguments correspond to specific index of the constructor argument list, starting from zero.
 */
export interface Constructors {
    [k: string]: unknown;
}
/**
 * Optional properties to set on the created bean.
 */
export interface Properties {
    [k: string]: unknown;
}
/**
 * Configure data formats.
 */
export interface DataFormats {
    asn1?: ASN1File;
    avro?: Avro;
    barcode?: Barcode;
    base64?: Base64;
    beanio?: BeanIO;
    bindy?: Bindy;
    cbor?: CBOR;
    crypto?: CryptoJavaCryptographicExtension;
    csv?: CSV;
    custom?: Custom;
    fhirJson?: FHIRJSon;
    fhirXml?: FHIRXML;
    flatpack?: Flatpack;
    fury?: Fury;
    grok?: Grok;
    gzipDeflater?: GZipDeflater;
    hl7?: HL7;
    ical?: ICal;
    jacksonXml?: JacksonXML;
    jaxb?: JAXB;
    json?: JSon;
    jsonApi?: JSonApi;
    lzf?: LZFDeflateCompression;
    mimeMultipart?: MIMEMultipart;
    parquetAvro?: ParquetFile;
    pgp?: PGP;
    protobuf?: Protobuf;
    rss?: RSS;
    smooks?: Smooks;
    soap?: SOAP;
    swiftMt?: SWIFTMT;
    swiftMx?: SWIFTMX;
    syslog?: Syslog;
    tarFile?: TarFile;
    thrift?: Thrift;
    tidyMarkup?: TidyMarkup;
    univocityCsv?: UniVocityCSV;
    univocityFixed?: UniVocityFixedLength;
    univocityTsv?: UniVocityTSV;
    xmlSecurity?: XMLSecurity;
    yaml?: YAML;
    zipDeflater?: ZipDeflater;
    zipFile?: ZipFile;
}
/**
 * Transform strings to various 1D/2D barcode bitmap formats and back.
 */
export interface Barcode {
    barcodeFormat?: BarcodeFormat;
    height?: Height;
    id?: Id2;
    imageType?: ImageType;
    width?: Width;
}
/**
 * Encode and decode data using Base64.
 */
export interface Base64 {
    id?: Id3;
    lineLength?: LineLength;
    lineSeparator?: LineSeparator;
    urlSafe?: UrlSafe;
}
/**
 * Marshal and unmarshal Java beans to and from flat files (such as CSV, delimited, or fixed length formats).
 */
export interface BeanIO {
    beanReaderErrorHandlerType?: BeanReaderErrorHandlerType;
    encoding?: Encoding;
    id?: Id4;
    ignoreInvalidRecords?: IgnoreInvalidRecords;
    ignoreUnexpectedRecords?: IgnoreUnexpectedRecords;
    ignoreUnidentifiedRecords?: IgnoreUnidentifiedRecords;
    mapping: Mapping;
    streamName: StreamName;
    unmarshalSingleObject?: UnmarshalSingleObject;
}
/**
 * Marshal and unmarshal Java beans from and to flat payloads (such as CSV, delimited, fixed length formats, or FIX messages).
 */
export interface Bindy {
    allowEmptyStream?: AllowEmptyStream;
    classType?: ClassType;
    id?: Id5;
    locale?: Locale;
    type?: Type1;
    unwrapSingleInstance?: UnwrapSingleInstance;
}
/**
 * Unmarshal a CBOR payload to POJO and back.
 */
export interface CBOR {
    allowJmsType?: AllowJmsType1;
    allowUnmarshallType?: AllowUnmarshallType1;
    collectionType?: CollectionType1;
    disableFeatures?: DisableFeatures1;
    enableFeatures?: EnableFeatures1;
    id?: Id6;
    objectMapper?: ObjectMapper1;
    prettyPrint?: PrettyPrint;
    unmarshalType?: UnmarshalType2;
    useDefaultObjectMapper?: UseDefaultObjectMapper1;
    useList?: UseList1;
}
/**
 * Encrypt and decrypt messages using Java Cryptography Extension (JCE).
 */
export interface CryptoJavaCryptographicExtension {
    algorithm?: Algorithm;
    algorithmParameterRef?: AlgorithmParameterRef;
    bufferSize?: BufferSize;
    cryptoProvider?: CryptoProvider;
    id?: Id7;
    initVectorRef?: InitVectorRef;
    inline?: Inline;
    keyRef?: KeyRef;
    macAlgorithm?: MacAlgorithm;
    shouldAppendHMAC?: ShouldAppendHMAC;
}
/**
 * Marshall and unmarshall FHIR objects to/from JSON.
 */
export interface FHIRJSon {
    contentTypeHeader?: ContentTypeHeader1;
    dontEncodeElements?: DontEncodeElements;
    dontStripVersionsFromReferencesAtPaths?: DontStripVersionsFromReferencesAtPaths;
    encodeElements?: EncodeElements;
    encodeElementsAppliesToChildResourcesOnly?: EncodeElementsAppliesToChildResourcesOnly;
    fhirContext?: FhirContext;
    fhirVersion?: FhirVersion;
    forceResourceId?: ForceResourceId;
    id?: Id10;
    omitResourceId?: OmitResourceId;
    overrideResourceIdWithBundleEntryFullUrl?: OverrideResourceIdWithBundleEntryFullUrl;
    parserErrorHandler?: ParserErrorHandler;
    parserOptions?: ParserOptions;
    preferTypes?: PreferTypes;
    prettyPrint?: PrettyPrint1;
    serverBaseUrl?: ServerBaseUrl;
    stripVersionsFromReferences?: StripVersionsFromReferences;
    summaryMode?: SummaryMode;
    suppressNarratives?: SuppressNarratives;
}
/**
 * Marshall and unmarshall FHIR objects to/from XML.
 */
export interface FHIRXML {
    contentTypeHeader?: ContentTypeHeader2;
    dontEncodeElements?: DontEncodeElements1;
    dontStripVersionsFromReferencesAtPaths?: DontStripVersionsFromReferencesAtPaths1;
    encodeElements?: EncodeElements1;
    encodeElementsAppliesToChildResourcesOnly?: EncodeElementsAppliesToChildResourcesOnly1;
    fhirContext?: FhirContext1;
    fhirVersion?: FhirVersion1;
    forceResourceId?: ForceResourceId1;
    id?: Id11;
    omitResourceId?: OmitResourceId1;
    overrideResourceIdWithBundleEntryFullUrl?: OverrideResourceIdWithBundleEntryFullUrl1;
    parserErrorHandler?: ParserErrorHandler1;
    parserOptions?: ParserOptions1;
    preferTypes?: PreferTypes1;
    prettyPrint?: PrettyPrint2;
    serverBaseUrl?: ServerBaseUrl1;
    stripVersionsFromReferences?: StripVersionsFromReferences1;
    summaryMode?: SummaryMode1;
    suppressNarratives?: SuppressNarratives1;
}
/**
 * Marshal and unmarshal Java lists and maps to/from flat files (such as CSV, delimited, or fixed length formats) using Flatpack library.
 */
export interface Flatpack {
    allowShortLines?: AllowShortLines;
    definition?: Definition;
    delimiter?: Delimiter1;
    fixed?: Fixed;
    id?: Id12;
    ignoreExtraColumns?: IgnoreExtraColumns;
    ignoreFirstRecord?: IgnoreFirstRecord;
    parserFactoryRef?: ParserFactoryRef;
    textQualifier?: TextQualifier;
}
/**
 * Serialize and deserialize messages using Apache Fury
 */
export interface Fury {
    allowAutoWiredFury?: AllowAutoWiredFury;
    id?: Id13;
    requireClassRegistration?: RequireClassRegistration;
    threadSafe?: ThreadSafe;
    unmarshalType?: UnmarshalType3;
}
/**
 * Unmarshal unstructured data to objects using Logstash based Grok patterns.
 */
export interface Grok {
    allowMultipleMatchesPerLine?: AllowMultipleMatchesPerLine;
    flattened?: Flattened;
    id?: Id14;
    namedOnly?: NamedOnly;
    pattern: Pattern;
}
/**
 * Compress and decompress messages using java.util.zip.GZIPStream.
 */
export interface GZipDeflater {
    id?: Id15;
}
/**
 * Marshal and unmarshal HL7 (Health Care) model objects using the HL7 MLLP codec.
 */
export interface HL7 {
    id?: Id16;
    validate?: Validate;
}
/**
 * Marshal and unmarshal iCal (.ics) documents to/from model objects.
 */
export interface ICal {
    id?: Id17;
    validating?: Validating;
}
/**
 * Unmarshal an XML payloads to POJOs and back using XMLMapper extension of Jackson.
 */
export interface JacksonXML {
    allowJmsType?: AllowJmsType2;
    allowUnmarshallType?: AllowUnmarshallType2;
    collectionType?: CollectionType2;
    contentTypeHeader?: ContentTypeHeader3;
    disableFeatures?: DisableFeatures2;
    enableFeatures?: EnableFeatures2;
    enableJaxbAnnotationModule?: EnableJaxbAnnotationModule;
    id?: Id18;
    include?: Include1;
    jsonView?: JsonView1;
    moduleClassNames?: ModuleClassNames1;
    moduleRefs?: ModuleRefs1;
    prettyPrint?: PrettyPrint3;
    timezone?: Timezone1;
    unmarshalType?: UnmarshalType4;
    useList?: UseList2;
    xmlMapper?: XmlMapper;
}
/**
 * Unmarshal XML payloads to POJOs and back using JAXB2 XML marshalling standard.
 */
export interface JAXB {
    accessExternalSchemaProtocols?: AccessExternalSchemaProtocols;
    contentTypeHeader?: ContentTypeHeader4;
    contextPath: ContextPath;
    contextPathIsClassName?: ContextPathIsClassName;
    encoding?: Encoding1;
    filterNonXmlChars?: FilterNonXmlChars;
    fragment?: Fragment;
    id?: Id19;
    ignoreJAXBElement?: IgnoreJAXBElement;
    jaxbProviderProperties?: JaxbProviderProperties;
    mustBeJAXBElement?: MustBeJAXBElement;
    namespacePrefixRef?: NamespacePrefixRef;
    noNamespaceSchemaLocation?: NoNamespaceSchemaLocation;
    objectFactory?: ObjectFactory;
    partClass?: PartClass;
    partNamespace?: PartNamespace;
    prettyPrint?: PrettyPrint4;
    schema?: Schema;
    schemaLocation?: SchemaLocation;
    schemaSeverityLevel?: SchemaSeverityLevel;
    xmlStreamWriterWrapper?: XmlStreamWriterWrapper;
}
/**
 * Marshal POJOs to JSON and back.
 */
export interface JSon {
    allowJmsType?: AllowJmsType3;
    allowUnmarshallType?: AllowUnmarshallType3;
    autoDiscoverObjectMapper?: AutoDiscoverObjectMapper1;
    autoDiscoverSchemaResolver?: AutoDiscoverSchemaResolver1;
    collectionType?: CollectionType3;
    combineUnicodeSurrogates?: CombineUnicodeSurrogates;
    contentTypeHeader?: ContentTypeHeader5;
    dateFormatPattern?: DateFormatPattern;
    disableFeatures?: DisableFeatures3;
    enableFeatures?: EnableFeatures3;
    id?: Id20;
    include?: Include2;
    jsonView?: JsonView2;
    library?: Library1;
    moduleClassNames?: ModuleClassNames2;
    moduleRefs?: ModuleRefs2;
    namingStrategy?: NamingStrategy;
    objectMapper?: ObjectMapper2;
    prettyPrint?: PrettyPrint5;
    schemaResolver?: SchemaResolver1;
    timezone?: Timezone2;
    unmarshalType?: UnmarshalType5;
    useDefaultObjectMapper?: UseDefaultObjectMapper2;
    useList?: UseList3;
}
/**
 * Marshal and unmarshal JSON:API resources using JSONAPI-Converter library.
 */
export interface JSonApi {
    dataFormatTypes?: DataFormatTypes;
    id?: Id21;
    mainFormatType?: MainFormatType;
}
/**
 * Compress and decompress streams using LZF deflate algorithm.
 */
export interface LZFDeflateCompression {
    id?: Id22;
    usingParallelCompression?: UsingParallelCompression;
}
/**
 * Marshal Ua messages with attachments into MIME-Multipart messages and back.
 */
export interface MIMEMultipart {
    binaryContent?: BinaryContent;
    headersInline?: HeadersInline;
    id?: Id23;
    includeHeaders?: IncludeHeaders;
    multipartSubType?: MultipartSubType;
    multipartWithoutAttachment?: MultipartWithoutAttachment;
}
/**
 * Encrypt and decrypt messages using Java Cryptographic Extension (JCE) and PGP.
 */
export interface PGP {
    algorithm?: Algorithm1;
    armored?: Armored;
    compressionAlgorithm?: CompressionAlgorithm;
    hashAlgorithm?: HashAlgorithm;
    id?: Id25;
    integrity?: Integrity;
    keyFileName?: KeyFileName;
    keyUserid?: KeyUserid;
    password?: Password;
    provider?: Provider;
    signatureKeyFileName?: SignatureKeyFileName;
    signatureKeyRing?: SignatureKeyRing;
    signatureKeyUserid?: SignatureKeyUserid;
    signaturePassword?: SignaturePassword;
    signatureVerificationOption?: SignatureVerificationOption;
}
/**
 * Transform from ROME SyndFeed Java Objects to XML and vice-versa.
 */
export interface RSS {
    id?: Id27;
}
/**
 * Transform and bind XML as well as non-XML data, including EDI, CSV, JSON, and YAML using Smooks.
 */
export interface Smooks {
    id?: Id28;
    smooksConfig: SmooksConfig;
}
/**
 * Encode and decode SWIFT MX messages.
 */
export interface SWIFTMX {
    id?: Id31;
    readConfigRef?: ReadConfigRef;
    readMessageId?: ReadMessageId;
    writeConfigRef?: WriteConfigRef;
    writeInJson?: WriteInJson1;
}
/**
 * Marshall SyslogMessages to RFC3164 and RFC5424 messages and back.
 */
export interface Syslog {
    id?: Id32;
}
/**
 * Archive files into tarballs or extract files from tarballs.
 */
export interface TarFile {
    allowEmptyDirectory?: AllowEmptyDirectory;
    id?: Id33;
    maxDecompressedSize?: MaxDecompressedSize;
    preservePathElements?: PreservePathElements;
    usingIterator?: UsingIterator1;
}
/**
 * Parse (potentially invalid) HTML into valid HTML or DOM.
 */
export interface TidyMarkup {
    dataObjectType?: DataObjectType;
    id?: Id35;
    omitXmlDeclaration?: OmitXmlDeclaration;
}
/**
 * Marshal and unmarshal Java objects from and to CSV (Comma Separated Values) using UniVocity Parsers.
 */
export interface UniVocityCSV {
    asMap?: AsMap;
    comment?: Comment;
    delimiter?: Delimiter2;
    emptyValue?: EmptyValue;
    headerExtractionEnabled?: HeaderExtractionEnabled;
    headersDisabled?: HeadersDisabled;
    id?: Id36;
    ignoreLeadingWhitespaces?: IgnoreLeadingWhitespaces;
    ignoreTrailingWhitespaces?: IgnoreTrailingWhitespaces;
    lazyLoad?: LazyLoad2;
    lineSeparator?: LineSeparator1;
    normalizedLineSeparator?: NormalizedLineSeparator;
    nullValue?: NullValue;
    numberOfRecordsToRead?: NumberOfRecordsToRead;
    quote?: Quote1;
    quoteAllFields?: QuoteAllFields;
    quoteEscape?: QuoteEscape;
    skipEmptyLines?: SkipEmptyLines;
    univocityHeader?: UniVocityHeader[];
}
/**
 * To configure headers for UniVocity data formats.
 */
export interface UniVocityHeader {
    length?: Length;
    name?: Name1;
}
/**
 * Marshal and unmarshal Java objects from and to fixed length records using UniVocity Parsers.
 */
export interface UniVocityFixedLength {
    asMap?: AsMap1;
    comment?: Comment1;
    emptyValue?: EmptyValue1;
    headerExtractionEnabled?: HeaderExtractionEnabled1;
    headersDisabled?: HeadersDisabled1;
    id?: Id37;
    ignoreLeadingWhitespaces?: IgnoreLeadingWhitespaces1;
    ignoreTrailingWhitespaces?: IgnoreTrailingWhitespaces1;
    lazyLoad?: LazyLoad3;
    lineSeparator?: LineSeparator2;
    normalizedLineSeparator?: NormalizedLineSeparator1;
    nullValue?: NullValue1;
    numberOfRecordsToRead?: NumberOfRecordsToRead1;
    padding?: Padding;
    recordEndsOnNewline?: RecordEndsOnNewline;
    skipEmptyLines?: SkipEmptyLines1;
    skipTrailingCharsUntilNewline?: SkipTrailingCharsUntilNewline;
    univocityHeader?: UniVocityHeader[];
}
/**
 * Marshal and unmarshal Java objects from and to TSV (Tab-Separated Values) records using UniVocity Parsers.
 */
export interface UniVocityTSV {
    asMap?: AsMap2;
    comment?: Comment2;
    emptyValue?: EmptyValue2;
    escapeChar?: EscapeChar;
    headerExtractionEnabled?: HeaderExtractionEnabled2;
    headersDisabled?: HeadersDisabled2;
    id?: Id38;
    ignoreLeadingWhitespaces?: IgnoreLeadingWhitespaces2;
    ignoreTrailingWhitespaces?: IgnoreTrailingWhitespaces2;
    lazyLoad?: LazyLoad4;
    lineSeparator?: LineSeparator3;
    normalizedLineSeparator?: NormalizedLineSeparator2;
    nullValue?: NullValue2;
    numberOfRecordsToRead?: NumberOfRecordsToRead2;
    skipEmptyLines?: SkipEmptyLines2;
    univocityHeader?: UniVocityHeader[];
}
/**
 * Encrypt and decrypt XML payloads using Apache Santuario.
 */
export interface XMLSecurity {
    addKeyValueForEncryptedKey?: AddKeyValueForEncryptedKey;
    digestAlgorithm?: DigestAlgorithm;
    id?: Id39;
    keyCipherAlgorithm?: KeyCipherAlgorithm;
    keyOrTrustStoreParametersRef?: KeyOrTrustStoreParametersRef;
    keyPassword?: KeyPassword;
    mgfAlgorithm?: MgfAlgorithm;
    passPhrase?: PassPhrase;
    passPhraseByte?: PassPhraseByte;
    recipientKeyAlias?: RecipientKeyAlias;
    secureTag?: SecureTag;
    secureTagContents?: SecureTagContents;
    xmlCipherAlgorithm?: XmlCipherAlgorithm;
}
/**
 * Marshal and unmarshal Java objects to and from YAML.
 */
export interface YAML {
    allowAnyType?: AllowAnyType;
    allowRecursiveKeys?: AllowRecursiveKeys;
    constructor?: Constructor;
    dumperOptions?: DumperOptions;
    id?: Id40;
    library?: Library3;
    maxAliasesForCollections?: MaxAliasesForCollections;
    prettyFlow?: PrettyFlow;
    representer?: Representer;
    resolver?: Resolver;
    typeFilter?: TypeFilter;
    unmarshalType?: UnmarshalType8;
    useApplicationContextClassLoader?: UseApplicationContextClassLoader;
}
export interface YAMLTypeFilter {
    type?: Type2;
    value?: Value;
}
/**
 * Compress and decompress streams using java.util.zip.Deflater and java.util.zip.Inflater.
 */
export interface ZipDeflater {
    compressionLevel?: CompressionLevel;
    id?: Id41;
}
/**
 * Compression and decompress streams using java.util.zip.ZipStream.
 */
export interface ZipFile {
    allowEmptyDirectory?: AllowEmptyDirectory1;
    id?: Id42;
    maxDecompressedSize?: MaxDecompressedSize1;
    preservePathElements?: PreservePathElements1;
    usingIterator?: UsingIterator2;
}
/**
 * Error handler with dead letter queue.
 */
export interface DeadLetterChannel {
    deadLetterHandleNewException?: DeadLetterHandleNewException;
    deadLetterUri: DeadLetterUri;
    executorServiceRef?: ExecutorServiceRef;
    id?: Id43;
    level?: Level;
    logName?: LogName;
    loggerRef?: LoggerRef;
    onExceptionOccurredRef?: OnExceptionOccurredRef;
    onPrepareFailureRef?: OnPrepareFailureRef;
    onRedeliveryRef?: OnRedeliveryRef;
    redeliveryPolicy?: RedeliveryPolicy;
    redeliveryPolicyRef?: RedeliveryPolicyRef;
    retryWhileRef?: RetryWhileRef;
    useOriginalBody?: UseOriginalBody;
    useOriginalMessage?: UseOriginalMessage;
}
/**
 * Sets the redelivery settings
 */
export interface RedeliveryPolicy {
    allowRedeliveryWhileStopping?: AllowRedeliveryWhileStopping;
    asyncDelayedRedelivery?: AsyncDelayedRedelivery;
    backOffMultiplier?: BackOffMultiplier;
    collisionAvoidanceFactor?: CollisionAvoidanceFactor;
    delayPattern?: DelayPattern;
    disableRedelivery?: DisableRedelivery;
    exchangeFormatterRef?: ExchangeFormatterRef;
    id?: Id44;
    logContinued?: LogContinued;
    logExhausted?: LogExhausted;
    logExhaustedMessageBody?: LogExhaustedMessageBody;
    logExhaustedMessageHistory?: LogExhaustedMessageHistory;
    logHandled?: LogHandled;
    logNewException?: LogNewException;
    logRetryAttempted?: LogRetryAttempted;
    logRetryStackTrace?: LogRetryStackTrace;
    logStackTrace?: LogStackTrace;
    maximumRedeliveries?: MaximumRedeliveries;
    maximumRedeliveryDelay?: MaximumRedeliveryDelay;
    redeliveryDelay?: RedeliveryDelay;
    retriesExhaustedLogLevel?: RetriesExhaustedLogLevel;
    retryAttemptedLogInterval?: RetryAttemptedLogInterval;
    retryAttemptedLogLevel?: RetryAttemptedLogLevel;
    useCollisionAvoidance?: UseCollisionAvoidance;
    useExponentialBackOff?: UseExponentialBackOff;
}
/**
 * The default error handler.
 */
export interface DefaultErrorHandler {
    executorServiceRef?: ExecutorServiceRef1;
    id?: Id45;
    level?: Level1;
    logName?: LogName1;
    loggerRef?: LoggerRef1;
    onExceptionOccurredRef?: OnExceptionOccurredRef1;
    onPrepareFailureRef?: OnPrepareFailureRef1;
    onRedeliveryRef?: OnRedeliveryRef1;
    redeliveryPolicy?: RedeliveryPolicy1;
    redeliveryPolicyRef?: RedeliveryPolicyRef1;
    retryWhileRef?: RetryWhileRef1;
    useOriginalBody?: UseOriginalBody1;
    useOriginalMessage?: UseOriginalMessage1;
}
/**
 * Sets the redelivery settings
 */
export interface RedeliveryPolicy1 {
    allowRedeliveryWhileStopping?: AllowRedeliveryWhileStopping;
    asyncDelayedRedelivery?: AsyncDelayedRedelivery;
    backOffMultiplier?: BackOffMultiplier;
    collisionAvoidanceFactor?: CollisionAvoidanceFactor;
    delayPattern?: DelayPattern;
    disableRedelivery?: DisableRedelivery;
    exchangeFormatterRef?: ExchangeFormatterRef;
    id?: Id44;
    logContinued?: LogContinued;
    logExhausted?: LogExhausted;
    logExhaustedMessageBody?: LogExhaustedMessageBody;
    logExhaustedMessageHistory?: LogExhaustedMessageHistory;
    logHandled?: LogHandled;
    logNewException?: LogNewException;
    logRetryAttempted?: LogRetryAttempted;
    logRetryStackTrace?: LogRetryStackTrace;
    logStackTrace?: LogStackTrace;
    maximumRedeliveries?: MaximumRedeliveries;
    maximumRedeliveryDelay?: MaximumRedeliveryDelay;
    redeliveryDelay?: RedeliveryDelay;
    retriesExhaustedLogLevel?: RetriesExhaustedLogLevel;
    retryAttemptedLogInterval?: RetryAttemptedLogInterval;
    retryAttemptedLogLevel?: RetryAttemptedLogLevel;
    useCollisionAvoidance?: UseCollisionAvoidance;
    useExponentialBackOff?: UseExponentialBackOff;
}
/**
 * JTA based transactional error handler (requires camel-jta).
 */
export interface JtaTransactionErrorHandler {
    executorServiceRef?: ExecutorServiceRef2;
    id?: Id46;
    level?: Level2;
    logName?: LogName2;
    loggerRef?: LoggerRef2;
    onExceptionOccurredRef?: OnExceptionOccurredRef2;
    onPrepareFailureRef?: OnPrepareFailureRef2;
    onRedeliveryRef?: OnRedeliveryRef2;
    redeliveryPolicy?: RedeliveryPolicy2;
    redeliveryPolicyRef?: RedeliveryPolicyRef2;
    retryWhileRef?: RetryWhileRef2;
    rollbackLoggingLevel?: RollbackLoggingLevel;
    transactedPolicyRef?: TransactedPolicyRef;
    useOriginalBody?: UseOriginalBody2;
    useOriginalMessage?: UseOriginalMessage2;
}
/**
 * Sets the redelivery settings
 */
export interface RedeliveryPolicy2 {
    allowRedeliveryWhileStopping?: AllowRedeliveryWhileStopping;
    asyncDelayedRedelivery?: AsyncDelayedRedelivery;
    backOffMultiplier?: BackOffMultiplier;
    collisionAvoidanceFactor?: CollisionAvoidanceFactor;
    delayPattern?: DelayPattern;
    disableRedelivery?: DisableRedelivery;
    exchangeFormatterRef?: ExchangeFormatterRef;
    id?: Id44;
    logContinued?: LogContinued;
    logExhausted?: LogExhausted;
    logExhaustedMessageBody?: LogExhaustedMessageBody;
    logExhaustedMessageHistory?: LogExhaustedMessageHistory;
    logHandled?: LogHandled;
    logNewException?: LogNewException;
    logRetryAttempted?: LogRetryAttempted;
    logRetryStackTrace?: LogRetryStackTrace;
    logStackTrace?: LogStackTrace;
    maximumRedeliveries?: MaximumRedeliveries;
    maximumRedeliveryDelay?: MaximumRedeliveryDelay;
    redeliveryDelay?: RedeliveryDelay;
    retriesExhaustedLogLevel?: RetriesExhaustedLogLevel;
    retryAttemptedLogInterval?: RetryAttemptedLogInterval;
    retryAttemptedLogLevel?: RetryAttemptedLogLevel;
    useCollisionAvoidance?: UseCollisionAvoidance;
    useExponentialBackOff?: UseExponentialBackOff;
}
/**
 * To not use an error handler.
 */
export interface NoErrorHandler {
    id?: Id47;
}
/**
 * Spring based transactional error handler (requires camel-spring).
 */
export interface SpringTransactionErrorHandler {
    executorServiceRef?: ExecutorServiceRef3;
    id?: Id49;
    level?: Level3;
    logName?: LogName3;
    loggerRef?: LoggerRef3;
    onExceptionOccurredRef?: OnExceptionOccurredRef3;
    onPrepareFailureRef?: OnPrepareFailureRef3;
    onRedeliveryRef?: OnRedeliveryRef3;
    redeliveryPolicy?: RedeliveryPolicy3;
    redeliveryPolicyRef?: RedeliveryPolicyRef3;
    retryWhileRef?: RetryWhileRef3;
    rollbackLoggingLevel?: RollbackLoggingLevel1;
    transactedPolicyRef?: TransactedPolicyRef1;
    useOriginalBody?: UseOriginalBody3;
    useOriginalMessage?: UseOriginalMessage3;
}
/**
 * Sets the redelivery settings
 */
export interface RedeliveryPolicy3 {
    allowRedeliveryWhileStopping?: AllowRedeliveryWhileStopping;
    asyncDelayedRedelivery?: AsyncDelayedRedelivery;
    backOffMultiplier?: BackOffMultiplier;
    collisionAvoidanceFactor?: CollisionAvoidanceFactor;
    delayPattern?: DelayPattern;
    disableRedelivery?: DisableRedelivery;
    exchangeFormatterRef?: ExchangeFormatterRef;
    id?: Id44;
    logContinued?: LogContinued;
    logExhausted?: LogExhausted;
    logExhaustedMessageBody?: LogExhaustedMessageBody;
    logExhaustedMessageHistory?: LogExhaustedMessageHistory;
    logHandled?: LogHandled;
    logNewException?: LogNewException;
    logRetryAttempted?: LogRetryAttempted;
    logRetryStackTrace?: LogRetryStackTrace;
    logStackTrace?: LogStackTrace;
    maximumRedeliveries?: MaximumRedeliveries;
    maximumRedeliveryDelay?: MaximumRedeliveryDelay;
    redeliveryDelay?: RedeliveryDelay;
    retriesExhaustedLogLevel?: RetriesExhaustedLogLevel;
    retryAttemptedLogInterval?: RetryAttemptedLogInterval;
    retryAttemptedLogLevel?: RetryAttemptedLogLevel;
    useCollisionAvoidance?: UseCollisionAvoidance;
    useExponentialBackOff?: UseExponentialBackOff;
}
export interface WorkflowFromDefinitionDeserializer {
    description?: string;
    id?: string;
    parameters?: {
        [k: string]: unknown;
    };
    steps: ProcessorDefinition[];
    uri: string;
    variableReceive?: string;
}
export interface ProcessorDefinition {
    aggregate?: Aggregate;
    bean?: Bean;
    doCatch?: DoCatch;
    choice?: Choice;
    circuitBreaker?: CircuitBreaker;
    claimCheck?: ClaimCheck;
    convertBodyTo?: ConvertBodyTo;
    convertHeaderTo?: ConvertHeaderTo;
    convertVariableTo?: ConvertVariableTo;
    delay?: Delay1;
    dynamicWorkflowr?: DynamicWorkflowr;
    enrich?: Enrich;
    filter?: Filter1;
    doFinally?: DoFinally;
    idempotentConsumer?: IdempotentConsumer;
    loadBalance?: LoadBalance;
    log?: Logger;
    loop?: Loop;
    marshal?: Marshal;
    multicast?: Multicast;
    onFallback?: OnFallback1;
    otherwise?: Otherwise1;
    pausable?: Pausable;
    pipeline?: Pipeline;
    policy?: Policy;
    poll?: Poll;
    pollEnrich?: PollEnrich;
    process?: Process;
    recipientList?: RecipientList;
    removeHeader?: RemoveHeader;
    removeHeaders?: RemoveHeaders;
    removeProperties?: RemoveProperties;
    removeProperty?: RemoveProperty;
    removeVariable?: RemoveVariable;
    resequence?: Resequence;
    resumable?: Resumable;
    rollback?: Rollback;
    routingSlip?: RoutingSlip;
    saga?: Saga;
    sample?: Sample;
    script?: Script1;
    setBody?: SetBody;
    setExchangePattern?: SetExchangePattern;
    setHeader?: SetHeader;
    setHeaders?: SetHeaders;
    setProperty?: SetProperty;
    setVariable?: SetVariable;
    setVariables?: SetVariables;
    sort?: Sort;
    split?: Split;
    step?: Step;
    stop?: Stop;
    threads?: Threads;
    throttle?: Throttle;
    throwException?: ThrowException;
    to?: To;
    toD?: ToD;
    tokenizer?: SpecializedTokenizerForAIApplications;
    transacted?: Transacted;
    transform?: Transform;
    doTry?: DoTry;
    unmarshal?: Unmarshal;
    validate?: Validate2;
    when?: When1;
    whenSkipSendToEndpoint?: WhenSkipSendToEndpoint;
    wireTap?: WireTap;
    serviceCall?: ServiceCall;
}
/**
 * Aggregates many messages into a single message
 */
export interface Aggregate {
    aggregateController?: AggregateController;
    aggregationRepository?: AggregationRepository;
    aggregationStrategy: AggregationStrategy;
    aggregationStrategyMethodAllowNull?: AggregationStrategyMethodAllowNull;
    aggregationStrategyMethodName?: AggregationStrategyMethodName;
    closeCorrelationKeyOnCompletion?: CloseCorrelationKeyOnCompletion;
    completeAllOnStop?: CompleteAllOnStop;
    completionFromBatchConsumer?: CompletionFromBatchConsumer;
    completionInterval?: CompletionInterval;
    completionOnNewCorrelationGroup?: CompletionOnNewCorrelationGroup;
    completionPredicate?: CompletionPredicate;
    completionSize?: CompletionSize;
    completionSizeExpression?: CompletionSizeExpression;
    completionTimeout?: CompletionTimeout;
    completionTimeoutCheckerInterval?: CompletionTimeoutCheckerInterval;
    completionTimeoutExpression?: CompletionTimeoutExpression;
    correlationExpression?: CorrelationExpression;
    description?: Description;
    disabled?: Disabled;
    discardOnAggregationFailure?: DiscardOnAggregationFailure;
    discardOnCompletionTimeout?: DiscardOnCompletionTimeout;
    eagerCheckCompletion?: EagerCheckCompletion;
    executorService?: ExecutorService;
    forceCompletionOnStop?: ForceCompletionOnStop;
    id?: Id76;
    ignoreInvalidCorrelationKeys?: IgnoreInvalidCorrelationKeys;
    optimisticLockRetryPolicy?: OptimisticLockRetryPolicy;
    optimisticLocking?: OptimisticLocking;
    parallelProcessing?: ParallelProcessing;
    steps?: ProcessorDefinition[];
    timeoutCheckerExecutorService?: TimeoutCheckerExecutorService;
}
/**
 * Evaluates a custom language.
 */
export interface Language {
    expression: Expression12;
    id?: Id62;
    language: Language1;
    trim?: Trim13;
}
/**
 * A key value pair where the value is a literal value
 */
export interface Property {
    key: Key;
    value: Value1;
}
/**
 * Allows to configure retry settings when using optimistic locking.
 */
export interface OptimisticLockRetryPolicy {
    exponentialBackOff?: ExponentialBackOff;
    maximumRetries?: MaximumRetries;
    maximumRetryDelay?: MaximumRetryDelay;
    randomBackOff?: RandomBackOff;
    retryDelay?: RetryDelay;
}
/**
 * Catches exceptions as part of a try, catch, finally block
 */
export interface DoCatch {
    description?: Description2;
    disabled?: Disabled2;
    exception?: Exception;
    id?: Id78;
    onWhen?: OnWhen;
    steps?: ProcessorDefinition[];
}
/**
 * Workflow messages based on a series of predicates
 */
export interface Choice {
    description?: Description3;
    disabled?: Disabled3;
    id?: Id79;
    otherwise?: Otherwise;
    precondition?: Precondition;
    when?: When;
}
/**
 * Sets the otherwise node
 */
export interface Otherwise {
    description?: Description4;
    disabled?: Disabled4;
    id?: Id80;
    steps?: ProcessorDefinition[];
}
/**
 * Workflow messages in a fault tolerance way using Circuit Breaker
 */
export interface CircuitBreaker {
    configuration?: Configuration;
    description?: Description5;
    disabled?: Disabled5;
    faultToleranceConfiguration?: FaultToleranceConfiguration;
    id?: Id82;
    onFallback?: OnFallback;
    resilience4jConfiguration?: Resilience4JConfiguration;
    steps?: ProcessorDefinition[];
}
/**
 * Configures the circuit breaker to use MicroProfile Fault Tolerance with the given configuration.
 */
export interface FaultToleranceConfiguration {
    bulkheadEnabled?: BulkheadEnabled;
    bulkheadExecutorService?: BulkheadExecutorService;
    bulkheadMaxConcurrentCalls?: BulkheadMaxConcurrentCalls;
    bulkheadWaitingTaskQueue?: BulkheadWaitingTaskQueue;
    circuitBreaker?: CircuitBreaker1;
    delay?: Delay;
    failureRatio?: FailureRatio;
    id?: Id81;
    requestVolumeThreshold?: RequestVolumeThreshold;
    successThreshold?: SuccessThreshold;
    timeoutDuration?: TimeoutDuration;
    timeoutEnabled?: TimeoutEnabled;
    timeoutPoolSize?: TimeoutPoolSize;
    timeoutScheduledExecutorService?: TimeoutScheduledExecutorService;
}
/**
 * The fallback workflow path to execute that does not go over the network. This should be a static or cached result that can immediately be returned upon failure. If the fallback requires network connection then use onFallbackViaNetwork() .
 */
export interface OnFallback {
    description?: Description6;
    disabled?: Disabled6;
    fallbackViaNetwork?: FallbackViaNetwork;
    id?: Id83;
    steps?: ProcessorDefinition[];
}
/**
 * Configures the circuit breaker to use Resilience4j with the given configuration.
 */
export interface Resilience4JConfiguration {
    automaticTransitionFromOpenToHalfOpenEnabled?: AutomaticTransitionFromOpenToHalfOpenEnabled;
    bulkheadEnabled?: BulkheadEnabled1;
    bulkheadMaxConcurrentCalls?: BulkheadMaxConcurrentCalls1;
    bulkheadMaxWaitDuration?: BulkheadMaxWaitDuration;
    circuitBreaker?: CircuitBreaker2;
    config?: Config;
    failureRateThreshold?: FailureRateThreshold;
    id?: Id84;
    ignoreException?: IgnoreException;
    minimumNumberOfCalls?: MinimumNumberOfCalls;
    permittedNumberOfCallsInHalfOpenState?: PermittedNumberOfCallsInHalfOpenState;
    recordException?: RecordException;
    slidingWindowSize?: SlidingWindowSize;
    slidingWindowType?: SlidingWindowType;
    slowCallDurationThreshold?: SlowCallDurationThreshold;
    slowCallRateThreshold?: SlowCallRateThreshold;
    throwExceptionWhenHalfOpenOrOpenState?: ThrowExceptionWhenHalfOpenOrOpenState;
    timeoutCancelRunningFuture?: TimeoutCancelRunningFuture;
    timeoutDuration?: TimeoutDuration1;
    timeoutEnabled?: TimeoutEnabled1;
    timeoutExecutorService?: TimeoutExecutorService;
    waitDurationInOpenState?: WaitDurationInOpenState;
    writableStackTraceEnabled?: WritableStackTraceEnabled;
}
/**
 * The Claim Check EIP allows you to replace message content with a claim check (a unique key), which can be used to retrieve the message content at a later time.
 */
export interface ClaimCheck {
    aggregationStrategy?: AggregationStrategy1;
    aggregationStrategyMethodName?: AggregationStrategyMethodName1;
    description?: Description7;
    disabled?: Disabled7;
    filter?: Filter;
    id?: Id85;
    key?: Key1;
    operation?: Operation;
}
/**
 * Converts the message header to another type
 */
export interface ConvertHeaderTo {
    charset?: Charset1;
    description?: Description9;
    disabled?: Disabled9;
    id?: Id87;
    mandatory?: Mandatory1;
    name: Name2;
    toName?: ToName;
    type: Type4;
}
/**
 * Converts the variable to another type
 */
export interface ConvertVariableTo {
    charset?: Charset2;
    description?: Description10;
    disabled?: Disabled10;
    id?: Id88;
    mandatory?: Mandatory2;
    name: Name3;
    toName?: ToName1;
    type: Type5;
}
/**
 * Path traversed when a try, catch, finally block exits
 */
export interface DoFinally {
    description?: Description11;
    disabled?: Disabled11;
    id?: Id89;
    steps?: ProcessorDefinition[];
}
/**
 * In case of failures the exchange will be tried on the next endpoint.
 */
export interface FailoverLoadBalancer {
    exception?: Exception1;
    id?: Id91;
    maximumFailoverAttempts?: MaximumFailoverAttempts;
    roundRobin?: RoundRobin;
    sticky?: Sticky;
}
/**
 * The destination endpoints are selected randomly.
 */
export interface RandomLoadBalancer {
    id?: Id92;
}
/**
 * The destination endpoints are selected in a round-robin fashion. This is a well-known and classic policy, which spreads the load evenly.
 */
export interface RoundRobinLoadBalancer {
    id?: Id93;
}
/**
 * Sticky load balancing using an expression to calculate a correlation key to perform the sticky load balancing.
 */
export interface StickyLoadBalancer {
    correlationExpression?: CorrelationExpression1;
    id?: Id94;
}
/**
 * Topic which sends to all destinations.
 */
export interface TopicLoadBalancer {
    id?: Id95;
}
/**
 * Uses a weighted load distribution ratio for each server with respect to others.
 */
export interface WeightedLoadBalancer {
    distributionRatio: DistributionRatio;
    distributionRatioDelimiter?: DistributionRatioDelimiter;
    id?: Id96;
    roundRobin?: RoundRobin1;
}
/**
 * Workflows the same message to multiple paths either sequentially or in parallel.
 */
export interface Multicast {
    aggregationStrategy?: AggregationStrategy2;
    aggregationStrategyMethodAllowNull?: AggregationStrategyMethodAllowNull1;
    aggregationStrategyMethodName?: AggregationStrategyMethodName2;
    description?: Description13;
    disabled?: Disabled13;
    executorService?: ExecutorService1;
    id?: Id98;
    onPrepare?: OnPrepare;
    parallelAggregate?: ParallelAggregate;
    parallelProcessing?: ParallelProcessing1;
    shareUnitOfWork?: ShareUnitOfWork;
    steps?: ProcessorDefinition[];
    stopOnException?: StopOnException;
    streaming?: Streaming;
    synchronous?: Synchronous;
    timeout?: Timeout;
}
/**
 * Workflow to be executed when Circuit Breaker EIP executes fallback
 */
export interface OnFallback1 {
    description?: Description6;
    disabled?: Disabled6;
    fallbackViaNetwork?: FallbackViaNetwork;
    id?: Id83;
    steps?: ProcessorDefinition[];
}
/**
 * Workflow to be executed when all other choices evaluate to false
 */
export interface Otherwise1 {
    description?: Description4;
    disabled?: Disabled4;
    id?: Id80;
    steps?: ProcessorDefinition[];
}
/**
 * Pausable EIP to support resuming processing from last known offset.
 */
export interface Pausable {
    consumerListener: ConsumerListener;
    description?: Description14;
    disabled?: Disabled14;
    id?: Id99;
    untilCheck: UntilCheck;
}
/**
 * Workflows the message to a sequence of processors.
 */
export interface Pipeline {
    description?: Description15;
    disabled?: Disabled15;
    id?: Id100;
    steps?: ProcessorDefinition[];
}
/**
 * Defines a policy the workflow will use
 */
export interface Policy {
    description?: Description16;
    disabled?: Disabled16;
    id?: Id101;
    ref: Ref6;
    steps?: ProcessorDefinition[];
}
/**
 * Calls a Ua processor
 */
export interface Process {
    description?: Description18;
    disabled?: Disabled18;
    id?: Id103;
    ref: Ref7;
}
/**
 * Configures batch-processing resequence eip.
 */
export interface BatchConfig {
    allowDuplicates?: AllowDuplicates;
    batchSize?: BatchSize;
    batchTimeout?: BatchTimeout;
    ignoreInvalidExchanges?: IgnoreInvalidExchanges;
    reverse?: Reverse;
}
/**
 * Configures stream-processing resequence eip.
 */
export interface StreamConfig {
    capacity?: Capacity;
    comparator?: Comparator;
    deliveryAttemptInterval?: DeliveryAttemptInterval;
    ignoreInvalidExchanges?: IgnoreInvalidExchanges1;
    rejectOld?: RejectOld;
    timeout?: Timeout2;
}
/**
 * Resume EIP to support resuming processing from last known offset.
 */
export interface Resumable {
    description?: Description24;
    disabled?: Disabled24;
    id?: Id109;
    intermittent?: Intermittent;
    loggingLevel?: LoggingLevel1;
    resumeStrategy: ResumeStrategy;
}
/**
 * Enables Sagas on the workflow
 */
export interface Saga {
    compensation?: Compensation;
    completion?: Completion;
    completionMode?: CompletionMode;
    description?: Description26;
    disabled?: Disabled26;
    id?: Id111;
    option?: Option1;
    propagation?: Propagation;
    sagaService?: SagaService;
    steps?: ProcessorDefinition[];
    timeout?: Timeout3;
}
/**
 * Allows setting multiple headers on the message at the same time.
 */
export interface SetHeaders {
    description?: Description29;
    disabled?: Disabled29;
    headers?: Headers;
    id?: Id114;
}
/**
 * Allows setting multiple variables at the same time.
 */
export interface SetVariables {
    description?: Description30;
    disabled?: Disabled30;
    id?: Id115;
    variables?: Variables;
}
/**
 * Workflows the message to a sequence of processors which is grouped together as one logical name
 */
export interface Step {
    description?: Description31;
    disabled?: Disabled31;
    id?: Id116;
    steps?: ProcessorDefinition[];
}
/**
 * Stops the processing of the current message
 */
export interface Stop {
    description?: Description32;
    disabled?: Disabled32;
    id?: Id117;
}
/**
 * Specifies that all steps after this node are processed asynchronously
 */
export interface Threads {
    allowCoreThreadTimeOut?: AllowCoreThreadTimeOut;
    callerRunsWhenRejected?: CallerRunsWhenRejected;
    description?: Description33;
    disabled?: Disabled33;
    executorService?: ExecutorService2;
    id?: Id118;
    keepAliveTime?: KeepAliveTime;
    maxPoolSize?: MaxPoolSize;
    maxQueueSize?: MaxQueueSize;
    poolSize?: PoolSize;
    rejectedPolicy?: RejectedPolicy;
    threadName?: ThreadName;
    timeUnit?: TimeUnit;
}
/**
 * Throws an exception
 */
export interface ThrowException {
    description?: Description34;
    disabled?: Disabled34;
    exceptionType?: ExceptionType;
    id?: Id119;
    message?: Message2;
    ref?: Ref8;
}
export interface LangChain4JTokenizerWithCharacterSplitter {
    id?: Id122;
    maxOverlap: MaxOverlap;
    maxTokens: MaxTokens;
    tokenizerType?: TokenizerType;
}
export interface LangChain4JTokenizerDefinition {
    id?: string;
    maxOverlap: number;
    maxTokens: number;
    tokenizerType?: "OPEN_AI" | "AZURE" | "QWEN";
}
export interface LangChain4JTokenizerWithParagraphSplitter {
    id?: Id123;
    maxOverlap: MaxOverlap1;
    maxTokens: MaxTokens1;
    tokenizerType?: TokenizerType1;
}
export interface LangChain4JTokenizerWithSentenceSplitter {
    id?: Id124;
    maxOverlap: MaxOverlap2;
    maxTokens: MaxTokens2;
    tokenizerType?: TokenizerType2;
}
export interface LangChain4JTokenizerWithWordSplitter {
    id?: Id125;
    maxOverlap: MaxOverlap3;
    maxTokens: MaxTokens3;
    tokenizerType?: TokenizerType3;
}
/**
 * Enables transaction on the workflow
 */
export interface Transacted {
    description?: Description37;
    disabled?: Disabled37;
    id?: Id126;
    ref?: Ref9;
    steps?: ProcessorDefinition[];
}
/**
 * Marks the beginning of a try, catch, finally block
 */
export interface DoTry {
    description?: Description38;
    disabled?: Disabled38;
    doCatch?: DoCatch[];
    doFinally?: DoFinally;
    id?: Id127;
    steps?: ProcessorDefinition[];
}
/**
 * Workflows a copy of a message (or creates a new message) to a secondary destination while continue routing the original message.
 */
export interface WireTap {
    allowOptimisedComponents?: AllowOptimisedComponents1;
    autoStartComponents?: AutoStartComponents1;
    cacheSize?: CacheSize1;
    copy?: Copy;
    description?: Description39;
    disabled?: Disabled39;
    dynamicUri?: DynamicUri;
    executorService?: ExecutorService3;
    id?: Id128;
    ignoreInvalidEndpoint?: IgnoreInvalidEndpoint1;
    onPrepare?: OnPrepare1;
    parameters?: {
        [k: string]: unknown;
    };
    pattern?: Pattern6;
    uri: Uri3;
    variableReceive?: VariableReceive3;
    variableSend?: VariableSend2;
}
/**
 * @deprecated
 */
export interface BlacklistServiceFilter {
    id?: Id129;
    properties?: Properties1;
    servers?: Servers;
}
/**
 * @deprecated
 */
export interface CombinedServiceFilter {
    blacklistServiceFilter?: BlacklistServiceFilter;
    customServiceFilter?: CustomServiceFilter;
    healthyServiceFilter?: HealthyServiceFilter;
    id?: Id132;
    passThroughServiceFilter?: PassThroughServiceFilter;
    properties?: Properties5;
}
/**
 * @deprecated
 */
export interface CustomServiceFilter {
    id?: Id130;
    properties?: Properties2;
    ref?: Ref10;
}
/**
 * @deprecated
 */
export interface HealthyServiceFilter {
    id?: Id131;
    properties?: Properties3;
}
/**
 * @deprecated
 */
export interface PassThroughServiceFilter {
    id?: Id133;
    properties?: Properties4;
}
/**
 * @deprecated
 */
export interface CombinedServiceDiscovery {
    cachingServiceDiscovery?: CachingServiceDiscovery;
    consulServiceDiscovery?: ConsulServiceDiscovery;
    dnsServiceDiscovery?: DnsServiceDiscovery;
    id?: Id136;
    kubernetesServiceDiscovery?: KubernetesServiceDiscovery;
    properties?: Properties9;
    staticServiceDiscovery?: StaticServiceDiscovery;
}
/**
 * @deprecated
 */
export interface ConsulServiceDiscovery {
    aclToken?: AclToken;
    blockSeconds?: BlockSeconds;
    connectTimeoutMillis?: ConnectTimeoutMillis;
    datacenter?: Datacenter;
    id?: Id134;
    password?: Password1;
    properties?: Properties6;
    readTimeoutMillis?: ReadTimeoutMillis;
    url?: Url;
    userName?: UserName;
    writeTimeoutMillis?: WriteTimeoutMillis;
}
/**
 * @deprecated
 */
export interface DnsServiceDiscovery {
    domain?: Domain;
    id?: Id135;
    properties?: Properties7;
    proto?: Proto;
}
/**
 * @deprecated
 */
export interface KubernetesServiceDiscovery {
    apiVersion?: ApiVersion;
    caCertData?: CaCertData;
    caCertFile?: CaCertFile;
    clientCertData?: ClientCertData;
    clientCertFile?: ClientCertFile;
    clientKeyAlgo?: ClientKeyAlgo;
    clientKeyData?: ClientKeyData;
    clientKeyFile?: ClientKeyFile;
    clientKeyPassphrase?: ClientKeyPassphrase;
    dnsDomain?: DnsDomain;
    id?: Id137;
    lookup?: Lookup;
    masterUrl?: MasterUrl;
    namespace?: Namespace3;
    oauthToken?: OauthToken;
    password?: Password2;
    portName?: PortName;
    portProtocol?: PortProtocol;
    properties?: Properties8;
    trustCerts?: TrustCerts;
    username?: Username;
}
/**
 * @deprecated
 */
export interface StaticServiceDiscovery {
    id?: Id138;
    properties?: Properties10;
    servers?: Servers1;
}
/**
 * @deprecated
 */
export interface ZookeeperServiceDiscovery {
    basePath: BasePath;
    connectionTimeout?: ConnectionTimeout;
    id?: Id139;
    namespace?: Namespace4;
    nodes: Nodes;
    properties?: Properties11;
    reconnectBaseSleepTime?: ReconnectBaseSleepTime;
    reconnectMaxRetries?: ReconnectMaxRetries;
    reconnectMaxSleepTime?: ReconnectMaxSleepTime;
    sessionTimeout?: SessionTimeout;
}
/**
 * @deprecated
 */
export interface DefaultLoadBalancer {
    id?: Id140;
    properties?: Properties12;
}
/**
 * @deprecated
 * Configures the Expression using the given configuration.
 */
export interface Expression24 {
    expressionType?: ExpressionDefinition;
    hostHeader?: HostHeader;
    id?: Id141;
    portHeader?: PortHeader;
    properties?: Properties13;
}
/**
 * Intercepts a message at each step in the workflow
 */
export interface Intercept {
    description?: Description40;
    disabled?: Disabled40;
    id?: Id142;
    steps?: ProcessorDefinition[];
}
/**
 * Workflow to be executed when normal workflow processing completes
 */
export interface OnCompletion {
    description?: Description43;
    disabled?: Disabled43;
    executorService?: ExecutorService4;
    id?: Id145;
    mode?: Mode1;
    onCompleteOnly?: OnCompleteOnly;
    onFailureOnly?: OnFailureOnly;
    onWhen?: When2;
    parallelProcessing?: ParallelProcessing2;
    steps?: ProcessorDefinition[];
    useOriginalMessage?: UseOriginalMessage4;
}
/**
 * Workflow to be executed when an exception is thrown
 */
export interface OnException {
    continued?: Continued;
    description?: Description44;
    disabled?: Disabled44;
    exception?: Exception2;
    handled?: Handled;
    id?: Id146;
    onExceptionOccurredRef?: OnExceptionOccurredRef4;
    onRedeliveryRef?: OnRedeliveryRef4;
    onWhen?: When3;
    redeliveryPolicy?: RedeliveryPolicy4;
    redeliveryPolicyRef?: RedeliveryPolicyRef4;
    retryWhile?: RetryWhile;
    steps?: ProcessorDefinition[];
    useOriginalBody?: UseOriginalBody4;
    useOriginalMessage?: UseOriginalMessage5;
}
/**
 * Used for configuring redelivery options
 */
export interface RedeliveryPolicy4 {
    allowRedeliveryWhileStopping?: AllowRedeliveryWhileStopping;
    asyncDelayedRedelivery?: AsyncDelayedRedelivery;
    backOffMultiplier?: BackOffMultiplier;
    collisionAvoidanceFactor?: CollisionAvoidanceFactor;
    delayPattern?: DelayPattern;
    disableRedelivery?: DisableRedelivery;
    exchangeFormatterRef?: ExchangeFormatterRef;
    id?: Id44;
    logContinued?: LogContinued;
    logExhausted?: LogExhausted;
    logExhaustedMessageBody?: LogExhaustedMessageBody;
    logExhaustedMessageHistory?: LogExhaustedMessageHistory;
    logHandled?: LogHandled;
    logNewException?: LogNewException;
    logRetryAttempted?: LogRetryAttempted;
    logRetryStackTrace?: LogRetryStackTrace;
    logStackTrace?: LogStackTrace;
    maximumRedeliveries?: MaximumRedeliveries;
    maximumRedeliveryDelay?: MaximumRedeliveryDelay;
    redeliveryDelay?: RedeliveryDelay;
    retriesExhaustedLogLevel?: RetriesExhaustedLogLevel;
    retryAttemptedLogInterval?: RetryAttemptedLogInterval;
    retryAttemptedLogLevel?: RetryAttemptedLogLevel;
    useCollisionAvoidance?: UseCollisionAvoidance;
    useExponentialBackOff?: UseExponentialBackOff;
}
export interface WorkflowConfigurationDefinition {
    description?: string;
    errorHandler?: ErrorHandler;
    id?: string;
    intercept?: {
        intercept?: Intercept;
    }[];
    interceptFrom?: {
        interceptFrom?: InterceptFrom;
    }[];
    interceptSendToEndpoint?: {
        interceptSendToEndpoint?: InterceptSendToEndpoint;
    }[];
    onCompletion?: {
        onCompletion?: OnCompletion;
    }[];
    onException?: {
        onException?: OnException;
    }[];
    precondition?: string;
}
export interface WorkflowDefinition {
    autoStartup?: boolean;
    description?: string;
    errorHandler?: ErrorHandler;
    errorHandlerRef?: string;
    from: FromDefinition;
    group?: string;
    id?: string;
    inputType?: InputType;
    logMask?: boolean;
    messageHistory?: boolean;
    nodePrefixId?: string;
    outputType?: OutputType;
    precondition?: string;
    workflowConfigurationId?: string;
    workflowPolicy?: string;
    /**
     * To control how to shut down the workflow.
     */
    shutdownWorkflow?: "Default" | "Defer";
    /**
     * To control how to shut down the workflow.
     */
    shutdownRunningTask?: "CompleteCurrentTaskOnly" | "CompleteAllTasks";
    startupOrder?: number;
    streamCache?: boolean;
    trace?: boolean;
}
export interface FromDefinition {
    description?: string;
    id?: string;
    parameters?: {
        [k: string]: unknown;
    };
    steps: ProcessorDefinition[];
    uri: string;
    variableReceive?: string;
}
/**
 * Set the expected data type of the input message. If the actual message type is different at runtime, camel look for a required Transformer and apply if exists. If validate attribute is true then camel applies Validator as well. Type name consists of two parts, 'scheme' and 'name' connected with ':'. For Java type 'name' is a fully qualified class name. For example {code java:java.lang.String} , {code json:ABCOrder} . It's also possible to specify only scheme part, so that it works like a wildcard. If only 'xml' is specified, all the XML message matches. It's handy to add only one transformer/validator for all the transformation from/to XML.
 */
export interface InputType {
    description?: Description45;
    id?: Id147;
    urn: Urn;
    validate?: Validate3;
}
/**
 * Set the expected data type of the output message. If the actual message type is different at runtime, camel look for a required Transformer and apply if exists. If validate attribute is true then camel applies Validator as well. Type name consists of two parts, 'scheme' and 'name' connected with ':'. For Java type 'name' is a fully qualified class name. For example {code java:java.lang.String} , {code json:ABCOrder} . It's also possible to specify only scheme part, so that it works like a wildcard. If only 'xml' is specified, all the XML message matches. It's handy to add only one transformer/validator for all the XML-Java transformation.
 */
export interface OutputType {
    description?: Description46;
    id?: Id148;
    urn: Urn1;
    validate?: Validate4;
}
export interface WorkflowTemplateDefinition {
    beans?: BeanFactory[];
    description?: string;
    from?: FromDefinition;
    id: string;
    parameters?: TemplateParameter[];
    workflow?: WorkflowDefinition;
}
/**
 * A workflow template parameter
 */
export interface TemplateParameter {
    defaultValue?: DefaultValue;
    description?: Description47;
    name: Name7;
    required?: Required;
}
export interface TemplatedWorkflowDefinition {
    beans?: BeanFactory[];
    parameters?: TemplatedWorkflowParameter[];
    prefixId?: string;
    workflowId?: string;
    workflowTemplateRef: string;
}
/**
 * An input parameter of a workflow template.
 */
export interface TemplatedWorkflowParameter {
    name: Name8;
    value: Value2;
}
/**
 * To configure rest
 */
export interface RestConfiguration {
    apiComponent?: ApiComponent;
    apiContextPath?: ApiContextPath;
    apiContextWorkflowId?: ApiContextWorkflowId;
    apiHost?: ApiHost;
    apiProperty?: ApiProperty;
    apiVendorExtension?: ApiVendorExtension;
    bindingMode?: BindingMode;
    bindingPackageScan?: BindingPackageScan;
    clientRequestValidation?: ClientRequestValidation;
    component?: Component;
    componentProperty?: ComponentProperty;
    consumerProperty?: ConsumerProperty;
    contextPath?: ContextPath2;
    corsHeaders?: CorsHeaders;
    dataFormatProperty?: DataFormatProperty;
    enableCORS?: EnableCORS;
    enableNoContentResponse?: EnableNoContentResponse;
    endpointProperty?: EndpointProperty;
    host?: Host;
    hostNameResolver?: HostNameResolver;
    inlineWorkflows?: InlineWorkflows;
    jsonDataFormat?: JsonDataFormat;
    port?: Port;
    producerApiDoc?: ProducerApiDoc;
    producerComponent?: ProducerComponent;
    scheme?: Scheme;
    skipBindingOnErrorCode?: SkipBindingOnErrorCode;
    useXForwardHeaders?: UseXForwardHeaders;
    xmlDataFormat?: XmlDataFormat;
}
/**
 * A key value pair
 */
export interface RestProperty {
    key: Key2;
    value: Value3;
}
/**
 * Defines a rest service using the rest-dsl
 */
export interface Rest {
    apiDocs?: ApiDocs;
    bindingMode?: BindingMode1;
    clientRequestValidation?: ClientRequestValidation1;
    consumes?: Consumes;
    delete?: Delete[];
    description?: Description51;
    disabled?: Disabled46;
    enableCORS?: EnableCORS2;
    enableNoContentResponse?: EnableNoContentResponse2;
    get?: Get[];
    head?: Head[];
    id?: Id152;
    openApi?: OpenApi;
    patch?: Patch[];
    path?: Path4;
    post?: Post[];
    produces?: Produces5;
    put?: Put[];
    securityDefinitions?: SecurityDefinitions;
    securityRequirements?: SecurityRequirements;
    skipBindingOnErrorCode?: SkipBindingOnErrorCode7;
    tag?: Tag;
}
/**
 * Rest DELETE command
 */
export interface Delete {
    apiDocs?: ApiDocs1;
    bindingMode?: BindingMode2;
    clientRequestValidation?: ClientRequestValidation2;
    consumes?: Consumes1;
    deprecated?: Deprecated;
    description?: Description48;
    disabled?: Disabled45;
    enableCORS?: EnableCORS1;
    enableNoContentResponse?: EnableNoContentResponse1;
    id?: Id149;
    outType?: OutType;
    param?: Param[];
    path?: Path;
    produces?: Produces;
    responseMessage?: ResponseMessage[];
    workflowId?: WorkflowId;
    security?: RestSecurity[];
    skipBindingOnErrorCode?: SkipBindingOnErrorCode1;
    streamCache?: StreamCache;
    to?: To1;
    type?: Type7;
}
/**
 * To specify the rest operation parameters.
 */
export interface Param {
    allowableValues?: AllowableValues;
    arrayType?: ArrayType;
    collectionFormat?: CollectionFormat;
    dataFormat?: DataFormat;
    dataType?: DataType;
    defaultValue?: DefaultValue1;
    description?: Description49;
    examples?: Examples;
    name: Name9;
    required?: Required1;
    type?: Type6;
}
/**
 * To specify the rest operation response messages.
 */
export interface ResponseMessage {
    code?: Code;
    examples?: Examples1;
    header?: Header2;
    message: Message3;
    responseModel?: ResponseModel;
}
/**
 * To specify the rest operation response headers.
 */
export interface ResponseHeader {
    allowableValues?: AllowableValues1;
    arrayType?: ArrayType1;
    collectionFormat?: CollectionFormat1;
    dataFormat?: DataFormat1;
    dataType?: DataType1;
    description?: Description50;
    example?: Example;
    name: Name10;
}
/**
 * Rest security definition
 */
export interface RestSecurity {
    key: Key3;
    scopes?: Scopes;
}
/**
 * Rest GET command
 */
export interface Get {
    apiDocs?: ApiDocs2;
    bindingMode?: BindingMode3;
    clientRequestValidation?: ClientRequestValidation3;
    consumes?: Consumes2;
    deprecated?: Deprecated1;
    description?: Description52;
    disabled?: Disabled47;
    enableCORS?: EnableCORS3;
    enableNoContentResponse?: EnableNoContentResponse3;
    id?: Id150;
    outType?: OutType1;
    param?: Param[];
    path?: Path1;
    produces?: Produces1;
    responseMessage?: ResponseMessage[];
    workflowId?: WorkflowId1;
    security?: RestSecurity[];
    skipBindingOnErrorCode?: SkipBindingOnErrorCode2;
    streamCache?: StreamCache1;
    to?: To2;
    type?: Type8;
}
/**
 * Rest HEAD command
 */
export interface Head {
    apiDocs?: ApiDocs3;
    bindingMode?: BindingMode4;
    clientRequestValidation?: ClientRequestValidation4;
    consumes?: Consumes3;
    deprecated?: Deprecated2;
    description?: Description53;
    disabled?: Disabled48;
    enableCORS?: EnableCORS4;
    enableNoContentResponse?: EnableNoContentResponse4;
    id?: Id151;
    outType?: OutType2;
    param?: Param[];
    path?: Path2;
    produces?: Produces2;
    responseMessage?: ResponseMessage[];
    workflowId?: WorkflowId2;
    security?: RestSecurity[];
    skipBindingOnErrorCode?: SkipBindingOnErrorCode3;
    streamCache?: StreamCache2;
    to?: To3;
    type?: Type9;
}
/**
 * To use an existing OpenAPI specification as contract-first for Ua Rest DSL.
 */
export interface OpenApi {
    description?: Description54;
    disabled?: Disabled49;
    id?: Id153;
    missingOperation?: MissingOperation;
    mockIncludePattern?: MockIncludePattern;
    workflowId?: WorkflowId3;
    specification: Specification;
}
/**
 * Rest PATCH command
 */
export interface Patch {
    apiDocs?: ApiDocs4;
    bindingMode?: BindingMode5;
    clientRequestValidation?: ClientRequestValidation5;
    consumes?: Consumes4;
    deprecated?: Deprecated3;
    description?: Description55;
    disabled?: Disabled50;
    enableCORS?: EnableCORS5;
    enableNoContentResponse?: EnableNoContentResponse5;
    id?: Id154;
    outType?: OutType3;
    param?: Param[];
    path?: Path3;
    produces?: Produces3;
    responseMessage?: ResponseMessage[];
    workflowId?: WorkflowId4;
    security?: RestSecurity[];
    skipBindingOnErrorCode?: SkipBindingOnErrorCode4;
    streamCache?: StreamCache3;
    to?: To4;
    type?: Type10;
}
/**
 * Rest POST command
 */
export interface Post {
    apiDocs?: ApiDocs5;
    bindingMode?: BindingMode6;
    clientRequestValidation?: ClientRequestValidation6;
    consumes?: Consumes5;
    deprecated?: Deprecated4;
    description?: Description56;
    disabled?: Disabled51;
    enableCORS?: EnableCORS6;
    enableNoContentResponse?: EnableNoContentResponse6;
    id?: Id155;
    outType?: OutType4;
    param?: Param[];
    path?: Path5;
    produces?: Produces4;
    responseMessage?: ResponseMessage[];
    workflowId?: WorkflowId5;
    security?: RestSecurity[];
    skipBindingOnErrorCode?: SkipBindingOnErrorCode5;
    streamCache?: StreamCache4;
    to?: To5;
    type?: Type11;
}
/**
 * Rest PUT command
 */
export interface Put {
    apiDocs?: ApiDocs6;
    bindingMode?: BindingMode7;
    clientRequestValidation?: ClientRequestValidation7;
    consumes?: Consumes6;
    deprecated?: Deprecated5;
    description?: Description57;
    disabled?: Disabled52;
    enableCORS?: EnableCORS7;
    enableNoContentResponse?: EnableNoContentResponse7;
    id?: Id156;
    outType?: OutType5;
    param?: Param[];
    path?: Path6;
    produces?: Produces6;
    responseMessage?: ResponseMessage[];
    workflowId?: WorkflowId6;
    security?: RestSecurity[];
    skipBindingOnErrorCode?: SkipBindingOnErrorCode6;
    streamCache?: StreamCache5;
    to?: To6;
    type?: Type12;
}
/**
 * Sets the security definitions such as Basic, OAuth2 etc.
 */
export interface SecurityDefinitions {
    apiKey?: ApiKey;
    basicAuth?: BasicAuth;
    bearer?: BearerToken;
    mutualTLS?: MutualTLS;
    oauth2?: Oauth2;
    openIdConnect?: OpenIdConnect;
}
/**
 * Rest security basic auth definition
 */
export interface ApiKey {
    description?: Description58;
    inCookie?: InCookie;
    inHeader?: InHeader;
    inQuery?: InQuery;
    key: Key4;
    name: Name11;
}
/**
 * Rest security basic auth definition
 */
export interface BasicAuth {
    description?: Description59;
    key: Key5;
}
/**
 * Rest security bearer token authentication definition
 */
export interface BearerToken {
    description?: Description60;
    format?: Format;
    key: Key6;
}
/**
 * Rest security mutual TLS authentication definition
 */
export interface MutualTLS {
    description?: Description61;
    key: Key7;
}
/**
 * Rest security OAuth2 definition
 */
export interface Oauth2 {
    authorizationUrl?: AuthorizationUrl;
    description?: Description62;
    flow?: Flow;
    key: Key8;
    refreshUrl?: RefreshUrl;
    scopes?: Scopes1;
    tokenUrl?: TokenUrl;
}
/**
 * Rest security OpenID Connect definition
 */
export interface OpenIdConnect {
    description?: Description63;
    key: Key9;
    url: Url1;
}
