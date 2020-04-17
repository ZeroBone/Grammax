package net.zerobone.grammax.slr;

import java.util.Stack;

public class SLRParserAlgorithm {

    public static final int T_MUL = 0;
    public static final int T_NUM = 1;
    public static final int T_LPAREN = 2;
    public static final int T_RPAREN = 3;
    public static final int T_PLUS = 4;
    public static final int T_EOF = 5;
    private static final int terminalCount = 6;
    private static final int nonTerminalCount = 3;
    private static final int[] gotoTable = {
        4,6,3,
        0,0,0,
        0,0,0,
        0,0,0,
        4,8,3,
        0,0,0,
        0,0,10,
        0,0,0,
        12,0,3,
        0,0,0,
        0,0,0,
        0,0,0};
    private static final int[] actionTable = {
        0,2,5,0,0,0,
        -7,0,0,-7,-7,-7,
        -5,0,0,-5,-5,-5,
        7,0,0,-3,-3,-3,
        0,2,5,0,0,0,
        0,0,0,0,9,-1,
        0,2,5,0,0,0,
        0,0,0,11,9,0,
        0,2,5,0,0,0,
        -4,0,0,-4,-4,-4,
        -6,0,0,-6,-6,-6,
        7,0,0,-2,-2,-2};
    private static final int[] productionLabels = {1,1,0,0,2,2};
    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                Object term = _grx_stack.pop().payload;
                _grx_stack.pop();
                Object expr = _grx_stack.pop().payload;
                Object v;
                {
                    v = (int)expr + (int)term;
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                Object term = _grx_stack.pop().payload;
                Object v;
                {
                    v = term;
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                Object factor = _grx_stack.pop().payload;
                _grx_stack.pop();
                Object term = _grx_stack.pop().payload;
                Object v;
                {
                    v = (int)term * (int)factor;
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                Object factor = _grx_stack.pop().payload;
                Object v;
                {
                    v = factor;
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                _grx_stack.pop();
                Object expr = _grx_stack.pop().payload;
                _grx_stack.pop();
                Object v;
                {
                    v = expr;
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                Object n = _grx_stack.pop().payload;
                Object v;
                {
                    v = n;
                }
                return v;
            }
        }
    };

    private interface Reductor {
        Object reduce(Stack<StackEntry> grx_stack);
    }

    private static final class StackEntry {

        private final int previousState;

        private final Object payload;

        private StackEntry(int previousState, Object payload) {
            this.previousState = previousState;
            this.payload = payload;
        }

    }

    private Stack<StackEntry> stack = new Stack<>();

    private Object payload = null;

    public SLRParserAlgorithm() {
        stack.push(new StackEntry(0, null));
    }

    private static void debug_printAction(int action) {

        System.out.print("Action: ");

        if (action == 0) {
            System.out.println("error");
            return;
        }

        if (action == -1) {
            System.out.println("accept");
            return;
        }

        if (action > 0) {
            System.out.println("shift " + (action - 1));
            return;
        }

        int productionIndex = -action - 2;

        System.out.println("reduce " + productionIndex);

    }

    public void parse(int tokenId, Object tokenPayload) {

        while (true) {

            assert !stack.isEmpty();

            int state = stack.peek().previousState;

            int action = actionTable[terminalCount * state + tokenId];

            debug_printAction(action);

            if (action == 0) {
                throw new RuntimeException("Syntax error");
            }

            if (action == -1) {

                assert tokenId == T_EOF;

                assert stack.size() == 2;

                payload = stack.peek().payload;

                return;

            }

            if (action > 0) {
                // shift action

                stack.push(new StackEntry(action - 1, tokenPayload));

                return;

            }

            // reduce action

            int productionIndex = -action - 2;

            int productionLabel = productionLabels[productionIndex];

            // this will take as many symbols from the stack as there are in the production
            // and reduce the production with the rule attached to the production
            Object reducedProduction = reductions[productionIndex].reduce(stack);

            // the new state is what is now on top of the stack

            assert !stack.isEmpty();

            StackEntry newState = stack.peek();

            // compute the next state

            int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabel];

            // System.out.println("GOTO " + nextState);

            assert nextState != 0;

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

    public static void main(String[] args) {

        SLRParserAlgorithm parser = new SLRParserAlgorithm();

        parser.parse(SLRParserAlgorithm.T_LPAREN, "(");
        parser.parse(SLRParserAlgorithm.T_NUM, 5);
        parser.parse(SLRParserAlgorithm.T_PLUS, "+");
        parser.parse(SLRParserAlgorithm.T_NUM, 7);
        parser.parse(SLRParserAlgorithm.T_RPAREN, ")");
        parser.parse(SLRParserAlgorithm.T_MUL, "*");
        parser.parse(SLRParserAlgorithm.T_NUM, 2);
        parser.parse(SLRParserAlgorithm.T_EOF, "eof");

        if (parser.successfullyParsed()) {
            System.out.println("Done: success");
            System.out.println(parser.getValue());
        }
        else {
            System.err.println("Done: error while parsing");
        }

    }

}