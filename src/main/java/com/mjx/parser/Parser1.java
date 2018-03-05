package com.mjx.parser;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.treefactory.PSTPennTreeBankFactory;
import com.mjx.treefactory.TreeFactory;
import com.mjx.treeload.PennTreeBankStream;
import com.mjx.treeload.TreeBankStream;
import com.mjx.syntax.CNF;
import com.mjx.syntax.PennCFG;
import com.mjx.utils.PennTreeBankUtil;

public class Parser1 extends CYKParser {

    public static void main(String[] args) throws Exception {
        //构造树的过程中去掉none element
        TreeFactory treeFactory = new PSTPennTreeBankFactory(true);
        //构造一个空文法集
        CNF pennCFG = new PennCFG();
        //加载PennTreeBank
        TreeBankStream bankStream = new PennTreeBankStream();
        for (int no = 1; no < 200; ++no) {
            String treeBank = PennTreeBankUtil.getCombinedPath() + "/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            bankStream.openTreeBank(treeBank, "utf-8", treeFactory);
            BasicPhraseStructureTree phraseStructureTree = null;
            while ((phraseStructureTree = bankStream.readNextTree()) != null) {
                //此时在pennCFG中加载了unit productions
                pennCFG.expandGrammer(phraseStructureTree);
                if (phraseStructureTree.toString().indexOf("(JJ anti-takeover)(NN plan)") > 0) {
                    System.out.println("正确解析树：" + phraseStructureTree);
                    //(S(NP-SBJ(DT The)(NN company))(ADVP(RB also))(VP(VBD adopted)(NP(DT an)(JJ anti-takeover)(NN plan)))(. .))
                }
            }
        }

        //加载的Penn CFG转化为标准的CNF
        pennCFG.convertToCNFs();
        //构造cky解析工具
        CYKParser CYKParser = new Parser1(pennCFG);
        //解析生句得到多个穷举结果
        BasicPhraseStructureTree[] phraseStructureTrees = CYKParser.parsing("Kalipharma is a New Jersey-based pharmaceuticals concern . ");
        for (BasicPhraseStructureTree phraseStructureTree : phraseStructureTrees) {
            //CNF树还原为CFG树，还原的过程中，再次衍生出多个还原结果
            BasicPhraseStructureTree[] trees = phraseStructureTree.convertCFGTree(pennCFG);
            if (trees != null&&trees.length>1) {
                for (BasicPhraseStructureTree tree : trees) {
                    System.out.println(tree.printTree());
                }
            }

        }
    }

    public Parser1(CNF cnf) {
        super(cnf);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] words = sentence.trim().split("\\s+");
        for (int i = 0; i < words.length; ++i) {
            if (words[i].equals("(")) {
                words[i] = "-LRB-";
            } else if (words[i].equals(")")) {
                words[i] = "-RRB-";
            } else if (words[i].equals("{")) {
                words[i] = "-LCB-";
            } else if (words[i].equals("}")) {
                words[i] = "-RCB-";
            }
        }
        this.setTags(null);
        this.setWords(words);
    }
}
