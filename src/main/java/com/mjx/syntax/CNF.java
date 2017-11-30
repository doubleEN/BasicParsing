package com.mjx.syntax;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;

import java.util.*;

/**
 * 乔姆斯基正则文法类
 */
public abstract class CNF {

    /**
     * PennCFG,上下文无关文法规则集
     */
    private Set<Rule> CNFRules;

    /**
     * CNF由右部派生左部的映射集
     */
    private Map<RHS, Set<LHS>> rtl;

    /**
     * CNF由右部派生左部的映射集
     */
    private Map<LHS, Set<RHS>> ltr;

    /**
     * 非终结符集
     */
    private Set<String> nonterminals = new HashSet<>();

    /**
     * 终结符集
     */
    private Set<String> terminals = new HashSet<>();

    /**
     * 同意构造CNF三个树形的方法
     */
    protected void constructCNF() {
        this.CNFRules = new HashSet<>();
        this.ltr = new HashMap<>();
        this.rtl = new HashMap<>();
    }

    /**
     * 由一棵短语结构树扩充上下文无关文法
     */
    public void expandGrammer(BasicPhraseStructureTree phraseStructureTree) {
        //添加CFG规则
        this.addCFGRules(phraseStructureTree.generateRuleSet());
        //添加非终结符
        this.addNonterminals(phraseStructureTree.getNonterminals());
        //添加非终结符
        this.addTerminals(phraseStructureTree.getTerminals());
    }

    /**
     * 添加CFG规则集
     */
    public void addCFGRules(List<Rule> rules) {
        for (Rule rule : rules) {
            this.addCFGRule(rule);
        }
    }

    /**
     * 添加单个CFG规则
     */
    public abstract boolean addCFGRule(Rule rule);

    /**
     * 添加非终结符
     */
    public boolean addNonterminal(String nonterminal) {
        return this.nonterminals.add(nonterminal);
    }

    public void addNonterminals(Set<String> nonterminals) {
        this.nonterminals.addAll(nonterminals);
    }

    /**
     * 添加终结符
     */
    public boolean addTerminal(String terminal) {
        return this.terminals.add(terminal);
    }

    public boolean addTerminals(Set<String> terminals) {
        return this.terminals.addAll(terminals);
    }

    /**
     * CFG转CNF,long RHS 和unit productions分开转，直接在集合上操作
     */
    public abstract void convertToCNFs();

    /**
     * 添加单个CNF规则,同时添加映射关系
     */
    public boolean addCNFRule(Rule rule) {
        this.addCNFMapping(rule);//判断以后再决定是否增加映射关系
        return this.CNFRules.add(rule);
    }

    /**
     * 添加CNF规则的右左映射关系
     */
    private void addCNFMapping(Rule rule) {
        LHS lhs = rule.getLHS();
        RHS rhs = rule.getRHS();
        if (this.rtl.get(rhs) == null) {
            Set<LHS> set = new HashSet<>();
            set.add(lhs);
            this.rtl.put(rhs, set);
        } else {
            this.rtl.get(rhs).add(lhs);
        }
        if (this.ltr.get(lhs) == null) {
            Set<RHS> set = new HashSet<>();
            set.add(rhs);
            this.ltr.put(lhs, set);
        } else {
            this.ltr.get(lhs).add(rhs);
        }
    }

    /**
     * 正则文法集大小
     */
    public int getSizeOfCNF() {
        if (this.CNFRules == null) {
            return -1;
        }
        return this.CNFRules.size();
    }

    /**
     * 上下文无关文法集大小
     */
    public abstract int getSizeOfCFG();

    /**
     * 是否包含CNF规则
     */
    public boolean containCNFRule(Rule rule) {
        return this.CNFRules.contains(rule);
    }

    /**
     * 判断一个文法是否是正则文法
     */
    public boolean isCNF(Rule rule) {
        String[] children = rule.getRHS().getValues();
        if (children.length == 2) {
            for (String symbol : children) {
                if (this.isTerminal(symbol)) {
                    return false;
                }
            }
            return true;
        }
        if (children.length == 1) {
            if (this.isTerminal(children[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得CFG规则集
     */
    public abstract Set<Rule> getCFGs();

    /**
     * 获得CFG规则集
     */
    public Set<Rule> getCNFs() {
        return this.CNFRules;
    }

    /**
     * 判断是否是非终结符
     */
    public boolean isNonterminal(String symbol) {
        return this.nonterminals.contains(symbol);
    }

    /**
     * 判断是否是终结符
     */
    public boolean isTerminal(String symbol) {
        return this.terminals.contains(symbol);
    }

    /**
     * 非终结符集大小
     */
    public int getSizeOfNonterminals() {
        return this.nonterminals.size();
    }

    /**
     * 终结符集大小
     */
    public int getSizeOfTerminals() {
        return this.terminals.size();
    }

    /**
     * (PennCFG)终结符和非终结符的交集
     */
    public Set<String> symbolIntersection() {
        Set<String> tempSet1 = new HashSet<>();
        Set<String> tempSet2 = new HashSet<>();
        tempSet1.addAll(this.nonterminals);
        tempSet2.addAll(this.terminals);
        tempSet1.retainAll(tempSet2);
        return tempSet1;
    }


    /**
     * 由正则文法的RHS查找所有对应的LHS
     */
    public Set<LHS> searchLHS(String... rhs) {
        if (rhs.length == 1) {
            return this.rtl.get(new RHS(rhs[0]));
        }
        if (rhs.length == 2) {
            return this.rtl.get(new RHS(rhs[0], rhs[1]));
        } else {
            throw new IllegalArgumentException("正则文法的RHS长度为1或2.");
        }
    }

    /**
     * 由正则文法的LHS查找所有对应的RHS
     */
    public Set<RHS> searchRHS(String lhs) {
        return this.ltr.get(new LHS(lhs));
    }


    /**
     * 由消除unit productions的A-->t，超照原本的A-->B
     */
    public abstract Set<RuleChain> getUnitProductionChain(Rule rule);

    /**
     * 是否包含CFG规则
     */
    public abstract boolean containCFGRule(Rule rule);

    /**
     * 格式化打印当前文法集内容
     */
    public String printGrammer(){
        String content = "< The Grammer of "+this.getClass().getName()+"> extends "+this.getClass().getSuperclass().getName()+"\n\n>>>[Non_Terminal]\n";
        //非终结符
        System.out.println("非终结符集大小："+this.nonterminals.size());
        String[] nonT = this.nonterminals.toArray(new String[]{});
        Arrays.sort(nonT);
        String nonTStr=Arrays.toString(nonT);
        content += nonTStr.substring(1,nonTStr.length()-1)+"\n";
        content+="\n>>>[Terminal]\n";
        //终结符
        System.out.println("终结符集大小："+this.terminals.size());
        String[] t = this.terminals.toArray(new String[]{});
        Arrays.sort(t);
        String tStr=Arrays.toString(t);
        content += tStr.substring(1,tStr.length()-1)+"\n";
        //正则文法规则
        System.out.println("CNF规则集大小："+this.CNFRules.size());
        content+="\n>>>[CNF_Rules]\n";
        String[] rules = new String[this.CNFRules.size()];
        int i=0;
        for (Rule r : this.CNFRules) {
            rules[i] = r.toString();
            ++i;
        }
        Arrays.sort(rules);
        String ruleStr=Arrays.toString(rules);
        content += ruleStr.substring(1,ruleStr.length()-1).replaceAll("],","]\n")+"\n";
        return content;
    }
}
