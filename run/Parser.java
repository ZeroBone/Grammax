package net.zerobone.parser;

import java.lang.Object;
import java.lang.Override;
import java.util.Stack;

public final class Parser {
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

	private static final Reductor[] reductions = {new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object term = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object expr = _grx_stack.pop().payload;
            Object v;
            { v = v = (int)expr + (int)term; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object term = _grx_stack.pop().payload;
            Object v;
            { v = term; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object factor = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object term = _grx_stack.pop().payload;
            Object v;
            { v = (int)term * (int)factor; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object factor = _grx_stack.pop().payload;
            Object v;
            { v = factor; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            Object expr = _grx_stack.pop().payload;
            _grx_stack.pop();
            Object v;
            { v = expr; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            Object n = _grx_stack.pop().payload;
            Object v;
            { v = n; }
            return v;
        }
    },new Reductor() {
        @Override
        public Object reduce(Stack<StackEntry> _grx_stack) {
            _grx_stack.pop();
            return null;
        }
    }};

	public Parser() {
		System.out.println(0xff);
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
		Object reduce();
	}
}
