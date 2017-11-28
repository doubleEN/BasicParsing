package com.mjx.phrasestructuretree;

import com.mjx.syntax.CNF;
import com.mjx.syntax.Rule;
import com.mjx.syntax.RuleChain;

import java.util.*;

public class PennTreeBankPST extends BasicPhraseStructureTree {

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public PennTreeBankPST(String treeStr) {
        super(treeStr);
    }

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public PennTreeBankPST(String treeStr,boolean removeNone) {
        super(treeStr,removeNone);
    }

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public PennTreeBankPST(Node root) {
        super(root);
    }

    /**
     * 直接从树形上处理unit productions情况。
     *
     * @return 返回unit productions的链式结构及其计数
     */
    @Override
    public Map<RuleChain, Integer> getUnitProductionsChain() {
        Map<RuleChain, Integer> chain = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.getRoot());
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            if (currNode.numChild() == 1 && !currNode.getChildren()[0].isLeaf()) {
                // A-->B...
                List<String> tempList = new ArrayList<>();
                Node tempNode = currNode;
                //提取unit productions的规则链
                while (tempNode.numChild() == 1 && !tempNode.getChildren()[0].isLeaf()) {
                    tempList.add(tempNode.getValue());
                    tempNode = tempNode.getChildren()[0];
                }
                tempList.add(tempNode.getValue());

                String[] tail = new String[tempNode.numChild()];
                for (int i = 0; i < tempNode.numChild(); ++i) {
                    tail[i] = tempNode.getChildren()[i].getValue();
                }
                RuleChain chain1 = new RuleChain(tempList.toArray(new String[]{}), tail);
                //规则链计数
                String[] ruleKey = tempList.toArray(new String[]{});
                Integer count = chain.get(chain1);
                if (count == null) {
                    chain.put(chain1, 1);
                } else {
                    chain.put(chain1, count + 1);
                }
                //层序进栈
                if (tempNode.numChild() != 1) {
                    for (Node node : tempNode.getChildren()) {
                        queue.offer(node);
                    }
                }
            } else {
                //层序进栈
                for (Node node : currNode.getChildren()) {
                    queue.add(node);
                }
            }
        }
        return chain;
    }

    /**
     * 将一颗短语结构树转化为上下文无关文法的树(重构)
     *
     * @return 转化前后的树是否一致
     */
    public boolean convertCFGTree(CNF grammer) {
        String treeStr = this.toString();
        //首先还原longRHS
        this.restoreLongRHS();
        if (this.getRoot() == null) {
            //自定义符作为树根的树无效，直接忽略
            return true;
        }
        //然后还原unit productions
        this.restoreUnitProductions(grammer);

        return this.toString().equals(treeStr);
    }

    private void restoreLongRHS() {
        while (this.linkRHS(this.getRoot())) {
        }
    }

    private boolean linkRHS(Node node) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.getRoot());
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            if (currNode.getValue().length() > 3 && currNode.getValue().substring(0, 4).equals("rule")) {
                //新构造的短语结构符成为了树根。问题可能存在于cutLong时，在新的空间构造新规则集，没有考虑原CFG中 A-->BC 的存在，而是构造了rulex-->BC
                //但是，在CNF是存在A-->BC这条规则的，所以，判定这棵正则文法树不存在相应的上下文无关文法树。
                if (currNode.isRoot()) {
//                    System.out.println("这棵正则文法树不存在相应的上下文无关文法树。");
                    this.setRoot(null);
                    return false;
                }
                Node parent = currNode.getParent();
                Node[] children = currNode.getChildren();
                Node[] parent_childen = parent.getChildren();

                parent.resetChildren();
                //先将currNode的孩子拼接到父节点上
                for (Node child1 : children) {
                    parent.addChild(child1);
                    child1.setParent(parent);
                }
                //再将原父节点的孩子拼接到父节点右侧
                for (Node child2 : parent_childen) {
                    if (!child2.equals(currNode)) {
                        parent.addChild(child2);
                        child2.setParent(parent);
                    }
                }
                return true;
            }
            Node[] children = currNode.getChildren();
            for (Node child : children) {
                queue.offer(child);
            }
        }
        return false;
    }

    private void restoreUnitProductions(CNF grammer) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.getRoot());
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            String[] children = new String[currNode.getChildren().length];
            for (int i = 0; i < currNode.getChildren().length; ++i) {
                children[i] = currNode.getChildren()[i].getValue();
            }
            Set<RuleChain> unitProductionsRules = grammer.getUnitProductionChain(new Rule(currNode.getValue(), children));
            RuleChain chain = null;
            if (unitProductionsRules != null && unitProductionsRules.size() > 1) {
                System.out.println("还原unit productions时，存在多种还原路径，取一种还原路径。");
                for (RuleChain c : unitProductionsRules) {
                    chain = c;
                    break;
                }
                Rule[] rules = chain.getRuleChain();
                currNode.getChildren()[0].setParent(null);
                currNode.resetChildren();
                Node tempNode = currNode;
                if (chain.getTailLen() == 1) {
                    //还原A-->B-->d，处理到了叶子，子树不需要再进行遍历
                    for (Rule rule : rules) {
                        Node newNode = new Node(rule.getRHS().getValues()[0]);
                        tempNode.addChild(newNode);
                        newNode.setParent(tempNode);
                        tempNode = newNode;
                    }
                } else {
                    //还原A-->B-->CD..。这种unit productions的结构恢复要另外验证
                    for (Rule rule : rules) {
                        if (rule.lenOfRHS() == 1) {
                            Node newNode = new Node(rule.getRHS().getValues()[0]);
                            tempNode.addChild(newNode);
                            newNode.setParent(tempNode);
                            tempNode = newNode;
                        } else {
                            for (String child : rule.getRHS().getValues()) {
                                Node newNode = new Node(child);
                                tempNode.addChild(newNode);
                                newNode.setParent(tempNode);
                                queue.offer(newNode);//孩子节点进栈
                            }
                        }
                    }
                }
            } else {
                for (Node child : currNode.getChildren()) {
                    queue.offer(child);
                }
            }
        }

    }

    @Override
    protected boolean processLexicon() {
        return this.eliminateNoneElement();
    }

    /**
     * 从树形上删除[none-element](正则处理的可行性)
     */
    private boolean eliminateNoneElement() {
        boolean flag = false;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.getRoot());
        String tree = this.dictTree();
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            if (currNode.getValue().equals("-NONE-")) {
                flag = true;
                Node parent = currNode.getParent();
                if (parent.numChild() == 1) {
                    //P-->NONE-->*
                    Node ancestor = parent.getParent();
                    Node[] parents = ancestor.getChildren();
                    ancestor.resetChildren();
                    parent.setParent(null);
                    for (Node p : parents) {
                        if (!p.equals(parent)) {
                            ancestor.addChild(p);
                            p.setParent(ancestor);
                        }
                    }
                    if (ancestor.numChild() == 1 && ancestor.getParent().numChild() == 1) {
                    }
                } else {
                    //P-->NONE X
                    Node[] children = parent.getChildren();
                    parent.resetChildren();
                    currNode.setParent(null);
                    for (Node child : children) {
                        if (!child.equals(currNode)) {
                            parent.addChild(child);
                            child.setParent(parent);
                        }
                    }
                    if (parent.numChild() == 1 && parent.getParent().numChild() == 1) {
                    }
                }
            } else {
                for (Node node : currNode.getChildren()) {
                    queue.offer(node);
                }
            }

        }
        return flag;
    }
}