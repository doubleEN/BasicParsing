package com.mjx.applications;

import com.mjx.PhraseStructureTree;
import com.mjx.TreeLoad.PennTreeBankFactory;
import com.mjx.TreeLoad.TreeFactory;

public class TreeBankLoading {

    public static void main(String[] args) throws Exception {
        TreeFactory treeFactory=new PennTreeBankFactory();
        int num=0;
        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + ensureLen(no) + ".mrg";
            treeFactory.openTreeBank(treeBank,"utf-8");
            PhraseStructureTree phraseStructureTree=null;
            while ((phraseStructureTree=treeFactory.readNextTree())!=null) {
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
