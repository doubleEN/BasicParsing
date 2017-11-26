package com.mjx.explore;

import com.mjx.phrasestructuretree.BasicPhraseStructureTree;
import com.mjx.treefactory.PSTPennTreeBankFactory;
import com.mjx.treefactory.TreeFactory;
import com.mjx.treeload.PennTreeBankStream;
import com.mjx.treeload.TreeBankStream;
import com.mjx.syntax.CNF;
import com.mjx.syntax.PennCFG;
import com.mjx.syntax.Rule;
import com.mjx.utils.PennTreeBankUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataAnalysis {
    public static void main(String[] args) throws IOException {
        DataAnalysis analysis = new DataAnalysis(new PennTreeBankStream());
        //加载PennTreeBank
        for (int no = 1; no < 200; ++no) {
            String treeBank = PennTreeBankUtil.getCombinedPath()+"/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            analysis.loadBank(treeBank, "utf-8", new PSTPennTreeBankFactory());
        }
        analysis.convertCNF();
        //统计数据情况
        String fPath = DataAnalysis.class.getResource("").getFile();

        analysis.printNonCNFOnCNF(fPath + "NonCNFOnCNF.txt");
        analysis.printNonCNFOnCFG(fPath + "NonCNFOnCFG.txt");
        analysis.printNotEnder(fPath + "NotEnder.txt");
        analysis.printNotS(fPath + "NotS.txt");
        analysis.printOver2(fPath + "Over2.txt");
        analysis.printSameLandR(fPath + "SameLandR.txt");
        analysis.printUnitProductions(fPath + "UnitProductions.txt");
        analysis.printSameSymbol(fPath + "SameSymbol.txt");
        analysis.printNonCNFOnCNF2(fPath + "NonCNFOnCNF2.txt");
        analysis.printSpecialSameLR(fPath + "SpecialSameLR.txt");
        analysis.printSpecialSymbol(fPath + "SpecialSymbol.txt");
        analysis.printUnitProductionsTree(fPath + "UnitProductionsTree.txt");
        analysis.print(fPath + "wrong.txt");
    }

    private CNF grammer=new PennCFG();

    private TreeBankStream bankStream;

    public Set<Rule> CFGs;

    public Set<Rule> CNFs;

    private Set<BasicPhraseStructureTree> phraseStructureTrees = new HashSet<>();

    public DataAnalysis(TreeBankStream bankStream) {
        this.bankStream = bankStream;
    }

    public void loadBank(String bankPath, String encoding, TreeFactory treeFactory) throws IOException {
        this.bankStream.openTreeBank(bankPath, encoding, treeFactory);
        BasicPhraseStructureTree phraseStructureTree = null;
        while ((phraseStructureTree = this.bankStream.readNextTree()) != null) {
//            System.out.println(phraseStructureTree);
            this.phraseStructureTrees.add(phraseStructureTree);
            this.grammer.expandGrammer(phraseStructureTree);

        }
    }

    public void convertCNF() {
        this.grammer.convertToCNFs();
        this.CFGs = this.grammer.getCFGs();
        this.CNFs = this.grammer.getCNFs();
    }

    /**
     * 输出CFG中所有非CNF规则,
     * 主要有两种情形：1.unit productions
     */
    public void printNonCNFOnCFG(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CFGs) {
            if (!this.grammer.isCNF(rule)) {
                bw.write(rule.toString());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     * 获得CFG上的所有unit productions
     */
    public void printUnitProductions(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CFGs) {
            if (rule.getRHS().len() == 1 && this.grammer.isNonterminal(rule.getRHS().getValues()[0])) {
                bw.write(rule.toString());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     * 左右项相同的规则，多为标点符号
     */
    public void printSameLandR(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CFGs) {
            if (rule.getRHS().len() == 1) {
                if (rule.getLHS().getValue().equals(rule.getRHS().getValues()[0])) {
                    bw.write(rule.toString());
                    bw.newLine();
                    bw.flush();
                }
            }
        }
    }

    /**
     * 检查CNF中是否有不符合CNF的规则（没有直接将unit productions进行转换）
     */
    public void printNonCNFOnCNF(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CNFs) {
            if (!this.grammer.isCNF(rule)) {
                bw.write(rule.toString());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     * 检查CNF中是否有不符合CNF的规则,不计标点的重复影响和unit productions
     */
    public void printNonCNFOnCNF2(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CNFs) {
            if (!this.grammer.isCNF(rule)) {
                String[] lhs = rule.getRHS().getValues();
                boolean flag = false;
                for (String s : lhs) {
                    if (this.grammer.isNonterminal(s) && this.grammer.isTerminal(s)) {
                        flag = true;
                    }
                    if (rule.getRHS().len() == 1) {
                        flag = true;
                    }
                }
                if (!flag) {
                    bw.write(rule.toString());
                    bw.newLine();
                    bw.flush();
                }
            }
        }
    }

    /**
     * 打印文法中，所有非S为根的树
     */
    public void printNotS(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (BasicPhraseStructureTree phraseStructureTree : this.phraseStructureTrees) {
            if (!phraseStructureTree.getRoot().equals("S")) {
                bw.write(phraseStructureTree.printTree());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     * 打印不以标点符号"."结尾的句子
     */
    public void printNotEnder(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (BasicPhraseStructureTree phraseStructureTree : this.phraseStructureTrees) {
            if (!phraseStructureTree.getRightMostLeaf().equals(".")) {
                bw.write(phraseStructureTree.printTree());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     * CFG上所有右项长度大于2的规则
     */
    public void printOver2(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CFGs) {
            if (rule.getRHS().len() > 2) {
                bw.write(rule.toString());
                bw.newLine();
                bw.flush();
            }
        }
    }


    /**
     * 找到同为终结符和非终结符的符号
     */
    public void printSameSymbol(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        Set<String> sameSybol = this.grammer.symbolIntersection();
        for (String str : sameSybol) {
            bw.write(str);
            bw.newLine();
            bw.flush();
        }
    }

    /**
     * 打印含有  NP->NP,TO->TO,VP->VP 的树
     */
    public void printSpecialSameLR(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (BasicPhraseStructureTree tree : this.phraseStructureTrees) {
            if (tree.hasSubTree("NP", "NP") || tree.hasSubTree("TO", "TO") || tree.hasSubTree("VP", "VP")) {
                bw.write(tree.printTree());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     * 打印含有 -RRB- -LRB- 的树
     */
    public void printSpecialSymbol(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (BasicPhraseStructureTree tree : this.phraseStructureTrees) {
            if (tree.hasNode("-RRB-") || tree.hasNode("-LRB-")) {
                bw.write(tree.printTree());
                bw.newLine();
                bw.write(tree.getSentence(false));
                bw.newLine();
                bw.flush();

            }
        }
    }

    //打印含有unit productions的树
    public void printUnitProductionsTree(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (BasicPhraseStructureTree tree : this.phraseStructureTrees) {
            String t = "";
            if (tree.hasUnitProductions() != null) {
                bw.write(tree.dictTree());
                bw.newLine();
                bw.flush();
            }
        }
    }

    /**
     *  从属性上看unit productions的不同情形:
     *  1.nonterminal-->nonterminal-->terminal；
     *  2.nonterminal-->nonterminal-->[nonterminal1  nonterminal2];
     */
    public void print(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (BasicPhraseStructureTree tree : this.phraseStructureTrees) {
            String t = "";
            if (tree.has3GramUnitProductions() == null && (t = tree.hasUnitProductions()) != null) {
                bw.write(t);
                bw.newLine();
                bw.flush();
            }
        }
    }

}
