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
package org.zenithblox.reifier.dataformat;

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.model.Model;
import org.zenithblox.model.dataformat.*;
import org.zenithblox.reifier.AbstractReifier;
import org.zenithblox.spi.*;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class DataFormatReifier<T extends DataFormatDefinition> extends AbstractReifier {

    private static final Logger LOG = LoggerFactory.getLogger(DataFormatReifier.class);

    // for custom reifiers
    private static final Map<Class<? extends DataFormatDefinition>, BiFunction<ZwangineContext, DataFormatDefinition, DataFormatReifier<? extends DataFormatDefinition>>> DATAFORMATS
            = new HashMap<>(0);

    protected final T definition;

    public DataFormatReifier(ZwangineContext zwangineContext, T definition) {
        super(zwangineContext);
        this.definition = definition;
    }

    public static void registerReifier(
            Class<? extends DataFormatDefinition> dataFormatClass,
            BiFunction<ZwangineContext, DataFormatDefinition, DataFormatReifier<? extends DataFormatDefinition>> creator) {
        if (DATAFORMATS.isEmpty()) {
            ReifierStrategy.addReifierClearer(DataFormatReifier::clearReifiers);
        }
        DATAFORMATS.put(dataFormatClass, creator);
    }

    public static void clearReifiers() {
        DATAFORMATS.clear();
    }

    public static DataFormat getDataFormat(ZwangineContext zwangineContext, DataFormatDefinition type) {
        return getDataFormat(zwangineContext, ObjectHelper.notNull(type, "type"), null);
    }

    public static DataFormat getDataFormat(ZwangineContext zwangineContext, String ref) {
        return getDataFormat(zwangineContext, null, ObjectHelper.notNull(ref, "ref"));
    }

    /**
     * Factory method to create the data format
     *
     * @param  zwangineContext the zwangine context
     * @param  type         the data format type
     * @param  ref          reference to lookup for a data format
     * @return              the data format or null if not possible to create
     */
    public static DataFormat getDataFormat(ZwangineContext zwangineContext, DataFormatDefinition type, String ref) {
        if (type == null) {
            ObjectHelper.notNull(ref, "ref or type");

            DataFormat dataFormat = ZwangineContextHelper.lookup(zwangineContext, ref, DataFormat.class);
            if (dataFormat != null) {
                return dataFormat;
            }

            // try to let resolver see if it can resolve it, its not always
            // possible
            type = zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class).resolveDataFormatDefinition(ref);

            if (type == null) {
                dataFormat = zwangineContext.resolveDataFormat(ref);
                if (dataFormat == null) {
                    throw new IllegalArgumentException("Cannot find data format in registry with ref: " + ref);
                }

                return dataFormat;
            }
        }
        if (type.getDataFormat() != null) {
            return type.getDataFormat();
        }
        return reifier(zwangineContext, type).createDataFormat();
    }

    public static DataFormatReifier<? extends DataFormatDefinition> reifier(
            ZwangineContext zwangineContext, DataFormatDefinition definition) {

        DataFormatReifier<? extends DataFormatDefinition> answer = null;
        if (!DATAFORMATS.isEmpty()) {
            // custom take precedence
            BiFunction<ZwangineContext, DataFormatDefinition, DataFormatReifier<? extends DataFormatDefinition>> reifier
                    = DATAFORMATS.get(definition.getClass());
            if (reifier != null) {
                answer = reifier.apply(zwangineContext, definition);
            }
        }
        if (answer == null) {
            answer = coreReifier(zwangineContext, definition);
        }
        if (answer == null) {
            throw new IllegalStateException("Unsupported definition: " + definition);
        }
        return answer;
    }

    private static DataFormatReifier<? extends DataFormatDefinition> coreReifier(
            ZwangineContext zwangineContext, DataFormatDefinition definition) {
        if (definition instanceof ASN1DataFormat) {
            return new ASN1DataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof AvroDataFormat) {
            return new AvroDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof BarcodeDataFormat) {
            return new BarcodeDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof Base64DataFormat) {
            return new Base64DataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof BeanioDataFormat) {
            return new BeanioDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof BindyDataFormat) {
            return new BindyDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof CBORDataFormat) {
            return new CBORDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof CryptoDataFormat) {
            return new CryptoDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof CsvDataFormat) {
            return new CsvDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof CustomDataFormat) {
            return new CustomDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof FhirJsonDataFormat) {
            return new FhirJsonDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof FhirXmlDataFormat) {
            return new FhirXmlDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof FhirDataformat) {
            return new FhirDataFormatReifier<>(zwangineContext, definition);
        } else if (definition instanceof FlatpackDataFormat) {
            return new FlatpackDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof FuryDataFormat) {
            return new FuryDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof GrokDataFormat) {
            return new GrokDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof GzipDeflaterDataFormat) {
            return new GzipDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof HL7DataFormat) {
            return new HL7DataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof IcalDataFormat) {
            return new IcalDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof JacksonXMLDataFormat) {
            return new JacksonXMLDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof JaxbDataFormat) {
            return new JaxbDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof JsonApiDataFormat) {
            return new JsonApiDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof JsonDataFormat) {
            return new JsonDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof LZFDataFormat) {
            return new LZFDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof MimeMultipartDataFormat) {
            return new MimeMultipartDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof ParquetAvroDataFormat) {
            return new ParquetAvroDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof PGPDataFormat) {
            return new PGPDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof ProtobufDataFormat) {
            return new ProtobufDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof RssDataFormat) {
            return new RssDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof SmooksDataFormat) {
            return new SmooksDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof SoapDataFormat) {
            return new SoapDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof SyslogDataFormat) {
            return new SyslogDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof SwiftMtDataFormat) {
            return new SwiftMtDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof SwiftMxDataFormat) {
            return new SwiftMxDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof TarFileDataFormat) {
            return new TarFileDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof ThriftDataFormat) {
            return new ThriftDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof TidyMarkupDataFormat) {
            return new TidyMarkupDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof UniVocityCsvDataFormat) {
            return new UniVocityCsvDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof UniVocityFixedDataFormat) {
            return new UniVocityFixedWidthDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof UniVocityTsvDataFormat) {
            return new UniVocityTsvDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof XMLSecurityDataFormat) {
            return new XMLSecurityDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof YAMLDataFormat) {
            return new YAMLDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof ZipDeflaterDataFormat) {
            return new ZipDataFormatReifier(zwangineContext, definition);
        } else if (definition instanceof ZipFileDataFormat) {
            return new ZipFileDataFormatReifier(zwangineContext, definition);
        }
        return null;
    }

    public DataFormat createDataFormat() {
        DataFormat dataFormat = definition.getDataFormat();
        if (dataFormat == null) {
            dataFormat = doCreateDataFormat();
            if (dataFormat != null) {
                if (dataFormat instanceof DataFormatContentTypeHeader dataFormatContentTypeHeader
                        && definition instanceof ContentTypeHeaderAware contentTypeHeaderAware) {
                    String header = contentTypeHeaderAware.getContentTypeHeader();
                    // is enabled by default so assume true if null
                    final boolean contentTypeHeader = parseBoolean(header, true);
                    dataFormatContentTypeHeader.setContentTypeHeader(contentTypeHeader);
                }
                // configure the rest of the options
                configureDataFormat(dataFormat);
            } else {
                throw new IllegalArgumentException(
                        "Data format '" + (definition.getDataFormatName() != null ? definition.getDataFormatName() : "<null>")
                                                   + "' could not be created. "
                                                   + "Ensure that the data format is valid and the associated Zwangine component is present on the classpath");
            }
        }
        return dataFormat;
    }

    /**
     * Factory method to create the data format instance
     */
    protected DataFormat doCreateDataFormat() {
        // must use getDataFormatName() as we need special logic in json dataformat
        String dfn = definition.getDataFormatName();
        if (dfn != null) {
            return zwangineContext.createDataFormat(dfn);
        }
        return null;
    }

    private String getDataFormatName() {
        return definition.getDataFormatName();
    }

    /**
     * Allows derived classes to customize the data format
     */
    protected void configureDataFormat(DataFormat dataFormat) {
        Map<String, Object> properties = new LinkedHashMap<>();
        prepareDataFormatConfig(properties);
        properties.entrySet().removeIf(e -> e.getValue() == null);

        PropertyConfigurer configurer = findPropertyConfigurer(dataFormat);

        PropertyBindingSupport.build()
                .withZwangineContext(zwangineContext)
                .withTarget(dataFormat)
                .withReference(true)
                .withMandatory(true)
                .withIgnoreCase(true)
                .withConfigurer(configurer)
                .withProperties(properties)
                .bind();
    }

    private PropertyConfigurer findPropertyConfigurer(DataFormat dataFormat) {
        PropertyConfigurer configurer = null;
        String name = getDataFormatName();
        LOG.trace("Discovering optional dataformat property configurer class for dataformat: {}", name);
        if (dataFormat instanceof PropertyConfigurerAware propertyConfigurerAware) {
            configurer = propertyConfigurerAware.getPropertyConfigurer(dataFormat);
            if (LOG.isDebugEnabled() && configurer != null) {
                LOG.debug("Discovered dataformat property configurer using the PropertyConfigurerAware: {} -> {}", name,
                        configurer);
            }
        }
        if (configurer == null) {
            String configurerName = name + "-dataformat-configurer";
            configurer = PluginHelper.getConfigurerResolver(zwangineContext)
                    .resolvePropertyConfigurer(configurerName, zwangineContext);
        }
        return configurer;
    }

    protected abstract void prepareDataFormatConfig(Map<String, Object> properties);

    protected String asTypeName(Class<?> classType) {
        String type;
        if (!classType.isPrimitive()) {
            if (classType.isArray()) {
                type = StringHelper.between(classType.getName(), "[L", ";") + "[]";
            } else {
                type = classType.getName();
            }
        } else {
            type = classType.getCanonicalName();
        }

        return type;
    }

}
