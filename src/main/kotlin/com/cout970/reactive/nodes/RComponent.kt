package com.cout970.reactive.nodes

import com.cout970.reactive.core.*
import org.liquidengine.legui.component.Component
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

class RComponentDescriptor<P : RProps, T : RComponent<P, *>>(val clazz: Class<T>, val props: P) : RDescriptor {

    init {
        require(!Modifier.isAbstract(clazz.modifiers)) {
            "Invalid class $clazz, It must not be abstract"
        }
    }

    override fun mapToComponent(): Component = throw IllegalStateException("This descriptor needs a special treatment!")
}

fun <P : RProps, T : RComponent<P, *>> RBuilder.child(clazz: KClass<T>, props: P, key: String? = null) =
    child(clazz.java, props, key)

fun <T : RComponent<EmptyProps, *>> RBuilder.child(clazz: KClass<T>, key: String? = null) =
    child(clazz.java, EmptyProps, key)


fun <P : RProps, T : RComponent<P, *>> RBuilder.child(clazz: Class<T>, props: P, key: String? = null) =
    +RNode(key, RComponentDescriptor(clazz, props))

