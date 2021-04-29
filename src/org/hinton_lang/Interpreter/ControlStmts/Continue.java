package org.hinton_lang.Interpreter.ControlStmts;

/**
 * Used to restart a loop, even when the continue statement is deep within many
 * other statements in the loop's body.
 */
public class Continue extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = -2289347107930686765L;

    public Continue() {
        super(null, null, false, false);
    }
}
