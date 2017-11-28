package com.mjx.syntax;

import junit.framework.TestCase;

public class RuleChainTest extends TestCase {

    /**
     * 测试chain的基本行为
     */
    public void testRuleChain() throws Exception {

        String[] singleSymbols = {"A","B","C"};

        String[] tailSymbols = {"w1", "w2","w3"};

        RuleChain chain = new RuleChain(singleSymbols, tailSymbols);

        assertEquals(4,chain.getChainLen());
        assertEquals(3,chain.getTailLen());

        assertEquals(new Rule("A","w1", "w2","w3"),chain.getEqualRule());

        assertEquals(3, chain.getRuleChain().length);
        assertEquals(new Rule("A","B"),chain.getRuleChain()[0]);
        assertEquals(new Rule("B","C"),chain.getRuleChain()[1]);
    }

}