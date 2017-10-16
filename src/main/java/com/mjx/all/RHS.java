package com.mjx.all;

public class RHS {

    private int[] values;

    public RHS(int... RHS) {
        this.values=RHS;
    }

    public int len() {
        return values.length;
    }

    /**
     * 暴露了RHS的存储结构
     */
    public int[] getRHS() {
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
}
