package net.zerobone.grammax.grammar.slr.conflict;

import net.zerobone.grammax.grammar.slr.Automation;

public class AcceptOption extends ConflictOption {

    public AcceptOption() {
        super(SHORT_NAME_ACCEPT);
    }

    @Override
    public String toString(Automation automation) {
        return "reduce to the starting symbol (accept)";
    }

}