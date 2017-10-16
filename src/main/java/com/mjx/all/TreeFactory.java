package com.mjx.all;

/**
 * 读取文本中的树，转化为Node形式，并每次只返回一棵树。
 */
public abstract class TreeFactory {

    /**
     * 打开树库流，要能够实现文本间的读取转换
     */
    abstract void openTreeBank(String bankPath);

    /**
     * 判断是否还存在下一棵树
     */
    abstract boolean hasTree();

    /**
     * 返回下一棵句法树
     */
    abstract PhraseStructureTree nextTree();

    /**
     * 关闭流
     */
    abstract void close();

}
