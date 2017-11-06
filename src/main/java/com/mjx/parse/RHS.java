package com.mjx.parse;

import java.util.Arrays;

public class RHS {

    private String[] values;

    public RHS(String... RHS) {
        this.values = RHS;
    }

    public int len() {
        return values.length;
    }

    /**
     * 暴露了RHS的存储结构
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
