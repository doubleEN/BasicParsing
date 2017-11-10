package com.mjx.explore;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.BasicPSTFactory;
import com.mjx.TreeFactory.PSTCollins1Factory;
import com.mjx.TreeFactory.TreeFactory;
import com.mjx.TreeLoad.PennTreeBankStream;
import com.mjx.TreeLoad.TreeBankStream;
import com.mjx.applications.PeenTreeBankLoading;
import com.mjx.parse.Grammer;
import com.mjx.parse.Rule;
import com.mjx.utils.PennTreeBankUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataAnalysis {
    public static void main(String[] args) throws IOException{
        DataAnalysis analysis = new DataAnalysis(new PennTreeBankStream());

        for (int no = 1; no < 200; ++no) {
            String treeBank = "/home/jx_m/桌面/NLparsing/treebank/combined/wsj_" + PennTreeBankUtil.ensureLen(no) + ".mrg";
            analysis.loadBank(treeBank, "utf-8",new BasicPSTFactory());
        }

        System.out.println(analysis.phraseStructureTrees.size());
    }

    private Grammer grammer = new Grammer();

    private TreeBankStream bankStream;

    private Set<Rule> CFGs;

    private Set<Rule> CNFs;

    private Set<BasicPhraseStructureTree> phraseStructureTrees = new HashSet<>();

    public DataAnalysis(TreeBankStream bankStream) {
        this.bankStream = bankStream;
    }

    public void loadBank(String bankPath, String encoding, TreeFactory treeFactory) throws IOException{
        this.bankStream.openTreeBank(bankPath,encoding,treeFactory);
        BasicPhraseStructureTree phraseStructureTree=null;
        while ((phraseStructureTree = this.bankStream.readNextTree()) != null) {
            System.out.println(phraseStructureTree);
            this.phraseStructureTrees.add(phraseStructureTree);
            this.grammer.expandGrammer(phraseStructureTree);

        }
        this.CFGs=this.grammer.getCFGs();
        this.CNFs=this.grammer.getCNFs();
    }

    /**
     * 输出CFG中所有非CNF规则
     */
    public void getNonCNFOnCFG(String path) throws IOException {
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
    public void getUnitProductions(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CFGs) {
            if (rule.getRHS().len() == 1 && this.grammer.isNonterminal(rule.getRHS().getValues()[0])) {
                bw.write(rule.toString());
                bw.newLine();
                bw.flush();
            }
        }
    }

    public void getSameLandR(String path) throws IOException {
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
     * 检查CNF中是否有不符合CNF的规则（即unit productions）
     */
    public void getNonCNF(String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for (Rule rule : this.CNFs) {
            if (!this.grammer.isCNF(rule)) {
                bw.write(rule.toString());
                bw.newLine();
                bw.flush();
            }
        }
    }

}
