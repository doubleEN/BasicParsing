package com.mjx.applications;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;

public class PeenTreeBankLoading {

    public static void main(String[] args) throws Exception {
        TreeBankStream treeBankStream =new PennTreeBankStream();
        int num=0;
        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + ensureLen(no) + ".mrg";
            treeBankStream.openTreeBank(treeBank,"utf-8",new BasicPSTFactory());
            BasicPhraseStructureTree basicPhraseStructureTree =null;
            while ((basicPhraseStructureTree = treeBankStream.readNextTree())!=null) {
                System.out.println(basicPhraseStructureTree.toString());
                ++num;
            }
        }
        System.out.println("树库总共包含句子："+num+" 棵");//3914
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
