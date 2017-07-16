package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.GuiResources
import org.joml.Vector2f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollablePanel

/**
 * Created by cout970 on 2017/07/13.
 */
open class CVerticalPanel(
        x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 10f
) : ScrollablePanel<Panel<*>>(Vector2f(x, y), Vector2f(width, height)) {

    val id = lastID++


    init {
        border = SimpleLineBorder(Config.colorPalette.borderColor.toColor(), 0.5f)
        backgroundColor = ColorConstants.transparent()
        container.backgroundColor = Config.colorPalette.lightColor.toColor()
        horizontalScrollBar.hide()
        verticalScrollBar.backgroundColor = ColorConstants.transparent()
        verticalScrollBar.arrowColor = Config.colorPalette.darkColor.toColor()
        verticalScrollBar.scrollColor = Config.colorPalette.darkColor.toColor()
        verticalScrollBar.isArrowsEnabled = false
    }

    companion object {
        private var lastID = 0
    }

    override fun resize() {
        super.resize()
//        val containerSize = Vector2f(container.size)
//        val viewportSize = Vector2f(size)
//        val verticalRange = verticalScrollBar.maxValue - verticalScrollBar.minValue
//        verticalScrollBar.visibleAmount = if (containerSize.y >= viewportSize.y) verticalRange * viewportSize.y / containerSize.y else verticalRange
    }

    open fun loadResources(resources: GuiResources) {
        childs.filterIsInstance<CPanel>().forEach { it.loadResources(resources) }
    }

    fun setBorderless() {
        border.isEnabled = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CVerticalPanel) return false
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