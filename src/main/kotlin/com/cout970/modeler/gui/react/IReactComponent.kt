package com.cout970.modeler.gui.react

import com.cout970.modeler.gui.react.leguicomp.LeguiComponentBridge
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/09/07.
 */
interface IReactComponent<P, S> {

    var parent: Container<Component>

    val state: S

    fun setState(state: S)

    fun render(): Component
}

abstract class ReactComponent<P, S: Any> : IReactComponent<P, S> {

    override lateinit var parent: Container<Component>

    private var _state: S? = null

    override val state: S get() = _state!!

    override fun setState(state: S){
        if (_state != null){
            this._state = state
            ReactRenderer.render(parent){ this.render() }
            return
        }
        this._state = state
    }
}

interface IComponentFactory<P, S, out C : IReactComponent<P, S>> {

    fun createDefaultProps(): P

    fun build(props: P): C

    operator fun invoke(props: P = createDefaultProps(), func: P.() -> Unit = {}): LeguiComponentBridge<P, S, C> {
        return LeguiComponentBridge(this, props)
    }
}

interface IScalable {

    fun updateScale(comp: Component, parent: Container<*>, windowSize: IVector2)
}