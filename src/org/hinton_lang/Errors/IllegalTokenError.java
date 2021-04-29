package org.hinton_lang.Errors;

import org.hinton_lang.Scanner.Token;

public class IllegalTokenError extends Error {
    /** Serial Version ID */
    private static final long serialVersionUID = -2503948893459935778L;

    /**
     * Error thrown when the found token cannot be interpreter by Hinton.
     * 
     * @param token The illegal token.
     */
    public IllegalTokenError(Token token) {
        super("Unexpected Illegal Token '" + token.lexeme + "' on line " + token.linePos + ":" + token.columnPos);
    }
}
