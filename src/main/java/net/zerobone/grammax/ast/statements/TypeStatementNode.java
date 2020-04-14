package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;

public class TypeStatementNode extends StatementNode {

    public String symbol;

    public String type;

    public TypeStatementNode(String symbol, String type) {
        this.symbol = symbol;
        this.type = type;
    }

    @Override
    public void apply(GrammaxContext context) {
        context.addType(symbol, type);
    }

}