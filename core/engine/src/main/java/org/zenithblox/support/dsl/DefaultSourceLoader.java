package org.zenithblox.support.dsl;


import org.zenithblox.spi.Resource;
import org.zenithblox.util.IOHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Default {@link SourceLoader}.
 */
public class DefaultSourceLoader implements SourceLoader {

    @Override
    public String loadResource(Resource resource) throws IOException {
        InputStream in = resource.getInputStream();

        StringBuilder builder = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(in);
        boolean first = true;
        try {
            BufferedReader reader = IOHelper.buffered(isr);
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    // we need to skip first line if it starts with a special script marker for camel-jbang in pipe mode
                    builder.append(line);
                    builder.append("\n");
                    first = false;
                } else {
                    break;
                }
            }
            return builder.toString();
        } finally {
            IOHelper.close(isr, in);
        }
    }
}
