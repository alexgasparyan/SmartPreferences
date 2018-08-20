package com.armdroid.smartpreferences;

import java.util.List;

/**
 * Interface for applying transformations between 2 types (types can match as well).
 * @param <From> Type that is being converted
 * @param <To> Type that is converted to
 */
public interface PreferenceTransformer<From, To> {

    /**
     * Applied transformation when reading value from {@link android.content.SharedPreferences}
     * @param from Type that is being converted
     * @return Type that is converted to
     */
    To convertRead(From from);

    /**
     * Applied transformation when reading list of values from {@link android.content.SharedPreferences}
     * @param from Type that is being converted
     * @return Type that is converted to
     */
    List<To> convertReadList(From from);

    /**
     * Applied transformation when writing value to {@link android.content.SharedPreferences}
     * @param to Type that is being converted
     * @return Type that is converted to
     */
    From convertWrite(To to);

    /**
     * Applied transformation when writing list of values to {@link android.content.SharedPreferences}
     * @param to Type that is being converted
     * @return Type that is converted to
     */
    From convertWriteList(List<To> to);
}
