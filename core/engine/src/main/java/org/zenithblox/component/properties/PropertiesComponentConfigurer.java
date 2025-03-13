package org.zenithblox.component.properties;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.ExtendedPropertyConfigurerGetter;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.support.component.PropertyConfigurerSupport;
import org.zenithblox.util.CaseInsensitiveMap;

import java.util.Map;

public class PropertiesComponentConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, ExtendedPropertyConfigurerGetter {


    private static final Map<String, Object> ALL_OPTIONS;
    static {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("AutoDiscoverPropertiesSources", boolean.class);
        map.put("ZwangineContext", ZwangineContext.class);
        map.put("DefaultFallbackEnabled", boolean.class);
        map.put("Encoding", java.lang.String.class);
        map.put("EnvironmentVariableMode", int.class);
        map.put("IgnoreMissingLocation", boolean.class);
        map.put("IgnoreMissingProperty", boolean.class);
        map.put("InitialProperties", java.util.Properties.class);
        map.put("LocalProperties", java.util.Properties.class);
        map.put("Location", java.lang.String.class);
        map.put("Locations", java.util.List.class);
        map.put("NestedPlaceholder", boolean.class);
        map.put("OverrideProperties", java.util.Properties.class);
        map.put("PropertiesFunctionResolver", PropertiesFunctionResolver.class);
        map.put("PropertiesParser", PropertiesParser.class);
        map.put("SystemPropertiesMode", int.class);
        ALL_OPTIONS = map;
    }

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        PropertiesComponent target = (PropertiesComponent) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "autodiscoverpropertiessources":
            case "autoDiscoverPropertiesSources": target.setAutoDiscoverPropertiesSources(property(zwangineContext, boolean.class, value)); return true;
            case "zwanginecontext":
            case "zwangineContext": target.setZwangineContext(property(zwangineContext, ZwangineContext.class, value)); return true;
            case "defaultfallbackenabled":
            case "defaultFallbackEnabled": target.setDefaultFallbackEnabled(property(zwangineContext, boolean.class, value)); return true;
            case "encoding": target.setEncoding(property(zwangineContext, java.lang.String.class, value)); return true;
            case "environmentvariablemode":
            case "environmentVariableMode": target.setEnvironmentVariableMode(property(zwangineContext, int.class, value)); return true;
            case "ignoremissinglocation":
            case "ignoreMissingLocation": target.setIgnoreMissingLocation(property(zwangineContext, boolean.class, value)); return true;
            case "ignoremissingproperty":
            case "ignoreMissingProperty": target.setIgnoreMissingProperty(property(zwangineContext, boolean.class, value)); return true;
            case "initialproperties":
            case "initialProperties": target.setInitialProperties(property(zwangineContext, java.util.Properties.class, value)); return true;
            case "localproperties":
            case "localProperties": target.setLocalProperties(property(zwangineContext, java.util.Properties.class, value)); return true;
            case "location": target.setLocation(property(zwangineContext, java.lang.String.class, value)); return true;
            case "locations": target.setLocations(property(zwangineContext, java.util.List.class, value)); return true;
            case "nestedplaceholder":
            case "nestedPlaceholder": target.setNestedPlaceholder(property(zwangineContext, boolean.class, value)); return true;
            case "overrideproperties":
            case "overrideProperties": target.setOverrideProperties(property(zwangineContext, java.util.Properties.class, value)); return true;
            case "propertiesfunctionresolver":
            case "propertiesFunctionResolver": target.setPropertiesFunctionResolver(property(zwangineContext, PropertiesFunctionResolver.class, value)); return true;
            case "propertiesparser":
            case "propertiesParser": target.setPropertiesParser(property(zwangineContext, PropertiesParser.class, value)); return true;
            case "systempropertiesmode":
            case "systemPropertiesMode": target.setSystemPropertiesMode(property(zwangineContext, int.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Map<String, Object> getAllOptions(Object target) {
        return ALL_OPTIONS;
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "autodiscoverpropertiessources":
            case "autoDiscoverPropertiesSources": return boolean.class;
            case "zwanginecontext":
            case "zwangineContext": return ZwangineContext.class;
            case "defaultfallbackenabled":
            case "defaultFallbackEnabled": return boolean.class;
            case "encoding": return java.lang.String.class;
            case "environmentvariablemode":
            case "environmentVariableMode": return int.class;
            case "ignoremissinglocation":
            case "ignoreMissingLocation": return boolean.class;
            case "ignoremissingproperty":
            case "ignoreMissingProperty": return boolean.class;
            case "initialproperties":
            case "initialProperties": return java.util.Properties.class;
            case "localproperties":
            case "localProperties": return java.util.Properties.class;
            case "location": return java.lang.String.class;
            case "locations": return java.util.List.class;
            case "nestedplaceholder":
            case "nestedPlaceholder": return boolean.class;
            case "overrideproperties":
            case "overrideProperties": return java.util.Properties.class;
            case "propertiesfunctionresolver":
            case "propertiesFunctionResolver": return PropertiesFunctionResolver.class;
            case "propertiesparser":
            case "propertiesParser": return PropertiesParser.class;
            case "systempropertiesmode":
            case "systemPropertiesMode": return int.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        PropertiesComponent target = (PropertiesComponent) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "autodiscoverpropertiessources":
            case "autoDiscoverPropertiesSources": return target.isAutoDiscoverPropertiesSources();
            case "zwanginecontext":
            case "zwangineContext": return target.getZwangineContext();
            case "defaultfallbackenabled":
            case "defaultFallbackEnabled": return target.isDefaultFallbackEnabled();
            case "encoding": return target.getEncoding();
            case "environmentvariablemode":
            case "environmentVariableMode": return target.getEnvironmentVariableMode();
            case "ignoremissinglocation":
            case "ignoreMissingLocation": return target.isIgnoreMissingLocation();
            case "ignoremissingproperty":
            case "ignoreMissingProperty": return target.isIgnoreMissingProperty();
            case "initialproperties":
            case "initialProperties": return target.getInitialProperties();
            case "localproperties":
            case "localProperties": return target.getLocalProperties();
            case "location": return target.getLocation();
            case "locations": return target.getLocations();
            case "nestedplaceholder":
            case "nestedPlaceholder": return target.isNestedPlaceholder();
            case "overrideproperties":
            case "overrideProperties": return target.getOverrideProperties();
            case "propertiesfunctionresolver":
            case "propertiesFunctionResolver": return target.getPropertiesFunctionResolver();
            case "propertiesparser":
            case "propertiesParser": return target.getPropertiesParser();
            case "systempropertiesmode":
            case "systemPropertiesMode": return target.getSystemPropertiesMode();
            default: return null;
        }
    }

    @Override
    public Object getCollectionValueType(Object target, String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "locations": return PropertiesLocation.class;
            default: return null;
        }
    }
}
