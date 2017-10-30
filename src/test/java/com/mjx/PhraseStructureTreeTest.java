package com.mjx;

import com.mjx.TreeLoad.PennTreeBankFactory;
import com.mjx.TreeLoad.TreeFactory;
import junit.framework.TestCase;

public class PhraseStructureTreeTest extends TestCase {

    /**
     * 测试一个树加载工厂是否能够重复正确加载同一棵树
     */
    public void testSameTree() throws Exception {
        TreeFactory treeFactory = new PennTreeBankFactory();

        treeFactory.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8");
        String tree1 = treeFactory.readNextTree().toString();

        treeFactory.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8");
        String tree2=treeFactory.readNextTree().toString();

        assertEquals(tree1,tree2);
    }

    /**
     * 测试PeenBank树的括号表达式和PhraseStructureTree的格式输出树的括号表达式是否完全一致。
     */
    public void testTreeGenerate(){
        String originalTree="( (S (NP-SBJ (NP (NNP Pierre) (NNP Vinken) )(, ,) (ADJP (NP (CD 61) (NNS years) )(JJ old) )(, ,) )(VP (MD will) (VP (VB join) (NP (DT the) (NN board) )(PP-CLR (IN as) (NP (DT a) (JJ nonexecutive) (NN director) ))(NP-TMP (NNP Nov.) (CD 29) )))(. .) ))";

        TreeFactory treeFactory=new PennTreeBankFactory();
        originalTree=treeFactory.format(originalTree);
        String printTree =new PhraseStructureTree(originalTree).toString();

        assertEquals(printTree,originalTree);
    }

}