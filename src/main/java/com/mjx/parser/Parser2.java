package com.mjx.parser;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.syntax.Grammer;
import com.mjx.utils.PennTreeBankUtil;

public class Parser2 extends CKYParser {

    public static void main(String[] args) throws Exception {
        TreeBankStream bankStream = new PennTreeBankStream();
        Grammer grammer = new Grammer();
        //加载PennTreeBank
        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            bankStream.openTreeBank(treeBank, "utf-8", new BasicPSTFactory());
            BasicPhraseStructureTree phraseStructureTree = null;
            while ((phraseStructureTree = bankStream.readNextTree()) != null) {
                grammer.expandGrammer(phraseStructureTree);
            }
        }
        grammer.convertToCNFs();
        CKYParser ckyParser = new Parser2(grammer);
        BasicPhraseStructureTree[] phraseStructureTrees = ckyParser.parsing("Clark J. Vitulli");
        for (BasicPhraseStructureTree phraseStructureTree : phraseStructureTrees) {
            System.out.println(phraseStructureTree.toString());
        }
    }

    public Parser2(Grammer grammer) {
        super(grammer);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] words = sentence.trim().split("\\s+");
        this.setTags(null);
        this.setWords(words);
    }
}
