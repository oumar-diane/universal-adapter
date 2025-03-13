package org.zenithblox.support;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class AcceptAllHeaderFilterStrategyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        return false;
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        return null;
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        return null;
    }
}
