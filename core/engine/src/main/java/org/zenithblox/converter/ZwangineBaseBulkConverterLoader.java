package org.zenithblox.converter;

import org.zenithblox.*;
import org.zenithblox.spi.BulkTypeConverters;
import org.zenithblox.spi.TypeConverterLoader;
import org.zenithblox.spi.TypeConverterRegistry;
import org.zenithblox.spi.TypeConvertible;

import java.nio.ByteBuffer;

@DeferredContextBinding
public class ZwangineBaseBulkConverterLoader implements TypeConverterLoader, BulkTypeConverters, ZwangineContextAware {
    private ZwangineContext camelContext;

    public ZwangineBaseBulkConverterLoader() {
    }

    @Override
    public void setZwangineContext(ZwangineContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return camelContext;
    }

    @Override
    public int size() {
        return 130;
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
            if (value instanceof java.nio.ByteBuffer) {
                return NIOConverter.toByteArray((java.nio.ByteBuffer) value);
            }
            if (value instanceof org.zenithblox.spi.Resource) {
                return ResourceConverter.toByteArray((org.zenithblox.spi.Resource) value, camelContext);
            }
            if (value instanceof java.io.File) {
                return IOConverter.toByteArray((java.io.File) value);
            }
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toByteArray((java.nio.file.Path) value);
            }
            if (value instanceof java.io.BufferedReader) {
                return IOConverter.toByteArray((java.io.BufferedReader) value, exchange);
            }
            if (value instanceof java.io.Reader) {
                return IOConverter.toByteArray((java.io.Reader) value, exchange);
            }
            if (value instanceof java.lang.String) {
                return IOConverter.toByteArray((java.lang.String) value, exchange);
            }
            if (value instanceof java.io.InputStream) {
                return IOConverter.toBytes((java.io.InputStream) value);
            }
            if (value instanceof java.io.ByteArrayOutputStream) {
                return IOConverter.toByteArray((java.io.ByteArrayOutputStream) value);
            }
        } else if (to == char[].class) {
            if (value instanceof java.lang.String) {
                return ObjectConverter.toCharArray((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toCharArray((byte[]) value, exchange);
            }
        } else if (to == java.lang.Object[].class) {
            if (value instanceof java.util.Collection) {
                return CollectionConverter.toArray((java.util.Collection) value);
            }
        } else if (to == boolean.class) {
            if (value instanceof java.lang.Object) {
                return ObjectConverter.toBool(value);
            }
        } else if (to == char.class) {
            if (value instanceof java.lang.String) {
                return ObjectConverter.toChar((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toChar((byte[]) value);
            }
        } else if (to == java.io.BufferedReader.class) {
            if (value instanceof java.io.File) {
                return IOConverter.toReader((java.io.File) value, exchange);
            }
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toReader((java.nio.file.Path) value, exchange);
            }
        } else if (to == java.io.BufferedWriter.class) {
            if (value instanceof java.io.File) {
                return IOConverter.toWriter((java.io.File) value, exchange);
            }
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toWriter((java.nio.file.Path) value, exchange);
            }
        } else if (to == java.io.File.class) {
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toFile((java.nio.file.Path) value);
            }
        } else if (to == java.io.InputStream.class) {
            if (value instanceof java.util.stream.Stream) {
                return IOConverter.toInputStream((java.util.stream.Stream) value, exchange);
            }
            if (value instanceof org.zenithblox.spi.Resource) {
                return ResourceConverter.toInputStream((org.zenithblox.spi.Resource) value);
            }
            if (value instanceof java.net.URL) {
                return IOConverter.toInputStream((java.net.URL) value);
            }
            if (value instanceof java.io.File) {
                return IOConverter.toInputStream((java.io.File) value);
            }
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toInputStream((java.nio.file.Path) value);
            }
            if (value instanceof java.nio.ByteBuffer) {
                return NIOConverter.toInputStream((java.nio.ByteBuffer) value);
            }
            if (value instanceof java.lang.String) {
                return IOConverter.toInputStream((java.lang.String) value, exchange);
            }
            if (value instanceof java.lang.StringBuffer) {
                return IOConverter.toInputStream((java.lang.StringBuffer) value, exchange);
            }
            if (value instanceof java.lang.StringBuilder) {
                return IOConverter.toInputStream((java.lang.StringBuilder) value, exchange);
            }
            if (value instanceof java.io.BufferedReader) {
                return IOConverter.toInputStream((java.io.BufferedReader) value, exchange);
            }
            if (value instanceof java.io.Reader) {
                return IOConverter.toInputStream((java.io.Reader) value, exchange);
            }
            if (value instanceof byte[]) {
                return IOConverter.toInputStream((byte[]) value);
            }
            if (value instanceof java.io.ByteArrayOutputStream) {
                return IOConverter.toInputStream((java.io.ByteArrayOutputStream) value);
            }
        } else if (to == java.io.ObjectInput.class) {
            if (value instanceof java.io.InputStream) {
                return IOConverter.toObjectInput((java.io.InputStream) value, exchange);
            }
        } else if (to == java.io.ObjectOutput.class) {
            if (value instanceof java.io.OutputStream) {
                return IOConverter.toObjectOutput((java.io.OutputStream) value);
            }
        } else if (to == java.io.OutputStream.class) {
            if (value instanceof java.io.File) {
                return IOConverter.toOutputStream((java.io.File) value);
            }
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toOutputStream((java.nio.file.Path) value);
            }
        } else if (to == java.io.Reader.class) {
            if (value instanceof org.zenithblox.spi.Resource) {
                return ResourceConverter.toReader((org.zenithblox.spi.Resource) value);
            }
            if (value instanceof java.io.InputStream) {
                return IOConverter.toReader((java.io.InputStream) value, exchange);
            }
            if (value instanceof byte[]) {
                return IOConverter.toReader((byte[]) value, exchange);
            }
            if (value instanceof java.lang.String) {
                return IOConverter.toReader((java.lang.String) value);
            }
        } else if (to == java.io.Writer.class) {
            if (value instanceof java.io.OutputStream) {
                return IOConverter.toWriter((java.io.OutputStream) value, exchange);
            }
        } else if (to == java.lang.Boolean.class || to == boolean.class) {
            if (value instanceof java.lang.Object) {
                return ObjectConverter.toBoolean(value);
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toBoolean((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toBoolean((byte[]) value, exchange);
            }
        } else if (to == java.lang.Byte.class || to == byte.class) {
            if (value instanceof java.lang.Number) {
                Object obj = ObjectConverter.toByte((java.lang.Number) value);
                if (obj == null) {
                    return Void.class;
                } else {
                    return obj;
                }
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toByte((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toByte((byte[]) value, exchange);
            }
        } else if (to == java.lang.Character.class || to == char.class) {
            if (value instanceof java.lang.String) {
                return ObjectConverter.toCharacter((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toCharacter((byte[]) value);
            }
        } else if (to == java.lang.Class.class) {
            if (value instanceof java.lang.String) {
                return ObjectConverter.toClass((java.lang.String) value, camelContext);
            }
        } else if (to == java.lang.Double.class || to == double.class) {
            if (value instanceof java.lang.Number) {
                return ObjectConverter.toDouble((java.lang.Number) value);
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toDouble((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toDouble((byte[]) value, exchange);
            }
        } else if (to == java.lang.Float.class || to == float.class) {
            if (value instanceof java.lang.Number) {
                return ObjectConverter.toFloat((java.lang.Number) value);
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toFloat((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toFloat((byte[]) value, exchange);
            }
        } else if (to == java.lang.Integer.class || to == int.class) {
            if (value instanceof java.lang.Number) {
                Object obj = ObjectConverter.toInteger((java.lang.Number) value);
                if (obj == null) {
                    return Void.class;
                } else {
                    return obj;
                }
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toInteger((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toInteger((byte[]) value, exchange);
            }
        } else if (to == java.lang.Iterable.class) {
            if (value instanceof java.lang.Object) {
                return ObjectConverter.iterable(value);
            }
        } else if (to == java.lang.Long.class || to == long.class) {
            if (value instanceof java.time.Duration) {
                return DurationConverter.toMilliSeconds((java.time.Duration) value);
            }
            if (value instanceof java.sql.Timestamp) {
                return SQLConverter.toLong((java.sql.Timestamp) value);
            }
            if (value instanceof java.util.Date) {
                return DateTimeConverter.toLong((java.util.Date) value);
            }
            if (value instanceof java.lang.Number) {
                Object obj = ObjectConverter.toLong((java.lang.Number) value);
                if (obj == null) {
                    return Void.class;
                } else {
                    return obj;
                }
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toLong((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toLong((byte[]) value, exchange);
            }
        } else if (to == java.lang.Number.class) {
            if (value instanceof java.lang.String) {
                return ObjectConverter.toNumber((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toNumber((byte[]) value, exchange);
            }
        } else if (to == java.lang.Short.class || to == short.class) {
            if (value instanceof java.lang.Number) {
                Object obj = ObjectConverter.toShort((java.lang.Number) value);
                if (obj == null) {
                    return Void.class;
                } else {
                    return obj;
                }
            }
            if (value instanceof java.lang.String) {
                return ObjectConverter.toShort((java.lang.String) value);
            }
            if (value instanceof byte[]) {
                return ObjectConverter.toShort((byte[]) value, exchange);
            }
        } else if (to == java.lang.String.class) {
            if (value instanceof java.net.URI) {
                return UriTypeConverter.toString((java.net.URI) value);
            }
            if (value instanceof java.nio.ByteBuffer) {
                return NIOConverter.toString((java.nio.ByteBuffer) value, exchange);
            }
            if (value instanceof java.time.Duration) {
                return DurationConverter.toString((java.time.Duration) value);
            }
            if (value instanceof org.zenithblox.spi.Resource) {
                return ResourceConverter.toString((org.zenithblox.spi.Resource) value, camelContext);
            }
            if (value instanceof char[]) {
                return ObjectConverter.fromCharArray((char[]) value);
            }
            if (value instanceof byte[]) {
                return IOConverter.toString((byte[]) value, exchange);
            }
            if (value instanceof java.io.File) {
                return IOConverter.toString((java.io.File) value, exchange);
            }
            if (value instanceof java.nio.file.Path) {
                return IOConverter.toString((java.nio.file.Path) value, exchange);
            }
            if (value instanceof java.net.URL) {
                return IOConverter.toString((java.net.URL) value, exchange);
            }
            if (value instanceof java.io.BufferedReader) {
                return IOConverter.toString((java.io.BufferedReader) value);
            }
            if (value instanceof java.io.Reader) {
                return IOConverter.toString((java.io.Reader) value);
            }
            if (value instanceof java.io.InputStream) {
                return IOConverter.toString((java.io.InputStream) value, exchange);
            }
            if (value instanceof java.lang.Integer) {
                return ObjectConverter.toString((java.lang.Integer) value);
            }
            if (value instanceof java.lang.Long) {
                return ObjectConverter.toString((java.lang.Long) value);
            }
            if (value instanceof java.lang.Boolean) {
                return ObjectConverter.toString((java.lang.Boolean) value);
            }
            if (value instanceof java.lang.StringBuffer) {
                return ObjectConverter.toString((java.lang.StringBuffer) value);
            }
            if (value instanceof java.lang.StringBuilder) {
                return ObjectConverter.toString((java.lang.StringBuilder) value);
            }
            if (value instanceof java.io.ByteArrayOutputStream) {
                return IOConverter.toString((java.io.ByteArrayOutputStream) value, exchange);
            }
        } else if (to == java.math.BigInteger.class) {
            if (value instanceof java.lang.Object) {
                Object obj = ObjectConverter.toBigInteger(value);
                if (obj == null) {
                    return Void.class;
                } else {
                    return obj;
                }
            }
        } else if (to == java.net.URI.class) {
            if (value instanceof java.lang.CharSequence) {
                return UriTypeConverter.toUri((java.lang.CharSequence) value);
            }
        } else if (to == java.nio.ByteBuffer.class) {
            if (value instanceof byte[]) {
                return NIOConverter.toByteBuffer((byte[]) value);
            }
            if (value instanceof java.io.ByteArrayOutputStream) {
                return NIOConverter.toByteBuffer((java.io.ByteArrayOutputStream) value);
            }
            if (value instanceof java.io.File) {
                return NIOConverter.toByteBuffer((java.io.File) value);
            }
            if (value instanceof java.nio.file.Path) {
                return NIOConverter.toByteBuffer((java.nio.file.Path) value);
            }
            if (value instanceof java.lang.String) {
                return NIOConverter.toByteBuffer((java.lang.String) value, exchange);
            }
            if (value instanceof java.lang.Short) {
                return NIOConverter.toByteBuffer((java.lang.Short) value);
            }
            if (value instanceof java.lang.Integer) {
                return NIOConverter.toByteBuffer((java.lang.Integer) value);
            }
            if (value instanceof java.lang.Long) {
                return NIOConverter.toByteBuffer((java.lang.Long) value);
            }
            if (value instanceof java.lang.Float) {
                return NIOConverter.toByteBuffer((java.lang.Float) value);
            }
            if (value instanceof java.lang.Double) {
                return NIOConverter.toByteBuffer((java.lang.Double) value);
            }
            if (value instanceof java.io.InputStream) {
                return IOConverter.covertToByteBuffer((java.io.InputStream) value);
            }
        } else if (to == java.nio.file.Path.class) {
            if (value instanceof java.io.File) {
                return IOConverter.toPath((java.io.File) value);
            }
        } else if (to == java.sql.Timestamp.class) {
            if (value instanceof java.lang.Long) {
                return SQLConverter.toTimestamp((java.lang.Long) value);
            }
        } else if (to == java.time.Duration.class) {
            if (value instanceof java.lang.Long) {
                return DurationConverter.toDuration((java.lang.Long) value);
            }
            if (value instanceof java.lang.String) {
                return DurationConverter.toDuration((java.lang.String) value);
            }
        } else if (to == java.util.ArrayList.class) {
            if (value instanceof java.util.Iterator) {
                return CollectionConverter.toArrayList((java.util.Iterator) value);
            }
            if (value instanceof java.lang.Iterable) {
                return CollectionConverter.toArrayList((java.lang.Iterable) value);
            }
        } else if (to == java.util.Collection.class) {
            if (value instanceof java.util.Map) {
                return CollectionConverter.toCollection((java.util.Map) value);
            }
        } else if (to == java.util.Date.class) {
            if (value instanceof java.lang.Long) {
                return DateTimeConverter.toDate((java.lang.Long) value);
            }
        } else if (to == java.util.HashMap.class) {
            if (value instanceof java.util.Map) {
                return CollectionConverter.toHashMap((java.util.Map) value);
            }
        } else if (to == java.util.Hashtable.class) {
            if (value instanceof java.util.Map) {
                return CollectionConverter.toHashtable((java.util.Map) value);
            }
        } else if (to == java.util.Iterator.class) {
            if (value instanceof java.lang.Object) {
                return ObjectConverter.iterator(value);
            }
        } else if (to == java.util.List.class) {
            if (value instanceof java.lang.Object[]) {
                return CollectionConverter.toList((java.lang.Object[]) value);
            }
            if (value instanceof java.util.Collection) {
                return CollectionConverter.toList((java.util.Collection) value);
            }
            if (value instanceof java.lang.Iterable) {
                return CollectionConverter.toList((java.lang.Iterable) value);
            }
            if (value instanceof java.util.Iterator) {
                return CollectionConverter.toList((java.util.Iterator) value);
            }
        } else if (to == java.util.Properties.class) {
            if (value instanceof java.util.Map) {
                return CollectionConverter.toProperties((java.util.Map) value);
            }
            if (value instanceof java.io.File) {
                return IOConverter.toProperties((java.io.File) value);
            }
            if (value instanceof java.io.InputStream) {
                return IOConverter.toProperties((java.io.InputStream) value);
            }
            if (value instanceof java.io.Reader) {
                return IOConverter.toProperties((java.io.Reader) value);
            }
        } else if (to == java.util.Set.class) {
            if (value instanceof java.lang.Object[]) {
                return CollectionConverter.toSet((java.lang.Object[]) value);
            }
            if (value instanceof java.util.Collection) {
                return CollectionConverter.toSet((java.util.Collection) value);
            }
            if (value instanceof java.util.Map) {
                return CollectionConverter.toSet((java.util.Map) value);
            }
        } else if (to == java.util.TimeZone.class) {
            if (value instanceof java.lang.String) {
                return DateTimeConverter.toTimeZone((java.lang.String) value);
            }
        } else if (to == java.util.concurrent.TimeUnit.class) {
            if (value instanceof java.lang.String) {
                return DateTimeConverter.toTimeUnit((java.lang.String) value);
            }
        } else if (to == org.zenithblox.Processor.class) {
            if (value instanceof org.zenithblox.Expression) {
                return ZwangineConverter.toProcessor((org.zenithblox.Expression) value);
            }
            if (value instanceof org.zenithblox.Predicate) {
                return ZwangineConverter.toProcessor((org.zenithblox.Predicate) value);
            }
        } else if (to == org.zenithblox.spi.Resource.class) {
            if (value instanceof java.lang.String) {
                return ResourceConverter.toResource((java.lang.String) value, camelContext);
            }
        }
        return null;
    }

    private void doRegistration(TypeConverterRegistry registry) {
        registry.addConverter(new TypeConvertible<>(ByteBuffer.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(org.zenithblox.spi.Resource.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.io.BufferedReader.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.io.Reader.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.io.ByteArrayOutputStream.class, byte[].class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, char[].class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, char[].class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Collection.class, java.lang.Object[].class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object.class, boolean.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, char.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, char.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.io.BufferedReader.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.io.BufferedReader.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.io.BufferedWriter.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.io.BufferedWriter.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.io.File.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.stream.Stream.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(org.zenithblox.spi.Resource.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.net.URL.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.ByteBuffer.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.StringBuffer.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.StringBuilder.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.BufferedReader.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.Reader.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.ByteArrayOutputStream.class, java.io.InputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, java.io.ObjectInput.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.OutputStream.class, java.io.ObjectOutput.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.io.OutputStream.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.io.OutputStream.class), this);
        registry.addConverter(new TypeConvertible<>(org.zenithblox.spi.Resource.class, java.io.Reader.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, java.io.Reader.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.io.Reader.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.io.Reader.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.OutputStream.class, java.io.Writer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object.class, java.lang.Boolean.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Boolean.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Boolean.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Number.class, java.lang.Byte.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Byte.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Byte.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Character.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Character.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Class.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Number.class, java.lang.Double.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Double.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Double.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Number.class, java.lang.Float.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Float.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Float.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Number.class, java.lang.Integer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Integer.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Integer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object.class, java.lang.Iterable.class), this);
        registry.addConverter(new TypeConvertible<>(java.time.Duration.class, java.lang.Long.class), this);
        registry.addConverter(new TypeConvertible<>(java.sql.Timestamp.class, java.lang.Long.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Date.class, java.lang.Long.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Number.class, java.lang.Long.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Long.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Long.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Number.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Number.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Number.class, java.lang.Short.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.lang.Short.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.Short.class), this);
        registry.addConverter(new TypeConvertible<>(java.net.URI.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.ByteBuffer.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.time.Duration.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(org.zenithblox.spi.Resource.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(char[].class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.net.URL.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.BufferedReader.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.Reader.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Integer.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Long.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Boolean.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.StringBuffer.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.StringBuilder.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.ByteArrayOutputStream.class, java.lang.String.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object.class, java.math.BigInteger.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.CharSequence.class, java.net.URI.class), this);
        registry.addConverter(new TypeConvertible<>(byte[].class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.ByteArrayOutputStream.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.nio.file.Path.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Short.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Integer.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Long.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Float.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Double.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, java.nio.ByteBuffer.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.nio.file.Path.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Long.class, java.sql.Timestamp.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Long.class, java.time.Duration.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.time.Duration.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Iterator.class, java.util.ArrayList.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Iterable.class, java.util.ArrayList.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Map.class, java.util.Collection.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Long.class, java.util.Date.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Map.class, java.util.HashMap.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Map.class, java.util.Hashtable.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object.class, java.util.Iterator.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object[].class, java.util.List.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Collection.class, java.util.List.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Iterable.class, java.util.List.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Iterator.class, java.util.List.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Map.class, java.util.Properties.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.File.class, java.util.Properties.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.InputStream.class, java.util.Properties.class), this);
        registry.addConverter(new TypeConvertible<>(java.io.Reader.class, java.util.Properties.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.Object[].class, java.util.Set.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Collection.class, java.util.Set.class), this);
        registry.addConverter(new TypeConvertible<>(java.util.Map.class, java.util.Set.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.util.TimeZone.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, java.util.concurrent.TimeUnit.class), this);
        registry.addConverter(new TypeConvertible<>(org.zenithblox.Expression.class, org.zenithblox.Processor.class), this);
        registry.addConverter(new TypeConvertible<>(org.zenithblox.Predicate.class, org.zenithblox.Processor.class), this);
        registry.addConverter(new TypeConvertible<>(java.lang.String.class, org.zenithblox.spi.Resource.class), this);
    }

    public TypeConverter lookup(Class<?> to, Class<?> from) {
        if (to == byte[].class) {
            if (from == java.nio.ByteBuffer.class) {
                return this;
            }
            if (from == org.zenithblox.spi.Resource.class) {
                return this;
            }
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
            if (from == java.io.BufferedReader.class) {
                return this;
            }
            if (from == java.io.Reader.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == java.io.InputStream.class) {
                return this;
            }
            if (from == java.io.ByteArrayOutputStream.class) {
                return this;
            }
        } else if (to == char[].class) {
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Object[].class) {
            if (from == java.util.Collection.class) {
                return this;
            }
        } else if (to == boolean.class) {
            if (from == java.lang.Object.class) {
                return this;
            }
        } else if (to == char.class) {
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.io.BufferedReader.class) {
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
        } else if (to == java.io.BufferedWriter.class) {
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
        } else if (to == java.io.File.class) {
            if (from == java.nio.file.Path.class) {
                return this;
            }
        } else if (to == java.io.InputStream.class) {
            if (from == java.util.stream.Stream.class) {
                return this;
            }
            if (from == org.zenithblox.spi.Resource.class) {
                return this;
            }
            if (from == java.net.URL.class) {
                return this;
            }
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
            if (from == java.nio.ByteBuffer.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == java.lang.StringBuffer.class) {
                return this;
            }
            if (from == java.lang.StringBuilder.class) {
                return this;
            }
            if (from == java.io.BufferedReader.class) {
                return this;
            }
            if (from == java.io.Reader.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
            if (from == java.io.ByteArrayOutputStream.class) {
                return this;
            }
        } else if (to == java.io.ObjectInput.class) {
            if (from == java.io.InputStream.class) {
                return this;
            }
        } else if (to == java.io.ObjectOutput.class) {
            if (from == java.io.OutputStream.class) {
                return this;
            }
        } else if (to == java.io.OutputStream.class) {
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
        } else if (to == java.io.Reader.class) {
            if (from == org.zenithblox.spi.Resource.class) {
                return this;
            }
            if (from == java.io.InputStream.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
        } else if (to == java.io.Writer.class) {
            if (from == java.io.OutputStream.class) {
                return this;
            }
        } else if (to == java.lang.Boolean.class || to == boolean.class) {
            if (from == java.lang.Object.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Byte.class || to == byte.class) {
            if (from == java.lang.Number.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Character.class || to == char.class) {
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Class.class) {
            if (from == java.lang.String.class) {
                return this;
            }
        } else if (to == java.lang.Double.class || to == double.class) {
            if (from == java.lang.Number.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Float.class || to == float.class) {
            if (from == java.lang.Number.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Integer.class || to == int.class) {
            if (from == java.lang.Number.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Iterable.class) {
            if (from == java.lang.Object.class) {
                return this;
            }
        } else if (to == java.lang.Long.class || to == long.class) {
            if (from == java.time.Duration.class) {
                return this;
            }
            if (from == java.sql.Timestamp.class) {
                return this;
            }
            if (from == java.util.Date.class) {
                return this;
            }
            if (from == java.lang.Number.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Number.class) {
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.Short.class || to == short.class) {
            if (from == java.lang.Number.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
        } else if (to == java.lang.String.class) {
            if (from == java.net.URI.class) {
                return this;
            }
            if (from == java.nio.ByteBuffer.class) {
                return this;
            }
            if (from == java.time.Duration.class) {
                return this;
            }
            if (from == org.zenithblox.spi.Resource.class) {
                return this;
            }
            if (from == char[].class) {
                return this;
            }
            if (from == byte[].class) {
                return this;
            }
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
            if (from == java.net.URL.class) {
                return this;
            }
            if (from == java.io.BufferedReader.class) {
                return this;
            }
            if (from == java.io.Reader.class) {
                return this;
            }
            if (from == java.io.InputStream.class) {
                return this;
            }
            if (from == java.lang.Integer.class) {
                return this;
            }
            if (from == java.lang.Long.class) {
                return this;
            }
            if (from == java.lang.Boolean.class) {
                return this;
            }
            if (from == java.lang.StringBuffer.class) {
                return this;
            }
            if (from == java.lang.StringBuilder.class) {
                return this;
            }
            if (from == java.io.ByteArrayOutputStream.class) {
                return this;
            }
        } else if (to == java.math.BigInteger.class) {
            if (from == java.lang.Object.class) {
                return this;
            }
        } else if (to == java.net.URI.class) {
            if (from == java.lang.CharSequence.class) {
                return this;
            }
        } else if (to == java.nio.ByteBuffer.class) {
            if (from == byte[].class) {
                return this;
            }
            if (from == java.io.ByteArrayOutputStream.class) {
                return this;
            }
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.nio.file.Path.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
            if (from == java.lang.Short.class) {
                return this;
            }
            if (from == java.lang.Integer.class) {
                return this;
            }
            if (from == java.lang.Long.class) {
                return this;
            }
            if (from == java.lang.Float.class) {
                return this;
            }
            if (from == java.lang.Double.class) {
                return this;
            }
            if (from == java.io.InputStream.class) {
                return this;
            }
        } else if (to == java.nio.file.Path.class) {
            if (from == java.io.File.class) {
                return this;
            }
        } else if (to == java.sql.Timestamp.class) {
            if (from == java.lang.Long.class) {
                return this;
            }
        } else if (to == java.time.Duration.class) {
            if (from == java.lang.Long.class) {
                return this;
            }
            if (from == java.lang.String.class) {
                return this;
            }
        } else if (to == java.util.ArrayList.class) {
            if (from == java.util.Iterator.class) {
                return this;
            }
            if (from == java.lang.Iterable.class) {
                return this;
            }
        } else if (to == java.util.Collection.class) {
            if (from == java.util.Map.class) {
                return this;
            }
        } else if (to == java.util.Date.class) {
            if (from == java.lang.Long.class) {
                return this;
            }
        } else if (to == java.util.HashMap.class) {
            if (from == java.util.Map.class) {
                return this;
            }
        } else if (to == java.util.Hashtable.class) {
            if (from == java.util.Map.class) {
                return this;
            }
        } else if (to == java.util.Iterator.class) {
            if (from == java.lang.Object.class) {
                return this;
            }
        } else if (to == java.util.List.class) {
            if (from == java.lang.Object[].class) {
                return this;
            }
            if (from == java.util.Collection.class) {
                return this;
            }
            if (from == java.lang.Iterable.class) {
                return this;
            }
            if (from == java.util.Iterator.class) {
                return this;
            }
        } else if (to == java.util.Properties.class) {
            if (from == java.util.Map.class) {
                return this;
            }
            if (from == java.io.File.class) {
                return this;
            }
            if (from == java.io.InputStream.class) {
                return this;
            }
            if (from == java.io.Reader.class) {
                return this;
            }
        } else if (to == java.util.Set.class) {
            if (from == java.lang.Object[].class) {
                return this;
            }
            if (from == java.util.Collection.class) {
                return this;
            }
            if (from == java.util.Map.class) {
                return this;
            }
        } else if (to == java.util.TimeZone.class) {
            if (from == java.lang.String.class) {
                return this;
            }
        } else if (to == java.util.concurrent.TimeUnit.class) {
            if (from == java.lang.String.class) {
                return this;
            }
        } else if (to == org.zenithblox.Processor.class) {
            if (from == org.zenithblox.Expression.class) {
                return this;
            }
            if (from == org.zenithblox.Predicate.class) {
                return this;
            }
        } else if (to == org.zenithblox.spi.Resource.class) {
            if (from == java.lang.String.class) {
                return this;
            }
        }
        return null;
    }

}
