package com.mjx.parser;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.syntax.Grammer;
import com.mjx.syntax.Rule;
import com.mjx.utils.PennTreeBankUtil;

import java.util.Arrays;
import java.util.Set;

public class Parser1 extends CKYParser{
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

        grammer.eliminateUnitProductions();

        CKYParser ckyParser = new Parser1(grammer);

        BasicPhraseStructureTree[] phraseStructureTrees = ckyParser.parsing("Clark/NNP J./NNP Vitulli/NNP");
        for (BasicPhraseStructureTree phraseStructureTree : phraseStructureTrees) {
            System.out.println(phraseStructureTree.toString());
        }
    }

    public Parser1(Grammer grammer) {
        super(grammer);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] wts = sentence.trim().split("\\s+");
        String[]words = new String[wts.length];
        String[]tags = new String[wts.length];

        for (int index = 0; index < wts.length; ++index) {
            String[] wordTag = wts[index].split("/");
            words[index] = wordTag[0];
            tags[index] = wordTag[1];
        }

        this.setTags(tags);
        this.setWords(words);
    }
}
