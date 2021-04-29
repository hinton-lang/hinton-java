package org.hinton_lang.Interpreter.HintonArrays;

import java.util.HashMap;

import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Scanner.Token;

/**
 * Method for adding an element to the end of an array.
 */
public class ArrayPush extends HintonFunction {
    HintonArray parent;

    public ArrayPush(HintonArray parent) {
        super("[Array].push", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public HintonInteger call(Token token, HashMap<Object, Object> arguments) {
        this.parent.arr.add(arguments.get(0));
        return new HintonInteger(this.parent.arr.size());
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
        return "<Function: [Array].push>";
    }
}
