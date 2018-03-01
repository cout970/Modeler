package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.controller.usecases.scriptEngine
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.hack
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.util.child
import com.cout970.modeler.util.disableInput
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toJoml2f
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.Viewport
import org.liquidengine.legui.component.event.scrollbar.ScrollBarChangeValueEvent
import org.liquidengine.legui.component.optional.Orientation
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.CursorEnterEvent
import org.liquidengine.legui.event.ScrollEvent
import org.liquidengine.legui.system.context.Context
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


data class FloatInputProps(
        val getter: () -> Float,
        val command: String,
        val metadata: Map<String, Any>,
        val enabled: Boolean,
        val pos: IVector2
) : RProps

private val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

class FloatInput : RStatelessComponent<FloatInputProps>() {

    override fun RBuilder.render() = div("FloatInput") {
        style {
            transparent()
            borderless()
            sizeX = 75f
            sizeY = 70f
            position = props.pos.toJoml2f()
        }

        postMount {
            if (!props.enabled) {
                disableInput()
            }
        }

        comp(StringInput(null)) {
            style {
                defaultTextColor()
                background { greyColor }
                horizontalAlign = HorizontalAlign.CENTER
                fontSize = 24f
                posX = 0f
                posY = 16f
                sizeX = 75f
                sizeY = 40f
                text = formatter.format(props.getter())

                onLoseFocus = {
                    dispatch(this, 0f, text)
                }
                onEnterPress = {
                    dispatch(this, 0f, text)
                }
            }

            on<ScrollEvent<StringInput>> {
                val off = it.yoffset.toFloat()
                dispatch(it.targetComponent, off, it.targetComponent.text)
            }
        }

        comp(IconButton()) {
            style {
                sizeX = 75f
                sizeY = 16f
                icon = "button_up"
                background { lightDarkColor }
                onRelease {
                    dispatch(this, 1f, formatter.format(props.getter()))
                }
            }
        }

        comp(IconButton()) {
            style {
                posY = 56f
                sizeX = 75f
                sizeY = 16f
                icon = "button_down"
                background { lightDarkColor }
                onRelease {
                    dispatch(this, -1f, formatter.format(props.getter()))
                }
            }
        }
    }

    var lastTick = 0L

    fun dispatch(comp: Component, offset: Float, content: String) {
        // this avoid generating a million task doing the same thing
        if (lastTick == Loop.currentTick) return
        lastTick = Loop.currentTick

        comp.apply {
            metadata += props.metadata
            metadata += "offset" to offset
            metadata += "content" to content
            dispatch(props.command)
        }
    }
}

data class ScrollPanelProps(
        val postMount: Component.() -> Unit = {},
        val vertical: ScrollBar.() -> Unit = {},
        val horizontal: ScrollBar.() -> Unit = {},
        val container: DivBuilder.() -> Unit,
        val style: Panel.() -> Unit
) : RProps

data class ScrollPanelState(
        val scrollX: Float,
        val scrollY: Float
) : RState

class ScrollPanel : RComponent<ScrollPanelProps, ScrollPanelState>() {

    override fun getInitialState() = ScrollPanelState(0f, 0f)

    private var lastVerticalScrollBar: ScrollBar? = null
    private var lastHorizontalScrollBar: ScrollBar? = null
    private var leguiContext: Context? = null

    override fun RBuilder.render() = div("ScrollPanel") {

        style {
            props.style(this)
        }

        postMount {
            val vertical = child("VerticalScroll") as ScrollBar
            val horizontal = child("HorizontalScroll") as ScrollBar
            val container = child("Container") as Panel

            // Default Bar sizes, this must go before calling 'vertical' or 'horizontal' so the user can change the size
            horizontal.sizeX = 8f
            vertical.sizeY = 8f

            props.postMount.invoke(this)
            props.vertical.invoke(vertical)
            props.horizontal.invoke(horizontal)

            val size = run {
                val realSizeX = container.childComponents.map { it.posX + it.sizeX }.max() ?: 0f
                val realSizeY = container.childComponents.map { it.posY + it.sizeY }.max() ?: 0f
                Vector2f(realSizeX, realSizeY)
            }

            vertical.viewport = FakeViewport(container.size, size)
            horizontal.viewport = FakeViewport(container.size, size)
            offsetContent(this, vertical, horizontal, container)
        }

        div("Container") {

            onScroll {
                val parent = it.targetComponent.parent ?: return@onScroll
                val scroll = parent.child("VerticalScroll") as? ScrollBar ?: return@onScroll

                if (scroll.isEnabled) {
                    scroll.listenerMap.getListeners(ScrollEvent::class.java).forEach { list ->
                        list.process(ScrollEvent(scroll, it.context, it.frame, it.xoffset, it.yoffset))
                    }
                }
            }

            props.container(this)
        }

        comp(ScrollBar(), "VerticalScroll") {
            style {
                orientation = Orientation.VERTICAL
                isTabFocusable = false
                borderless()
                visibleAmount = maxValue
            }

            on<ScrollBarChangeValueEvent<ScrollBar>> {
                setState { copy(scrollY = it.newValue) }
            }

            on<CursorEnterEvent<ScrollBar>> {
                leguiContext = it.context
            }

            postMount {
                val hSize = parent.child("HorizontalScroll")!!.let { if (it.isEnabled) it.sizeY else 0f }
                posX = parent.width - sizeX
                posY = 0f
                sizeY = parent.height - hSize

                this as ScrollBar

                curValue = state.scrollY
                hack {
                    isScrolling = lastVerticalScrollBar?.isScrolling ?: false
                    lastVerticalScrollBar?.animation?.stopAnimation()

                    leguiContext?.let {
                        if (lastVerticalScrollBar == it.focusedGui)
                            it.focusedGui = this
                    }
                    lastVerticalScrollBar = this
                }
            }
        }

        comp(ScrollBar(), "HorizontalScroll") {
            style {
                orientation = Orientation.HORIZONTAL
                isTabFocusable = false
                borderless()
                visibleAmount = maxValue
            }

            on<ScrollBarChangeValueEvent<ScrollBar>> {
                setState { copy(scrollX = it.newValue) }
            }

            on<CursorEnterEvent<ScrollBar>> {
                leguiContext = it.context
            }

            postMount {
                val vSize = parent.child("VerticalScroll")!!.let { if (it.isEnabled) it.sizeX else 0f }
                posX = 0f
                posY = parent.height - sizeY
                sizeX = parent.width - vSize

                this as ScrollBar

                curValue = state.scrollX
                hack {
                    isScrolling = lastHorizontalScrollBar?.isScrolling ?: false
                    lastHorizontalScrollBar?.animation?.stopAnimation()

                    leguiContext?.let {
                        if (lastHorizontalScrollBar == it.focusedGui)
                            it.focusedGui = this
                    }
                    lastHorizontalScrollBar = this
                }
            }
        }
    }

    class FakeViewport(val size: Vector2f, val viewSize: Vector2f) : Viewport {

        override fun getViewportViewSize() = Vector2f(viewSize)

        override fun getViewportSize() = Vector2f(size)
    }

    fun offsetContent(parent: Component, vertical: ScrollBar, horizontal: ScrollBar, container: Panel) {
        val realSizeX = container.childComponents.map { it.posX + it.sizeX }.max() ?: 0f
        val realSizeY = container.childComponents.map { it.posY + it.sizeY }.max() ?: 0f

        container.let {
            it.sizeX = parent.sizeX - vertical.let { if (it.isEnabled) it.sizeX else 0f }
            it.sizeY = parent.sizeY - horizontal.let { if (it.isEnabled) it.sizeY else 0f }
        }

        val offsetX = if (realSizeX > container.sizeX) -(realSizeX - container.sizeX) * (state.scrollX / vertical.maxValue) else 0f
        val offsetY = if (realSizeY > container.sizeY) -(realSizeY - container.sizeY) * (state.scrollY / horizontal.maxValue) else 0f

        container.forEach {
            it.posX += offsetX
            it.posY += offsetY
        }
    }
}

class ScrollPanelBuilder {

    private var postMount: Component.() -> Unit = {}
    private var vertical: ScrollBar.() -> Unit = {}
    private var horizontal: ScrollBar.() -> Unit = {}
    private var container: DivBuilder.() -> Unit = {}
    private var style: Panel.() -> Unit = {}

    fun postMount(func: Component.() -> Unit) {
        postMount = func
    }

    fun style(func: Panel.() -> Unit) {
        style = func
    }

    fun container(func: DivBuilder.() -> Unit) {
        container = func
    }

    fun verticalScroll(func: ScrollBar.() -> Unit) {
        vertical = func
    }

    fun horizontalScroll(func: ScrollBar.() -> Unit) {
        horizontal = func
    }

    fun buildProps() = ScrollPanelProps(postMount, vertical, horizontal, container, style)
}

fun RBuilder.scrollPanel(key: String? = null, block: ScrollPanelBuilder.() -> Unit = {}) =
        child(ScrollPanel::class, ScrollPanelBuilder().apply(block).buildProps())


data class TinyFloatInputProps(val pos: Vector2f, val getter: () -> Float, val setter: (Float) -> Unit) : RProps

class TinyFloatInput : RStatelessComponent<TinyFloatInputProps>() {

    override fun RBuilder.render() = div("TinyFloatInput") {
        style {
            background { darkestColor }
            borderless()
            rectCorners()
            position.set(props.pos)
            width = 120f
            height = 24f
        }

        +IconButton("", "button_left", 0f, 0f, 24f, 24f).apply {
            borderless()
            onClick {
                value -= 0.1f
                rerender()
            }
        }

        +StringInput("", "%.3f".format(Locale.ENGLISH, value), 24f, 0f, 72f, 24f).apply {
            horizontalAlign = HorizontalAlign.CENTER
            onScroll {
                text.toFloatValue()?.let { txt ->
                    value = it.yoffset.toFloat() + txt
                } ?: rerender()
            }

            onEnterPress = {
                text.toFloatValue()?.let { txt ->
                    value = txt
                } ?: rerender()
            }

            onLoseFocus = onEnterPress
        }

        +IconButton("", "button_right", 96f, 0f, 24f, 24f).apply {
            borderless()
            onClick {
                value += 0.1f
                rerender()
            }
        }
    }

    var value: Float
        get() = props.getter()
        set(value) {
            props.setter(value)
            rerender()
        }

    fun String.toFloatValue(): Float? {
        return (scriptEngine.eval(this) as? Number)?.toFloat()
    }
}