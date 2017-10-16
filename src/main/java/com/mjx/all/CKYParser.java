package com.mjx.all;

public abstract class CKYParser {

    private Grammer grammer;

    private String[][][] parseTable;

    private int[][][][] toolTable;

    public CKYParser(Grammer grammer) {
        this.grammer = grammer;
    }

    /**
     * 初始化解析工具数组
     */
    private void initAttributes(int len) {
        this.parseTable = new String[len][][];
        this.toolTable = new int[len][][][];
        //构造数组
        for (int i = 0; i < len; ++i) {
            this.parseTable[i] = new String[i + 1][];//单元格初始长度为1
            this.toolTable[i] = new int[i + 1][][];//一个单元格需要两个指向两个关联单元格，每个关联单元格需要3个位置数组索引
        }
    }

    public abstract PhraseStructureTree[] parse(String unknownSentence);

    public abstract PhraseStructureTree[] parse(String[] words,String[]tags);

    //后处理应该在Grammer中
    public abstract PhraseStructureTree toCFGTree(PhraseStructureTree root);

}
