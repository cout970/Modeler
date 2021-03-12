package com.cout970.reactive.internal

import com.cout970.reactive.core.AsyncManager
import com.cout970.reactive.core.RContext
import com.cout970.reactive.core.Renderer
import com.cout970.reactive.core.SyncManager
import org.joml.Vector2f
import org.liquidengine.legui.animation.AnimatorProvider
import org.liquidengine.legui.system.layout.LayoutManager

fun demoWindow(builder: (LeguiEnvironment) -> RContext) {

    LeguiEnvironment(windowSize = Vector2f(400f, 400f)).let { env ->

        env.frame.container.size = Vector2f(400f, 400f)

        val ctx = builder(env)

        // Update all layouts when the components are mounted/unmounted
        ctx.registerUpdateListener {
            LayoutManager.getInstance().layout(env.frame)
        }
        // Update layouts for first time
        LayoutManager.getInstance().layout(env.frame)

        updateOnResize(ctx, env)

        AsyncManager.setInstance(SyncManager)
        env.update = {
            SyncManager.runSync()
            AnimatorProvider.getAnimator().runAnimations()
        }

        env.loop()
        env.finalice()
    }
}

// Updates the gui when the frame size changes, This is needed if you don't use layout
fun updateOnResize(ctx: RContext, env: LeguiEnvironment) {

    var lastTime = System.currentTimeMillis()

    // Add callback for window resize
    env.keeper.chainFramebufferSizeCallback.add { _, _, _ ->

        // limit resize count to 1 per second
        val now = System.currentTimeMillis()
        if (now - lastTime > 100) {
            lastTime = now

            // Rerender the screen
            AsyncManager.runLater {
                Renderer.rerender(ctx)
            }
        }
    }
}


