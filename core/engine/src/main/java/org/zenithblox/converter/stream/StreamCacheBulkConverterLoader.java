package org.zenithblox.converter.stream;

import org.zenithblox.*;
import org.zenithblox.spi.BulkTypeConverters;
import org.zenithblox.spi.TypeConverterLoader;
import org.zenithblox.spi.TypeConverterRegistry;
import org.zenithblox.spi.TypeConvertible;
import org.zenithblox.stream.CachedOutputStream;
import org.zenithblox.stream.StreamCacheConverter;

public final class StreamCacheBulkConverterLoader implements TypeConverterLoader, BulkTypeConverters, ZwangineContextAware {

    private ZwangineContext zwangineContext;

    public StreamCacheBulkConverterLoader() {
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public int size() {
        return 6;
    }

    @Override
    public void load(TypeConverterRegistry registry) throws TypeConverterLoaderException {
        registry.addBulkTypeConverters(this);
        doRegistration(registry);
    }

    @Override
    public <T> T convertTo(Class<?> from, Class<T> to, Exchange exchange, Object value) throws TypeConversionException {
        try {
            Object obj = doConvertTo(from, to, exchange, value);
            if (obj == Void.class) {;
                return null;
            } else {
                return (T) obj;
            }
        } catch (TypeConversionException e) {
            throw e;
        } catch (Exception e) {
            throw new TypeConversionException(value, to, e);
        }
    }

    private Object doConvertTo(Class<?> from, Class<?> to, Exchange exchange, Object value) throws Exception {
        if (to == byte[].class) {
            if (value instanceof StreamCache) {
                return StreamCacheConverter.convertToByteArray((StreamCache) value, exchange);
            }
        } else if (to == java.nio.ByteBuffer.class) {
            if (value instanceof StreamCache) {
                return StreamCacheConverter.convertToByteBuffer((StreamCache) value, exchange);
            }
        } else if (to == StreamCache.class) {
            if (value instanceof java.io.ByteArrayInputStream) {
                return StreamCacheConverter.convertToStreamCache((java.io.ByteArrayInputStream) value, exchange);
            }
            if (value instanceof java.io.InputStream) {
                return StreamCacheConverter.convertToStreamCache((java.io.InputStream) value, exchange);
            }
            if (value instanceof CachedOutputStream) {
                return StreamCacheConverter.convertToStreamCache((CachedOutputStream) value, exchange);
            }
            if (value instanceof java.io.Reader) {
                return StreamCacheConverter.convertToStreamCache((java.io.Reader) value, exchange);
            }
        }
        return null;
    }

    private void doRegistration(TypeConverterRegistry registry) {
        registry.addConverter(new TypeConvertible<>(StreamCache.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(StreamCache.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.ByteArrayInputStream.class, StreamCache.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, StreamCache.class), this);
        registry.addConverter(new TypeConvertible<>(CachedOutputStream.class, StreamCache.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.Reader.class, StreamCache.class), this);
    }

    public TypeConverter lookup(Class<?> to, Class<?> from) {
        if (to == byte[].class) {
            if (from == StreamCache.class) {
                return this;
            }
        } else if (to == java.nio.ByteBuffer.class) {
            if (from == StreamCache.class) {
                return this;
            }
        } else if (to == StreamCache.class) {
            if (from == java.io.ByteArrayInputStream.class) {
                return this;
            }
            if (from == java.io.InputStream.class) {
                return this;
            }
            if (from == CachedOutputStream.class) {
                return this;
            }
            if (from == java.io.Reader.class) {
                return this;
            }
        }
        return null;
    }

}

