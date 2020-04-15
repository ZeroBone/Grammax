package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;

public class ImportsStatementNode extends StatementNode {

    public String code;

    public ImportsStatementNode(String code) {
        this.code = code;
    }

    @Override
    public void apply(GrammaxContext context) {
        context.setImports(code);
    }

}