package org.hinton_lang.RuntimeLib.NativeFunctions;

import java.util.HashMap;

import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Interpreter.HintonFunctions.HintonCallable;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.Token;

public class TypeOf implements NativeFunc {

    /**
     * Specifies the name of the function.
     */
    @Override
    public String getFuncName() {
        return "typeOf";
    }

    /**
     * Specifies the function's body.
     */
    @Override
    public HintonCallable getFunc() {
        return new HintonFunction(this.getFuncName(), null, null) {
            @Override
            public HintonString call(Token token, HashMap<Object, Object> arguments) {
                Object arg = arguments.get(0);

                if (arg instanceof NativeType) {
                    return new HintonString(((NativeType) arg).typeName());
                } else {
                    return new HintonString(arg.getClass().getName());
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
                return "<HintonFunction: typeOf>";
            }
        };
    }
}