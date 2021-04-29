package org.hinton_lang.Interpreter.HintonArrays;

import java.util.HashMap;

import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonFunctions.HintonLambda;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Interpreter.HintonNull.HintonNull;
import org.hinton_lang.Parser.*;
import org.hinton_lang.Scanner.Token;
import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Helper.Helper;
import org.hinton_lang.Interpreter.HintonBoolean.HintonBoolean;

/**
 * Method for looping through the items of an array.
 */
public class ArrayForEach extends HintonFunction {
    HintonArray parent;

    public ArrayForEach(HintonArray parent) {
        super("[Array].forEach", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public HintonNull call(Token token, HashMap<Object, Object> arguments) {
        Object arg = arguments.get(0);

        if (!(arg instanceof HintonLambda)) {
            throw new RuntimeError(token,
                    "Expected a function or lambda expression. Got '" + Helper.getObjectType(arg) + "' instead.");
        }

        HintonLambda lambda = (HintonLambda) arg;

        // The callback accepted by HintonArray.forEach can only have
        // a max of two parameters, namely, item and index respectively.
        if (lambda.maxArity() > 2) {
            Stmt.Parameter extraParam = lambda.parameters.get(2);
            throw new RuntimeError(extraParam.name,
                    "[Array].forEach accepts up to 2 parameter, got " + lambda.minArity() + " instead.");
        }

        for (int i = 0; i < this.parent.arr.size(); i++) {
            Object item = this.parent.arr.get(i);
            HashMap<Object, Object> args = new HashMap<>();

            if (lambda.parameters.size() >= 1) {
                // Adds the array item to the callback's arguments
                if (item instanceof Expr) {
                    args.put(0, lambda.interpreter.evaluate((Expr) item));
                } else {
                    args.put(0, item);
                }

                // Adds the item's index to the callback's arguments
                if (lambda.parameters.size() == 2) {
                    args.put(1, new HintonInteger(i));
                }
            }

            // executes the callback
            Object c = lambda.call(token, args);

            // We break the loop if the programmer returns `false`
            // within the .forEach callback.
            if (c instanceof HintonBoolean && !((HintonBoolean) c).getRaw()) {
                break;
            }
        }

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
        return "<Function: [Array].forEach>";
    }
}
