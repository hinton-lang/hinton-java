package org.hinton_lang.Interpreter.HintonArrays;

import java.util.HashMap;

import org.hinton_lang.Interpreter.HintonBoolean.HintonBoolean;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Scanner.Token;

/**
 * Method for checking if an element is inside an array.
 */
public class ArrayContains extends HintonFunction {
    HintonArray parent;

    public ArrayContains(HintonArray parent) {
        super("[Array].contains", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public HintonBoolean call(Token token, HashMap<Object, Object> arguments) {
        return new HintonBoolean(this.parent.arr.contains(arguments.get(0)));
    }

    @Override
    public int minArity() {
        return 1;
    }

    @Override
    public int maxArity() {
        return 1;
    }

    @Override
    public String toString() {
        return "<Function: [Array].constains>";
    }
}