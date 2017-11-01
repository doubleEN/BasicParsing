package com.mjx.parsing;

import com.mjx.Dictionary;

import java.util.Arrays;

public class RHS {

    private String[] values;

    private Dictionary dict;

    public RHS(String... RHS) {
        this.values = RHS;
    }

    public int len() {
        return values.length;
    }

    /**
     * 暴露了RHS的存储结构
     */
    public String[] getRHS() {
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

        if (dict == null) {
            return Arrays.equals(this.values, ((RHS) obj).values);
        }
        return false;
    }

    @Override
    public int hashCode() {
        String lhs = Arrays.toString(this.values);
        lhs = lhs.substring(1, lhs.length() - 1);
        int h = 0;
        if (this.dict == null) {
            if (h == 0 && lhs.length() > 0) {
                char val[] = lhs.toCharArray();
                for (int i = 0; i < lhs.length(); i++) {
                    h = 31 * h + val[i];
                }
            }
        }
        return h;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.values);
    }
}
