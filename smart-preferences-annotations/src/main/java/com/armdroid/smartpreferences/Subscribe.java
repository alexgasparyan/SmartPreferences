package com.armdroid.smartpreferences;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * Annotation is used on a method and acts as the preference change callback for an associated
 * tag. In order to use this annotation, there should be a field that has same non-empty tag as the
 * field with {@link Observe} annotation. For example:
 *<pre><code>
 * {@literal @}LongPreference
 * {@literal @}Observe(tag="myTag")
 *  public long a;
 *
 * {@literal @}Subscribe(tag="myTag")
 *  public void onValueUpdate(long oldValue) {
 *
 *  }
 *</code></pre>
 *  Here, whenever value of key "a" changes in preferences, field 'a' is updated and method onValueUpdate is invoked, carrying the value
 *  of field before update as well.
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
