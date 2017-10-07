package com.cout970.modeler.core.log

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.util.ITickeable
import java.text.NumberFormat
import java.util.*

/**
 * Created by cout970 on 2017/10/02.
 */

object Profiler : ITickeable {

    private val sectionStack = ArrayDeque<SectionStarted>()
    private val profiledSections = mutableListOf<SectionFinished>()
    private lateinit var lastLog: ProfilingLog

    fun startSection(section: String) {
        sectionStack.push(SectionStarted(section, Timer.secTime))
    }

    fun endSection() {
        val name = getCurrentSectionString()
        val section = sectionStack.pop()
        profiledSections.add(SectionFinished(name, section.start, Timer.secTime))
    }

    fun getCurrentSectionString() = sectionStack.reversed().joinToString(".") { it.name }

    fun nextSection(section: String) {
        endSection()
        startSection(section)
    }

    fun endAll() {
        while (sectionStack.isNotEmpty()) {
            endSection()
        }
    }

    override fun tick() {
        endAll()
        if (profiledSections.isNotEmpty()) {
            lastLog = ProfilingLog(profiledSections.toList())
            profiledSections.clear()
//            lastLog.print()
        }
        startSection("root")
    }

    data class SectionStarted(val name: String, val start: Double)
    data class SectionFinished(val name: String, val start: Double, val end: Double)
    data class ProfilingLog(val data: List<SectionFinished>) {

        fun print() {
            val str = data
                    .sortedBy { it.name }
                    .map { it.name to (it.end - it.start) }
                    .joinToString("\n") { "${NumberFormat.getInstance().format(it.second * 1000)} -> ${it.first}" }

            println(str)
        }
    }
}