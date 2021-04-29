package org.hinton_lang.Helper;

import org.hinton_lang.Interpreter.NativeType;

public class Helper {
    /**
     * Converts the given object to a string for printing.
     *
     * @param object The object to tbe converted.
     * @return The string version of the object.
     */
    public static String stringify(Object object) {
        if (object == null)
            return "null";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    /**
     * Returns the Hinton Type Name associated with the passed object.
     * 
     * @param obj The object whose type will be evaluated.
     * @return The Hinton Type name of the passed object.
     */
    public static String getObjectType(Object obj) {
        String type;
        if (obj instanceof NativeType) {
            type = ((NativeType) obj).typeName();
        } else {
            type = obj.toString();
        }

        return type;
    }
}
