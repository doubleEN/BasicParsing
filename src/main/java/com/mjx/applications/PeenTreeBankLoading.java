package com.mjx.applications;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.parse.Grammer;

public class PeenTreeBankLoading {

    public static void main(String[] args) throws Exception {
        Grammer grammer=new Grammer();
        TreeBankStream treeBankStream = new PennTreeBankStream();
        int num = 0;
        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + ensureLen(no) + ".mrg";
            treeBankStream.openTreeBank(treeBank, "utf-8", new BasicPSTFactory());
            BasicPhraseStructureTree basicPhraseStructureTree = null;
            while ((basicPhraseStructureTree = treeBankStream.readNextTree()) != null) {
                grammer.expandGrammer(basicPhraseStructureTree);
                ++num;
            }
        }
        System.out.println("树库总共包含句子：" + num + " 棵");//3914
        System.out.println("树库非终结符数量："+grammer.getSizeOfNonterminals());
        System.out.println("树库终结符数量："+grammer.getSizeOfTerminals());
        System.out.println("树库CFG规则数："+grammer.getSizeOfCFG());
    }

    /**
     * 构造树库文本名
     */
    public static String ensureLen(int number) {
        if (Integer.toString(number).length() == 1) {
            return "000" + number;
        } else if (Integer.toString(number).length() == 2) {
            return "00" + number;
        } else {
            return "0" + number;
        }
    }
}
