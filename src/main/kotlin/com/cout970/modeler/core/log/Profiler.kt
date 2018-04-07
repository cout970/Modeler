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
    private val acumLogs = mutableListOf<ProfilingLog>()
    lateinit var renderLog: List<Pair<String, Double>>
    var lastTime = -1L

    fun startSection(section: String) {
        sectionStack.push(SectionStarted(section.replace('.', '_'), Timer.secTime))
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
            val log = ProfilingLog(profiledSections.toList())
            acumLogs.add(log)

            if (System.currentTimeMillis() - lastTime > 500) {
                lastTime = System.currentTimeMillis()
                renderLog = acumLogs
                        .flatMap { it.data }
                        .groupBy { it.name }
                        .entries
                        .map { it.key to it.value.map { it.end - it.start }.average() }

                acumLogs.clear()
            }
            profiledSections.clear()
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