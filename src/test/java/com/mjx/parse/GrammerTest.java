package com.mjx.parse;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import junit.framework.TestCase;

public class GrammerTest extends TestCase {

    private String treeStr="(A(B(C1 d1)(C2 d2)(C3 d3)))";

    //测试是否生成的指定CFG规则集
    public void testCFG() throws Exception {
        BasicPhraseStructureTree basicPhraseStructureTree = new BasicPhraseStructureTree(treeStr);
        Grammer grammer=new Grammer();
        grammer.addCFGRules(basicPhraseStructureTree.generateRuleSet());

        //得到的CFG规则集合
        Rule A_B = new Rule("A", "B");
        Rule B_C1C2C3 = new Rule("B", "C1","C2","C3");//右项过长的规则
        Rule C1_d1 = new Rule("C1", "d1");
        Rule C2_d2 = new Rule("C2", "d2");
        Rule C3_d3 = new Rule("C3", "d3");

        assertEquals(5,grammer.getSizeOfCFG());//CFG集的大小

        assertTrue(grammer.containCFGRule(A_B));
        assertTrue(grammer.containCFGRule(B_C1C2C3));
        assertTrue(grammer.containCFGRule(C1_d1));
        assertTrue(grammer.containCFGRule(C2_d2));
        assertTrue(grammer.containCFGRule(C3_d3));

    }

    //测试是否正确转化得到了CNF规则集
    public void testCNF() throws Exception {
        BasicPhraseStructureTree basicPhraseStructureTree = new BasicPhraseStructureTree(treeStr);
        Grammer grammer=new Grammer();
        grammer.addCFGRules(basicPhraseStructureTree.generateRuleSet());

        //拆分长右项得到的规则
        Rule rule0_C1C2 = new Rule("rule0", "C1","C2");
        Rule B_rule0C3 = new Rule("B","rule0","C3");

        assertEquals(6,grammer.getSizeOfCNF());//CNF集的大小

        assertTrue(grammer.containCNFRule(rule0_C1C2));
        assertTrue(grammer.containCNFRule(B_rule0C3));
    }

    //测试是否得到正确的终结符集和非终结符集
    public void testSymbols(){
        //(A(B(C1 d1)(C2 d2)(C3 d3)))
        BasicPhraseStructureTree basicPhraseStructureTree = new BasicPhraseStructureTree(treeStr);
        Grammer grammer=new Grammer();
        grammer.expandGrammer(basicPhraseStructureTree);

        assertEquals(5,grammer.getSizeOfNonterminals());
        assertTrue(grammer.isNonterminal("A"));
        assertTrue(grammer.isNonterminal("B"));
        assertTrue(grammer.isNonterminal("C1"));
        assertTrue(grammer.isNonterminal("C2"));
        assertTrue(grammer.isNonterminal("C3"));

        assertEquals(3,grammer.getSizeOfTerminals());
        assertTrue(grammer.isTerminal("d1"));
        assertTrue(grammer.isTerminal("d2"));
        assertTrue(grammer.isTerminal("d3"));
    }

}