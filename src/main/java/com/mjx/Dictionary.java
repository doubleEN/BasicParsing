package com.mjx;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    private Map<String, Integer> terminalId = new HashMap<String, Integer>();

    private Map<String, Integer> nonTerminalId = new HashMap<String, Integer>();

    private Map<Integer, String> terminalPhraseStructureTree = new HashMap<Integer, String>();

    private Map<Integer, String> nonTerminal = new HashMap<Integer, String>();

    public int addTerminalId(String terminal) {
        return this.terminalId.put(terminal, this.terminalId.size());
    }

    public int addNonTerminalId(String nonTerminal) {
        return this.nonTerminalId.put(nonTerminal, this.nonTerminalId.size());
    }

    public int getTerminalId(String symbol) {
        return this.terminalId.get(symbol);
    }

    public int getNonTerminalId(String symbol) {
        return this.nonTerminalId.get(symbol);
    }

    public String getTerminal(int symbol) {
        return this.terminalPhraseStructureTree.get(symbol);
    }

    public String getNonTerminal(int symbol) {
        return this.nonTerminal.get(symbol);
    }

    public boolean isTerminal(String symbol) {
        return this.terminalId.containsKey(symbol);
    }

    public boolean isNonTerminal(String symbol) {
        return this.nonTerminalId.containsKey(symbol);
    }

    public int terminalSize() {
        return this.terminalId.size();
    }

    public int nonTerminalSize() {
        return this.nonTerminalId.size();
    }
}

