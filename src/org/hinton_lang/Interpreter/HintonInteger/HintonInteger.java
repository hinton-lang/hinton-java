package org.hinton_lang.Interpreter.HintonInteger;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Scanner.Token;

/**
 * Represents an integer in the Hinton language.
 */
public class HintonInteger implements NativeType {
    private final int integerValue;
    private final HashMap<String, Object> methods = new HashMap<>();

    /**
     * Wraps a Java Integer so that it can be read as a Hinton Integer by the
     * interpreter.
     * 
     * @param integer The Java Integer to be wrapped.
     */
    public HintonInteger(int integer) {
        this.integerValue = integer;

        // The methods for Hinton Integer objects in the Hinton language
    }

    /**
     * Returns the raw integer stored in this wrapper.
     * 
     * @return The raw integer.
     */
    public Integer getRaw() {
        return this.integerValue;
    }

    /**
     * Return the Hinton type name for the object.
     */
    public String typeName() {
        return "Int";
    }

    /**
     * Gets the given Hinton Integer property.
     * 
     * @param prop The property to be accessed.
     * @return The property's value.
     */
    public Object getProperty(Token prop) {
        if (methods.containsKey(prop.lexeme)) {
            return methods.get(prop.lexeme);
        }

        throw new RuntimeError(prop, "Property '" + prop.lexeme + "' does not exist on type 'Integer'.");
    }

    /**
     * String representation of a Hinton Integer.
     */
    @Override
    public String toString() {
        return String.valueOf(this.integerValue);
    }

    /**
     * Formatted string representation of a Hinton Integer.
     */
    @Override
    public String formattedStr() {
        return "\u001b[34m" + this.toString() + "\u001b[0m";
    }
}
