package com.mjx.parser;

import com.mjx.syntax.CNF;

public class Parser2 extends CKYParser{

    public Parser2(CNF cnf) {
        super(cnf);
    }

    @Override
    public void formatSentence(String sentence) {
        String[] wts = sentence.trim().split("\\s+");
        String[]words = new String[wts.length];
        String[]tags = new String[wts.length];

        for (int index = 0; index < wts.length; ++index) {
            String[] wordTag = wts[index].split("/");
            words[index] = wordTag[0];
            tags[index] = wordTag[1];
        }

        this.setTags(tags);
        this.setWords(words);
    }
}
