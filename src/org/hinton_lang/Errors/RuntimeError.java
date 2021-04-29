package org.hinton_lang.Errors;

import org.hinton_lang.Scanner.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;

    /** Serial ID */
    private static final long serialVersionUID = -7660344298236100497L;

    /**
     * Error thrown when the interpreter encounters an error.
     * 
     * @param token   The token which produced the error.
     * @param message The error message.
     */
    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
