package com.mjx.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Grammer {

    private Map<Rule, Integer> CFG = new HashMap<Rule, Integer>();

    private Map<Rule, Integer> CNF = new HashMap<Rule, Integer>();

    private Map<LHS, RHS> ltrCFG = new HashMap<LHS, RHS>();

    private Map<RHS, LHS> rtlCFG = new HashMap<RHS, LHS>();

    private Map<LHS, RHS[]> ltrCNF = new HashMap<LHS, RHS[]>();

    private Map<RHS, LHS> rtlCNF = new HashMap<RHS, LHS>();

    /**
     * 是否包含CFG规则
     */
    public boolean containCFGRule(Rule rule) {
        return this.CFG.containsKey(rule);
    }

    /**
     * 是否包含CNF规则
     */
    public boolean containCNFRule(Rule rule) {
        return this.CNF.containsKey(rule);
    }

    /*
     * 由RHS查找其所有LHS
     */
    public abstract RHS[]getRHSOnLHS(LHS LHS);

    public abstract RHS[]getRHSOnLHS(String LHS);

    /*
     * 由RHS查找其所有LHS
     */
    public abstract LHS getLHSOnRHS(RHS RHS);

    public abstract LHS getLHSOnRHS(String RHS);

    /**
     * 添加单个CFG规则
     */
    public abstract boolean addCFGRule(Rule rule);

    /**
     * 添加CFG规则集
     */
    public abstract boolean addCFGRuleSet(List<Rule> rules);

    /**
     * 添加CFG规则集
     */
    public abstract boolean addCFGRuleSet(String[] rules);

    /**
     * 添加单个CNF规则
     */
    public abstract boolean addCNFRule(Rule rule);

    /**
     * 添加CFG规则的左右映射关系
     */
    public abstract boolean addCFGMapping(Rule rule);

    /**
     * 添加CNF规则的左右映射关系
     */
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
