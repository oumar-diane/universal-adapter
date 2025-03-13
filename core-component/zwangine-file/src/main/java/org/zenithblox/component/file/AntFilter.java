
package org.zenithblox.component.file;

/**
 * File filter using AntPathMatcher.
 * <p/>
 * Exclude take precedence over includes. If a file match both exclude and include it will be regarded as excluded.
 */
public class AntFilter {

    private final AntPathMatcherFileFilter filter;

    public AntFilter() {
        filter = new AntPathMatcherFileFilter();
    }

    public AntFilter(String... includes) {
        filter = new AntPathMatcherFileFilter();
        filter.setIncludes(includes);
    }

    public boolean accept(boolean directory, String relativeFilePath) {
        // directories should always be accepted by ANT path matcher
        if (directory) {
            return true;
        }
        return filter.acceptPathName(relativeFilePath);
    }

    public String[] getExcludes() {
        return filter.getExcludes();
    }

    public void setExcludes(String[] excludes) {
        filter.setExcludes(excludes);
    }

    public String[] getIncludes() {
        return filter.getIncludes();
    }

    public void setIncludes(String[] includes) {
        filter.setIncludes(includes);
    }

    /**
     * Sets excludes using a single string where each element can be separated with comma
     */
    public void setExcludes(String excludes) {
        filter.setExcludes(excludes);
    }

    /**
     * Sets includes using a single string where each element can be separated with comma
     */
    public void setIncludes(String includes) {
        filter.setIncludes(includes);
    }

    /**
     * Sets case sensitive flag on {@link AntPathMatcherFileFilter}
     * <p/>
     * Is by default turned on <tt>true</tt>.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        filter.setCaseSensitive(caseSensitive);
    }

    public boolean isCaseSensitive() {
        return filter.isCaseSensitive();
    }
}
