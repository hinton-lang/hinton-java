package org.hinton_lang.Errors;

import org.hinton_lang.Scanner.Token;

public class SyntaxError extends RuntimeException {
    public final Token token;

    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /**
     * Error thrown when the parser encounters an error.
     * 
     * @param token   The token which caused the error.
     * @param message The error message.
     */
    public SyntaxError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
