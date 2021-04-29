package org.hinton_lang.Interpreter.HintonArrays;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Scanner.Token;

/**
 * Method for removing the last element of an array.
 */
public class ArrayPop extends HintonFunction {
    HintonArray parent;

    public ArrayPop(HintonArray parent) {
        super("[Array].pop", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public Object call(Token token, HashMap<Object, Object> arguments) {
        if (this.parent.arr.size() == 0) {
            throw new RuntimeError(token, "Cannot pop from empty array.");
        }

        Object popped = this.parent.arr.get(this.parent.arr.size() - 1);
        this.parent.arr.remove(this.parent.arr.size() - 1);
        return popped;
    }

    @Override
    public int minArity() {
        return 0;
    }

    @Override
    public int maxArity() {
        return 0;
    }

    @Override
    public String toString() {
        return "<Function: [Array].pop>";
    }
}
