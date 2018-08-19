package com.armdroid.smartpreferences;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation is used with fields and indicates that the field is working with string preference.
 * Field does not have to be of type String, as this annotation can be used with {@link Transform} and
 * appropriate transformation can transform String to any object. For example:
 *
 *<pre><code>
 * {@literal @}StringPreference
 *  public String a;
 *
 * {@literal @}StringPreference
 * {@literal @}Transform(using = CustomTransformer.class)
 *  public Custom custom;
 *
 * </code></pre>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface StringPreference {

    /**
     * This attribute can be set to indicate the key that will be used working with preferences. If this
     * attribute is empty, SmartPreferences uses the name of field as key.
     * @return Preference key to find value.
     */
    String named() default "";

    /**
     * This attribute sets the default value when reading from shared preferences. In case value is not found with
     * associated key, this value will be returned.
     * @return Default preference value if key not found.
     */
    String defaultValue() default "";
}
