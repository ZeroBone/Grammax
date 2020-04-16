package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;

public class NonTerminalNotDefinedMessage extends VerificationMessage {

    private final int nonTerminal;

    public NonTerminalNotDefinedMessage(int nonTerminal) {
        super(false);
        this.nonTerminal = nonTerminal;
    }

    @Override
    public String getMessage(Grammar grammar) {
        return "Non-terminal '" + grammar.idToSymbol(nonTerminal) + "' has not been defined.";
    }

}