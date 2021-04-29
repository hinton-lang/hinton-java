package org.hinton_lang.Interpreter.ControlStmts;

/**
 * Used to break from a loop, even when the break statement is deep within many
 * other statements in the loop's body.
 */
public class Break extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = -2289347107930686765L;

    public Break() {
        super(null, null, false, false);
    }
}
