package com.mjx;

public class LHS {

    private String value;

    public LHS(String symbol) {
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
}
