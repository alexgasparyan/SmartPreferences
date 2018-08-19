package com.armdroid.smartpreferences;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation is used on a field with one of the following annotations:
 * <br>
 * {@link IntPreference}, {@link FloatPreference}, {@link LongPreference}, {@link StringPreference}, {@link BooleanPreference}.
 * <br><br>
 * It indicates that it is listening to preference changes. If the value of associate preference changes, the field on which this
 * annotation is used will automatically be updated. For example:
 *
 *<pre><code>
 * {@literal @}StringPreference
 * {@literal @}Observe
 *  public String a;
 *</code></pre>
 *
 * It is useful to use this annotation along with {@link Subscribe} on a listener method. This way not only value will be updated, but
 * a method specified by yourself will be invoked and old value will be available as well. In order to use this feature, subscribe must have
 * tag attribute set to field name. For example:
 *
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
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Observe {
}
