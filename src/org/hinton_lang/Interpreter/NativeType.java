package org.hinton_lang.Interpreter;

import org.hinton_lang.Scanner.Token;

/**
 * Represents a native Hinton type.
 */
public interface NativeType {
    /**
     * The raw Java Object wrapped by the implementer class.
     * 
     * @return The raw Java Object.
     */
    Object getRaw();

    /**
     * The literal type name for the object.
     * 
     * @return The literal type name.
     */
    String typeName();

    /**
     * Gets the given property from the native type.
     * 
     * @param prop The property to be accessed.
     * @return The property's value.
     */
    Object getProperty(Token prop);

    /**
     * Formatted string representation of a Hinton native type.
     * 
     * @return A formatted string that can represents the native type.
     */
    String formattedStr();
}
