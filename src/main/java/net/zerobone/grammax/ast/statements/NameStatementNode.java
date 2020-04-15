package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;

public class NameStatementNode extends StatementNode {

    private String name;

    public NameStatementNode(String name) {
        this.name = name;
    }

    @Override
    public void apply(GrammaxContext context) {
        context.setName(name);
    }

}