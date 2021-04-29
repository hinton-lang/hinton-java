package org.hinton_lang.Interpreter.HintonDictionary;

import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Helper.Helper;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.Token;

/**
 * Method to get a dictionary member by its key name.
 */
public class DictionaryGet extends HintonFunction {
    HintonDictionary parent;

    public DictionaryGet(HintonDictionary parent) {
        super("[Dict].get", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public Object call(Token token, HashMap<Object, Object> arguments) {
        Object arg = arguments.get(0);

        if (!(arg instanceof HintonString)) {
            throw new RuntimeError(token,
                    "Expected a string key name. Got '" + Helper.getObjectType(arg) + "' instead.");
        }

        String key = ((HintonString) arg).getRaw();

        // We then check if it is inside the keyset for the dictionary
        if (this.parent.members.containsKey(key)) {
            return this.parent.members.get(key);
        }

        throw new RuntimeError(token, "Property '" + key + "' does not exist in Dictionary.");
    }

    @Override
    public int minArity() {
        return 1;
    }

    @Override
    public int maxArity() {
        return 1;
    }

    @Override
    public String toString() {
        return "<Function: [Dict].get>";
    }
}
