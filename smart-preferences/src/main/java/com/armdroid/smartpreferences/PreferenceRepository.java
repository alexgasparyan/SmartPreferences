package com.armdroid.smartpreferences;

import android.content.SharedPreferences;

import java.util.Map;

public class PreferenceRepository {

    private SharedPreferences mPreferences;

    private static class Holder {
        private static PreferenceRepository INSTANCE = new PreferenceRepository();
    }

    /**
     * Returns singleton instance of PreferenceRepository which can be used to handle all kind of
     * operations with {@link SharedPreferences}.
     * @return Singleton instance of {@link PreferenceRepository}
     */
    public static PreferenceRepository getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Normally, this method should not be called by user, however, it can be used for handling operations
     * with other instances of {@link SharedPreferences} other than the one provided by SmartPreferences library.
     */
    public void setPreferences(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    /**
     * Returns value of associated key from {@link SharedPreferences}. Return type is specified by the type of defaultValue.
     * defaultValue can be one of the following: String, int, long, float, boolean and their boxed types.
     * {@link IllegalArgumentException} is thrown in case the type of defaultValue is not in the list above.
     * @param key Identifier of object in preferences.
     * @param defaultValue Default value in case preference not found.
     * @param <T> Type of the object that is going to be returned.
     * @return Instance of object associated with key.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object value;
        if (defaultValue instanceof String) {
            value = mPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            value = mPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            value = mPreferences.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            value = mPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            value = mPreferences.getBoolean(key, (Boolean) defaultValue);
        } else {
            throw new IllegalArgumentException("Field is of type that has no support in shared preferences");
        }
        return ((T) value);
    }

    /**
     * Returns a map of key, value pairs from {@link SharedPreferences}.
     * @return Map of pairs.
     */
    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    /**
     * Saves value with associated key in {@link SharedPreferences}.
     * Value can be one of the following: String, int, long, float, boolean and their boxed types.
     * {@link IllegalArgumentException} is thrown in case the type of value is not in the list above.
     * @param key Identifier of object in preferences.
     * @param value Value that is going to be saved.
     */
    public void put(String key, Object value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else {
            throw new IllegalArgumentException("Field is of type that has no support in shared preferences");
        }
        editor.apply();
    }

    /**
     * Checks whether {@link SharedPreferences} contains key/value pair associated with specified key.
     * @param key Key to be searched.
     * @return true if key/value pair exists, false otherwise.
     */
    public boolean contains(String key) {
        return mPreferences.contains(key);
    }

    /**
     * Tries to remove key/balue pair from {@link SharedPreferences}.
     * @param key Key to be searched.
     * @return true if key/value pair was removed, false if pair did not exist.
     */
    public boolean remove(String key) {
        if (contains(key)) {
            mPreferences.edit().remove(key).apply();
            return true;
        }
        return false;
    }

    /**
     * Clears all key/value pairs from {@link SharedPreferences}.
     */
    public void clearPreferences() {
        mPreferences.edit().clear().apply();
    }

    /**
     * Registers a listener that listens to changes in {@link SharedPreferences}.
     * @param listener New instance of {@link SharedPreferences.OnSharedPreferenceChangeListener} that will listen to changes.
     */
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregisters existing listener that was listening to changes in {@link SharedPreferences}.
     * @param listener Instance of {@link SharedPreferences.OnSharedPreferenceChangeListener} that was listening to changes.
     */
    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
