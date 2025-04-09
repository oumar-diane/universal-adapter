package org.zenithblox.util;

import java.util.Objects;

public class KeyValuePair<L, R> {
    private final L left;
    private final R right;

    public KeyValuePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyValuePair<?, ?> that = (KeyValuePair<?, ?>) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
