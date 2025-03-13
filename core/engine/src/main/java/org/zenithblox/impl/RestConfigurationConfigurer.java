package org.zenithblox.impl;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.spi.RestConfiguration;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class RestConfigurationConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        RestConfiguration target = (RestConfiguration) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "apicomponent":
            case "apiComponent": target.setApiComponent(property(zwangineContext, java.lang.String.class, value)); return true;
            case "apicontextpath":
            case "apiContextPath": target.setApiContextPath(property(zwangineContext, java.lang.String.class, value)); return true;
            case "apicontextworkflowid":
            case "apiContextWorkflowId": target.setApiContextWorkflowId(property(zwangineContext, java.lang.String.class, value)); return true;
            case "apihost":
            case "apiHost": target.setApiHost(property(zwangineContext, java.lang.String.class, value)); return true;
            case "apiproperties":
            case "apiProperties": target.setApiProperties(property(zwangineContext, java.util.Map.class, value)); return true;
            case "apivendorextension":
            case "apiVendorExtension": target.setApiVendorExtension(property(zwangineContext, boolean.class, value)); return true;
            case "bindingmode":
            case "bindingMode": target.setBindingMode(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bindingpackagescan":
            case "bindingPackageScan": target.setBindingPackageScan(property(zwangineContext, java.lang.String.class, value)); return true;
            case "clientrequestvalidation":
            case "clientRequestValidation": target.setClientRequestValidation(property(zwangineContext, boolean.class, value)); return true;
            case "component": target.setComponent(property(zwangineContext, java.lang.String.class, value)); return true;
            case "componentproperties":
            case "componentProperties": target.setComponentProperties(property(zwangineContext, java.util.Map.class, value)); return true;
            case "consumerproperties":
            case "consumerProperties": target.setConsumerProperties(property(zwangineContext, java.util.Map.class, value)); return true;
            case "contextpath":
            case "contextPath": target.setContextPath(property(zwangineContext, java.lang.String.class, value)); return true;
            case "corsheaders":
            case "corsHeaders": target.setCorsHeaders(property(zwangineContext, java.util.Map.class, value)); return true;
            case "dataformatproperties":
            case "dataFormatProperties": target.setDataFormatProperties(property(zwangineContext, java.util.Map.class, value)); return true;
            case "enablecors":
            case "enableCORS": target.setEnableCORS(property(zwangineContext, boolean.class, value)); return true;
            case "enablenocontentresponse":
            case "enableNoContentResponse": target.setEnableNoContentResponse(property(zwangineContext, boolean.class, value)); return true;
            case "endpointproperties":
            case "endpointProperties": target.setEndpointProperties(property(zwangineContext, java.util.Map.class, value)); return true;
            case "host": target.setHost(property(zwangineContext, java.lang.String.class, value)); return true;
            case "hostnameresolver":
            case "hostNameResolver": target.setHostNameResolver(property(zwangineContext, java.lang.String.class, value)); return true;
            case "inlineworkflows":
            case "inlineWorkflows": target.setInlineWorkflows(property(zwangineContext, boolean.class, value)); return true;
            case "jsondataformat":
            case "jsonDataFormat": target.setJsonDataFormat(property(zwangineContext, java.lang.String.class, value)); return true;
            case "port": target.setPort(property(zwangineContext, int.class, value)); return true;
            case "producerapidoc":
            case "producerApiDoc": target.setProducerApiDoc(property(zwangineContext, java.lang.String.class, value)); return true;
            case "producercomponent":
            case "producerComponent": target.setProducerComponent(property(zwangineContext, java.lang.String.class, value)); return true;
            case "scheme": target.setScheme(property(zwangineContext, java.lang.String.class, value)); return true;
            case "skipbindingonerrorcode":
            case "skipBindingOnErrorCode": target.setSkipBindingOnErrorCode(property(zwangineContext, boolean.class, value)); return true;
            case "usexforwardheaders":
            case "useXForwardHeaders": target.setUseXForwardHeaders(property(zwangineContext, boolean.class, value)); return true;
            case "xmldataformat":
            case "xmlDataFormat": target.setXmlDataFormat(property(zwangineContext, java.lang.String.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "apicomponent":
            case "apiComponent": return java.lang.String.class;
            case "apicontextpath":
            case "apiContextPath": return java.lang.String.class;
            case "apicontextworkflowid":
            case "apiContextWorkflowId": return java.lang.String.class;
            case "apihost":
            case "apiHost": return java.lang.String.class;
            case "apiproperties":
            case "apiProperties": return java.util.Map.class;
            case "apivendorextension":
            case "apiVendorExtension": return boolean.class;
            case "bindingmode":
            case "bindingMode": return java.lang.String.class;
            case "bindingpackagescan":
            case "bindingPackageScan": return java.lang.String.class;
            case "clientrequestvalidation":
            case "clientRequestValidation": return boolean.class;
            case "component": return java.lang.String.class;
            case "componentproperties":
            case "componentProperties": return java.util.Map.class;
            case "consumerproperties":
            case "consumerProperties": return java.util.Map.class;
            case "contextpath":
            case "contextPath": return java.lang.String.class;
            case "corsheaders":
            case "corsHeaders": return java.util.Map.class;
            case "dataformatproperties":
            case "dataFormatProperties": return java.util.Map.class;
            case "enablecors":
            case "enableCORS": return boolean.class;
            case "enablenocontentresponse":
            case "enableNoContentResponse": return boolean.class;
            case "endpointproperties":
            case "endpointProperties": return java.util.Map.class;
            case "host": return java.lang.String.class;
            case "hostnameresolver":
            case "hostNameResolver": return java.lang.String.class;
            case "inlineworkflows":
            case "inlineWorkflows": return boolean.class;
            case "jsondataformat":
            case "jsonDataFormat": return java.lang.String.class;
            case "port": return int.class;
            case "producerapidoc":
            case "producerApiDoc": return java.lang.String.class;
            case "producercomponent":
            case "producerComponent": return java.lang.String.class;
            case "scheme": return java.lang.String.class;
            case "skipbindingonerrorcode":
            case "skipBindingOnErrorCode": return boolean.class;
            case "usexforwardheaders":
            case "useXForwardHeaders": return boolean.class;
            case "xmldataformat":
            case "xmlDataFormat": return java.lang.String.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        RestConfiguration target = (RestConfiguration) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "apicomponent":
            case "apiComponent": return target.getApiComponent();
            case "apicontextpath":
            case "apiContextPath": return target.getApiContextPath();
            case "apicontextworkflowid":
            case "apiContextWorkflowId": return target.getApiContextWorkflowId();
            case "apihost":
            case "apiHost": return target.getApiHost();
            case "apiproperties":
            case "apiProperties": return target.getApiProperties();
            case "apivendorextension":
            case "apiVendorExtension": return target.isApiVendorExtension();
            case "bindingmode":
            case "bindingMode": return target.getBindingMode();
            case "bindingpackagescan":
            case "bindingPackageScan": return target.getBindingPackageScan();
            case "clientrequestvalidation":
            case "clientRequestValidation": return target.isClientRequestValidation();
            case "component": return target.getComponent();
            case "componentproperties":
            case "componentProperties": return target.getComponentProperties();
            case "consumerproperties":
            case "consumerProperties": return target.getConsumerProperties();
            case "contextpath":
            case "contextPath": return target.getContextPath();
            case "corsheaders":
            case "corsHeaders": return target.getCorsHeaders();
            case "dataformatproperties":
            case "dataFormatProperties": return target.getDataFormatProperties();
            case "enablecors":
            case "enableCORS": return target.isEnableCORS();
            case "enablenocontentresponse":
            case "enableNoContentResponse": return target.isEnableNoContentResponse();
            case "endpointproperties":
            case "endpointProperties": return target.getEndpointProperties();
            case "host": return target.getHost();
            case "hostnameresolver":
            case "hostNameResolver": return target.getHostNameResolver();
            case "inlineworkflows":
            case "inlineWorkflows": return target.isInlineWorkflows();
            case "jsondataformat":
            case "jsonDataFormat": return target.getJsonDataFormat();
            case "port": return target.getPort();
            case "producerapidoc":
            case "producerApiDoc": return target.getProducerApiDoc();
            case "producercomponent":
            case "producerComponent": return target.getProducerComponent();
            case "scheme": return target.getScheme();
            case "skipbindingonerrorcode":
            case "skipBindingOnErrorCode": return target.isSkipBindingOnErrorCode();
            case "usexforwardheaders":
            case "useXForwardHeaders": return target.isUseXForwardHeaders();
            case "xmldataformat":
            case "xmlDataFormat": return target.getXmlDataFormat();
            default: return null;
        }
    }

    @Override
    public Object getCollectionValueType(Object target, String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "apiproperties":
            case "apiProperties": return java.lang.Object.class;
            case "componentproperties":
            case "componentProperties": return java.lang.Object.class;
            case "consumerproperties":
            case "consumerProperties": return java.lang.Object.class;
            case "corsheaders":
            case "corsHeaders": return java.lang.String.class;
            case "dataformatproperties":
            case "dataFormatProperties": return java.lang.Object.class;
            case "endpointproperties":
            case "endpointProperties": return java.lang.Object.class;
            default: return null;
        }
    }
}
