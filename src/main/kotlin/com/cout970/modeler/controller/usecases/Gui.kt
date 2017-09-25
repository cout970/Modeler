package com.cout970.modeler.controller.usecases

import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.ModifyGui
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show

/**
 * Created by cout970 on 2017/08/31.
 */

class ShowLeftPanel : IUseCase {
    override val key: String = "show.left.panel"

    override fun createTask(): ITask {
        return ModifyGui { gui ->
            gui.editorPanel.leftPanelModule.panel.let {
                if (it.isEnabled) it.hide() else it.show()
            }
        }
    }
}

class ShowRightPanel : IUseCase {
    override val key: String = "show.right.panel"

    override fun createTask(): ITask {
        return ModifyGui { gui ->
            //            gui.editorPanel.rightPanelModule.panel.let {
//                if (it.isEnabled) it.hide() else it.show()
//            }
        }
    }
}

class ShowBottomPanel : IUseCase {
    override val key: String = "show.bottom.panel"

    override fun createTask(): ITask {
        return ModifyGui { gui ->
            gui.editorPanel.bottomPanelModule.panel.let {
                if (it.isEnabled) it.hide() else it.show()
            }
        }
    }
}