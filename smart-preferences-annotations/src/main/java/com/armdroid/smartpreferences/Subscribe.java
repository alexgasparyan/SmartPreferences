package com.armdroid.smartpreferences;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * Annotation is used on a method and acts as a preference change callback. In order to use this annotation,
 * there should be a field with name "tag" which also has {@link Observe} annotation. Field must be in the same class.
 * For example:
 *<pre><code>
 * {@literal @}LongPreference
 * {@literal @}Observe
 *  public long longFoo;
 *
 * {@literal @}Subscribe(tag="longFoo")
 *  public void onValueUpdate(long oldValue) {
 *
 *  }
 *</code></pre>
 *  Here, whenever value of key "longFoo" changes in preferences, field 'longFoo' is updated and method onValueUpdate is invoked,
 *  carrying the value of field before update as well.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Subscribe {

    /**
     * This attribute is required when {@link Observe} annotation is also used somewhere in the class. It provides connection between
     * field and update callback method if tags match. Importantly, tag should never be empty in order to make the connection work.
     * @return Tag that acts as connector.
     */
    String tag();
}
