package org.hinton_lang.RuntimeLib.NativeFunctions;

import java.util.HashMap;

import org.hinton_lang.Interpreter.HintonFunctions.HintonCallable;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Scanner.Token;

/**
 * Native Hinton function for measuring the execution time between two
 * statements.
 */
public class Clock implements NativeFunc {

    /**
     * Specifies the name of the function.
     */
    @Override
    public String getFuncName() {
        return "clock";
    }

    /**
     * Specifies the function's body.
     */
    @Override
    public HintonCallable getFunc() {
        return new HintonFunction(this.getFuncName(), null, null) {
            @Override
            public Object call(Token token, HashMap<Object, Object> arguments) {
                return new HintonInteger((int) System.currentTimeMillis());
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
                return "<HintonFunction: clock>";
            }
        };
    }

}
