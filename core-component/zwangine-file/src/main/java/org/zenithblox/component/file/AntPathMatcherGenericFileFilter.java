
package org.zenithblox.component.file;

/**
 * File filter using AntPathMatcher.
 * <p/>
 * Exclude take precedence over includes. If a file match both exclude and include it will be regarded as excluded.
 *
 * @param      <T>
 * @deprecated     use {@link AntFilter}
 */
@Deprecated
public class AntPathMatcherGenericFileFilter<T> implements GenericFileFilter<T> {

    private final AntPathMatcherFileFilter filter;

    public AntPathMatcherGenericFileFilter() {
        filter = new AntPathMatcherFileFilter();
    }

    public AntPathMatcherGenericFileFilter(String... includes) {
        filter = new AntPathMatcherFileFilter();
        filter.setIncludes(includes);
    }

    @Override
    public boolean accept(GenericFile<T> file) {
        // directories should always be accepted by ANT path matcher
        if (file.isDirectory()) {
            return true;
        }

        String path = file.getRelativeFilePath();
        return filter.acceptPathName(path);
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
     * Sets case sensitive flag on {@link org.zenithblox.component.file.AntPathMatcherFileFilter}
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
