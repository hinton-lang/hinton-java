package org.hinton_lang.Envornment;

/**
 * The value of the object stored by an identifier in a specific environment.
 */
public class StoredValue {
    private Object value;
    public final DecType dclType;

    /**
     * The value of the object stored by an identifier in a specific environment.
     * 
     * @param value   The value.
     * @param dclType The type of declaration associated with the identifer that
     *                maps to this value.
     */
    public StoredValue(Object value, DecType dclType) {
        this.value = value;
        this.dclType = dclType;
    }

    /**
     * Resets the value.
     * 
     * @param value The new value.
     */
    public void setValue(Object value) {
        if (dclType != DecType.CONSTANT) {
            this.value = value;
        }
    }

    /**
     * Gets the value.
     * 
     * @return The stored value.
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Gets the signature type.
     * 
     * @return The signature type.
     */
    public DecType getDeclarationType() {
        return dclType;
    }
}
