package net.zerobone.grammax.slr;

import java.util.Arrays;
import java.util.Stack;

public class SLRParser {

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

    private static final int[][] productions = {
        {-1,1,-2},
        {-2},
        {-2,2,-3},
        {-3},
        {3,-1,4},
        {5},
        {-1}};

    private Stack<Integer> stack = new Stack<>();

    private static void debug_printAction(int action) {

        System.out.print("Action: ");

        if (action == 0) {
            System.out.println("error");
            return;
        }

        if (action > 0) {
            System.out.println("shift " + action);
            return;
        }

        int productionIndex = -action - 2;

        int[] production = productions[productionIndex];

        System.out.println("reduce " + productionIndex + " ( production = " + Arrays.toString(production) + " )");

    }

    public void parse(int token, String value) {

        int state = stack.isEmpty() ? 0 : stack.peek();

        int action = actionTable[terminalCount * state + token];

        debug_printAction(action);

        if (action == 0) {
            throw new RuntimeException("Action = 0");
        }

        if (action > 0) {
            // shift action

            stack.push(action);

            return;

        }

        // reduce action

        int productionIndex = -action - 2;

        int[] production = productions[productionIndex];

        // take as many symbols from the stack as there are in the production

        assert stack.size() >= production.length;

        for (int i = 0; i < production.length; i++) {
            stack.pop();
        }

        int newState = stack.isEmpty() ? 0 : stack.peek();

        // compute the next state

        int nextState = gotoTable[newState * nonTerminalCount];

    }

    public static void main(String[] args) {

        SLRParser parser = new SLRParser();

        parser.parse(SLRParser.T_NUM, "2");
        parser.parse(SLRParser.T_MUL, "*");
        parser.parse(SLRParser.T_NUM, "5");
        parser.parse(SLRParser.T_PLUS, "+");
        parser.parse(SLRParser.T_NUM, "7");
        parser.parse(SLRParser.T_EOF, "eof");

        System.out.println("done");
    }

}