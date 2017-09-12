package com.cout970.modeler.gui.react.scalable

import com.cout970.modeler.gui.react.IScalable
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/09/07.
 */

class FillWindow : IScalable {

    override fun updateScale(comp: Component, parent: Container<*>, windowSize: IVector2) {
        comp.size = windowSize.toJoml2f()
        comp.position = Vector2.ORIGIN.toJoml2f()
    }
}

class FillParent : IScalable {

    override fun updateScale(comp: Component, parent: Container<*>, windowSize: IVector2) {
        comp.size = Vector2f(parent.size)
        comp.position = Vector2.ORIGIN.toJoml2f()
    }
}

class FixedXFillY(val xPos: Float, val xSize: Float) : IScalable {

    override fun updateScale(comp: Component, parent: Container<*>, windowSize: IVector2) {
        comp.size = vec2Of(xSize, parent.size.y).toJoml2f()
        comp.position = vec2Of(xPos, 0f).toJoml2f()
    }
}

class FixedYFillX(val yPos: Float, val ySize: Float) : IScalable {

    override fun updateScale(comp: Component, parent: Container<*>, windowSize: IVector2) {
        comp.size = vec2Of(parent.size.x, ySize).toJoml2f()
        comp.position = vec2Of(0f, yPos).toJoml2f()
    }
}