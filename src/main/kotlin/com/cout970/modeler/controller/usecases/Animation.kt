package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.animation.*
import com.cout970.modeler.api.model.`object`.RootGroupRef
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.animation.*
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.model.toTRTS
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.leguicomp.StringInput
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.util.*
import com.cout970.reactive.dsl.width
import org.liquidengine.legui.component.Component
import kotlin.math.roundToInt


private var lastAnimation = 0
private var lastChannel = 0

private fun selectAnimation(projectManager: ProjectManager, it: Gui, ref: IAnimationRef) {
    projectManager.selectedAnimation = ref
    val length = projectManager.animation.timeLength
    it.animator.animationState = AnimationState.STOP
    it.animator.animationTime = 0f
    it.animator.offset = 5.fromFrame()
    it.animator.zoom = (length.toFrame() + 10).fromFrame()
    it.animator.sendUpdate()
}

@UseCase("animation.add")
private fun addAnimation(programState: ProjectManager): ITask {
    val model = programState.model
    val animation = Animation.of("Animation_${lastAnimation++}")

    return TaskChain(listOf(
        TaskUpdateModel(model, model.addAnimation(animation)),
        ModifyGui {
            selectAnimation(programState, it, animation.ref)
        }
    ))
}

@UseCase("animation.rename")
private fun renameAnimation(programState: IProgramState, component: Component): ITask {
    val model = programState.model
    val text = (component as StringInput).text
    if (text.isEmpty()) return TaskNone

    val animation = programState.animation.withName(text)

    return TaskChain(listOf(
        TaskUpdateModel(model, model.modifyAnimation(animation))
    ))
}

@UseCase("animation.dup")
private fun duplicateAnimation(programState: ProjectManager): ITask {
    val model = programState.model
    val selected = programState.animation

    if (selected == AnimationNone) return TaskNone

    val animation = Animation.of(
        selected.name + "_copy",
        selected.timeLength,
        selected.channels,
        selected.channelMapping
    )

    return TaskChain(listOf(
        TaskUpdateModel(model, model.addAnimation(animation)),
        ModifyGui { programState.selectedAnimation = animation.ref; it.animator.sendUpdate() }
    ))
}

@UseCase("animation.remove")
private fun removeAnimation(programState: ProjectManager): ITask {
    val model = programState.model
    val animation = programState.selectedAnimation

    return TaskChain(listOf(
        ModifyGui { programState.selectedAnimation = AnimationRefNone; it.animator.sendUpdate() },
        TaskUpdateModel(model, model.removeAnimation(animation))
    ))
}

@UseCase("animation.channel.add")
private fun addAnimationChannel(programState: IProgramState): ITask {
    val group = programState.selectedGroup
    val selection = programState.modelSelection
    val anim = programState.animation
    val model = programState.model

    if (anim.ref !in programState.model.animationMap) {
        return TaskNone
    }

    val target = if (group == RootGroupRef) {
        val sel = selection.getOrNull() ?: return TaskNone
        AnimationTargetObject(sel.objects)
    } else {
        AnimationTargetGroup(group)
    }

    val defaultTRS = target.getTransformation(model).toTRTS()

    val channel = Channel(
        name = "Channel ${lastChannel++}",
        interpolation = InterpolationMethod.LINEAR,
        keyframes = listOf(
            Keyframe(0f, defaultTRS),
            Keyframe(anim.timeLength, defaultTRS)
        )
    )
    val newAnimation = anim
        .withChannel(channel)
        .withMapping(channel.ref, target)

    return TaskChain(listOf(
        TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation)),
        ModifyGui { it.animator.selectedChannel = channel.ref }
    ))
}


@UseCase("animation.channel.select")
private fun selectAnimationChannel(comp: Component, projectManager: ProjectManager): ITask {
    val animation = projectManager.animation
    val channel = comp.metadata["ref"] as IChannelRef

    val task1 = ModifyGui { it.animator.selectedChannel = channel }
    val target = animation.channelMapping[channel] ?: return task1

    return when (target) {
        is AnimationTargetGroup -> {
            TaskChain(listOf(task1, ModifyGui { projectManager.selectedGroup = target.ref }))
        }
        is AnimationTargetObject -> {
            val sel = projectManager.modelSelection
            val task2 = TaskUpdateModelSelection(sel, Selection.of(target.refs).asNullable())
            TaskChain(listOf(task1, task2))
        }
    }
}

@UseCase("animation.channel.rename")
private fun renameAnimationChannel(component: Component, programState: IProgramState, animator: Animator): ITask {
    val ref = component.metadata["ref"] as IChannelRef
    val text = (component as StringInput).text
    if (text.isEmpty()) return TaskNone

    val model = programState.model
    val animation = animator.animation
    val channel = animation.channels[ref] ?: error("Missing Channel $ref")

    val newChannel = channel.withName(text)
    val newAnimation = animator.animation.withChannel(newChannel)

    return TaskChain(listOf(
        TaskUpdateModel(model, model.modifyAnimation(newAnimation))
    ))
}

@UseCase("animation.channel.disable")
private fun disableAnimationChannel(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val ref = comp.metadata["ref"] as IChannelRef
    val channel = animation.channels[ref]!!

    val newAnimation = animation.withChannel(channel.withEnable(false))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.channel.enable")
private fun enableAnimationChannel(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val ref = comp.metadata["ref"] as IChannelRef
    val channel = animation.channels[ref] ?: error("Missing channel $ref")

    val newAnimation = animation.withChannel(channel.withEnable(true))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.channel.interpolation")
private fun setAnimationChannelInterpolation(comp: Component, programState: IProgramState): ITask {
    val ref = comp.metadata["ref"] as IChannelRef
    val method = comp.metadata["type"] as InterpolationMethod
    val animation = programState.animation
    val channel = animation.channels[ref] ?: error("Missing channel $ref")

    val newAnimation = animation.withChannel(channel.withInterpolation(method))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.channel.type")
private fun setAnimationChannelType(comp: Component, programState: IProgramState): ITask {
    val ref = comp.metadata["ref"] as IChannelRef
    val type = comp.metadata["type"] as ChannelType
    val animation = programState.animation
    val channel = animation.channels[ref] ?: error("Missing channel $ref")

    val newAnimation = animation.withChannel(channel.withType(type))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.channel.update")
private fun setAnimationChannelObjects(comp: Component, programState: IProgramState): ITask {
    val ref = comp.metadata["ref"] as IChannelRef
    val animation = programState.animation
    val group = programState.selectedGroup

    val target = if (group != RootGroupRef) {
        AnimationTargetGroup(group)
    } else {
        val selection = programState.modelSelection.getOrNull() ?: return TaskNone
        if (selection.selectionType != SelectionType.OBJECT) return TaskNone
        if (selection.selectionTarget != SelectionTarget.MODEL) return TaskNone

        AnimationTargetObject(selection.objects)
    }

    val newAnimation = animation.withMapping(ref, target)

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.select")
private fun selectAnimation(comp: Component, projectManager: ProjectManager): ITask = ModifyGui {
    val ref = comp.metadata["animation"] as IAnimationRef
    selectAnimation(projectManager, it, ref)
}

@UseCase("animation.channel.delete")
private fun removeAnimationChannel(comp: Component, programState: IProgramState): ITask {
    val animation = programState.animation
    val channel = comp.metadata["ref"] as IChannelRef
    val newAnimation = animation.removeChannels(listOf(channel))

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(newAnimation))
}

@UseCase("animation.set.length")
private fun setAnimationLength(comp: Component, programState: IProgramState): ITask {
    var animation = programState.animation
    val newLength = comp.metadata["time"] as Float

    if (newLength <= 0) return TaskNone
    val diff = newLength / animation.timeLength

    val newChannels = animation.channels.values.map { channel ->
        channel.withKeyframes(channel.keyframes.map { keyframe ->
            keyframe.withTime((keyframe.time * diff * 60f).roundToInt() / 60f)
        })
    }

    newChannels.forEach {
        animation = animation.withChannel(it)
    }
    animation = animation.withTimeLength(newLength)

    return TaskUpdateModel(programState.model, programState.model.modifyAnimation(animation))
}

@UseCase("animation.panel.key")
private fun onAnimationPanelKey(comp: Component): ITask {
    val offset = 5.fromFrame() * (if (comp.metadata["key"] == "left") -1 else 1)

    return ModifyGui {
        it.animator.animationTime += offset
    }
}

@UseCase("animation.panel.click")
private fun onAnimationPanelClick(comp: Component, animator: Animator, input: IInput): ITask {
    val mousePos = input.mouse.getMousePos()
    val compPos = comp.absolutePositionV
    val diffX = mousePos.xf - compPos.xf
    val diffY = mousePos.yf - compPos.yf

    val zoom = animator.zoom
    val timeToPixel = comp.width / zoom
    val pixelOffset = animator.offset * timeToPixel

    val channels = animator.animation.channels.values
    val time = (diffX - pixelOffset) / timeToPixel

    // TODO fix incorrect bounding box
    channels.forEachIndexed { i, channel ->
        if (diffY > i * 24f && diffY <= (i + 1) * 24f) {

            channel.keyframes.forEachIndexed { index, keyframe ->
                val pos = keyframe.time * timeToPixel + pixelOffset

                if (diffX > pos - 12f && diffX <= pos + 12f) {
                    return ModifyGui {
                        animator.selectedChannel = channel.ref
                        animator.selectedKeyframe = index
                        animator.animationTime = keyframe.time
                        animator.animationState = AnimationState.STOP
                        it.state.cursor.update(it)
                    }
                }
            }
        }
    }

//    if (animator.animationState != AnimationState.STOP) return TaskNone

    return ModifyGui {
        animator.selectedKeyframe = null
        animator.animationTime = time.toFrame().fromFrame()
        it.state.cursor.update(it)
    }
}


@UseCase("animation.state.toggle")
private fun animationTogglePlay(): ITask = ModifyGui {
    if (it.animator.animationState == AnimationState.STOP) {
        it.animator.animationState = AnimationState.FORWARD
    } else {
        it.animator.animationState = AnimationState.STOP
    }
}

@UseCase("animation.state.backward")
private fun animationPlayBackwards(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.BACKWARD
}

@UseCase("animation.state.forward")
private fun animationPlayForward(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.FORWARD
}

@UseCase("animation.state.stop")
private fun animationStop(): ITask = ModifyGui {
    it.animator.animationState = AnimationState.STOP
}

@UseCase("animation.seek.start")
private fun animationSeekStart(): ITask = ModifyGui {
    it.animator.animationTime = 0f
}

@UseCase("animation.seek.end")
private fun animationSeekEnd(): ITask = ModifyGui {
    it.animator.animationTime = it.programState.animation.timeLength
}

@UseCase("animation.prev.keyframe")
private fun prevKeyframe(animator: Animator): ITask {
    val selected = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[selected]!!

    val prev = channel.keyframes.findLast { it.time < animator.animationTime } ?: return TaskNone

    return ModifyGui {
        it.animator.animationTime = prev.time
        it.state.cursor.update(it)
    }
}

@UseCase("animation.next.keyframe")
private fun nextKeyframe(animator: Animator): ITask {
    val selected = animator.selectedChannel ?: return TaskNone
    val channel = animator.animation.channels[selected]!!

    val next = channel.keyframes.find { it.time > animator.animationTime } ?: return TaskNone

    return ModifyGui {
        it.animator.animationTime = next.time
        it.state.cursor.update(it)
    }
}