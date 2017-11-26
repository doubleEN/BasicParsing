package com.mjx.PhraseStructureTree;

import com.mjx.syntax.CNF;
import com.mjx.syntax.Rule;

import java.util.LinkedList;
import java.util.Queue;

public class PSTPennTreeBank extends BasicPhraseStructureTree {


    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public PSTPennTreeBank(String treeStr) {
        super(treeStr);
    }

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public PSTPennTreeBank(Node root) {
        super(root);
    }


    /**
     * 将一颗短语结构树转化为符合正则文法的树(重构)
     */
    public boolean convertCFGTree(CNF grammer) {
        String treeStr = this.toString();
        //首先还原longRHS
        this.restoreLongRHS();
        if (this.getRoot() == null) {
            return true;
        }
        //然后还原unit productions
        this.restoreUnitProductions(grammer);

        return !this.toString().equals(treeStr);
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
            Rule unitProductionsRule = grammer.getUnitProductions(new Rule(currNode.getValue(), children));
            if (unitProductionsRule != null) {
                Node unitNode = new Node(unitProductionsRule.getRHS().getValues()[0]);
                if (unitProductionsRule.getRHS().len() == 1) {
                    //还原A-->B-->d，处理到了叶子，不进行栈处理
                    Node lexicon = currNode.getChildren()[0];
                    unitNode.addChild(lexicon);
                    lexicon.setParent(unitNode);

                    currNode.resetChildren();
                    currNode.addChild(unitNode);
                    unitNode.setParent(currNode);
                } else {
                    //还原A-->B-->CD..。这种unit productions的结构恢复要另外验证
                    Node[] symbols = currNode.getChildren();
                    unitNode.addChildren(symbols);

                    //要将原本的孩子断开
                    for (Node child : symbols) {
                        child.setParent(unitNode);
                        queue.offer(child);//孩子节点进栈
                    }
                    currNode.resetChildren();
                    currNode.addChild(unitNode);
                    unitNode.setParent(currNode);
                }
            } else {
                for (Node child : currNode.getChildren()) {
                    queue.offer(child);
                }
            }
        }
    }
}
