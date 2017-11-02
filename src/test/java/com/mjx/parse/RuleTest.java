package com.mjx.parse;

import junit.framework.TestCase;

public class RuleTest extends TestCase {
    public void testEquals() throws Exception {
        //S-->[A1,A2,A3]
        LHS lhs = new LHS("S");
        RHS rhs = new RHS("A1", "A2", "A3");
        Rule rule = new Rule(lhs, rhs);

        //左项
        assertTrue(lhs.equals(new LHS("S")));
        //右项
        assertTrue(rhs.equals(new RHS("A1", "A2", "A3")));
        //规则
        assertTrue(rule.equals(new Rule("S", "A1", "A2", "A3")));
    }

    public void testHashCode() throws Exception {

        //S-->[A1,A2,A3]
        LHS lhs = new LHS("S");
        RHS rhs = new RHS("A1", "A2", "A3");
        Rule rule = new Rule(lhs, rhs);

        //左项
        assertEquals(lhs.hashCode(),new LHS("S").hashCode());
        //右项
        assertEquals(rhs.hashCode(),new RHS("A1", "A2", "A3").hashCode());
        //规则
        assertEquals(rule.hashCode(),new Rule("S", "A1", "A2", "A3").hashCode());

    }

}