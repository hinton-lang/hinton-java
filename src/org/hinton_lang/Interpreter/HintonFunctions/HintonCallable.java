package org.hinton_lang.Interpreter.HintonFunctions;

import java.util.HashMap;
import java.util.List;

import org.hinton_lang.Envornment.DecType;
import org.hinton_lang.Envornment.Environment;
import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Interpreter.Interpreter;
import org.hinton_lang.Interpreter.NativeType;
import org.hinton_lang.Interpreter.ControlStmts.Return;
import org.hinton_lang.Interpreter.HintonNull.HintonNull;
import org.hinton_lang.Parser.*;
import org.hinton_lang.Scanner.Token;

/**
 * Represents any callable function in Hinton.
 */
public abstract class HintonCallable implements NativeType {
    public String name;
    public final List<Stmt.Parameter> parameters;
    public final List<Stmt> body;
    public final Interpreter interpreter;
    // The scope for this function
    public final Environment environment;
    private final HashMap<String, Object> methods = new HashMap<>();

    public int minArity, maxArity = 0;

    /**
     * Initializer for function declaration statements.
     * 
     * @param name        The name of the function.
     * @param interpreter A reference to the interpreter.
     * @param declaration The function declaration.
     */
    public HintonCallable(String name, Interpreter interpreter, Stmt.Function declaration) {
        this.name = name;
        this.parameters = (declaration != null) ? declaration.params : null;
        this.body = (declaration != null) ? declaration.body : null;
        this.environment = (interpreter != null) ? new Environment(interpreter.environment) : null;
        this.interpreter = interpreter;

        if (declaration != null)
            addParams();
    }

    /**
     * Initializer for lambda-function expressions.
     * 
     * @param inter       A reference to the interpreter.
     * @param declaration The lambda expression.
     */
    public HintonCallable(Interpreter inter, Expr.Lambda declaration) {
        this.name = "<Lambda>";
        this.parameters = (declaration != null) ? declaration.params : null;
        this.body = (declaration != null) ? declaration.body : null;
        this.environment = (inter != null) ? new Environment(inter.environment) : null;
        this.interpreter = inter;

        if (declaration != null)
            addParams();
    }

    /**
     * Adds any parameters declared in this function's signature.
     */
    private void addParams() {
        for (int i = 0; i < this.parameters.size(); i++) {
            Stmt.Parameter param = this.parameters.get(i);
            // We define the passed arguments as variables available within
            // the scope of this particular function.
            environment.define(param.name, interpreter.evaluate(param.defVal), DecType.VARIABLE);

            // Records the minimum and maximum number of
            // parameters accepted by this function.
            if (!param.isOptnl) {
                minArity = minArity + 1;
            }
            maxArity = maxArity + 1;
        }
    }

    /**
     * Default call method, which does not evaluate the function's body and
     * automatically returns null.
     * 
     * @param token     The caller token.
     * @param arguments The arguments passed to the function call.
     * @return The object returned by the function's body (if any).
     */
    public Object call(Token token, HashMap<Object, Object> arguments) {
        return new HintonNull();
    }

    /**
     * Executes the function body for the current function.
     * 
     * @return The return value of the function's body.
     */
    public Object executeFunc() {
        // Executes the function
        try {
            interpreter.executeBlock(this.body, this.environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }

        // Returns null on void block
        return new HintonNull();
    }

    /**
     * The number of required parameters declared for this function.
     */
    public int minArity() {
        return this.minArity;
    }

    /**
     * The number of optional parameters declared for this function.
     */
    public int maxArity() {
        return this.maxArity;
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

        throw new RuntimeError(prop, "Property '" + prop.lexeme + "' does not exist in enum '" + this.name + "'.");
    }

    /**
     * Returns the raw HashMap in this wrapper.
     * 
     * @return The raw HashMap.
     */
    public Object getRaw() {
        return null;
    }

    /**
     * Return the Hinton type name for the object.
     */
    public String typeName() {
        return "Function";
    }

    /**
     * Formatted string representation of a Hinton Enum.
     */
    @Override
    public String formattedStr() {
        return this.toString();
    }
}
