package com.mjx;

public class LHS {

    private String value;

    private Dictionary dict;

    public LHS(String symbol) {
        this.dict=dict;
        this.value=symbol;
    }

    public String getLHS() {
        return this.value;
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
        return this.value;
    }
}
