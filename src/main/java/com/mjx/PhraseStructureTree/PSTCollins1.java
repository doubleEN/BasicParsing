package com.mjx.PhraseStructureTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PSTCollins1 extends BasicPhraseStructureTree{

    public PSTCollins1(String treeStr){
        this.generateTree(treeStr);
    }

    @Override
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
        Stack<Node> stack1 = new Stack<Node>();
        int j = 0;
        for (int i = 0; i < parts.size(); i++) {
            //非")"且非" "时，直接入栈
            if (!parts.get(i).equals(")") && !parts.get(i).equals(" ")) {
                stack1.push(new Node(parts.get(i)));
            } else if (parts.get(i).equals(")")) {
                //栈2用来连接父与孩子
                Stack<Node> stack2 = new Stack<Node>();
                while (!stack1.peek().getValue().equals("(")) {
                    stack2.push(stack1.pop());
                }
                //左括号出栈
                stack1.pop();
                Node node = stack2.pop();
                while (!stack2.isEmpty()) {
                    stack2.peek().setParent(node);
                    node.addChild(stack2.pop());
                }

                if (!node.isLeaf()) {
//                    node.setHeadWord("" + j++);
                }
                //形成的完整子树再进栈1
                stack1.push(node);
            }
        }
        this.setRoot(stack1.pop());
    }

    /**
     * 未完成。
     */
    class LexicalNode extends BasicPhraseStructureTree.Node{

        /**
         * 当前非终止符的中心词
         */
        private String headWord;

        public void setHeadWord(String headWord) {
            this.headWord = headWord;
        }
    }
}
