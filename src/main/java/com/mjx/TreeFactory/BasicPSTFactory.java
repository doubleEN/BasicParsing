package com.mjx.TreeFactory;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;

public class BasicPSTFactory implements TreeFactory{
    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new BasicPhraseStructureTree(treeStr);
    }
}
