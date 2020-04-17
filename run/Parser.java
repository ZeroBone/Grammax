/* This file was generated by Grammax v1.0.0-beta */

import java.util.Stack;

public final class Parser {
    public static final int T_A = 0;
    public static final int T_MUL = 1;
    public static final int T_PLUS = 2;
    public static final int T_EOF = 3;
    private static final int terminalCount = 4;
    private static final int nonTerminalCount = 1;
    private static final int[] gotoTable = {
        3,
        0,
        4,
        0,
        0,
        0};
    private static final int[] actionTable = {
        2,0,0,0,
        -4,0,0,-4,
        2,0,0,-1,
        0,6,5,0,
        -2,0,0,-2,
        -3,0,0,-3};
    private static final int[] productionLabels = {0,0,0};
    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                _grx_stack.pop();
                _grx_stack.pop();
                _grx_stack.pop();
                return null;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                _grx_stack.pop();
                _grx_stack.pop();
                _grx_stack.pop();
                return null;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                _grx_stack.pop();
                return null;
            }
        }
    };
    private static final StackEntry initialStackEntry = new StackEntry(0, null);
    private Stack<StackEntry> stack;
    private Object payload = null;
    private static final class StackEntry {
        private final int previousState;
        private final Object payload;
        private StackEntry(int previousState, Object payload) {
            this.previousState = previousState;
            this.payload = payload;
        }
    }
    private interface Reductor {
        Object reduce(Stack<StackEntry> _grx_stack);
    }
    public Parser() {
        stack = new Stack<>();
        stack.push(initialStackEntry);
    }
    public void reset() {
        stack.clear();
        stack.push(initialStackEntry);
        payload = null;
    }
    public void parse(int tokenId, Object tokenPayload) {
        while (true) {
            int action = actionTable[terminalCount * stack.peek().previousState + tokenId];
            if (action == 0) {
                throw new RuntimeException("Syntax error");
            }
            if (action == -1) {
                payload = stack.peek().payload;
                return;
            }
            if (action > 0) {
                stack.push(new StackEntry(action - 1, tokenPayload));
                return;
            }
            int productionIndex = -action - 2;
            Object reducedProduction = reductions[productionIndex].reduce(stack);
            StackEntry newState = stack.peek();
            int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabels[productionIndex]];
            stack.push(new StackEntry(nextState - 1, reducedProduction));
        }
    }
    public boolean successfullyParsed() {
        return payload != null;
    }
    public Object getValue() {
        assert payload != null : "parsing did not succeed";
        return payload;
    }
}