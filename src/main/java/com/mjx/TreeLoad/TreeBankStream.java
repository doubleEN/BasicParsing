package com.mjx.TreeLoad;

import com.mjx.PhraseStructureTree.BasicPhraseStructureTree;
import com.mjx.TreeFactory.TreeFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 读取文本中的树。
 */
public interface TreeBankStream {

    /**
     * 打开树库流，要能够实现文本间的读取转换
     */
     void openTreeBank(String bankPath,String encoding, TreeFactory treeFactory)throws FileNotFoundException,UnsupportedEncodingException,IOException;

    /**
     * 返回下一棵句法树
     */
     BasicPhraseStructureTree readNextTree() throws IOException;

    /**
     * 关闭流
     */
    void closeCurrentStream()throws IOException;

    /**
     * 将代表结构树的括号表达式规范化为PhraseStructureTree能够处理的唯一形式
     */
    String format(String treeStr);

}
