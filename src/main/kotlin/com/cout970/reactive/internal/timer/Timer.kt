package com.cout970.reactive.internal.timer

import com.cout970.reactive.core.*
import com.cout970.reactive.core.CoroutineAsyncManager.Interval
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.label


fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(Timer::class)
        }
    }
}

data class TimerState(val seconds: Int) : RState

class Timer : RComponent<EmptyProps, TimerState>() {

    private lateinit var interval: Interval

    override fun getInitialState() = TimerState(0)

    private fun tick() {
        setState { TimerState(seconds + 1) }
    }

    override fun componentDidMount() {
        interval = CoroutineAsyncManager.setInterval(1000) {
            tick()
        }
    }

    override fun componentWillUnmount() {
        CoroutineAsyncManager.clearInterval(interval)
    }

    override fun RBuilder.render() {
        div {
            label("Seconds: ${state.seconds}")
        }
    }
}