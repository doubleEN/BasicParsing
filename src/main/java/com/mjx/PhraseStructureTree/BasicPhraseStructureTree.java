package com.mjx.PhraseStructureTree;

import com.mjx.parse.LHS;
import com.mjx.parse.RHS;
import com.mjx.parse.Rule;

import java.util.*;

public class BasicPhraseStructureTree {

    /**
     * 树根
     */
    private Node root;

    BasicPhraseStructureTree() {
        System.out.println("构造短语结构树：" + this.getClass().getSimpleName());
    }

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public BasicPhraseStructureTree(String treeStr) {
        this();
        this.generateTree(treeStr);
    }

    /**
     * 一条规则转换成节点（无用）
     *
     * @param rule PCFG规则
     * @return 树节点
     */
    private Node ruleToNode(Rule rule) {
        Node node = new Node(rule.getLHS().getValue());
        node.addChildren(rule.getRHS().getValues());
        return node;
    }

    /**
     * 一个节点转换成规则
     *
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
     * 解析固定格式的树的括号表达式，得到短语结构树。利用两个栈实现同时将多个孩子与一个父节点连接，以便辨别规则的中心词。
     *
     * @param treeStr 树的括号表达式
     * @return 短语结构树
     */
    public void generateTree(String treeStr) {
        List<String> parts = new ArrayList<String>();
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
        Stack<Node> tempStack = new Stack<Node>();
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
                ++index;//直接调到下一个符号考虑
            } else if (currVal.equals(")")) {
                tempStack.pop();
            } else if (currVal.equals(" ")) {
                Node child = new Node(parts.get(index + 1));
                tempStack.peek().addChild(child);
                ++index;
            }
        }
    }

    @Override
    public String toString() {
        return this.root.toString();
    }


    void setRoot(Node root) {
        this.root = root;
    }

    class Node {

        /**
         * 当前节点的符号
         */
        private String value;



        /**
         * 父节点索引
         */
        private Node parent;

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
        public void setParent(Node father) {
            this.parent = father;
        }

        /**
         * 连接父节点
         */
        public void setParent(String father) {
            this.parent = new Node(father);
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
            return this.children.size() == 0;
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
            return this.parent == null;
        }

        /**
         * 返回父节点
         */
        public String parentVal() {
            if (this.parent == null) {
                return null;
            }
            return this.parent.getValue();
        }

        /**
         * 先序遍历打印得到树的括号表达式
         */
        @Override
        public String toString() {
            if (this.children.size() == 0) {
                return " " + this.value;
            } else {
                String treeStr = "(" + this.value;
                for (Node child : this.children) {
                    treeStr += child.toString();
                }
                treeStr += ")";
                return treeStr;
            }
        }
    }

}
