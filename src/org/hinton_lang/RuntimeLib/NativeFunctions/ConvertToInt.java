package org.hinton_lang.RuntimeLib.NativeFunctions;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.HintonFunctions.HintonCallable;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.Token;

public class ConvertToInt implements NativeFunc {
    /**
     * Specifies the name of the function.
     */
    @Override
    public String getFuncName() {
        return "int";
    }

    /**
     * Specifies the function's body.
     */
    @Override
    public HintonCallable getFunc() {
        return new HintonFunction(this.getFuncName(), null, null) {
            @Override
            public HintonInteger call(Token token, HashMap<Object, Object> arguments) {

                // Checks that the passed argument is a Hinton String
                String strInt;
                if (arguments.get(0) instanceof HintonString) {
                    strInt = ((HintonString) arguments.get(0)).getRaw();
                } else {
                    throw new RuntimeError(token, "Cannot cast \"" + arguments.get(0) + "\" to integer");
                }

                // Tries to convert the string to an integer, if not possible, throw a
                // RuntimeError.
                try {
                    return new HintonInteger(Integer.parseInt(strInt));
                } catch (NumberFormatException e) {
                    throw new RuntimeError(token, "Cannot cast \"" + arguments.get(0) + "\" to integer");
                }
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
                return "<HintonFunction: int>";
            }
        };
    }
}
