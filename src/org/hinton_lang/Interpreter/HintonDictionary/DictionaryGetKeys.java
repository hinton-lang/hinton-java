package org.hinton_lang.Interpreter.HintonDictionary;

import java.util.ArrayList;
import java.util.HashMap;

import org.hinton_lang.Interpreter.HintonArrays.HintonArray;
import org.hinton_lang.Interpreter.HintonFunctions.HintonFunction;
import org.hinton_lang.Interpreter.HintonString.HintonString;
import org.hinton_lang.Scanner.Token;

/**
 * Method for getting the list of top-level keys in a dictionary.
 */
public class DictionaryGetKeys extends HintonFunction {
    HintonDictionary parent;

    public DictionaryGetKeys(HintonDictionary parent) {
        super("[Dict].getKeys", parent.interpreter, null);
        this.parent = parent;
    }

    @Override
    public HintonArray call(Token token, HashMap<Object, Object> arguments) {
        ArrayList<Object> keys = new ArrayList<>();
        this.parent.members.keySet().forEach(key -> keys.add(new HintonString(key)));
        return new HintonArray(keys, interpreter);
    }

    @Override
    public int minArity() {
        return 0;
    }

    @Override
    public int maxArity() {
        return 0;
    }

    @Override
    public String toString() {
        return "<Function: [Dict].getKeys>";
    }
}
