package org.hinton_lang.Scanner;

import static org.hinton_lang.Scanner.TokenType.*;

import java.util.ArrayList;

public class Lexer {
    private final String source;
    private int lineNum = 1;
    private int lineStartPos = 0;
    private int tokenStart = 0;
    private int srcPos = 0;
    public ArrayList<Token> TokensList = new ArrayList<>();

    /**
     * Lexical Analyzer that takes an input file and converts the contents of the
     * file into a list of tokens based on the rules defined by the Tokenizer.
     * 
     * @param source The raw source text.
     */
    public Lexer(String source) {
        this.source = source;
    }

    /**
     * Generates a list of tokens from the source file.
     * 
     * @return A list of tokens.
     */
    public ArrayList<Token> lexTokens() {
        while (!isAtEnd()) {
            TokensList.add(scanToken());
        }

        TokensList.add(makeEOFToken());
        return TokensList;
    }

    /**
     * Advances to the next character in the source file.
     * 
     * @return The character we just advanced from.
     */
    private char advanceChar() {
        this.srcPos++;
        return this.source.charAt(srcPos - 1);
    }

    /**
     * Checks that the current character matches the give character.
     *
     * From Bob Nystrom: "If the current character is the desired one, we advance
     * and return true. Otherwise, we return false to indicate it was not matched."
     *
     * @param expected The character to be checked.
     * @return True if the character was matched, false otherwise.
     */
    private boolean match(char expected) {
        if (isAtEnd())
            return false;

        if (this.source.charAt(this.srcPos) != expected)
            return false;

        advanceChar();
        return true;
    }

    /**
     * Checks whether we are at the end of the source code or not.
     *
     * @return true if we are at the end of the source code, false otherwise.
     */
    private boolean isAtEnd() {
        return this.srcPos >= this.source.length();
    }

    /**
     * Returns the current character.
     *
     * @return the current character.
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        } else {
            return this.source.charAt(this.srcPos);
        }
    }

    /**
     * Returns the previous character.
     *
     * @return the previous character.
     */
    private char previous() {
        return this.source.charAt(this.srcPos - 1);
    }

    /**
     * Returns the next character.
     *
     * @return The next character.
     */
    private char peekNext() {
        if (isAtEnd())
            return '\0';
        return this.source.charAt(this.srcPos + 1);
    }

    /**
     * Constructs a token with the given tokenType and value.
     *
     * @param type The type of token to construct.
     * @param val  The literal value of the token.
     * @return The constructed token.
     */
    private Token makeToken(TokenType type, Object val) {
        int columnPos = this.srcPos - (this.lineStartPos + (this.srcPos - this.tokenStart));

        // int tLength = - this.tokenStart;
        String lexeme = this.source.substring(this.tokenStart, this.srcPos);

        return new Token(type, this.lineNum, columnPos, lexeme, val);
    }

    /**
     * Constructs a token with the given tokenType.
     *
     * @param type The type of token to construct.
     * @return The constructed token.
     */
    private Token makeToken(TokenType type) {
        return makeToken(type, null);
    }

    /**
     * Constructs an END_OF_FILE token.
     * 
     * @return The constructed token.
     */
    private Token makeEOFToken() {
        return new Token(END_OF_FILE, this.lineNum, this.tokenStart, "EOF", null);
    }

    /**
     * Constructs an error token.
     *
     * @param message The message for the error token.
     * @return The constructed error token.
     */
    private Token errorToken(String message) {
        return new Token(ERROR_TOKEN, this.lineNum, this.tokenStart, message, null);
    }

    /**
     * Skips any whitespace-like character from the source code.
     */
    private void skipWhitespace() {
        for (;;) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t') {
                advanceChar();
            } else if (c == '\n') {
                this.lineNum++;
                this.lineStartPos = (this.srcPos + 1);
                advanceChar();
            } else if (c == '/' && (peekNext() == '/' || peekNext() == '*')) {
                skipComments();
            } else {
                break;
            }
        }
    }

    /**
     * Skips block comments.
     */
    private void skipComments() {
        // single-line comments
        if (peek() == '/' && peekNext() == '/') {
            while (peek() != '\n' && !isAtEnd())
                advanceChar();
            return;
        }

        // block comments
        if (peek() == '/' && peekNext() == '*') {
            advanceChar();
            advanceChar();

            while (true) {
                if (peek() == '*' && peekNext() == '/') {
                    advanceChar();
                    advanceChar();
                    break;
                }

                if (isAtEnd())
                    break;

                advanceChar();
            }
        }
    }

    /**
     * Generates a string literal
     *
     * @return The string literal token
     */
    private Token makeStringLiteral() {
        while (true) {
            advanceChar();

            if ((peek() == '"' && previous() != '\\') || isAtEnd()) {
                break;
            }
        }

        if (isAtEnd())
            return errorToken("Unterminated string.");

        // The closing quote.
        advanceChar();

        // Source string without the quotes
        String srcString = this.source.substring(this.tokenStart + 1, this.srcPos - 1);
        StringBuilder stringLiteral = new StringBuilder();

        // Parses the escaped characters
        for (int i = 0; i < srcString.length(); i++) {
            char c = srcString.charAt(i);
            char nc = (i < srcString.length() - 1) ? srcString.charAt(i + 1) : '\0';

            if (c == '\\') {
                switch (nc) {
                    case 'n':
                        stringLiteral.append('\n');
                        i++;
                        break;
                    case 't':
                        stringLiteral.append('\t');
                        i++;
                        break;
                    case 'r':
                        stringLiteral.append('\r');
                        i++;
                        break;
                    case 'b':
                        stringLiteral.append('\b');
                        i++;
                        break;
                    case '\\':
                        stringLiteral.append('\\');
                        i++;
                        break;
                    case '"':
                        stringLiteral.append('"');
                        i++;
                        break;
                    default:
                        stringLiteral.append('\\');
                        stringLiteral.append(nc);
                        i++;
                        break;
                }
            } else {
                stringLiteral.append(c);
            }
        }

        return makeToken(STRING_LITERAL, stringLiteral.toString());
    }

    /**
     * Converts a given string from any base to base-10.
     * 
     * @param raw  The raw string.
     * @param base The base to which the string will be converted.
     * @return The base-10 integer that the string represents.
     */
    private int toBase10(String raw, int base) {
        int num = 0;

        for (int i = 2; i < raw.length(); i++) {
            char c = raw.charAt(i);
            int p = raw.length() - i - 1;

            if (c >= '0' && c <= '9') {
                num += (c - 48) * Math.pow(base, p);
            }

            // For hexadecimal base
            if (base == 16) {
                if (c >= 'a' && c <= 'f') {
                    num += (c - 87) * Math.pow(base, p);
                }

                if (c >= 'A' && c <= 'F') {
                    num += (c - 55) * Math.pow(base, p);
                }
            }
        }

        return num;
    }

    /**
     * Generates a numeric literal.
     *
     * @return The numeric literal token.
     */
    private Token makeNumericLiteral() {
        // Support for hexadecimal integers
        // Hexadecimal literals are converted to integer literals during compilation
        if (previous() == '0' && (peek() == 'x' || peek() == 'X')) {
            advanceChar(); // consumes the 'x'

            while ((peek() >= '0' && peek() <= '9') || (peek() >= 'a' && peek() <= 'f')
                    || (peek() >= 'A' && peek() <= 'F')) {
                advanceChar();
            }

            String strNum = this.source.substring(this.tokenStart, this.srcPos);
            return makeToken(INTEGER_LITERAL, toBase10(strNum, 16));
        }

        // Support for octal integers
        // Octal literals are converted to integer literals during compilation
        if (previous() == '0' && (peek() == 'o' || peek() == 'O')) {
            advanceChar(); // consumes the 'o'

            while (peek() >= '0' && peek() <= '7')
                advanceChar();

            String strNum = this.source.substring(this.tokenStart, this.srcPos);
            return makeToken(INTEGER_LITERAL, toBase10(strNum, 8));
        }

        // Support for binary integers
        // Binary literals are converted to integer literals during compilation
        if (previous() == '0' && (peek() == 'b' || peek() == 'B')) {
            advanceChar(); // consumes the 'b'

            while (peek() == '0' || peek() == '1')
                advanceChar();

            String strNum = this.source.substring(this.tokenStart, this.srcPos);
            return makeToken(INTEGER_LITERAL, toBase10(strNum, 2));
        }

        // Regular integers and reals
        StringBuilder strNum = new StringBuilder();

        char start = previous();
        if (start == '.') {
            strNum.append('0');
        }
        strNum.append(start);

        while (Character.isDigit(peek()) || (peek() == '_' && previous() != '_')) {
            char c = advanceChar();
            if (c != '_') {
                strNum.append(c);
            }
        }

        // Support for real numbers that start with a period. I.e., `.314`
        if (start == '.') {
            return makeToken(FLOAT_LITERAL, Double.parseDouble(strNum.toString()));
        }

        // Look for a fractional part.
        if (peek() == '.' && Character.isDigit(peekNext())) {
            // Consume the ".".
            strNum.append(advanceChar());

            while (Character.isDigit(peek()) || (peek() == '_' && previous() != '_')) {
                char c = advanceChar();
                if (c != '_') {
                    strNum.append(c);
                }
            }

            return makeToken(FLOAT_LITERAL, Double.parseDouble(strNum.toString()));
        }

        return makeToken(INTEGER_LITERAL, Integer.parseInt(strNum.toString()));
    }

    /**
     * Generates an identifier.
     *
     * @return The identifier token.
     */
    private Token makeIdentifierToken() {
        int tStart = this.srcPos - 1;
        while (Character.isAlphabetic(peek()) || Character.isDigit(peek()) || peek() == '_')
            advanceChar();
        String name = this.source.substring(tStart, this.srcPos);

        if (Token.Keywords.containsKey(name)) {
            return makeToken(Token.Keywords.get(name));
        }

        return makeToken(IDENTIFIER);
    }

    /**
     * Scans a token.
     * 
     * @return The scanned token.
     */
    private Token scanToken() {
        skipWhitespace();

        // Reset the start of the token
        this.tokenStart = this.srcPos;

        if (isAtEnd())
            return makeEOFToken();

        char c = advanceChar();

        if (Character.isAlphabetic(c) || c == '_')
            return makeIdentifierToken();
        if (Character.isDigit(c))
            return makeNumericLiteral();

        switch (c) {
            case '(':
                return makeToken(L_PARENTHESIS);
            case ')':
                return makeToken(R_PARENTHESIS);
            case '{':
                return makeToken(L_CURLY_BRACES);
            case '}':
                return makeToken(R_CURLY_BRACES);
            case '[':
                return makeToken(L_SQUARE_BRACKET);
            case ']':
                return makeToken(R_SQUARE_BRACKET);
            case ';':
                return makeToken(SEMICOLON_SEPARATOR);
            case ':':
                return makeToken(COLON_SEPARATOR);
            case ',':
                return makeToken(COMMA_SEPARATOR);
            case '.': {
                if (Character.isDigit(peek())) {
                    return makeNumericLiteral();
                } else if (match('.')) {
                    return makeToken(RANGE_OPERATOR);
                } else {
                    return makeToken(DOT_SEPARATOR);
                }
            }
            case '-': {
                if (match('=')) {
                    return makeToken(MINUS_EQUALS);
                } else if (match('-')) {
                    return makeToken(DECREMENT);
                } else if (match('>')) {
                    return makeToken(THIN_ARROW);
                } else {
                    return makeToken(MINUS);
                }
            }
            case '+': {
                if (match('=')) {
                    return makeToken(PLUS_EQUALS);
                } else if (match('+')) {
                    return makeToken(INCREMENT);
                } else {
                    return makeToken(PLUS);
                }
            }
            case '/':
                return makeToken(match('=') ? SLASH_EQUALS : SLASH);
            case '*':
                if (match('=')) {
                    return makeToken(STAR_EQUALS);
                } else if (match('*') && !match('=')) {
                    return makeToken(EXPO);
                } else if (match('*') && match('=')) {
                    return makeToken(EXPO_EQUALS);
                } else {
                    return makeToken(STAR);
                }
            case '%':
                return makeToken(match('=') ? MOD_EQUALS : MODULUS);
            case '!':
                return makeToken(match('=') ? LOGICAL_NOT_EQ : LOGICAL_NOT);
            case '=':
                return makeToken(match('=') ? LOGICAL_EQ : EQUALS_SIGN);
            case '<':
                return makeToken(match('=') ? LESS_THAN_EQ : LESS_THAN);
            case '>':
                return makeToken(match('=') ? GREATER_THAN_EQ : GREATER_THAN);
            case '&':
                return makeToken(match('&') ? LOGICAL_AND : BITWISE_AND);
            case '|':
                return makeToken(match('|') ? LOGICAL_OR : BITWISE_OR);
            case '^':
                return makeToken(BITWISE_XOR);
            case '"':
                return makeStringLiteral();
        }

        return errorToken("Invalid or unexpected token.");
    }
}
