package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.phrasestructuretree.PSTPennTreeBank;

public class PSTPennTreeBankFactory implements TreeFactory {

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PSTPennTreeBank(treeStr);
    }
}
