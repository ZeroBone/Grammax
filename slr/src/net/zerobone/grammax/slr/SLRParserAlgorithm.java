package net.zerobone.grammax.slr;

import java.util.Arrays;
import java.util.Stack;

public class SLRParserAlgorithm {

    public static final int T_EOF = 0;

    public static final int T_PLUS = 1;

    public static final int T_MUL = 2;

    public static final int T_LPAREN = 3;

    public static final int T_RPAREN = 4;

    public static final int T_NUM = 5;

    private static final int terminalCount = 6;

    private static final int nonTerminalCount = 4;

    private static final int[] gotoTable = {
        1,2,3,0,
        0,0,0,0,
        0,0,0,0,
        0,0,0,0,
        8,2,3,0,
        0,0,0,0,
        0,9,3,0,
        0,0,10,0,
        0,0,0,0,
        0,0,0,0,
        0,0,0,0,
        0,0,0,0};

    private static final int[] actionTable = {
        0,0,0,4,0,5,
        -1,6,0,0,0,0,
        -3,-3,7,0,-3,0,
        -5,-5,-5,0,-5,0,
        0,0,0,4,0,5,
        -7,-7,-7,0,-7,0,
        0,0,0,4,0,5,
        0,0,0,4,0,5,
        0,6,0,0,11,0,
        -2,-2,7,0,-2,0,
        -4,-4,-4,0,-4,0,
        -6,-6,-6,0,-6,0};

    private static final int[] productionLabels = {0,0,1,1,2,2,3};

    private interface Reductor {
        Object reduce(Stack<StackEntry> grx_stack);
    }

    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            _grx_stack.pop();
            _grx_stack.pop();
            return null;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            return null;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            _grx_stack.pop();
            _grx_stack.pop();
            return null;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            return null;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            _grx_stack.pop();
            _grx_stack.pop();
            return null;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            return null;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            return null;
        }
    }};

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
            System.out.println("shift " + action);
            return;
        }

        int productionIndex = -action - 2;

        System.out.println("reduce " + productionIndex);

    }

    public void parse(int token, Object tokenPayload) {

        while (true) {

            assert !stack.isEmpty();

            int state = stack.peek().previousState;

            int action = actionTable[terminalCount * state + token];

            debug_printAction(action);

            if (action == 0) {
                throw new RuntimeException("Syntax error");
            }

            if (action == -1) {

                if (token != T_EOF) {
                    throw new RuntimeException("Expected end of input, got " + token);
                }

                assert stack.size() == 2;

                payload = stack.peek().payload;

                System.out.println("Parsing succeeded: string accepted");

                return;

            }

            if (action > 0) {
                // shift action

                stack.push(new StackEntry(action, tokenPayload));

                return;

            }

            // reduce action

            int productionIndex = -action - 2;

            int productionLabel = productionLabels[productionIndex];

            // this will take as many symbols from the stack as there are in the production
            // and reduce the production with the rule attached to the production
            reductions[productionIndex].reduce(stack);

            // the new state is what is now on top of the stack

            assert !stack.isEmpty();

            StackEntry newState = stack.peek();

            // compute the next state

            int nextState = gotoTable[newState.previousState * nonTerminalCount + productionLabel];

            System.out.println("GOTO " + nextState);

            assert nextState != 0;

            stack.push(new StackEntry(nextState, null));

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

        parser.parse(SLRParserAlgorithm.T_NUM, "2");
        parser.parse(SLRParserAlgorithm.T_MUL, "*");
        // parser.parse(SLRParser.T_LPAREN, "(");
        parser.parse(SLRParserAlgorithm.T_NUM, "5");
        parser.parse(SLRParserAlgorithm.T_PLUS, "+");
        parser.parse(SLRParserAlgorithm.T_NUM, "7");
        // parser.parse(SLRParser.T_RPAREN, ")");
        parser.parse(SLRParserAlgorithm.T_EOF, "eof");

        System.out.println("done");
    }

}