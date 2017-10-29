package com.mjx;

import java.util.*;

public class PhraseStructureTree {

    /**
     * 树根
     */
    private Node root;

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public PhraseStructureTree(String treeStr) {
        this.generateTree(treeStr);
    }

    /**
     * 一个节点转换成规则
     * @param node 一个非叶子节点
     * @return PCFG规则
     */
    public Rule nodeToRule(Node node) {
        if (node.leafFlag) {
            return null;
        }
        //获得孩子的方式重构
        Iterator<Node> iter = node.childrenIter();
        String[] lhs = new String[node.numChild()];
        int num = 0;
        while (iter.hasNext()) {
            lhs[num] = iter.next().value;
            ++num;
        }
        Rule rule = new Rule(new LHS(node.value), new RHS(lhs));
        return rule;
    }

    /**
     * 解析固定格式的树的括号表达式，得到短语结构树
     * @param treeStr 树的括号表达式
     * @return 短语结构树
     */
    public void generateTree(String treeStr) {
        //重构到一个循环中
        List<String> parts = new ArrayList<>();
        for (int index = 0; index < treeStr.length(); ++index) {
            if (treeStr.charAt(index) == '(' || treeStr.charAt(index) == ')' || treeStr.charAt(index) == ' ') {
                parts.add(Character.toString(treeStr.charAt(index)));
            } else {
                for (int i = index + 1; i < treeStr.length(); ++i) {
                    if (treeStr.charAt(i) == '(' || treeStr.charAt(i) == ')' || treeStr.charAt(i) == ' ') {
                        parts.add(treeStr.substring(index, i));
                        index = i - 1;
                        break;
                    }
                }
            }
        }
        //由树的括号表达式生成树
        Stack<Node> tempStack = new Stack<>();

        //初始化根节点
        this.root = new Node(parts.get(1));

        tempStack.push(root);
        for (int index = 2; index < parts.size(); ++index) {
            String currVal = parts.get(index);
            //当为"("时，当前字符串的下一个字符串作为栈顶节点的孩子，且该字符串进栈
            if (currVal.equals("(")) {
                Node child = new Node(parts.get(index + 1));
                tempStack.peek().addChild(child);
                tempStack.push(child);
                ++index;
            } else if (currVal.equals(")")) {
                tempStack.pop();
            } else if (currVal.equals(" ")) {
                Node child = new Node(parts.get(index + 1));
                tempStack.peek().addChild(child);
                ++index;
            }
        }
    }

    /**
     * 解析PhraseStructureTree得到相应规则集
     */
    public Set<Rule> generateRuleSet() {
        Set<Rule> ruleSet = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Iterator<Node> iter = queue.peek().childrenIter();
            while (iter.hasNext()) {
                queue.offer(iter.next());
            }
            Node node = queue.poll();
            if (!node.leafFlag) {
                Rule rule = this.nodeToRule(node);
                ruleSet.add(rule);
            }
        }
        return ruleSet;
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    private class Node {

        /**
         * 当前节点的符号
         */
        private String value;

        /**
         * 是否是叶子
         */
        private boolean leafFlag = true;

        /**
         * 是否是跟节点
         */
        private boolean rootFlag = true;

        /**
         * 父节点索引
         */
        private Node father;

        /**
         * 孩子结点列表
         */
        private List<Node> children = new ArrayList<Node>();

        public Node() {
        }

        public Node(String value) {
            this.value = value;
        }

        /**
         * 给当前节点添加最右孩子节点
         */
        public void setValue(String val) {
            this.value = val;
        }

        /**
         * 给当前节点添加最右孩子节点
         */
        public void addChild(Node node) {
            this.leafFlag = false;
            this.children.add(node);
        }

        /**
         * 连接父节点
         */
        public void setFather(Node father) {
            this.rootFlag = false;
            this.father = father;
        }

        /**
         * 获得当前节点的值
         */
        public String getValue() {
            return this.value;
        }

        /**
         * 判断当前节点是否为叶子节点
         */
        public boolean isLeaf() {
            return this.leafFlag;
        }

        /**
         * 返回当前节点的孩子个数
         */
        public int numChild() {
            return this.children.size();
        }

        /**
         * 判断当前节点是否是根
         */
        public boolean isRoot() {
            return this.rootFlag;
        }

        /**
         * 返回孩子节点的构造器???还是返回孩子的字符串列表？？？
         */
        public Iterator<Node> childrenIter() {
            if (this.leafFlag) {
                return null;
            }
            return this.children.iterator();
        }

        /**
         * 返回父节点
         */
        public String fatherVal() {
            if (this.rootFlag) {
                return null;
            }
            return this.father.fatherVal();
        }

        @Override
        public String toString() {
            if (this.leafFlag) {
                return " " + this.value;
            } else {
                String treeStr="("+this.value;
                for (Node child:this.children) {
                    treeStr+=child.toString();
                }
                treeStr+=")";
                return treeStr;
            }
        }
    }

}
