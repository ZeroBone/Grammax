package net.zerobone.grammax.ast;

import net.zerobone.grammax.ast.statements.StatementNode;

import java.util.LinkedList;

public class TranslationUnitNode extends AstNode {

    public LinkedList<StatementNode> statements = new LinkedList<>();

    public TranslationUnitNode() {}

    public void addStatement(StatementNode s) {
        statements.addFirst(s);
    }

}