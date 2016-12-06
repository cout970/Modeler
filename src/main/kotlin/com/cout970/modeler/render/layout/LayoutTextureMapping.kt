package com.cout970.modeler.render.layout

import com.cout970.modeler.render.RenderManager
import com.cout970.modeler.render.controller.IViewController
import com.cout970.modeler.render.controller.LambdaViewController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/04.
 */
class LayoutTextureMapping(renderManager: RenderManager) : Layout(renderManager) {

    override val contentPanel: Panel = Panel().apply { addComponent(Button(10f, 10f, 500f, 50f)) }
    override val viewController: IViewController = LambdaViewController { }

    override fun renderExtras() {
    }
}