package com.cout970.modeler.to_redo.newView

import com.cout970.modeler.to_redo.newView.gui.Scene
import com.cout970.modeler.to_redo.newView.viewtarget.ModelViewTarget
import com.cout970.modeler.to_redo.newView.viewtarget.TextureViewTarget
import com.cout970.modeler.to_redo.newView.viewtarget.ViewTarget
import org.joml.Vector2f
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/05/02.
 */
class SceneHandler(val container: Panel) {

    val scenes = mutableListOf<Scene>()
    var selectedScene: Scene? = null

    private val sceneBuffer = mutableListOf<Scene>()

    fun addScene(viewTarget: ViewTarget) {
        if (sceneBuffer.isEmpty()) {
            scenes.add(Scene(viewTarget))
        } else {
            val last = sceneBuffer.removeAt(sceneBuffer.size - 1)
            last.viewTarget = viewTarget
            scenes.add(last)
        }
        refreshScenes()
    }

    fun removeScene(index: Int) {
        val scene = scenes.removeAt(index)
        sceneBuffer.add(scene)
        refreshScenes()
    }

    fun refreshScenes() {
        container.clearComponents()
        for (scene in scenes) {
            container.addComponent(scene)
        }
        selectedScene = scenes.firstOrNull()
    }

    fun scaleScenes() {
        when (scenes.size) {
            1 -> scenes[0].apply {
                size = container.size
                position = Vector2f(0f, 0f)
            }
            2 -> {
                scenes[0].apply {
                    size = container.size.run { Vector2f(x, y / 2) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = container.size.run { Vector2f(x, y / 2) }
                    position = Vector2f(0f, container.size.y / 2f)
                }
            }
            3 -> {
                scenes[0].apply {
                    size = container.size.run { Vector2f(x / 2, y) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = container.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(container.size.x / 2f, 0f)
                }
                scenes[2].apply {
                    size = container.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(container.size.x / 2f, container.size.y / 2f)
                }
            }
            4 -> {
                scenes[0].apply {
                    size = container.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = container.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(container.size.x / 2f, 0f)
                }
                scenes[2].apply {
                    size = container.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(0f, container.size.y / 2f)
                }
                scenes[3].apply {
                    size = container.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(container.size.x / 2f, container.size.y / 2f)
                }
            }
        }
    }

    fun setSceneLayout(layout: Int, model: ModelViewTarget, tex: TextureViewTarget) {
        repeat(scenes.size) {
            removeScene(scenes.size - 1)
        }
        when (layout) {
            1 -> {
                addScene(model)
                addScene(tex)
            }
            2 -> {
                addScene(model)
                addScene(model)
                addScene(model)
                addScene(model)
            }
            3 -> {
                addScene(model)
                addScene(model)
            }
            4 -> {
                addScene(model)
                addScene(model)
                addScene(model)
                addScene(tex)
            }
            else -> {
                addScene(model)
            }
        }
    }
}