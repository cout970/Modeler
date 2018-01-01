package com.cout970.modeler.controller.injection

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.Program
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.controller.FutureExecutor
import com.cout970.modeler.controller.TaskHistory
import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.controller.binders.KeyboardBinder
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.model.selection.IClipboard
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.*
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.canvas.cursor.CursorManager
import com.cout970.modeler.gui.views.EditorView
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.input.window.WindowHandler
import com.cout970.modeler.render.RenderManager
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import java.lang.reflect.Type
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaType

/**
 * Created by cout970 on 2017/07/19.
 */

class DependencyInjector {

    private fun Program.getInstance(type: Type, comp: Component?): Any? = when (type) {
        IModel::class.java -> projectManager.model
        ProjectProperties::class.java -> projectManager.projectProperties
        IClipboard::class.java -> projectManager.clipboard
        Component::class.java -> comp ?: Panel()

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

        Root::class.java -> gui.root
        CanvasContainer::class.java -> gui.canvasContainer
        Listeners::class.java -> gui.listeners
        WindowHandler::class.java -> gui.windowHandler
        Timer::class.java -> gui.timer
        IInput::class.java -> gui.input
        EditorView::class.java -> gui.editorView
        ProjectManager::class.java -> projectManager
        CanvasManager::class.java -> gui.canvasManager
        CursorManager::class.java -> gui.cursorManager
        GuiResources::class.java -> gui.resources
        GuiState::class.java -> gui.state
        Dispatcher::class.java -> gui.dispatcher
        ButtonBinder::class.java -> gui.buttonBinder
        KeyboardBinder::class.java -> gui.keyboardBinder
        IModelAccessor::class.java -> gui.modelAccessor
        IProjectPropertiesHolder::class.java -> gui.propertyHolder

        else -> null
    }

    fun callUseCase(state: Program, comp: Component?, useCase: KFunction<*>): ITask {

        val args: Map<KParameter, Any> = useCase.valueParameters.associate { param ->
            Pair(param,
                    state.getInstance(param.type.javaType, comp)
                    ?: throw IllegalStateException("Unable to inject ${param.type.javaType}")
            )
        }

        val task = useCase.callBy(args) as? ITask

        if (task == null) {
            log(Level.ERROR) { "[Dispatcher] Error task was null: $useCase" }
            return TaskNone
        }

        return task
    }

    fun checkUseCaseArguments(state: Program, func: KFunction<*>) {
        func.valueParameters.forEach { param ->
            val value = state.getInstance(param.type.javaType, null)
            if (value == null) {
                log(Level.CRITICAL) { "[Dispatcher] Error UseCase argument invalid: $func" }
            }
        }
    }
}