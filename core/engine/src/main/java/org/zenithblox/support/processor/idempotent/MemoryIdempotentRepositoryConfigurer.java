package org.zenithblox.support.processor.idempotent;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class MemoryIdempotentRepositoryConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext camelContext, Object obj, String name, Object value, boolean ignoreCase) {
        MemoryIdempotentRepository target = (MemoryIdempotentRepository) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "cachesize":
            case "cacheSize": target.setCacheSize(property(camelContext, int.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "cachesize":
            case "cacheSize": return int.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        MemoryIdempotentRepository target = (MemoryIdempotentRepository) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "cachesize":
            case "cacheSize": return target.getCacheSize();
            default: return null;
        }
    }
}