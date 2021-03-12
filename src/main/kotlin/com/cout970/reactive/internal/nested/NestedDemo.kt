package com.cout970.reactive.internal.nested

import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
import org.liquidengine.legui.style.color.ColorConstants

fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(NestedComponent::class)
        }
    }
}

data class PositionProps(val pos: Vector2f) : RProps

class NestedComponent : RStatelessComponent<EmptyProps>() {

    override fun componentWillUnmount() {
        println("componentWillUnmount: NestedComponent")
    }

    override fun componentDidMount() {
        println("componentDidMount: NestedComponent")
    }

    override fun RBuilder.render() = div("NestedComponent") {
        style {
            sizeX = 256f
            sizeY = 256f
        }
        child(ForceUpdateButton::class, ForceUpdateButton.Props(Vector2f(0f, 0f), { rerender() }))

        child(StateTesterComponent::class, PositionProps(Vector2f(128f, 0f)))
        label("Level 1") {
            style {
                posY = 128f
                sizeX = 32f
                sizeY = 32f
            }
        }

        child(MoreNestedComponent::class, PositionProps(Vector2f(128f, 128f)))
    }
}

class MoreNestedComponent : RStatelessComponent<PositionProps>() {

    override fun componentWillUnmount() {
        println("componentWillUnmount: MoreNestedComponent")
    }

    override fun componentDidMount() {
        println("componentDidMount: MoreNestedComponent")
    }

    override fun RBuilder.render() = div("MoreNestedComponent") {
        style {
            position.set(props.pos)
            sizeX = 128f
            sizeY = 128f
        }
        child(ForceUpdateButton::class, ForceUpdateButton.Props(Vector2f(0f, 0f), { rerender() }))
        child(StateTesterComponent::class, PositionProps(Vector2f(64f, 0f)))
        label("Level 2") {
            style {
                posY = 64f
                sizeX = 32f
                sizeY = 32f
            }
        }

        child(EvenMoreNestedComponent::class, PositionProps(Vector2f(64f, 64f)))

    }
}

class EvenMoreNestedComponent : RStatelessComponent<PositionProps>() {

    override fun componentWillUnmount() {
        println("componentWillUnmount: EvenMoreNestedComponent")
    }

    override fun componentDidMount() {
        println("componentDidMount: EvenMoreNestedComponent")
    }

    override fun RBuilder.render() = div("EvenMoreNestedComponent") {
        style {
            position.set(props.pos)
            sizeX = 64f
            sizeY = 64f
        }
        child(ForceUpdateButton::class, ForceUpdateButton.Props(Vector2f(0f, 0f), { rerender() }))
        child(StateTesterComponent::class, PositionProps(Vector2f(32f, 0f)))
        label("Level 3") {
            style {
                posY = 32f
                sizeX = 32f
                sizeY = 32f
            }
        }
    }
}

class StateTesterComponent : RStatelessComponent<PositionProps>() {

    override fun componentWillUnmount() {
        println("componentWillUnmount: StateTesterComponent")
    }

    override fun componentDidMount() {
        println("componentDidMount: StateTesterComponent")
    }

    override fun RBuilder.render() = div("Tester") {
        style {
            position.set(props.pos)
            sizeX = 32f
            sizeY = 64f
        }

        child(ExampleToggleButton::class, PositionProps(Vector2f(0f, 0f)))
        child(ForceUpdateButton::class, ForceUpdateButton.Props(Vector2f(0f, 32f), { rerender() }))
    }
}

class ExampleToggleButton : RComponent<PositionProps, ExampleToggleButton.State>() {

    override fun getInitialState() = State(false)

    override fun componentWillUnmount() {
        println("componentWillUnmount: ExampleToggleButton")
    }

    override fun componentDidMount() {
        println("componentDidMount: ExampleToggleButton")
    }

    override fun RBuilder.render() = div("ToggleButton") {

        style {
            backgroundColor { if (state.on) ColorConstants.green() else ColorConstants.red() }
            borderRadius(0f)

            position.set(props.pos)
            sizeX = 32f
            sizeY = 32f
        }

        if (state.on) {
            div("OnlyOn") {
                style {
                    sizeX = 16f
                    sizeY = 32f
                }
                child(CounterButton::class)
            }
        }

        onClick { setState { State(!on) } }
    }

    data class State(val on: Boolean) : RState
}

class CounterButton : RComponent<EmptyProps, CounterButton.State>() {

    override fun getInitialState() = State(0)

    override fun componentWillUnmount() {
        println("componentWillUnmount: CounterButton")
    }

    override fun componentDidMount() {
        println("componentDidMount: CounterButton")
    }

    override fun RBuilder.render() = label(state.count.toString(), "CounterButton") {

        style {
            backgroundColor { ColorConstants.lightBlack() }
            borderRadius(0f)
            style.textColor = ColorConstants.white()
            sizeX = 16f
            sizeY = 32f
        }
        onClick { setState { State(count + 1) } }
    }

    data class State(val count: Int) : RState
}


class ForceUpdateButton : RStatelessComponent<ForceUpdateButton.Props>() {

    override fun componentWillUnmount() {
        println("componentWillUnmount: ForceUpdateButton")
    }

    override fun componentDidMount() {
        println("componentDidMount: ForceUpdateButton")
    }

    override fun RBuilder.render() = div("ForceUpdateButton") {

        style {
            backgroundColor { ColorConstants.blue() }
            borderRadius(0f)

            position.set(props.pos)
            sizeX = 32f
            sizeY = 32f
        }

        onClick { props.callback() }
    }

    data class Props(val pos: Vector2f, val callback: () -> Unit) : RProps
}
