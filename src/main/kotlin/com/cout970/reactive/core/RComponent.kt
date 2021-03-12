package com.cout970.reactive.core

import org.liquidengine.legui.component.Component

abstract class RComponent<P : RProps, S : RState> {

    internal lateinit var ctx: RContext
    internal lateinit var mountPoint: Component
    internal var mounted: Boolean = false
    internal var key: String? = null

    var state: S = getInitialState()
        private set

    lateinit var props: P
        private set

    abstract fun getInitialState(): S

    fun setState(callback: S.() -> S) {
        Renderer.scheduleUpdate(this, callback) { state = it }
    }

    fun rerender() = setState { this }

    open fun componentWillUnmount() = Unit
    open fun componentWillMount() = Unit
    open fun componentDidMount() = Unit

    open fun componentWillUpdate() = Unit

    open fun componentWillReceiveProps(nextProps: P) {
        this.props = nextProps
    }

    open fun shouldComponentUpdate(nextProps: P, nextState: S): Boolean {
        return true
    }

    abstract fun RBuilder.render()

    fun render() = buildNodeList { render() }
}

abstract class RStatelessComponent<P : RProps> : RComponent<P, EmptyState>() {
    override fun getInitialState() = EmptyState
}

object EmptyProps : RProps
object EmptyState : RState

interface RState
interface RProps