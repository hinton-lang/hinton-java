package org.hinton_lang.Interpreter;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Helper.Helper;
import org.hinton_lang.Interpreter.HintonBoolean.HintonBoolean;
import org.hinton_lang.Interpreter.HintonFloat.HintonFloat;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Scanner.Token;

public class EvalUnaryExpr {

    /**
     * Checks whether the provided operand holds a valid numeric value.
     * 
     * @param operator The operator.
     * @param operand  The operand.
     */
    private static void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof HintonFloat || operand instanceof HintonInteger || operand instanceof HintonBoolean)
            return;

        throw new RuntimeError(operator, "Cannot negate operand of type '" + Helper.getObjectType(operand) + "'.");
    }

    /**
     * Evaluates the boolean negation of the provided object.
     * 
     * @param right The operand.
     * @return (Boolean) The negation of the provided object.
     */
    public static HintonBoolean evalLogicNegation(Object right) {
        return new HintonBoolean(!Interpreter.isTruthy(right));
    }

    /**
     * Evaluates the numeric negation of the provided object.
     * 
     * @param opr   The operator to be applied on the operand.
     * @param right The operand.
     * @return (Number) The negation of the provided object.
     */
    public static NativeType evalNumericNegation(Token opr, Object right) {
        checkNumberOperand(opr, right);

        if (right instanceof HintonBoolean && ((HintonBoolean) right).getRaw())
            return new HintonInteger(-1);
        if (right instanceof HintonBoolean && !((HintonBoolean) right).getRaw())
            return new HintonInteger(0);
        if (right instanceof HintonFloat)
            return new HintonFloat(-((HintonFloat) right).getRaw());

        return new HintonInteger(-((HintonInteger) right).getRaw());
    }
}
