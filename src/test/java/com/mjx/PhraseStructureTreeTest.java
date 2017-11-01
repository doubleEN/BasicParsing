package com.mjx;

import com.mjx.TreeLoad.PennTreeBankFactory;
import com.mjx.TreeLoad.TreeFactory;
import com.mjx.parsing.PhraseStructureTree;
import com.mjx.parsing.Rule;
import junit.framework.TestCase;

import java.util.List;

public class PhraseStructureTreeTest extends TestCase {

    /**
     * 测试一个树加载工厂是否能够重复正确加载同一棵树
     */
    public void testSameTree() throws Exception {
        TreeFactory treeFactory = new PennTreeBankFactory();

        treeFactory.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8");
        String tree1 = treeFactory.readNextTree().toString();

        treeFactory.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8");
        String tree2 = treeFactory.readNextTree().toString();

        assertEquals(tree1, tree2);
    }

    /**
     * 测试PeenBank树的括号表达式和PhraseStructureTree的格式输出树的括号表达式是否完全一致。
     */
    public void testTreeGenerate() {
        String originalTree = "( (S (NP-SBJ (NP (NNP Pierre) (NNP Vinken) )(, ,) (ADJP (NP (CD 61) (NNS years) )(JJ old) )(, ,) )(VP (MD will) (VP (VB join) (NP (DT the) (NN board) )(PP-CLR (IN as) (NP (DT a) (JJ nonexecutive) (NN director) ))(NP-TMP (NNP Nov.) (CD 29) )))(. .) ))";

        TreeFactory treeFactory = new PennTreeBankFactory();
        originalTree = treeFactory.format(originalTree);
        String printTree = new PhraseStructureTree(originalTree).toString();

        assertEquals(printTree, originalTree);
    }

    /**
     * 测试短语结构树是否嫩能够成功解析成规则集
     */
    public void testToRules() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";
        PhraseStructureTree phraseStructureTree = new PhraseStructureTree(new PennTreeBankFactory().format(tree));
        System.out.println(phraseStructureTree.toString());
        List<Rule> rules = phraseStructureTree.generateRuleSet();

        //转换得到的规则数量
        assertEquals(rules.size(),5);
        //具体的规则
        assertTrue(rules.contains(new Rule("S","A1","A2")));
        assertTrue(rules.contains(new Rule("A1","B1","B2")));
        assertTrue(rules.contains(new Rule("A2","c3")));
        assertTrue(rules.contains(new Rule("B1","c1")));
        assertTrue(rules.contains(new Rule("B2","c2")));
        }

}