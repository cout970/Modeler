package com.cout970.modeler.gui.react.tests

/**
 * Created by cout970 on 2017/09/23.
 */

interface RComponentSpec<out C : RComponent<P, S>, P : Any, S : Any> {

    val defaultProps: P

    fun build(props: P): C
}