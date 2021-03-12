package com.cout970.reactive.internal.todoapp

import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.*
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign


fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(TodoApp::class)
        }
    }
}

data class TodoAppState(val items: List<String>, val text: String) : RState

class TodoApp : RComponent<EmptyProps, TodoAppState>() {
    override fun getInitialState() = TodoAppState(emptyList(), "")

    override fun RBuilder.render() = div("TodoApp") {

        style {
            sizeX = 180f
            sizeY = 300f
        }

        postMount {
            center()
        }

        label("TODO") {
            style {
                sizeX = 180f
                fontSize = 24f
                horizontalAlign = HorizontalAlign.CENTER
            }
        }

        comp(TextInput()) {

            style {
                posX = 5f
                posY = 30f
                sizeX = 170f
                sizeY = 24f
            }

            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(text = it.newValue) }
            }
        }

        button("Add #${state.items.size + 1}") {

            style {
                posX = 50f
                posY = 60f
                sizeX = 80f
                sizeY = 24f
            }

            onClick {

                if (!state.text.isBlank()) {
                    setState { TodoAppState(items + text, "") }
                }
            }
        }

        child(TodoList::class, TodoListProps(state.items))
    }

    override fun shouldComponentUpdate(nextProps: EmptyProps, nextState: TodoAppState): Boolean {
        return nextState.items != state.items
    }
}

data class TodoListProps(val items: List<String>) : RProps

class TodoList : RStatelessComponent<TodoListProps>() {

    override fun RBuilder.render() = div("TodoList") {

        style {
            if (props.items.isEmpty())
                displayNone()

            posX = 10f
            posY = 90f
            sizeX = 160f
            sizeY = 20f * props.items.size
        }

        props.items.forEachIndexed { index, item ->
            label(text = "- $item") {
                style {
                    posX = 5f
                    posY = index * 20f
                    sizeY = 20f
                }
            }
        }
    }
}