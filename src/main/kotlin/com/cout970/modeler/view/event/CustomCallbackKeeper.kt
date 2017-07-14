package com.cout970.modeler.view.event

import org.liquidengine.cbchain.*
import org.liquidengine.legui.system.context.CallbackKeeper

/**
 * Created by cout970 on 2016/12/02.
 */
class CustomCallbackKeeper : CallbackKeeper {

    override fun getChainCharModsCallback(): IChainCharModsCallback = CharModsCallback

    override fun getChainKeyCallback(): IChainKeyCallback = KeyCallback

    override fun getChainWindowRefreshCallback(): IChainWindowRefreshCallback = WindowRefreshCallback

    override fun getChainCursorPosCallback(): IChainCursorPosCallback = CursorPosCallback

    override fun getChainDropCallback(): IChainDropCallback = DropCallback

    override fun getChainWindowSizeCallback(): IChainWindowSizeCallback = WindowSizeCallback

    override fun getChainCursorEnterCallback(): IChainCursorEnterCallback = CursorEnterCallback

    override fun getChainMouseButtonCallback(): IChainMouseButtonCallback = MouseButtonCallback

    override fun getChainWindowPosCallback(): IChainWindowPosCallback = WindowPosCallback

    override fun getChainScrollCallback(): IChainScrollCallback = ScrollCallback

    override fun getChainWindowCloseCallback(): IChainWindowCloseCallback = WindowCloseCallback

    override fun getChainWindowFocusCallback(): IChainWindowFocusCallback = WindowFocusCallback

    override fun getChainCharCallback(): IChainCharCallback = CharCallback

    override fun getChainFramebufferSizeCallback(): IChainFramebufferSizeCallback = FramebufferSizeCallback

    override fun getChainWindowIconifyCallback(): IChainWindowIconifyCallback = WindowIconifyCallback
}