# Grammax
[![Build Status](https://travis-ci.org/ZeroBone/Grammax.svg?branch=master)](https://travis-ci.org/ZeroBone/Grammax)

Grammax takes grammar in BNF format as an input and converts it to a Java-class that recognizes the language generated by the grammar. Formally speaking, this tool creates a left-to-right, rightmost derivation (LR) parser for a given grammar. That means that grammax parses the given string by constructing a reversed rightmost derivation of it.

This tool doesn't require any external libraries or dependencies. All generation is done ahead-of-time. After generating the parsing classes you can just copy them into your project.

Also, as other good parser generation tools, [grammax uses itself](https://github.com/ZeroBone/Grammax/blob/master/run/grammax.grx) to read the input grammar.

## Features

* No runtime dependencies, only pure Java code is generated.
* Parsing is done using a push-down automation without recursion.
* Grammax uses an explicit API for accepting the token stream. It allows you to easily use the tool with any (including your own) lexer. You can pause and resume parsing at any point. Parsing multiple token streams simultaneously is also possible.
* Grammax supports simple lr and canonical lr parsing algorithms.
* Automatic warnings about possible right-recursion cycles that cause a lot of parsing stack memory consumption.
* Types can be assigned to terminals and non-terminals. The corresponding expressions are casted automatically.
* The `%top` statement allows inserting `package` and `import` automatically. Therefore grammax can be used in an automated pipeline.

## Getting Started

1. Download the latest `.jar` file from the [releases](https://github.com/ZeroBone/Grammax/releases) page.
2. Create a `.grx` file with your grammar. You can find an example below.
3. Run `java -jar grammax.jar grammar.grx` where `grammar.grx` is your grammar file.
4. Grammax will generate the parsing class in the same directory.

## Grammar file syntax

Knife accepts grammar files in BNF (Backus-Naur-Form) format with productions in the following syntax:

### Terminals and non-terminals

```
non_terminal = TERMINAL non_terminal TERMINAL_WITH_ARG(argument); { java code }
```

The way grammax distinguished between terminals and non-terminals is by looking at the first character. If it is a capital letter, the identifier identifies a terminal symbol, otherwise a non-terminal.

The first non-terminal declared in the grammar file will be the starting symbol of the grammar. Further ordering doesn't matter. It is important, that all declared non-terminals are being used in some production and that they are derivable from the start symbol.

### Arguments

An argument can be attached to any grammar symbol (terminal or non-terminal) if you need to access it's payload in the code attached to the production.

The production label cannot have an argument. In order to assign a value to it, assign it to `v`. For example:

```
if_stmt = IF LPAREN expr(e) RPAREN stmt(ifbody) ELSE stmt(elsebody); {
	v = new IfStatement(e, ifbody, elseBody);
}
```

### Type statement

By default the type associated to all grammar symbols is `java.lang.Object`. In order to avoid a lot of unsafe type castings in the production code blocks you can use following syntax to assign a type to a symbol:

```
%type if_stmt IfStatement
```

Everywhere where the typed symbol will be used, the corresponding argument name will have the specified type.

### Top statement

With the top statement you can specify code that should be inserted at the top of the file. It is intended to be used for specifying package and import statements, for example:

```
%top {
    package net.zerobone.grammax.parser;

    import net.zerobone.grammax.ast.*;
    import net.zerobone.grammax.lexer.*;
    import net.zerobone.grammax.utils.StringUtils;
}
```

### Name statement

With the name statement it is possible to specify the class name for the generated parser:

```
%name GrxParser
```

tells grammax to write the parser code in `GrxParser.java`.

### Algo statement

By default the `SLR` algorithm is used to generate the parser. You can explicitly specify the algorithm you want to use with this statement:

```
%algo SLR
```

Available algorithms: `SLR`, `CLR`.

## Example

In this example we will build a complete working calculator with grammax. You can find the full source code as well as an executable of the example [here](https://github.com/ZeroBone/Grammax/tree/master/examples/calculator).

Our calculator will accept any valid arithmetic expressions with `+`, `-`, `*`, `/`. In order to handle precedence we will denote expressions with multiplicative operands with the `t` non-terminal. Additive expressions will be derivable from the `e` non-terminal which will also be the starting symbol of the grammar. Expressions in parentheses and atomic expressions we will call factor and denote with `f`. We can write the grammar as:

```
// expression
e = e PLUS t;
e = e MINUS t;
e = t;
// term
t = t MUL f;
t = t DIV f;
t = f;
// factor
f = LPAREN e RPAREN;
f = NUM;
f = ID NUM;
```

However, we would like to execute some code when some handle is reduced with a production. In order to do that we can attach code to every production after the semicolon. To reference already parsed grammar symbols from this code block we need to denote them with arguments in parentheses.

```
e = e(expr) PLUS t(term); { v = expr + term; }
e = e(expr) MINUS t(term); { v = expr - term; }
e = t(term); { v = term; }
t = t(term) MUL f(factor); { v = term * factor; }
t = t(term) DIV f(factor); { v = term / factor; }
t = f(factor); { v = factor; }
f = LPAREN e(expr) RPAREN; { v = expr; }
f = NUM(n); { v = n.value; }
f = ID NUM(n); { v = n.value; }
```

By default, the type of all grammar symbols in `java.lang.Object`. Arithmetic operations with this type is a sematic error, so we need to let grammax know the type we want to use explicitly:

```
%type e double
%type t double
%type f double

%type NUM NumberToken

e = e(expr) PLUS t(term); { v = expr + term; }
e = e(expr) MINUS t(term); { v = expr - term; }
e = t(term); { v = term; }
t = t(term) MUL f(factor); { v = term * factor; }
t = t(term) DIV f(factor); { v = term / factor; }
t = f(factor); { v = factor; }
f = LPAREN e(expr) RPAREN; { v = expr; }
f = NUM(n); { v = n.value; }
f = ID NUM(n); { v = n.value; }
```

Grammax will create a working parser based on this grammar.

## Support

Don't hesitate to ask via [issues](https://github.com/ZeroBone/Grammax/issues)

Please [open an issue](https://github.com/ZeroBone/Grammax/issues) if you found a bug in Grammax.

Any contributions are **greatly appreciated**.

### TODO

These are the major features that aren't implemented yet:

* A system for resolving conflicts in ambiguous grammars.
* `LALR` parsing algorithm.
* Support for the `c/c++` programming language.

## Copyright

Copyright (c) 2020 Alexander Mayorov.

This software is licensed under the terms of the GNU General Public License, Version 3.

See the LICENSE file for more details.