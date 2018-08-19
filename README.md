# Smart Preferences #

An extremely lightweight library that uses full power of annotation processing to 
make working with android `SharedPreferences` easier than before.
You can:
* Read int, float, long, String or boolean values from `SharedPreferences`
* Write int, float, long, String or boolean  values to `SharedPreferences`
* Read and write any kind of object using custom transformations
* Observe changes in `SharedPreferences`
* Detect compile time errors in gradle console with the easiest
possible way

## Usage ##

Add the following to the gradle file of your main module. The first one is the library itself.
The second dependency is the annotation processor that is responsible for generating support
classes. 
```gradle
dependencies {
    implementation 'com.armdroid:smartpreferences:1.0.2'
    annotationProcessor 'com.armdroid:smartpreferences-processor:1.0.2'
}
```
**Note**: Library fully supports `Kotlin`. More detailed, see at the bottom of md file.

To initialize the library add this code to `Application` class.
```java
public class App extends Application {

    @Override
    protected void onCreate() {
        super.onCreate();
        SmartPreferences.initialize(this);
    }
}
```
And that's it! You can start playing with `SharedPreferences`. Let's have a look at
full power of the library.

### Declaration ###
You can create connection with `SharedPreferences` by adding one of the following annotations 
to your field:
* `@IntPreferene`
* `@StringPreferene`
* `@BooleanPreferene`
* `@FloatPreferene`
* `@LongPreferene`

For example:
```java
public class MainActivity extends Activity {
    
    @IntPrefernce
    public int intFoo;
    
    @IntPreference(named = "preferenceKey", defaultValue = 5)
    protected static Integer boxedIntFoo;
}
```
As you see, you can bind not only primitives, but also their boxed classes. 
You can provide additional attributes to annotations:
* **named** - Defines the key that will be used to find value in `SharedPreferences`. If not
provided, library will take the name of the field (in the first example above, the key will
be `intFoo`).
* **defaultValue** - Defines the default value which will be used when reading in case key is not found in
`SharedPreferences`.


All kind of modifiers are allowed to be used with fields: **public, protected, static etc.** And even **private!**

### Use of private fields ###
You can use private fields with preference annotations, however **you have to provide** 
getter and setter for that field. Absence of getter/setter will cause compilation error.

```java
public class PojoClass {
    
    //this will cause compile time error with message:
    //"Field with @***Preference annotation can be private only if enclosing class 
    // has public or protected getter and setter for that field"
    @IntPrefernce
    private int intFoo;
}
```

```java
public class PojoClass {
    
    //this will compile fine
    @IntPrefernce
    private int intFoo;
    
    public void setIntFoo(Integer intFoo) {
        this.intFoo = intFoo;
    }
    
    protected int getIntFoo() {
        return intFoo;
    }
}
```

**Note** that access modifiers of getter/setter can be **public, protected** but not **private**.
Boxing/unboxing can be used here as well and everywhere in the app.

### Binding ###
After annotations are added, generated class instance should bind target class
so that `SharedPreferences` values can be read/written/observed. Here is how it is done:

```java
public class MainActivity extends Activity {
    
    @IntPrefernce
    public int intFoo;
    
    private MainActivityPreferences mBinding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //1. Reads values from preferences and keeps no binding
        MainActivityPreferences.read(this);
        
        //2. Writes values to preferences and keeps no binding
        MainActivityPreferences.write(this);
        
        //3. Just creates binding
        mBinding = MainActivityPreferences.bind(this);
        
        //4. Reads values from preferences and creates binding
        mBinding = MainActivityPreferences.readAndBind(this);
        
        //5. Writes values to preferences and creates binding
        mBinding = MainActivityPreferences.writeAndBind(this);
    }
}
```
For each class, where `@***Preference` annotations are used, `{CLASS_NAME}Preferences` class is generated which is responsible for operations 
with `SharedPreferences`. As you see, there are variety of options of binding:
1. Suitable for cases when values must be read from `SharedPreferences` only once.
Likely to be used in POJO classes.
2. Suitable for cases when values must be written to `SharedPreferences` only once.
3. Suitable for cases when more functionality is going to be used than just reading/writing.
Returned instance can be used to do lazy loading of fields, (un)registration of observers etc
 (more examples following).
4. Same as in the third case, but reads values from `SharedPreferences` as well.
5. Same as in the third case, but writes values to `SharedPreferences` as well

### @Transform ###
Along with `@***Preference` annotations, `@Tramform` annotation can be used to transform
the type associated with preference to the type associated with class field. For example:

```java
public abstract class GsonTransformer<To> implements PreferenceTransformer<String, To> {
 
    @Override
    public To convertRead(String from) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return new Gson().fromJson(from, genericType);
    }
    
    @Override
    public String convertWrite(To to) {
        Type superClass = getClass().getGenericSuperclass();
        Type genericType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return new Gson().toJson(genericType);
    }
}
```

```java
public class PojoClass {
    
    @StringPreference
    @Transform(using = GsonTransformer.class, typeParam1 = Custom.class)
    public Custom customFoo;
    
    class Custom {
        String field;
    }
    
    public PojoClass() {
        PojoClassPreferences.read(this);
    }
}    
```
So whenever reading `String` value associated with key `customFoo`, it will be transformed 
to object of type `Custom` and whenever writing to preferences, `Custom` will be
tranformed to `String`. More detailed:
* **using** -  Required attribute that provides transformation class. Transformation class
must be of type `class` (not `interface`, not `enum`) and must implement `PreferenceTransformer<String, To>`.
Class CAN be abstract, but should not have abstract methods (in fact, in the 
example above, class being abstract is essential).
* **typeParam1** - Optional type parameter for transformation class. Sometimes, type parameter is 
required for correct transformation, such as in the example above, however it is not possible to write 
`GsonTransformer<Custom>.class` due to type erasure.
For that, this attribute imitates the type parameter of transformation class, as if it was wrtitten `GsonTransformer<Custom>.class`.
* **typeParam2** - Optional second type parameter.

### @Observe ###
Now here is the fun part. We can listen to changes in `SharedPreferences`. All you need to do
is add `@Observe` annotation to field and tell binding to listen changes:

```java
public class MainActivity extends Activity {
    
    @IntPrefernce(named = "observablePref")
    @Observe
    public int intFoo;
    
    private MainActivityPreferences mBinding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = MainActivityPreferences.readAndBind(this); 
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mBinding.observeChanges();
    }
    
    @Override
    protected void onPause() {
        mBinding.stopObserveChanges();
        super.onPause();
    }
}
```
Once we call `mBinding.observeChanges()`, whenever the value of the key `observablePref` changes
in `SharedPreferences`, the value of our field `intFoo` will be updated.
<br><br>Once we call `mBinding.stopObserveChanges()`, changes will not be observed anymore.
<br><br>Looking good, but it can get better if we provide update callback.


### @Subscribe ###
Besides updating the field when changes occur, we can also detect when changes are
made and make appropriate actions. Let's have a look at previous example:

```java
public class MainActivity extends Activity {
    
    @IntPrefernce(named = "observablePref")
    @Observe
    public int intFoo;
    
    private MainActivityPreferences mBinding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = MainActivityPreferences.readAndBind(this); 
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mBinding.observeChanges();
    }
    
    @Override
    protected void onPause() {
        mBinding.stopObserveChanges();
        super.onPause();
    }
    
    @Subscribe(tag = "intFoo")
    public void onIntFooUpdated(int oldValue) {
        //here we have parameter oldValue as the value before update and 
        //field intFoo which has the updated value
    }
}
```
* **tag** - Required attribute that is used to bind listener method to field in the same class. 
Tag should match the name of the field (`intFoo` in the example above), otherwise binding will not occur.

There are also limitations for listener method. Method:
 * Should **NOT be private.**
 * Should be annotated with `@Subscribe` and appropriate tag.
 * Should have exactly one parameter of same type as the field with `@Observe` 
 annotation (in our case it is `int` or `Integer`).
 
### More methods in generated class ###
Besides the functionality above, there are some other useful methods in generated class.
Let's have a full example, where comments will explain their use:

```java
public class MainActivity extends Activity {
    
    @StringPreference(defaultValue = "{ someField: \"abc\" }")
    @Transform(using = GsonTransformer.class, typeParam1 = Custom.class)
    @Observe
    public Custom customFoo;
    
    @IntPreference
    protected int intFoo;
    
    @LongPreference(named = "longPreference")
    @Observe
    public static long longFoo;
    
    @FloatPreference(named = "floatPreference", defaultValue = 2f)
    protected static float floatFoo;
    
    @BooleanPreference
    private boolean booleanFoo;
    
    private MainActivityPreferences mBinder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinder = MainActivityPreferences.bind(this);
        
        //read the values from preferences and set to all annotated fields
        mBinder.readAll();
        
        //read only "booleanFoo" preference and set to field "booleanFoo"
        mBinder.readBooleanFoo();
        
        //read only "floatPreference" preference and set to field "floatFoo"
        mBinder.readFloatFoo();
        
        //this is the singleton instance, that does all the magic under the hood. It has
        //full functionality to work with SharedPreferences and can be used if available 
        //functionality is not enough.
        PreferenceRepository repository = mBinder.getPreferenceRepository();
        repository.clearPreferences();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //observe changes in preferences
        mBinder.observeChanges();
    }
    
    @Override
    protected void onPause() {
        //stop observing changes in preferences
        mBinder.stopObserveChanges();
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        //write the values of all annotated fields to preferences
        mBinder.writeAll();
        
        //write the value of "booleanFoo" field in preferences with key "booleanFoo"
        mBinder.writeBooleanFoo();
        
        //write the value of "floatFoo" field in preferences with key "floatPreference"
        mBinder.writeFloatFoo();
                
        //reset the values of fields annotated with @***Preference to java standard default values, 
        //i.e. boolean -> false, reference -> null etc.
        mBinder.setTypeDefaults();
        
        //destroy connection between MainActivity and MainActivityPreferences. 
        //Instance cannot be used anymore
        mBinder.unbind();
        super.onDestroy();
    }
    
    @Subscribe(tag = "customFoo")
    public void onUpdateCompleted(Custom oldValue) {
        // here we can detect changes of customFoo
    }
    
    @Subscribe(tag = "longFoo")
    public void onUpdateCompleted(long oldValue) {
        // here we can detect changes of longFoo
    }
    
    //getter required for private field booleanFoo
    public boolean getBooleanFoo() {
        return booleanFoo;
    }
    
    //setter required for private field booleanFoo
    protected void setBooleanFoo(Boolean booleanFoo) {
        this.booleanFoo = booleanFoo;
    }
}
```

### Kotlin support ###
Library is fully compatible with Kotlin. Before using the library, some changes are required
in main module gradle file:
 
```gradle
apply plugin: 'kotlin-kapt'

..
dependencies {
    implementation 'com.armdroid:smartpreferences:1.0.2'
    kapt 'com.armdroid:smartpreferences-processor:1.0.2'
}
```

```kotlin
class PojoClass {
    
    @IntPreference
    var intFoo1: Int = 0
        
    @IntPreference
    var intFoo2: Int? = null
    
    
    
    @StringPreference
    var stringFoo1: String? = null
    
    @StringPreference
    var stringFoo2: String = ""
        
    @StringPreference
    lateinit var stringFoo3: String
    
    
    
    @BooleanPreference
    @JvmField
    @Transform(using = CustomTransformer::class)
    var custom1: Custom1? = null
    
    @LongPreference
    @Transform(using = CustomTransformer::class)
    lateinit var custom2: Custom
}
```
In the example above, all are correct. So here is a sum up for fields:
 * **primitives** - All applications are correct.
 * **String without @Transform** - All applications are correct + lateinit.
 * **String and other objects with @Transform** - Field must either be annotated as
 `@JvmField` and be `nullable` or must be `lateinit`.
 
