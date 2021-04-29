package org.hinton_lang.Interpreter.HintonString;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Scanner.Token;

/**
 * Represents a string in the Hinton language.
 */
public class HintonString implements NativeType {
    private final String str;
    private final HashMap<String, Object> methods = new HashMap<>();

    /**
     * Wraps a Java String so that it can be read as a Hinton String by the
     * interpreter.
     * 
     * @param string The Java String to be wrapped.
     */
    public HintonString(String string) {
        this.str = string;

        // The methods for hinton string objects in the Hinton language
        methods.put("length", string.length());
    }

    /**
     * Returns the raw string stored in this wrapper.
     * 
     * @return The raw string.
     */
    public String getRaw() {
        return this.str;
    }

    /**
     * Return the Hinton type name for the object.
     */
    public String typeName() {
        return "String";
    }

    /**
     * Gets the given Hinton String property.
     * 
     * @param prop The property to be accessed.
     * @return The property's value.
     */
    public Object getProperty(Token prop) {
        if (methods.containsKey(prop.lexeme)) {
            return methods.get(prop.lexeme);
        }

        throw new RuntimeError(prop, "Property '" + prop.lexeme + "' does not exist on type 'String'.");
    }

    /**
     * String representation of a Hinton String.
     */
    @Override
    public String toString() {
        return this.str;
    }

    /**
     * Formatted string representation of a Hinton String.
     */
    @Override
    public String formattedStr() {
        return "\u001b[32m\"" + this.toString() + "\"\u001b[0m";
    }
}
