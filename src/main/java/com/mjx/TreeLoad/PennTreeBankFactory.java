package com.mjx.TreeLoad;

import com.mjx.parsing.PhraseStructureTree;

import java.io.*;

public class PennTreeBankFactory implements TreeFactory {

    /**
     * 加载树库的字符流
     */
    private BufferedReader br;

    /**
     * 记录PennBank中树的开始一行
     */
    private String lastStr = "";

    public PennTreeBankFactory() {
    }

    @Override
    public void openTreeBank(String bankPath, String encoding) throws IOException {
        this.closeCurrentStream();
        FileInputStream fis = new FileInputStream(bankPath);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        this.br = new BufferedReader(isr);
    }

    @Override
    public PhraseStructureTree readNextTree() throws IOException {
        if (br == null) {
            throw new IOException("树库加载流未构造。");
        }
        //treeBracket取上一次迭代最后取到的值或者为""
        if (this.lastStr == null) {
            return null;
        }
        String treeBracket = this.lastStr;
        String line = null;
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("") && line.charAt(0) == '(' && treeBracket.trim().equals("")) {
                treeBracket += line;
            } else if (!line.trim().equals("") && line.charAt(0) == '(') {
                this.lastStr = line;
                break;
            } else {
                treeBracket += line;
            }
        }
        treeBracket = this.format(treeBracket);
        //给一个文本的迭代一个终止条件,但在第一次迭代null时，不能直接结束当前迭代，因为当前得到的树是有效的
        if (line == null) {
            this.lastStr=null;
        }
        return new PhraseStructureTree(treeBracket);
    }

    /**
     * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
     */
    @Override
    public  String format(String tree) {
        //去除最外围的括号
        tree = tree.substring(1, tree.length() - 1).trim();
        //所有空白符替换成一位空格
        tree = tree.replaceAll("\\s+", " ");
        //去掉 ( 和 ) 前的空格
        String newTree = "";
        for (int c = 0; c < tree.length(); ++c) {
            if (tree.charAt(c) == ' ' && (tree.charAt(c + 1) == '(' || tree.charAt(c + 1) == ')')) {
                continue;
            } else {
                newTree = newTree + (tree.charAt(c));
            }
        }
        return newTree;
    }

    @Override
    public void closeCurrentStream() throws IOException {
        this.lastStr = "";
    }
}