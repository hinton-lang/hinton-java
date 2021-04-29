package org.hinton_lang.Interpreter.HintonEnum;

import java.util.List;
import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Interpreter.HintonInteger.HintonInteger;
import org.hinton_lang.Parser.Stmt.EnumMember;
import org.hinton_lang.Scanner.Token;

/**
 * Represents a Hinton Enum in the Hinton language.
 */
public class HintonEnum implements NativeType {
    private final String name;
    private final HashMap<String, HintonInteger> members = new HashMap<>();
    private final HashMap<String, Object> methods = new HashMap<>();

    /**
     * Wraps a Java HashMap object so that it can be interpreted as a Hinton Enum.
     * 
     * @param enumName The enum identifier.
     * @param members  A list of members for this enum.
     */
    public HintonEnum(Token enumName, List<EnumMember> members) {
        this.name = enumName.lexeme;
        // The methods for enum objects in the Hinton language
        methods.put("length", new HintonInteger(members.size()));

        // Adds the enum members
        for (EnumMember member : members) {
            if (methods.containsKey(member.name.lexeme)) {
                throw new RuntimeError(member.name, "Cannot redeclare built-in enum member '" + member.name.lexeme
                        + "' in enum '" + this.name + "'.");
            } else if (this.members.containsKey(member.name.lexeme)) {
                throw new RuntimeError(member.name,
                        "Cannot redeclare enum member '" + member.name.lexeme + "' in enum '" + this.name + "'.");
            } else {
                // Adds the members of this Hinton Enum, where each member maps to
                // the integer position in the declaration sequence.
                this.members.put(member.name.lexeme, new HintonInteger(this.members.size()));
            }
        }
    }

    /**
     * Gets the given Hinton Enum property.
     * 
     * @param prop The property to be accessed.
     * @return The property's value.
     */
    public Object getProperty(Token prop) {
        // We first check if the requested property is a built-in member.
        if (methods.containsKey(prop.lexeme)) {
            return methods.get(prop.lexeme);
        }

        // We then check if it is inside the keyset for the enum
        if (members.containsKey(prop.lexeme)) {
            return members.get(prop.lexeme);
        }

        throw new RuntimeError(prop, "Property '" + prop.lexeme + "' does not exist in enum '" + this.name + "'.");
    }

    /**
     * Returns the raw HashMap in this wrapper.
     * 
     * @return The raw HashMap.
     */
    public HashMap<String, HintonInteger> getRaw() {
        return this.members;
    }

    /**
     * Return the Hinton type name for the object.
     */
    public String typeName() {
        return this.name;
    }

    /**
     * String representation of a Hinton Enum.
     */
    @Override
    public String toString() {
        return "<Enum " + this.name + ">";
    }

    /**
     * Formatted string representation of a Hinton Enum.
     */
    @Override
    public String formattedStr() {
        return this.toString();
    }
}
