
package org.zenithblox.component.file;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.annotations.Component;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.StringHelper;

import java.io.File;
import java.util.Map;

@Component("file")
public class FileComponent extends GenericFileComponent<File> {
    /**
     * GenericFile property on Zwangine Exchanges.
     */
    public static final String FILE_EXCHANGE_FILE = "ZwangineFileExchangeFile";

    /**
     * Default zwangine lock filename postfix
     */
    public static final String DEFAULT_LOCK_FILE_POSTFIX = ".zwangineLock";

    public FileComponent() {
    }

    public FileComponent(ZwangineContext context) {
        super(context);
    }

    @Override
    protected GenericFileEndpoint<File> buildFileEndpoint(String uri, String remaining, Map<String, Object> parameters)
            throws Exception {
        // the starting directory must be a static (not containing dynamic
        // expressions)
        if (StringHelper.hasStartToken(remaining, "simple")) {
            throw new IllegalArgumentException(
                    "Invalid directory: " + remaining + ". Dynamic expressions with ${ } placeholders is not allowed."
                                               + " Use the fileName option to set the dynamic expression.");
        }

        File file = new File(remaining);

        FileEndpoint result = new FileEndpoint(uri, this);
        result.setFile(file);

        GenericFileConfiguration config = new GenericFileConfiguration();
        config.setDirectory(FileUtil.isAbsolute(file) ? file.getAbsolutePath() : file.getPath());
        result.setConfiguration(config);

        return result;
    }

    @Override
    protected void afterPropertiesSet(GenericFileEndpoint<File> endpoint) throws Exception {
        // noop
    }

}
