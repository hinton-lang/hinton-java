package org.hinton_lang.Interpreter.ControlStmts;

/**
 * Used to return values from a function, even when the return statement is deep
 * within many other statements in the function's body.
 */
public class Return extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = -2289347107930686765L;

    public final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
