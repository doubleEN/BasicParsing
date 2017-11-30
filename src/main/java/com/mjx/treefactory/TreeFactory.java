package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;

/**
 * 短语结构树工厂
 */
public interface TreeFactory {

    /**
     * 构造短语结构树
     * @param treeStr 树的括号表达式
     * @return 短语结构树
     */
    BasicPhraseStructureTree createStructureTree(String treeStr);

}
