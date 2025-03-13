package org.zenithblox.support;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class DefaultHeaderFilterStrategyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        DefaultHeaderFilterStrategy target = (DefaultHeaderFilterStrategy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allownullvalues":
            case "allowNullValues": target.setAllowNullValues(property(zwangineContext, boolean.class, value)); return true;
            case "caseinsensitive":
            case "caseInsensitive": target.setCaseInsensitive(property(zwangineContext, boolean.class, value)); return true;
            case "filteronmatch":
            case "filterOnMatch": target.setFilterOnMatch(property(zwangineContext, boolean.class, value)); return true;
            case "infilter":
            case "inFilter": target.setInFilter(property(zwangineContext, java.util.Set.class, value)); return true;
            case "lowercase":
            case "lowerCase": target.setLowerCase(property(zwangineContext, boolean.class, value)); return true;
            case "outfilter":
            case "outFilter": target.setOutFilter(property(zwangineContext, java.util.Set.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allownullvalues":
            case "allowNullValues": return boolean.class;
            case "caseinsensitive":
            case "caseInsensitive": return boolean.class;
            case "filteronmatch":
            case "filterOnMatch": return boolean.class;
            case "infilter":
            case "inFilter": return java.util.Set.class;
            case "lowercase":
            case "lowerCase": return boolean.class;
            case "outfilter":
            case "outFilter": return java.util.Set.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        DefaultHeaderFilterStrategy target = (DefaultHeaderFilterStrategy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allownullvalues":
            case "allowNullValues": return target.isAllowNullValues();
            case "caseinsensitive":
            case "caseInsensitive": return target.isCaseInsensitive();
            case "filteronmatch":
            case "filterOnMatch": return target.isFilterOnMatch();
            case "infilter":
            case "inFilter": return target.getInFilter();
            case "lowercase":
            case "lowerCase": return target.isLowerCase();
            case "outfilter":
            case "outFilter": return target.getOutFilter();
            default: return null;
        }
    }

    @Override
    public Object getCollectionValueType(Object target, String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "infilter":
            case "inFilter": return java.lang.String.class;
            case "outfilter":
            case "outFilter": return java.lang.String.class;
            default: return null;
        }
    }
}

