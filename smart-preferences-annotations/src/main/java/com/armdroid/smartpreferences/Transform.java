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
 * It provides custom logic of transformation between field and preference values using an inherited class from
 * {@link PreferenceTransformer}. For example:
 *
 *<pre><code>
 * {@literal @}StringPreference
 * {@literal @}Transform(using = GsonTransformer.class, typeParam1 = Custom.class) //to imitate GsonTranformer<Custom>
 *  protected static Custom custom;
 *
 *  public abstract class GsonTransformer<To> implements PreferenceTransformer<String, To> {
 *
 * {@literal @}Override
 *  public To convertRead(String from) {
 *      Type superClass = getClass().getGenericSuperclass();
 *      Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
 *      return new Gson().fromJson(from, genericType);
 *  }
 *
 * {@literal @}Override
 *  public String convertWrite(To to) {
 *      Type superClass = getClass().getGenericSuperclass();
 *      Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
 *      return new Gson().toJson(genericType);
 *  }
 *}
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Transform {

    /**
     * This attribute specifies the class that will be used for field and preference transformations.
     * Class must implement {@link PreferenceTransformer}
     * @return Transformer class.
     */
    Class<? extends PreferenceTransformer> using();

    /**
     * This optional attribute is the first type parameter of the transformer. In some cases, generics is
     * required for correct transformations (see the code in the description of class).
     * <br><br>
     * So using = CustomTransformer.class, typeParam1 = Custom1.class imitates
     * <br><br>
     * CustomTransformer < Custom1 >
     * @return Transformer class.
     */
    Class typeParam1() default Object.class;

    /**
     * This optional attribute is the second type parameter of the transformer. In some cases, generics is
     * required for correct transformations (see the code in the description of class).
     * <br><br>
     * So "using = CustomTransformer.class, typeParam1 = Custom1.class, typeParam2 = Custom2.class" imitates
     * <br><br>
     * CustomTransformer < Custom1, Custom2 >
     * @return Transformer class.
     */
    Class typeParam2() default Object.class;
}
