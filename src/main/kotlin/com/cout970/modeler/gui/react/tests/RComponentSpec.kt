package com.cout970.modeler.gui.react.tests

import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/23.
 */

interface RComponentSpec<out C : RComponent<P, S>, P : Any, S : Any> {

    val defaultProps: P

    fun build(props: P): C
}

operator fun <C : RComponent<P, S>, P : Any, S : Any> RComponentSpec<C, P, S>.invoke(init: (P) -> P): Component {
    return build(init(defaultProps))
}