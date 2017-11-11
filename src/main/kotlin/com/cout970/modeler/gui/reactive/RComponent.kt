package com.cout970.modeler.gui.reactive

import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/23.
 */
abstract class RComponent<P : Any, S : Any>() {

    lateinit var context: RContext

    lateinit var props: P
        private set

    private lateinit var stateField: S

    private var sealed = false

    var state: S
        get() = stateField
        set(value) {
            if (sealed) {
                throw IllegalStateException(
                        "State can't be changed after the component is created, use replaceState instead")
            }
            stateField = value
        }

    abstract fun build(ctx: RBuildContext): Component

    fun rebuild(){
        replaceState(state)
    }

    fun replaceState(newState: S) {
        if (shouldComponentUpdate(props, newState)) {
            stateField = newState
            context.markDirty(this)
            return
        }
        stateField = newState
    }

    fun seal() {
        sealed = true
    }

    open fun componentWillMount() {

    }

    open fun componentDidMount() {

    }

    open fun componentWillUnmount() {

    }

    open fun shouldComponentUpdate(nextProps: P, nextState: S): Boolean {
        return true
    }

    fun transferProps(props: P) {
        this.props = props
    }

    fun transferState(state: S) {
        stateField = state
    }
}