package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;

public class TopStatementNode extends StatementNode {

    public String code;

    public TopStatementNode(String code) {
        this.code = code;
    }

    @Override
    public void apply(GrammaxContext context) {
        context.setTopCode(code);
    }

}