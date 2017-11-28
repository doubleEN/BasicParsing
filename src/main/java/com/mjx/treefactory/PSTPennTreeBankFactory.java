package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.phrasestructuretree.PennTreeBankPST;

public class PSTPennTreeBankFactory implements TreeFactory {

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PennTreeBankPST(treeStr);
    }
}
