package org.hinton_lang.RuntimeLib.NativeFunctions;

import org.hinton_lang.Interpreter.HintonFunctions.HintonCallable;

/**
 * Signature interface for implementing native Hinton functions.
 */
public interface NativeFunc {
    /**
     * Specifies the function's name.
     */
    String getFuncName();

    /**
     * Specifies the function's body.
     */
    HintonCallable getFunc();
}
