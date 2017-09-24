package com.cout970.modeler.gui.react.tests

/**
 * Created by cout970 on 2017/09/23.
 */

interface RComponentSpec<out C : RComponent<P, S>, P : Any, S : Any> {

    fun build(props: P): C
}

operator fun <C : RComponent<P, S>, P : Any, S : Any> RComponentSpec<C, P, S>.invoke(
        init: () -> P): RComponentWrapper<C, P, S> {
    return RComponentWrapper(init(), this)
}