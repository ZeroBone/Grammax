package net.zerobone.parser;

import java.lang.Object;
import java.lang.Override;
import java.lang.SuppressWarnings;
import java.util.Stack;

public final class Parser {
    public static final int T_EOF = 0;

    public static final int T_TYPE = 1;

    public static final int T_ID = 2;

    public static final int T_LEFT_PAREN = 3;

    public static final int T_RIGHT_PAREN = 4;

    public static final int T_CODE = 5;

    public static final int T_SEMICOLON = 6;

    public static final int T_ASSIGN = 7;

    private static final int terminalCount = 8;

    private static final int nonTerminalCount = 6;

    private static final int[] gotoTable = {
    1,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,8,0,0,
    0,0,0,0,0,0,
    0,10,0,0,0,0,
    0,0,0,0,0,0,
    0,0,12,0,0,0,
    0,0,0,14,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0,
    0,0,0,0,0,0};

    private static final int[] actionTable = {
    0,2,3,0,0,0,0,0,
    -1,0,0,0,0,0,0,0,
    0,0,4,0,0,0,0,0,
    0,0,0,0,0,0,0,5,
    0,0,6,0,0,0,0,0,
    0,0,7,0,0,0,9,0,
    -2,-2,-2,0,0,0,0,0,
    0,0,-4,11,0,0,-4,0,
    -9,-9,-9,0,0,0,0,0,
    -6,-6,-6,0,0,13,0,0,
    0,0,7,0,0,0,9,0,
    0,0,15,0,0,0,0,0,
    -8,-8,-8,0,0,0,0,0,
    -5,-5,-5,0,0,0,0,0,
    -7,-7,-7,0,0,0,0,0,
    0,0,0,0,16,0,0,0,
    0,0,-3,0,0,0,-3,0};

    private static final int[] productionLabels = {0,1,1,2,2,3,3,0,4,4,5};

    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object type = _grx_stack.pop().payload;
            Object symbol = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = new TypeStatementNode(symbol.id, type.id); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            Object arg = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = arg; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object v;
            { v = null; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object c = _grx_stack.pop().payload;
            Object v;
            { v = c; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object v;
            { v = null; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object b = _grx_stack.pop().payload;
            Object arg = _grx_stack.pop().payload;
            Object s = _grx_stack.pop().payload;
            Object v;
            {
                if (StringUtils.isTerminal(s.id)) {
                    if (arg == null) {
                        b.addTerminal(s.id);
                    }
                    else {
                        b.addTerminal(s.id, arg.id);
                    }
                }
                else {
                    if (arg == null) {
                        b.addNonTerminal(s.id);
                    }
                    else {
                        b.addNonTerminal(s.id, arg.id);
                    }
                }
                v = b;
            }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object code = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = new ProductionStatementBody(code == null ? null : code.code); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object body = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object nonTerminal = _grx_stack.pop().payload;
            Object v;
            { v = new ProductionStatementNode(nonTerminal.id, body.getProduction(), body.getCode()); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object s = _grx_stack.pop().payload;
            Object t = _grx_stack.pop().payload;
            Object v;
            { t.addStatement(s); v = t; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object v;
            { v = new TranslationUnitNode(); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            return null;
        }
    }};

    private static final StackEntry initialStackEntry = new StackEntry(0, null);

    private Stack<StackEntry> stack;

    private Object payload = null;

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
                stack.push(new StackEntry(action, tokenPayload));
                return;
            }
            int productionIndex = -action - 2;
            Object reducedProduction = reductions[productionIndex].reduce(stack);
            StackEntry newState = stack.peek();
            int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabels[productionIndex]];
            stack.push(new StackEntry(nextState, reducedProduction));
        }
    }

    public boolean successfullyParsed() {
        return payload != null;
    }

    public Object getValue() {
        assert payload != null : "parsing did not succeed";
        return payload;
    }

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
}
