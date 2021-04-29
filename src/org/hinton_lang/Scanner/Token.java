package org.hinton_lang.Scanner;

import java.util.HashMap;

public class Token {
    public TokenType type;
    public int linePos;
    public int columnPos;
    public String lexeme;
    public Object literal;

    public Token(TokenType type, int linePos, int columnPos, String text, Object value) {
        this.type = type;
        this.linePos = linePos;
        this.columnPos = columnPos;
        this.lexeme = text;
        this.literal = value;
    }

    /**
     * String representation of the Token.
     * 
     * @return The string representation of the Token.
     */
    public String toString() {
        // The largest token's name length is 27
        String leftPad = " ".repeat(30 - type.name().length());
        return type.name() + ":" + leftPad + lexeme;
    }

    /** Maps the keyword's names to their corresponding token types. */
    public static HashMap<String, TokenType> Keywords = new HashMap<>();

    static {
        // Static Type Keywords
        Keywords.put("Any", TokenType.ANY_TYPE);
        Keywords.put("Array", TokenType.ARRAY_DATATYPE);
        Keywords.put("Bool", TokenType.BOOLEAN_TYPE);
        Keywords.put("Char", TokenType.CHARACTER_TYPE);
        Keywords.put("Dict", TokenType.DICTIONARY_TYPE);
        Keywords.put("Float", TokenType.FLOAT_TYPE);
        Keywords.put("Function", TokenType.FUNCTION_TYPE);
        Keywords.put("Int", TokenType.INTEGER_TYPE);
        Keywords.put("Null", TokenType.NULL_TYPE);
        Keywords.put("String", TokenType.STRING_TYPE);
        Keywords.put("Void", TokenType.VOID_TYPE);

        // Language keywords
        Keywords.put("and", TokenType.LOGICAL_AND);
        Keywords.put("as", TokenType.AS_OPERATOR);
        Keywords.put("break", TokenType.BREAK_KEYWORD);
        Keywords.put("const", TokenType.CONST_KEYWORD);
        Keywords.put("continue", TokenType.CONTINUE_KEYWORD);
        Keywords.put("else", TokenType.ELSE_KEYWORD);
        Keywords.put("enum", TokenType.ENUM_KEYWORD);
        Keywords.put("equals", TokenType.LOGICAL_EQ);
        Keywords.put("false", TokenType.FALSE_LITERAL);
        Keywords.put("fn", TokenType.FN_LAMBDA_KEYWORD);
        Keywords.put("for", TokenType.FOR_KEYWORD);
        Keywords.put("func", TokenType.FUNC_KEYWORD);
        Keywords.put("if", TokenType.IF_KEYWORD);
        Keywords.put("in", TokenType.IN_OPERATOR);
        Keywords.put("is", TokenType.IS_OPERATOR);
        Keywords.put("mod", TokenType.MODULUS);
        Keywords.put("not", TokenType.LOGICAL_NOT);
        Keywords.put("null", TokenType.NULL_LITERAL);
        Keywords.put("or", TokenType.LOGICAL_OR);
        Keywords.put("return", TokenType.RETURN_KEYWORD);
        Keywords.put("self", TokenType.SELF_KEYWORD);
        Keywords.put("super", TokenType.SUPER_KEYWORD);
        Keywords.put("true", TokenType.TRUE_LITERAL);
        Keywords.put("var", TokenType.VAR_KEYWORD);
        Keywords.put("while", TokenType.WHILE_KEYWORD);

        // ***** Modules and OOP
        // Keywords.put("abstract", TokenType.ABSTRACT_KEYWORD);
        // Keywords.put("class", TokenType.CLASS_KEYWORD);
        // Keywords.put("export", TokenType.EXPORT_KEYWORD);
        // Keywords.put("extends", TokenType.EXTENDS_KEYWORD);
        // Keywords.put("final", TokenType.FINAL_KEYWORD);
        // Keywords.put("from", TokenType.FROM_KEYWORD);
        // Keywords.put("implements", TokenType.IMPLEMENTS_KEYWORD);
        // Keywords.put("import", TokenType.IMPORT_KEYWORD);
        // Keywords.put("instanceof", TokenType.INSTANCE_OF_KEYWORD);
        // Keywords.put("interface", TokenType.INTERFACE_KEYWORD);
        // Keywords.put("new", TokenType.NEW_KEYWORD);
        // Keywords.put("optional", TokenType.OPTIONAL_KEYWORD);
        // Keywords.put("override", TokenType.OVERRIDE_KEYWORD);
        // Keywords.put("private", TokenType.PRIVATE_KEYWORD);
        // Keywords.put("public", TokenType.PUBLIC_KEYWORD);
        // Keywords.put("self", TokenType.SELF_KEYWORD);
        // Keywords.put("static", TokenType.STATIC_KEYWORD);

        // Other
        // Keywords.put("async", TokenType.ASYNC_KEYWORD);
        // Keywords.put("await", TokenType.AWAIT_KEYWORD);
        // Keywords.put("struct", TokenType.STRUCT_KEYWORD);
        // Keywords.put("yield", TokenType.YIELD_KEYWORD);
    }
}
