package com.mjx;

import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.parse.Rule;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BasicPhraseStructureTreeTest extends TestCase {

    /**
     * 测试一个树加载工厂是否能够重复正确加载同一棵树
     */
    public void testSameTree() throws Exception {
        TreeBankStream treeBankStream = new PennTreeBankStream();

        treeBankStream.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8",new BasicPSTFactory());
        String tree1 = treeBankStream.readNextTree().toString();

        treeBankStream.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8",new BasicPSTFactory());
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
        String printTree = new BasicPhraseStructureTree(originalTree).toString();

        assertEquals(printTree, originalTree);
    }

    /**
     * 测试短语结构树是否嫩能够成功解析成规则集
     */
    public void testToRules() {
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";
        BasicPhraseStructureTree basicPhraseStructureTree = new BasicPhraseStructureTree(new PennTreeBankStream().format(tree));
        List<Rule> rules = basicPhraseStructureTree.generateRuleSet();

        //转换得到的规则数量
        assertEquals(rules.size(),5);
        //具体的规则
        assertTrue(rules.contains(new Rule("S","A1","A2")));
        assertTrue(rules.contains(new Rule("A1","B1","B2")));
        assertTrue(rules.contains(new Rule("A2","c3")));
        assertTrue(rules.contains(new Rule("B1","c1")));
        assertTrue(rules.contains(new Rule("B2","c2")));
        }

    /**
     * 测试是否生成特定的终结符集和非终结符集
     */
    public void testSymbol(){
        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";
        BasicPhraseStructureTree basicPhraseStructureTree = new BasicPhraseStructureTree(new PennTreeBankStream().format(tree));
        String[] nonterminal=basicPhraseStructureTree.getNonterminal();
        String[] terminal=basicPhraseStructureTree.getTerminal();

        assertEquals(5,nonterminal.length);
        assertEquals(3,terminal.length);

        int S = this.getIndex(nonterminal, "S");
        int A1 = this.getIndex(nonterminal, "A1");
        int B1 = this.getIndex(nonterminal, "B1");
        int B2 = this.getIndex(nonterminal, "B2");
        int A2 = this.getIndex(nonterminal, "A2");

        //从左往后顺序扫描括号表达式得到符号集
        assertEquals(0,S);
        assertEquals(1,A1);
        assertEquals(2,B1);
        assertEquals(3,B2);
        assertEquals(4,A2);

        int C1=this.getIndex(terminal,"c1");
        int C2=this.getIndex(terminal,"c2");
        int C3=this.getIndex(terminal,"c3");

        assertEquals(0,C1);
        assertEquals(1,C2);
        assertEquals(2,C3);
    }

    /**
     * 测试短语结构树是否toString出与树库加载流的format一样的树
     */
    public void testFormatTree()throws IOException{

        String tree = "((S(A1(B1 c1)(B2 c2))(A2 c3)))";

        TreeBankStream treeBankStream = new PennTreeBankStream();
        BasicPhraseStructureTree basicPhraseStructureTree = new BasicPhraseStructureTree(treeBankStream.format(tree));

        assertEquals(treeBankStream.format(tree),basicPhraseStructureTree.toString());
    }

    /**
     * PennTreeBank的树形打印测试
     */
    public void testPrintPennTreeBank()throws IOException{

        TreeBankStream treeBankStream = new PennTreeBankStream();
        treeBankStream.openTreeBank("/home/jx_m/桌面/NLparsing/treebank/combined/wsj_0002.mrg", "utf-8",new BasicPSTFactory());

        //树库中生成的短语结构树
        BasicPhraseStructureTree psTree=treeBankStream.readNextTree();
        //将短语结构树打印成penn形式，再格式化
        String newPeenTree=treeBankStream.format(psTree.printTree());
        //检验格式化后的penn树形是否与最初的格式化树一致
        assertEquals(newPeenTree,psTree.toString());
    }

    /**]
     * 查找对应元素的索引
     */
    public int getIndex(String[] arr, String x) {
        for (int i = 0; i < arr.length; ++i) {
            if (arr[i].equals(x)) {
                return i;
            }
        }
        return -1;
    }
}