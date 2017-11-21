package com.mjx.parser;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.syntax.CNF;
import com.mjx.syntax.PennCFG;
import com.mjx.utils.PennTreeBankUtil;

public class Parser1 extends CKYParser{
    public static void main(String[] args) throws Exception {
        TreeBankStream bankStream = new PennTreeBankStream();
        CNF pennCFG = new PennCFG();
        //加载PennTreeBank
        for (int no = 1; no < 200; ++no) {
            String treeBank = "C:\\Users\\2en\\Desktop\\treebank\\combined\\wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            bankStream.openTreeBank(treeBank, "utf-8", new BasicPSTFactory());
            BasicPhraseStructureTree phraseStructureTree = null;
            while ((phraseStructureTree = bankStream.readNextTree()) != null) {
                pennCFG.expandGrammer(phraseStructureTree);
            }
        }

        pennCFG.convertToCNFs();

        CKYParser ckyParser = new Parser1(pennCFG);

        BasicPhraseStructureTree[] phraseStructureTrees = ckyParser.parsing("Clark/NNP J./NNP Vitulli/NNP");
        for (BasicPhraseStructureTree phraseStructureTree : phraseStructureTrees) {
            System.out.println(phraseStructureTree.toString());
        }
    }

    public Parser1(CNF cnf) {
        super(cnf);
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