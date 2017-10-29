package com.mjx;

public class Rule {

    private LHS lhs;

    private RHS rhs;

    public Rule() {
    }

    public Rule(LHS lhs, RHS rhs) {
        this.rhs=rhs;
        this.lhs=lhs;
    }

    public void setLhs(LHS lhs) {
        this.lhs=lhs;
    }

    public void setRhs(RHS rhs) {
        this.rhs=rhs;
    }

    public int lenOfRHS() {
        return this.rhs.len();
    }

    public String getLHS() {
        return this.lhs.getLHS();
    }

    public String[] getRHS() {
        return this.rhs.getRHS();
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
