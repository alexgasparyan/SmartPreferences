package com.armdroid.smartpreferences;

public class DefaultValue {

    /**
     *
     * @param type Instance for which class type default value is returned.
     * @param <T> Type for which default value is returned.
     * @return Default value for specified type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T forObject(T type) {
        if (type instanceof Boolean) {
            return (T) Boolean.FALSE;
        } else if (type instanceof Character) {
            return (T) Character.valueOf('\0');
        } else if (type instanceof Byte) {
            return (T) Byte.valueOf((byte) 0);
        } else if (type instanceof Short) {
            return (T) Short.valueOf((short) 0);
        } else if (type instanceof Integer) {
            return (T) Integer.valueOf(0);
        } else if (type instanceof Long) {
            return (T) Long.valueOf(0L);
        } else if (type instanceof Float) {
            return (T) Float.valueOf(0f);
        } else if (type instanceof Double) {
            return (T) Double.valueOf(0d);
        } else {
            return null;
        }
    }
}
