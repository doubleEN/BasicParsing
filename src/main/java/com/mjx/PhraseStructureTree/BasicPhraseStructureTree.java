package com.mjx.PhraseStructureTree;

import com.mjx.syntax.CNF;
import com.mjx.syntax.LHS;
import com.mjx.syntax.RHS;
import com.mjx.syntax.Rule;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短语结构树，可重构
 */
public abstract class BasicPhraseStructureTree {

    /**
     * 树根
     */
    private Node root;

    /**
     * 树中的非终结符
     */
    private Set<String> nonterminal = new HashSet<>();

    /**
     * 树中的终结符
     */
    private Set<String> terminal = new HashSet<>();

    public BasicPhraseStructureTree() {
//        System.out.println("构造短语结构树：" + this.getClass().getSimpleName());
    }

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public BasicPhraseStructureTree(String treeStr) {
        this();
        this.generateTree(treeStr);
    }

    /**
     * 传入PhraseStructureTree能够处理的短语结构树括号表达式
     */
    public BasicPhraseStructureTree(Node root) {
        this();
        this.root = root;
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
     * 解析固定格式的树的括号表达式，得到短语结构树。同时得到终结符和非终结符。(重构，在生成树的过程中直接得到规则)
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

        nonterminal.add(parts.get(1));

        tempStack.push(root);
        for (int index = 2; index < parts.size(); ++index) {
            String currVal = parts.get(index);
            //当为"("时，当前字符串的下一个字符串作为栈顶节点的孩子，且该字符串进栈
            if (currVal.equals("(")) {
                nonterminal.add(parts.get(index + 1));
                Node child = new Node(parts.get(index + 1));
                tempStack.peek().addChild(child);
                child.setParent(tempStack.peek());
                tempStack.push(child);
                ++index;//直接调到下一个符号考虑
            } else if (currVal.equals(")")) {
                //遇到")"时，当前栈顶元素出栈
                tempStack.pop();
            } else if (currVal.equals(" ")) {
                //遇到空格，当前下一个元素是叶子
                terminal.add(parts.get(index + 1));
                Node child = new Node(parts.get(index + 1));
                child.setParent(tempStack.peek());
                tempStack.peek().addChild(child);
                ++index;
            }
        }
    }

    /**
     * 提炼树中的终结符和非终结符
     */
    public void setSymbol() {
        if (this.root == null) {
            throw new IllegalArgumentException("树为构造完成，不能提炼树节点");
        }
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.isLeaf()) {
                this.terminal.add(node.value);
            } else {
                this.nonterminal.add(node.value);
            }
            for (Node n : node.children) {
                queue.offer(n);
            }
        }
    }

    /**
     * 有序返回短语结构树上的非终结符
     */
    public Set<String> getNonterminals() {
        return this.nonterminal;
    }

    /**
     * 有序返回短语结构树上的终结符
     */
    public Set<String> getTerminals() {
        return this.terminal;
    }

    /**
     * 设置根
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * 找到树根
     */
    public String getRootValue() {
        return this.root.value;
    }

    /**
     * 找到树根
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * 找到树的最右叶子
     */
    public String getRightMostLeaf() {
        Node tempNode = this.root;
        while (!tempNode.isLeaf()) {
            tempNode = tempNode.getRightMostChild();
        }
        return tempNode.value;
    }

    /**
     * 生成短语结构树对应的句子(先序遍历)
     */
    public String getSentence(boolean tagged) {
        return this.scanTree(this.root, tagged).trim();
    }

    private String scanTree(Node node, boolean tagged) {
        if (node.isLeaf()) {
            if (tagged) {
                return " " + node.value + "/" + node.parentVal();
            } else {

                return " " + node.value;
            }
        }
        List<Node> children = node.children;
        String subStr = "";
        for (Node child : children) {
            subStr += this.scanTree(child, tagged);
        }
        return subStr;
    }

    /**
     * 直接从树形上处理unit productions情况。直接处理PennTreeBank上的两种unit productions 情况：
     * 1.A-->B-->[C D]
     * 2.A-->B-->d
     *
     * @return 返回unit productions的链式结构
     */
    public Map<Rule[], Integer> getUnitProductionsChain() {
        Map<Rule[], Integer> chain = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.root);
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            if (currNode.numChild() == 1 && currNode.children.get(0).numChild() > 1) {
                // A-->B-->[C D ...]
                Rule[] rules = new Rule[2];
                rules[0] = new Rule(currNode.value, currNode.children.get(0).value);
                String[] _rhs = new String[currNode.children.get(0).numChild()];
                for (int i = 0; i < currNode.children.get(0).numChild(); ++i) {
                    _rhs[i] = currNode.children.get(0).children.get(i).value;
                }
                rules[1] = new Rule(currNode.children.get(0).value, _rhs);
                Integer num = chain.get(rules);
                if (num != null) {
                    ++num;
                } else {
                    num = 1;
                }
                chain.put(rules, num);
            } else if (currNode.numChild() == 1 && currNode.children.get(0).numChild() == 1) {
                //A-->B-->d
                Rule[] rules = new Rule[2];
                rules[0] = new Rule(currNode.value, currNode.children.get(0).value);
                rules[1] = new Rule(currNode.children.get(0).value, currNode.children.get(0).children.get(0).value);
                Integer num = chain.get(rules);
                if (num != null) {
                    ++num;
                } else {
                    num = 1;
                }
                chain.put(rules, num);
            }
            for (Node node : currNode.children) {
                queue.offer(node);
            }
        }
        return chain;
    }

    /**
     * 扫描是否包含unit productions
     */
    public String hasUnitProductions() {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.root);
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            if (currNode.numChild() == 1 && !currNode.children.get(0).isLeaf()) {
                return currNode.value + "-->" + currNode.children.get(0).value + "-->" + currNode.children.get(0).children;
            }
            for (Node node : currNode.children) {
                queue.offer(node);
            }
        }
        return null;
    }

    /**
     * 扫描是否包含长度为3的unit productions,结构为 nonT-->nonT-->T
     */
    public String has3GramUnitProductions() {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.root);
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            if (currNode.numChild() == 1 && currNode.children.get(0).numChild() == 1 && currNode.children.get(0).children.get(0).isLeaf()) {
                return currNode.value + "-->" + currNode.children.get(0).value + "-->" + currNode.children.get(0).children.get(0).value;
            }
            for (Node node : currNode.children) {
                queue.offer(node);
            }
        }
        return null;
    }

    /**
     * 扫描是否包含制定子树(树桩),是否可以用正则重构
     */
    public boolean hasSubTree(String parent, String... children) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this.root);
        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
            if (currNode.value.equals(parent) && currNode.numChild() == children.length) {
                boolean exitFlag = true;
                for (int i = 0; i < currNode.numChild(); ++i) {
                    if (!currNode.children.get(i).value.equals(children[i])) {
                        exitFlag = false;
                        break;
                    }
                }
                if (exitFlag) {
                    return true;
                }
            }
            for (Node node : currNode.children) {
                queue.offer(node);
            }
        }
        return false;
    }

    /**
     * 扫描是否包含指定节点(直接遍历树太慢)
     */
    public boolean hasNode(String symbol) {
        Pattern pattern = Pattern.compile(symbol);
        Matcher matcher = pattern.matcher(this.toString());
        return matcher.find();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

    /**
     * 先序遍历
     */
    public String dictTree() {
        int depth = 0;
        return this.partTree(root, depth);
    }

    private String partTree(Node node, int depth) {
        if (node.isLeaf()) {
            return "\"" + node.value + "\"";
        }
        String subStr = "{\"" + node.value + "\":{";
        for (int i = 0; i < node.children.size(); ++i) {
            if (i == node.children.size() - 1) {
                depth++;
                subStr += "\"--" + depth + "\":" + this.partTree(node.children.get(i), depth) + "}";
            } else {
                depth++;
                subStr += "\"--" + depth + "\":" + this.partTree(node.children.get(i), depth) + ",";
            }
        }
        return subStr + "}";
    }

    public static class Node {

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
         * 获得父节点
         */
        public Node getParent() {
            return this.parent;
        }

        /**
         * 连接父节点
         */
        public void setParent(Node father) {
            this.parent = father;
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
         * 获得所有孩子
         */
        public Node[] getChildren() {
            return this.children.toArray(new Node[]{});
        }

        /**
         * 重置当前节点的孩子节点为空
         */
        public void resetChildren() {
            this.children = new ArrayList<>();
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
         * 找到最右孩子节点
         */
        public Node getRightMostChild() {
            return this.children.get(this.children.size() - 1);
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

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Node)) {
                return false;
            }
            Node n = (Node) obj;
            return this.value.equals(n.value);
        }
    }

    /**
     * 依照PennTreeBank的格式，打印树的括号表达式
     * 中序遍历树
     */
    public String printTree() {
        int depth = 1;
        String tree = this.printBranch(root, depth);
        String newTree = "";
        for (int i = 0; i < tree.length(); ++i) {
            newTree += Character.toString(tree.charAt(i));
            if (tree.charAt(i) == '\n') {
                newTree += "  ";//根下是\t
            }
        }
        return "(" + newTree + ")";
    }

    /**
     * 打印Penn树形的递归方法
     *
     * @param subTree 当前待打印的树
     * @param depth   当前树的根的深度
     * @return 树的Penn树形
     */
    private String printBranch(Node subTree, int depth) {
        //当前树的缩进量
        String indent = "";
        for (int i = 0; i < depth; ++i) {
            indent += "  ";
        }

        String childStr = "(" + subTree.value;
        List<Node> children = subTree.children;
        //当前树的孩子中的(词性 词)形式的子树，是否被其他形式的子树隔开
        boolean tailFlag = false;
        for (Node child : children) {
            if (child.numChild() == 1 && child.children.get(0).isLeaf()) {
                if (tailFlag) {
                    childStr += "\n" + indent;
                }
                childStr += "(" + child.value + " " + child.children.get(0).value + ") ";//尾部加一个空格
            } else {
                tailFlag = true;
                childStr += "\n" + indent + printBranch(child, depth + 1);
            }
        }
        return childStr + ")";
    }

    public abstract boolean convertCFGTree(CNF grammer);
}
