
package org.zenithblox.component.file;

import org.zenithblox.util.AntPathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter using {@link AntPathMatcher}.
 * <p/>
 * Exclude take precedence over includes. If a file match both exclude and include it will be regarded as excluded.
 */
public class AntPathMatcherFileFilter implements FileFilter {
    private static final Logger LOG = LoggerFactory.getLogger(AntPathMatcherFileFilter.class);

    private final AntPathMatcher matcher = new AntPathMatcher();
    private String[] excludes;
    private String[] includes;
    private boolean caseSensitive = true;

    @Override
    public boolean accept(File pathname) {
        return acceptPathName(pathname.getPath());
    }

    /**
     * Accepts the given file by the path name
     *
     * @param  path the path
     * @return      <tt>true</tt> if accepted, <tt>false</tt> if not
     */
    public boolean acceptPathName(String path) {
        // must use single / as path separators
        path = path.replace(File.separatorChar, '/');

        LOG.trace("Filtering file: {}", path);

        // excludes take precedence
        if (excludes != null) {
            for (String exclude : excludes) {
                if (matcher.match(exclude, path, caseSensitive)) {
                    // something to exclude so we cant accept it
                    LOG.trace("File is excluded: {}", path);
                    return false;
                }
            }
        }

        if (includes != null) {
            for (String include : includes) {
                if (matcher.match(include, path, caseSensitive)) {
                    // something to include so we accept it
                    LOG.trace("File is included: {}", path);
                    return true;
                }
            }
        }

        if (excludes != null && includes == null) {
            // if the user specified excludes but no includes, presumably we
            // should include by default
            return true;
        }

        // nothing to include so we can't accept it
        return false;
    }

    /**
     * @return <tt>true</tt> if case sensitive pattern matching is on, <tt>false</tt> if case sensitive pattern matching
     *         is off.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets Whether or not pattern matching should be case sensitive
     * <p/>
     * Is by default turned on <tt>true</tt>.
     *
     * @param caseSensitive <tt>false</tt> to disable case sensitive pattern matching
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    /**
     * Sets excludes using a single string where each element can be separated with comma
     */
    public void setExcludes(String excludes) {
        setExcludes(excludes.split(","));
    }

    /**
     * Sets includes using a single string where each element can be separated with comma
     */
    public void setIncludes(String includes) {
        setIncludes(includes.split(","));
    }

}
