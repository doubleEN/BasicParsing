package com.mjx.syntax;

public class Rule {

    private LHS lhs;

    private RHS rhs;

    public Rule(LHS lhs, RHS rhs) {
        this.rhs = rhs;
        this.lhs = lhs;
    }

    public Rule(String lhs, String... rhs) {
        this.rhs = new RHS(rhs);
        this.lhs = new LHS(lhs);
    }

    public void setLhs(LHS lhs) {
        this.lhs = lhs;
    }

    public void setRhs(RHS rhs) {
        this.rhs = rhs;
    }

    public int lenOfRHS() {
        return this.rhs.len();
    }

    public LHS getLHS() {
        return this.lhs;
    }

    public RHS getRHS() {
        return this.rhs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Rule)) {
            return false;
        }
        return this.rhs.equals(((Rule) obj).rhs) && this.lhs.equals(((Rule) obj).lhs);
    }

    @Override
    public int hashCode() {
        return this.rhs.hashCode()+this.lhs.hashCode();
    }

    @Override
    public String toString() {
        return this.lhs.toString() + "-->" + this.rhs.toString();
    }
}
