package com.mjx.parser;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.treefactory.PSTPennTreeBankFactory;
import com.mjx.treeload.PennTreeBankStream;
import com.mjx.treeload.TreeBankStream;
import com.mjx.syntax.CNF;
import com.mjx.syntax.PennCFG;
import com.mjx.utils.PennTreeBankUtil;

public class Parser1 extends CKYParser {

    public static void main(String[] args) throws Exception {
        TreeBankStream bankStream = new PennTreeBankStream();
        CNF pennCFG = new PennCFG();
        //加载PennTreeBank
        for (int no = 1; no < 200; ++no) {
            String treeBank = PennTreeBankUtil.getCombinedPath()+"/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            bankStream.openTreeBank(treeBank, "utf-8", new PSTPennTreeBankFactory());
            BasicPhraseStructureTree phraseStructureTree = null;
            while ((phraseStructureTree = bankStream.readNextTree()) != null) {
                pennCFG.expandGrammer(phraseStructureTree);
                if (phraseStructureTree.toString().indexOf("(JJ anti-takeover)(NN plan)")>0){
                    System.out.println("目标树："+phraseStructureTree);
                    //(S(NP-SBJ(DT The)(NN company))(ADVP(RB also))(VP(VBD adopted)(NP(DT an)(JJ anti-takeover)(NN plan)))(. .))
                }
            }
        }
        pennCFG.convertToCNFs();
        CKYParser ckyParser = new Parser1(pennCFG);
        BasicPhraseStructureTree[] phraseStructureTrees = ckyParser.parsing("The company also adopted an anti-takeover plan .");
        for (BasicPhraseStructureTree phraseStructureTree : phraseStructureTrees) {
            if (phraseStructureTree.convertCFGTree(pennCFG)&&phraseStructureTree.getRoot()!=null){
                System.out.println(phraseStructureTree);
            }
        }
        //穷举结果中，存在正确的树形。
    }

    public Parser1(CNF cnf) {
        super(cnf);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] words = sentence.trim().split("\\s+");
        this.setTags(null);
        this.setWords(words);
    }
}
