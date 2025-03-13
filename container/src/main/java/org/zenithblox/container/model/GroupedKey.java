package org.zenithblox.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupedKey {
    private String projectId;
    private String env;
    private String key;

    public static String create(String projectId, String env, String key) {
        return new GroupedKey(projectId, env, key).getCacheKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupedKey that = (GroupedKey) o;

        if (!projectId.equals(that.projectId)) return false;
        if (!env.equals(that.env)) return false;
        return key.equals(that.key);
    }

    public String getCacheKey() {
        return projectId + ":" + env + ":" + key;
    }

    @Override
    public int hashCode() {
        return getCacheKey().hashCode();
    }

    @Override
    public String toString() {
        return "GroupedKey{" + getCacheKey() + '}';
    }

}
