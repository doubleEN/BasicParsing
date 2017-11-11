package com.mjx.explore;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeFactory.TreeFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.parse.Grammer;
import com.mjx.parse.Rule;
import com.mjx.utils.PennTreeBankUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataAnalysis {
    public static void main(String[] args) throws IOException {
        DataAnalysis analysis = new DataAnalysis(new PennTreeBankStream());
        //加载PennTreeBank
        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            analysis.loadBank(treeBank, "utf-8", new BasicPSTFactory());
        }

        //统计数据情况
        String fPath=DataAnalysis.class.getResource("").getFile();

        analysis.printNonCNFOnCNF(fPath+"NonCNFOnCNF.txt");
        analysis.printNonCNFOnCFG(fPath+"NonCNFOnCFG.txt");
        analysis.printNotEnder(fPath+"NotEnder.txt");
        analysis.printNotS(fPath+"NotS.txt");
        analysis.printOver2(fPath+"Over2.txt");
        analysis.printSameLandR(fPath+"SameLandR.txt");
        analysis.printUnitProductions(fPath+"UnitProductions.txt");
        analysis.printSameSymbol(fPath+"SameSymbol.txt");
    }

    private Grammer grammer = new Grammer();

    private TreeBankStream bankStream;

    private Set<Rule> CFGs;

    private Set<Rule> CNFs;

    private Set<BasicPhraseStructureTree> phraseStructureTrees = new HashSet<>();

    public DataAnalysis(TreeBankStream bankStream) {
        this.bankStream = bankStream;
    }

    public void loadBank(String bankPath, String encoding, TreeFactory treeFactory) throws IOException {
        this.bankStream.openTreeBank(bankPath, encoding, treeFactory);
        BasicPhraseStructureTree phraseStructureTree = null;
        while ((phraseStructureTree = this.bankStream.readNextTree()) != null) {
            System.out.println(phraseStructureTree);
            this.phraseStructureTrees.add(phraseStructureTree);
            this.grammer.expandGrammer(phraseStructureTree);

        }
        this.CFGs = this.grammer.getCFGs();
        this.CNFs = this.grammer.getCNFs();
    }

    /**
     * 输出CFG中所有非CNF规则
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

}