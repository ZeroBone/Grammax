package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;
import net.zerobone.grammax.ParsingAlgorithm;

public class AlgoStatementNode extends StatementNode {

    private final String algorithmName;

    private final ParsingAlgorithm algorithm;

    public AlgoStatementNode(String algorithmName) {
        this.algorithmName = algorithmName;
        algorithm = ParsingAlgorithm.fromString(algorithmName);
    }

    @Override
    public void apply(GrammaxContext context) {

        if (algorithm == null) {
            context.addError("Invalid algorithm name '" + algorithmName + "'.");
            return;
        }

        context.setParsingAlgorithm(algorithm);

    }

}