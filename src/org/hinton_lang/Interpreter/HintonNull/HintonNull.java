package org.hinton_lang.Interpreter.HintonNull;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Scanner.Token;

/**
 * Represents a null literal in the Hinton language.
 */
public class HintonNull implements NativeType {
    /**
     * Wraps a Java Null so that it can be read as a Hinton Null by the interpreter.
     */
    public HintonNull() {
    }

    /**
     * Returns the raw null in this wrapper.
     * 
     * @return The raw null.
     */
    public Object getRaw() {
        return null;
    }

    /**
     * Return the Hinton type name for the object.
     */
    public String typeName() {
        return "Null";
    }

    /**
     * Gets the given Hinton Null property.
     * 
     * @param prop The property to be accessed.
     * @return The property's value.
     */
    public Object getProperty(Token prop) {
        throw new RuntimeError(prop, "Property '" + prop.lexeme + "' does not exist on type 'Null'.");
    }

    /**
     * String representation of a Hinton Null.
     */
    @Override
    public String toString() {
        return "null";
    }

    /**
     * Formatted string representation of a Hinton Null.
     */
    public String formattedStr() {
        return "\u001b[1m\u001b[38;5;255m" + this.toString() + "\u001b[0m";
    }
}