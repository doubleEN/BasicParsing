package com.mjx;

import com.mjx.phrasestructuretree.PennTreeBankPST;
import com.mjx.treefactory.PSTPennTreeBankFactory;
import com.mjx.treeload.PennTreeBankStream;
import com.mjx.treeload.TreeBankStream;
import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.syntax.Rule;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class BasicPhraseStructureTreeTest extends TestCase {

    /**
     * 测试一个树加载工厂是否能够重复正确加载同一棵树
     */
    public void testSameTree() throws Exception {
        TreeBankStream treeBankStream = new PennTreeBankStream();

        treeBankStream.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8", new PSTPennTreeBankFactory());
        String tree1 = treeBankStream.readNextTree().toString();

        treeBankStream.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8", new PSTPennTreeBankFactory());
        String tree2 = treeBankStream.readNextTree().toString();

        assertEquals(tree1, tree2);
    }

    /**
     * 测试PeenBank树的括号表达式和PhraseStructureTree的格式输出树的括号表达式是否完全一致。
     */
    public void testTreeGenerate() {
        String originalTree = "( (S (NP-SBJ (NP (NNP Pierre) (NNP Vinken) )(, ,) (ADJP (NP (CD 61) (NNS years) )(JJ old) )(, ,) )(VP (MD will) (VP (VB join) (NP (DT the) (NN board) )(PP-CLR (IN as) (NP (DT a) (JJ nonexecutive) (NN director) ))(NP-TMP (NNP Nov.) (CD 29) )))(. .) ))";

        TreeBankStream treeBankStream = new PennTreeBankStream();
        originalTree = treeBankStream.format(originalTree);
        String printTree = new PennTreeBankPST(originalTree).toString();

        assertEquals(printTree, originalTree);
    }

    /**
     * 测试短语结构树是否嫩能够成功解析成规则集
     */
    public void testToRules() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";
        BasicPhraseStructureTree basicPhraseStructureTree = new PennTreeBankPST(new PennTreeBankStream().format(tree));
        List<Rule> rules = basicPhraseStructureTree.generateRuleSet();

        //转换得到的规则数量
        assertEquals(rules.size(), 5);
        //具体的规则
        assertTrue(rules.contains(new Rule("S", "A1", "A2")));
        assertTrue(rules.contains(new Rule("A1", "B1", "B2")));
        assertTrue(rules.contains(new Rule("A2", "c3")));
        assertTrue(rules.contains(new Rule("B1", "c1")));
        assertTrue(rules.contains(new Rule("B2", "c2")));
    }

    /**
     * 测试是否生成特定的终结符集和非终结符集
     */
    public void testSymbol() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";
        BasicPhraseStructureTree basicPhraseStructureTree = new PennTreeBankPST(new PennTreeBankStream().format(tree));
        Set<String> nonterminal = basicPhraseStructureTree.getNonterminals();
        Set<String> terminal = basicPhraseStructureTree.getTerminals();

        assertEquals(5, nonterminal.size());
        assertEquals(3, terminal.size());

        //从左往后顺序扫描括号表达式得到符号集
        assertTrue(nonterminal.contains("S"));
        assertTrue(nonterminal.contains("A1"));
        assertTrue(nonterminal.contains("B1"));
        assertTrue(nonterminal.contains("B2"));
        assertTrue(nonterminal.contains("A2"));

        assertTrue(terminal.contains("c1"));
        assertTrue(terminal.contains("c2"));
        assertTrue(terminal.contains("c3"));
    }

    /**
     * 测试短语结构树是否toString出与树库加载流的format一样的树
     */
    public void testFormatTree() throws IOException {

        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";

        TreeBankStream treeBankStream = new PennTreeBankStream();
        BasicPhraseStructureTree basicPhraseStructureTree = new PennTreeBankPST(treeBankStream.format(tree));

        assertEquals(treeBankStream.format(tree), basicPhraseStructureTree.toString());
    }

    /**
     * PennTreeBank的树形打印测试
     */
    public void testPrintPennTreeBank() throws IOException {
        TreeBankStream treeBankStream = new PennTreeBankStream();
        treeBankStream.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8", new PSTPennTreeBankFactory());

        //树库中生成的短语结构树
        BasicPhraseStructureTree psTree = treeBankStream.readNextTree();
        //将短语结构树打印成penn形式，再格式化
        String newPeenTree = treeBankStream.format(psTree.printTree());
        //检验格式化后的penn树形是否与最初的格式化树一致
        assertEquals(newPeenTree, psTree.toString());
    }

    /**
     * 测试是否能够正确从短语结构树获取正确的句子
     */
    public void testReleatedSentence() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";

        TreeBankStream treeBankStream = new PennTreeBankStream();
        BasicPhraseStructureTree basicPhraseStructureTree = new PennTreeBankPST(treeBankStream.format(tree));

        assertEquals("c1 c2 c3", basicPhraseStructureTree.getSentence(false));
        assertEquals("c1/B1 c2/B2 c3/A2", basicPhraseStructureTree.getSentence(true));
    }

    /**
     * 测试子树查找功能
     */
    public void testFindSubTree() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";

        TreeBankStream treeBankStream = new PennTreeBankStream();
        BasicPhraseStructureTree basicPhraseStructureTree = new PennTreeBankPST(treeBankStream.format(tree));

        assertTrue(basicPhraseStructureTree.hasSubTree("S", "A1", "A2"));
        assertTrue(basicPhraseStructureTree.hasSubTree("A1", "B1", "B2"));
        assertTrue(basicPhraseStructureTree.hasSubTree("B1", "c1"));
        assertTrue(basicPhraseStructureTree.hasSubTree("B2", "c2"));
        assertTrue(basicPhraseStructureTree.hasSubTree("A2", "c3"));
    }

    /**
     * 测试节点查找功能
     */
    public void testFindNode() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";

        TreeBankStream treeBankStream = new PennTreeBankStream();
        BasicPhraseStructureTree basicPhraseStructureTree = new PennTreeBankPST(treeBankStream.format(tree));

        assertTrue(basicPhraseStructureTree.hasNode("S"));
        assertTrue(basicPhraseStructureTree.hasNode("A1"));
        assertTrue(basicPhraseStructureTree.hasNode("A2"));
        assertTrue(basicPhraseStructureTree.hasNode("B1"));
        assertTrue(basicPhraseStructureTree.hasNode("B2"));
        assertTrue(basicPhraseStructureTree.hasNode("c1"));
        assertTrue(basicPhraseStructureTree.hasNode("c2"));
        assertTrue(basicPhraseStructureTree.hasNode("c3"));
    }


}