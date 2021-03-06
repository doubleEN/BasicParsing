package com.mjx.syntax;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;

import java.util.*;

/**
 * PeenTreeBank 上下文无关文法类
 */
public class PennCFG extends CNF {

    /**
     * PennCFG,上下文无关文法规则集
     */
    private Set<Rule> CFGRules = new HashSet<>();

    private Map<RuleChain, Integer> ruleChains = new HashMap<>();

    /**
     * <Rule1,Rule2>，在A-->B-->t中，Rule1是A-->t,Rule2是A-->B
     */
    private Map<Rule, Set<RuleChain>> ruleChain;

    public Map<Rule, Set<RuleChain>> getRuleChain() {
        return ruleChain;
    }

    public PennCFG() {
    }

    @Override
    public void expandGrammer(BasicPhraseStructureTree phraseStructureTree) {
        super.expandGrammer(phraseStructureTree);
        //在CFG中，保存来自短语结构树的unit productions规则链
        this.addRuleChain(phraseStructureTree.getUnitProductionsChain());
    }

    /**
     * 添加由unit productions形成的chain of rule
     */
    private void addRuleChain(Map<RuleChain, Integer> ruleChains) {
        Set<Map.Entry<RuleChain, Integer>> entries = ruleChains.entrySet();
        for (Map.Entry<RuleChain, Integer> entry : entries) {
            if (ruleChains.get(entry.getKey()) == null) {
                this.ruleChains.put(entry.getKey(), entry.getValue());
            } else {
                this.ruleChains.put(entry.getKey(), entry.getValue() + ruleChains.get(entry.getKey()));
            }
        }
    }

    @Override
    public boolean addCFGRule(Rule rule) {
        return this.CFGRules.add(rule);
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
        return this.CFGRules;
    }

    @Override
    public boolean containCFGRule(Rule rule) {
        return this.CFGRules.contains(rule);
    }

    @Override
    public void convertToCNFs() {
        this.constructCNF();
        this.ruleChain = new HashMap<>();

        Set<Rule> tempRules = new HashSet<>();
        tempRules.addAll(this.CFGRules);

        //处理unit productions,且在正则文法规则和long rhs之前处理，因为存在A-->B-->C D..，这样的语法
        Iterator<Map.Entry<RuleChain, Integer>> iter1 = this.ruleChains.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry<RuleChain, Integer> chain = iter1.next();
            Rule newRule =chain.getKey().getEqualRule();
            if (newRule.lenOfRHS() > 1) {
                //A-->B-->CDE..转化为A-->CDE..，放入tempRules，以便对long RHS进行处理
                //删除处理完的unit productions，可能重复删除
                Set<RuleChain> ruleSet = this.ruleChain.get(newRule);
                if (ruleSet == null) {
                    ruleSet = new HashSet<>();
                }
                ruleSet.add(chain.getKey());
                this.ruleChain.put(newRule, ruleSet);
                Rule[] userlessRules = chain.getKey().getRuleChain();
                for (Rule rule : userlessRules) {
                    tempRules.remove(rule);
                }
            } else if (chain.getKey().getTailLen() == 1) {
                //A-->B-->d转化为A-->d
                // 1.一般的unit productions处理后加入CNF
                this.addCNFRule(newRule);
                Set<RuleChain> ruleSet = this.ruleChain.get(newRule);
                List<RuleChain> ruleSet2 = new ArrayList<>();
                if (ruleSet == null) {
                    ruleSet = new HashSet<>();
                }
                ruleSet.add(chain.getKey());
                this.addCNFRule(newRule);
                this.ruleChain.put(newRule, ruleSet);
                Rule[] userlessRules = chain.getKey().getRuleChain();
                for (Rule rule : userlessRules) {
                    tempRules.remove(rule);
                }
            }
        }

        Iterator<Rule> iter2 = tempRules.iterator();
        //筛选CFG中存在的CNF规则
        while (iter2.hasNext()) {
            Rule _rule = iter2.next();
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

        Iterator<Rule> iter3 = tempRules.iterator();

        while (iter3.hasNext()) {
            Rule rule = iter3.next();
            if (rule.lenOfRHS() < 3) {
                throw new IllegalArgumentException("存在RHS长度低于3的规则未处理完。");
            }
            this.cutLongRHS(rule, newRules);
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
    public Set<RuleChain> getUnitProductionChain(Rule rule) {
        return this.ruleChain.get(rule);
    }

    @Override
    public String printGrammer() {
        String content = super.printGrammer() + "\nParticular Content of " + this.getClass().getName() + "\n\n>>>[PennCFG_Rules]\n";
        System.out.println("CFG规则集大小：" + this.CFGRules.size());
        String[] rules = new String[this.CFGRules.size()];
        int i = 0;
        for (Rule rule : this.CFGRules) {
            rules[i] =rule.toString();
            ++i;
        }
        Arrays.sort(rules);
        String ruleStr = Arrays.toString(rules);
        content += ruleStr.substring(1, ruleStr.length() - 1).replaceAll("],", "]\n");
        return content;
    }
}
