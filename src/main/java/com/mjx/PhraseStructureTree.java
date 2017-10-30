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
     * 一条规则转换成节点（无用）
     * @param rule PCFG规则
     * @return 树节点
     */
    private Node ruleToNode(Rule rule) {
        Node node = new Node(rule.getLHS());
        node.addChildren(rule.getRHS());
        return node;
    }

    /**
     * 一个节点转换成规则
     * @param node 一个非叶子节点
     * @return PCFG规则
     */
    private Rule nodeToRule(Node node) {
        if (node.isLeaf()) {
            return null;
        }
        //获得孩子的方式重构
        Iterator<Node> iter = node.children.iterator();
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
     * 解析PhraseStructureTree得到相应规则集(树的层序遍历)
     */
    public List<Rule> generateRuleSet() {
        //使用list而不是set，避免丢失同一个树中出现多次的规则
        List<Rule> ruleSet = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Iterator<Node> iter = queue.peek().children.iterator();
            while (iter.hasNext()) {
                queue.offer(iter.next());
            }
            Node node = queue.poll();
            if (!node.isLeaf()) {
                Rule rule = this.nodeToRule(node);
                ruleSet.add(rule);
            }
        }
        return ruleSet;
    }

    /**
     * 解析固定格式的树的括号表达式，得到短语结构树
     * @param treeStr 树的括号表达式
     * @return 短语结构树
     */
    public void generateTree(String treeStr) {
        //将括号、语法符号、空格各作为一个单位独立出来，方便括号表达式的解析
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
                child.setFather(tempStack.peek());
                tempStack.peek().addChild(child);
                tempStack.push(child);
                ++index;
            } else if (currVal.equals(")")) {
                tempStack.pop();
            } else if (currVal.equals(" ")) {
                Node child = new Node(parts.get(index + 1));
                child.setFather(tempStack.peek());
                tempStack.peek().addChild(child);
                ++index;
            }
        }
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
            this.children.add(node);
        }

        /**
         * 给当前节点添加最右孩子节点
         */
        public void addChild(String node) {
            this.children.add(new Node(node));
        }

        /**
         * 给当前节点添加数个右孩子
         */
        public void addChildren(String[] children) {
            for (String child : children) {
                this.addChild(child);
            }
        }

        /**
         * 给当前节点添加数个右孩子
         */
        public void addChildren(Node[] children) {
            for (Node child : children) {
                this.addChild(child);
            }
        }

        /**
         * 连接父节点
         */
        public void setFather(Node father) {
            this.father = father;
        }

        /**
         * 连接父节点
         */
        public void setFather(String father) {
            this.father = new Node(father);
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
            return this.children.size()==0;
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
            return this.father==null;
        }

        /**
         * 返回父节点
         */
        public String fatherVal() {
            if (this.father==null) {
                return null;
            }
            return this.father.fatherVal();
        }

        /**
         * 先序遍历打印得到树的括号表达式
         */
        @Override
        public String toString() {
            if (this.children.size()==0) {
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
