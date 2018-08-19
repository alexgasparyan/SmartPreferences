package com.armdroid.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.armdroid.smartpreferences.BooleanPreference;
import com.armdroid.smartpreferences.FloatPreference;
import com.armdroid.smartpreferences.IntPreference;
import com.armdroid.smartpreferences.LongPreference;
import com.armdroid.smartpreferences.Observe;
import com.armdroid.smartpreferences.PreferenceRepository;
import com.armdroid.smartpreferences.StringPreference;
import com.armdroid.smartpreferences.Subscribe;
import com.armdroid.smartpreferences.Transform;

public class MainActivity extends AppCompatActivity {

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

        //read only "booleanFoo" preference
        mBinder.readBooleanFoo();

        //read only "floatPreference" preference and set to field floatFoo
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

        //write the value of "booleanFoo" field in preferences
        mBinder.writeBooleanFoo();

        //reset the values of fields annotated with @***Preference to java standard default values, i.e. boolean -> false, reference -> null etc.
        mBinder.setTypeDefaults();

        //write the value of "floatFoo" field in preferences with key "floatPreference"
        mBinder.readFloatFoo();

        //destroy connection between MainActivity and MainActivityPreferences
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
