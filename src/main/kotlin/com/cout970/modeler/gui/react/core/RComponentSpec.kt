package com.cout970.modeler.gui.react.core

import kotlin.reflect.full.createInstance

/**
 * Created by cout970 on 2017/09/23.
 */

interface RComponentSpec<out C : RComponent<P, S>, P : Any, S : Any>

inline operator fun <reified C : RComponent<P, S>, reified P : Any, S : Any> RComponentSpec<C, P, S>.invoke(
        func: () -> P
): RComponentWrapper<C, P, S> {

    return RComponentWrapper(func(), { C::class.createInstance() })
}