package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.rcomponents.TinyFloatInput
import com.cout970.modeler.gui.rcomponents.TinyFloatInputProps
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon

data class EditGridsProps(val gridLines: GridLines) : RProps

class EditGrids : RComponent<EditGridsProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(false)

    override fun RBuilder.render() = div("EditGrids") {
        style {
            classes("left_panel_group", "config_grids")
            height = if (state.on) 355f else 24f
        }

        postMount {
            marginX(5f)
            alignAsColumn(5f, 20f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Config Grids", state.on) { setState { copy(on = !on) } })

        div("Checkboxes") {
            style {
                classes("config_grids_checkboxes")
                height = 24f * 3 + 15f
                width = 160f
            }

            postMount {
                centerX()
            }

            +CheckBox("Enable Plane X", 0f, 0f, 160f, 24f).apply {
                defaultTextColor()
                paddingLeft(4f)
                isChecked = props.gridLines.enableXPlane
                classes("checkbox")
                if (isChecked) classes("checkbox_active")

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableXPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Y", 0f, 24f + 5f, 160f, 24f).apply {
                defaultTextColor()
                paddingLeft(4f)
                isChecked = props.gridLines.enableYPlane
                classes("checkbox")
                if (isChecked) classes("checkbox_active")

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableYPlane = isChecked; rerender() }
            }

            +CheckBox("Enable Plane Z", 0f, 48f + 10f, 160f, 24f).apply {
                defaultTextColor()
                paddingLeft(4f)
                isChecked = props.gridLines.enableZPlane
                classes("checkbox")
                if (isChecked) classes("checkbox_active")

                configIcon(iconChecked as CharIcon)
                configIcon(iconUnchecked as CharIcon)

                onClick { props.gridLines.enableZPlane = isChecked; rerender() }
            }
        }

        div("Offset") {
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

                label("Offset X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Offset Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Offset Z") {
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

                val offset = props.gridLines.gridOffset

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { offset.xf },
                        setter = { cmd("x", it, "grid.offset.change") }
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { offset.yf },
                        setter = { cmd("y", it, "grid.offset.change") }
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { offset.zf },
                        setter = { cmd("z", it, "grid.offset.change") }
                ))
            }
        }

        div("Size") {
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

                label("Size X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Size Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Size Z") {
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

                val offset = props.gridLines.gridSize

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { offset.xf },
                        setter = { cmd("x", it, "grid.size.change") }
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { offset.yf },
                        setter = { cmd("y", it, "grid.size.change") }
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { offset.zf },
                        setter = { cmd("z", it, "grid.size.change") }
                ))
            }
        }
    }

    private fun CheckBox.configIcon(icon: CharIcon) {
        icon.color = Config.colorPalette.bright4.toColor()
        icon.size = Vector2f(24f, 24f)
        icon.position = Vector2f(size.x - icon.size.x, 0f)
        icon.horizontalAlign = HorizontalAlign.CENTER
    }

    fun cmd(txt: String, value: Float, usecase: String) {
        Panel().apply {
            metadata += "axis" to txt
            metadata += "offset" to 0f
            metadata += "listener" to { rerender() }
            metadata += "content" to value.toString()
            dispatch(usecase)
        }
    }
}