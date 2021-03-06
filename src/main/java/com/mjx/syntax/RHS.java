package com.mjx.syntax;

import java.util.Arrays;

/**
 * 文法派生规则的 Right hand side
 */
public class RHS {

    private String[] values;

    public RHS(String... RHS) {
        this.values = RHS;
    }

    public int len() {
        return values.length;
    }

    /**
     * 有序获得RHS的各个值
     */
    public String[] getValues() {
        return this.values;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RHS)) {
            return false;
        }

        return Arrays.equals(this.values, ((RHS) obj).values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.values);
    }
}
