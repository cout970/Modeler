package com.cout970.modeler.gui.components

import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.views.VisibleElements
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import org.liquidengine.legui.component.Component

class BottomPanel : RComponent<BottomPanel.Props, Unit>() {

    override fun build(ctx: RBuilder): Component = panel {
        val left = if (props.visibleElements.left) 288f else 0f
        val right = if (props.visibleElements.right) 190f else 0f
        val totalWidth = ctx.parentSize.xf - left - right
        width = totalWidth
        height = 200f
        posX = left
        posY = ctx.parentSize.yf - 200f

        background { darkColor }
        setBorderless()

        if (!props.visibleElements.bottom) hide()

        +panel {
            height = 32f
            width = totalWidth

            background { darkestColor }
            setBorderless()

            +panel {
                width = 32f * 6
                height = 32f
                setTransparent()
                setBorderless()

                +IconButton("animation.seek.start", "seek_start", 3f + 0f, 3f, 26f, 26f)
                +IconButton("animation.prev.keyframe", "prev_keyframe", 3f + 32f, 3f, 26f, 26f)
                +IconButton("animation.next.keyframe", "next_keyframe", 3f + 128f, 3f, 26f, 26f)
                +IconButton("animation.seek.end", "seek_end", 3f + 160f, 3f, 26f, 26f)
                if (true) {
                    +IconButton("animation.play.reversed", "play_reversed", 3f + 64f, 3f, 26f, 26f)
                    +IconButton("animation.play.normal", "play_normal", 3f + 96f, 3f, 26f, 26f)
                } else {
                    +IconButton("animation.play.pause", "play_pause", 3f + 64f, 3f, 58f, 26f)
                }
            }

            +panel {
                posY = 32f
                height = 200f - 32f
                width = totalWidth
                setTransparent()
                var index = 0

                props.modelAccessor.animation.operations.forEach { key, op ->
                    +panel {
                        posY = index * 32f
                        width = totalWidth
                        height = 32f
                        background { greyColor }
                        +FixedLabel(op.id.toString(), width = 150f)
                    }
                    index++
                }
            }
        }
    }

    data class Props(val visibleElements: VisibleElements, val modelAccessor: IModelAccessor)

    companion object : RComponentSpec<BottomPanel, BottomPanel.Props, Unit>
}