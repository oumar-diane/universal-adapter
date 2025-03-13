package org.zenithblox.support.processor.idempotent;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class FileIdempotentRepositoryConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        FileIdempotentRepository target = (FileIdempotentRepository) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "cachesize":
            case "cacheSize": target.setCacheSize(property(zwangineContext, int.class, value)); return true;
            case "dropoldestfilestore":
            case "dropOldestFileStore": target.setDropOldestFileStore(property(zwangineContext, long.class, value)); return true;
            case "filestore":
            case "fileStore": target.setFileStore(property(zwangineContext, java.io.File.class, value)); return true;
            case "maxfilestoresize":
            case "maxFileStoreSize": target.setMaxFileStoreSize(property(zwangineContext, long.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "cachesize":
            case "cacheSize": return int.class;
            case "dropoldestfilestore":
            case "dropOldestFileStore": return long.class;
            case "filestore":
            case "fileStore": return java.io.File.class;
            case "maxfilestoresize":
            case "maxFileStoreSize": return long.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        FileIdempotentRepository target = (FileIdempotentRepository) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "cachesize":
            case "cacheSize": return target.getCacheSize();
            case "dropoldestfilestore":
            case "dropOldestFileStore": return target.getDropOldestFileStore();
            case "filestore":
            case "fileStore": return target.getFileStore();
            case "maxfilestoresize":
            case "maxFileStoreSize": return target.getMaxFileStoreSize();
            default: return null;
        }
    }
}


