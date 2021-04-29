package org.hinton_lang.Interpreter.HintonDictionary;

import java.util.HashMap;

import org.hinton_lang.Errors.RuntimeError;
import org.hinton_lang.Helper.Helper;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonNull.HintonNull;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.Token;

/**
 * Method for adding a new member to the dictionary. If the key already exists
 * in the dictionary, the previous value gets overwritten.
 */
public class DictionaryPut extends HintonFunction {
    HintonDictionary parent;

    public DictionaryPut(HintonDictionary parent) {
        super("[Dict].put", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public HintonNull call(Token token, HashMap<Object, Object> arguments) {
        Object arg1 = arguments.get(0);
        Object value = arguments.get(1);

        if (!(arg1 instanceof HintonString)) {
            throw new RuntimeError(token,
                    "Expected a string for dictionary key. Got '" + Helper.getObjectType(arg1) + "' instead.");
        }

        String key = ((HintonString) arg1).getRaw();
        this.parent.members.put(key, value);

        return new HintonNull();
    }

    @Override
    public int minArity() {
        return 2;
    }

    @Override
    public int maxArity() {
        return 2;
    }

    @Override
    public String toString() {
        return "<Function: [Dict].put>";
    }
}
