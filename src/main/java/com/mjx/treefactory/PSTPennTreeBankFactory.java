package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.phrasestructuretree.PennTreeBankPST;

public class PSTPennTreeBankFactory implements TreeFactory {
    private boolean removeFlag = false;

    public PSTPennTreeBankFactory(){}

    public PSTPennTreeBankFactory(boolean removeFlag){
        this.removeFlag = removeFlag;
    }

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new PennTreeBankPST(treeStr,this.removeFlag);
    }

}
