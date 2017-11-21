package com.mjx.parser;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.syntax.CNF;
import com.mjx.syntax.PennCFG;
import com.mjx.utils.PennTreeBankUtil;

public class Parser2 extends CKYParser {

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
        CKYParser ckyParser = new Parser2(pennCFG);
        // 句子长度为13，跑到N.V.时，栈溢出
        BasicPhraseStructureTree[] phraseStructureTrees = ckyParser.parsing("Clark J. Vitulli");
        for (BasicPhraseStructureTree phraseStructureTree : phraseStructureTrees) {
            System.out.println(phraseStructureTree.printTree());
        }
    }

    public Parser2(CNF cnf) {
        super(cnf);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] words = sentence.trim().split("\\s+");
        this.setTags(null);
        this.setWords(words);
    }
}
