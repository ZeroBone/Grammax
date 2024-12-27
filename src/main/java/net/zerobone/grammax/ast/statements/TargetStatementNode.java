package net.zerobone.grammax.ast.statements;

import net.zerobone.grammax.GrammaxContext;
import net.zerobone.grammax.TargetLanguage;

public class TargetStatementNode extends StatementNode {

    private final String targetLanguageName;

    private final TargetLanguage targetLanguage;

    public TargetStatementNode(String targetLanguageName) {
        this.targetLanguageName = targetLanguageName;
        targetLanguage = TargetLanguage.fromString(targetLanguageName);
    }

    @Override
    public void apply(GrammaxContext context) {

        if (targetLanguage == null) {
            context.addError("Invalid target language name '" + targetLanguageName + "'.");
            return;
        }

        context.setTargetLanguage(targetLanguage);

    }

}
