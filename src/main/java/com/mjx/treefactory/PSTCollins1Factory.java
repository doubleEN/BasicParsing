package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.phrasestructuretree.PSTCollins1;

public class PSTCollins1Factory implements TreeFactory{

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PSTCollins1(treeStr);
    }
}
