package org.zenithblox.support.processor;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class DefaultExchangeFormatterConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext camelContext, Object obj, String name, Object value, boolean ignoreCase) {
        DefaultExchangeFormatter target = (DefaultExchangeFormatter) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "maxchars":
            case "maxChars": target.setMaxChars(property(camelContext, int.class, value)); return true;
            case "multiline": target.setMultiline(property(camelContext, boolean.class, value)); return true;
            case "plain": target.setPlain(property(camelContext, boolean.class, value)); return true;
            case "showall":
            case "showAll": target.setShowAll(property(camelContext, boolean.class, value)); return true;
            case "showallproperties":
            case "showAllProperties": target.setShowAllProperties(property(camelContext, boolean.class, value)); return true;
            case "showbody":
            case "showBody": target.setShowBody(property(camelContext, boolean.class, value)); return true;
            case "showbodytype":
            case "showBodyType": target.setShowBodyType(property(camelContext, boolean.class, value)); return true;
            case "showcachedstreams":
            case "showCachedStreams": target.setShowCachedStreams(property(camelContext, boolean.class, value)); return true;
            case "showcaughtexception":
            case "showCaughtException": target.setShowCaughtException(property(camelContext, boolean.class, value)); return true;
            case "showexception":
            case "showException": target.setShowException(property(camelContext, boolean.class, value)); return true;
            case "showexchangeid":
            case "showExchangeId": target.setShowExchangeId(property(camelContext, boolean.class, value)); return true;
            case "showexchangepattern":
            case "showExchangePattern": target.setShowExchangePattern(property(camelContext, boolean.class, value)); return true;
            case "showfiles":
            case "showFiles": target.setShowFiles(property(camelContext, boolean.class, value)); return true;
            case "showfuture":
            case "showFuture": target.setShowFuture(property(camelContext, boolean.class, value)); return true;
            case "showheaders":
            case "showHeaders": target.setShowHeaders(property(camelContext, boolean.class, value)); return true;
            case "showproperties":
            case "showProperties": target.setShowProperties(property(camelContext, boolean.class, value)); return true;
            case "showroutegroup":
            case "showWorkflowGroup": target.setShowWorkflowGroup(property(camelContext, boolean.class, value)); return true;
            case "showrouteid":
            case "showWorkflowId": target.setShowWorkflowId(property(camelContext, boolean.class, value)); return true;
            case "showstacktrace":
            case "showStackTrace": target.setShowStackTrace(property(camelContext, boolean.class, value)); return true;
            case "showstreams":
            case "showStreams": target.setShowStreams(property(camelContext, boolean.class, value)); return true;
            case "showvariables":
            case "showVariables": target.setShowVariables(property(camelContext, boolean.class, value)); return true;
            case "skipbodylineseparator":
            case "skipBodyLineSeparator": target.setSkipBodyLineSeparator(property(camelContext, boolean.class, value)); return true;
            case "style": target.setStyle(property(camelContext, DefaultExchangeFormatter.OutputStyle.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "maxchars":
            case "maxChars": return int.class;
            case "multiline": return boolean.class;
            case "plain": return boolean.class;
            case "showall":
            case "showAll": return boolean.class;
            case "showallproperties":
            case "showAllProperties": return boolean.class;
            case "showbody":
            case "showBody": return boolean.class;
            case "showbodytype":
            case "showBodyType": return boolean.class;
            case "showcachedstreams":
            case "showCachedStreams": return boolean.class;
            case "showcaughtexception":
            case "showCaughtException": return boolean.class;
            case "showexception":
            case "showException": return boolean.class;
            case "showexchangeid":
            case "showExchangeId": return boolean.class;
            case "showexchangepattern":
            case "showExchangePattern": return boolean.class;
            case "showfiles":
            case "showFiles": return boolean.class;
            case "showfuture":
            case "showFuture": return boolean.class;
            case "showheaders":
            case "showHeaders": return boolean.class;
            case "showproperties":
            case "showProperties": return boolean.class;
            case "showroutegroup":
            case "showWorkflowGroup": return boolean.class;
            case "showrouteid":
            case "showWorkflowId": return boolean.class;
            case "showstacktrace":
            case "showStackTrace": return boolean.class;
            case "showstreams":
            case "showStreams": return boolean.class;
            case "showvariables":
            case "showVariables": return boolean.class;
            case "skipbodylineseparator":
            case "skipBodyLineSeparator": return boolean.class;
            case "style": return DefaultExchangeFormatter.OutputStyle.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        DefaultExchangeFormatter target = (DefaultExchangeFormatter) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "maxchars":
            case "maxChars": return target.getMaxChars();
            case "multiline": return target.isMultiline();
            case "plain": return target.isPlain();
            case "showall":
            case "showAll": return target.isShowAll();
            case "showallproperties":
            case "showAllProperties": return target.isShowAllProperties();
            case "showbody":
            case "showBody": return target.isShowBody();
            case "showbodytype":
            case "showBodyType": return target.isShowBodyType();
            case "showcachedstreams":
            case "showCachedStreams": return target.isShowCachedStreams();
            case "showcaughtexception":
            case "showCaughtException": return target.isShowCaughtException();
            case "showexception":
            case "showException": return target.isShowException();
            case "showexchangeid":
            case "showExchangeId": return target.isShowExchangeId();
            case "showexchangepattern":
            case "showExchangePattern": return target.isShowExchangePattern();
            case "showfiles":
            case "showFiles": return target.isShowFiles();
            case "showfuture":
            case "showFuture": return target.isShowFuture();
            case "showheaders":
            case "showHeaders": return target.isShowHeaders();
            case "showproperties":
            case "showProperties": return target.isShowProperties();
            case "showroutegroup":
            case "showWorkflowGroup": return target.isShowWorkflowGroup();
            case "showrouteid":
            case "showWorkflowId": return target.isShowWorkflowId();
            case "showstacktrace":
            case "showStackTrace": return target.isShowStackTrace();
            case "showstreams":
            case "showStreams": return target.isShowStreams();
            case "showvariables":
            case "showVariables": return target.isShowVariables();
            case "skipbodylineseparator":
            case "skipBodyLineSeparator": return target.isSkipBodyLineSeparator();
            case "style": return target.getStyle();
            default: return null;
        }
    }
}

