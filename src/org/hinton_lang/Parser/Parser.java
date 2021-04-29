package org.hinton_lang.Parser;

import java.util.Arrays;

import static org.hinton_lang.Scanner.TokenType.*;

import java.util.ArrayList;
import java.util.List;

import org.hinton_lang.Hinton;
import org.hinton_lang.Errors.SyntaxError;
import org.hinton_lang.Interpreter.HintonBoolean.HintonBoolean;
import org.hinton_lang.Interpreter.HintonFloat.HintonFloat;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Interpreter.HintonNull.HintonNull;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        // System.out.println(tokens);
    }

    /**
     * Parses the provided list of tokens to generate am Abstract Syntax Tree (AST)
     * 
     * @return An AST representation of the source code.
     */
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        try {
            while (!isAtEnd()) {
                statements.addAll(declaration());
            }
        } catch (SyntaxError error) {
            Hinton.parserError(error);
        }

        return statements;
    }

    /**
     * Checks if the current token matches any of the provided tokens and consumes
     * it.
     * 
     * @param types The tokens to be matched against the current token
     * @return True if the current token matches any of the provided tokens, false
     *         otherwise.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Consume the current token, making sure that the current token is the token we
     * expected to consume.
     * 
     * @param type    The token we expect to consume
     * @param message A message displayed in the case that the current token is not
     *                the type we expected to consume
     * @return The consumed token
     */
    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw new SyntaxError(peek(), message);
    }

    /**
     * Checks that the current token matches the provided type without consuming the
     * current token.
     * 
     * @param type The type we expect to see
     * @return True if the current token matches the provided type, false otherwise.
     */
    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    /**
     * Advances the token pointer to the next token.
     * 
     * @return The previous token (before advancing).
     */
    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    /**
     * Checks whether we are currently reading the last token or not.
     * 
     * @return True if we are at the last token, false otherwise
     */
    private boolean isAtEnd() {
        return peek().type == END_OF_FILE;
    }

    /**
     * Gets the current token from the list without consuming it.
     * 
     * @return The current token.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Gets the previous token from the list without consuming it.
     * 
     * @return The previous token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Matches a statement as specified in the grammar.cfg file.
     * 
     * @return A statement.
     */
    private Stmt statement() {
        if (match(L_CURLY_BRACES))
            return new Stmt.Block(block());
        if (match(IF_KEYWORD))
            return ifStatement();
        if (match(WHILE_KEYWORD))
            return whileStatement();
        if (match(FOR_KEYWORD))
            return forStatement();
        if (match(BREAK_KEYWORD))
            return breakStatement();
        if (match(CONTINUE_KEYWORD))
            return continueStatement();
        if (match(RETURN_KEYWORD))
            return returnStatement();
        // if (match(IMPORT_KEYWORD))
        // return importStatement();

        return expressionStatement();
    }

    /**
     * Matches a for-statement as specified in the grammar.cfg file.
     * 
     * @return A for-statement.
     */
    private Stmt forStatement() {
        consume(L_PARENTHESIS, "Expect '(' after 'for'.");

        // Gets the loop's initializer
        Stmt initializer;
        if (match(SEMICOLON_SEPARATOR)) {
            initializer = null;
        } else if (match(VAR_KEYWORD)) {
            ArrayList<Stmt> varDcl = varDeclaration();

            if (varDcl.size() > 1) {
                throw new SyntaxError(((Stmt.Var) varDcl.get(1)).name, "Expected a single-variable initializer.");
            } else {
                initializer = varDcl.get(0);
            }
        } else {
            initializer = expressionStatement();
        }

        // Gets the condition for the loop
        Expr condition = null;
        if (!check(SEMICOLON_SEPARATOR)) {
            condition = expression();
        }
        consume(SEMICOLON_SEPARATOR, "Expect ';' after loop condition.");

        // Gets the increment statement for the loop
        Expr increment = null;
        if (!check(R_PARENTHESIS)) {
            increment = expression();
        }
        consume(R_PARENTHESIS, "Expect ')' after for clauses.");

        // Gets the loops body
        Stmt body = statement();

        // **** The following code converts the for-loop into a while-loop. ****

        // Adds the increment to the end of the loop's body
        if (increment != null) {
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }

        // Creates a while loop with the condition and the body
        if (condition == null) {
            condition = new Expr.Literal(new HintonBoolean(true));
        }
        body = new Stmt.While(condition, body);

        // Creates a block with the initializer, followed by the while loop
        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        // Returns a while-loop version of the for-loop.
        // This is know an "syntactic sugar" in CS.
        return body;
    }

    /**
     * Matches a break statement as specified in the grammar.cfg file.
     * 
     * @return A break statement.
     */
    private Stmt breakStatement() {
        Token keyword = previous();
        match(SEMICOLON_SEPARATOR); // Optional semicolon
        return new Stmt.Break(keyword);
    }

    /**
     * Matches a continue statement as specified in the grammar.cfg file.
     * 
     * @return A continue statement.
     */
    private Stmt continueStatement() {
        Token keyword = previous();
        match(SEMICOLON_SEPARATOR); // Optional semicolon
        return new Stmt.Continue(keyword);
    }

    /**
     * Matches a return statement as specified in the grammar.cfg file.
     * 
     * @return A return statement.
     */
    private Stmt returnStatement() {
        Token keyword = previous();

        // If there wasn't a semicolon, then we expect an expression
        if (!match(SEMICOLON_SEPARATOR)) {
            Expr value = expression();
            match(SEMICOLON_SEPARATOR); // Optional semicolon
            return new Stmt.Return(keyword, value);
        }

        // If there wasn't an expression after the return statement,
        // then the function's body returns null.
        return new Stmt.Return(keyword, new Expr.Literal(new HintonNull()));
    }

    // /**
    // * Matches an import statement as specified in the grammar.cfg file.
    // *
    // * @return An import statement.
    // */
    // private Stmt importStatement() {
    // // TODO: This is buggy. Finish the implementation to work as expected.
    // consume(STRING_LITERAL, "Expected model path after import statement.");
    // String path = (String) previous().literal;
    // match(SEMICOLON_SEPARATOR); // Optional semicolon

    // try {
    // byte[] bytes = Files.readAllBytes(Paths.get(path));
    // String sourceCode = new String(bytes, Charset.defaultCharset());

    // Lexer lexer = new Lexer(sourceCode);
    // List<Token> tokens = lexer.lexTokens();

    // Parser parser = new Parser(tokens);
    // List<Stmt> statements = parser.parse();

    // return new Stmt.Import(statements);
    // } catch (IOException e) {
    // throw new SyntaxError(previous(), "Cannot find module " + path);
    // }
    // }

    /**
     * Matches an if-statement as specified in the grammar.cfg file.
     * 
     * @return An if-statement.
     */
    private Stmt ifStatement() {
        consume(L_PARENTHESIS, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(R_PARENTHESIS, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE_KEYWORD)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    /**
     * Matches a while-statement as specified in the grammar.cfg file.
     * 
     * @return An while-statement.
     */
    private Stmt whileStatement() {
        consume(L_PARENTHESIS, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(R_PARENTHESIS, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    /**
     * Matches a variable declaration as specified in the grammar.cfg file.
     * 
     * @return A variable declaration.
     */
    private ArrayList<Stmt> varDeclaration() {
        ArrayList<Token> declarations = new ArrayList<>();

        // Gets at least one variable name, or a list of
        // names separated by a comma
        declarations.add(consume(IDENTIFIER, "Expect variable name."));
        while (match(COMMA_SEPARATOR)) {
            declarations.add(consume(IDENTIFIER, "Expect variable name."));
        }

        // Since the .forEach loop bellow requires the
        // variables to be final, we use an array of size
        // one to represent the value of the variable.
        Expr initializer = null;
        if (match(EQUALS_SIGN)) {
            initializer = expression();
        }

        // Requires a semicolon at the end of the declaration
        // if the declaration was not a block
        if (previous().type != TokenType.R_CURLY_BRACES)
            consume(SEMICOLON_SEPARATOR, "Expect ';' after variable declaration.");

        // But if there is a semicolon after a curly brace, then we consume it
        if (previous().type == TokenType.R_CURLY_BRACES && check(SEMICOLON_SEPARATOR))
            advance();

        // Holds the declaration statements
        ArrayList<Stmt> statements = new ArrayList<>();

        // Assigns the value to the names.
        for (Token name : declarations) {
            statements.add(new Stmt.Var(name, initializer));
        }

        return statements;
    }

    /**
     * Matches a variable declaration as specified in the grammar.cfg file.
     * 
     * @return A variable declaration.
     */
    private ArrayList<Stmt> constDeclaration() {
        ArrayList<Token> declarations = new ArrayList<>();

        // Gets at least one constant name, or a list of
        // names separated by a comma
        declarations.add(consume(IDENTIFIER, "Expect constant name."));
        while (match(COMMA_SEPARATOR)) {
            declarations.add(consume(IDENTIFIER, "Expect constant name."));
        }

        // Gets the value
        match(EQUALS_SIGN);
        Expr initializer = expression();

        // Requires a semicolon at the end of the declaration
        // if the declaration was not a block
        if (previous().type != TokenType.R_CURLY_BRACES)
            consume(SEMICOLON_SEPARATOR, "Expect ';' after constant declaration.");

        // But if there is a semicolon after a curly brace, then we consume it
        if (check(SEMICOLON_SEPARATOR))
            advance();

        // Holds the declaration statements
        ArrayList<Stmt> statements = new ArrayList<>();

        // Assigns the value to the names.
        for (Token name : declarations) {
            statements.add(new Stmt.Const(name, initializer));
        }

        return statements;
    }

    /**
     * Matches an enum declaration as specified in the grammar.cfg file.
     * 
     * @return An enum declaration.
     */
    private Stmt enumDeclaration() {

        // Gets at least one variable name, or a list of
        // names separated by a comma
        Token name = consume(IDENTIFIER, "Expected enum name.");
        consume(L_CURLY_BRACES, "Expected '{' after enum name");

        ArrayList<Stmt.EnumMember> members = new ArrayList<>();

        while (!match(R_CURLY_BRACES) && !isAtEnd()) {
            int idx = 0;
            do {
                Token memberName = consume(IDENTIFIER, "Expected enum member.");
                members.add(new Stmt.EnumMember(memberName, new HintonInteger(idx)));
                idx += 1;
            } while (match(COMMA_SEPARATOR));
        }

        return new Stmt.Enum(name, members);
    }

    /**
     * Matches an expression statement as specified in the grammar.cfg file.
     * 
     * @return An expression statement.
     */
    private Stmt expressionStatement() {
        Expr expr = expression();
        match(SEMICOLON_SEPARATOR); // Optional semicolon
        return new Stmt.Expression(expr);
    }

    /**
     * Matches a block statement as specified in the grammar.cfg file.
     * 
     * @return A block statement.
     */
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(R_CURLY_BRACES) && !isAtEnd()) {
            statements.addAll(declaration());
        }

        consume(R_CURLY_BRACES, "Expect '}' after block.");
        return statements;
    }

    /**
     * Matches an assignment statement as specified in the grammar.cfg file.
     * 
     * @return An assignment statement.
     */
    private Expr assignment() {
        Expr expr = logicOr();

        if (match(EQUALS_SIGN)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.MemberAccess) {
                Expr.MemberAccess get = (Expr.MemberAccess) expr;
                return new Expr.MemberSetter(get.object, get.name, value);
            } else if (expr instanceof Expr.Indexing) {
                Expr.Indexing setter = (Expr.Indexing) expr;
                return new Expr.ArrayItemSetter(peek(), setter, value);
            }

            throw new SyntaxError(equals, "Invalid assignment target.");
        }

        return expr;
    }

    /**
     * Matches an expression as specified in the grammar.cfg file.
     * 
     * @return An expression.
     */
    private Expr expression() {
        return assignment();
    }

    /**
     * Matches a declaration statement as specified in the grammar.cfg file.
     * 
     * @return A declaration expression
     */
    private ArrayList<Stmt> declaration() {
        ArrayList<Stmt> statements = new ArrayList<>();

        if (match(VAR_KEYWORD)) {
            statements = varDeclaration();
        } else if (match(CONST_KEYWORD)) {
            statements = constDeclaration();
        } else if (match(FUNC_KEYWORD)) {
            statements.add(function());
        } else if (match(ENUM_KEYWORD)) {
            statements.add(enumDeclaration());
        } else {
            statements.add(statement());
        }

        return statements;
    }

    /**
     * Matches a function statement as specified in the grammar.cfg file.
     * 
     * @return A function declaration statement.
     */
    private Stmt.Function function() {
        Token name = consume(IDENTIFIER, "Expect " + "function" + " name.");

        consume(L_PARENTHESIS, "Expect '(' after " + "function" + " name.");
        List<Stmt.Parameter> parameters = new ArrayList<>();
        if (!check(R_PARENTHESIS)) {
            do {
                if (parameters.size() >= 255) {
                    throw new SyntaxError(peek(), "Can't have more than 255 parameters.");
                }

                if (check(R_PARENTHESIS)) {
                    // We assume there are no more parameters if there
                    // was a comma after the last parameter, but there
                    // wasn't a parameter after the comma.
                    break;
                } else {
                    // Gets the next parameter
                    Stmt.Parameter param = parameter();

                    // Checks that optional parameters are declared at the
                    // end of the function definition
                    if (parameters.size() > 0 && !param.isOptnl && parameters.get(parameters.size() - 1).isOptnl) {
                        throw new SyntaxError(parameters.get(parameters.size() - 1).name,
                                "Optional and named parameters must be declared after all required parameters.");
                    }

                    // If everything is good, we add it to the param list.
                    parameters.add(param);
                }

            } while (match(COMMA_SEPARATOR));
        }
        consume(R_PARENTHESIS, "Expect ')' after parameters.");

        consume(L_CURLY_BRACES, "Expect '{' before " + "function" + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    /**
     * Matches a parameter declaration as specified in the grammar.cfg file.
     * 
     * @return A parameter declaration.
     */
    public Stmt.Parameter parameter() {
        Token id = consume(IDENTIFIER, "Expected a parameter definition.");

        if (match(QUESTION_MARK)) {
            return new Stmt.Parameter(id, true, new Expr.Literal(new HintonNull()));
        } else if (match(EQUALS_SIGN)) {
            Expr right = expression();
            return new Stmt.Parameter(id, true, right);
        } else {
            return new Stmt.Parameter(id, false, new Expr.Literal(new HintonNull()));
        }
    }

    /**
     * Matches an equality expression as specified in the grammar.cfg file.
     * 
     * @return An equality expression.
     */
    private Expr equality() {
        Expr expr = comparison();

        while (match(LOGICAL_NOT_EQ, LOGICAL_EQ)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Matches a logical "OR" expression as specified in the grammar.cfg file.
     * 
     * @return A logical "OR" expression
     */
    private Expr logicOr() {
        Expr term = logicAnd();

        while (match(LOGICAL_OR)) {
            Token operator = previous();
            Expr right = logicAnd();
            term = new Expr.Logical(term, operator, right);
        }

        return term;
    }

    /**
     * Matches a logical "AND" expression as specified in the grammar.cfg file.
     * 
     * @return A logical "AND" expression
     */
    private Expr logicAnd() {
        Expr term = equality();

        while (match(LOGICAL_AND)) {
            Token operator = previous();
            Expr right = equality();
            term = new Expr.Logical(term, operator, right);
        }

        return term;
    }

    /**
     * Matches a comparison expression as specified in the grammar.cfg file.
     * 
     * @return A comparison expression.
     */
    private Expr comparison() {
        Expr range = range();

        while (match(LESS_THAN, LESS_THAN_EQ, GREATER_THAN, GREATER_THAN_EQ)) {
            Token operator = previous();
            Expr right = range();
            range = new Expr.Binary(range, operator, right);
        }

        return range;
    }

    /**
     * Matches a range expression as specified in the grammar.cfg file.
     * 
     * @return A range expression.
     */
    private Expr range() {
        Expr term = term();

        if (match(RANGE_OPERATOR)) {
            Token operator = previous();
            Expr right = term();
            term = new Expr.Binary(term, operator, right);
        }

        return term;
    }

    /**
     * Matches a term expression as specified in the grammar.cfg file.
     * 
     * @return A term expression.
     */
    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Matches a factor expression as specified in the grammar.cfg file.
     * 
     * @return A factor expression.
     */
    private Expr factor() {
        Expr expr = expo();

        while (match(SLASH, STAR, MODULUS)) {
            Token operator = previous();
            Expr right = expo();
            return new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Matches a exponentiation expression as specified in the grammar.cfg file.
     * 
     * @return A exponentiation expression.
     */
    private Expr expo() {
        Expr expr = unary();

        while (match(EXPO)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Matches a unary expression as specified in the grammar.cfg file.
     * 
     * @return A unary expression.
     */
    private Expr unary() {
        if (match(LOGICAL_NOT, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        } else if (match(FN_LAMBDA_KEYWORD)) {
            return lambda();
        } else if (match(INCREMENT, DECREMENT)) {
            return new Expr.DeIn_crement(previous(), unary(), true);
        } else {
            Expr expr = primary();

            while (match(L_SQUARE_BRACKET, L_PARENTHESIS, DOT_SEPARATOR, INCREMENT, DECREMENT)) {
                // If there is an opening sqr bracket after the expression,
                // then we must have an array indexing expression.
                if (previous().type == L_SQUARE_BRACKET) {
                    expr = arrayIndexing(expr);
                }

                // If there is an opening parenthesis after the expression,
                // then we must have a function call expression.
                if (previous().type == L_PARENTHESIS) {
                    expr = call(expr);
                }

                // If there is a dot separator after the expression,
                // then we must have a member access expression.
                if (previous().type == DOT_SEPARATOR) {
                    Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                    expr = new Expr.MemberAccess(expr, name);
                }

                // If there is an increment or decrement operator after the expression,
                // then we must have an increment or decrement expression.
                if (previous().type == INCREMENT || previous().type == DECREMENT) {
                    expr = new Expr.DeIn_crement(previous(), expr, false);
                }
            }

            return expr;
        }
    }

    /**
     * Matches a function call expression as specified in the grammar.cfg file.
     * 
     * @return A function call expression.
     */
    private Expr call(Expr expr) {
        do {
            expr = finishCall(expr);
        } while (match(L_PARENTHESIS));

        return expr;
    }

    /**
     * Matches a lambda expression as specified in the grammar.cfg file.
     * 
     * @return A lambda expression.
     */
    private Expr lambda() {
        consume(L_PARENTHESIS, "Expected '(' before parameters.");

        List<Stmt.Parameter> parameters = new ArrayList<>();
        if (!check(R_PARENTHESIS)) {
            do {
                if (parameters.size() >= 255) {
                    throw new SyntaxError(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(parameter());
            } while (match(COMMA_SEPARATOR));
        }
        consume(R_PARENTHESIS, "Expected ')' for after parameters.");

        Token arrow = consume(THIN_ARROW, "Expected '->' before function body.");

        // If there is an opening curly brace after the arrow, then we expect to execute
        // a block. Otherwise, we expect an expression, and compose a return statement
        // from that expression.
        List<Stmt> body = new ArrayList<>();
        if (match(L_CURLY_BRACES)) {
            body = block();
        } else {
            Expr value = expression();
            body.add(new Stmt.Return(arrow, value));
        }

        return new Expr.Lambda(parameters, body);
    }

    /**
     * Helper method to match a function call.
     * 
     * @param callee The called function.
     * @return A function call expression.
     */
    private Expr finishCall(Expr callee) {
        List<Expr.Argument> arguments = new ArrayList<>();

        if (!check(R_PARENTHESIS)) {
            do {
                // Hinton only supports 255 arguments for a function call.
                if (arguments.size() >= 255) {
                    throw new SyntaxError(peek(), "Can't have more than 255 arguments.");
                }

                if (check(R_PARENTHESIS)) {
                    // We assume there are no more arguments if there
                    // was a comma after the last argument, but there
                    // wasn't an argument after the comma.
                    break;
                } else {
                    // Gets the next argument
                    Expr.Argument arg = argument();

                    // Checks that named arguments are declared at the
                    // end of the function call
                    if (arguments.size() > 0 && arg.name == null
                            && !(arguments.get(arguments.size() - 1).name == null)) {
                        throw new SyntaxError(arguments.get(arguments.size() - 1).name,
                                "Named arguments must be declared after all unnamed arguments.");
                    }

                    // If everything is good, we add it to the args list.
                    arguments.add(arg);
                }

            } while (match(COMMA_SEPARATOR));
        }

        Token paren = consume(R_PARENTHESIS, "Expect ')' after arguments.");

        return new Expr.Call(callee, paren, arguments);
    }

    /**
     * Matches an argument expression as specified in the grammar.cfg file.
     * 
     * @return An argument expression.
     */
    private Expr.Argument argument() {
        Expr expr = expression();

        if (expr instanceof Expr.Assign) {
            Expr.Assign assign = (Expr.Assign) expr;
            return new Expr.Argument(assign.name, assign.value);
        }

        return new Expr.Argument(null, expr);
    }

    /**
     * Matches a primary (terminal) expression as specified in the grammar.cfg file.
     * These serve as a base-case for the recursive nature of the parser.
     * 
     * @return A primary (terminal) expression.
     */
    private Expr primary() {
        if (match(FALSE_LITERAL)) {
            HintonBoolean bool = new HintonBoolean(false);
            return new Expr.Literal(bool);
        }

        if (match(TRUE_LITERAL)) {
            HintonBoolean bool = new HintonBoolean(true);
            return new Expr.Literal(bool);
        }

        if (match(NULL_LITERAL)) {
            HintonNull nil = new HintonNull();
            return new Expr.Literal(nil);
        }

        if (match(FLOAT_LITERAL)) {
            HintonFloat real = new HintonFloat((double) previous().literal);
            return new Expr.Literal(real);
        }

        if (match(INTEGER_LITERAL)) {
            HintonInteger intr = new HintonInteger((int) previous().literal);
            return new Expr.Literal(intr);
        }

        if (match(STRING_LITERAL)) {
            HintonString str = new HintonString((String) previous().literal);
            return new Expr.Literal(str);
        }

        if (match(L_SQUARE_BRACKET)) {
            return constructArray();
        }

        if (match(L_PARENTHESIS)) {
            Expr expr = expression();
            consume(R_PARENTHESIS, "Expected ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(L_CURLY_BRACES)) {
            return constructDictionary();
        }

        throw new SyntaxError(peek(), "Expected an expression.");
    }

    /**
     * Matches an array indexing expression as specified in the grammar.cfg file.
     * 
     * @param expr The identifier to be indexed.
     * @return An array indexing expression.
     */
    private Expr arrayIndexing(Expr expr) {
        do {
            expr = new Expr.Indexing(peek(), expr, expression());
            consume(R_SQUARE_BRACKET, "Expected ']' after array index.");
        } while (match(L_SQUARE_BRACKET));

        return expr;
    }

    /**
     * Constructs an array expression as specified in the grammar.cfg file.
     * 
     * @return An array expression.
     */
    private Expr constructArray() {
        ArrayList<Expr> expressions = new ArrayList<>();

        if (!match(R_SQUARE_BRACKET)) {
            do {
                if (check(R_SQUARE_BRACKET)) {
                    // We assume there are no more array items if there
                    // was a comma after the last array item, but there
                    // wasn't an array item after the comma.
                    break;
                }

                expressions.add(expression());
            } while (match(COMMA_SEPARATOR));

            consume(R_SQUARE_BRACKET, "Expected ']' after array declaration.");
        }

        return new Expr.Array(expressions);
    }

    /**
     * Constructs a dictionary expression as specified in the grammar.cfg file.
     * 
     * @return A dictionary expression.
     */
    private Expr.Dictionary constructDictionary() {
        ArrayList<Expr.KeyValPair> pairs = new ArrayList<>();

        if (!match(R_CURLY_BRACES)) {
            do {
                if (check(R_CURLY_BRACES)) {
                    // We assume there are no more vay-value pairs if there
                    // was a comma after the last key-value pair, but there
                    // wasn't a key-value after the comma.
                    break;
                }

                pairs.add(getKeyValPair());
            } while (match(COMMA_SEPARATOR));

            consume(R_CURLY_BRACES, "Expected '}' after dictionary declaration.");
        }

        return new Expr.Dictionary(pairs);
    }

    /**
     * Matches a key-value pair as specified in the grammar.cfg file.
     * 
     * @return A key-value pair.
     */
    private Expr.KeyValPair getKeyValPair() {
        Token key;
        if (match(IDENTIFIER, STRING_LITERAL)) {
            key = previous();
        } else {
            throw new SyntaxError(peek(), "Expected a key name.");
        }

        consume(COLON_SEPARATOR, "Expected a ':' after key name.");
        Expr val = expression();

        return new Expr.KeyValPair(key, val);
    }
}
