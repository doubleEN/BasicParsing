package com.mjx.TreeFactory;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.PhraseStructureTree.PSTreeCollins1;

public class PSTCollins1Factory implements TreeFactory{

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PSTreeCollins1(treeStr);
    }
}
