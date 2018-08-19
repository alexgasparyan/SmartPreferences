package com.armdroid.smartpreferences;

/**
 * Interface which provides contract between generated Preferences class and target class
 */
public interface PreferenceBinder {

    /**
     * Reads values from {@link android.content.SharedPreferences} and sets to fields that have one of the
     * following annotations in target class:
     * {@link IntPreference}, {@link FloatPreference}, {@link LongPreference}, {@link StringPreference}, {@link BooleanPreference}.
     */
    void readAll();

    /**
     * Writes values into {@link android.content.SharedPreferences} from fields that have one of the
     * following annotations in target class:
     * {@link IntPreference}, {@link FloatPreference}, {@link LongPreference}, {@link StringPreference}, {@link BooleanPreference}.
     */
    void writeAll();

    /**
     * Observes changes of preferences of fields that have {@link Observe} annotation in target class along with one of
     * the following annotations:
     * {@link IntPreference}, {@link FloatPreference}, {@link LongPreference}, {@link StringPreference}, {@link BooleanPreference}.
     */
    void observeChanges();

    /**
     * Stops observation of changes of preferences of fields that have {@link Observe} annotation in target class along with one of
     * the following annotations:
     * {@link IntPreference}, {@link FloatPreference}, {@link LongPreference}, {@link StringPreference}, {@link BooleanPreference}.
     */
    void stopObserveChanges();

    /**
     * Returns an instance that can be used for more actions with {@link android.content.SharedPreferences} if generated class
     * actions are not enough.
     * @return Singleton instance of {@link PreferenceRepository}.
     */
    PreferenceRepository getPreferenceRepository();

    /**
     * Sets default values to fields that have one of the following annotations in target class. Values are
     * specified by {@link DefaultValue}.
     * {@link IntPreference}, {@link FloatPreference}, {@link LongPreference}, {@link StringPreference}, {@link BooleanPreference}.
     */
    void setTypeDefaults();

    /**
     * Unbinds generated class instance from target instance. After being called, the current instance is not suitable for use anymore
     * and static bind method must be called again in order to get a new instance. Use of current instance after this method call will
     * cause {@link NullPointerException}.
     */
    void unbind();

}
