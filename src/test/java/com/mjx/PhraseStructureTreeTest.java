package com.mjx;

import com.mjx.TreeLoad.PennTreeBankFactory;
import com.mjx.TreeLoad.TreeFactory;
import junit.framework.TestCase;

public class PhraseStructureTreeTest extends TestCase {

    /**
     * 测试一个树加载工厂是否能够正确加载多个文本中的树
     * @throws Exception
     */
    public void testTreeLoading() throws Exception {
        TreeFactory treeFactory = new PennTreeBankFactory();

        treeFactory.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8");
        String tree1 = treeFactory.readNextTree().toString();

        treeFactory.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8");
        String tree2=treeFactory.readNextTree().toString();

        assertEquals(tree1,tree2);
    }

}