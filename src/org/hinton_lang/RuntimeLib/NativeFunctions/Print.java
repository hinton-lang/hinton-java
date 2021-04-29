package org.hinton_lang.RuntimeLib.NativeFunctions;

import java.util.HashMap;

import org.hinton_lang.Helper.Helper;
import org.hinton_lang.Interpreter.HintonFunctions.HintonCallable;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonNull.HintonNull;
import org.hinton_lang.Scanner.Token;

/**
 * Native Hinton function for printing output to the command prompt.
 */
public class Print implements NativeFunc {

    /**
     * Specifies the name of the function.
     */
    @Override
    public String getFuncName() {
        return "print";
    }

    /**
     * Specifies the function's body.
     */
    @Override
    public HintonCallable getFunc() {
        return new HintonFunction(this.getFuncName(), null, null) {
            @Override
            public Object call(Token token, HashMap<Object, Object> arguments) {
                System.out.println(Helper.stringify(arguments.get(0)));
                return new HintonNull();
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
                return "<HintonFunction: print>";
            }
        };
    }
}
