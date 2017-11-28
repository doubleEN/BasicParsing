package com.mjx.treefactory;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.phrasestructuretree.Collins1PST;

public class PSTCollins1Factory implements TreeFactory {
    private boolean removeFlag = false;

    public PSTCollins1Factory(){}

    public PSTCollins1Factory(boolean removeFlag){
        this.removeFlag = removeFlag;
    }

    @Override
    public BasicPhraseStructureTree createStructureTree(String treeStr) {
        return new Collins1PST(treeStr,this.removeFlag);
    }

}
