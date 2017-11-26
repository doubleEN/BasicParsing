package com.mjx.syntax;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;

import java.util.*;

public class PennCFG extends CNF {
    /**
     * PennCFG,上下文无关文法规则集
     */
    private Map<Rule, Integer> CFGRules = new HashMap<Rule, Integer>();

    private Map<Rule[], Integer> ruleChains = new HashMap<>();

    private Map<Rule, Rule> ruleToRule;

    public PennCFG() {
    }

    @Override
    public void expandGrammer(BasicPhraseStructureTree phraseStructureTree) {
        super.expandGrammer(phraseStructureTree);
        //在CFG中，保存来自短语结构树的unit productions规则链
        this.addRuleChain(phraseStructureTree.getUnitProductionsChain());
    }

    private void addRuleChain(Map<Rule[], Integer> ruleChains) {
        Set<Map.Entry<Rule[], Integer>> entries = ruleChains.entrySet();
        for (Map.Entry<Rule[], Integer> entry : entries) {
            if (ruleChains.get(entry.getKey()) == null) {
                this.ruleChains.put(entry.getKey(), entry.getValue());
            } else {
                this.ruleChains.put(entry.getKey(), entry.getValue() + ruleChains.get(entry.getKey()));
            }
        }
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
        this.ruleToRule = new HashMap<>();

        Map<Rule, Integer> tempRules = new HashMap<>();
        tempRules.putAll(this.CFGRules);

        //处理unit productions,且在正则文法规则和long rhs之前处理，因为存在A-->B-->C D..，这样的语法
        Iterator<Map.Entry<Rule[], Integer>> iter1 = this.ruleChains.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry<Rule[], Integer> chain = iter1.next();
            Rule rule1 = chain.getKey()[0];
            Rule rule2 = chain.getKey()[1];
            Rule newRule = new Rule(rule1.getLHS(), rule2.getRHS());
            if (rule2.lenOfRHS() > 1) {
                //A-->B-->CDE..转化为A-->CDE..，放入tempRules，以便对long RHS进行处理
                Integer num = tempRules.get(newRule);
                if (num == null) {
                    tempRules.put(newRule, chain.getValue());
                } else {
                    tempRules.put(newRule, chain.getValue() + num);
                }
                //删除处理完的unit productions，可能重复删除
                this.ruleToRule.put(newRule, rule1);
                tempRules.remove(rule1);
                tempRules.remove(rule2);
            } else if (rule2.lenOfRHS() == 1) {
                //A-->B-->d转化为A-->d
                // 1.一般的unit productions处理后加入CNF
                this.addCNFRule(newRule);
                this.ruleToRule.put(newRule, rule1);
                tempRules.remove(rule1);
                tempRules.remove(rule2);//直接删除词性规则，是否欠考虑
            }
        }

        Iterator<Map.Entry<Rule, Integer>> iter2 = tempRules.entrySet().iterator();
        //筛选CFG中存在的CNF规则
        while (iter2.hasNext()) {
            Rule _rule = iter2.next().getKey();
            //Penn上，rhs长度为2的规则，一定属于正则文法
            if (_rule.lenOfRHS() == 2) {
                // 2.CFG上正则文法规则加入CNF
                this.addCNFRule(_rule);
                iter2.remove();
            } else if (_rule.lenOfRHS() == 1 && this.isTerminal(_rule.getRHS().getValues()[0])) {
                // 3.正常的词性规则加入CNF
                this.addCNFRule(_rule);
                iter2.remove();
            }
        }

        //开始处理longRHS，没有正确处理计数
        Map<RHS, LHS> newRules = new HashMap<>();

        Iterator<Map.Entry<Rule, Integer>> iter3 = tempRules.entrySet().iterator();

        while (iter3.hasNext()) {
            Map.Entry<Rule, Integer> entry = iter3.next();
            if (entry.getKey().lenOfRHS() < 3) {
                throw new IllegalArgumentException("存在RHS长度低于3的规则未处理完。");
            }
            this.cutLongRHS(entry.getKey(), newRules);
        }

        Set<Map.Entry<RHS, LHS>> entries = newRules.entrySet();
        // 4.longRHS处理过后加入正则文法集
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

    @Override
    public Rule getUnitProductions(Rule rule) {
        return this.ruleToRule.get(rule);
    }

    @Override //particular
    public String printGrammer() {
        String content = super.printGrammer()+"\nParticular Content of "+this.getClass().getName()+"\n\n>>>[PennCFG_Rules]\n";
        Set<Map.Entry<Rule, Integer>> entries = this.CFGRules.entrySet();
        String[] rules = new String[this.CFGRules.size()];
        int i=0;
        for (Map.Entry<Rule, Integer> entry : entries) {
            rules[i] = entry.getKey().toString();
            ++i;
        }
        Arrays.sort(rules);
        String ruleStr=Arrays.toString(rules);
        content += ruleStr.substring(1,ruleStr.length()-1).replaceAll("],","]\n");
        return content;
    }
}
