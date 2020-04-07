package net.zerobone.grammax.grammar.automation.conflict;

import net.zerobone.grammax.grammar.automation.Automation;

public class AcceptOption extends ConflictOption {

    public AcceptOption() {
        super(SHORT_NAME_ACCEPT);
    }

    @Override
    public String toString(Automation automation) {
        return "accept";
    }

}