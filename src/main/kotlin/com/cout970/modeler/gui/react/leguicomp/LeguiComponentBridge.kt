package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.IReactComponent
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */

class LeguiComponentBridge<P, S, out C : IReactComponent<P, S>>(
        val factory: IComponentFactory<P, S, C>,
        val props: P
) : Component()

class LeguiComponentWrapper<out P, S, out C : IReactComponent<P, S>>(
        val component: C
) : Component()