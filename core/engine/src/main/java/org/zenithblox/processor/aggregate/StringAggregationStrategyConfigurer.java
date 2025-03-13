package org.zenithblox.processor.aggregate;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class StringAggregationStrategyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        StringAggregationStrategy target = (StringAggregationStrategy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "delimiter": target.setDelimiter(property(zwangineContext, java.lang.String.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "delimiter": return java.lang.String.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        StringAggregationStrategy target = (StringAggregationStrategy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "delimiter": return target.getDelimiter();
            default: return null;
        }
    }
}
