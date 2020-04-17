package net.zerobone.grammax.grammar.verification.messages;

import net.zerobone.grammax.grammar.Grammar;
import net.zerobone.grammax.grammar.Symbol;

public class NonTerminalNotDefinedMessage extends VerificationMessage {

    private final String nonTerminal;

    public NonTerminalNotDefinedMessage(Symbol nonTerminal) {
        super(false);
        this.nonTerminal = nonTerminal.id;
    }

    @Override
    public String getMessage(Grammar grammar) {
        return "Non-terminal '" + nonTerminal + "' has not been defined.";
    }

}