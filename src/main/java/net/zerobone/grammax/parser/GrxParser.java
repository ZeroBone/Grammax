/* This file was generated by Grammax v1.1.0 */


package net.zerobone.grammax.parser;

import net.zerobone.grammax.ast.TranslationUnitNode;
import net.zerobone.grammax.ast.entities.ProductionStatementBody;
import net.zerobone.grammax.ast.statements.*;
import net.zerobone.grammax.lexer.tokens.CodeToken;
import net.zerobone.grammax.lexer.tokens.IdToken;
import net.zerobone.grammax.utils.StringUtils;

import java.util.Stack;

public final class GrxParser {
    public static final int T_TARGET = 0;
    public static final int T_CODE = 1;
    public static final int T_TOP = 2;
    public static final int T_SEMICOLON = 3;
    public static final int T_RIGHT_PAREN = 4;
    public static final int T_ID = 5;
    public static final int T_ASSIGN = 6;
    public static final int T_LEFT_PAREN = 7;
    public static final int T_TYPE = 8;
    public static final int T_ALGO = 9;
    public static final int T_NAME = 10;
    public static final int T_EOF = 11;
    private static final int terminalCount = 12;
    private static final int nonTerminalCount = 5;
    private static final int[] gotoTable = {
            2,0,0,0,0,
            0,0,0,8,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,18,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,21,0,0,0,
            0,0,24,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0};
    private static final int[] actionTable = {
            -2,0,-2,0,0,-2,0,0,-2,-2,-2,-2,
            3,0,4,0,0,9,0,0,6,7,5,-1,
            0,0,0,0,0,10,0,0,0,0,0,0,
            0,11,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,12,0,0,0,0,0,0,
            0,0,0,0,0,13,0,0,0,0,0,0,
            0,0,0,0,0,14,0,0,0,0,0,0,
            -3,0,-3,0,0,-3,0,0,-3,-3,-3,-3,
            0,0,0,0,0,0,15,0,0,0,0,0,
            -16,0,-16,0,0,-16,0,0,-16,-16,-16,-16,
            -13,0,-13,0,0,-13,0,0,-13,-13,-13,-13,
            -14,0,-14,0,0,-14,0,0,-14,-14,-14,-14,
            0,16,0,0,0,17,0,0,0,0,0,0,
            -15,0,-15,0,0,-15,0,0,-15,-15,-15,-15,
            0,0,0,-5,0,-5,0,0,0,0,0,0,
            -12,0,-12,0,0,-12,0,0,-12,-12,-12,-12,
            -11,0,-11,0,0,-11,0,0,-11,-11,-11,-11,
            0,0,0,19,0,20,0,0,0,0,0,0,
            -7,22,-7,0,0,-7,0,0,-7,-7,-7,-7,
            0,0,0,-9,0,-9,0,23,0,0,0,0,
            -4,0,-4,0,0,-4,0,0,-4,-4,-4,-4,
            -8,0,-8,0,0,-8,0,0,-8,-8,-8,-8,
            0,0,0,0,0,25,0,0,0,0,0,0,
            0,0,0,-6,0,-6,0,0,0,0,0,0,
            0,0,0,0,26,0,0,0,0,0,0,0,
            0,0,0,-10,0,-10,0,0,0,0,0,0};
    private static final int[] productionLabels = {0,0,3,4,4,1,1,2,2,3,3,3,3,3,3};
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
    @SuppressWarnings("Convert2Lambda")
    private static final Reductor[] reductions = {
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    Object v;
                    {
                        v = new TranslationUnitNode();
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    StatementNode s = (StatementNode)_grx_stack.pop().payload;
                    TranslationUnitNode t = (TranslationUnitNode)_grx_stack.pop().payload;
                    Object v;
                    {
                        t.addStatement(s); v = t;
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    CodeToken code = (CodeToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    ProductionStatementBody body = (ProductionStatementBody)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    IdToken nonTerminal = (IdToken)_grx_stack.pop().payload;
                    Object v;
                    {
                        v = new ProductionStatementNode(nonTerminal.id, body.getProduction(), code);
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    Object v;
                    {
                        v = new ProductionStatementBody();
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    IdToken arg = (IdToken)_grx_stack.pop().payload;
                    IdToken s = (IdToken)_grx_stack.pop().payload;
                    ProductionStatementBody b = (ProductionStatementBody)_grx_stack.pop().payload;
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
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    Object v;
                    {
                        v = null;
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    CodeToken c = (CodeToken)_grx_stack.pop().payload;
                    Object v;
                    {
                        v = c;
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    Object v;
                    {
                        v = null;
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    _grx_stack.pop();
                    IdToken arg = (IdToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = arg;
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    IdToken type = (IdToken)_grx_stack.pop().payload;
                    IdToken symbol = (IdToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = new TypeStatementNode(symbol.id, type.id);
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    CodeToken c = (CodeToken)_grx_stack.pop().payload;
                    IdToken symbol = (IdToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = new TypeStatementNode(symbol.id, c.code);
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    CodeToken c = (CodeToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = new TopStatementNode(c.code);
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    IdToken name = (IdToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = new NameStatementNode(name.id);
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    IdToken alg = (IdToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = new AlgoStatementNode(alg.id);
                    }
                    return v;
                }
            },
            new Reductor() {
                @Override
                public Object reduce(Stack<StackEntry> _grx_stack) {
                    IdToken target = (IdToken)_grx_stack.pop().payload;
                    _grx_stack.pop();
                    Object v;
                    {
                        v = new TargetStatementNode(target.id);
                    }
                    return v;
                }
            }
    };
    private static final StackEntry initialStackEntry = new StackEntry(0, null);
    private Stack<StackEntry> stack;
    private Object payload = null;
    public GrxParser() {
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