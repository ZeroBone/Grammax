package net.zerobone.grammax.parser;

import net.zerobone.grammax.ast.TranslationUnitNode;
import net.zerobone.grammax.ast.entities.ProductionStatementBody;
import net.zerobone.grammax.ast.statements.ImportsStatementNode;
import net.zerobone.grammax.ast.statements.ProductionStatementNode;
import net.zerobone.grammax.ast.statements.StatementNode;
import net.zerobone.grammax.ast.statements.TypeStatementNode;
import net.zerobone.grammax.lexer.tokens.CodeToken;
import net.zerobone.grammax.lexer.tokens.IdToken;
import net.zerobone.grammax.utils.StringUtils;

import java.util.Stack;

public final class Parser {
    public static final int T_EOF = 0;

    public static final int T_ID = 1;

    public static final int T_ASSIGN = 2;

    public static final int T_SEMICOLON = 3;

    public static final int T_CODE = 4;

    public static final int T_LEFT_PAREN = 5;

    public static final int T_RIGHT_PAREN = 6;

    public static final int T_TYPE = 7;

    public static final int T_IMPORTS = 8;

    private static final int terminalCount = 9;

    private static final int nonTerminalCount = 6;

    private static final int[] gotoTable = {
        1,0,0,0,0,0,
        0,3,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,10,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,13,0,
        0,0,0,0,0,0,
        0,0,0,15,0,0,
        0,0,0,0,0,0,
        0,0,17,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0,
        0,0,0,0,0,0};

    private static final int[] actionTable = {
        -2,-2,0,0,0,0,0,-2,-2,
        -1,2,0,0,0,0,0,4,5,
        0,0,6,0,0,0,0,0,0,
        -3,-3,0,0,0,0,0,-3,-3,
        0,7,0,0,0,0,0,0,0,
        0,0,0,0,8,0,0,0,0,
        0,9,0,11,0,0,0,0,0,
        0,12,0,0,0,0,0,0,0,
        -12,-12,0,0,0,0,0,-12,-12,
        0,-9,0,-9,0,14,0,0,0,
        -4,-4,0,0,0,0,0,-4,-4,
        -7,-7,0,0,16,0,0,-7,-7,
        -11,-11,0,0,0,0,0,-11,-11,
        0,9,0,11,0,0,0,0,0,
        0,18,0,0,0,0,0,0,0,
        -5,-5,0,0,0,0,0,-5,-5,
        -8,-8,0,0,0,0,0,-8,-8,
        -6,-6,0,0,0,0,0,-6,-6,
        0,0,0,0,0,0,19,0,0,
        0,-10,0,-10,0,0,0,0,0};

    private static final int[] productionLabels = {0,0,1,2,2,3,3,4,4,1,1,5};

    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object v;
            { v = new TranslationUnitNode(); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            StatementNode s = (StatementNode)_grx_stack.pop().payload;
            TranslationUnitNode t = (TranslationUnitNode)_grx_stack.pop().payload;
            Object v;
            { t.addStatement(s); v = t; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            ProductionStatementBody body = (ProductionStatementBody)_grx_stack.pop().payload;
            _grx_stack.pop();
            IdToken nonTerminal = (IdToken)_grx_stack.pop().payload;
            Object v;
            { v = new ProductionStatementNode(nonTerminal.id, body.getProduction(), body.getCode()); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            CodeToken code = (CodeToken)_grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = new ProductionStatementBody(code == null ? null : code.code); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            ProductionStatementBody b = (ProductionStatementBody)_grx_stack.pop().payload;
            IdToken arg = (IdToken)_grx_stack.pop().payload;
            IdToken s = (IdToken)_grx_stack.pop().payload;
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
            Object v;
            { v = null; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            CodeToken c = (CodeToken)_grx_stack.pop().payload;
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
            _grx_stack.pop();
            IdToken arg = (IdToken)_grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = arg; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            IdToken type = (IdToken)_grx_stack.pop().payload;
            IdToken symbol = (IdToken)_grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = new TypeStatementNode(symbol.id, type.id); }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            CodeToken c = (CodeToken)_grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = new ImportsStatementNode(c.code); }
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