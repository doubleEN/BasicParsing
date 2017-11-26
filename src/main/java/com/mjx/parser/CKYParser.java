package com.mjx.parser;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.PhraseStructureTree.PSTPennTreeBank;
import com.mjx.syntax.CNF;
import com.mjx.syntax.LHS;

import java.util.*;

public abstract class CKYParser {

    /**
     * 文法集
     */
    private CNF cnf;

    /**
     * 词序列
     */
    private String[] words;

    /**
     * 词性序列
     */
    private String[] tags;

    /**
     * 符号解析数组
     */
    private String[][][] parseTable;

    /**
     * 回溯解码数组
     */
    private int[][][][] toolTable;

    /**
     * 词汇添加计数工具
     */
    private int count = 0;

    public CKYParser(CNF cnf) {
        this.cnf = cnf;
    }

    /**
     * 给定词性的情况下，解析句法树
     */
    public BasicPhraseStructureTree[] parsing(String sentence) throws CloneNotSupportedException {
        this.formatSentence(sentence);

        this.recognize();

        return this.buildParsingTree();
    }

    /**
     * 无词性的情况下，解析句法树
     */
    public BasicPhraseStructureTree[] parsing(String[] words) throws CloneNotSupportedException {
        this.words = words;

        this.recognize();

        return this.buildParsingTree();
    }

    /**
     * 给定词性的情况下，解析句法树
     */
    public BasicPhraseStructureTree[] parsing(String[] words, String[] tags) throws CloneNotSupportedException {
        this.words = words;
        this.tags = tags;

        this.recognize();

        return this.buildParsingTree();
    }

    /**
     * 构造解析表格
     */
    private void initTable(int len) {
        //toolTable比parseTable多一维
        this.parseTable = new String[len][][];
        this.toolTable = new int[len][][][];
        //构建两个下三角矩阵
        for (int i = 0; i < len; ++i) {
            this.parseTable[i] = new String[i + 1][];//单元格初始长度为1
            this.toolTable[i] = new int[i + 1][][];//一个单元格需要两个指向两个关联单元格，每个关联单元格需要3个位置数组索引
        }
    }

    /**
     * 识别生句子，重构去除String类型，直接使用RHS、LHS
     */
    private void recognize() throws CloneNotSupportedException {
        int wordLen = this.words.length;
        this.initTable(wordLen);

        //遍历矩阵每行
        for (int row = 0; row < wordLen; ++row) {
            String[] singleLHSs = null;
            //首先填充词汇的LHS
            if (this.tags == null) {
                LHS[]lhs = cnf.searchLHS(this.words[row]).toArray(new LHS[]{});
                singleLHSs = new String[lhs.length];
                for (int i=0;i<lhs.length;++i) {
                    singleLHSs[i] = lhs[i].getValue();
                }
            } else {
                singleLHSs=new String[]{tags[row]};
            }
            //填充词性层，可能为null
            //下三角矩阵每行的最后一格填充词汇的LHS
            this.parseTable[row][this.parseTable[row].length - 1] = singleLHSs;
            //toolTable的相应位置为null，以便终结判断（本身就为null，声明以便阅读）
            this.toolTable[row][this.toolTable[row].length - 1] = null;
            System.out.println("单元格_[row:"+row+",col:"+(this.parseTable[row].length-1)+"] 放入："+Arrays.toString(this.parseTable[row][this.parseTable[row].length-1]));
            //从解析数组当前行的倒数第二格沿这行向前解析
            for (int col = this.parseTable[row].length - 2; col >= 0; --col) {
                //当前格可能的LHS
                Map<int[], String> currCell = new HashMap<>();
                //为了填充单元格[row,col]，在可选的span上选择RHS的组合情况
                for (int spanHead = col, spanTail = col + 1; spanHead < row && spanTail < row + 1; ++spanHead, ++spanTail) {
                    //在下三角矩阵上，span的上的RHS选择有明确的数学关系：(row,col)-->( each[col,row) ,col) (row, each[col+1,row+1] )
                    String[] firstChild = this.parseTable[spanHead][col];
                    String[] secondChild = this.parseTable[row][spanTail];
                    //当前span的切分上，没有有效的孩子
                    if (firstChild == null || secondChild == null) {
                        continue;
                    }
                    //当前span的切分上，RHS[0]>=0,RHS[1]>=0，取RHS[0]和RHS[1]的笛卡尔积作为当前格的可能RHS，但要严格区分左右顺序
                    for (int first = 0; first < firstChild.length; ++first) {
                        for (int second = 0; second < secondChild.length; ++second) {
                            //查找可能的LHS
                            Set<LHS> partLHS = this.cnf.searchLHS(firstChild[first], secondChild[second]);
                            //可能不存在这样的LHS
                            if (partLHS == null) {
                                continue;
                            }
                            //不同的左右child，可能出现相同的LHS,允许相同的LHS出现,size区分相同的RHS，但不同的LHS
                            for (LHS _lhs : partLHS) {
                                int[] index = new int[]{currCell.size(), spanHead, col, first, row, spanTail, second};
                                currCell.put(index, _lhs.getValue());
                            }
                        }
                    }
                    //找到的所有LHS放入数组形式的当前单元格中
                    Set<Map.Entry<int[], String>> LHSInSet = currCell.entrySet();
                    //当前单元格存在LHS，构造parseTable、toolTable
                    this.parseTable[row][col] = new String[currCell.size()];
                    this.toolTable[row][col] = new int[currCell.size()][];

                    for (Map.Entry<int[], String> s : LHSInSet) {
                        this.parseTable[row][col][s.getKey()[0]] = s.getValue();
                        //toolTable中放入当前格的RHS[0]、RHS[1]索引
                        this.toolTable[row][col][s.getKey()[0]] = new int[]{s.getKey()[1], s.getKey()[2], s.getKey()[3], s.getKey()[4], s.getKey()[5], s.getKey()[6]};
                    }
                }
                System.out.println("单元格_[row:"+row+",col:"+col+"] 放入："+Arrays.toString(this.parseTable[row][col]));
            }
        }

        if (this.parseTable[this.parseTable.length - 1][0].length == 0) {
            System.err.println("无法解析目标句子为当前语种。");
        }
    }

    /**
     * 回溯得到短语结构树
     */
    private BasicPhraseStructureTree[] buildParsingTree() {
        int treeNum = this.parseTable[this.parseTable.length - 1][0].length;
        BasicPhraseStructureTree[] phraseStructureTrees = new BasicPhraseStructureTree[treeNum];
        for (int i = 0; i < treeNum; ++i) {
            BasicPhraseStructureTree.Node root = this.buildSubTree(this.parseTable.length - 1, 0, i);
            phraseStructureTrees[i] = new PSTPennTreeBank(root);
            count = 0;
        }
        return phraseStructureTrees;
    }

    /**
     * 构建短语结构树的递归方法
     * @param row 当前符号数组的一维
     * @param col 当前符号数组的二维
     * @param rank 当前符号数组的三维
     * @return 由当前符号构造的子树
     */
    private BasicPhraseStructureTree.Node buildSubTree(int row, int col, int rank) {
        BasicPhraseStructureTree.Node parent = new BasicPhraseStructureTree.Node(this.parseTable[row][col][rank]);

        //判断this.toolTable[row][col]，而不是this.toolTable[row][col][rank]
        if (this.toolTable[row][col] == null) {
            BasicPhraseStructureTree.Node leaf = new BasicPhraseStructureTree.Node(this.words[count++]);
            parent.addChild(leaf);
            leaf.setParent(parent);
            return parent;
        }
        int[] child_index = this.toolTable[row][col][rank];

        BasicPhraseStructureTree.Node child1 = this.buildSubTree(child_index[0], child_index[1], child_index[2]);
        if (child1 != null) {
            parent.addChild(child1);
            child1.setParent(parent);
        }
        BasicPhraseStructureTree.Node child2 = this.buildSubTree(child_index[3], child_index[4], child_index[5]);
        if (child2 != null) {
            parent.addChild(child2);
            child2.setParent(parent);
        }
        return parent;
    }

    /**
     * 生文本的格式化方法
     */
    public abstract void formatSentence(String sentence);

    public void setWords(String[] words) {
        this.words = words;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

}
