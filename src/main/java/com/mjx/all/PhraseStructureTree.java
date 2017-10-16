package com.mjx.all;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class PhraseStructureTree {

    private Node root;

    abstract Rule nodeToRule(Node node);

    abstract PhraseStructureTree generateTree(String treeStr);

    abstract Set<Rule> generateRuleSet();

    private class Node {

        private int value;

        private boolean leafFlag=true;

        private List<Node> children = new ArrayList<Node>();

        /**
         * 给当前节点添加最右孩子节点
         */
        public void addChild(Node node){
            this.leafFlag=false;
            this.children.add(node);
        }

        /**
         *  获得当前节点的值
         */
        public int getValue(){
            return this.value;
        }

        /**
         * 判断当前节点是否为叶子节点
         */
        public boolean isLeaf(){
            return this.leafFlag;
        }

        /**
         * 返回当前节点的孩子个数
         */
        public int numChild(){
            return this.children.size();
        }

        /**
         * 返回孩子节点的构造器
         */
        public Iterator<Node> treeIter() {
            return this.children.iterator();
        }
    }

}
