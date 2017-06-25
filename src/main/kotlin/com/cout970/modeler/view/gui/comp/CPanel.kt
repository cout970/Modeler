package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/03/12.
 */
open class CPanel(
        x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 10f
) : Panel(Vector2f(x, y), Vector2f(width, height)) {

    val id = lastID++

    init {
        backgroundColor = Config.colorPalette.lightColor.toColor()
    }

    companion object {
        private var lastID = 0
    }

    fun setBorderless() {
        getBorder().isEnabled = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CPanel) return false
        if (!super.equals(other)) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + id
        return result
    }
}