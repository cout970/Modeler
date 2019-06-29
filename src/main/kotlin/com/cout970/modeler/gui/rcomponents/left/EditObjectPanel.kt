package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.rcomponents.TinyFloatInput
import com.cout970.modeler.gui.rcomponents.TinyFloatInputProps
import com.cout970.modeler.gui.rcomponents.TransformationInput
import com.cout970.modeler.gui.rcomponents.TransformationInputProps
import com.cout970.modeler.util.disableInput
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.vec3Of
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.align.HorizontalAlign

class EditObjectPanel : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditCubePanel") {
        val pair = getObject()

        style {
            classes("left_panel_group", "edit_cube")
            height = if (state.on && pair != null) 557f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 16f)
        }

        val trans = pair?.second?.transformation ?: TRTSTransformation.IDENTITY
        val tex = (pair?.second as? IObjectCube)?.textureOffset ?: Vector2.ORIGIN
        val scale = (pair?.second as? IObjectCube)?.textureSize ?: Vector2.ORIGIN
        val text = pair?.second?.name ?: ""

        child(GroupTitle::class.java, GroupTitleProps("Edit Cube", state.on) { setState { copy(on = !on) } })

        comp(StringInput("model.obj.change.name")) {
            style {
                textState.horizontalAlign = HorizontalAlign.CENTER
                textState.text = text
                textState.fontSize = 24f
                sizeY = 32f
            }

            postMount {
                marginX(5f)

                this as StringInput

                if (pair == null) {
                    isEditable = false
                    isEnabled = false
                    disableInput()
                }
            }
        }

        child(TransformationInput::class, TransformationInputProps(
            usecase = "update.object.transform",
            transformation = trans,
            enable = pair != null
        ))

        textureControls(vec3Of(tex.xf, tex.yf, scale.xf), pair != null && pair.second is IObjectCube)

        onCmd("updateModel") { rerender() }
        onCmd("updateSelection") { rerender() }
    }

    private fun RBuilder.textureControls(scale: IVector3, enable: Boolean) {
        div("Texture") {
            style {
                height = 92f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            val line = 0.4f

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 5f)
                }

                label("Texture X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Texture Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Scale") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - line)
                    fillY()
                    floatTop(6f, 5f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.xf },
                        setter = { cmd("tex.x", it, enable) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.yf },
                        setter = { cmd("tex.y", it, enable) },
                        enabled = enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.zf },
                        setter = { cmd("tex.scale", it, enable) },
                        enabled = enable
                ))
            }
        }
    }

    fun cmd(txt: String, value: Float, enable: Boolean) {
        if (enable) {
            Panel().apply {
                metadata += mapOf("command" to txt)
                metadata += "offset" to 0f
                metadata += "content" to value.toString()
                dispatch("update.object.transform")
            }
        }
    }

    fun isSelectingOne(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        model.getSelectedObjects(new).firstOrNull() ?: return false
        return true
    }

    fun getObject(): Pair<IObjectRef, IObject>? {
        val sel = props.access.modelSelection.getOrNull() ?: return null
        if (!isSelectingOne(props.access.model, sel)) return null
        val objRef = sel.objects.first()
        val obj = props.access.model.getObject(objRef)

        return objRef to obj
    }
}