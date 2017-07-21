package com.cout970.modeler.util

import kotlin.reflect.KMutableProperty

/**
 * Created by cout970 on 2017/01/28.
 */

interface IPropertyBind<T> {
    fun set(value: T)
    fun get(): T
}

class BooleanPropertyWrapper(val prop: KMutableProperty<Boolean>) : IPropertyBind<Boolean> {

    override fun set(value: Boolean) {
        prop.setter.call(value)
    }

    override fun get(): Boolean = prop.getter.call()
}

class BooleanProperty(defaultValue: Boolean = false) : IPropertyBind<Boolean> {

    private var value: Boolean = defaultValue

    override fun set(value: Boolean) {
        this.value = value
    }

    override fun get(): Boolean = value
}