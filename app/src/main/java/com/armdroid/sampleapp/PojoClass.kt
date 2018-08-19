package com.armdroid.sampleapp

import com.armdroid.smartpreferences.*

class PojoClass {

    @IntPreference
    var intFoo1: Int? = null

    @IntPreference
    @Observe(tag="abs")
    var intFoo2: Int = 0

    @StringPreference
    var abc: String? = null

    @StringPreference
    var ggg: String = ""

    @StringPreference
    lateinit var def: String

    @StringPreference
    @JvmField
    @Transform(using = GsonTransformer::class, typeParam1 = Custom::class)
    var custom1: Custom? = null

    @StringPreference
    @Transform(using = GsonTransformer::class, typeParam1 = Custom::class)
    lateinit var custom2: Custom

    @Subscribe(tag="abs")
    public fun blabla(abc: Int) {

    }
}