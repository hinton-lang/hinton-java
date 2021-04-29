package org.hinton_lang.Interpreter;

import java.util.ArrayList;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Helper.Helper;
import org.hinton_lang.Interpreter.HintonArrays.HintonArray;
import org.hinton_lang.Interpreter.HintonBoolean.HintonBoolean;
import org.hinton_lang.Interpreter.HintonFloat.HintonFloat;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Interpreter.HintonNull.HintonNull;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.Token;

public class EvalBinaryExpr {

    /**
     * Checks that the two operands hold numeric value.
     * 
     * @param opr   The operator to be applied on the operands.
     * @param left  The left operand.
     * @param right The right operand.
     */
    private static void checkNumberOperands(Token opr, Object left, Object right) {
        if ((left instanceof HintonFloat || left instanceof HintonInteger)
                && (right instanceof HintonFloat || right instanceof HintonInteger))
            return;

        throw new RuntimeError(opr, "Operation '" + opr.lexeme + "' not defined for operands of type '"
                + Helper.getObjectType(left) + "' and '" + Helper.getObjectType(right) + "'.");
    }

    /**
     * Checks that the two operands hold equals values.
     * 
     * @param left  The left operand.
     * @param right The right operand.
     */
    private static boolean isEqual(Object left, Object right) {
        if (left instanceof HintonNull && right instanceof HintonNull)
            return true;
        if (left instanceof HintonNull)
            return false;

        return ((NativeType) left).getRaw().equals(((NativeType) right).getRaw());
    }

    /**
     * Evaluates an addition expression.
     * 
     * @param opr   The addition operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static NativeType evalAddition(Token opr, Object left, Object right) {
        // Addition or reals
        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonFloat(((HintonFloat) left).getRaw() + ((HintonFloat) right).getRaw());
        }
        // Additions of integers
        if (left instanceof HintonInteger && right instanceof HintonInteger) {
            return new HintonInteger(((HintonInteger) left).getRaw() + ((HintonInteger) right).getRaw());
        }
        // Addition of reals and integers
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonFloat(((HintonInteger) left).getRaw() + ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonFloat(((HintonFloat) left).getRaw() + ((HintonInteger) right).getRaw());
        }

        // Addition of two strings
        if (left instanceof HintonString && right instanceof HintonString) {
            return new HintonString(((HintonString) left).getRaw() + ((HintonString) right).getRaw());
        }

        // Addition of Strings and numbers
        if (left instanceof HintonString && right instanceof HintonFloat) {
            return new HintonString(((HintonString) left).getRaw() + ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonString && right instanceof HintonInteger) {
            return new HintonString(((HintonString) left).getRaw() + ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonString) {
            return new HintonString(((HintonFloat) left).getRaw() + ((HintonString) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonString) {
            return new HintonString(((HintonInteger) left).getRaw() + ((HintonString) right).getRaw());
        }

        throw new RuntimeError(opr, "Operation '+' not defined for operands of type '" + Helper.getObjectType(left)
                + "' and '" + Helper.getObjectType(right) + "'.");
    }

    /**
     * Evaluates a subtraction expression.
     * 
     * @param opr   The subtraction operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static NativeType evalSubtraction(Token opr, Object left, Object right) {
        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonFloat(((HintonFloat) left).getRaw() - ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonFloat(((HintonFloat) left).getRaw() - ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonFloat(((HintonInteger) left).getRaw() - ((HintonFloat) right).getRaw());
        }

        return new HintonInteger(((HintonInteger) left).getRaw() - ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates a division expression.
     * 
     * @param opr   The division operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static NativeType evalDivision(Token opr, Object left, Object right) {
        checkNumberOperands(opr, left, right);

        // Prevents division by zero
        if (right instanceof HintonInteger && ((HintonInteger) right).getRaw() == 0) {
            throw new RuntimeError(opr, "Cannot divide by zero.");
        }
        if (right instanceof HintonFloat && ((HintonFloat) right).getRaw() == 0.0) {
            throw new RuntimeError(opr, "Cannot divide by zero.");
        }

        // If no division by zero, continue to execute the division
        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonFloat(((HintonFloat) left).getRaw() / ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonFloat(((HintonFloat) left).getRaw() / ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonFloat(((HintonInteger) left).getRaw() / ((HintonFloat) right).getRaw());
        }

        // In Hinton, division always evaluates to a real number.
        return new HintonFloat(
                (double) (((HintonInteger) left).getRaw()) / (double) (((HintonInteger) right).getRaw()));
    }

    /**
     * Evaluates a multiplication expression.
     * 
     * @param opr   The multiplication operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static NativeType evalMultiplication(Token opr, Object left, Object right) {
        // Support for string-integer multiplication
        if (left instanceof HintonString && right instanceof HintonInteger) {
            return new HintonString((((HintonString) left).getRaw()).repeat(((HintonInteger) right).getRaw()));
        } else if (left instanceof HintonInteger && right instanceof HintonString) {
            return new HintonString((((HintonString) right).getRaw()).repeat(((HintonInteger) left).getRaw()));
        }

        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonFloat(((HintonFloat) left).getRaw() * ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonFloat(((HintonFloat) left).getRaw() * ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonFloat(((HintonInteger) left).getRaw() * ((HintonFloat) right).getRaw());
        }

        return new HintonInteger(((HintonInteger) left).getRaw() * ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates a modulus expression.
     * 
     * @param opr   The modulus operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonInteger evalModulus(Token opr, Object left, Object right) {
        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonInteger((int) (((HintonFloat) left).getRaw() % ((HintonFloat) right).getRaw()));
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonInteger((int) (((HintonFloat) left).getRaw() % ((HintonInteger) right).getRaw()));
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonInteger((int) (((HintonInteger) left).getRaw() % ((HintonFloat) right).getRaw()));
        }

        return new HintonInteger(((HintonInteger) left).getRaw() % ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates an exponentiation expression.
     * 
     * @param opr   The exponentiation operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static NativeType evalExponent(Token opr, Object left, Object right) {
        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonFloat(Math.pow(((HintonFloat) left).getRaw(), ((HintonFloat) right).getRaw()));
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonFloat(Math.pow(((HintonFloat) left).getRaw(), ((HintonInteger) right).getRaw()));
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonFloat(Math.pow(((HintonInteger) left).getRaw(), ((HintonFloat) right).getRaw()));
        }

        return new HintonInteger((int) Math.pow(((HintonInteger) left).getRaw(), ((HintonInteger) right).getRaw()));
    }

    /**
     * Evaluates a greater-than expression.
     * 
     * @param opr   The greater-than operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonBoolean evalGreaterThan(Token opr, Object left, Object right) {
        if (left instanceof HintonBoolean) {
            left = ((HintonBoolean) left).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        if (right instanceof HintonBoolean) {
            right = ((HintonBoolean) right).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }

        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonFloat) left).getRaw() > ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonBoolean(((HintonFloat) left).getRaw() > ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonInteger) left).getRaw() > ((HintonFloat) right).getRaw());
        }

        return new HintonBoolean(((HintonInteger) left).getRaw() > ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates a greater-than-or-equal expression.
     * 
     * @param opr   The greater-than-or-equal operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonBoolean evalGreaterThanEqual(Token opr, Object left, Object right) {
        if (left instanceof HintonBoolean) {
            left = ((HintonBoolean) left).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        if (right instanceof HintonBoolean) {
            right = ((HintonBoolean) right).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }

        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonFloat) left).getRaw() >= ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonBoolean(((HintonFloat) left).getRaw() >= ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonInteger) left).getRaw() >= ((HintonFloat) right).getRaw());
        }

        return new HintonBoolean(((HintonInteger) left).getRaw() >= ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates a less-than expression.
     * 
     * @param opr   The less-than operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonBoolean evalLessThan(Token opr, Object left, Object right) {
        if (left instanceof HintonBoolean) {
            left = ((HintonBoolean) left).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        if (right instanceof HintonBoolean) {
            right = ((HintonBoolean) right).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }

        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonFloat) left).getRaw() < ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonBoolean(((HintonFloat) left).getRaw() < ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonInteger) left).getRaw() < ((HintonFloat) right).getRaw());
        }

        return new HintonBoolean(((HintonInteger) left).getRaw() < ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates a less-than-or-equal expression.
     * 
     * @param opr   The less-than-or-equal operator token.
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonBoolean evalLessThanEqual(Token opr, Object left, Object right) {
        if (left instanceof HintonBoolean) {
            left = ((HintonBoolean) left).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        if (right instanceof HintonBoolean) {
            right = ((HintonBoolean) right).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }

        checkNumberOperands(opr, left, right);

        if (left instanceof HintonFloat && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonFloat) left).getRaw() <= ((HintonFloat) right).getRaw());
        }
        if (left instanceof HintonFloat && right instanceof HintonInteger) {
            return new HintonBoolean(((HintonFloat) left).getRaw() <= ((HintonInteger) right).getRaw());
        }
        if (left instanceof HintonInteger && right instanceof HintonFloat) {
            return new HintonBoolean(((HintonInteger) left).getRaw() <= ((HintonFloat) right).getRaw());
        }

        return new HintonBoolean(((HintonInteger) left).getRaw() <= ((HintonInteger) right).getRaw());
    }

    /**
     * Evaluates an equality expression.
     *
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonBoolean evalEquals(Object left, Object right) {
        if (left instanceof HintonBoolean) {
            left = ((HintonBoolean) left).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        if (right instanceof HintonBoolean) {
            right = ((HintonBoolean) right).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        return new HintonBoolean(isEqual(left, right));
    }

    /**
     * Evaluates an inequality expression.
     *
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonBoolean evalNotEquals(Object left, Object right) {
        if (left instanceof HintonBoolean) {
            left = ((HintonBoolean) left).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        if (right instanceof HintonBoolean) {
            right = ((HintonBoolean) right).getRaw() ? new HintonInteger(1) : new HintonInteger(0);
        }
        return new HintonBoolean(!isEqual(left, right));
    }

    /**
     * Evaluates a range expression.
     *
     * @param left  The left operand.
     * @param right The right operand.
     */
    public static HintonArray evalRange(Interpreter inter, Token opr, Object left, Object right) {
        if (!(left instanceof HintonInteger) || !(right instanceof HintonInteger)) {
            throw new RuntimeError(opr, "Range operation '" + opr.lexeme + "' not defined for operands of type '"
                    + Helper.getObjectType(left) + "' and '" + Helper.getObjectType(right) + "'.");
        }

        int l = ((HintonInteger) left).getRaw();
        int r = ((HintonInteger) right).getRaw();
        int direction = l - r;

        ArrayList<Object> arr = new ArrayList<>();

        if (direction < 0) {
            for (int i = l; i < r; i++) {
                arr.add(new HintonInteger(i));
            }
        } else if (direction > 0) {
            for (int i = l; i > r; i--) {
                arr.add(new HintonInteger(i));
            }
        } else {
            arr.add(new HintonInteger(l));
        }

        return new HintonArray(arr, inter);
    }
}
