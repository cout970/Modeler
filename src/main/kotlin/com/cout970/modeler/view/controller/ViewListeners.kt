package com.cout970.modeler.view.controller

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.IInput
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.scene.Scene
import com.cout970.modeler.view.scene.Scene2d
import com.cout970.modeler.view.scene.Scene3d
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.unaryMinus

/**
 * Created by cout970 on 2017/04/02.
 */
class ViewListeners(val modelProvider: IModelProvider, val input: IInput, val sceneController: SceneController) {

    fun registerListeners(eventHandler: IEventController) {
        eventHandler.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean = onScroll(e)
        })
        eventHandler.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean = onKey(e)
        })
        eventHandler.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean = onClick(e)
        })
    }

    fun onScroll(e: EventMouseScroll): Boolean {
        val mousePos = input.mouse.getMousePos()
        sceneController.scenes.forEach { scene ->
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                scene.run {
                    val scroll = -e.offsetY * Config.cameraScrollSpeed
                    if (camera.zoom > 0.5 || scroll > 0) {
                        cameraHandler.makeZoom(camera.zoom + scroll * (camera.zoom / 60f))
                    }
                }
            }
        }
        return true
    }

    private var lastOption = 0

    fun onKey(e: EventKeyUpdate): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false
        if (Config.keyBindings.switchCameraAxis.check(e) && sceneController.selectedScene is Scene3d) {
            val handler = sceneController.selectedScene.cameraHandler
            when (lastOption) {
                0 -> handler.setRotation(angleX = 0.0, angleY = 0.0)
                1 -> handler.setRotation(angleX = 0.0, angleY = -90.toRads())
                2 -> handler.setRotation(angleX = 90.toRads(), angleY = 0.0)
                3 -> handler.setRotation(angleX = 45.toRads(), angleY = -45.toRads())
            }
            lastOption++
            if (lastOption > 3) {
                lastOption = 0
            }
        } else if (Config.keyBindings.switchOrthoProjection.check(e)) {
            (sceneController.selectedScene as? Scene3d)?.apply {
                perspective = !perspective
            }
        } else if (Config.keyBindings.moveCameraToCursor.check(e)) {
            sceneController.selectedScene.apply {
                cameraHandler.moveTo(-sceneController.cursorCenter)
            }
        }
        return false
    }

    private var lastJumpClickTime = 0L

    fun onClick(e: EventMouseClick): Boolean {
        val mousePos = input.mouse.getMousePos()
        sceneController.scenes.forEach {
            if (mousePos.isInside(it.absolutePosition, it.size.toIVector())) {
                sceneController.selectedScene = it
            }
        }
        sceneController.scenes.any { scene -> onSceneClick(e, scene, mousePos) }
        return false
    }

    fun onSceneClick(e: EventMouseClick, scene: Scene, mousePos: IVector2): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false


        if (Config.keyBindings.selectModel.check(e)) {
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                if (scene.selectorCache.hoveredObject == null &&
                    scene.selectorCache.selectedObject == null) {
                    if (scene is Scene3d) {
                        modelProvider.selectionManager.selectPos(
                                scene.selectorCache.currentContext!!.mouseRay,
                                scene.camera.zoom.toFloat(),
                                Config.keyBindings.multipleSelection.check(input)
                        )
                    }
                    if (scene is Scene2d) {
                        modelProvider.selectionManager.selectTex(
                                scene.selectorCache.currentContext!!.mouseRay,
                                scene.camera.zoom.toFloat(),
                                Config.keyBindings.multipleSelection.check(input),
                                { vec -> scene.fromTextureToWorld(vec) }
                        )
                    }
                    return true
                }
            }
        }

        if (Config.keyBindings.jumpCameraToCursor.check(e)) {
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                if (System.currentTimeMillis() - lastJumpClickTime < 500) {
                    val ray = scene.selectorCache.currentContext!!.mouseRay
                    val hit = modelProvider.selectionManager.getMouseHit(ray)

                    if (hit != null) {
                        scene.cameraHandler.moveTo(-hit.hit)
                        return true
                    }
                }
                lastJumpClickTime = System.currentTimeMillis()
            }
        }
        return false
    }

}