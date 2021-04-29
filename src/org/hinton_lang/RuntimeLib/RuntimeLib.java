package org.hinton_lang.RuntimeLib;

import java.util.ArrayList;

import org.hinton_lang.RuntimeLib.NativeFunctions.*;

/**
 * The library of functions, classes, constant (+ other resources) that is
 * attached to the global scope of the program during runtime.
 */
public class RuntimeLib {
    /**
     * The native Hinton runtime functions.
     */
    public static final ArrayList<NativeFunc> nativeFunctions = new ArrayList<>() {
        private static final long serialVersionUID = -403425461174683407L;

        // Declaring all native functions
        {
            add(new Print()); // Native print function
            add(new Input()); // Native print function
            add(new Clock()); // Native clock function
            add(new ConvertToInt()); // Native clock function
            add(new TypeOf()); // Native type checking function
        }
    };
}
