package com.cout970.modeler.controller.injection

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.controller.FutureExecutor
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.controller.TaskHistory
import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.controller.binders.KeyboardBinder
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.model.selection.IClipboard
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.*
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.editor.EditorPanel
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.input.window.WindowHandler
import com.cout970.modeler.render.RenderManager
import org.liquidengine.legui.component.Component
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl
import java.lang.reflect.ParameterizedType

/**
 * Created by cout970 on 2017/07/19.
 */

class DependencyInjector {

    fun injectDependencies(state: Program, comp: Component?, obj: Any) {
        val clazz = obj::class.java
        val properties = clazz.declaredFields
                .filter { it.isAnnotationPresent(Inject::class.java) }
                .map { it.genericType to it }

        val valueAndProperty = properties.map { (type, property) ->
            val value: Any? = state.run {

                if (type is ParameterizedType) {
                    val genericType = (type.actualTypeArguments[0] as WildcardTypeImpl).upperBounds[0]
                    when (genericType) {
                        ISelection::class.java -> gui.selectionHandler.getModelSelection()
                        else -> {
                            println(genericType)
                            println(genericType.javaClass)
                            null
                        }

                    }
                } else when (type) {
                    IModel::class.java -> projectManager.model
                    ProjectProperties::class.java -> projectManager.projectProperties
                    IClipboard::class.java -> projectManager.clipboard
                    Component::class.java -> comp

                    ResourceLoader::class.java -> resourceLoader
                    EventController::class.java -> eventController
                    WindowHandler::class.java -> windowHandler
                    RenderManager::class.java -> renderManager
                    Gui::class.java -> gui
                    ProjectManager::class.java -> projectManager
                    Loop::class.java -> mainLoop
                    ExportManager::class.java -> exportManager
                    FutureExecutor::class.java -> futureExecutor
                    TaskHistory::class.java -> taskHistory
                //gui
                    Root::class.java -> gui.root
                    GuiUpdater::class.java -> gui.guiUpdater
                    CanvasContainer::class.java -> gui.canvasContainer
                    Listeners::class.java -> gui.listeners
                    WindowHandler::class.java -> gui.windowHandler
                    Timer::class.java -> gui.timer
                    IInput::class.java -> gui.input
                    EditorPanel::class.java -> gui.editorPanel
                    ProjectManager::class.java -> gui.projectManager
                    CanvasManager::class.java -> gui.canvasManager
                    GuiResources::class.java -> gui.resources
                    GuiState::class.java -> gui.state
                    SelectionHandler::class.java -> gui.selectionHandler
                    Dispatcher::class.java -> gui.dispatcher
                    ButtonBinder::class.java -> gui.buttonBinder
                    KeyboardBinder::class.java -> gui.keyboardBinder

                    else -> null
                }
            }
            value to property
        }

        valueAndProperty.forEach { (value, property) ->
            if (value == null) {
                throw IllegalStateException(
                        "Error finding a value for property: ${property.name}: ${property.type.simpleName} in ${clazz.simpleName}")
            } else {
                property.isAccessible = true
                property.set(obj, value)
            }
        }
    }
}