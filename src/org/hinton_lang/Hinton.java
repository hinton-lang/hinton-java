package org.hinton_lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hinton_lang.Parser.*;
import org.hinton_lang.Scanner.*;
import org.hinton_lang.Analyzers.Resolver;
import org.hinton_lang.CLI.ProcessArgs;
import org.hinton_lang.Errors.SyntaxError;
import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.Interpreter;

public class Hinton {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    public static ArrayList<String> programPermissions = new ArrayList<>();
    public static ArrayList<String> programArgs = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ProcessArgs argsProcessor = new ProcessArgs(args);
        argsProcessor.run();
    }

    /**
     * Sets the program permissions provided in the CLI.
     * 
     * @param permissions The program permissions.
     */
    public static void setPermissions(ArrayList<String> permissions) {
        programPermissions = permissions;
    }

    /**
     * Sets the program arguments provided in the CLI.
     * 
     * @param args The program arguments.
     */
    public static void setProgramArgs(String[] args) {
        Collections.addAll(programArgs, args);
    }

    /**
     * Rus a file containing Hinton source code.
     * 
     * @param path The path to the file.
     * @throws IOException Error when the file is not found.
     */
    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    /**
     * Runs a REPL to execute Hinton code.
     * 
     * @throws IOException Error when the line cannot be read.
     */
    public static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print(">> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    /**
     * Executes the source code (source file or REPL).
     * 
     * @param source The source code.
     */
    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.lexTokens();

        // Delete the reference to the lexer so that it is garbage collected later.
        lexer = null;

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError)
            return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // Stop if there was a resolution error.
        if (hadError)
            return;

        interpreter.interpret(statements);
        // System.out.println(interpreter.environment);
    }

    /**
     * Reports an error to the console
     * 
     * @param line    The line of source code that caused the error.
     * @param where   The position fo the error.
     * @param message An error message.
     */
    private static void report(int line, int column, String where, String message) {
        System.err.println("[line " + line + ":" + column + "] Error" + where + ": " + message);
        hadError = true;
    }

    /**
     * Reports a generic error.
     * 
     * @param line    The line of the error.
     * @param message The error message.
     */
    static void error(int line, int column, String message) {
        report(line, column, "", message);
    }

    /**
     * Reports a token error.
     * 
     * @param token   The token that is causing the error.
     * @param message The error message.
     */
    public static void error(Token token, String message) {
        if (token.type == TokenType.END_OF_FILE) {
            report(token.linePos, token.columnPos, " at end", message);
        } else {
            report(token.linePos, token.columnPos, " at '" + token.lexeme + "'", message);
        }
    }

    /**
     * Reports a runtime error.
     * 
     * @param error The runtime error.
     */
    public static void runtimeError(RuntimeError error) {
        if (error.token != null) {
            String line = error.token.linePos + "," + error.token.columnPos;
            System.err.println("RuntimeError [" + line + "]: " + error.getMessage());
        } else {
            System.err.println("RuntimeError: " + error.getMessage());
        }
        hadRuntimeError = true;
    }

    /**
     * Reports a syntax (parser) error.
     * 
     * @param error The syntax error.
     */
    public static void parserError(SyntaxError error) {
        if (error.token != null) {
            String line = error.token.linePos + "," + error.token.columnPos;
            System.err.println("SyntaxError [" + line + "]: " + error.getMessage());
        } else {
            System.err.println("SyntaxError: " + error.getMessage());
        }
        hadRuntimeError = true;
    }
}
