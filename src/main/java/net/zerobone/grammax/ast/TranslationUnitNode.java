package net.zerobone.grammax.ast;

import net.zerobone.grammax.ast.statements.StatementNode;

import java.util.ArrayList;

public class TranslationUnitNode extends AstNode {

    public ArrayList<StatementNode> statements = new ArrayList<>();

    public TranslationUnitNode() {}

    public void addStatement(StatementNode s) {
        statements.add(s);
    }

}