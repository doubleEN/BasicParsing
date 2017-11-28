package com.mjx.syntax;

import java.util.Arrays;

public class RuleChain {
    private String[] singleSymbols;
    private String[] tailRHS;

    public RuleChain(String[] singleSymbols,String[] tailRHS) {
        this.singleSymbols = singleSymbols;
        this.tailRHS = tailRHS;
    }

    public Rule[] getRuleChain(){
        Rule[] ruleChain = new Rule[this.singleSymbols.length];
        for (int index=0;index<ruleChain.length-1;++index) {
            ruleChain[index] = new Rule(singleSymbols[index], singleSymbols[index + 1]);
        }
        ruleChain[ruleChain.length - 1] = new Rule(singleSymbols[singleSymbols.length - 1], tailRHS);
        return ruleChain;
    }

    public Rule getEqualRule(){
        return new Rule(singleSymbols[0], tailRHS);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RuleChain)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        RuleChain chain = (RuleChain) obj;

        if (Arrays.equals(this.singleSymbols, chain.singleSymbols) && Arrays.equals(this.tailRHS, chain.tailRHS)) {
            return true;
        }
        return false;
    }

    public int getChainLen() {
        return this.singleSymbols.length+1;
    }

    public int getTailLen() {
        return this.tailRHS.length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.singleSymbols) + Arrays.hashCode(this.tailRHS);
    }

    @Override
    public String toString() {
        String chain = "";
        for (String str : this.singleSymbols) {
            chain+=str+"-->";
        }
        chain = chain + Arrays.toString(this.tailRHS);
        return chain;
    }
}
