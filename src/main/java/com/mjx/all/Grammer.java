package com.mjx.all;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Grammer {

    private Map<Rule, Integer> CFG = new HashMap<Rule, Integer>();

    private Map<Rule, Integer> CNF = new HashMap<Rule, Integer>();

    private Map<LHS, RHS[]> ltrCFG = new HashMap<LHS, RHS[]>();

    private Map<RHS[], LHS> rtlCFG = new HashMap<RHS[], LHS>();

    private Map<LHS, RHS[]> ltrCNF = new HashMap<LHS, RHS[]>();

    private Map<RHS[], LHS> rtlCNF = new HashMap<RHS[], LHS>();

    private Dictionary dict;

    /**
     * 将句法树解析成规则,怎么让文法类遍历节点
     */
    abstract void parseTree(PhraseStructureTree root);

    /**
     * 扫描生成词典
     */
    abstract void generateDict(PhraseStructureTree root);

    public boolean containCFGRule(Rule rule) {
        return this.CFG.containsKey(rule);
    }

    public boolean containCNFRule(Rule rule) {
        return this.CNF.containsKey(rule);
    }

    /**
     * 由RHS查找其所有LHS
     */
    public abstract RHS[]getRHSOnLHS(LHS LHS);

    public abstract RHS[]getRHSOnLHS(String LHS);

    /**
     * 由RHS查找其所有LHS
     */
    public abstract LHS getLHSOnRHS(RHS RHS);

    public abstract LHS getLHSOnRHS(String RHS);

    public abstract boolean addCFGRule(Rule rule);

    public abstract boolean addCFGRuleSet(Set<Rule> ruleSet);

    public abstract boolean addCNFRule(Rule rule);

    public abstract boolean addCFGMapping(Rule rule);

    public abstract boolean addCNFMapping(Rule rule);

    /**
     * CFG转CNF
     */
    public abstract void convertToCNF();

    /**
     * CNF转CFG
     */
    public abstract void convertToCFG();

}
