package com.mjx.parser;

import com.mjx.syntax.CNF;

public class Parser2 extends CKYParser {

    public Parser2(CNF cnf) {
        super(cnf);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] wts = sentence.trim().split("\\s+");
        String[] words = new String[wts.length];
        String[] tags = new String[wts.length];

        for (int i = 0; i < wts.length; ++i) {
            String[] wordTag = wts[i].split("/");

            if (wordTag[0].equals("(")) {
                words[i] = "-LRB-";
                tags[i] = "-LRB-";
            } else if (wordTag[0].equals(")")) {
                words[i] = "-RRB-";
                tags[i] = "-RRB-";
            } else if (wordTag[0].equals("{")) {
                words[i] = "-LCB-";
                tags[i] = "-LRB-";
            } else if (wordTag[0].equals("}")) {
                words[i] = "-RCB-";
                tags[i] = "-RRB-";
            }
            words[i] = wordTag[0];
            tags[i] = wordTag[1];
        }

        this.setTags(tags);
        this.setWords(words);
    }
}
