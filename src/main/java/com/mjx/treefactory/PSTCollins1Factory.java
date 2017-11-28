package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.phrasestructuretree.Collins1PST;

public class PSTCollins1Factory implements TreeFactory{

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new Collins1PST(treeStr);
    }
}
