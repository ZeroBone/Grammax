package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;

public abstract class StatementNode {

    public abstract void apply(GrammaxContext context);

}