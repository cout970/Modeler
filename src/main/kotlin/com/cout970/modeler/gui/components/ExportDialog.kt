package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.gui.leguicomp.background
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.util.toPointerBuffer
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.posX
import com.cout970.reactive.dsl.posY
import com.cout970.reactive.dsl.width
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.lwjgl.PointerBuffer

/**
 * Created by cout970 on 2017/09/30.
 */
class ExportDialog : RComponent<ExportDialog.Props, ExportDialog.State>() {

    init {
        state = State("", 0)
    }

    override fun build(ctx: RBuilder): Component = panel {
        size = ctx.parentSize.toJoml2f()
        style.background.color = Vector4f(1f, 1f, 1f, 0.05f)

        +panel {
            background{ darkestColor }
            style.border = SimpleLineBorder(Config.colorPalette.greyColor.toColor(), 2f)
            width = 460f
            height = 240f
            posX = (ctx.parentSize.xf - width) / 2f
            posY = (ctx.parentSize.yf - height) / 2f


        }
    }


    class Props(val popup: Popup)
    data class State(val text: String, val selection: Int, var forceUpdate: Boolean = false)

    override fun shouldComponentUpdate(nextProps: Props, nextState: State): Boolean {
        return state.selection != nextState.selection || nextState.forceUpdate
    }

    companion object : RComponentSpec<ExportDialog, Props, State> {
        private val options = listOf("Obj (*.obj)", "MCX (*.mcx)")
        private val exportExtensionsObj = listOf("*.obj").toPointerBuffer()
        private val exportExtensionsMcx = listOf("*.mcx").toPointerBuffer()

        private fun getExportFileExtensions(format: ExportFormat): PointerBuffer = when (format) {
            ExportFormat.OBJ -> exportExtensionsObj
            ExportFormat.MCX -> exportExtensionsMcx
        }
    }

}