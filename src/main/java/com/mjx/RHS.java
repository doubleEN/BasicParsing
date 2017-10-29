package com.mjx;

public class RHS {

    private String[] values;

    public RHS(String... RHS) {
        this.values=RHS;
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
}
