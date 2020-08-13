/* This file was generated by Grammax v1.0.0-beta */

package net.zerobone.grammax.examples.calculator.parser;

import net.zerobone.grammax.examples.calculator.tokens.NumberToken;

import java.util.Stack;

public final class CalcParser {
    public static final int T_DIV = 0;
    public static final int T_MUL = 1;
    public static final int T_NUM = 2;
    public static final int T_LPAREN = 3;
    public static final int T_RPAREN = 4;
    public static final int T_ID = 5;
    public static final int T_PLUS = 6;
    public static final int T_MINUS = 7;
    public static final int T_EOF = 8;
    private static final int terminalCount = 9;
    private static final int nonTerminalCount = 3;
    private static final int[] gotoTable = {
        4,5,2,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        15,16,13,
        0,0,0,
        0,0,0,
        0,0,19,
        0,0,20,
        21,0,2,
        22,0,2,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        15,29,13,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,30,
        0,0,31,
        0,0,0,
        32,0,13,
        33,0,13,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0,
        0,0,0};
    private static final int[] actionTable = {
        0,0,7,6,0,3,0,0,0,
        -7,-7,0,0,0,0,-7,-7,-7,
        0,0,8,0,0,0,0,0,0,
        10,9,0,0,0,0,-4,-4,-4,
        0,0,0,0,0,0,12,11,-1,
        0,0,18,17,0,14,0,0,0,
        -9,-9,0,0,0,0,-9,-9,-9,
        -10,-10,0,0,0,0,-10,-10,-10,
        0,0,7,6,0,3,0,0,0,
        0,0,7,6,0,3,0,0,0,
        0,0,7,6,0,3,0,0,0,
        0,0,7,6,0,3,0,0,0,
        -7,-7,0,0,-7,0,-7,-7,0,
        0,0,23,0,0,0,0,0,0,
        25,24,0,0,-4,0,-4,-4,0,
        0,0,0,0,26,0,28,27,0,
        0,0,18,17,0,14,0,0,0,
        -9,-9,0,0,-9,0,-9,-9,0,
        -5,-5,0,0,0,0,-5,-5,-5,
        -6,-6,0,0,0,0,-6,-6,-6,
        10,9,0,0,0,0,-3,-3,-3,
        10,9,0,0,0,0,-2,-2,-2,
        -10,-10,0,0,-10,0,-10,-10,0,
        0,0,18,17,0,14,0,0,0,
        0,0,18,17,0,14,0,0,0,
        -8,-8,0,0,0,0,-8,-8,-8,
        0,0,18,17,0,14,0,0,0,
        0,0,18,17,0,14,0,0,0,
        0,0,0,0,34,0,28,27,0,
        -5,-5,0,0,-5,0,-5,-5,0,
        -6,-6,0,0,-6,0,-6,-6,0,
        25,24,0,0,-3,0,-3,-3,0,
        25,24,0,0,-2,0,-2,-2,0,
        -8,-8,0,0,-8,0,-8,-8,0};
    private static final int[] productionLabels = {1,1,1,0,0,0,2,2,2};
    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                double term = (double)_grx_stack.pop().payload;
                _grx_stack.pop();
                double expr = (double)_grx_stack.pop().payload;
                Object v;
                {
                 v = expr + term; 
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                double term = (double)_grx_stack.pop().payload;
                _grx_stack.pop();
                double expr = (double)_grx_stack.pop().payload;
                Object v;
                {
                 v = expr - term; 
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                double term = (double)_grx_stack.pop().payload;
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
                double factor = (double)_grx_stack.pop().payload;
                _grx_stack.pop();
                double term = (double)_grx_stack.pop().payload;
                Object v;
                {
                 v = term * factor; 
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                double factor = (double)_grx_stack.pop().payload;
                _grx_stack.pop();
                double term = (double)_grx_stack.pop().payload;
                Object v;
                {
                 v = term / factor; 
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                double factor = (double)_grx_stack.pop().payload;
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
                double expr = (double)_grx_stack.pop().payload;
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
                NumberToken n = (NumberToken)_grx_stack.pop().payload;
                Object v;
                {
                 v = n.value; 
                }
                return v;
            }
        },
        new Reductor() {
            @Override
            public Object reduce(Stack<StackEntry> _grx_stack) {
                NumberToken n = (NumberToken)_grx_stack.pop().payload;
                _grx_stack.pop();
                Object v;
                {
                 v = n.value; 
                }
                return v;
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
    public CalcParser() {
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