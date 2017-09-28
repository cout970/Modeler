package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui

/**
 * Created by cout970 on 2017/08/31.
 */

class ShowLeftPanel : IUseCase {
    override val key: String = "show.left.panel"

    override fun createTask(): ITask {
        return ModifyGui { gui ->
            gui.state.showLeftPanel = !gui.state.showLeftPanel
            gui.editorPanel.reRender()
        }
    }
}

class ShowRightPanel : IUseCase {
    override val key: String = "show.right.panel"

    override fun createTask(): ITask {
        return ModifyGui { gui ->
            gui.state.showRightPanel = !gui.state.showRightPanel
            gui.editorPanel.reRender()
        }
    }
}

class ShowBottomPanel : IUseCase {
    override val key: String = "show.bottom.panel"

    override fun createTask(): ITask {
        return ModifyGui { gui ->
            //            gui.editorPanel.bottomPanelModule.panel.let {
//                if (it.isEnabled) it.hide() else it.show()
//            }
        }
    }
}