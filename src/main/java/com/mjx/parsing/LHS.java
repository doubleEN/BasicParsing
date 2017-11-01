package com.mjx.parsing;

import com.mjx.Dictionary;

public class LHS {

    private String value;

    private Dictionary dict;

    public LHS(String symbol) {
        this.value = symbol;
    }

    public String getLHS() {
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

        if (dict == null) {
            return this.value.equals(((LHS) obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 0;
        if (dict == null) {
            if (h == 0 && value.length() > 0) {
                char val[] = value.toCharArray();
                for (int i = 0; i < value.length(); i++) {
                    h = 31 * h + val[i];
                }
            }
        }
        return h;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
