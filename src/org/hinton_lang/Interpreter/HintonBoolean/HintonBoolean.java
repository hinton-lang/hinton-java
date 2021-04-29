package org.hinton_lang.Interpreter.HintonBoolean;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Scanner.Token;

/**
 * Represents a boolean literal in the Hinton Language.
 */
public class HintonBoolean implements NativeType {
    private final boolean booleanValue;
    private final HashMap<String, Object> methods = new HashMap<>();

    /**
     * Wraps a Java Boolean so that it can be read as a Hinton Bool by the
     * interpreter.
     * 
     * @param bool The Java Boolean to be wrapped.
     */
    public HintonBoolean(boolean bool) {
        this.booleanValue = bool;

        // The methods for Hinton Bool objects in the Hinton language
        // **** Hinton Booleans do not have any native methods
    }

    /**
     * Returns the raw boolean in this wrapper.
     * 
     * @return The raw boolean.
     */
    public Boolean getRaw() {
        return this.booleanValue;
    }

    /**
     * Return the Hinton type name for the object.
     */
    public String typeName() {
        return "Bool";
    }

    /**
     * Gets the given Hinton Bool property.
     * 
     * @param prop The property to be accessed.
     * @return The property's value.
     */
    public Object getProperty(Token prop) {
        if (methods.containsKey(prop.lexeme)) {
            return methods.get(prop.lexeme);
        }

        throw new RuntimeError(prop, "Property '" + prop.lexeme + "' does not exist on type 'Bool'.");
    }

    /**
     * String representation of a Hinton Bool.
     */
    @Override
    public String toString() {
        return String.valueOf(this.booleanValue);
    }

    /**
     * Formatted string representation of a Hinton Bool.
     */
    public String formattedStr() {
        return "\u001b[33m" + this.toString() + "\u001b[0m";
    }
}
