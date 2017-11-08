package com.mjx.TreeFactory;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.PhraseStructureTree.PSTCollins1;

public class PSTCollins1Factory implements TreeFactory{

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PSTCollins1(treeStr);
    }
}
