package com.cout970.modeler.gui.react.scalable

import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */

interface IScalable {

    fun updateScale(comp: Component, parentSize: IVector2)
}

object FillParent : IScalable {

    override fun updateScale(comp: Component, parentSize: IVector2) {
        comp.size = parentSize.toJoml2f()
    }
}

class FixedXFillY(val xSize: Float) : IScalable {

    override fun updateScale(comp: Component, parentSize: IVector2) {
        comp.size = vec2Of(xSize, parentSize.y).toJoml2f()
    }
}

class FixedYFillX(val ySize: Float) : IScalable {

    override fun updateScale(comp: Component, parentSize: IVector2) {
        comp.size = vec2Of(parentSize.x, ySize).toJoml2f()
    }
}