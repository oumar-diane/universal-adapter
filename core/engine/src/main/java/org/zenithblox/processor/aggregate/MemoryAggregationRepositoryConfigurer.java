package org.zenithblox.processor.aggregate;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class MemoryAggregationRepositoryConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        MemoryAggregationRepository target = (MemoryAggregationRepository) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "optimisticlocking":
            case "optimisticLocking": target.setOptimisticLocking(property(zwangineContext, boolean.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "optimisticlocking":
            case "optimisticLocking": return boolean.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        MemoryAggregationRepository target = (MemoryAggregationRepository) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "optimisticlocking":
            case "optimisticLocking": return target.isOptimisticLocking();
            default: return null;
        }
    }
}


