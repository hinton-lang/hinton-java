package org.hinton_lang.Interpreter.HintonFunctions;

import java.util.ArrayList;
import java.util.HashMap;

import org.hinton_lang.Parser.Expr;
import org.hinton_lang.Scanner.Token;
import org.hinton_lang.Interpreter.Interpreter;
import org.hinton_lang.Errors.RuntimeError;

/**
 * Represents a lambda expression.
 */
public class HintonLambda extends HintonCallable {

    public HintonLambda(Interpreter inter, Expr.Lambda declaration) {
        super(inter, declaration);
    }

    /**
     * Creates a scope and executes the function on every function call.
     */
    @Override
    public Object call(Token token, HashMap<Object, Object> arguments) {
        Object[] argsList = arguments.keySet().toArray();
        ArrayList<String> alreadySet = new ArrayList<>();

        for (int j = 0, argsListLength = argsList.length; j < argsListLength; j++) {
            Object arg = argsList[j];
            Object val = arguments.get(arg);

            if (arg instanceof Integer) {
                int i = (int) arg;
                alreadySet.add(this.parameters.get(i).name.lexeme);
                this.environment.assign(this.parameters.get(i).name, val);
            } else {
                Token t = (Token) arg;

                if (alreadySet.contains(t.lexeme)) {
                    throw new RuntimeError(t, "Received multiple values for argument '" + t.lexeme + "'.");
                } else {
                    if (environment.contains(t)) {
                        alreadySet.add(t.lexeme);
                        environment.assign(t, val);
                    } else {
                        throw new RuntimeError(t, "Unexpected named argument '" + t.lexeme + "'.");
                    }
                }
            }
        }

        return this.executeFunc();
    }

    /**
     * String representation.
     */
    @Override
    public String toString() {
        return "<LambdaFunction>";
    }
}
