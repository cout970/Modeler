package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.config.colorOf
import com.cout970.modeler.core.config.colorToHex
import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.material.TexturedMaterial
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.dialogs.FileDialogs
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toResourcePath
import com.cout970.modeler.util.toVector3
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.DivBuilder
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import com.cout970.vector.api.IVector3
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.color.ColorConstants
import java.io.File
import java.net.URI

data class EditTextureProps(val returnFunc: (Any?) -> Unit, val guiState: GuiState, val metadata: Map<String, Any>) : RProps

data class EditTextureState(
        var textureName: String? = null,
        val texturePath: ResourcePath? = null,
        val color: IVector3? = null
) : RState

class EditTexture : RComponent<EditTextureProps, EditTextureState>() {

    inline val material: IMaterial get() = props.metadata["material"] as IMaterial

    override fun getInitialState() = EditTextureState()

    override fun RBuilder.render() = div("EditTexture") {
        style {
            width = 460f
            height = 170f
            classes("popup_back")
        }

        postMount {
            center()
        }

        val material = material

        // first line
        +FixedLabel("Config Material", 0f, 8f, 460f, 24f).apply {
            style.fontSize = 22f
        }

        //second line
        +FixedLabel("Name", 25f, 50f, 400f, 24f).apply {
            style.fontSize = 20f
            style.horizontalAlign = HorizontalAlign.LEFT
        }

        comp(TextInput(state.textureName ?: material.name, 90f, 50f, 250f, 24f)) {
            style {
                horizontalAlign = HorizontalAlign.LEFT
                fontSize(20f)
                if (material is MaterialNone) {
                    isEditable = false
                    isEnabled = false
                    style.background.color = ColorConstants.black()
                    focusedStyle.background.color = style.background.color
                    pressedStyle.background.color = style.background.color
                    hoveredStyle.background.color = style.background.color
                }
            }

            on<TextInputContentChangeEvent<TextInput>> {
                state.textureName = it.newValue
            }
        }

        //third line
        if (material is TexturedMaterial) {
            texturedMaterial(material)
        } else if (material is ColoredMaterial) {
            coloredMaterial(material)
        }

        //fifth line
        +TextButton("", "Accept", 270f, 130f, 80f, 24f).apply {
            onClick {
                if (state.textureName == null && state.texturePath == null && state.color == null) {
                    props.returnFunc(null)
                } else {
                    when (material) {
                        is TexturedMaterial -> {
                            val newMaterial = material.copy(
                                    name = state.textureName ?: material.name,
                                    path = state.texturePath ?: material.path
                            )
                            props.returnFunc(newMaterial)
                        }
                        is ColoredMaterial -> {
                            val newMaterial = material.copy(
                                    name = state.textureName ?: material.name,
                                    color = state.color ?: material.color
                            )
                            props.returnFunc(newMaterial)
                        }
                        else -> props.returnFunc(null)
                    }
                }
            }
        }

        +TextButton("", "Cancel", 360f, 130f, 80f, 24f).apply {
            onClick {
                props.returnFunc(null)
            }
        }
    }

    fun DivBuilder.coloredMaterial(material: ColoredMaterial) {
        +FixedLabel("Color", 25f, 90f, 400f, 24f).apply {
            style.fontSize = 20f
            style.horizontalAlign = HorizontalAlign.LEFT
        }

        val color = state.color ?: material.color

        comp(TextInput(colorToHex(color), 90f, 90f, 250f, 24f)) {
            on<TextInputContentChangeEvent<TextInput>> {
                if (it.newValue.length == 6) {
                    val newColor = colorOf(it.newValue).toIVector().toVector3()
                    if (newColor != color) {
                        setState { copy(color = newColor) }
                    }
                }
            }
        }

        div {
            style {
                posX = 360f
                posY = 90f
                sizeX = 80f
                sizeY = 24f
                style.background.setColor(color.xf, color.yf, color.zf, 1.0f)
            }
        }
    }

    fun DivBuilder.texturedMaterial(material: TexturedMaterial) {
        +FixedLabel("Path", 25f, 90f, 400f, 24f).apply {
            style.fontSize = 20f
            style.horizontalAlign = HorizontalAlign.LEFT
        }

        val path = state.texturePath ?: material.path
        comp(TextInput(path.toString(), 90f, 90f, 250f, 24f)) {
            on<TextInputContentChangeEvent<TextInput>> {
                try {
                    val newPath = ResourcePath(URI.create(it.newValue))
                    if (newPath != path) {
                        setState { copy(texturePath = newPath) }
                    }
                } catch (e: Exception) {
                    val newPath = File(it.newValue).toResourcePath()
                    if (newPath != path) {
                        setState { copy(texturePath = newPath) }
                    }
                }
            }
        }

        comp(TextButton("", "Select", 360f, 90f, 80f, 24f)) {
            onRelease {
                val file = FileDialogs.openFile(
                        title = "Load Texture",
                        description = "PNG texture (*.png)",
                        filters = listOf("*.png")
                )

                if (file != null) {
                    val newPath = File(file).toResourcePath()
                    if (newPath != path) {
                        setState { copy(texturePath = newPath) }
                    }
                }
            }
        }
    }
}