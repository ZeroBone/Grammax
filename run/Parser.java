package net.zerobone.parser;

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

	private static final int[][] productions = {
	{0,-1,1,-2},
	{0,-2},
	{1,-2,2,-3},
	{1,-3},
	{2,3,-1,4},
	{2,5},
	{3,-1}};

	public Parser() {
		System.out.println(0xff);
	}
}
