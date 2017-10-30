package com.mjx;

import java.util.Arrays;

public class RHS {

    private String[] values;

    private Dictionary dict;

    public RHS(String... RHS) {
        this.dict=dict;
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
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return Arrays.toString(this.values);
    }
}
