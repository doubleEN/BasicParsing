package com.mjx.TreeFactory;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;

public interface TreeFactory {
    BasicPhraseStructureTree createStructureTree(String treeStr);
}
