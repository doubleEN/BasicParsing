package com.mjx.syntax;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;

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
     * CNF由右部派生左部的映射集
     */
    private Map<LHS, Set<RHS>> ltrCNF = new HashMap<LHS, Set<RHS>>();

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
    public Set<String> symbolIntersection() {
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
        //添加CFG规则
        this.addCFGRules(basicPhraseStructureTree.generateRuleSet());
        //添加非终结符
        this.addNonterminals(basicPhraseStructureTree.getNonterminals());
        //添加非终结符
        this.addTerminals(basicPhraseStructureTree.getTerminals());
    }

    /**
     * 添加单个CNF规则,同时添加映射关系
     */
    private Integer addCNFRule(Rule rule) {
        Integer val = this.CNFs.get(rule);
        if (val == null) {
            val = 1;
        } else {
            ++val;
        }
        this.addCNFMapping(rule);//判断以后再决定是否增加映射关系
        return this.CNFs.put(rule, val);
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
        if (this.ltrCNF.get(lhs) == null) {
            Set<RHS> set = new HashSet<>();
            set.add(rhs);
            this.ltrCNF.put(lhs, set);
        } else {
            this.ltrCNF.get(lhs).add(rhs);
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
     * CFG转CNF,unit production需要一次性处理
     */
    private void convertToCNF(Rule rule) {
        if (rule.getRHS().len() > 2) {
            this.cutLongRHS(rule);
        } else {
            this.addCNFRule(rule);
        }
    }

    public void eliminateUnitProductions() {
        Set<Rule> newRules = new HashSet<>();
        //过滤出所有单支派生
        Set<Rule> singleRule = new HashSet<>();
        for (Rule rule : this.getCNFs()) {
            if (rule.getRHS().len() == 1) {
                singleRule.add(rule);
                this.CNFs.remove(rule);
            }
        }

        //消除每一个unit productions
        for (Rule rule : singleRule) {
            if (!this.isTerminal(rule.getRHS().getValues()[0]) && this.isNonterminal(rule.getRHS().getValues()[0])) {
                Set<RHS> set = this.ltrCNF.get(rule.getLHS());
                for (RHS rhs : set) {
                    if (rhs.len() == 1 && !rhs.getValues()[0].equals(rule.getLHS().getValue())) {
                        this.searchLexicon(rule.getLHS(), rhs, newRules);
                    }
                }
            } else {
                newRules.add(rule);//直接添加标点符号类型的派生规则
            }
        }
        //309884条unit productions衍生的规则
        this.rtlCNF = new HashMap<>();
        this.ltrCNF = new HashMap<>();
        for (Rule rule : newRules) {
            this.CNFs.put(rule, 1);
        }

        Set<Rule> newCNFs = this.getCNFs();
        for (Rule rule : newCNFs) {
            this.addCNFMapping(rule);
        }
    }

    private void searchLexicon(LHS fistLHS, RHS rhs, Set<Rule> newRules) {
        if (this.isTerminal(rhs.getValues()[0])) {
            newRules.add(new Rule(fistLHS, rhs));
            return;
        }
        Set<RHS> set = this.ltrCNF.get(new LHS(rhs.getValues()[0]));
        for (RHS rhs1 : set) {
            //非终结符派生自己产生的死循环,但没有几个符号之间的死循环
            if (rhs1.len() == 1 && !rhs1.getValues()[0].equals(rhs.getValues()[0])) {
                this.searchLexicon(fistLHS, rhs1, newRules);
            }
        }
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

        //每次获得的新的LHS
        String newLHS = longRHSStr[0];
        //CNF的长度为2右项
        for (int i = 1; i < longRHSStr.length; ++i) {
            //每次产生的新的CNF规则长度为2的右项
            String[] cnfRHS = new String[2];
            cnfRHS[0] = newLHS;//新的CNF的右项的第一个派生，是前一次新规则构造中的LHS
            cnfRHS[1] = longRHSStr[i];//新的CNF的右项的第二个派生，原规则长右项上的下一个派生

            //新规则LHS的值
            if (i == rhsLen - 1) {
                //长RHS最后一个派生在新规则中，由原LHS派生
                newLHS = rule.getLHS().getValue();
            } else {
                //给新规则假定一个LHS
                newLHS = "rule" + this.newRules.size();
            }

            RHS newRHS = new RHS(cnfRHS);
            //在新规则集中，查找是否存在当前假定的RHS
            LHS currLHS = this.newRules.get(newRHS);
            if (currLHS == null) {
                //假定的新规则未构造，构造这个新的CNF规则，并添加到CNF
                LHS lhs = new LHS(newLHS);
                this.newRules.put(newRHS, lhs);
                this.addCNFRule(new Rule(lhs, newRHS));
            } else {
                //假定的新规则已经存在，则取存在的规则的LHS作为新一个新规则的RHS的第一个派生
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
     * 上下文无关文法集大小
     */
    public int getSizeOfNewRule() {
        return this.newRules.size();
    }

    /**
     * 是否包含CNF规则
     */
    public boolean containCNFRule(Rule rule) {
        return this.CNFs.containsKey(rule);
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

    /**
     * 找出所有unit productions 链上的LHS
     */
    @Deprecated
    public String[] searchSingleChainSet(String symbol) {
        Set<String> allSymbols = new HashSet<>();
        this.searchSingleChain(symbol, allSymbols);
        if (!this.isNonterminal(symbol)) {
            //可能刚好一个词与文法中的某一个非终结符一致
            allSymbols.remove(symbol);
        }
        return allSymbols.toArray(new String[]{});
    }

    @Deprecated
    private void searchSingleChain(String symbol, Set<String> tempSet) {
        if (tempSet.contains(symbol)) {
            return;
        }
        tempSet.add(symbol);
        Set<LHS> lhsSet = this.rtlCNF.get(new RHS(symbol));
        if (lhsSet == null) {
            return;
        }
        for (LHS lhs : lhsSet) {
            this.searchSingleChain(lhs.getValue(), tempSet);
        }
    }

    public Set<LHS> searchLHS(String...children) {
        if (children.length == 1) {
            return this.rtlCNF.get(new RHS(children[0]));
        }
        if (children.length == 2) {
            return this.rtlCNF.get(new RHS(children[0], children[1]));
        } else {
            throw new IllegalArgumentException("正则文法的RHS长度为1或2.");
        }
    }
}
