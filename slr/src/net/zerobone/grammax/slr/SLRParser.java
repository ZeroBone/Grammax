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
        {0,-1,1,-2},
        {0,-2},
        {1,-2,2,-3},
        {1,-3},
        {2,3,-1,4},
        {2,5},
        {3,-1}};

    private Stack<Integer> stack = new Stack<>();

    public SLRParser() {
        stack.push(0);
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

        int[] production = productions[productionIndex];

        System.out.println("reduce " + productionIndex + " ( production = " + Arrays.toString(production) + " )");

    }

    public void parse(int token, String value) {

        assert !stack.isEmpty();

        int state = stack.peek();

        int action = actionTable[terminalCount * state + token];

        debug_printAction(action);

        if (action == 0) {
            throw new RuntimeException("Action = 0");
        }

        if (action == -1) {
            System.out.println("Parsing succeeded: string accepted");
            return;
        }

        if (action > 0) {
            // shift action

            stack.push(action);

            return;

        }

        // reduce action

        int productionIndex = -action - 2;

        int[] production = productions[productionIndex];

        int productionLabel = production[0];

        assert productionLabel >= 0;

        // take as many symbols from the stack as there are in the production

        assert stack.size() >= production.length - 1;

        for (int i = 1; i < production.length; i++) {
            stack.pop();
        }

        // the new state is what is now on top of the stack

        assert !stack.isEmpty();

        int newState = stack.peek();

        // compute the next state

        int nextState = gotoTable[newState * nonTerminalCount + productionLabel];

        System.out.println("GOTO " + nextState);

        stack.push(nextState);

        parse(token, value);

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