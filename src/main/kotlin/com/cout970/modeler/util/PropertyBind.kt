package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/01/28.
 */

interface IPropertyBind<T> {
    fun set(value: T)
    fun get(): T
}

class BooleanProperty(defaultValue: Boolean = false) : IPropertyBind<Boolean> {

    private var value: Boolean = defaultValue

    override fun set(value: Boolean) {
        this.value = value
    }

    override fun get(): Boolean = value
}