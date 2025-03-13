
package org.zenithblox.component.file;

import org.zenithblox.Exchange;

import java.io.File;
import java.io.IOException;

/**
 * File binding with the {@link File} type.
 */
public class FileBinding implements GenericFileBinding<File> {
    private File body;
    private byte[] content;

    @Override
    public Object getBody(GenericFile<File> file) {
        // if file content has been loaded then return it
        if (content != null) {
            return content;
        }

        // as we use java.io.File itself as the body (not loading its content
        // into an OutputStream etc.)
        // we just store a java.io.File handle to the actual file denoted by the
        // file.getAbsoluteFilePath. We must do this as the original file
        // consumed can be renamed before
        // being processed (preMove) and thus it points to an invalid file
        // location.
        // GenericFile#getAbsoluteFilePath() is always up-to-date and thus we
        // use it to create a file
        // handle that is correct
        if (body == null || !file.getAbsoluteFilePath().equals(body.getAbsolutePath())) {
            body = new File(file.getAbsoluteFilePath());
        }
        return body;
    }

    @Override
    public void setBody(GenericFile<File> file, Object body) {
        // noop
    }

    @Override
    public void loadContent(Exchange exchange, GenericFile<?> file) throws IOException {
        if (content == null) {
            // use converter to convert the content into memory as byte array
            Object data = GenericFileConverter.convertTo(byte[].class, exchange, file,
                    exchange.getContext().getTypeConverterRegistry());
            if (data != null) {
                content = (byte[]) data;
            }
        }
    }
}
