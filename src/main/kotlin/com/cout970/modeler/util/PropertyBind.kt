package com.cout970.modeler.util

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

/**
 * Created by cout970 on 2017/01/28.
 */

interface IPropertyBind<T> {
    fun set(value: T)
    fun get(): T
}

interface IGuiCmdRunner {
    fun runGuiCommand(cmd: String, args: Map<String, Any> = emptyMap())
}

object PropertyManager {

    private val properties = mutableMapOf<String, IPropertyBind<*>>()

    fun findProperty(name: String) = properties[name]

    fun setupProperties(runner: IGuiCmdRunner) {
        properties.values.forEach { if (it is GuiProperty) it.runner = runner }
    }

    fun registerProperty(name: String, prop: IPropertyBind<*>) {
        properties[name] = prop
    }
}

class GuiProperty<T>(var value: T, val name: String) : IPropertyBind<T> {

    lateinit var runner: IGuiCmdRunner

    init {
        PropertyManager.registerProperty(name, this)
    }

    override fun set(value: T) {
        this.value = value
        runner.runGuiCommand("update$name", mapOf("value" to (value as Any)))
    }

    override fun get(): T = value

    operator fun getValue(obj: Any, property: KProperty<*>): T = get()

    operator fun setValue(obj: Any, property: KProperty<*>, t: T) = set(t)
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