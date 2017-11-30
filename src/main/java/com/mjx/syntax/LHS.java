package com.mjx.syntax;

/**
 * 文法派生规则的 Left hand side
 */
public class LHS {

    /**
     * LHS值
     */
    private String value;

    public LHS(String symbol) {
        this.value = symbol;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LHS)) {
            return false;
        }

        return this.value.equals(((LHS) obj).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString() {
        return this.value;
    }
}
