package org.hinton_lang.Envornment;

import java.util.HashMap;
import java.util.Map;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Scanner.Token;

public class Environment {
    /** Stores the values for this scope */
    private final Map<String, StoredValue> values = new HashMap<>();
    /** Parent scope */
    private final Environment enclosing;

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Defines an object in this scope.
     * 
     * @param name    The object's name.
     * @param value   Tha object's value.
     * @param decType The type of declaration.
     */
    public void define(Token name, Object value, DecType decType) {
        if (values.containsKey(name.lexeme)) {
            throw new RuntimeError(name, "Cannot redeclare \"" + name.lexeme + "\".");
        }

        // If the value hasn't been declared, we added to the environment.
        values.put(name.lexeme, new StoredValue(value, decType));
    }

    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name).getValue();
    }

    public Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    public void assignAt(int distance, Token name, Object value) {
        DecType dtype = ancestor(distance).values.get(name.lexeme).getDeclarationType();

        if (dtype == DecType.CONSTANT) {
            throw new RuntimeError(name, "Cannot reassign to constant \"" + name.lexeme + "\".");
        } else if (dtype == DecType.HINTON_FUNCTION) {
            throw new RuntimeError(name, "Cannot reassign to built-in function \"" + name.lexeme + "\".");
        } else {
            ancestor(distance).values.get(name.lexeme).setValue(value);
        }
    }

    /**
     * Defines a built-in objects in this scope.
     * 
     * @param name    The object's name.
     * @param value   Tha object's value.
     * @param decType The type of declaration.
     */
    public void defineBuiltIn(String name, Object value, DecType decType) {
        values.put(name, new StoredValue(value, decType));
    }

    /**
     * Reassigns a value to an identifier in this or enclosing scopes.
     * 
     * @param name  The identifier's name.
     * @param value The new identifier's value.
     */
    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            DecType dtype = values.get(name.lexeme).getDeclarationType();

            if (dtype == DecType.CONSTANT) {
                throw new RuntimeError(name, "Cannot reassign to constant \"" + name.lexeme + "\".");
            } else if (dtype == DecType.HINTON_FUNCTION) {
                throw new RuntimeError(name, "Cannot reassign to built-in function \"" + name.lexeme + "\".");
            } else {
                values.get(name.lexeme).setValue(value);
            }

            return;
        }

        // Recursively look for the given variable
        // name in enclosing environments.
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Gets a value stored by an identifier in this scope or enclosing scopes.
     * 
     * @param name The name of the identifier.
     * @return The value stored by the identifier.
     */
    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            Object val = values.get(name.lexeme).getValue();

            if (val == null) {
                throw new RuntimeError(name, "Variable \"" + name.lexeme + "\" has not been initialized.");
            } else {
                return val;
            }
        }

        // Recursively look for the given variable
        // name in enclosing environments.
        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Checks that an identifier exists in this scope or the enclosing scopes.
     * 
     * @param name The name of the identifier.
     * @return True if the identifier is defined, False otherwise.
     */
    public boolean contains(Token name) {
        if (values.containsKey(name.lexeme)) {
            return true;
        }

        // Recursively look for the given variable
        // name in enclosing environments.
        if (enclosing != null)
            return enclosing.contains(name);

        return false;
    }

    /**
     * Converts the hashmap into a readable string.
     */
    public String toString() {
        StringBuilder str = new StringBuilder("====================\n");
        str.append("ENVIRONMENT\n");
        str.append("====================\n");

        int mx = 0;
        for (String n : values.keySet()) {
            if (mx < n.length()) {
                mx = n.length();
            }
        }

        for (String name : values.keySet()) {
            Object value = values.get(name).getValue();

            if (value == null)
                value = "null";

            int pad = mx - name.length() + 4;
            str.append(name).append(" ".repeat(pad)).append("= ").append(value.toString()).append("\n");
        }

        str.append("====================\n");

        return str.toString();
    }
}
