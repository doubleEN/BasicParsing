package com.mjx.applications;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.PSTPennTreeBankFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.syntax.CNF;
import com.mjx.syntax.PennCFG;
import com.mjx.utils.PennTreeBankUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PeenTreeBankLoading {

    public static void main(String[] args) throws Exception {
        new PeenTreeBankLoading().loadBank();
    }

    public static int testBankSize() throws IOException {
        int num = 0;

        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/raw/wsj_" + PennTreeBankUtil.ensureLen(no);
            BufferedReader br = new BufferedReader(new FileReader(treeBank));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("") && !line.trim().endsWith(".START")) {
                    ++num;
                }
            }
        }

        return num;
    }

    public void loadBank() throws Exception {
        CNF grammer=new PennCFG();
        TreeBankStream treeBankStream = new PennTreeBankStream();
        int num = 0;
        for (int no = 1; no < 200; ++no) {
            int num1=0;
            int num2=0;
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            String treeBank2 = "/home/jx_m/桌面/NLparsing/treebank/raw/wsj_" + PennTreeBankUtil.ensureLen(no);

            BufferedReader br = new BufferedReader(new FileReader(treeBank2));
            String line=br.readLine();
            while ((line=br.readLine())!=null) {
                if (!line.trim().equals("")){
                    ++num2;
                }
            }

            treeBankStream.openTreeBank(treeBank, "utf-8", new PSTPennTreeBankFactory());
            BasicPhraseStructureTree basicPhraseStructureTree = null;
            while ((basicPhraseStructureTree = treeBankStream.readNextTree()) != null) {
                grammer.expandGrammer(basicPhraseStructureTree);
                System.out.println(basicPhraseStructureTree.toString());
                ++num1;
                ++num;
            }

            if (num1 != num2) {
                System.out.println(treeBank+":"+num1);
                System.out.println(treeBank2+":"+num2);
            }
        }
        System.out.println("树库总共包含句子：" + num + " 棵");//3914
        System.out.println("树库非终结符数量：" + grammer.getSizeOfNonterminals());
        System.out.println("树库终结符数量：" + grammer.getSizeOfTerminals());
        System.out.println("树库CFG规则数：" + grammer.getSizeOfCFG());
    }


}
