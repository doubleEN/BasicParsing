package com.mjx.TreeFactory;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.PhraseStructureTree.PSTPennTreeBank;
import sun.reflect.generics.tree.Tree;

public class PSTPennTreeBankFactory implements TreeFactory {

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PSTPennTreeBank(treeStr);
    }
}
