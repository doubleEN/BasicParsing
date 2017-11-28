package com.mjx.syntax;

import java.util.Arrays;

/**
 * 规则链类，存储 A-->B-->C-->d;A-->B-->C-->[D E] 这样的链式结构
 */
public class RuleChain {
    /**
     * A-->B-->C-->[D E]中的[A,B,C]
     */
    private String[] singleSymbols;

    /**
     * A-->B-->C-->[D E]中的[D,E]
     */
    private String[] tailRHS;

    public RuleChain(String[] singleSymbols,String[] tailRHS) {
        this.singleSymbols = singleSymbols;
        this.tailRHS = tailRHS;
    }

    /**
     *  规则链提炼出有序的对应规则序列
     */
    public Rule[] getRuleChain(){
        Rule[] ruleChain = new Rule[this.singleSymbols.length];
        for (int index=0;index<ruleChain.length-1;++index) {
            ruleChain[index] = new Rule(singleSymbols[index], singleSymbols[index + 1]);
        }
        ruleChain[ruleChain.length - 1] = new Rule(singleSymbols[singleSymbols.length - 1], tailRHS);
        return ruleChain;
    }

    /**
     * 返回A-->B-->C-->[D,E]的等价规则 A-->[D,E]
     */
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

    /**
     * 返回规则链的长度
     */
    public int getChainLen() {
        return this.singleSymbols.length+1;
    }

    /**
     * 返回规则链末端的长度，如A-->B-->C-->[D E]的末端长度为2
     */
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
