package net.zerobone.grammax.grammar.slr;

public class AutomationProduction {

    public final int nonTerminal;

    public final String code;

    public final AutomationSymbol[] body;

    public AutomationProduction(int nonTerminalIndex, String code, int bodyLength) {
        this.nonTerminal = nonTerminalIndex;
        this.code = code;
        body = new AutomationSymbol[bodyLength];
    }

    public String toString(Automation automation) {

        StringBuilder sb = new StringBuilder();

        sb.append(automation.nonTerminalToSymbol(nonTerminal));
        sb.append(" -> ");

        if (body.length != 0) {
            for (int i = 0;; i++) {

                AutomationSymbol symbol = body[i];

                sb.append(
                    symbol.isTerminal ?
                    automation.terminalToSymbol(symbol.index) :
                    automation.nonTerminalToSymbol(symbol.index)
                );

                if (symbol.argumentName != null) {

                    sb.append('(');
                    sb.append(symbol.argumentName);
                    sb.append(')');

                }

                if (i != body.length - 1) {
                    sb.append(' ');
                    continue;
                }

                break;

            }
        }

        return sb.toString();

    }

}