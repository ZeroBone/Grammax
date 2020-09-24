package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;
import net.zerobone.grammax.ast.entities.ProductionSymbol;
import net.zerobone.grammax.lexer.tokens.CodeToken;

import java.util.ArrayList;

public class ProductionStatementNode extends StatementNode {

    public String nonTerminal;

    public ArrayList<ProductionSymbol> production;

    public String code;

    public ProductionStatementNode(String nonTerminal, ArrayList<ProductionSymbol> production, CodeToken code) {
        this.nonTerminal = nonTerminal;
        this.production = production;
        this.code = code == null ? null : code.code;
    }

    @Override
    public void apply(GrammaxContext context) {
        context.addProduction(nonTerminal, this);
    }

}