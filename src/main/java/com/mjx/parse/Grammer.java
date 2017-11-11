package com.mjx.parse;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.*;

/**
 * 上下文无关文法和乔姆斯基正则文法关系集。
 */
public class Grammer {

    /**
     * CFG,上下文无关文法规则集
     */
    private Map<Rule, Integer> CFGs = new HashMap<Rule, Integer>();

    /**
     * CNF,乔姆斯基正则文法规则集
     */
    private Map<Rule, Integer> CNFs = new HashMap<Rule, Integer>();

    /**
     * CNF由右部派生左部的映射集
     */
    private Map<RHS, Set<LHS>> rtlCNF = new HashMap<RHS, Set<LHS>>();

    /**
     * CFG转CNF产生的新规则
     */
    private Map<RHS, LHS> newRules = new HashMap<RHS, LHS>();

    /**
     * 非终结符集
     */
    private Set<String> nonterminals = new HashSet<>();

    /**
     * 终结符集
     */
    private Set<String> terminals = new HashSet<>();

    public Grammer() {

    }

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
     * 终结符和非终结符的交集
     */
    public Set<String> symbolIntersection(){
        Set<String> tempSet1 = new HashSet<>();
        Set<String> tempSet2 = new HashSet<>();
        tempSet1.addAll(this.nonterminals);
        tempSet2.addAll(this.terminals);
        tempSet1.retainAll(tempSet2);
        return tempSet1;

    }

    /**
     * 是否包含CFG规则
     */
    public boolean containCFGRule(Rule rule) {
        return this.CFGs.containsKey(rule);
    }

    /**
     * 添加单个CFG规则
     */
    public void addCFGRule(Rule rule) {
        Integer val = this.CFGs.get(rule);
        if (val == null) {
            val = 1;
        } else {
            ++val;
        }
        this.CFGs.put(rule, val);
        this.convertToCNF(rule);
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
     * 由一棵短语结构树扩充文法
     */
    public void expandGrammer(BasicPhraseStructureTree basicPhraseStructureTree) {
        this.addCFGRules(basicPhraseStructureTree.generateRuleSet());
        this.addNonterminals(basicPhraseStructureTree.getNonterminals());
        this.addTerminals(basicPhraseStructureTree.getTerminals());
    }

    /**
     * 添加单个CNF规则
     */
    private Integer addCNFRule(Rule rule) {
        Integer val = this.CNFs.get(rule);
        if (val == null) {
            val = 1;
        } else {
            ++val;
        }
        this.addCNFMapping(rule);
        return this.CNFs.put(rule, val);
    }

    /**
     * CFG转CNF
     */
    private void convertToCNF(Rule rule) {
        if (rule.getRHS().len() > 2) {
            this.cutLongRHS(rule);
        } else {
            this.addCNFRule(rule);
        }
        //可以有其他上下文无关文法的转换
    }

    /**
     * 添加CNF规则的右左映射关系
     */
    private void addCNFMapping(Rule rule) {
        LHS lhs = rule.getLHS();
        RHS rhs = rule.getRHS();
        if (this.rtlCNF.get(rhs) == null) {
            Set<LHS> set = new HashSet<>();
            set.add(lhs);
            this.rtlCNF.put(rhs, set);
        } else {
            this.rtlCNF.get(rhs).add(lhs);
        }
    }

    /*
     * 由RHS查找其所有LHS(CNF)
     */
    public LHS[] getLHSOnRHS(RHS RHS) {
        return this.rtlCNF.get(RHS).toArray(new LHS[]{});
    }

    public LHS[] getLHSOnRHS(String[] RHS) {
        return this.getLHSOnRHS(new RHS(RHS));
    }

    /**
     * 将右项长度大于2的上下文无关文法转换成乔姆斯基正则文法
     */
    private void cutLongRHS(Rule rule) {
        String lhsStr = rule.getLHS().getValue();
        String[] longRHSStr = rule.getRHS().getValues();
        int rhsLen = longRHSStr.length;
        if (rhsLen < 3) {
            throw new IllegalArgumentException("规则的右项长度小于3");
        }

        String newLHS = longRHSStr[0];
        //CNF的长度为2右项
        for (int i = 1; i < longRHSStr.length; ++i) {
            String[] cnfRHS = new String[2];
            cnfRHS[0] = newLHS;
            cnfRHS[1] = longRHSStr[i];
            //新规则左项的值
            if (i == rhsLen - 1) {
                newLHS = rule.getLHS().getValue();
            } else {
                newLHS = "rule" + this.newRules.size();
            }

            RHS newRHS = new RHS(cnfRHS);
            //是否存在原CFG的右项和CNF右项相同，但左项不一样的情况
            LHS currLHS = this.newRules.get(newRHS);

            //截取的最左两个RHS不存在对应的LHS
            if (currLHS == null) {
                LHS lhs = new LHS(newLHS);
                this.newRules.put(newRHS, lhs);
                this.addCNFRule(new Rule(lhs, newRHS));
            } else {
                this.addCNFRule(new Rule(currLHS, newRHS));//累计而非添加
                newLHS = currLHS.getValue();
            }
        }
    }

    /**
     * 正则文法集大小
     */
    public int getSizeOfCNF() {
        return this.CNFs.size();
    }

    /**
     * 上下文无关文法集大小
     */
    public int getSizeOfCFG() {
        return this.CFGs.size();
    }

    /**
     * 是否包含CNF规则
     */
    public boolean containCNFRule(Rule rule) {
        return this.CNFs.containsKey(rule);
    }

    /**
     * CNF转CFG
     */
//    abstract void convertToCFG();

    /**
     * 判断一个文法是否是正则文法
     */
    public boolean isCNF(Rule rule) {
        String[] children = rule.getRHS().getValues();
        if (children.length == 2) {
            for (String symbol : children){
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
    public Set<Rule> getCFGs() {
        Set<Rule> rules = new HashSet<>();
        Set<Map.Entry<Rule, Integer>> ruleSet = this.CFGs.entrySet();
        for (Map.Entry<Rule, Integer> rule : ruleSet) {
            rules.add(rule.getKey());
        }
        return rules;
    }

    /**
     * 获得CFG规则集
     */
    public Set<Rule> getCNFs() {
        Set<Rule> rules = new HashSet<>();
        Set<Map.Entry<Rule, Integer>> ruleSet = this.CNFs.entrySet();
        for (Map.Entry<Rule, Integer> rule : ruleSet) {
            rules.add(rule.getKey());
        }
        return rules;
    }
}
