package com.mjx.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PennCFG extends CNF {
    /**
     * PennCFG,上下文无关文法规则集
     */
    private Map<Rule, Integer> CFGRules = new HashMap<Rule, Integer>();

    public PennCFG() {
    }

    @Override
    public void addCFGRule(Rule rule) {
        Integer val = this.CFGRules.get(rule);
        if (val == null) {
            val = 1;
        } else {
            ++val;
        }
        this.CFGRules.put(rule, val);
    }

    /**
     * 上下文无关文法集大小
     */
    @Override
    public int getSizeOfCFG() {
        return this.CFGRules.size();
    }

    @Override
    public Set<Rule> getCFGs() {
        Set<Rule> rules = new HashSet<>();
        Set<Map.Entry<Rule, Integer>> ruleSet = this.CFGRules.entrySet();
        for (Map.Entry<Rule, Integer> rule : ruleSet) {
            rules.add(rule.getKey());
        }
        return rules;
    }

    @Override
    public boolean containCFGRule(Rule rule) {
        return this.CFGRules.containsKey(rule);
    }

    @Override
    public void convertToCNFs() {
        this.constructCNF();

        Set<Rule> CFG = this.getCFGs();
        Map<RHS, LHS> newRules1 = new HashMap<>();

        Set<Rule> singleRule = new HashSet<>();
        Map<LHS, Set<RHS>> ltr = new HashMap<>();

        for (Rule rule : CFG) {
            if (rule.getRHS().len() > 2) {
                this.cutLongRHS(rule, newRules1);
            } else if (rule.getRHS().len() == 1) {
                //单链派生
                singleRule.add(rule);
                Set<RHS> RHSsET = ltr.get(rule.getLHS());
                if (RHSsET == null) {
                    Set<RHS> _rhs = new HashSet<>();
                    _rhs.add(rule.getRHS());
                    ltr.put(rule.getLHS(), _rhs);
                } else {
                    RHSsET.add(rule.getRHS());
                }
            } else {
                this.addCNFRule(rule);//CFG上的正则文法
            }
        }
        Set<Rule> newRules2 = this.eliminateUnitProductions(singleRule, ltr);

        for (Rule rule : newRules2) {
            this.addCNFRule(rule);
        }

        Set<Map.Entry<RHS, LHS>> entries = newRules1.entrySet();
        for (Map.Entry<RHS, LHS> entry : entries) {
            this.addCNFRule(new Rule(entry.getValue(), entry.getKey()));
        }
    }


    /**
     * 将右项长度大于2的上下文无关文法转换成乔姆斯基正则文法
     */
    private void cutLongRHS(Rule rule, Map<RHS, LHS> newRules) {
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
                newLHS = "rule" + newRules.size();
            }

            RHS newRHS = new RHS(cnfRHS);
            //在新规则集中，查找是否存在当前假定的RHS
            LHS currLHS = newRules.get(newRHS);//相同的RHS不存在覆盖的不同LHS，因为本身就是有RHS不同来判定一个规则是否是新生成的
            if (currLHS == null) {
                //假定的新规则未构造，构造这个新的CNF规则，并添加到CNF
                LHS lhs = new LHS(newLHS);
                newRules.put(newRHS, lhs);
                this.addCNFRule(new Rule(lhs, newRHS));
            } else {
                //假定的新规则已经存在，则取存在的规则的LHS作为新一个新规则的RHS的第一个派生
                newLHS = currLHS.getValue();
            }
        }
    }


    /**
     * 去除unit productions
     * bug 是否存在unit productions 无法派生到词汇的情况，这种情况如何处理
     */
    private Set<Rule> eliminateUnitProductions(Set<Rule> singleRule, Map<LHS, Set<RHS>> ltr) {
        Set<Rule> newRules = new HashSet<>();
        //消除每一个unit productions
        for (Rule rule : singleRule) {
            //RHS是非终结符，且RHS和LHS不一样的情况下。
            if (!rule.getLHS().getValue().equals(rule.getRHS().getValues()[0]) && this.isNonterminal(rule.getRHS().getValues()[0])) {
                Set<RHS> set = ltr.get(rule.getLHS());
                for (RHS rhs : set) {
                    //自身派生自身到底如何处理
                    if (!rhs.getValues()[0].equals(rule.getLHS().getValue())) {
                        this.searchLexicon(rule.getLHS(), rhs, newRules, ltr);
                    }
                }
            } else {
                newRules.add(rule);//直接添加正则派生规则
            }
        }
        //309884条unit productions衍生的规则
        return newRules;
    }

    private void searchLexicon(LHS fistLHS, RHS rhs, Set<Rule> newRules, Map<LHS, Set<RHS>> ltr) {
        //不将自身派生自身的情况加入新规则集
        if (fistLHS.getValue().equals(rhs.getValues()[0])) {
            return;
        }
        if (this.isTerminal(rhs.getValues()[0]) && !fistLHS.getValue().equals(rhs.getValues()[0])) {
            newRules.add(new Rule(fistLHS, rhs));
            return;
        }
        Set<RHS> set = ltr.get(new LHS(rhs.getValues()[0]));
        if (set == null) {
            return;
        }
        for (RHS rhs1 : set) {
            //非终结符派生自己产生的死循环,但没有几个符号之间的死循环
            if (rhs1.len() == 1 && !rhs1.getValues()[0].equals(rhs.getValues()[0])) {
                this.searchLexicon(fistLHS, rhs1, newRules, ltr);
            }
        }
    }

}
